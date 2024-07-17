package com.ui.ubiquitiassignment.controller.dto;

import com.ui.ubiquitiassignment.common.DeviceType;

public record DeviceResponse(DeviceType deviceType, String macAddress) {}
