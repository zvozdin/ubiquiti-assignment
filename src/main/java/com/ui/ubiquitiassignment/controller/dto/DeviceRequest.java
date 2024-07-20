package com.ui.ubiquitiassignment.controller.dto;

import com.ui.ubiquitiassignment.constant.DeviceType;
import com.ui.ubiquitiassignment.validation.MacAddress;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class DeviceRequest {

    private DeviceType deviceType;

    @NotEmpty
    @MacAddress
    private String macAddress;

    @MacAddress
    private String uplinkMacAddress;
}
