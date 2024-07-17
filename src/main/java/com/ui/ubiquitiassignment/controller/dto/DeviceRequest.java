package com.ui.ubiquitiassignment.controller.dto;

import com.ui.ubiquitiassignment.common.DeviceType;
import jakarta.validation.constraints.NotEmpty;

public record DeviceRequest(@NotEmpty DeviceType deviceType, /*todo @macAddress*/ String macAddress, String uplinkMacAddress) {}
