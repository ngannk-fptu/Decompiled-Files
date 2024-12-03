/*
 * Decompiled with CFR 0.152.
 */
package oshi.hardware.platform.unix.aix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.UsbDevice;
import oshi.hardware.common.AbstractUsbDevice;
import oshi.util.ParseUtil;

@Immutable
public class AixUsbDevice
extends AbstractUsbDevice {
    public AixUsbDevice(String name, String vendor, String vendorId, String productId, String serialNumber, String uniqueDeviceId, List<UsbDevice> connectedDevices) {
        super(name, vendor, vendorId, productId, serialNumber, uniqueDeviceId, connectedDevices);
    }

    public static List<UsbDevice> getUsbDevices(boolean tree, Supplier<List<String>> lscfg) {
        ArrayList<UsbDevice> deviceList = new ArrayList<UsbDevice>();
        for (String line : lscfg.get()) {
            String[] split;
            String s = line.trim();
            if (!s.startsWith("usb") || (split = ParseUtil.whitespaces.split(s, 3)).length != 3) continue;
            deviceList.add(new AixUsbDevice(split[2], "unknown", "unknown", "unknown", "unknown", split[0], Collections.emptyList()));
        }
        if (tree) {
            return Arrays.asList(new AixUsbDevice("USB Controller", "", "0000", "0000", "", "", deviceList));
        }
        return deviceList;
    }
}

