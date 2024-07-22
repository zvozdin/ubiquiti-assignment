package com.ui.ubiquitiassignment.repository;

import com.ui.ubiquitiassignment.model.Device;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class DeviceRepository {

    private final Map<String, Device> devices = new HashMap<>();

    public void save(Device device) {
        devices.put(device.getMacAddress(), device);
    }

    public List<Device> findAll() {
        return new ArrayList<>(devices.values());
    }

    public Optional<Device> findByMacAddress(String macAddress) {
        return Optional.ofNullable(devices.get(macAddress));
    }

}
