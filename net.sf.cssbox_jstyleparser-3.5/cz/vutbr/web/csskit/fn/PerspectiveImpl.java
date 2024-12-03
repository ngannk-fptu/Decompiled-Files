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

public class PerspectiveImpl
extends TermFunctionImpl
implements TermFunction.Perspective {
    private TermLength distance;

    public PerspectiveImpl() {
        this.setValid(false);
    }

    @Override
    public TermLength getDistance() {
        return this.distance;
    }

    @Override
    public TermList setValue(List<Term<?>> value) {
        super.setValue(value);
        List<Term<?>> args = this.getSeparatedValues(DEFAULT_ARG_SEP, false);
        if (args.size() == 1 && (this.distance = this.getLengthArg(args.get(0))) != null) {
            this.setValid(true);
        }
        return this;
    }
}

