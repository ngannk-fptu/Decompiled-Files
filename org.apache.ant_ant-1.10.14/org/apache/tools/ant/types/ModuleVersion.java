/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import java.util.Objects;

public class ModuleVersion {
    private String number;
    private String preRelease;
    private String build;

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        Objects.requireNonNull(number, "Version number cannot be null.");
        if (number.indexOf(45) >= 0 || number.indexOf(43) >= 0) {
            throw new IllegalArgumentException("Version number cannot contain '-' or '+'.");
        }
        this.number = number;
    }

    public String getPreRelease() {
        return this.preRelease;
    }

    public void setPreRelease(String pre) {
        if (pre != null && pre.indexOf(43) >= 0) {
            throw new IllegalArgumentException("Version's pre-release cannot contain '+'.");
        }
        this.preRelease = pre;
    }

    public String getBuild() {
        return this.build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String toModuleVersionString() {
        if (this.number == null) {
            throw new IllegalStateException("Version number cannot be null.");
        }
        StringBuilder version = new StringBuilder(this.number);
        if (this.preRelease != null || this.build != null) {
            version.append('-').append(Objects.toString(this.preRelease, ""));
        }
        if (this.build != null) {
            version.append('+').append(this.build);
        }
        return version.toString();
    }

    public String toString() {
        return this.getClass().getName() + "[number=" + this.number + ", preRelease=" + this.preRelease + ", build=" + this.build + "]";
    }
}

