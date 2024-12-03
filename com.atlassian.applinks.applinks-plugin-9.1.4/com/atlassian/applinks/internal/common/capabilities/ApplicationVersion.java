/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ComparisonChain
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.applinks.internal.common.capabilities;

import com.google.common.collect.ComparisonChain;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

public class ApplicationVersion
implements Comparable<ApplicationVersion> {
    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)(\\.\\d+)?(\\.\\d+)?(.*)");
    private final String versionString;
    private final int major;
    private final int minor;
    private final int bugfix;
    private final String suffix;

    private ApplicationVersion(String versionString, int major, int minor, int bugfix, String suffix) {
        this.versionString = versionString;
        this.major = major;
        this.minor = minor;
        this.bugfix = bugfix;
        this.suffix = suffix;
    }

    @Nonnull
    public static ApplicationVersion parse(@Nonnull String versionString) throws IllegalArgumentException {
        Objects.requireNonNull(versionString, "versionString");
        versionString = versionString.trim();
        Matcher matcher = VERSION_PATTERN.matcher(versionString);
        if (matcher.matches() && matcher.groupCount() == 4) {
            return new ApplicationVersion(versionString, ApplicationVersion.parseVersionInt(matcher, 1), ApplicationVersion.parseVersionInt(matcher, 2), ApplicationVersion.parseVersionInt(matcher, 3), matcher.group(matcher.groupCount()));
        }
        throw new IllegalArgumentException("Invalid version: " + versionString);
    }

    public int getMajor() {
        return this.major;
    }

    public int getMinor() {
        return this.minor;
    }

    public int getBugfix() {
        return this.bugfix;
    }

    @Nonnull
    public String getSuffix() {
        return this.suffix;
    }

    @Nonnull
    public String getVersionString() {
        return this.versionString;
    }

    public String toString() {
        return this.getVersionString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ApplicationVersion that = (ApplicationVersion)o;
        return Objects.equals(this.major, that.major) && Objects.equals(this.minor, that.minor) && Objects.equals(this.bugfix, that.bugfix) && Objects.equals(this.suffix, that.suffix);
    }

    public int hashCode() {
        return Objects.hash(this.major, this.minor, this.bugfix, this.suffix);
    }

    @Override
    public int compareTo(@Nonnull ApplicationVersion that) {
        return ComparisonChain.start().compare(this.major, that.major).compare(this.minor, that.minor).compare(this.bugfix, that.bugfix).compare((Comparable)((Object)this.suffix), (Comparable)((Object)that.suffix)).result();
    }

    private static int parseVersionInt(Matcher matcher, int group) {
        String value = matcher.group(group);
        if (value != null) {
            return Integer.parseInt(StringUtils.remove((String)value, (char)'.'));
        }
        return 0;
    }
}

