/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.util;

import com.ibm.icu.lang.UCharacter;

public class CaseInsensitiveString {
    private String string;
    private int hash = 0;
    private String folded = null;

    private static String foldCase(String foldee) {
        return UCharacter.foldCase(foldee, true);
    }

    private void getFolded() {
        if (this.folded == null) {
            this.folded = CaseInsensitiveString.foldCase(this.string);
        }
    }

    public CaseInsensitiveString(String s) {
        this.string = s;
    }

    public String getString() {
        return this.string;
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (o instanceof CaseInsensitiveString) {
            this.getFolded();
            CaseInsensitiveString cis = (CaseInsensitiveString)o;
            cis.getFolded();
            return this.folded.equals(cis.folded);
        }
        return false;
    }

    public int hashCode() {
        this.getFolded();
        if (this.hash == 0) {
            this.hash = this.folded.hashCode();
        }
        return this.hash;
    }

    public String toString() {
        return this.string;
    }
}

