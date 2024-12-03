/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 */
package com.google.template.soy.javasrc.restricted;

import com.google.common.base.Objects;

public class JavaExpr {
    private final String text;
    private final Class<?> type;
    private final int precedence;

    public JavaExpr(String text, Class<?> type, int precedence) {
        this.text = text;
        this.type = type;
        this.precedence = precedence;
    }

    public String getText() {
        return this.text;
    }

    public Class<?> getType() {
        return this.type;
    }

    public int getPrecedence() {
        return this.precedence;
    }

    public String toString() {
        return String.format("JavaExpr{text=%s, precedence=%d}", this.text, this.precedence);
    }

    public boolean equals(Object other) {
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        JavaExpr otherCast = (JavaExpr)other;
        if (this.text.equals(otherCast.text) && this.type.equals(otherCast.type)) {
            if (this.precedence != otherCast.precedence) {
                throw new AssertionError();
            }
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.text, this.type, this.precedence});
    }
}

