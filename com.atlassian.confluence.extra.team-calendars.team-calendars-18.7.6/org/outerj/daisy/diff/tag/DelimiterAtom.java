/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.tag;

import org.outerj.daisy.diff.tag.Atom;
import org.outerj.daisy.diff.tag.TextAtom;

public class DelimiterAtom
extends TextAtom {
    public DelimiterAtom(char c) {
        super("" + c);
    }

    public static boolean isValidDelimiter(String s) {
        if (s.length() == 1) {
            return DelimiterAtom.isValidDelimiter(s.charAt(0));
        }
        return false;
    }

    public static boolean isValidDelimiter(char c) {
        switch (c) {
            case '\t': 
            case '\n': 
            case '\r': 
            case ' ': 
            case '!': 
            case '\"': 
            case '&': 
            case '\'': 
            case '(': 
            case ')': 
            case '*': 
            case '+': 
            case ',': 
            case '-': 
            case '.': 
            case '/': 
            case ':': 
            case ';': 
            case '=': 
            case '?': 
            case '[': 
            case '\\': 
            case ']': 
            case '_': 
            case '{': 
            case '|': 
            case '}': {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isValidAtom(String s) {
        return super.isValidAtom(s) && this.isValidDelimiterAtom(s);
    }

    private boolean isValidDelimiterAtom(String s) {
        return DelimiterAtom.isValidDelimiter(s);
    }

    @Override
    public String toString() {
        return "DelimiterAtom: " + this.getFullText().replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r").replaceAll("\t", "\\\\t");
    }

    @Override
    public boolean equalsIdentifier(Atom a) {
        return super.equalsIdentifier(a) || (a.getIdentifier().equals(" ") || a.getIdentifier().equals("\n")) && (this.getIdentifier().equals(" ") || this.getIdentifier().equals("\n"));
    }
}

