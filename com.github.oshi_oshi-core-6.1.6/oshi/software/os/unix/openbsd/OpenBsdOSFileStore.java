/*
 * Decompiled with CFR 0.152.
 */
package oshi.software.os.unix.openbsd;

import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.common.AbstractOSFileStore;
import oshi.software.os.OSFileStore;
import oshi.software.os.unix.openbsd.OpenBsdFileSystem;

@ThreadSafe
public class OpenBsdOSFileStore
extends AbstractOSFileStore {
    private String logicalVolume;
    private String description;
    private String fsType;
    private long freeSpace;
    private long usableSpace;
    private long totalSpace;
    private long freeInodes;
    private long totalInodes;

    public OpenBsdOSFileStore(String name, String volume, String label, String mount, String options, String uuid, String logicalVolume, String description, String fsType, long freeSpace, long usableSpace, long totalSpace, long freeInodes, long totalInodes) {
        super(name, volume, label, mount, options, uuid);
        this.logicalVolume = logicalVolume;
        this.description = description;
        this.fsType = fsType;
        this.freeSpace = freeSpace;
        this.usableSpace = usableSpace;
        this.totalSpace = totalSpace;
        this.freeInodes = freeInodes;
        this.totalInodes = totalInodes;
    }

    @Override
    public String getLogicalVolume() {
        return this.logicalVolume;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getType() {
        return this.fsType;
    }

    @Override
    public long getFreeSpace() {
        return this.freeSpace;
    }

    @Override
    public long getUsableSpace() {
        return this.usableSpace;
    }

    @Override
    public long getTotalSpace() {
        return this.totalSpace;
    }

    @Override
    public long getFreeInodes() {
        return this.freeInodes;
    }

    @Override
    public long getTotalInodes() {
        return this.totalInodes;
    }

    @Override
    public boolean updateAttributes() {
        for (OSFileStore fileStore : OpenBsdFileSystem.getFileStoreMatching(this.getName())) {
            if (!this.getName().equals(fileStore.getName()) || !this.getVolume().equals(fileStore.getVolume()) || !this.getMount().equals(fileStore.getMount())) continue;
            this.logicalVolume = fileStore.getLogicalVolume();
            this.description = fileStore.getDescription();
            this.fsType = fileStore.getType();
            this.freeSpace = fileStore.getFreeSpace();
            this.usableSpace = fileStore.getUsableSpace();
            this.totalSpace = fileStore.getTotalSpace();
            this.freeInodes = fileStore.getFreeInodes();
            this.totalInodes = fileStore.getTotalInodes();
            return true;
        }
        return false;
    }
}

