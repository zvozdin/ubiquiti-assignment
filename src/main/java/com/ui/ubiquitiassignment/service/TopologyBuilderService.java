package com.ui.ubiquitiassignment.service;

import com.ui.ubiquitiassignment.exception.DeviceNotFoundException;
import com.ui.ubiquitiassignment.model.Device;
import com.ui.ubiquitiassignment.model.DeviceTopology;
import com.ui.ubiquitiassignment.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class TopologyBuilderService {

    private final DeviceRepository deviceRepository;

    public DeviceTopology buildTopologyFromMap(Device root, Map<String, Device> devices) {
        DeviceTopology deviceTopology = new DeviceTopology();
        deviceTopology.setRootMacAddress(root.getMacAddress());
        List<DeviceTopology> rootTopologies = deviceTopology.getDownlinkDevices();
        buildTopology(root, rootTopologies, devices);
        return deviceTopology;
    }

    public DeviceTopology buildTopologyByMacAddress(String macAddress) {
        DeviceTopology deviceTopology = new DeviceTopology();
        Device root = deviceRepository.findByMacAddress(macAddress)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found: " + macAddress));

        deviceTopology.setRootMacAddress(root.getMacAddress());

        // todo: ids of downlink devices -> how to identify all we need to retrieve by single call
        buildTopologyByLookupInStorage(root, deviceTopology.getDownlinkDevices());
        return deviceTopology;
    }

    private void buildTopologyByLookupInStorage(Device root, List<DeviceTopology> rootDownlinkTopologies) {
        if (!CollectionUtils.isEmpty(root.getDownlinkMacAddresses())) {
            for (String downlinkMacAddress : root.getDownlinkMacAddresses()) {
                Device downlinkDevice =
                        deviceRepository.findByMacAddress(downlinkMacAddress)
                                .orElseThrow(() -> new DeviceNotFoundException("Device not found: " + downlinkMacAddress));

                DeviceTopology downlinkTopology = new DeviceTopology();
                downlinkTopology.setRootMacAddress(downlinkDevice.getMacAddress());
                rootDownlinkTopologies.add(downlinkTopology);
                buildTopologyByLookupInStorage(downlinkDevice, downlinkTopology.getDownlinkDevices());
            }
        }
    }

    private void buildTopology(Device root, List<DeviceTopology> rootTopologies, Map<String, Device> devices) {
        if (!CollectionUtils.isEmpty(root.getDownlinkMacAddresses())) {
            root.getDownlinkMacAddresses().stream()
                    .map(devices::get)
                    .forEach(downlinkDevice -> {
                        DeviceTopology downlinkTopology = new DeviceTopology();
                        downlinkTopology.setRootMacAddress(downlinkDevice.getMacAddress());
                        rootTopologies.add(downlinkTopology);
                        buildTopology(downlinkDevice, downlinkTopology.getDownlinkDevices(), devices);
                    });
        }
    }

}
