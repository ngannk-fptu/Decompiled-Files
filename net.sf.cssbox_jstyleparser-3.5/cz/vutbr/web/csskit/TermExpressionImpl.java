/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.TermExpression;
import cz.vutbr.web.csskit.TermImpl;

public class TermExpressionImpl
extends TermImpl<String>
implements TermExpression {
    protected TermExpressionImpl() {
    }

    public TermExpression setValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Invalid value for TermExpression(null)");
        }
        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.operator != null) {
            sb.append(this.operator.value());
        }
        sb.append("expression(").append((String)this.value).append(")");
        return sb.toString();
    }
}

