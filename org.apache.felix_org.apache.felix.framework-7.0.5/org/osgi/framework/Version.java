/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.framework;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class Version
implements Comparable<Version> {
    private final int major;
    private final int minor;
    private final int micro;
    private final String qualifier;
    private static final String SEPARATOR = ".";
    private transient String versionString;
    private transient int hash;
    public static final Version emptyVersion = new Version(0, 0, 0);

    public Version(int major, int minor, int micro) {
        this(major, minor, micro, null);
    }

    public Version(int major, int minor, int micro, String qualifier) {
        if (qualifier == null) {
            qualifier = "";
        }
        this.major = major;
        this.minor = minor;
        this.micro = micro;
        this.qualifier = qualifier;
        this.validate();
    }

    public Version(String version) {
        int maj = 0;
        int min = 0;
        int mic = 0;
        String qual = "";
        try {
            StringTokenizer st = new StringTokenizer(version, SEPARATOR, true);
            maj = Version.parseInt(st.nextToken(), version);
            if (st.hasMoreTokens()) {
                st.nextToken();
                min = Version.parseInt(st.nextToken(), version);
                if (st.hasMoreTokens()) {
                    st.nextToken();
                    mic = Version.parseInt(st.nextToken(), version);
                    if (st.hasMoreTokens()) {
                        st.nextToken();
                        qual = st.nextToken("");
                        if (st.hasMoreTokens()) {
                            throw new IllegalArgumentException("invalid version \"" + version + "\": invalid format");
                        }
                    }
                }
            }
        }
        catch (NoSuchElementException e) {
            throw new IllegalArgumentException("invalid version \"" + version + "\": invalid format", e);
        }
        this.major = maj;
        this.minor = min;
        this.micro = mic;
        this.qualifier = qual;
        this.validate();
    }

    private static int parseInt(String value, String version) {
        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("invalid version \"" + version + "\": non-numeric \"" + value + "\"", e);
        }
    }

    private void validate() {
        if (this.major < 0) {
            throw new IllegalArgumentException("invalid version \"" + this.toString0() + "\": negative number \"" + this.major + "\"");
        }
        if (this.minor < 0) {
            throw new IllegalArgumentException("invalid version \"" + this.toString0() + "\": negative number \"" + this.minor + "\"");
        }
        if (this.micro < 0) {
            throw new IllegalArgumentException("invalid version \"" + this.toString0() + "\": negative number \"" + this.micro + "\"");
        }
        for (char ch : this.qualifier.toCharArray()) {
            if ('A' <= ch && ch <= 'Z' || 'a' <= ch && ch <= 'z' || '0' <= ch && ch <= '9' || ch == '_' || ch == '-') continue;
            throw new IllegalArgumentException("invalid version \"" + this.toString0() + "\": invalid qualifier \"" + this.qualifier + "\"");
        }
    }

    public static Version parseVersion(String version) {
        if (version == null) {
            return emptyVersion;
        }
        return Version.valueOf(version);
    }

    public static Version valueOf(String version) {
        if ((version = version.trim()).length() == 0) {
            return emptyVersion;
        }
        return new Version(version);
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

    public String toString() {
        return this.toString0();
    }

    String toString0() {
        String s = this.versionString;
        if (s != null) {
            return s;
        }
        int q = this.qualifier.length();
        StringBuilder result = new StringBuilder(20 + q);
        result.append(this.major);
        result.append(SEPARATOR);
        result.append(this.minor);
        result.append(SEPARATOR);
        result.append(this.micro);
        if (q > 0) {
            result.append(SEPARATOR);
            result.append(this.qualifier);
        }
        this.versionString = result.toString();
        return this.versionString;
    }

    public int hashCode() {
        int h = this.hash;
        if (h != 0) {
            return h;
        }
        h = 527;
        h = 31 * h + this.major;
        h = 31 * h + this.minor;
        h = 31 * h + this.micro;
        this.hash = h = 31 * h + this.qualifier.hashCode();
        return this.hash;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof Version)) {
            return false;
        }
        Version other = (Version)object;
        return this.major == other.major && this.minor == other.minor && this.micro == other.micro && this.qualifier.equals(other.qualifier);
    }

    @Override
    public int compareTo(Version other) {
        if (other == this) {
            return 0;
        }
        int result = this.major - other.major;
        if (result != 0) {
            return result;
        }
        result = this.minor - other.minor;
        if (result != 0) {
            return result;
        }
        result = this.micro - other.micro;
        if (result != 0) {
            return result;
        }
        return this.qualifier.compareTo(other.qualifier);
    }
}

