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
public final class Win32LogicalDiskToPartition {
    private static final String WIN32_LOGICAL_DISK_TO_PARTITION = "Win32_LogicalDiskToPartition";

    private Win32LogicalDiskToPartition() {
    }

    public static WbemcliUtil.WmiResult<DiskToPartitionProperty> queryDiskToPartition(WmiQueryHandler h) {
        WbemcliUtil.WmiQuery diskToPartitionQuery = new WbemcliUtil.WmiQuery(WIN32_LOGICAL_DISK_TO_PARTITION, DiskToPartitionProperty.class);
        return h.queryWMI(diskToPartitionQuery, false);
    }

    public static enum DiskToPartitionProperty {
        ANTECEDENT,
        DEPENDENT,
        ENDINGADDRESS,
        STARTINGADDRESS;

    }
}

