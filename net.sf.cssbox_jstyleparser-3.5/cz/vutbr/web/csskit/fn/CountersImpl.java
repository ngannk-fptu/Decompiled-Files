/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit.fn;

import cz.vutbr.web.css.CSSProperty;
import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermFunction;
import cz.vutbr.web.css.TermIdent;
import cz.vutbr.web.css.TermList;
import cz.vutbr.web.css.TermString;
import cz.vutbr.web.csskit.TermFunctionImpl;
import cz.vutbr.web.csskit.fn.CounterImpl;
import java.util.List;

public class CountersImpl
extends TermFunctionImpl
implements TermFunction.Counters {
    private String name;
    private CSSProperty.ListStyleType style;
    private String separator;

    public CountersImpl() {
        this.setValid(false);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public CSSProperty.ListStyleType getStyle() {
        return this.style;
    }

    @Override
    public String getSeparator() {
        return this.separator;
    }

    @Override
    public TermList setValue(List<Term<?>> value) {
        super.setValue(value);
        List<Term<?>> args = this.getSeparatedValues(DEFAULT_ARG_SEP, true);
        if (args != null && (args.size() == 2 || args.size() == 3)) {
            if (args.get(0) instanceof TermIdent && args.get(1) instanceof TermString) {
                this.name = (String)((TermIdent)args.get(0)).getValue();
                this.separator = (String)((TermString)args.get(1)).getValue();
                this.setValid(true);
            }
            if (args.size() == 3) {
                if (args.get(2) instanceof TermIdent) {
                    String styleString = (String)((TermIdent)args.get(2)).getValue();
                    this.style = CounterImpl.allowedStyles.get(styleString.toLowerCase());
                    if (this.style == null) {
                        this.setValid(false);
                    }
                } else {
                    this.setValid(false);
                }
            }
        }
        return this;
    }
}

