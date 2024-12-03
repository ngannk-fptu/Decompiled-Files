/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Pointer
 *  com.sun.jna.PointerType
 *  com.sun.jna.platform.mac.CoreFoundation
 *  com.sun.jna.platform.mac.CoreFoundation$CFArrayRef
 *  com.sun.jna.platform.mac.CoreFoundation$CFBooleanRef
 *  com.sun.jna.platform.mac.CoreFoundation$CFDictionaryRef
 *  com.sun.jna.platform.mac.CoreFoundation$CFNumberRef
 *  com.sun.jna.platform.mac.CoreFoundation$CFStringRef
 *  com.sun.jna.platform.mac.CoreFoundation$CFTypeRef
 *  com.sun.jna.platform.mac.IOKit
 *  com.sun.jna.platform.mac.IOKit$IOService
 *  com.sun.jna.platform.mac.IOKitUtil
 */
package oshi.hardware.platform.mac;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.platform.mac.CoreFoundation;
import com.sun.jna.platform.mac.IOKit;
import com.sun.jna.platform.mac.IOKitUtil;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.PowerSource;
import oshi.hardware.common.AbstractPowerSource;
import oshi.util.platform.mac.CFUtil;

@ThreadSafe
public final class MacPowerSource
extends AbstractPowerSource {
    private static final CoreFoundation CF = CoreFoundation.INSTANCE;
    private static final IOKit IO = IOKit.INSTANCE;

    public MacPowerSource(String psName, String psDeviceName, double psRemainingCapacityPercent, double psTimeRemainingEstimated, double psTimeRemainingInstant, double psPowerUsageRate, double psVoltage, double psAmperage, boolean psPowerOnLine, boolean psCharging, boolean psDischarging, PowerSource.CapacityUnits psCapacityUnits, int psCurrentCapacity, int psMaxCapacity, int psDesignCapacity, int psCycleCount, String psChemistry, LocalDate psManufactureDate, String psManufacturer, String psSerialNumber, double psTemperature) {
        super(psName, psDeviceName, psRemainingCapacityPercent, psTimeRemainingEstimated, psTimeRemainingInstant, psPowerUsageRate, psVoltage, psAmperage, psPowerOnLine, psCharging, psDischarging, psCapacityUnits, psCurrentCapacity, psMaxCapacity, psDesignCapacity, psCycleCount, psChemistry, psManufactureDate, psManufacturer, psSerialNumber, psTemperature);
    }

    public static List<PowerSource> getPowerSources() {
        String psDeviceName = "unknown";
        double psTimeRemainingInstant = 0.0;
        double psPowerUsageRate = 0.0;
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
        IOKit.IOService smartBattery = IOKitUtil.getMatchingService((String)"AppleSmartBattery");
        if (smartBattery != null) {
            Integer temp;
            String s = smartBattery.getStringProperty("DeviceName");
            if (s != null) {
                psDeviceName = s;
            }
            if ((s = smartBattery.getStringProperty("Manufacturer")) != null) {
                psManufacturer = s;
            }
            if ((s = smartBattery.getStringProperty("BatterySerialNumber")) != null) {
                psSerialNumber = s;
            }
            if ((temp = smartBattery.getIntegerProperty("ManufactureDate")) != null) {
                int day = temp & 0x1F;
                int month = temp >> 5 & 0xF;
                int year80 = temp >> 9 & 0x7F;
                psManufactureDate = LocalDate.of(1980 + year80, month, day);
            }
            if ((temp = smartBattery.getIntegerProperty("DesignCapacity")) != null) {
                psDesignCapacity = temp;
            }
            if ((temp = smartBattery.getIntegerProperty("MaxCapacity")) != null) {
                psMaxCapacity = temp;
            }
            if ((temp = smartBattery.getIntegerProperty("CurrentCapacity")) != null) {
                psCurrentCapacity = temp;
            }
            psCapacityUnits = PowerSource.CapacityUnits.MAH;
            temp = smartBattery.getIntegerProperty("TimeRemaining");
            if (temp != null) {
                psTimeRemainingInstant = (double)temp.intValue() * 60.0;
            }
            if ((temp = smartBattery.getIntegerProperty("CycleCount")) != null) {
                psCycleCount = temp;
            }
            if ((temp = smartBattery.getIntegerProperty("Temperature")) != null) {
                psTemperature = (double)temp.intValue() / 100.0;
            }
            if ((temp = smartBattery.getIntegerProperty("Voltage")) != null) {
                psVoltage = (double)temp.intValue() / 1000.0;
            }
            if ((temp = smartBattery.getIntegerProperty("Amperage")) != null) {
                psAmperage = temp.intValue();
            }
            psPowerUsageRate = psVoltage * psAmperage;
            Boolean bool = smartBattery.getBooleanProperty("ExternalConnected");
            if (bool != null) {
                psPowerOnLine = bool;
            }
            if ((bool = smartBattery.getBooleanProperty("IsCharging")) != null) {
                psCharging = bool;
            }
            psDischarging = !psCharging;
            smartBattery.release();
        }
        CoreFoundation.CFTypeRef powerSourcesInfo = IO.IOPSCopyPowerSourcesInfo();
        CoreFoundation.CFArrayRef powerSourcesList = IO.IOPSCopyPowerSourcesList(powerSourcesInfo);
        int powerSourcesCount = powerSourcesList.getCount();
        double psTimeRemainingEstimated = IO.IOPSGetTimeRemainingEstimate();
        CoreFoundation.CFStringRef nameKey = CoreFoundation.CFStringRef.createCFString((String)"Name");
        CoreFoundation.CFStringRef isPresentKey = CoreFoundation.CFStringRef.createCFString((String)"Is Present");
        CoreFoundation.CFStringRef currentCapacityKey = CoreFoundation.CFStringRef.createCFString((String)"Current Capacity");
        CoreFoundation.CFStringRef maxCapacityKey = CoreFoundation.CFStringRef.createCFString((String)"Max Capacity");
        ArrayList<PowerSource> psList = new ArrayList<PowerSource>(powerSourcesCount);
        for (int ps = 0; ps < powerSourcesCount; ++ps) {
            CoreFoundation.CFBooleanRef isPresentRef;
            Pointer pwrSrcPtr = powerSourcesList.getValueAtIndex(ps);
            CoreFoundation.CFTypeRef powerSource = new CoreFoundation.CFTypeRef();
            powerSource.setPointer(pwrSrcPtr);
            CoreFoundation.CFDictionaryRef dictionary = IO.IOPSGetPowerSourceDescription(powerSourcesInfo, powerSource);
            Pointer result = dictionary.getValue((PointerType)isPresentKey);
            if (result == null || 0 == CF.CFBooleanGetValue(isPresentRef = new CoreFoundation.CFBooleanRef(result))) continue;
            result = dictionary.getValue((PointerType)nameKey);
            String psName = CFUtil.cfPointerToString(result);
            double currentCapacity = 0.0;
            if (dictionary.getValueIfPresent((PointerType)currentCapacityKey, null)) {
                result = dictionary.getValue((PointerType)currentCapacityKey);
                CoreFoundation.CFNumberRef cap = new CoreFoundation.CFNumberRef(result);
                currentCapacity = cap.intValue();
            }
            double maxCapacity = 1.0;
            if (dictionary.getValueIfPresent((PointerType)maxCapacityKey, null)) {
                result = dictionary.getValue((PointerType)maxCapacityKey);
                CoreFoundation.CFNumberRef cap = new CoreFoundation.CFNumberRef(result);
                maxCapacity = cap.intValue();
            }
            double psRemainingCapacityPercent = Math.min(1.0, currentCapacity / maxCapacity);
            psList.add(new MacPowerSource(psName, psDeviceName, psRemainingCapacityPercent, psTimeRemainingEstimated, psTimeRemainingInstant, psPowerUsageRate, psVoltage, psAmperage, psPowerOnLine, psCharging, psDischarging, psCapacityUnits, psCurrentCapacity, psMaxCapacity, psDesignCapacity, psCycleCount, psChemistry, psManufactureDate, psManufacturer, psSerialNumber, psTemperature));
        }
        isPresentKey.release();
        nameKey.release();
        currentCapacityKey.release();
        maxCapacityKey.release();
        powerSourcesList.release();
        powerSourcesInfo.release();
        return psList;
    }
}

