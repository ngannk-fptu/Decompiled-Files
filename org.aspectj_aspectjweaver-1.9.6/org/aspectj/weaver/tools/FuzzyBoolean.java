/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools;

public final class FuzzyBoolean {
    private String name;
    public static final FuzzyBoolean YES = new FuzzyBoolean("YES");
    public static final FuzzyBoolean NO = new FuzzyBoolean("NO");
    public static final FuzzyBoolean MAYBE = new FuzzyBoolean("MAYBE");

    public static final FuzzyBoolean fromBoolean(boolean b) {
        return b ? YES : NO;
    }

    public String toString() {
        return this.name;
    }

    private FuzzyBoolean() {
    }

    private FuzzyBoolean(String n) {
        this.name = n;
    }
}

