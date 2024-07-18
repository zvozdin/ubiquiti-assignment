package com.ui.ubiquitiassignment.model;

import com.ui.ubiquitiassignment.common.DeviceType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class DeviceTopology {

    private DeviceType rootDeviceType;
    private String rootMacAddress;

    // todo don't serialize this in response json if it is empty
    private List<DeviceTopology> downlinkDevices = new ArrayList<>();
}
