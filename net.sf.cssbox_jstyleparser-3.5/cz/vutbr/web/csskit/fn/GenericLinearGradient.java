/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit.fn;

import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermAngle;
import cz.vutbr.web.css.TermList;
import cz.vutbr.web.csskit.fn.GenericGradient;
import java.util.List;

public class GenericLinearGradient
extends GenericGradient {
    private TermAngle angle;

    public TermAngle getAngle() {
        return this.angle;
    }

    @Override
    public TermList setValue(List<Term<?>> value) {
        super.setValue(value);
        List<List<Term<?>>> args = this.getSeparatedArgs(DEFAULT_ARG_SEP);
        if (args.size() > 1) {
            int firstStop = 0;
            List<Term<?>> aarg = args.get(0);
            if (aarg.size() == 1 && (this.angle = this.getAngleArg(aarg.get(0))) != null) {
                firstStop = 1;
            } else {
                this.angle = this.convertSideOrCorner(aarg);
                if (this.angle != null) {
                    firstStop = 1;
                }
            }
            this.loadColorStops(args, firstStop);
            if (this.getColorStops() != null) {
                this.setValid(true);
            }
        }
        return this;
    }
}

