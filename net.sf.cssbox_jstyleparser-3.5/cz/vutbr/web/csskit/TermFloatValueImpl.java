/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermFloatValue;
import cz.vutbr.web.css.TermNumeric;
import cz.vutbr.web.csskit.TermNumericImpl;

public class TermFloatValueImpl
extends TermNumericImpl<Float>
implements TermFloatValue {
    @Override
    public TermNumeric<Float> setZero() {
        super.setValue(Float.valueOf(0.0f));
        return this;
    }

    @Override
    public Term<Float> setValue(Float value) {
        if (value.floatValue() == -0.0f) {
            return super.setValue(Float.valueOf(0.0f));
        }
        return super.setValue(value);
    }
}

