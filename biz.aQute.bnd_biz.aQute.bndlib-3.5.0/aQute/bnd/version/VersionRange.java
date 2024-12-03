/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.version;

import aQute.bnd.version.Version;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionRange {
    final Version high;
    final Version low;
    char start = (char)91;
    char end = (char)93;
    static Pattern RANGE = Pattern.compile("(\\(|\\[)\\s*((\\d{1,9})(\\.(\\d{1,9})(\\.(\\d{1,9})(\\.([-_\\da-zA-Z]+))?)?)?)\\s*,\\s*((\\d{1,9})(\\.(\\d{1,9})(\\.(\\d{1,9})(\\.([-_\\da-zA-Z]+))?)?)?)\\s*(\\)|\\])");

    public VersionRange(String string) {
        string = string.trim();
        int auto = 0;
        if (string.startsWith("@")) {
            string = string.substring(1);
            auto = 1;
        } else if (string.endsWith("@")) {
            string = string.substring(0, string.length() - 1);
            auto = 2;
        } else if (string.startsWith("=")) {
            string = string.substring(1);
            auto = 3;
        }
        Matcher m = RANGE.matcher(string);
        if (m.matches()) {
            this.start = m.group(1).charAt(0);
            String v1 = m.group(2);
            String v2 = m.group(10);
            this.low = new Version(v1);
            this.high = new Version(v2);
            this.end = m.group(18).charAt(0);
            if (this.low.compareTo(this.high) > 0) {
                throw new IllegalArgumentException("Low Range is higher than High Range: " + this.low + "-" + this.high);
            }
        } else {
            Version v = new Version(string);
            if (auto == 3) {
                this.start = (char)91;
                this.end = (char)93;
                this.low = v;
                this.high = v;
            } else if (auto != 0) {
                this.low = v;
                this.high = auto == 1 ? new Version(v.getMajor() + 1, 0, 0) : new Version(v.getMajor(), v.getMinor() + 1, 0);
                this.start = (char)91;
                this.end = (char)41;
            } else {
                this.low = this.high = v;
            }
        }
    }

    public VersionRange(boolean b, Version lower, Version upper, boolean c) {
        this.start = (char)(b ? 91 : 40);
        this.end = (char)(c ? 93 : 41);
        this.low = lower;
        this.high = VersionRange.unique(upper);
    }

    public VersionRange(String low, String higher) {
        this(new Version(low), new Version(higher));
    }

    public VersionRange(Version low, Version higher) {
        this.low = low;
        this.high = VersionRange.unique(higher);
        this.start = (char)91;
        this.end = (char)(this.low.equals(this.high) ? 93 : 41);
    }

    static Version unique(Version v) {
        if (Version.HIGHEST.equals(v)) {
            return Version.HIGHEST;
        }
        if (Version.LOWEST.equals(v)) {
            return Version.LOWEST;
        }
        return v;
    }

    public boolean isRange() {
        return this.high != this.low;
    }

    public boolean includeLow() {
        return this.start == '[';
    }

    public boolean includeHigh() {
        return this.end == ']';
    }

    public String toString() {
        if (this.high == Version.HIGHEST) {
            return this.low.toString();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(this.start);
        sb.append(this.low);
        sb.append(',');
        sb.append(this.high);
        sb.append(this.end);
        return sb.toString();
    }

    public Version getLow() {
        return this.low;
    }

    public Version getHigh() {
        return this.high;
    }

    public boolean includes(Version v) {
        if (!this.isRange()) {
            return this.low.compareTo(v) <= 0;
        }
        if (this.includeLow() ? v.compareTo(this.low) < 0 : v.compareTo(this.low) <= 0) {
            return false;
        }
        return !(this.includeHigh() ? v.compareTo(this.high) > 0 : v.compareTo(this.high) >= 0);
    }

    public Iterable<Version> filter(Iterable<Version> versions) {
        ArrayList<Version> list = new ArrayList<Version>();
        for (Version v : versions) {
            if (!this.includes(v)) continue;
            list.add(v);
        }
        return list;
    }

    public String toFilter() {
        return this.toFilter("version");
    }

    public String toFilter(String versionAttribute) {
        try (Formatter f = new Formatter();){
            if (this.high == Version.HIGHEST) {
                String string = "(" + versionAttribute + ">=" + this.low + ")";
                return string;
            }
            if (this.isRange()) {
                f.format("(&", new Object[0]);
                if (this.includeLow()) {
                    f.format("(%s>=%s)", versionAttribute, this.getLow());
                } else {
                    f.format("(!(%s<=%s))", versionAttribute, this.getLow());
                }
                if (this.includeHigh()) {
                    f.format("(%s<=%s)", versionAttribute, this.getHigh());
                } else {
                    f.format("(!(%s>=%s))", versionAttribute, this.getHigh());
                }
                f.format(")", new Object[0]);
            } else {
                f.format("(%s>=%s)", versionAttribute, this.getLow());
            }
            String string = f.toString();
            return string;
        }
    }

    public static boolean isVersionRange(String stringRange) {
        return RANGE.matcher(stringRange).matches();
    }

    public VersionRange intersect(VersionRange other) {
        Version upper;
        Version lower;
        char start = this.start;
        int lowc = this.low.compareTo(other.low);
        if (lowc <= 0) {
            lower = other.low;
            if (lowc != 0 || start == '[') {
                start = other.start;
            }
        } else {
            lower = this.low;
        }
        char end = this.end;
        int highc = this.high.compareTo(other.high);
        if (highc >= 0) {
            upper = other.high;
            if (highc != 0 || end == ']') {
                end = other.end;
            }
        } else {
            upper = this.high;
        }
        return new VersionRange(start == '[', lower, upper, end == ']');
    }

    public static VersionRange parseVersionRange(String version) {
        if (!VersionRange.isVersionRange(version)) {
            return null;
        }
        return new VersionRange(version);
    }

    public static VersionRange parseOSGiVersionRange(String version) {
        if (Version.isVersion(version)) {
            return new VersionRange(new Version(version), Version.HIGHEST);
        }
        if (VersionRange.isVersionRange(version)) {
            return new VersionRange(version);
        }
        return null;
    }

    public static boolean isOSGiVersionRange(String range) {
        return Version.isVersion(range) || VersionRange.isVersionRange(range);
    }

    public boolean isSingleVersion() {
        return this.high == Version.HIGHEST;
    }
}

