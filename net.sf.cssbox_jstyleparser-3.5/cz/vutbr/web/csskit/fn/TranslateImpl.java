/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit.fn;

import cz.vutbr.web.css.CSSFactory;
import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermFunction;
import cz.vutbr.web.css.TermLengthOrPercent;
import cz.vutbr.web.css.TermList;
import cz.vutbr.web.csskit.TermFunctionImpl;
import java.util.List;

public class TranslateImpl
extends TermFunctionImpl
implements TermFunction.Translate {
    private TermLengthOrPercent translateX;
    private TermLengthOrPercent translateY;

    public TranslateImpl() {
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
    public TermList setValue(List<Term<?>> value) {
        super.setValue(value);
        List<Term<?>> args = this.getSeparatedValues(DEFAULT_ARG_SEP, false);
        if (args.size() == 2 && (this.translateX = this.getLengthOrPercentArg(args.get(0))) != null && (this.translateY = this.getLengthOrPercentArg(args.get(1))) != null) {
            this.setValid(true);
        } else if (this.size() == 1 && (this.translateX = this.getLengthOrPercentArg(args.get(0))) != null) {
            this.translateY = CSSFactory.getTermFactory().createLength(Float.valueOf(0.0f));
            this.setValid(true);
        }
        return this;
    }
}

