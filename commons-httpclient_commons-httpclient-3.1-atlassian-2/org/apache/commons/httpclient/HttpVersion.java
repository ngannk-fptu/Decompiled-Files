/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient;

import org.apache.commons.httpclient.ProtocolException;

public class HttpVersion
implements Comparable {
    private int major = 0;
    private int minor = 0;
    public static final HttpVersion HTTP_0_9 = new HttpVersion(0, 9);
    public static final HttpVersion HTTP_1_0 = new HttpVersion(1, 0);
    public static final HttpVersion HTTP_1_1 = new HttpVersion(1, 1);

    public HttpVersion(int major, int minor) {
        if (major < 0) {
            throw new IllegalArgumentException("HTTP major version number may not be negative");
        }
        this.major = major;
        if (minor < 0) {
            throw new IllegalArgumentException("HTTP minor version number may not be negative");
        }
        this.minor = minor;
    }

    public int getMajor() {
        return this.major;
    }

    public int getMinor() {
        return this.minor;
    }

    public int hashCode() {
        return this.major * 100000 + this.minor;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof HttpVersion)) {
            return false;
        }
        return this.equals((HttpVersion)obj);
    }

    public int compareTo(HttpVersion anotherVer) {
        if (anotherVer == null) {
            throw new IllegalArgumentException("Version parameter may not be null");
        }
        int delta = this.getMajor() - anotherVer.getMajor();
        if (delta == 0) {
            delta = this.getMinor() - anotherVer.getMinor();
        }
        return delta;
    }

    public int compareTo(Object o) {
        return this.compareTo((HttpVersion)o);
    }

    public boolean equals(HttpVersion version) {
        return this.compareTo(version) == 0;
    }

    public boolean greaterEquals(HttpVersion version) {
        return this.compareTo(version) >= 0;
    }

    public boolean lessEquals(HttpVersion version) {
        return this.compareTo(version) <= 0;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("HTTP/");
        buffer.append(this.major);
        buffer.append('.');
        buffer.append(this.minor);
        return buffer.toString();
    }

    public static HttpVersion parse(String s) throws ProtocolException {
        int minor;
        int major;
        if (s == null) {
            throw new IllegalArgumentException("String may not be null");
        }
        if (!s.startsWith("HTTP/")) {
            throw new ProtocolException("Invalid HTTP version string: " + s);
        }
        int i1 = "HTTP/".length();
        int i2 = s.indexOf(".", i1);
        if (i2 == -1) {
            throw new ProtocolException("Invalid HTTP version number: " + s);
        }
        try {
            major = Integer.parseInt(s.substring(i1, i2));
        }
        catch (NumberFormatException e) {
            throw new ProtocolException("Invalid HTTP major version number: " + s);
        }
        i1 = i2 + 1;
        i2 = s.length();
        try {
            minor = Integer.parseInt(s.substring(i1, i2));
        }
        catch (NumberFormatException e) {
            throw new ProtocolException("Invalid HTTP minor version number: " + s);
        }
        return new HttpVersion(major, minor);
    }
}

