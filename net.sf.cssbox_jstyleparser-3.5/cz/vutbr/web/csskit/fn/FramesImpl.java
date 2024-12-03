/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit.fn;

import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermFunction;
import cz.vutbr.web.css.TermInteger;
import cz.vutbr.web.css.TermList;
import cz.vutbr.web.csskit.TermFunctionImpl;
import java.util.List;

public class FramesImpl
extends TermFunctionImpl
implements TermFunction.Frames {
    private int _frames;

    public FramesImpl() {
        this.setValid(false);
    }

    @Override
    public TermList setValue(List<Term<?>> value) {
        super.setValue(value);
        List<List<Term<?>>> args = this.getSeparatedArgs(DEFAULT_ARG_SEP);
        if (args != null && args.size() == 1 && this.setFrames(args.get(0))) {
            this.setValid(true);
        }
        return this;
    }

    @Override
    public int getFrames() {
        return this._frames;
    }

    private boolean setFrames(List<Term<?>> argTerms) {
        int value;
        Term<?> t;
        if (argTerms.size() == 1 && (t = argTerms.get(0)) instanceof TermInteger && (value = ((TermInteger)t).getIntValue()) > 0) {
            this._frames = value;
            return true;
        }
        return false;
    }
}

