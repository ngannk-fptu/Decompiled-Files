/*
 * Decompiled with CFR 0.152.
 */
package oshi.hardware.common;

import oshi.annotation.concurrent.Immutable;
import oshi.hardware.GraphicsCard;

@Immutable
public abstract class AbstractGraphicsCard
implements GraphicsCard {
    private final String name;
    private final String deviceId;
    private final String vendor;
    private final String versionInfo;
    private long vram;

    protected AbstractGraphicsCard(String name, String deviceId, String vendor, String versionInfo, long vram) {
        this.name = name;
        this.deviceId = deviceId;
        this.vendor = vendor;
        this.versionInfo = versionInfo;
        this.vram = vram;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDeviceId() {
        return this.deviceId;
    }

    @Override
    public String getVendor() {
        return this.vendor;
    }

    @Override
    public String getVersionInfo() {
        return this.versionInfo;
    }

    @Override
    public long getVRam() {
        return this.vram;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("GraphicsCard@");
        builder.append(Integer.toHexString(this.hashCode()));
        builder.append(" [name=");
        builder.append(this.name);
        builder.append(", deviceId=");
        builder.append(this.deviceId);
        builder.append(", vendor=");
        builder.append(this.vendor);
        builder.append(", vRam=");
        builder.append(this.vram);
        builder.append(", versionInfo=[");
        builder.append(this.versionInfo);
        builder.append("]]");
        return builder.toString();
    }
}

