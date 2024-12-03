/*
 * Decompiled with CFR 0.152.
 */
package oshi.hardware.common;

import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Firmware;

@Immutable
public abstract class AbstractFirmware
implements Firmware {
    @Override
    public String getName() {
        return "unknown";
    }

    @Override
    public String getDescription() {
        return "unknown";
    }

    @Override
    public String getReleaseDate() {
        return "unknown";
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("manufacturer=").append(this.getManufacturer()).append(", ");
        sb.append("name=").append(this.getName()).append(", ");
        sb.append("description=").append(this.getDescription()).append(", ");
        sb.append("version=").append(this.getVersion()).append(", ");
        sb.append("release date=").append(this.getReleaseDate() == null ? "unknown" : this.getReleaseDate());
        return sb.toString();
    }
}

