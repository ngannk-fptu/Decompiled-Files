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

public class StepsImpl
extends TermFunctionImpl
implements TermFunction.Steps {
    private int _numberOfSteps;
    private TermFunction.Steps.Direction _direciton;

    public StepsImpl() {
        this.setValid(false);
    }

    @Override
    public TermList setValue(List<Term<?>> value) {
        super.setValue(value);
        List<List<Term<?>>> args = this.getSeparatedArgs(DEFAULT_ARG_SEP);
        if (args != null) {
            if (args.size() == 1) {
                if (this.setNumberOfSteps(args.get(0))) {
                    this._direciton = TermFunction.Steps.Direction.END;
                    this.setValid(true);
                }
            } else if (args.size() == 2 && this.setNumberOfSteps(args.get(0)) && this.setDirection(args.get(1))) {
                this.setValid(true);
            }
        }
        return this;
    }

    @Override
    public int getNumberOfSteps() {
        return this._numberOfSteps;
    }

    @Override
    public TermFunction.Steps.Direction getDirection() {
        return this._direciton;
    }

    private boolean setNumberOfSteps(List<Term<?>> argTerms) {
        int value;
        Term<?> t;
        if (argTerms.size() == 1 && (t = argTerms.get(0)) instanceof TermInteger && (value = ((TermInteger)t).getIntValue()) > 0) {
            this._numberOfSteps = value;
            return true;
        }
        return false;
    }

    private boolean setDirection(List<Term<?>> argTerms) {
        Term<?> t;
        if (argTerms.size() == 1 && (t = argTerms.get(0)) instanceof TermIdent) {
            String value = (String)((TermIdent)t).getValue();
            for (TermFunction.Steps.Direction d : TermFunction.Steps.Direction.values()) {
                if (!d.toString().equals(value)) continue;
                this._direciton = d;
                return true;
            }
        }
        return false;
    }
}

