package com.ui.ubiquitiassignment.controller.dto;

import com.ui.ubiquitiassignment.common.DeviceType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DeviceRequest {

    private DeviceType deviceType;
// todo uncomment
//    @Pattern(regexp = "([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})", message = "Invalid MAC address")
    @NotEmpty
    private String macAddress;
    private String uplinkMacAddress;
}
