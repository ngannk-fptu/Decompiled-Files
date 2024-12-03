/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Memory
 *  com.sun.jna.Native
 *  com.sun.jna.Platform
 *  com.sun.jna.Pointer
 *  com.sun.jna.platform.win32.Guid$GUID
 *  com.sun.jna.platform.win32.Kernel32
 *  com.sun.jna.platform.win32.SetupApi
 *  com.sun.jna.platform.win32.SetupApi$SP_DEVICE_INTERFACE_DATA
 *  com.sun.jna.platform.win32.WinBase
 *  com.sun.jna.platform.win32.WinNT$HANDLE
 *  com.sun.jna.ptr.IntByReference
 *  com.sun.jna.win32.W32APITypeMapper
 */
package oshi.hardware.platform.windows;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.SetupApi;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APITypeMapper;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.PowerSource;
import oshi.hardware.common.AbstractPowerSource;
import oshi.jna.platform.windows.PowrProf;

@ThreadSafe
public final class WindowsPowerSource
extends AbstractPowerSource {
    private static final Guid.GUID GUID_DEVCLASS_BATTERY = Guid.GUID.fromString((String)"{72631E54-78A4-11D0-BCF7-00AA00B7B32A}");
    private static final int CHAR_WIDTH = W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE ? 2 : 1;
    private static final boolean X64 = Platform.is64Bit();
    private static final int BATTERY_SYSTEM_BATTERY = Integer.MIN_VALUE;
    private static final int BATTERY_IS_SHORT_TERM = 0x20000000;
    private static final int BATTERY_POWER_ON_LINE = 1;
    private static final int BATTERY_DISCHARGING = 2;
    private static final int BATTERY_CHARGING = 4;
    private static final int BATTERY_CAPACITY_RELATIVE = 0x40000000;
    private static final int IOCTL_BATTERY_QUERY_TAG = 2703424;
    private static final int IOCTL_BATTERY_QUERY_STATUS = 2703436;
    private static final int IOCTL_BATTERY_QUERY_INFORMATION = 2703428;

    public WindowsPowerSource(String psName, String psDeviceName, double psRemainingCapacityPercent, double psTimeRemainingEstimated, double psTimeRemainingInstant, double psPowerUsageRate, double psVoltage, double psAmperage, boolean psPowerOnLine, boolean psCharging, boolean psDischarging, PowerSource.CapacityUnits psCapacityUnits, int psCurrentCapacity, int psMaxCapacity, int psDesignCapacity, int psCycleCount, String psChemistry, LocalDate psManufactureDate, String psManufacturer, String psSerialNumber, double psTemperature) {
        super(psName, psDeviceName, psRemainingCapacityPercent, psTimeRemainingEstimated, psTimeRemainingInstant, psPowerUsageRate, psVoltage, psAmperage, psPowerOnLine, psCharging, psDischarging, psCapacityUnits, psCurrentCapacity, psMaxCapacity, psDesignCapacity, psCycleCount, psChemistry, psManufactureDate, psManufacturer, psSerialNumber, psTemperature);
    }

    public static List<PowerSource> getPowerSources() {
        return Arrays.asList(WindowsPowerSource.getPowerSource("System Battery"));
    }

    private static WindowsPowerSource getPowerSource(String name) {
        WinNT.HANDLE hdev;
        String psName = name;
        String psDeviceName = "unknown";
        double psRemainingCapacityPercent = 1.0;
        double psTimeRemainingEstimated = -1.0;
        double psTimeRemainingInstant = 0.0;
        int psPowerUsageRate = 0;
        double psVoltage = -1.0;
        double psAmperage = 0.0;
        boolean psPowerOnLine = false;
        boolean psCharging = false;
        boolean psDischarging = false;
        PowerSource.CapacityUnits psCapacityUnits = PowerSource.CapacityUnits.RELATIVE;
        int psCurrentCapacity = 0;
        int psMaxCapacity = 1;
        int psDesignCapacity = 1;
        int psCycleCount = -1;
        String psChemistry = "unknown";
        LocalDate psManufactureDate = null;
        String psManufacturer = "unknown";
        String psSerialNumber = "unknown";
        double psTemperature = 0.0;
        int size = new PowrProf.SystemBatteryState().size();
        Memory mem = new Memory((long)size);
        if (0 == PowrProf.INSTANCE.CallNtPowerInformation(5, null, 0, (Pointer)mem, size)) {
            PowrProf.SystemBatteryState batteryState = new PowrProf.SystemBatteryState((Pointer)mem);
            if (batteryState.batteryPresent > 0) {
                if (batteryState.acOnLine == 0 && batteryState.charging == 0 && batteryState.discharging > 0) {
                    psTimeRemainingEstimated = batteryState.estimatedTime;
                } else if (batteryState.charging > 0) {
                    psTimeRemainingEstimated = -2.0;
                }
                psMaxCapacity = batteryState.maxCapacity;
                psCurrentCapacity = batteryState.remainingCapacity;
                psRemainingCapacityPercent = Math.min(1.0, (double)psCurrentCapacity / (double)psMaxCapacity);
                psPowerUsageRate = batteryState.rate;
            }
        }
        if (!WinBase.INVALID_HANDLE_VALUE.equals((Object)(hdev = SetupApi.INSTANCE.SetupDiGetClassDevs(GUID_DEVCLASS_BATTERY, null, null, 18)))) {
            boolean batteryFound = false;
            for (int idev = 0; !batteryFound && idev < 100; ++idev) {
                SetupApi.SP_DEVICE_INTERFACE_DATA did = new SetupApi.SP_DEVICE_INTERFACE_DATA();
                did.cbSize = did.size();
                if (SetupApi.INSTANCE.SetupDiEnumDeviceInterfaces(hdev, null, GUID_DEVCLASS_BATTERY, idev, did)) {
                    String devicePath;
                    WinNT.HANDLE hBattery;
                    IntByReference requiredSize = new IntByReference(0);
                    SetupApi.INSTANCE.SetupDiGetDeviceInterfaceDetail(hdev, did, null, 0, requiredSize, null);
                    if (122 != Kernel32.INSTANCE.GetLastError()) continue;
                    Memory pdidd = new Memory((long)requiredSize.getValue());
                    pdidd.setInt(0L, 4 + (X64 ? 4 : CHAR_WIDTH));
                    if (!SetupApi.INSTANCE.SetupDiGetDeviceInterfaceDetail(hdev, did, (Pointer)pdidd, (int)pdidd.size(), requiredSize, null) || WinBase.INVALID_HANDLE_VALUE.equals((Object)(hBattery = Kernel32.INSTANCE.CreateFile(devicePath = CHAR_WIDTH > 1 ? pdidd.getWideString(4L) : pdidd.getString(4L), -1073741824, 3, null, 3, 128, null)))) continue;
                    PowrProf.BATTERY_QUERY_INFORMATION bqi = new PowrProf.BATTERY_QUERY_INFORMATION();
                    IntByReference dwWait = new IntByReference(0);
                    IntByReference dwTag = new IntByReference();
                    IntByReference dwOut = new IntByReference();
                    if (Kernel32.INSTANCE.DeviceIoControl(hBattery, 2703424, dwWait.getPointer(), 4, dwTag.getPointer(), 4, dwOut, null)) {
                        bqi.BatteryTag = dwTag.getValue();
                        if (bqi.BatteryTag > 0) {
                            bqi.InformationLevel = PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatteryInformation.ordinal();
                            bqi.write();
                            PowrProf.BATTERY_INFORMATION bi = new PowrProf.BATTERY_INFORMATION();
                            if (Kernel32.INSTANCE.DeviceIoControl(hBattery, 2703428, bqi.getPointer(), bqi.size(), bi.getPointer(), bi.size(), dwOut, null)) {
                                bi.read();
                                if (0 != (bi.Capabilities & Integer.MIN_VALUE) && 0 == (bi.Capabilities & 0x20000000)) {
                                    if (0 == (bi.Capabilities & 0x40000000)) {
                                        psCapacityUnits = PowerSource.CapacityUnits.MWH;
                                    }
                                    psChemistry = Native.toString((byte[])bi.Chemistry, (Charset)StandardCharsets.US_ASCII);
                                    psDesignCapacity = bi.DesignedCapacity;
                                    psMaxCapacity = bi.FullChargedCapacity;
                                    psCycleCount = bi.CycleCount;
                                    PowrProf.BATTERY_WAIT_STATUS bws = new PowrProf.BATTERY_WAIT_STATUS();
                                    bws.BatteryTag = bqi.BatteryTag;
                                    bws.write();
                                    PowrProf.BATTERY_STATUS bs = new PowrProf.BATTERY_STATUS();
                                    if (Kernel32.INSTANCE.DeviceIoControl(hBattery, 2703436, bws.getPointer(), bws.size(), bs.getPointer(), bs.size(), dwOut, null)) {
                                        bs.read();
                                        if (0 != (bs.PowerState & 1)) {
                                            psPowerOnLine = true;
                                        }
                                        if (0 != (bs.PowerState & 2)) {
                                            psDischarging = true;
                                        }
                                        if (0 != (bs.PowerState & 4)) {
                                            psCharging = true;
                                        }
                                        psCurrentCapacity = bs.Capacity;
                                        psVoltage = bs.Voltage > 0 ? (double)bs.Voltage / 1000.0 : (double)bs.Voltage;
                                        psPowerUsageRate = bs.Rate;
                                        if (psVoltage > 0.0) {
                                            psAmperage = (double)psPowerUsageRate / psVoltage;
                                        }
                                    }
                                }
                                psDeviceName = WindowsPowerSource.batteryQueryString(hBattery, dwTag.getValue(), PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatteryDeviceName.ordinal());
                                psManufacturer = WindowsPowerSource.batteryQueryString(hBattery, dwTag.getValue(), PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatteryManufactureName.ordinal());
                                psSerialNumber = WindowsPowerSource.batteryQueryString(hBattery, dwTag.getValue(), PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatterySerialNumber.ordinal());
                                bqi.InformationLevel = PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatteryManufactureDate.ordinal();
                                bqi.write();
                                PowrProf.BATTERY_MANUFACTURE_DATE bmd = new PowrProf.BATTERY_MANUFACTURE_DATE();
                                if (Kernel32.INSTANCE.DeviceIoControl(hBattery, 2703428, bqi.getPointer(), bqi.size(), bmd.getPointer(), bmd.size(), dwOut, null)) {
                                    bmd.read();
                                    if (bmd.Year > 1900 && bmd.Month > 0 && bmd.Day > 0) {
                                        psManufactureDate = LocalDate.of((int)bmd.Year, bmd.Month, (int)bmd.Day);
                                    }
                                }
                                bqi.InformationLevel = PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatteryTemperature.ordinal();
                                bqi.write();
                                IntByReference tempK = new IntByReference();
                                if (Kernel32.INSTANCE.DeviceIoControl(hBattery, 2703428, bqi.getPointer(), bqi.size(), tempK.getPointer(), 4, dwOut, null)) {
                                    psTemperature = (double)tempK.getValue() / 10.0 - 273.15;
                                }
                                bqi.InformationLevel = PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatteryEstimatedTime.ordinal();
                                if (psPowerUsageRate != 0) {
                                    bqi.AtRate = psPowerUsageRate;
                                }
                                bqi.write();
                                IntByReference tr = new IntByReference();
                                if (Kernel32.INSTANCE.DeviceIoControl(hBattery, 2703428, bqi.getPointer(), bqi.size(), tr.getPointer(), 4, dwOut, null)) {
                                    psTimeRemainingInstant = tr.getValue();
                                }
                                if (psTimeRemainingInstant < 0.0 && psPowerUsageRate != 0 && (psTimeRemainingInstant = (double)(psMaxCapacity - psCurrentCapacity) * 3600.0 / (double)psPowerUsageRate) < 0.0) {
                                    psTimeRemainingInstant *= -1.0;
                                }
                                batteryFound = true;
                            }
                        }
                    }
                    Kernel32.INSTANCE.CloseHandle(hBattery);
                    continue;
                }
                if (259 == Kernel32.INSTANCE.GetLastError()) break;
            }
            SetupApi.INSTANCE.SetupDiDestroyDeviceInfoList(hdev);
        }
        return new WindowsPowerSource(psName, psDeviceName, psRemainingCapacityPercent, psTimeRemainingEstimated, psTimeRemainingInstant, psPowerUsageRate, psVoltage, psAmperage, psPowerOnLine, psCharging, psDischarging, psCapacityUnits, psCurrentCapacity, psMaxCapacity, psDesignCapacity, psCycleCount, psChemistry, psManufactureDate, psManufacturer, psSerialNumber, psTemperature);
    }

    private static String batteryQueryString(WinNT.HANDLE hBattery, int tag, int infoLevel) {
        Memory nameBuf;
        PowrProf.BATTERY_QUERY_INFORMATION bqi = new PowrProf.BATTERY_QUERY_INFORMATION();
        bqi.BatteryTag = tag;
        bqi.InformationLevel = infoLevel;
        bqi.write();
        IntByReference dwOut = new IntByReference();
        boolean ret = false;
        long bufSize = 0L;
        do {
            nameBuf = new Memory(bufSize += 256L);
        } while (!(ret = Kernel32.INSTANCE.DeviceIoControl(hBattery, 2703428, bqi.getPointer(), bqi.size(), (Pointer)nameBuf, (int)nameBuf.size(), dwOut, null)) && bufSize < 4096L);
        return CHAR_WIDTH > 1 ? nameBuf.getWideString(0L) : nameBuf.getString(0L);
    }
}

