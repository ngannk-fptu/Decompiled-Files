/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit.fn;

import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermFunction;
import cz.vutbr.web.css.TermList;
import cz.vutbr.web.csskit.TermFunctionImpl;
import java.util.List;

public class MatrixImpl
extends TermFunctionImpl
implements TermFunction.Matrix {
    private float[] values;

    public MatrixImpl() {
        this.setValid(false);
    }

    @Override
    public float[] getValues() {
        return this.values;
    }

    @Override
    public TermList setValue(List<Term<?>> value) {
        super.setValue(value);
        List<Term<?>> args = this.getSeparatedValues(DEFAULT_ARG_SEP, false);
        if (args.size() == 6) {
            this.values = new float[6];
            this.setValid(true);
            for (int i = 0; i < 6; ++i) {
                if (this.isNumberArg(args.get(i))) {
                    this.values[i] = this.getNumberArg(args.get(i));
                    continue;
                }
                this.setValid(false);
            }
        }
        return this;
    }
}

