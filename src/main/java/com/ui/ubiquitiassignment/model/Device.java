package com.ui.ubiquitiassignment.model;

import com.ui.ubiquitiassignment.constant.DeviceType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Device {

    private DeviceType deviceType;
    private String macAddress;
    private String uplinkMacAddress;
    private List<String> downlinkMacAddresses = new ArrayList<>();
}
