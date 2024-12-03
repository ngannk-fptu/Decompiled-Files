/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit.fn;

import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermColor;
import cz.vutbr.web.css.TermFunction;
import cz.vutbr.web.css.TermLength;
import cz.vutbr.web.css.TermList;
import cz.vutbr.web.csskit.TermFunctionImpl;
import java.util.List;

public class DropShadowImpl
extends TermFunctionImpl
implements TermFunction.DropShadow {
    private TermLength offsetX;
    private TermLength offsetY;
    private TermLength blurRadius;
    private TermColor color;

    public DropShadowImpl() {
        this.setValid(false);
    }

    @Override
    public TermLength getOffsetX() {
        return this.offsetX;
    }

    @Override
    public TermLength getOffsetY() {
        return this.offsetY;
    }

    @Override
    public TermLength getBlurRadius() {
        return this.blurRadius;
    }

    @Override
    public TermColor getColor() {
        return this.color;
    }

    @Override
    public TermList setValue(List<Term<?>> value) {
        super.setValue(value);
        List<Term<?>> args = this.getValues(false);
        if (args.size() >= 2) {
            if (args.get(0) instanceof TermColor) {
                this.color = (TermColor)args.get(0);
                args.remove(0);
            } else if (args.get(args.size() - 1) instanceof TermColor) {
                this.color = (TermColor)args.get(args.size() - 1);
                args.remove(args.size() - 1);
            }
            if (args.size() >= 2) {
                this.offsetX = this.getLengthArg(args.get(0));
                if (this.offsetX != null && (this.offsetY = this.getLengthArg(args.get(1))) != null) {
                    this.setValid(true);
                }
                if (args.size() >= 3) {
                    this.blurRadius = this.getLengthArg(args.get(2));
                    if (this.blurRadius != null) {
                        this.setValid(true);
                    } else {
                        this.setValid(false);
                    }
                }
            }
        }
        return this;
    }
}

