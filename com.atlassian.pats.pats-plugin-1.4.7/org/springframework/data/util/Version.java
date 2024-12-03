/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.util;

import java.util.ArrayList;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class Version
implements Comparable<Version> {
    private static final String VERSION_PARSE_ERROR = "Invalid version string! Could not parse segment %s within %s.";
    private final int major;
    private final int minor;
    private final int bugfix;
    private final int build;

    public Version(int ... parts) {
        Assert.notNull((Object)parts, (String)"Parts must not be null!");
        Assert.isTrue((parts.length > 0 && parts.length < 5 ? 1 : 0) != 0, (String)String.format("Invalid parts length. 0 < %s < 5", parts.length));
        this.major = parts[0];
        this.minor = parts.length > 1 ? parts[1] : 0;
        this.bugfix = parts.length > 2 ? parts[2] : 0;
        this.build = parts.length > 3 ? parts[3] : 0;
        Assert.isTrue((this.major >= 0 ? 1 : 0) != 0, (String)"Major version must be greater or equal zero!");
        Assert.isTrue((this.minor >= 0 ? 1 : 0) != 0, (String)"Minor version must be greater or equal zero!");
        Assert.isTrue((this.bugfix >= 0 ? 1 : 0) != 0, (String)"Bugfix version must be greater or equal zero!");
        Assert.isTrue((this.build >= 0 ? 1 : 0) != 0, (String)"Build version must be greater or equal zero!");
    }

    public static Version parse(String version) {
        Assert.hasText((String)version, (String)"Version must not be null o empty!");
        String[] parts = version.trim().split("\\.");
        int[] intParts = new int[parts.length];
        for (int i = 0; i < parts.length; ++i) {
            String input;
            String string = input = i == parts.length - 1 ? parts[i].replaceAll("\\D.*", "") : parts[i];
            if (!StringUtils.hasText((String)input)) continue;
            try {
                intParts[i] = Integer.parseInt(input);
                continue;
            }
            catch (IllegalArgumentException o_O) {
                throw new IllegalArgumentException(String.format(VERSION_PARSE_ERROR, input, version), o_O);
            }
        }
        return new Version(intParts);
    }

    public static Version javaVersion() {
        return Version.parse(System.getProperty("java.version"));
    }

    public boolean isGreaterThan(Version version) {
        return this.compareTo(version) > 0;
    }

    public boolean isGreaterThanOrEqualTo(Version version) {
        return this.compareTo(version) >= 0;
    }

    public boolean is(Version version) {
        return this.equals(version);
    }

    public boolean isLessThan(Version version) {
        return this.compareTo(version) < 0;
    }

    public boolean isLessThanOrEqualTo(Version version) {
        return this.compareTo(version) <= 0;
    }

    @Override
    public int compareTo(Version that) {
        if (this.major != that.major) {
            return this.major - that.major;
        }
        if (this.minor != that.minor) {
            return this.minor - that.minor;
        }
        if (this.bugfix != that.bugfix) {
            return this.bugfix - that.bugfix;
        }
        if (this.build != that.build) {
            return this.build - that.build;
        }
        return 0;
    }

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Version)) {
            return false;
        }
        Version that = (Version)obj;
        return this.major == that.major && this.minor == that.minor && this.bugfix == that.bugfix && this.build == that.build;
    }

    public int hashCode() {
        int result = 17;
        result += 31 * this.major;
        result += 31 * this.minor;
        result += 31 * this.bugfix;
        return result += 31 * this.build;
    }

    public String toString() {
        ArrayList<Integer> digits = new ArrayList<Integer>();
        digits.add(this.major);
        digits.add(this.minor);
        if (this.build != 0 || this.bugfix != 0) {
            digits.add(this.bugfix);
        }
        if (this.build != 0) {
            digits.add(this.build);
        }
        return StringUtils.collectionToDelimitedString(digits, (String)".");
    }
}

