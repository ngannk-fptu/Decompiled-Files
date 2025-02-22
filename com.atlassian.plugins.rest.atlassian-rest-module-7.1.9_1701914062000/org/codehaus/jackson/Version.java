/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Version
implements Comparable<Version> {
    private static final Version UNKNOWN_VERSION = new Version(0, 0, 0, null);
    protected final int _majorVersion;
    protected final int _minorVersion;
    protected final int _patchLevel;
    protected final String _snapshotInfo;

    public Version(int major, int minor, int patchLevel, String snapshotInfo) {
        this._majorVersion = major;
        this._minorVersion = minor;
        this._patchLevel = patchLevel;
        this._snapshotInfo = snapshotInfo;
    }

    public static Version unknownVersion() {
        return UNKNOWN_VERSION;
    }

    public boolean isUknownVersion() {
        return this == UNKNOWN_VERSION;
    }

    public boolean isSnapshot() {
        return this._snapshotInfo != null && this._snapshotInfo.length() > 0;
    }

    public int getMajorVersion() {
        return this._majorVersion;
    }

    public int getMinorVersion() {
        return this._minorVersion;
    }

    public int getPatchLevel() {
        return this._patchLevel;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this._majorVersion).append('.');
        sb.append(this._minorVersion).append('.');
        sb.append(this._patchLevel);
        if (this.isSnapshot()) {
            sb.append('-').append(this._snapshotInfo);
        }
        return sb.toString();
    }

    public int hashCode() {
        return this._majorVersion + this._minorVersion + this._patchLevel;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        Version other = (Version)o;
        return other._majorVersion == this._majorVersion && other._minorVersion == this._minorVersion && other._patchLevel == this._patchLevel;
    }

    @Override
    public int compareTo(Version other) {
        int diff = this._majorVersion - other._majorVersion;
        if (diff == 0 && (diff = this._minorVersion - other._minorVersion) == 0) {
            diff = this._patchLevel - other._patchLevel;
        }
        return diff;
    }
}

