/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit.fn;

import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermFunction;
import cz.vutbr.web.css.TermLengthOrPercent;
import cz.vutbr.web.css.TermList;
import cz.vutbr.web.csskit.TermFunctionImpl;
import java.util.List;

public class FitContentImpl
extends TermFunctionImpl
implements TermFunction.FitContent {
    private TermLengthOrPercent _max;

    public FitContentImpl() {
        this.setValid(false);
    }

    @Override
    public TermList setValue(List<Term<?>> value) {
        super.setValue(value);
        List<Term<?>> args = this.getSeparatedValues(DEFAULT_ARG_SEP, true);
        if (args != null && args.size() == 1) {
            this._max = this.getLengthOrPercentArg(args.get(0));
            if (this._max != null) {
                this.setValid(true);
            }
        }
        return this;
    }

    @Override
    public TermLengthOrPercent getMaximum() {
        return this._max;
    }
}

