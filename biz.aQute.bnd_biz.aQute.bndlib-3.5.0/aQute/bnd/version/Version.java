/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version
implements Comparable<Version> {
    private static final String HIGHESTCHAR = "\uffff";
    final int major;
    final int minor;
    final int micro;
    final String qualifier;
    final boolean snapshot;
    public static final String VERSION_STRING = "(\\d{1,9})(\\.(\\d{1,9})(\\.(\\d{1,9})(\\.([-_\\da-zA-Z]+))?)?)?";
    public static final Pattern VERSION = Pattern.compile("(\\d{1,9})(\\.(\\d{1,9})(\\.(\\d{1,9})(\\.([-_\\da-zA-Z]+))?)?)?");
    public static final Version LOWEST = new Version();
    public static final Version HIGHEST = new Version(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, "\uffff");
    public static final Version emptyVersion = LOWEST;
    public static final Version ONE = new Version(1, 0, 0);
    public static final Pattern SNAPSHOT_P = Pattern.compile("(.*-)?SNAPSHOT$");

    public Version() {
        this(0);
    }

    public Version(int major, int minor, int micro, String qualifier) {
        this.major = major;
        this.minor = minor;
        this.micro = micro;
        this.qualifier = qualifier;
        this.snapshot = this.isSnapshot(qualifier);
    }

    public Version(int major, int minor, int micro) {
        this(major, minor, micro, null);
    }

    public Version(int major, int minor) {
        this(major, minor, 0, null);
    }

    public Version(int major) {
        this(major, 0, 0, null);
    }

    public Version(String version) {
        version = version.trim();
        Matcher m = VERSION.matcher(version);
        if (!m.matches()) {
            throw new IllegalArgumentException("Invalid syntax for version: " + version);
        }
        this.major = Integer.parseInt(m.group(1));
        this.minor = m.group(3) != null ? Integer.parseInt(m.group(3)) : 0;
        this.micro = m.group(5) != null ? Integer.parseInt(m.group(5)) : 0;
        this.qualifier = m.group(7);
        this.snapshot = this.isSnapshot(this.qualifier);
    }

    private boolean isSnapshot(String qualifier) {
        return qualifier != null && qualifier != HIGHESTCHAR && SNAPSHOT_P.matcher(qualifier).matches();
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

    public String getQualifier() {
        return this.qualifier;
    }

    @Override
    public int compareTo(Version other) {
        if (other == this) {
            return 0;
        }
        Version o = other;
        if (this.major != o.major) {
            return this.major - o.major;
        }
        if (this.minor != o.minor) {
            return this.minor - o.minor;
        }
        if (this.micro != o.micro) {
            return this.micro - o.micro;
        }
        int c = 0;
        if (this.qualifier != null) {
            c = 1;
        }
        if (o.qualifier != null) {
            c += 2;
        }
        switch (c) {
            case 0: {
                return 0;
            }
            case 1: {
                return 1;
            }
            case 2: {
                return -1;
            }
        }
        return this.qualifier.compareTo(o.qualifier);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.major);
        sb.append(".");
        sb.append(this.minor);
        sb.append(".");
        sb.append(this.micro);
        if (this.qualifier != null) {
            sb.append(".");
            sb.append(this.qualifier);
        }
        return sb.toString();
    }

    public boolean equals(Object ot) {
        if (!(ot instanceof Version)) {
            return false;
        }
        return this.compareTo((Version)ot) == 0;
    }

    public int hashCode() {
        return this.major * 97 ^ this.minor * 13 ^ this.micro + (this.qualifier == null ? 97 : this.qualifier.hashCode());
    }

    public int get(int i) {
        switch (i) {
            case 0: {
                return this.major;
            }
            case 1: {
                return this.minor;
            }
            case 2: {
                return this.micro;
            }
        }
        throw new IllegalArgumentException("Version can only get 0 (major), 1 (minor), or 2 (micro)");
    }

    public static Version parseVersion(String version) {
        if (version == null) {
            return LOWEST;
        }
        if ((version = version.trim()).length() == 0) {
            return LOWEST;
        }
        return new Version(version);
    }

    public Version getWithoutQualifier() {
        if (this.qualifier == null) {
            return this;
        }
        return new Version(this.major, this.minor, this.micro);
    }

    public static boolean isVersion(String version) {
        return version != null && VERSION.matcher(version).matches();
    }

    public boolean isSnapshot() {
        return this.snapshot;
    }
}

