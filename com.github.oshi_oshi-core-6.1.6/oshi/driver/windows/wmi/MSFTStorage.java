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
public final class MSFTStorage {
    private static final String STORAGE_NAMESPACE = "ROOT\\Microsoft\\Windows\\Storage";
    private static final String MSFT_STORAGE_POOL_WHERE_IS_PRIMORDIAL_FALSE = "MSFT_StoragePool WHERE IsPrimordial=FALSE";
    private static final String MSFT_STORAGE_POOL_TO_PHYSICAL_DISK = "MSFT_StoragePoolToPhysicalDisk";
    private static final String MSFT_PHYSICAL_DISK = "MSFT_PhysicalDisk";
    private static final String MSFT_VIRTUAL_DISK = "MSFT_VirtualDisk";

    private MSFTStorage() {
    }

    public static WbemcliUtil.WmiResult<StoragePoolProperty> queryStoragePools(WmiQueryHandler h) {
        WbemcliUtil.WmiQuery storagePoolQuery = new WbemcliUtil.WmiQuery(STORAGE_NAMESPACE, MSFT_STORAGE_POOL_WHERE_IS_PRIMORDIAL_FALSE, StoragePoolProperty.class);
        return h.queryWMI(storagePoolQuery, false);
    }

    public static WbemcliUtil.WmiResult<StoragePoolToPhysicalDiskProperty> queryStoragePoolPhysicalDisks(WmiQueryHandler h) {
        WbemcliUtil.WmiQuery storagePoolToPhysicalDiskQuery = new WbemcliUtil.WmiQuery(STORAGE_NAMESPACE, MSFT_STORAGE_POOL_TO_PHYSICAL_DISK, StoragePoolToPhysicalDiskProperty.class);
        return h.queryWMI(storagePoolToPhysicalDiskQuery, false);
    }

    public static WbemcliUtil.WmiResult<PhysicalDiskProperty> queryPhysicalDisks(WmiQueryHandler h) {
        WbemcliUtil.WmiQuery physicalDiskQuery = new WbemcliUtil.WmiQuery(STORAGE_NAMESPACE, MSFT_PHYSICAL_DISK, PhysicalDiskProperty.class);
        return h.queryWMI(physicalDiskQuery, false);
    }

    public static WbemcliUtil.WmiResult<VirtualDiskProperty> queryVirtualDisks(WmiQueryHandler h) {
        WbemcliUtil.WmiQuery virtualDiskQuery = new WbemcliUtil.WmiQuery(STORAGE_NAMESPACE, MSFT_VIRTUAL_DISK, VirtualDiskProperty.class);
        return h.queryWMI(virtualDiskQuery, false);
    }

    public static enum StoragePoolProperty {
        FRIENDLYNAME,
        OBJECTID;

    }

    public static enum StoragePoolToPhysicalDiskProperty {
        STORAGEPOOL,
        PHYSICALDISK;

    }

    public static enum PhysicalDiskProperty {
        FRIENDLYNAME,
        PHYSICALLOCATION,
        OBJECTID;

    }

    public static enum VirtualDiskProperty {
        FRIENDLYNAME,
        OBJECTID;

    }
}

