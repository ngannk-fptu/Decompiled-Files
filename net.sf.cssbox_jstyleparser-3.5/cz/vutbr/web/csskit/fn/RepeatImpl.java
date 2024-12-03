/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit.fn;

import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermFunction;
import cz.vutbr.web.css.TermIdent;
import cz.vutbr.web.css.TermInteger;
import cz.vutbr.web.css.TermList;
import cz.vutbr.web.csskit.TermFunctionImpl;
import java.util.List;

public class RepeatImpl
extends TermFunctionImpl
implements TermFunction.Repeat {
    private static final String AUTO_FIT = "auto-fit";
    private static final String AUTO_FILL = "auto-fill";
    private TermFunction.Repeat.Unit _numberOfRepetitions;
    private List<Term<?>> _repeatedTerms;

    public RepeatImpl() {
        this.setValid(false);
    }

    @Override
    public TermList setValue(List<Term<?>> value) {
        super.setValue(value);
        List<List<Term<?>>> args = this.getSeparatedArgs(DEFAULT_ARG_SEP);
        if (args != null && args.size() == 2 && this.setNumberOfRepetitions(args.get(0)) && this.setRepeatedTerms(args.get(1))) {
            this.setValid(true);
        }
        return this;
    }

    @Override
    public TermFunction.Repeat.Unit getNumberOfRepetitions() {
        return this._numberOfRepetitions;
    }

    @Override
    public List<Term<?>> getRepeatedTerms() {
        return this._repeatedTerms;
    }

    private boolean setNumberOfRepetitions(List<Term<?>> argTerms) {
        if (argTerms.size() == 1) {
            Term<?> t = argTerms.get(0);
            if (t instanceof TermInteger) {
                int value = ((TermInteger)t).getIntValue();
                if (value > 0) {
                    this._numberOfRepetitions = TermFunction.Repeat.Unit.createWithNRepetitions(value);
                    return true;
                }
            } else if (t instanceof TermIdent) {
                String value = (String)((TermIdent)t).getValue();
                if (value.equalsIgnoreCase(AUTO_FIT)) {
                    this._numberOfRepetitions = TermFunction.Repeat.Unit.createWithAutoFit();
                    return true;
                }
                if (value.equalsIgnoreCase(AUTO_FILL)) {
                    this._numberOfRepetitions = TermFunction.Repeat.Unit.createWithAutoFill();
                    return true;
                }
            }
        }
        return false;
    }

    private boolean setRepeatedTerms(List<Term<?>> argTerms) {
        this._repeatedTerms = argTerms;
        return true;
    }
}

