/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.tag;

import org.outerj.daisy.diff.tag.Atom;

public class TagAtom
implements Atom {
    private String identifier;
    private String internalIdentifiers = "";

    public TagAtom(String s) {
        if (!this.isValidAtom(s)) {
            throw new IllegalArgumentException("The given string is not a valid tag");
        }
        if ((s = s.substring(1, s.length() - 1)).indexOf(32) > 0) {
            this.identifier = s.substring(0, s.indexOf(32));
            this.internalIdentifiers = s.substring(s.indexOf(32) + 1);
        } else {
            this.identifier = s;
        }
    }

    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public String getInternalIdentifiers() {
        return this.internalIdentifiers;
    }

    @Override
    public boolean hasInternalIdentifiers() {
        return this.internalIdentifiers.length() > 0;
    }

    public static boolean isValidTag(String s) {
        return s.lastIndexOf(60) == 0 && s.indexOf(62) == s.length() - 1 && s.length() >= 3;
    }

    @Override
    public String getFullText() {
        String s = "<" + this.identifier;
        if (this.hasInternalIdentifiers()) {
            s = s + " " + this.internalIdentifiers;
        }
        s = s + ">";
        return s;
    }

    @Override
    public boolean isValidAtom(String s) {
        return TagAtom.isValidTag(s);
    }

    public String toString() {
        return "TagAtom: " + this.getFullText();
    }

    @Override
    public boolean equalsIdentifier(Atom other) {
        return other.getIdentifier().equals(this.getIdentifier());
    }
}

