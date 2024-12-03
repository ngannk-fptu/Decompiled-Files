/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.TermInteger;
import cz.vutbr.web.css.TermNumeric;
import cz.vutbr.web.csskit.TermLengthImpl;

public class TermIntegerImpl
extends TermLengthImpl
implements TermInteger {
    protected TermIntegerImpl() {
        this.setUnit(TermNumeric.Unit.none);
    }

    @Override
    public int getIntValue() {
        return ((Float)this.getValue()).intValue();
    }

    @Override
    public TermInteger setValue(int value) {
        this.setValue(Float.valueOf(value));
        return this;
    }

    @Override
    public String toString() {
        if (this.operator != null) {
            return this.operator.value() + this.getIntValue();
        }
        return String.valueOf(this.getIntValue());
    }
}

