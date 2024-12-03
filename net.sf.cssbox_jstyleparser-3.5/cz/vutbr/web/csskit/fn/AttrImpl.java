/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit.fn;

import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermFunction;
import cz.vutbr.web.css.TermIdent;
import cz.vutbr.web.css.TermList;
import cz.vutbr.web.csskit.TermFunctionImpl;
import java.util.List;

public class AttrImpl
extends TermFunctionImpl
implements TermFunction.Attr {
    private String name;

    public AttrImpl() {
        this.setValid(false);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public TermList setValue(List<Term<?>> value) {
        super.setValue(value);
        List<Term<?>> args = this.getSeparatedValues(DEFAULT_ARG_SEP, true);
        if (args != null && args.size() == 1 && args.get(0) instanceof TermIdent) {
            this.name = (String)((TermIdent)args.get(0)).getValue();
            this.setValid(true);
        }
        return this;
    }
}

