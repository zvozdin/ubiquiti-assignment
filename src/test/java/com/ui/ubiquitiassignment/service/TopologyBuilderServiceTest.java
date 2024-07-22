package com.ui.ubiquitiassignment.service;

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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TopologyBuilderServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private TopologyBuilderService topologyBuilderService;

    @Test
    void buildTopologyFromMap_shouldBuildCorrectTopology() {
        // Given
        Device root = DeviceTestHelper.buildDeviceWithMacAddresses("00:11:22:33:44:55");
        Device child = DeviceTestHelper.buildDeviceWithMacAddresses("00:11:22:33:44:66");
        root.setDownlinkMacAddresses(List.of(child.getMacAddress()));
        Map<String, Device> devices =
                Map.of(
                        root.getMacAddress(), root,
                        child.getMacAddress(), child
                );

        // When
        DeviceTopology topology = topologyBuilderService.buildTopologyFromMap(root, devices);

        // Then
        assertThat(topology.getRootMacAddress()).isEqualTo(root.getMacAddress());
        assertThat(topology.getDownlinkDevices()).hasSize(1);
        assertThat(topology.getDownlinkDevices().get(0).getRootMacAddress()).isEqualTo(child.getMacAddress());
    }

    @Test
    void buildTopologyByMacAddress_shouldBuildCorrectTopology() {
        // Given
        Device root = DeviceTestHelper.buildDeviceWithMacAddresses("00:11:22:33:44:55");;
        Device child = DeviceTestHelper.buildDeviceWithMacAddresses("00:11:22:33:44:66");
        root.setDownlinkMacAddresses(List.of(child.getMacAddress()));
        when(deviceRepository.findByMacAddress(root.getMacAddress())).thenReturn(Optional.of(root));
        when(deviceRepository.findByMacAddress(child.getMacAddress())).thenReturn(Optional.of(child));

        // When
        DeviceTopology topology = topologyBuilderService.buildTopologyByMacAddress(root.getMacAddress());

        // Then
        assertThat(topology.getRootMacAddress()).isEqualTo(root.getMacAddress());
        assertThat(topology.getDownlinkDevices()).hasSize(1);
        assertThat(topology.getDownlinkDevices().get(0).getRootMacAddress()).isEqualTo(child.getMacAddress());
    }

    @Test
    void buildTopologyByMacAddress_shouldThrowExceptionIfDeviceNotFound() {
        // Given
        String macAddress = "00:11:22:33:44:55";
        when(deviceRepository.findByMacAddress(macAddress)).thenReturn(Optional.empty());

        // When - Then
        assertThatThrownBy(() -> topologyBuilderService.buildTopologyByMacAddress(macAddress))
                .isInstanceOf(DeviceNotFoundException.class)
                .hasMessageContaining("Device not found: " + macAddress);
    }

}
