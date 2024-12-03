/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Memory
 *  com.sun.jna.Pointer
 *  com.sun.jna.platform.win32.Cfgmgr32
 *  com.sun.jna.platform.win32.Cfgmgr32Util
 *  com.sun.jna.platform.win32.Guid$GUID
 *  com.sun.jna.platform.win32.SetupApi
 *  com.sun.jna.platform.win32.SetupApi$SP_DEVINFO_DATA
 *  com.sun.jna.platform.win32.WinBase
 *  com.sun.jna.platform.win32.WinNT$HANDLE
 *  com.sun.jna.ptr.IntByReference
 */
package oshi.driver.windows;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Cfgmgr32;
import com.sun.jna.platform.win32.Cfgmgr32Util;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.SetupApi;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.tuples.Quintet;

@ThreadSafe
public final class DeviceTree {
    private static final int MAX_PATH = 260;
    private static final SetupApi SA = SetupApi.INSTANCE;
    private static final Cfgmgr32 C32 = Cfgmgr32.INSTANCE;

    private DeviceTree() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Quintet<Set<Integer>, Map<Integer, Integer>, Map<Integer, String>, Map<Integer, String>, Map<Integer, String>> queryDeviceTree(Guid.GUID guidDevInterface) {
        HashMap<Integer, Integer> parentMap = new HashMap<Integer, Integer>();
        HashMap<Integer, String> nameMap = new HashMap<Integer, String>();
        HashMap<Integer, String> deviceIdMap = new HashMap<Integer, String>();
        HashMap<Integer, String> mfgMap = new HashMap<Integer, String>();
        WinNT.HANDLE hDevInfo = SA.SetupDiGetClassDevs(guidDevInterface, null, null, 18);
        if (!WinBase.INVALID_HANDLE_VALUE.equals((Object)hDevInfo)) {
            try {
                Memory buf = new Memory(260L);
                IntByReference size = new IntByReference(260);
                ArrayDeque<Integer> deviceTree = new ArrayDeque<Integer>();
                SetupApi.SP_DEVINFO_DATA devInfoData = new SetupApi.SP_DEVINFO_DATA();
                devInfoData.cbSize = devInfoData.size();
                int i = 0;
                while (SA.SetupDiEnumDeviceInfo(hDevInfo, i, devInfoData)) {
                    deviceTree.add(devInfoData.DevInst);
                    int node = 0;
                    IntByReference child = new IntByReference();
                    IntByReference sibling = new IntByReference();
                    while (!deviceTree.isEmpty()) {
                        node = (Integer)deviceTree.poll();
                        String deviceId = Cfgmgr32Util.CM_Get_Device_ID((int)node);
                        deviceIdMap.put(node, deviceId);
                        String name = DeviceTree.getDevNodeProperty(node, 13, buf, size);
                        if (name.isEmpty()) {
                            name = DeviceTree.getDevNodeProperty(node, 1, buf, size);
                        }
                        if (name.isEmpty()) {
                            name = DeviceTree.getDevNodeProperty(node, 8, buf, size);
                            String svc = DeviceTree.getDevNodeProperty(node, 5, buf, size);
                            if (!svc.isEmpty()) {
                                name = name + " (" + svc + ")";
                            }
                        }
                        nameMap.put(node, name);
                        mfgMap.put(node, DeviceTree.getDevNodeProperty(node, 12, buf, size));
                        if (0 != C32.CM_Get_Child(child, node, 0)) continue;
                        parentMap.put(child.getValue(), node);
                        deviceTree.add(child.getValue());
                        while (0 == C32.CM_Get_Sibling(sibling, child.getValue(), 0)) {
                            parentMap.put(sibling.getValue(), node);
                            deviceTree.add(sibling.getValue());
                            child.setValue(sibling.getValue());
                        }
                    }
                    ++i;
                }
            }
            finally {
                SA.SetupDiDestroyDeviceInfoList(hDevInfo);
            }
        }
        Set controllerDevices = deviceIdMap.keySet().stream().filter(k -> !parentMap.containsKey(k)).collect(Collectors.toSet());
        return new Quintet<Set<Integer>, Map<Integer, Integer>, Map<Integer, String>, Map<Integer, String>, Map<Integer, String>>(controllerDevices, parentMap, nameMap, deviceIdMap, mfgMap);
    }

    private static String getDevNodeProperty(int node, int cmDrp, Memory buf, IntByReference size) {
        buf.clear();
        size.setValue((int)buf.size());
        C32.CM_Get_DevNode_Registry_Property(node, cmDrp, null, (Pointer)buf, size, 0);
        return buf.getWideString(0L);
    }
}

