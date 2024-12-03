/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Version
 */
package org.apache.felix.bundlerepository;

import org.osgi.framework.Version;

public class VersionRange {
    private Version m_low = null;
    private boolean m_isLowInclusive = false;
    private Version m_high = null;
    private boolean m_isHighInclusive = false;
    private String m_toString = null;
    public static final VersionRange infiniteRange = new VersionRange(Version.emptyVersion, true, null, true);

    public VersionRange(Version low, boolean isLowInclusive, Version high, boolean isHighInclusive) {
        this.m_low = low;
        this.m_isLowInclusive = isLowInclusive;
        this.m_high = high;
        this.m_isHighInclusive = isHighInclusive;
    }

    public Version getLow() {
        return this.m_low;
    }

    public boolean isLowInclusive() {
        return this.m_isLowInclusive;
    }

    public Version getHigh() {
        return this.m_high;
    }

    public boolean isHighInclusive() {
        return this.m_isHighInclusive;
    }

    public boolean isInRange(Version version) {
        if (this.m_high == null) {
            return version.compareTo((Object)this.m_low) >= 0;
        }
        if (this.isLowInclusive() && this.isHighInclusive()) {
            return version.compareTo((Object)this.m_low) >= 0 && version.compareTo((Object)this.m_high) <= 0;
        }
        if (this.isHighInclusive()) {
            return version.compareTo((Object)this.m_low) > 0 && version.compareTo((Object)this.m_high) <= 0;
        }
        if (this.isLowInclusive()) {
            return version.compareTo((Object)this.m_low) >= 0 && version.compareTo((Object)this.m_high) < 0;
        }
        return version.compareTo((Object)this.m_low) > 0 && version.compareTo((Object)this.m_high) < 0;
    }

    public static VersionRange parse(String range) {
        if (range.indexOf(44) >= 0) {
            String s = range.substring(1, range.length() - 1);
            String vlo = s.substring(0, s.indexOf(44)).trim();
            String vhi = s.substring(s.indexOf(44) + 1, s.length()).trim();
            return new VersionRange(new Version(vlo), range.charAt(0) == '[', new Version(vhi), range.charAt(range.length() - 1) == ']');
        }
        return new VersionRange(new Version(range), true, null, false);
    }

    public String toString() {
        if (this.m_toString == null) {
            if (this.m_high != null) {
                StringBuffer sb = new StringBuffer();
                sb.append(this.m_isLowInclusive ? (char)'[' : '(');
                sb.append(this.m_low.toString());
                sb.append(',');
                sb.append(this.m_high.toString());
                sb.append(this.m_isHighInclusive ? (char)']' : ')');
                this.m_toString = sb.toString();
            } else {
                this.m_toString = this.m_low.toString();
            }
        }
        return this.m_toString;
    }
}

