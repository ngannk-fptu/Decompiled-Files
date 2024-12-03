/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.tag;

public interface Atom {
    public String getIdentifier();

    public boolean hasInternalIdentifiers();

    public String getInternalIdentifiers();

    public String getFullText();

    public boolean isValidAtom(String var1);

    public boolean equalsIdentifier(Atom var1);
}

