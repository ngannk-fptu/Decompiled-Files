/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.ldap.odm.core.impl;

import org.springframework.util.Assert;

final class CaseIgnoreString
implements Comparable<CaseIgnoreString> {
    private final String string;
    private final int hashCode;

    public CaseIgnoreString(String string) {
        Assert.notNull((Object)string, (String)"string must not be null");
        this.string = string;
        this.hashCode = string.toUpperCase().hashCode();
    }

    public boolean equals(Object other) {
        return other instanceof CaseIgnoreString && ((CaseIgnoreString)other).string.equalsIgnoreCase(this.string);
    }

    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public int compareTo(CaseIgnoreString other) {
        CaseIgnoreString cis = other;
        return String.CASE_INSENSITIVE_ORDER.compare(this.string, cis.string);
    }

    public String toString() {
        return this.string;
    }
}

