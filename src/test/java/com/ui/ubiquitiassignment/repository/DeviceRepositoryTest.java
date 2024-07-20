package com.ui.ubiquitiassignment.repository;

import com.ui.ubiquitiassignment.constant.DeviceType;
import com.ui.ubiquitiassignment.model.Device;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DeviceRepositoryTest {

    @Test
    void save() {
        // Given
        DeviceRepository deviceRepository = new DeviceRepository();
        Device device = buildDevice();

        // When
        deviceRepository.save(device);

        // Then
        assertThat(deviceRepository.findAll()).contains(device);
    }

    @Test
    void findAll() {
        // Given
        DeviceRepository deviceRepository = new DeviceRepository();
        Device device1 = buildDevice();
        Device device2 = buildDevice();
        deviceRepository.save(device1);
        deviceRepository.save(device2);

        // When
        List<Device> devices = deviceRepository.findAll();

        // Then
        assertThat(devices).containsExactlyInAnyOrder(device1, device2);
    }

    @Test
    void findByMacAddress() {
        // Given
        DeviceRepository deviceRepository = new DeviceRepository();
        Device device = buildDevice();
        deviceRepository.save(device);

        // When
        Optional<Device> foundDevice = deviceRepository.findByMacAddress("00:11:22:33:44:55");

        // Then
        assertThat(foundDevice).isPresent();
        assertThat(foundDevice.get()).isEqualTo(device);
    }

    @Test
    void findByMacAddress_NotFound() {
        // Given
        DeviceRepository deviceRepository = new DeviceRepository();
        Device device = buildDevice();
        deviceRepository.save(device);

        // When
        Optional<Device> foundDevice = deviceRepository.findByMacAddress("00:11:22:33:44:56");

        // Then
        assertThat(foundDevice).isNotPresent();
    }

    private Device buildDevice() {
        Device device = new Device();
        device.setDeviceType(DeviceType.GATEWAY);
        device.setMacAddress("00:11:22:33:44:55");
        return device;
    }

}
