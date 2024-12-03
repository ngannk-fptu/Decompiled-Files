/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.RuleBlock;
import cz.vutbr.web.css.StyleSheet;
import cz.vutbr.web.csskit.AbstractRule;

public class StyleSheetImpl
extends AbstractRule<RuleBlock<?>>
implements StyleSheet {
    private StyleSheet.Origin origin = StyleSheet.Origin.AUTHOR;

    protected StyleSheetImpl() {
    }

    @Override
    public void setOrigin(StyleSheet.Origin o) {
        this.origin = o;
    }

    @Override
    public StyleSheet.Origin getOrigin() {
        return this.origin;
    }

    @Override
    public void add(int index, RuleBlock<?> element) {
        element.setStyleSheet(this);
        super.add(index, element);
    }

    @Override
    public boolean add(RuleBlock<?> o) {
        o.setStyleSheet(this);
        return super.add(o);
    }
}

