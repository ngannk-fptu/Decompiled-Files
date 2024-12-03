/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 */
package com.google.template.soy.jssrc.restricted;

import com.google.common.base.Objects;
import com.google.template.soy.internal.targetexpr.TargetExpr;

public class JsExpr
implements TargetExpr {
    private final String text;
    private final int precedence;

    public JsExpr(String text, int precedence) {
        this.text = text;
        this.precedence = precedence;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public int getPrecedence() {
        return this.precedence;
    }

    public String toString() {
        return String.format("JsExpr{text=%s, precedence=%d}", this.text, this.precedence);
    }

    public boolean equals(Object other) {
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        JsExpr otherCast = (JsExpr)other;
        if (this.text.equals(otherCast.text)) {
            if (this.precedence != otherCast.precedence) {
                throw new AssertionError();
            }
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.text, this.precedence});
    }
}

