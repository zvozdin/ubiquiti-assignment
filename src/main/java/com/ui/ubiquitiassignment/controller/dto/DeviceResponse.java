package com.ui.ubiquitiassignment.controller.dto;

import com.ui.ubiquitiassignment.constant.DeviceType;

public record DeviceResponse(DeviceType deviceType, String macAddress) {}
