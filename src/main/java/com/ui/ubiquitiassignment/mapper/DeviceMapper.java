package com.ui.ubiquitiassignment.mapper;

import com.ui.ubiquitiassignment.controller.dto.DeviceRequest;
import com.ui.ubiquitiassignment.controller.dto.DeviceResponse;
import com.ui.ubiquitiassignment.model.Device;
import org.mapstruct.Mapper;

@Mapper
public interface DeviceMapper {

    Device toDevice(DeviceRequest device);

    DeviceResponse toDeviceResponse(Device device);
}
