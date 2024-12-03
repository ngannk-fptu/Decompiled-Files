/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.version;

import java.util.Comparator;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfluenceServerVersion
implements Comparable<ConfluenceServerVersion> {
    private static final String MAJOR_MINOR_REGEX = "(0|[1-9]\\d*)\\.(0|[1-9]\\d*)";
    private static final String OPTIONAL_PATCH_REGEX = "\\.?(0|[1-9]\\d*)?";
    private static final String OPTIONAL_BUILD_REGEX = "([-.][a-zA-Z\\d][-a-zA-Z.\\d]*)?(\\+[a-zA-Z\\d][-a-zA-Z.\\d]*)?";
    private static final Pattern PATTERN = Pattern.compile("^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.?(0|[1-9]\\d*)?([-.][a-zA-Z\\d][-a-zA-Z.\\d]*)?(\\+[a-zA-Z\\d][-a-zA-Z.\\d]*)?$");
    private final int major;
    private final int minor;
    private final int patch;

    private ConfluenceServerVersion(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public static ConfluenceServerVersion of(String version) {
        Matcher matcher = PATTERN.matcher(version);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Version passed is illegal.");
        }
        return new ConfluenceServerVersion(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0);
    }

    @Override
    public int compareTo(ConfluenceServerVersion o) {
        return Comparator.comparingInt(ver -> ver.major).thenComparingInt(ver -> ver.minor).thenComparingInt(ver -> ver.patch).compare(this, o);
    }

    public boolean greaterOrEqual(String version) {
        return this.compareTo(ConfluenceServerVersion.of(version)) >= 0;
    }

    public boolean lessThan(String version) {
        return this.compareTo(ConfluenceServerVersion.of(version)) < 0;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ConfluenceServerVersion other = (ConfluenceServerVersion)o;
        return this.major == other.major && this.minor == other.minor && this.patch == other.patch;
    }

    public int hashCode() {
        return Objects.hash(this.major, this.minor, this.patch);
    }
}

