/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit.fn;

import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermAngle;
import cz.vutbr.web.css.TermFunction;
import cz.vutbr.web.css.TermList;
import cz.vutbr.web.csskit.TermFunctionImpl;
import java.util.List;

public class SkewYImpl
extends TermFunctionImpl
implements TermFunction.SkewY {
    private TermAngle skew;

    public SkewYImpl() {
        this.setValid(false);
    }

    @Override
    public TermAngle getSkew() {
        return this.skew;
    }

    @Override
    public TermList setValue(List<Term<?>> value) {
        super.setValue(value);
        List<Term<?>> args = this.getSeparatedValues(DEFAULT_ARG_SEP, false);
        if (args.size() == 1 && (this.skew = this.getAngleArg(args.get(0))) != null) {
            this.setValid(true);
        }
        return this;
    }
}

