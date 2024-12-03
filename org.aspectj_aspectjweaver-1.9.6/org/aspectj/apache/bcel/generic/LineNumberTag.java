/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import org.aspectj.apache.bcel.generic.Tag;

public class LineNumberTag
extends Tag {
    private final int lineNumber;

    public LineNumberTag(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public String toString() {
        return "line " + this.lineNumber;
    }

    public boolean equals(Object other) {
        if (!(other instanceof LineNumberTag)) {
            return false;
        }
        return this.lineNumber == ((LineNumberTag)other).lineNumber;
    }

    public int hashCode() {
        return this.lineNumber;
    }
}

