package com.ui.ubiquitiassignment.service;

import com.ui.ubiquitiassignment.constant.DeviceType;
import com.ui.ubiquitiassignment.exception.DeviceNotFoundException;
import com.ui.ubiquitiassignment.helper.DeviceTestHelper;
import com.ui.ubiquitiassignment.model.Device;
import com.ui.ubiquitiassignment.model.DeviceTopology;
import com.ui.ubiquitiassignment.repository.DeviceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private TopologyBuilderService topologyBuilderService;

    @InjectMocks
    private DeviceService deviceService;

    @Test
    void registerDevice_shouldSaveDeviceWithoutUplink() {
        // Given
        Device device =
                DeviceTestHelper.buildDeviceWithTypeAndMacAddresses(DeviceType.GATEWAY, "00:11:22:33:44:55", null);

        // When
        deviceService.registerDevice(device);

        // Then
        verify(deviceRepository).save(device);
    }

    @Test
    void registerDevice_shouldSaveDeviceWithUplink() {
        // Given
        Device device =
                DeviceTestHelper.buildDeviceWithTypeAndMacAddresses(DeviceType.SWITCH, "00:11:22:33:44:56", "00:11:22:33:44:55");
        Device uplinkDevice =
                DeviceTestHelper.buildDeviceWithTypeAndMacAddresses(DeviceType.GATEWAY, "00:11:22:33:44:55", null);

        when(deviceRepository.findByMacAddress(device.getUplinkMacAddress())).thenReturn(Optional.of(uplinkDevice));
        when(deviceRepository.findByMacAddress(device.getMacAddress())).thenReturn(Optional.empty());

        // When
        deviceService.registerDevice(device);

        // Then
        verify(deviceRepository).save(device);
        verify(deviceRepository).save(uplinkDevice);

        assertThat(uplinkDevice.getDownlinkMacAddresses()).contains(device.getMacAddress());
    }

    @Test
    void registerDevice_shouldThrowExceptionIfUplinkNotFound() {
        // Given
        Device device =
                DeviceTestHelper.buildDeviceWithTypeAndMacAddresses(DeviceType.SWITCH, "00:11:22:33:44:56", "00:11:22:33:44:55");

        when(deviceRepository.findByMacAddress(anyString())).thenReturn(Optional.empty());

        // When - Then
        assertThatThrownBy(() -> deviceService.registerDevice(device))
                .isInstanceOf(DeviceNotFoundException.class)
                .hasMessageContaining("Uplink device not found: " + device.getUplinkMacAddress());
    }

    @Test
    void getAllDevicesSortedByDeviceTypePriority_shouldReturnSortedDevices() {
        // Given
        Device gateway =
                DeviceTestHelper.buildDeviceWithTypeAndMacAddresses(DeviceType.GATEWAY, "00:11:22:33:44:55", null);
        Device switchDevice =
                DeviceTestHelper.buildDeviceWithTypeAndMacAddresses(DeviceType.SWITCH, "00:11:22:33:44:56", "00:11:22:33:44:55");
        Device accessPoint =
                DeviceTestHelper.buildDeviceWithTypeAndMacAddresses(DeviceType.ACCESS_POINT, "00:11:22:33:44:57", "00:11:22:33:44:56");

        when(deviceRepository.findAll()).thenReturn(List.of(switchDevice, accessPoint, gateway));

        // When
        List<Device> sortedDevices = deviceService.getAllDevicesSortedByDeviceTypePriority();

        // Then
        assertThat(sortedDevices).containsExactly(gateway, switchDevice, accessPoint);
    }

    @Test
    void getDeviceByMacAddress_shouldReturnDeviceIfExists() {
        // Given
        Device device = DeviceTestHelper.buildDeviceWithMacAddresses("00:11:22:33:44:55");
        when(deviceRepository.findByMacAddress(device.getMacAddress())).thenReturn(Optional.of(device));

        // When
        Optional<Device> foundDevice = deviceService.getDeviceByMacAddress(device.getMacAddress());

        // Then
        assertThat(foundDevice).isPresent().contains(device);
    }

    @Test
    void getDeviceByMacAddress_shouldReturnEmptyIfNotExists() {
        // Given
        String macAddress = "00:11:22:33:44:55";
        when(deviceRepository.findByMacAddress(macAddress)).thenReturn(Optional.empty());

        // When
        Optional<Device> foundDevice = deviceService.getDeviceByMacAddress(macAddress);

        // Then
        assertThat(foundDevice).isEmpty();
    }

    @Test
    void getNetworkTopologyByMacAddress_shouldReturnTopology() {
        // Given
        String macAddress = "00:11:22:33:44:55";
        DeviceTopology topology = new DeviceTopology();
        topology.setRootMacAddress(macAddress);
        when(topologyBuilderService.buildTopologyByMacAddress(macAddress)).thenReturn(topology);

        // When
        DeviceTopology result = deviceService.getNetworkTopologyByMacAddress(macAddress);

        // Then
        assertThat(result).isEqualTo(topology);
    }

    @Test
    void getFullNetworkTopology_shouldReturnFullTopology() {
        // Given
        Device root = DeviceTestHelper.buildDeviceWithMacAddresses("00:11:22:33:44:55");
        Device child = DeviceTestHelper.buildDeviceWithMacAddresses("00:11:22:33:44:66");
        root.getDownlinkMacAddresses().add(child.getMacAddress());
        child.setUplinkMacAddress(root.getMacAddress());
        root.setDownlinkMacAddresses(List.of(child.getMacAddress()));

        when(deviceRepository.findAll()).thenReturn(List.of(root, child));

        Map<Boolean, Map<String, Device>> devices = Map.of(
                true, Map.of(root.getMacAddress(), root),
                false, Map.of(child.getMacAddress(), child)
        );
        DeviceTopology rootTopology = new DeviceTopology();
        rootTopology.setRootMacAddress(root.getMacAddress());
        when(topologyBuilderService.buildTopologyFromMap(root, devices.get(false))).thenReturn(rootTopology);

        // When
        List<DeviceTopology> result = deviceService.getFullNetworkTopology();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRootMacAddress()).isEqualTo(root.getMacAddress());
    }

}
