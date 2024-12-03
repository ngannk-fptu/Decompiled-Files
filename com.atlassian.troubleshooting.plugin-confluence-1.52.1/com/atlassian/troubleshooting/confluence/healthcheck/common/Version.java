/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.confluence.healthcheck.common;

public class Version {
    public static final Version INVALID = new Version(-1, -1, -1);
    private final int major;
    private final int minor;
    private final int patch;

    private Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public Version(String version) {
        int tempMajor = Version.INVALID.major;
        int tempMinor = Version.INVALID.minor;
        int tempPatch = Version.INVALID.patch;
        try {
            int idxMinorEnd;
            version = version.trim();
            int idxFirstDot = version.indexOf(46);
            for (idxMinorEnd = idxFirstDot + 1; idxMinorEnd < version.length() && Character.isDigit(version.charAt(idxMinorEnd)); ++idxMinorEnd) {
            }
            int idxPatchEnd = -1;
            if (idxMinorEnd < version.length() && version.charAt(idxMinorEnd) == '.') {
                for (idxPatchEnd = idxMinorEnd + 1; idxPatchEnd < version.length() && Character.isDigit(version.charAt(idxPatchEnd)); ++idxPatchEnd) {
                }
            }
            tempMajor = Integer.valueOf(version.substring(0, idxFirstDot));
            tempMinor = Integer.valueOf(version.substring(idxFirstDot + 1, idxMinorEnd));
            tempPatch = idxPatchEnd > 0 ? Integer.valueOf(version.substring(idxMinorEnd + 1, idxPatchEnd)) : 0;
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.major = tempMajor;
        this.minor = tempMinor;
        this.patch = tempPatch;
    }

    public int getMajor() {
        return this.major;
    }

    public int getMinor() {
        return this.minor;
    }

    public int getPatch() {
        return this.patch;
    }

    public boolean isValid() {
        return this.major >= 0 && this.minor >= 0 && this.patch >= 0;
    }

    public String getMajorAndMinor() {
        return this.major + "." + this.minor;
    }

    public String getFullVersion() {
        return this.major + "." + this.minor + "." + this.patch;
    }

    public boolean isLowerThan(Version version) {
        if (version == null) {
            return false;
        }
        return this.major < version.getMajor() || this.major == version.getMajor() && this.minor < version.getMinor() || this.major == version.getMajor() && this.minor == version.getMinor() && this.patch < version.getPatch();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Version version = (Version)o;
        return this.major == version.major && this.minor == version.minor && this.patch == version.patch;
    }

    public int hashCode() {
        int result = this.major;
        result = 31 * result + this.minor;
        result = 31 * result + this.patch;
        return result;
    }
}

