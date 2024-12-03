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

public class Translate3dImpl
extends TermFunctionImpl
implements TermFunction.Translate3d {
    private TermLengthOrPercent translateX;
    private TermLengthOrPercent translateY;
    private TermLengthOrPercent translateZ;

    public Translate3dImpl() {
        this.setValid(false);
    }

    @Override
    public TermLengthOrPercent getTranslateX() {
        return this.translateX;
    }

    @Override
    public TermLengthOrPercent getTranslateY() {
        return this.translateY;
    }

    @Override
    public TermLengthOrPercent getTranslateZ() {
        return this.translateZ;
    }

    @Override
    public TermList setValue(List<Term<?>> value) {
        super.setValue(value);
        List<Term<?>> args = this.getSeparatedValues(DEFAULT_ARG_SEP, false);
        if (args.size() == 3 && (this.translateX = this.getLengthOrPercentArg(args.get(0))) != null && (this.translateY = this.getLengthOrPercentArg(args.get(1))) != null && (this.translateZ = this.getLengthOrPercentArg(args.get(2))) != null) {
            this.setValid(true);
        }
        return this;
    }
}

