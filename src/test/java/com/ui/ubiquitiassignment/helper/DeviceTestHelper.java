package com.ui.ubiquitiassignment.helper;

import com.ui.ubiquitiassignment.constant.DeviceType;
import com.ui.ubiquitiassignment.model.Device;

public class DeviceTestHelper {

    public static Device buildDeviceWithMacAddresses(String macAddress) {
        Device device = new Device();
        device.setDeviceType(DeviceType.GATEWAY);
        device.setMacAddress(macAddress);
        return device;
    }

    public static Device buildDeviceWithTypeAndMacAddresses(DeviceType deviceType, String macAddress, String uplinkMacAddress) {
        Device device = new Device();
        device.setDeviceType(deviceType);
        device.setMacAddress(macAddress);
        device.setUplinkMacAddress(uplinkMacAddress);
        return device;
    }

}