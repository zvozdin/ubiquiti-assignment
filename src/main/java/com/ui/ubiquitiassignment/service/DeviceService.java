package com.ui.ubiquitiassignment.service;

import com.ui.ubiquitiassignment.common.DeviceType;
import com.ui.ubiquitiassignment.model.Device;
import com.ui.ubiquitiassignment.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DeviceService {

    private DeviceRepository deviceRepository;

    public void registerDevice(Device device) {
        // todo
        validateDevice(device);

        if (device.getUplinkMacAddress() != null) {
            deviceRepository.findByMacAddress(device.getUplinkMacAddress())
                    .ifPresentOrElse(
                            uplinkDevice -> uplinkDevice.getChildDevices().add(device),
                            () -> {
                                throw new IllegalArgumentException("Uplink device not found: " + device.getUplinkMacAddress());
                            });
        }
        deviceRepository.save(device);
    }

    public List<Device> getAllDevicesSortedByDeviceTypePriority() {
        return deviceRepository.findAll().stream()
                .filter(device -> device.getType() != null && device.getMacAddress() != null) // todo write a test for this
                .sorted(Comparator.comparingInt(device -> switch (device.getType()) {
                    case DeviceType.GATEWAY -> 1;
                    case DeviceType.SWITCH -> 2;
                    case DeviceType.ACCESS_POINT -> 3;
                }))
                .collect(Collectors.toList());
    }

    public Optional<Device> getDeviceByMacAddress(String macAddress) {
        return deviceRepository.findByMacAddress(macAddress);
    }

    public List<Device> getNetworkTopology(String macAddress) {
        Optional<Device> rootDeviceOpt = getDeviceByMacAddress(macAddress);
        if (rootDeviceOpt.isPresent()) {
            Device rootDevice = rootDeviceOpt.get();
            Map<String, Device> deviceMap = buildDeviceMap();
            buildTopology(rootDevice, deviceMap);
            return Collections.singletonList(rootDevice);
        }
        return Collections.emptyList();
    }

    public List<Device> getFullNetworkTopology() {
        Map<String, Device> deviceMap = buildDeviceMap();
        List<Device> roots = findRootDevices(deviceMap);
        roots.forEach(root -> buildTopology(root, deviceMap));
        return roots;
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



    private Map<String, Device> buildDeviceMap() {
        List<Device> allDevices = deviceRepository.findAll();
        Map<String, Device> deviceMap = new HashMap<>();
        for (Device device : allDevices) {
            deviceMap.put(device.getMacAddress(), device);
        }
        return deviceMap;
    }

    private List<Device> findRootDevices(Map<String, Device> deviceMap) {
        List<Device> roots = new ArrayList<>();
        for (Device device : deviceMap.values()) {
            if (device.getUplinkMacAddress() == null) {
                roots.add(device);
            }
        }
        return roots;
    }

    private void buildTopology(Device root, Map<String, Device> deviceMap) {
        for (Device device : deviceMap.values()) {
            if (root.getMacAddress().equals(device.getUplinkMacAddress())) {
                root.getChildDevices().add(device);
                buildTopology(device, deviceMap);
            }
        }
    }
}
