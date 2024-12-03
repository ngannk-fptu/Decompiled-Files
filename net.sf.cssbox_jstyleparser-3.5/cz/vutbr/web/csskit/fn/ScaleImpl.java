/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit.fn;

import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermFunction;
import cz.vutbr.web.css.TermList;
import cz.vutbr.web.csskit.TermFunctionImpl;
import java.util.List;

public class ScaleImpl
extends TermFunctionImpl
implements TermFunction.Scale {
    private float scaleX;
    private float scaleY;

    public ScaleImpl() {
        this.setValid(false);
    }

    @Override
    public float getScaleX() {
        return this.scaleX;
    }

    @Override
    public float getScaleY() {
        return this.scaleY;
    }

    @Override
    public TermList setValue(List<Term<?>> value) {
        super.setValue(value);
        List<Term<?>> args = this.getSeparatedValues(DEFAULT_ARG_SEP, false);
        if (args.size() == 2 && this.isNumberArg(args.get(0)) && this.isNumberArg(args.get(1))) {
            this.scaleX = this.getNumberArg(args.get(0));
            this.scaleY = this.getNumberArg(args.get(1));
            this.setValid(true);
        } else if (this.size() == 1 && this.isNumberArg(args.get(0))) {
            this.scaleX = this.scaleY = this.getNumberArg(args.get(0));
            this.setValid(true);
        }
        return this;
    }
}

