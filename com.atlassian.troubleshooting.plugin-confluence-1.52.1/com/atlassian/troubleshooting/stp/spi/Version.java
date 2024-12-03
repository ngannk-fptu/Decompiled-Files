/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.math.NumberUtils
 */
package com.atlassian.troubleshooting.stp.spi;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class Version
implements Comparable<Version> {
    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+(\\.\\d+)*).*");
    private final int major;
    private final int minor;
    private final int micro;

    public Version(int major, int minor, int micro) {
        this.major = major;
        this.minor = minor;
        this.micro = micro;
    }

    public static Version of(@Nonnull String version) {
        Matcher matcher = VERSION_PATTERN.matcher(version);
        if (matcher.matches()) {
            String[] parts = StringUtils.split((String)matcher.group(1), (String)".");
            int major = parts.length > 0 ? NumberUtils.toInt((String)parts[0]) : 0;
            int minor = parts.length > 1 ? NumberUtils.toInt((String)parts[1]) : 0;
            int micro = parts.length > 2 ? NumberUtils.toInt((String)parts[2]) : 0;
            return new Version(major, minor, micro);
        }
        throw new IllegalArgumentException("Illegal version " + version);
    }

    public static Optional<Version> strictOf(@Nonnull String version) {
        String[] parts = StringUtils.split((String)version, (String)".");
        Optional<Integer> major = Version.partToInteger(parts, 0);
        Optional<Integer> minor = Version.partToInteger(parts, 1);
        Optional<Integer> micro = Version.partToInteger(parts, 2);
        if (parts.length > 0 && major.isPresent() && minor.isPresent() && micro.isPresent()) {
            return Optional.of(new Version(major.get(), minor.get(), micro.get()));
        }
        return Optional.empty();
    }

    public static Version of(int major, int minor, int bugfix) {
        return new Version(major, minor, bugfix);
    }

    public static Version of(String major, String minor, String bugfix) {
        return new Version(Version.toInteger(major).orElse(0), Version.toInteger(minor).orElse(0), Version.toInteger(bugfix).orElse(0));
    }

    private static Optional<Integer> partToInteger(String[] parts, int i) {
        if (parts.length > i) {
            try {
                return Optional.of(Integer.parseInt(parts[i]));
            }
            catch (NumberFormatException e) {
                return Optional.empty();
            }
        }
        return Optional.of(0);
    }

    private static Optional<Integer> toInteger(String part) {
        try {
            return Optional.of(Integer.parseInt(part));
        }
        catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public int getMajor() {
        return this.major;
    }

    public int getMinor() {
        return this.minor;
    }

    public int getMicro() {
        return this.micro;
    }

    public String getMajorAndMinor() {
        return this.major + "." + this.minor;
    }

    public String getAnalyticsString() {
        return String.format("%03d.%03d.%03d", this.major, this.minor, this.micro);
    }

    @Override
    public int compareTo(@Nonnull Version other) {
        if (this.major != other.major) {
            return this.major - other.major;
        }
        if (this.minor != other.minor) {
            return this.minor - other.minor;
        }
        return this.micro - other.micro;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Version other = (Version)o;
        return this.major == other.major && this.minor == other.minor && this.micro == other.micro;
    }

    public int hashCode() {
        int result = this.major;
        result = 31 * result + this.minor;
        result = 31 * result + this.micro;
        return result;
    }

    public String toString() {
        return this.major + "." + this.minor + "." + this.micro;
    }

    public boolean isFirstMinorVersion() {
        return this.micro == 0;
    }
}

