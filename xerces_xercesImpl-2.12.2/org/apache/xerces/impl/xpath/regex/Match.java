/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xpath.regex;

import java.text.CharacterIterator;
import org.apache.xerces.impl.xpath.regex.REUtil;

public class Match
implements Cloneable {
    int[] beginpos = null;
    int[] endpos = null;
    int nofgroups = 0;
    CharacterIterator ciSource = null;
    String strSource = null;
    char[] charSource = null;

    public synchronized Object clone() {
        Match match = new Match();
        if (this.nofgroups > 0) {
            match.setNumberOfGroups(this.nofgroups);
            if (this.ciSource != null) {
                match.setSource(this.ciSource);
            }
            if (this.strSource != null) {
                match.setSource(this.strSource);
            }
            for (int i = 0; i < this.nofgroups; ++i) {
                match.setBeginning(i, this.getBeginning(i));
                match.setEnd(i, this.getEnd(i));
            }
        }
        return match;
    }

    protected void setNumberOfGroups(int n) {
        int n2 = this.nofgroups;
        this.nofgroups = n;
        if (n2 <= 0 || n2 < n || n * 2 < n2) {
            this.beginpos = new int[n];
            this.endpos = new int[n];
        }
        for (int i = 0; i < n; ++i) {
            this.beginpos[i] = -1;
            this.endpos[i] = -1;
        }
    }

    protected void setSource(CharacterIterator characterIterator) {
        this.ciSource = characterIterator;
        this.strSource = null;
        this.charSource = null;
    }

    protected void setSource(String string) {
        this.ciSource = null;
        this.strSource = string;
        this.charSource = null;
    }

    protected void setSource(char[] cArray) {
        this.ciSource = null;
        this.strSource = null;
        this.charSource = cArray;
    }

    protected void setBeginning(int n, int n2) {
        this.beginpos[n] = n2;
    }

    protected void setEnd(int n, int n2) {
        this.endpos[n] = n2;
    }

    public int getNumberOfGroups() {
        if (this.nofgroups <= 0) {
            throw new IllegalStateException("A result is not set.");
        }
        return this.nofgroups;
    }

    public int getBeginning(int n) {
        if (this.beginpos == null) {
            throw new IllegalStateException("A result is not set.");
        }
        if (n < 0 || this.nofgroups <= n) {
            throw new IllegalArgumentException("The parameter must be less than " + this.nofgroups + ": " + n);
        }
        return this.beginpos[n];
    }

    public int getEnd(int n) {
        if (this.endpos == null) {
            throw new IllegalStateException("A result is not set.");
        }
        if (n < 0 || this.nofgroups <= n) {
            throw new IllegalArgumentException("The parameter must be less than " + this.nofgroups + ": " + n);
        }
        return this.endpos[n];
    }

    public String getCapturedText(int n) {
        if (this.beginpos == null) {
            throw new IllegalStateException("match() has never been called.");
        }
        if (n < 0 || this.nofgroups <= n) {
            throw new IllegalArgumentException("The parameter must be less than " + this.nofgroups + ": " + n);
        }
        int n2 = this.beginpos[n];
        int n3 = this.endpos[n];
        if (n2 < 0 || n3 < 0) {
            return null;
        }
        String string = this.ciSource != null ? REUtil.substring(this.ciSource, n2, n3) : (this.strSource != null ? this.strSource.substring(n2, n3) : new String(this.charSource, n2, n3 - n2));
        return string;
    }
}

