/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.platform.win32.Advapi32
 *  com.sun.jna.platform.win32.Guid$GUID
 *  com.sun.jna.platform.win32.SetupApi
 *  com.sun.jna.platform.win32.SetupApi$SP_DEVICE_INTERFACE_DATA
 *  com.sun.jna.platform.win32.SetupApi$SP_DEVINFO_DATA
 *  com.sun.jna.platform.win32.WinBase
 *  com.sun.jna.platform.win32.WinNT$HANDLE
 *  com.sun.jna.platform.win32.WinReg$HKEY
 *  com.sun.jna.ptr.IntByReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.SetupApi;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Display;
import oshi.hardware.common.AbstractDisplay;

@Immutable
final class WindowsDisplay
extends AbstractDisplay {
    private static final Logger LOG = LoggerFactory.getLogger(WindowsDisplay.class);
    private static final SetupApi SU = SetupApi.INSTANCE;
    private static final Advapi32 ADV = Advapi32.INSTANCE;
    private static final Guid.GUID GUID_DEVINTERFACE_MONITOR = new Guid.GUID("E6F07B5F-EE97-4a90-B076-33F57BF4EAA7");

    WindowsDisplay(byte[] edid) {
        super(edid);
        LOG.debug("Initialized WindowsDisplay");
    }

    public static List<Display> getDisplays() {
        ArrayList<Display> displays = new ArrayList<Display>();
        WinNT.HANDLE hDevInfo = SU.SetupDiGetClassDevs(GUID_DEVINTERFACE_MONITOR, null, null, 18);
        if (!hDevInfo.equals((Object)WinBase.INVALID_HANDLE_VALUE)) {
            SetupApi.SP_DEVICE_INTERFACE_DATA deviceInterfaceData = new SetupApi.SP_DEVICE_INTERFACE_DATA();
            deviceInterfaceData.cbSize = deviceInterfaceData.size();
            SetupApi.SP_DEVINFO_DATA info = new SetupApi.SP_DEVINFO_DATA();
            int memberIndex = 0;
            while (SU.SetupDiEnumDeviceInfo(hDevInfo, memberIndex, info)) {
                IntByReference lpcbData;
                byte[] edid;
                IntByReference pType;
                WinReg.HKEY key = SU.SetupDiOpenDevRegKey(hDevInfo, info, 1, 0, 1, 1);
                if (ADV.RegQueryValueEx(key, "EDID", 0, pType = new IntByReference(), edid = new byte[1], lpcbData = new IntByReference()) == 234 && ADV.RegQueryValueEx(key, "EDID", 0, pType, edid = new byte[lpcbData.getValue()], lpcbData) == 0) {
                    WindowsDisplay display = new WindowsDisplay(edid);
                    displays.add(display);
                }
                Advapi32.INSTANCE.RegCloseKey(key);
                ++memberIndex;
            }
            SU.SetupDiDestroyDeviceInfoList(hDevInfo);
        }
        return displays;
    }
}

