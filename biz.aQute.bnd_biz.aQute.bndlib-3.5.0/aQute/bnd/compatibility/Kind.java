/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.compatibility;

public enum Kind {
    ROOT,
    CLASS,
    FIELD,
    CONSTRUCTOR,
    METHOD,
    UNKNOWN;


    public String toString() {
        return super.toString().toLowerCase();
    }
}

