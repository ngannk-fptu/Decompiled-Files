/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.tag;

import org.outerj.daisy.diff.tag.Atom;

public class TextAtom
implements Atom {
    private String s;

    public TextAtom(String s) {
        if (!this.isValidAtom(s)) {
            throw new IllegalArgumentException("The given String is not a valid Text Atom");
        }
        this.s = s;
    }

    @Override
    public String getFullText() {
        return this.s;
    }

    @Override
    public String getIdentifier() {
        return this.s;
    }

    @Override
    public String getInternalIdentifiers() {
        throw new IllegalStateException("This Atom has no internal identifiers");
    }

    @Override
    public boolean hasInternalIdentifiers() {
        return false;
    }

    @Override
    public boolean isValidAtom(String s) {
        return s != null && s.length() > 0;
    }

    public String toString() {
        return "TextAtom: " + this.getFullText();
    }

    @Override
    public boolean equalsIdentifier(Atom other) {
        return other.getIdentifier().equals(this.getIdentifier());
    }
}

