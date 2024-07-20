package com.ui.ubiquitiassignment.repository;

import com.ui.ubiquitiassignment.model.Device;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class DeviceRepository {

    private final List<Device> devices = new ArrayList<>();

    public void save(Device device) {
        devices.add(device);
    }

    public List<Device> findAll() {
        return devices;
    }

    public Optional<Device> findByMacAddress(String macAddress) {
        return devices.stream()
                .filter(device -> device.getMacAddress().equals(macAddress))
                .findFirst();
    }

}
