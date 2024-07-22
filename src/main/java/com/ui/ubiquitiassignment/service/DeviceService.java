package com.ui.ubiquitiassignment.service;

import com.ui.ubiquitiassignment.constant.DeviceType;
import com.ui.ubiquitiassignment.exception.DeviceNotFoundException;
import com.ui.ubiquitiassignment.model.Device;
import com.ui.ubiquitiassignment.model.DeviceTopology;
import com.ui.ubiquitiassignment.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DeviceService {

    private static final ToIntFunction<Device> DEVICE_TO_PRIORITY_INT =
            device -> switch (device.getDeviceType()) {
                case DeviceType.GATEWAY -> 1;
                case DeviceType.SWITCH -> 2;
                case DeviceType.ACCESS_POINT -> 3;
            };

    private final DeviceRepository deviceRepository;
    private final TopologyBuilderService topologyBuilderService;

    public void registerDevice(Device device) {
        validateDevice(device);

        if (device.getUplinkMacAddress() != null) {
            deviceRepository.findByMacAddress(device.getUplinkMacAddress())
                    .ifPresentOrElse(
                            uplinkDevice -> {
                                uplinkDevice.getDownlinkMacAddresses().add(device.getMacAddress());
                                deviceRepository.save(uplinkDevice);
                            },
                            () -> {
                                throw new DeviceNotFoundException("Uplink device not found: " + device.getUplinkMacAddress());
                            });
        }
        deviceRepository.save(device);
    }

    public List<Device> getAllDevicesSortedByDeviceTypePriority() {
        return deviceRepository.findAll().stream()
                .filter(device -> device.getDeviceType() != null)
                .sorted(Comparator.comparingInt(DEVICE_TO_PRIORITY_INT))
                .collect(Collectors.toList());
    }

    public Optional<Device> getDeviceByMacAddress(String macAddress) {
        return deviceRepository.findByMacAddress(macAddress);
    }

    public DeviceTopology getNetworkTopologyByMacAddress(String macAddress) {
        return topologyBuilderService.buildTopologyByMacAddress(macAddress);
    }

    /**
     * Retrieving all registered network device topology
     * Building Map of devices by root and downlink devices where root is device without uplink device.getUplinkMacAddress() == null
     * and downlink devices are devices with uplink device.getUplinkMacAddress() != null
     * Then building tree structure of devices by root devices
     *
     * @return list of {@link DeviceTopology} as tree structure. Node is represented as macAddress
     */
    public List<DeviceTopology> getFullNetworkTopology() {
        Map<Boolean, Map<String, Device>> devices =
                deviceRepository.findAll().stream()
                        .collect(Collectors.groupingBy(device -> device.getUplinkMacAddress() == null,
                                Collectors.toMap(Device::getMacAddress, device -> device)));

        List<Device> roots =
                devices.getOrDefault(true, Map.of()).values().stream()
                        .filter(device -> device.getUplinkMacAddress() == null)
                        .toList();

        return roots.stream()
                .map(root -> topologyBuilderService.buildTopologyFromMap(root, devices.get(false)))
                .toList();
    }

    private void validateDevice(Device device) {
        deviceRepository.findByMacAddress(device.getMacAddress())
                .ifPresent(existingDevice -> {
                    throw new IllegalArgumentException("Device already exists: " + existingDevice.getMacAddress());
                });

        Optional.ofNullable(device.getUplinkMacAddress())
                .ifPresent(uplinkMacAddress -> {
                    if (uplinkMacAddress.equals(device.getMacAddress())) {
                        throw new IllegalArgumentException("Device cannot be its own uplink");
                    }
                });
    }

}
