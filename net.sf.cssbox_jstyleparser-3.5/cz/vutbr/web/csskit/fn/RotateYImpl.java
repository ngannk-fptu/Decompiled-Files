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

public class RotateYImpl
extends TermFunctionImpl
implements TermFunction.RotateY {
    private TermAngle angle;

    public RotateYImpl() {
        this.setValid(false);
    }

    @Override
    public TermAngle getAngle() {
        return this.angle;
    }

    @Override
    public TermList setValue(List<Term<?>> value) {
        super.setValue(value);
        List<Term<?>> args = this.getSeparatedValues(DEFAULT_ARG_SEP, false);
        if (args.size() == 1 && (this.angle = this.getAngleArg(args.get(0))) != null) {
            this.setValid(true);
        }
        return this;
    }
}

