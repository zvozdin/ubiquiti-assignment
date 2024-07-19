package com.ui.ubiquitiassignment.controller;

import com.ui.ubiquitiassignment.controller.dto.DeviceRequest;
import com.ui.ubiquitiassignment.controller.dto.DeviceResponse;
import com.ui.ubiquitiassignment.exception.DeviceNotFoundException;
import com.ui.ubiquitiassignment.model.Device;
import com.ui.ubiquitiassignment.model.DeviceTopology;
import com.ui.ubiquitiassignment.service.DeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/devices")
public class DeviceController {

    // todo exception handling 1. not found when linking 2. invalid mac address 3. invalid device type 4. invalid uplink mac address

    private final DeviceService deviceService;

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping
    public void registerDevice(@Valid @RequestBody DeviceRequest deviceRequest) {
        //TODO IMPLEMENT MAPPER via Mapstruct
        Device device = new Device();
        device.setType(deviceRequest.getDeviceType());
        device.setMacAddress(deviceRequest.getMacAddress());
        device.setUplinkMacAddress(deviceRequest.getUplinkMacAddress());

        deviceService.registerDevice(device);
    }

    @GetMapping
    public List<DeviceResponse> getAllDevices() {
        return deviceService.getAllDevicesSortedByDeviceTypePriority().stream()
                .map(device -> new DeviceResponse(device.getType(), device.getMacAddress()))
                .toList();
    }

    @GetMapping("/{macAddress}")
    public DeviceResponse getDeviceByMacAddress(@PathVariable String macAddress) { // todo custom @Macaddress validation instead of @Valid
        return deviceService.getDeviceByMacAddress(macAddress)
                .map(device -> new DeviceResponse(device.getType(), device.getMacAddress()))
                .orElseThrow(() -> new DeviceNotFoundException(macAddress));
    }

    @GetMapping("/topology")
    public List<DeviceTopology> getFullNetworkTopology() {
        return deviceService.getFullNetworkTopology();
    }

    @GetMapping("/topology/{macAddress}")
    public DeviceTopology getNetworkTopology(@PathVariable String macAddress) {
        return deviceService.getNetworkTopology(macAddress);
    }

}
