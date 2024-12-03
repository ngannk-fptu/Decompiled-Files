/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit.fn;

import cz.vutbr.web.css.CSSFactory;
import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermAngle;
import cz.vutbr.web.css.TermFunction;
import cz.vutbr.web.css.TermList;
import cz.vutbr.web.csskit.TermFunctionImpl;
import java.util.List;

public class SkewImpl
extends TermFunctionImpl
implements TermFunction.Skew {
    private TermAngle skewX;
    private TermAngle skewY;

    public SkewImpl() {
        this.setValid(false);
    }

    @Override
    public TermAngle getSkewX() {
        return this.skewX;
    }

    @Override
    public TermAngle getSkewY() {
        return this.skewY;
    }

    @Override
    public TermList setValue(List<Term<?>> value) {
        super.setValue(value);
        List<Term<?>> args = this.getSeparatedValues(DEFAULT_ARG_SEP, false);
        if (args.size() == 2 && (this.skewX = this.getAngleArg(args.get(0))) != null && (this.skewY = this.getAngleArg(args.get(1))) != null) {
            this.setValid(true);
        } else if (this.size() == 1 && (this.skewX = this.getAngleArg(args.get(0))) != null) {
            this.skewY = CSSFactory.getTermFactory().createAngle(Float.valueOf(0.0f));
            this.setValid(true);
        }
        return this;
    }
}

