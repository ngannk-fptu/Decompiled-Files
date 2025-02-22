/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.version;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.StringUtil;
import com.hazelcast.version.MajorMinorVersionComparator;
import com.hazelcast.version.Version;
import java.io.IOException;
import java.io.Serializable;
import java.util.Comparator;

public final class MemberVersion
implements IdentifiedDataSerializable,
Serializable,
Comparable<MemberVersion> {
    public static final MemberVersion UNKNOWN = new MemberVersion(0, 0, 0);
    public static final transient Comparator<MemberVersion> MAJOR_MINOR_VERSION_COMPARATOR = new MajorMinorVersionComparator();
    private static final String UNKNOWN_VERSION_STRING = "0.0.0";
    private static final long serialVersionUID = 2603770920931610781L;
    private byte major;
    private byte minor;
    private byte patch;

    public MemberVersion() {
    }

    public MemberVersion(int major, int minor, int patch) {
        this.major = (byte)major;
        this.minor = (byte)minor;
        this.patch = (byte)patch;
    }

    public MemberVersion(String version) {
        this.parse(version);
    }

    private void parse(String version) {
        String[] tokens = StringUtil.tokenizeVersionString(version);
        this.major = Byte.valueOf(tokens[0]);
        this.minor = Byte.valueOf(tokens[1]);
        if (tokens.length > 3 && tokens[3] != null) {
            this.patch = Byte.valueOf(tokens[3]);
        }
    }

    public byte getMajor() {
        return this.major;
    }

    public byte getMinor() {
        return this.minor;
    }

    public byte getPatch() {
        return this.patch;
    }

    public boolean isUnknown() {
        return this.major == 0 && this.minor == 0 && this.patch == 0;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MemberVersion that = (MemberVersion)o;
        if (this.major != that.major) {
            return false;
        }
        if (this.minor != that.minor) {
            return false;
        }
        return this.patch == that.patch;
    }

    public int hashCode() {
        int result = this.major;
        result = 31 * result + this.minor;
        result = 31 * result + this.patch;
        return result;
    }

    public String toString() {
        return this.major + "." + this.minor + "." + this.patch;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeByte(this.major);
        out.writeByte(this.minor);
        out.writeByte(this.patch);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.major = in.readByte();
        this.minor = in.readByte();
        this.patch = in.readByte();
    }

    public static MemberVersion of(int major, int minor, int patch) {
        if (major == 0 && minor == 0 && patch == 0) {
            return UNKNOWN;
        }
        return new MemberVersion(major, minor, patch);
    }

    public static MemberVersion of(String version) {
        if (version == null || version.startsWith(UNKNOWN_VERSION_STRING)) {
            return UNKNOWN;
        }
        return new MemberVersion(version);
    }

    @Override
    public int getFactoryId() {
        return 0;
    }

    @Override
    public int getId() {
        return 32;
    }

    @Override
    public int compareTo(MemberVersion otherVersion) {
        int thisVersion = this.major << 16 & 0xFF0000 | this.minor << 8 & 0xFF00 | this.patch & 0xFF;
        int thatVersion = otherVersion.major << 16 & 0xFF0000 | otherVersion.minor << 8 & 0xFF00 | otherVersion.patch & 0xFF;
        if (thisVersion > thatVersion) {
            return 1;
        }
        return thisVersion == thatVersion ? 0 : -1;
    }

    public Version asVersion() {
        return Version.of(this.major, this.minor);
    }
}

