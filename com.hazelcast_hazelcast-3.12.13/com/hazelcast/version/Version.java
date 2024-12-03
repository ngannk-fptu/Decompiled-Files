/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.version;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.StringUtil;
import java.io.IOException;

public final class Version
implements IdentifiedDataSerializable,
Comparable<Version> {
    public static final byte UNKNOWN_VERSION = 0;
    public static final Version UNKNOWN = new Version(0, 0);
    private byte major;
    private byte minor;

    public Version() {
    }

    private Version(int major, int minor) {
        assert (major >= 0 && major <= 127) : "Invalid value: " + major + ", must be in range [0,127]";
        assert (minor >= 0 && minor <= 127) : "Invalid value: " + minor + ", must be in range [0,127]";
        this.major = (byte)major;
        this.minor = (byte)minor;
    }

    public byte getMajor() {
        return this.major;
    }

    public byte getMinor() {
        return this.minor;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Version that = (Version)o;
        return this.isEqualTo(that);
    }

    public int hashCode() {
        int result = this.major;
        result = 31 * result + this.minor;
        return result;
    }

    public String toString() {
        return this.major + "." + this.minor;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeByte(this.major);
        out.writeByte(this.minor);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.major = in.readByte();
        this.minor = in.readByte();
    }

    @Override
    public int compareTo(Version o) {
        int thisVersion = this.pack();
        int thatVersion = o.pack();
        return thisVersion - thatVersion;
    }

    @Override
    public int getFactoryId() {
        return 0;
    }

    @Override
    public int getId() {
        return 35;
    }

    public static Version of(int major, int minor) {
        if (major == 0 && minor == 0) {
            return UNKNOWN;
        }
        return new Version(major, minor);
    }

    public static Version of(String version) {
        String[] tokens = StringUtil.tokenizeVersionString(version);
        if (tokens != null && tokens.length >= 2) {
            return Version.of(Byte.valueOf(tokens[0]).byteValue(), Byte.valueOf(tokens[1]).byteValue());
        }
        throw new IllegalArgumentException("Cannot parse " + version + " to ClusterVersion.");
    }

    public boolean isEqualTo(Version version) {
        return this.major == version.major && this.minor == version.minor;
    }

    public boolean isGreaterThan(Version version) {
        return !version.isUnknown() && this.compareTo(version) > 0;
    }

    public boolean isUnknownOrGreaterThan(Version version) {
        return this.isUnknown() || !version.isUnknown() && this.compareTo(version) > 0;
    }

    public boolean isGreaterOrEqual(Version version) {
        return !version.isUnknown() && this.compareTo(version) >= 0 || version.isUnknown() && this.isUnknown();
    }

    public boolean isUnknownOrGreaterOrEqual(Version version) {
        return this.isUnknown() || !version.isUnknown() && this.compareTo(version) >= 0;
    }

    public boolean isLessThan(Version version) {
        return !this.isUnknown() && this.compareTo(version) < 0;
    }

    public boolean isUnknownOrLessThan(Version version) {
        return this.isUnknown() || this.compareTo(version) < 0;
    }

    public boolean isLessOrEqual(Version version) {
        return !this.isUnknown() && this.compareTo(version) <= 0 || this.isUnknown() && version.isUnknown();
    }

    public boolean isUnknownOrLessOrEqual(Version version) {
        return this.isUnknown() || this.compareTo(version) <= 0;
    }

    public boolean isBetween(Version from, Version to) {
        int thisVersion = this.pack();
        int fromVersion = from.pack();
        int toVersion = to.pack();
        return thisVersion >= fromVersion && thisVersion <= toVersion;
    }

    public boolean isUnknown() {
        return this.pack() == 0;
    }

    private int pack() {
        return this.major << 8 & 0xFF00 | this.minor & 0xFF;
    }
}

