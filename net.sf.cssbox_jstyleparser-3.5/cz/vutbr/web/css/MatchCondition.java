/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import cz.vutbr.web.css.Selector;
import org.w3c.dom.Element;

public interface MatchCondition
extends Cloneable {
    public boolean isSatisfied(Element var1, Selector.SelectorPart var2);
}

