/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.RuleBlock;
import cz.vutbr.web.css.StyleSheet;
import cz.vutbr.web.csskit.AbstractRule;

public class AbstractRuleBlock<T>
extends AbstractRule<T>
implements RuleBlock<T> {
    protected StyleSheet stylesheet;

    @Override
    public StyleSheet getStyleSheet() {
        return this.stylesheet;
    }

    @Override
    public void setStyleSheet(StyleSheet stylesheet) {
        this.stylesheet = stylesheet;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        return obj instanceof AbstractRuleBlock;
    }
}

