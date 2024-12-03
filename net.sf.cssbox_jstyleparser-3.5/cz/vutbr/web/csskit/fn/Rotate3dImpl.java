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

public class Rotate3dImpl
extends TermFunctionImpl
implements TermFunction.Rotate3d {
    private float x;
    private float y;
    private float z;
    private TermAngle angle;

    public Rotate3dImpl() {
        this.setValid(false);
    }

    @Override
    public float getX() {
        return this.x;
    }

    @Override
    public float getY() {
        return this.y;
    }

    @Override
    public float getZ() {
        return this.z;
    }

    @Override
    public TermAngle getAngle() {
        return this.angle;
    }

    @Override
    public TermList setValue(List<Term<?>> value) {
        super.setValue(value);
        List<Term<?>> args = this.getSeparatedValues(DEFAULT_ARG_SEP, false);
        if (args.size() == 4 && this.isNumberArg(args.get(0)) && this.isNumberArg(args.get(1)) && this.isNumberArg(args.get(2)) && (this.angle = this.getAngleArg(args.get(3))) != null) {
            this.x = this.getNumberArg(args.get(0));
            this.y = this.getNumberArg(args.get(1));
            this.z = this.getNumberArg(args.get(2));
            this.setValid(true);
        }
        return this;
    }
}

