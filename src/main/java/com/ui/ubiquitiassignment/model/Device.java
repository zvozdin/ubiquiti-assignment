package com.ui.ubiquitiassignment.model;

import com.ui.ubiquitiassignment.constant.DeviceType;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


// todo try to implement dto to get rid of childDevices and have them in a model
/*
 * Device model class
 * Contains the device type, mac address, uplink mac address, child devices
 *
 * NOTE: To simplify the implementation, it was decided to skip creating DTOs and use the model class directly in requests/responses
 * but it is not recommended in a real-world application
 */
@Data
@NoArgsConstructor
public class Device {

    @NotEmpty
    private DeviceType type;

    // https://gist.github.com/takeouchida/b520471123bca4f71c4d
    private String macAddress;

    private String uplinkMacAddress;
    private List<String> downlinkMacAddresses = new ArrayList<>();

    // todo test case about can register device with mac address that already exists
}
