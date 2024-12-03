/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit.fn;

import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermFunction;
import cz.vutbr.web.css.TermList;
import cz.vutbr.web.css.TermNumber;
import cz.vutbr.web.csskit.TermFunctionImpl;
import java.util.List;

public class CubicBezierImpl
extends TermFunctionImpl
implements TermFunction.CubicBezier {
    private final float[] _values = new float[4];

    public CubicBezierImpl() {
        this.setValid(false);
    }

    @Override
    public TermList setValue(List<Term<?>> value) {
        super.setValue(value);
        List<List<Term<?>>> args = this.getSeparatedArgs(DEFAULT_ARG_SEP);
        if (args != null && args.size() == 4 && this.setValues(args)) {
            this.setValid(true);
        }
        return this;
    }

    @Override
    public float getX1() {
        return this._values[0];
    }

    @Override
    public float getY1() {
        return this._values[1];
    }

    @Override
    public float getX2() {
        return this._values[2];
    }

    @Override
    public float getY2() {
        return this._values[3];
    }

    private boolean setValues(List<List<Term<?>>> args) {
        for (int i = 0; i < args.size(); ++i) {
            if (this.setValueAt(i, args.get(i))) continue;
            return false;
        }
        return true;
    }

    private boolean setValueAt(int index, List<Term<?>> argTerms) {
        Term<?> t;
        if (argTerms.size() == 1 && (t = argTerms.get(0)) instanceof TermNumber) {
            float value = ((Float)((TermNumber)t).getValue()).floatValue();
            if (index == 1 || index == 3 || value >= 0.0f && value <= 1.0f) {
                this._values[index] = value;
                return true;
            }
        }
        return false;
    }
}

