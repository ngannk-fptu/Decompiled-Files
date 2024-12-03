/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.platform.win32.COM.WbemcliUtil$WmiQuery
 *  com.sun.jna.platform.win32.COM.WbemcliUtil$WmiResult
 */
package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32DiskDrive {
    private static final String WIN32_DISK_DRIVE = "Win32_DiskDrive";

    private Win32DiskDrive() {
    }

    public static WbemcliUtil.WmiResult<DiskDriveProperty> queryDiskDrive(WmiQueryHandler h) {
        WbemcliUtil.WmiQuery diskDriveQuery = new WbemcliUtil.WmiQuery(WIN32_DISK_DRIVE, DiskDriveProperty.class);
        return h.queryWMI(diskDriveQuery, false);
    }

    public static enum DiskDriveProperty {
        INDEX,
        MANUFACTURER,
        MODEL,
        NAME,
        SERIALNUMBER,
        SIZE;

    }
}

