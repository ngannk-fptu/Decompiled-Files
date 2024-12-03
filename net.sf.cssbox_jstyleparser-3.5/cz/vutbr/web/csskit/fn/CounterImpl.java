/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit.fn;

import cz.vutbr.web.css.CSSProperty;
import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermFunction;
import cz.vutbr.web.css.TermIdent;
import cz.vutbr.web.css.TermList;
import cz.vutbr.web.csskit.TermFunctionImpl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CounterImpl
extends TermFunctionImpl
implements TermFunction.Counter {
    public static Map<String, CSSProperty.ListStyleType> allowedStyles = new HashMap<String, CSSProperty.ListStyleType>(CSSProperty.ListStyleType.values().length - 4);
    private String name;
    private CSSProperty.ListStyleType style;

    public CounterImpl() {
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
    public TermList setValue(List<Term<?>> value) {
        super.setValue(value);
        List<Term<?>> args = this.getSeparatedValues(DEFAULT_ARG_SEP, true);
        if (args != null && (args.size() == 1 || args.size() == 2)) {
            if (args.get(0) instanceof TermIdent) {
                this.name = (String)((TermIdent)args.get(0)).getValue();
                this.setValid(true);
            }
            if (args.size() == 2) {
                if (args.get(1) instanceof TermIdent) {
                    String styleString = (String)((TermIdent)args.get(1)).getValue();
                    this.style = allowedStyles.get(styleString.toLowerCase());
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

    static {
        for (CSSProperty.ListStyleType item : CSSProperty.ListStyleType.values()) {
            if (item == CSSProperty.ListStyleType.INHERIT || item == CSSProperty.ListStyleType.INITIAL || item == CSSProperty.ListStyleType.UNSET) continue;
            allowedStyles.put(item.toString(), item);
        }
    }
}

