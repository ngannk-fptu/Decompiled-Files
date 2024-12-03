/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import cz.vutbr.web.css.Rule;
import cz.vutbr.web.css.StyleSheet;

public interface RuleBlock<T>
extends Rule<T> {
    public void setStyleSheet(StyleSheet var1);

    public StyleSheet getStyleSheet();
}

