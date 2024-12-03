/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

public abstract class TermState
implements Cloneable {
    protected TermState() {
    }

    public abstract void copyFrom(TermState var1);

    public TermState clone() {
        try {
            return (TermState)super.clone();
        }
        catch (CloneNotSupportedException cnse) {
            throw new RuntimeException(cnse);
        }
    }

    public String toString() {
        return "TermState";
    }
}

