/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit.fn;

import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermFunction;
import cz.vutbr.web.css.TermList;
import cz.vutbr.web.csskit.TermFunctionImpl;
import java.util.List;

public class Scale3dImpl
extends TermFunctionImpl
implements TermFunction.Scale3d {
    private float scaleX;
    private float scaleY;
    private float scaleZ;

    public Scale3dImpl() {
        this.setValid(false);
    }

    @Override
    public float getScaleX() {
        return this.scaleX;
    }

    @Override
    public float getScaleY() {
        return this.scaleY;
    }

    @Override
    public float getScaleZ() {
        return this.scaleZ;
    }

    @Override
    public TermList setValue(List<Term<?>> value) {
        super.setValue(value);
        List<Term<?>> args = this.getSeparatedValues(DEFAULT_ARG_SEP, false);
        if (args.size() == 3 && this.isNumberArg(args.get(0)) && this.isNumberArg(args.get(1)) && this.isNumberArg(args.get(2))) {
            this.scaleX = this.getNumberArg(args.get(0));
            this.scaleY = this.getNumberArg(args.get(1));
            this.scaleZ = this.getNumberArg(args.get(2));
            this.setValid(true);
        }
        return this;
    }
}

