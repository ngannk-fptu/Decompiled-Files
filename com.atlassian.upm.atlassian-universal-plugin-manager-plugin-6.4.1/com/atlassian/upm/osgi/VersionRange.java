/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.osgi;

import com.atlassian.upm.impl.Interval;
import com.atlassian.upm.osgi.Version;
import com.atlassian.upm.osgi.impl.Versions;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class VersionRange
extends Interval<Version> {
    private static final Pattern rangePattern = Pattern.compile("([\\[\\(])([^,]++),([^\\]\\)]++)([\\]\\)])");

    public VersionRange(Interval.Floor<Version> floor, Interval.Ceiling<Version> ceiling) {
        super(Objects.requireNonNull(floor, "floor"), ceiling);
    }

    public static VersionRange fromString(String range) {
        Matcher rangeMatcher = rangePattern.matcher(range);
        return rangeMatcher.matches() ? new VersionRange(new Interval.Floor<Version>(Versions.fromString(rangeMatcher.group(2).trim()), Interval.Floor.getType(rangeMatcher.group(1).charAt(0))), new Interval.Ceiling<Version>(Versions.fromString(rangeMatcher.group(3).trim()), Interval.Ceiling.getType(rangeMatcher.group(4).charAt(0)))) : VersionRange.atLeast(Versions.fromString(range));
    }

    public static VersionRange atLeast(Version version) {
        return new VersionRange(new Interval.Floor<Version>(version, Interval.Bound.Type.INCLUSIVE), null);
    }

    public static VersionRange exactly(Version version) {
        return new VersionRange(new Interval.Floor<Version>(version, Interval.Bound.Type.INCLUSIVE), new Interval.Ceiling<Version>(version, Interval.Bound.Type.INCLUSIVE));
    }

    @Override
    public String toString() {
        Interval.Floor floor = this.getFloor();
        Interval.Ceiling ceiling = this.getCeiling();
        return ceiling == null ? ((Version)floor.getValue()).toString() : String.format("%s,%s", floor, ceiling);
    }
}

