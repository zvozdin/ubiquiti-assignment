package com.ui.ubiquitiassignment.controller.dto;

import com.ui.ubiquitiassignment.constant.DeviceType;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class DeviceRequest {

    // todo make it required cuz NotEmpty doesn't work. custom annotation?
    private DeviceType deviceType;
// todo uncomment
//    @Pattern(regexp = "([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})", message = "Invalid MAC address")
    @NotEmpty
    private String macAddress;

    // todo if present, it should be a valid mac address
    private String uplinkMacAddress;
}
