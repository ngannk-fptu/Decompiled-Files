/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit.fn;

import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermFunction;
import cz.vutbr.web.css.TermIdent;
import cz.vutbr.web.css.TermLength;
import cz.vutbr.web.css.TermList;
import cz.vutbr.web.css.TermPercent;
import cz.vutbr.web.csskit.TermFunctionImpl;
import java.util.List;

public class MinMaxImpl
extends TermFunctionImpl
implements TermFunction.MinMax {
    private static final String MIN_CONTENT = "min-content";
    private static final String MAX_CONTENT = "max-content";
    private static final String AUTO = "auto";
    private TermFunction.MinMax.Unit _min;
    private TermFunction.MinMax.Unit _max;

    public MinMaxImpl() {
        this.setValid(false);
    }

    @Override
    public TermList setValue(List<Term<?>> value) {
        super.setValue(value);
        List<Term<?>> args = this.getSeparatedValues(DEFAULT_ARG_SEP, true);
        if (args != null && args.size() == 2 && this.setArgument(true, args.get(0)) && this.setArgument(false, args.get(1))) {
            this.setValid(true);
        }
        return this;
    }

    @Override
    public TermFunction.MinMax.Unit getMin() {
        return this._min;
    }

    @Override
    public TermFunction.MinMax.Unit getMax() {
        return this._max;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private boolean setArgument(boolean isMin, Term<?> argTerm) {
        if (argTerm instanceof TermLength) {
            if (isMin) {
                this._min = TermFunction.MinMax.Unit.createWithLenght((TermLength)argTerm);
                return true;
            } else {
                this._max = TermFunction.MinMax.Unit.createWithLenght((TermLength)argTerm);
            }
            return true;
        } else if (argTerm instanceof TermPercent) {
            if (isMin) {
                this._min = TermFunction.MinMax.Unit.createWithLenght((TermPercent)argTerm);
                return true;
            } else {
                this._max = TermFunction.MinMax.Unit.createWithLenght((TermPercent)argTerm);
            }
            return true;
        } else {
            if (!(argTerm instanceof TermIdent)) return false;
            String value = (String)((TermIdent)argTerm).getValue();
            if (value.equalsIgnoreCase(MIN_CONTENT)) {
                if (isMin) {
                    this._min = TermFunction.MinMax.Unit.createWithMinContent();
                    return true;
                } else {
                    this._max = TermFunction.MinMax.Unit.createWithMinContent();
                }
                return true;
            } else if (value.equalsIgnoreCase(MAX_CONTENT)) {
                if (isMin) {
                    this._min = TermFunction.MinMax.Unit.createWithMaxContent();
                    return true;
                } else {
                    this._max = TermFunction.MinMax.Unit.createWithMaxContent();
                }
                return true;
            } else {
                if (!value.equalsIgnoreCase(AUTO)) return false;
                if (isMin) {
                    this._min = TermFunction.MinMax.Unit.createWithAuto();
                    return true;
                } else {
                    this._max = TermFunction.MinMax.Unit.createWithAuto();
                }
            }
        }
        return true;
    }
}

