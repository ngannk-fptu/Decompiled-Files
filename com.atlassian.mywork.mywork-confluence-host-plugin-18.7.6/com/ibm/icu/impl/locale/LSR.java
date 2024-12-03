/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl.locale;

import java.util.Objects;

public final class LSR {
    public static final int REGION_INDEX_LIMIT = 1677;
    public static final int EXPLICIT_LSR = 7;
    public static final int EXPLICIT_LANGUAGE = 4;
    public static final int EXPLICIT_SCRIPT = 2;
    public static final int EXPLICIT_REGION = 1;
    public static final int IMPLICIT_LSR = 0;
    public static final int DONT_CARE_FLAGS = 0;
    public static final boolean DEBUG_OUTPUT = false;
    public final String language;
    public final String script;
    public final String region;
    final int regionIndex;
    public final int flags;

    public LSR(String language, String script, String region, int flags) {
        this.language = language;
        this.script = script;
        this.region = region;
        this.regionIndex = LSR.indexForRegion(region);
        this.flags = flags;
    }

    public static final int indexForRegion(String region) {
        if (region.length() == 2) {
            int a = region.charAt(0) - 65;
            if (a < 0 || 25 < a) {
                return 0;
            }
            int b = region.charAt(1) - 65;
            if (b < 0 || 25 < b) {
                return 0;
            }
            return 26 * a + b + 1001;
        }
        if (region.length() == 3) {
            int a = region.charAt(0) - 48;
            if (a < 0 || 9 < a) {
                return 0;
            }
            int b = region.charAt(1) - 48;
            if (b < 0 || 9 < b) {
                return 0;
            }
            int c = region.charAt(2) - 48;
            if (c < 0 || 9 < c) {
                return 0;
            }
            return (10 * a + b) * 10 + c + 1;
        }
        return 0;
    }

    public String toString() {
        StringBuilder result = new StringBuilder(this.language);
        if (!this.script.isEmpty()) {
            result.append('-').append(this.script);
        }
        if (!this.region.isEmpty()) {
            result.append('-').append(this.region);
        }
        return result.toString();
    }

    public boolean isEquivalentTo(LSR other) {
        return this.language.equals(other.language) && this.script.equals(other.script) && this.region.equals(other.region);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;
        LSR other = (LSR)obj;
        if (!this.language.equals(other.language)) return false;
        if (!this.script.equals(other.script)) return false;
        if (!this.region.equals(other.region)) return false;
        if (this.flags != other.flags) return false;
        return true;
    }

    public int hashCode() {
        return Objects.hash(this.language, this.script, this.region, this.flags);
    }
}

