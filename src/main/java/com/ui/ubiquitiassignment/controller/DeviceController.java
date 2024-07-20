package com.ui.ubiquitiassignment.controller;

import com.ui.ubiquitiassignment.controller.dto.DeviceRequest;
import com.ui.ubiquitiassignment.controller.dto.DeviceResponse;
import com.ui.ubiquitiassignment.exception.DeviceNotFoundException;
import com.ui.ubiquitiassignment.mapper.DeviceMapper;
import com.ui.ubiquitiassignment.model.DeviceTopology;
import com.ui.ubiquitiassignment.service.DeviceService;
import com.ui.ubiquitiassignment.validation.MacAddress;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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

    private final DeviceMapper deviceMapper;
    private final DeviceService deviceService;

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping
    public void registerDevice(@Valid @RequestBody DeviceRequest deviceRequest) {
        deviceService.registerDevice(deviceMapper.toDevice(deviceRequest));
    }

    @GetMapping
    public List<DeviceResponse> getAllDevices() {
        return deviceService.getAllDevicesSortedByDeviceTypePriority().stream()
                .map(deviceMapper::toDeviceResponse)
                .toList();
    }

    @GetMapping("/{macAddress}")
    public DeviceResponse getDeviceByMacAddress(@NotEmpty @MacAddress @PathVariable String macAddress) {
        return deviceService.getDeviceByMacAddress(macAddress)
                .map(deviceMapper::toDeviceResponse)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found with MAC address: " + macAddress));
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
