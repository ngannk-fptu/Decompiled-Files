/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit.fn;

import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermFunction;
import cz.vutbr.web.css.TermLength;
import cz.vutbr.web.css.TermList;
import cz.vutbr.web.csskit.TermFunctionImpl;
import java.util.List;

public class BlurImpl
extends TermFunctionImpl
implements TermFunction.Blur {
    private TermLength radius;

    public BlurImpl() {
        this.setValid(false);
    }

    @Override
    public TermLength getRadius() {
        return this.radius;
    }

    @Override
    public TermList setValue(List<Term<?>> value) {
        super.setValue(value);
        List<Term<?>> args = this.getSeparatedValues(DEFAULT_ARG_SEP, false);
        if (args.size() == 1 && (this.radius = this.getLengthArg(args.get(0))) != null) {
            this.setValid(true);
        }
        return this;
    }
}

