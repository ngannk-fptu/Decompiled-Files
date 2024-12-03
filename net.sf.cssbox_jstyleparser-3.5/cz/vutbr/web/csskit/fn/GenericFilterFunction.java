/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit.fn;

import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermList;
import cz.vutbr.web.css.TermPercent;
import cz.vutbr.web.csskit.TermFunctionImpl;
import java.util.List;

public class GenericFilterFunction
extends TermFunctionImpl {
    private float amount;

    public GenericFilterFunction() {
        this.setValid(false);
    }

    public float getAmount() {
        return this.amount;
    }

    @Override
    public TermList setValue(List<Term<?>> value) {
        super.setValue(value);
        List<Term<?>> args = this.getSeparatedValues(DEFAULT_ARG_SEP, false);
        if (args.size() == 1) {
            Term<?> arg = args.get(0);
            if (this.isNumberArg(arg)) {
                this.amount = this.getNumberArg(args.get(0));
                this.setValid(true);
            } else if (arg instanceof TermPercent) {
                this.amount = ((Float)((TermPercent)arg).getValue()).floatValue() / 100.0f;
                this.setValid(true);
            }
        }
        return this;
    }
}

