package com.ui.ubiquitiassignment.repository;

import com.ui.ubiquitiassignment.helper.DeviceTestHelper;
import com.ui.ubiquitiassignment.model.Device;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class DeviceRepositoryTest {

    @Test
    void save() {
        // Given
        DeviceRepository deviceRepository = new DeviceRepository();
        Device device = DeviceTestHelper.buildDeviceWithMacAddresses("00:11:22:33:44:55");

        // When
        deviceRepository.save(device);

        // Then
        assertThat(deviceRepository.findAll()).contains(device);
    }

    @Test
    void findAll() {
        // Given
        DeviceRepository deviceRepository = new DeviceRepository();
        Device device1 = DeviceTestHelper.buildDeviceWithMacAddresses("00:11:22:33:44:55");
        Device device2 = DeviceTestHelper.buildDeviceWithMacAddresses("00:11:22:33:44:56");
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
        Device device = DeviceTestHelper.buildDeviceWithMacAddresses("00:11:22:33:44:55");
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
        Device device = DeviceTestHelper.buildDeviceWithMacAddresses("00:11:22:33:44:55");
        deviceRepository.save(device);

        // When
        Optional<Device> foundDevice = deviceRepository.findByMacAddress("00:11:22:33:44:56");

        // Then
        assertThat(foundDevice).isNotPresent();
    }

}
