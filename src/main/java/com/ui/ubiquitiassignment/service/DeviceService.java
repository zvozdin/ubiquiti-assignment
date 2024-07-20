package com.ui.ubiquitiassignment.service;

import com.ui.ubiquitiassignment.constant.DeviceType;
import com.ui.ubiquitiassignment.exception.DeviceNotFoundException;
import com.ui.ubiquitiassignment.model.Device;
import com.ui.ubiquitiassignment.model.DeviceTopology;
import com.ui.ubiquitiassignment.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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

    public void registerDevice(Device device) {
        // todo
        validateDevice(device);

        if (device.getUplinkMacAddress() != null) {
            deviceRepository.findByMacAddress(device.getUplinkMacAddress())
                    .ifPresentOrElse(
                            uplinkDevice -> uplinkDevice.getDownlinkMacAddresses().add(device.getMacAddress()),
                            () -> {
                                throw new DeviceNotFoundException("Uplink device not found: " + device.getUplinkMacAddress());
                            });
        }
        deviceRepository.save(device);
    }

    public List<Device> getAllDevicesSortedByDeviceTypePriority() {
        return deviceRepository.findAll().stream()
                .filter(device -> device.getDeviceType() != null && device.getMacAddress() != null) // todo write test for Comparator
                .sorted(Comparator.comparingInt(DEVICE_TO_PRIORITY_INT))
                .collect(Collectors.toList());
    }

    public Optional<Device> getDeviceByMacAddress(String macAddress) {
        return deviceRepository.findByMacAddress(macAddress);
    }

    public DeviceTopology getNetworkTopology(String macAddress) {
        DeviceTopology deviceTopology = new DeviceTopology();
        Device root = getDeviceByMacAddress(macAddress)
                .orElseThrow(() -> new IllegalArgumentException("Device not found: " + macAddress));

        deviceTopology.setRootMacAddress(root.getMacAddress());

        // todo identify ids of downlink devices
        // todo: how to identify all we need to retrieve by single call
        buildTopologyByLookupInStorage(root, deviceTopology.getDownlinkDevices());

        return deviceTopology;
    }

    /**
     * Retrieving all registered network device topology
     *
     * @return list of {@link DeviceTopology} as tree structure. Node is represented as macAddress
     */
    public List<DeviceTopology> getFullNetworkTopology() {
        Map<Boolean, Map<String, Device>> devices =
                deviceRepository.findAll().stream()
                        .collect(Collectors.groupingBy(device -> device.getUplinkMacAddress() == null,
                                Collectors.toMap(Device::getMacAddress, device -> device)));

        List<DeviceTopology> result = new ArrayList<>(devices.get(true).size());

        List<Device> roots =
                devices.get(true).values().stream()
                        .filter(device -> device.getUplinkMacAddress() == null)
                        .toList();

        roots.forEach(root -> {
            DeviceTopology deviceTopology = new DeviceTopology();
            deviceTopology.setRootMacAddress(root.getMacAddress());
            List<DeviceTopology> rootDownlinkTopologies = deviceTopology.getDownlinkDevices();
            buildTopologyFromGivenDevices(root, rootDownlinkTopologies, devices.get(false));
            result.add(deviceTopology);
        });

        return result;

    }


    // todo think about to encapsulate those methods in a separate class
    private void buildTopologyByLookupInStorage(Device root, List<DeviceTopology> rootDownlinkTopologies) {
        if (!CollectionUtils.isEmpty(root.getDownlinkMacAddresses())) {
            for (String downlinkMacAddress : root.getDownlinkMacAddresses()) {
                Device downlinkDevice =
                        getDeviceByMacAddress(downlinkMacAddress) // todo try to fix n+1 problem or use tree structure
                                .orElseThrow(() -> new IllegalArgumentException("Device not found: " + downlinkMacAddress));

                DeviceTopology downlinkTopology = new DeviceTopology();
                downlinkTopology.setRootMacAddress(downlinkDevice.getMacAddress());
                rootDownlinkTopologies.add(downlinkTopology);
                buildTopologyByLookupInStorage(downlinkDevice, downlinkTopology.getDownlinkDevices());
            }
        }
    }

    private void buildTopologyFromGivenDevices(Device root, List<DeviceTopology> rootDownlinkTopologies, Map<String, Device> devices) {
        if (!CollectionUtils.isEmpty(root.getDownlinkMacAddresses())) {
            root.getDownlinkMacAddresses().stream()
                    .map(devices::get)
                    .forEach(downlinkDevice -> {
                        DeviceTopology downlinkTopology = new DeviceTopology();
                        downlinkTopology.setRootMacAddress(downlinkDevice.getMacAddress());
                        rootDownlinkTopologies.add(downlinkTopology);
                        buildTopologyFromGivenDevices(downlinkDevice, downlinkTopology.getDownlinkDevices(), devices);
                    });
        }
    }

    private void validateDevice(Device device) {
        // todo should go to annotation based validation
//        if (device.getMacAddress() == null || device.getMacAddress().isEmpty()) {
//            throw new IllegalArgumentException("MAC address cannot be null or empty");
//        }
//
//        if (!MAC_ADDRESS_PATTERN.matcher(device.getMacAddress()).matches()) {
//            throw new IllegalArgumentException("Invalid MAC address format");
//        }


        if (deviceRepository.findByMacAddress(device.getMacAddress()).isPresent()) {
            throw new IllegalArgumentException("Duplicate MAC address: " + device.getMacAddress());
        }

        if (device.getUplinkMacAddress() != null) {
            if (device.getMacAddress().equals(device.getUplinkMacAddress())) {
                throw new IllegalArgumentException("Device cannot be its own uplink");
            }
        }
    }

}
