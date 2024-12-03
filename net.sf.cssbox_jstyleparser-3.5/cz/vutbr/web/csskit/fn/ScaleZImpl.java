/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit.fn;

import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermFunction;
import cz.vutbr.web.css.TermList;
import cz.vutbr.web.csskit.TermFunctionImpl;
import java.util.List;

public class ScaleZImpl
extends TermFunctionImpl
implements TermFunction.ScaleZ {
    private float scale;

    public ScaleZImpl() {
        this.setValid(false);
    }

    @Override
    public float getScale() {
        return this.scale;
    }

    @Override
    public TermList setValue(List<Term<?>> value) {
        super.setValue(value);
        List<Term<?>> args = this.getSeparatedValues(DEFAULT_ARG_SEP, false);
        if (args.size() == 1 && this.isNumberArg(args.get(0))) {
            this.scale = this.getNumberArg(args.get(0));
            this.setValid(true);
        }
        return this;
    }
}

