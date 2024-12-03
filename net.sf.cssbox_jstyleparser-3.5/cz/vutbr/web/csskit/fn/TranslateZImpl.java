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

public class TranslateZImpl
extends TermFunctionImpl
implements TermFunction.TranslateZ {
    private TermLengthOrPercent translate;

    public TranslateZImpl() {
        this.setValid(false);
    }

    @Override
    public TermLengthOrPercent getTranslate() {
        return this.translate;
    }

    @Override
    public TermList setValue(List<Term<?>> value) {
        super.setValue(value);
        List<Term<?>> args = this.getSeparatedValues(DEFAULT_ARG_SEP, false);
        if (args.size() == 1 && (this.translate = this.getLengthOrPercentArg(args.get(0))) != null) {
            this.setValid(true);
        }
        return this;
    }
}

