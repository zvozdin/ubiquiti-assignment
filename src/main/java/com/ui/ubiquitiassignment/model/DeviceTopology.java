package com.ui.ubiquitiassignment.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@NoArgsConstructor
public class DeviceTopology {

    private String rootMacAddress;
    private List<DeviceTopology> downlinkDevices = new ArrayList<>();
}
