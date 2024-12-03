/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit.fn;

import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermColor;
import cz.vutbr.web.css.TermFunction;
import cz.vutbr.web.css.TermLengthOrPercent;
import cz.vutbr.web.csskit.ColorStopImpl;
import cz.vutbr.web.csskit.TermFunctionImpl;
import java.util.ArrayList;
import java.util.List;

public class GenericGradient
extends TermFunctionImpl {
    private List<TermFunction.Gradient.ColorStop> colorStops;

    public GenericGradient() {
        this.setValid(false);
    }

    public List<TermFunction.Gradient.ColorStop> getColorStops() {
        return this.colorStops;
    }

    protected void loadColorStops(List<List<Term<?>>> args, int firstStop) {
        this.colorStops = this.decodeColorStops(args, firstStop);
    }

    protected List<TermFunction.Gradient.ColorStop> decodeColorStops(List<List<Term<?>>> args, int firstStop) {
        boolean valid = true;
        ArrayList<TermFunction.Gradient.ColorStop> colorStops = null;
        if (args.size() > firstStop) {
            colorStops = new ArrayList<TermFunction.Gradient.ColorStop>();
            for (int i = firstStop; valid && i < args.size(); ++i) {
                List<Term<?>> sarg = args.get(i);
                if (sarg.size() == 1 || sarg.size() == 2) {
                    Term<?> tlen;
                    Term<?> tclr = sarg.get(0);
                    Term<?> term = tlen = sarg.size() == 2 ? sarg.get(1) : null;
                    if (tclr instanceof TermColor && (tlen == null || tlen instanceof TermLengthOrPercent)) {
                        ColorStopImpl newStop = new ColorStopImpl((TermColor)tclr, (TermLengthOrPercent)tlen);
                        colorStops.add(newStop);
                        continue;
                    }
                    valid = false;
                    continue;
                }
                valid = false;
            }
        }
        if (valid && colorStops != null && !colorStops.isEmpty()) {
            return colorStops;
        }
        return null;
    }
}

