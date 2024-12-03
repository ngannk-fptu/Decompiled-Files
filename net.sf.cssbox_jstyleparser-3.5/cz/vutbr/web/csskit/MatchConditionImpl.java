/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.MatchCondition;
import cz.vutbr.web.css.Selector;
import org.w3c.dom.Element;

public class MatchConditionImpl
implements MatchCondition {
    Selector.PseudoClassType pseudo;

    public MatchConditionImpl() {
        this.pseudo = Selector.PseudoClassType.LINK;
    }

    public MatchConditionImpl(Selector.PseudoClassType pseudoClass) {
        this.pseudo = pseudoClass;
    }

    public void setPseudoClass(Selector.PseudoClassType pseudoClass) {
        this.pseudo = pseudoClass;
    }

    @Override
    public boolean isSatisfied(Element e, Selector.SelectorPart selpart) {
        if (selpart instanceof Selector.PseudoClass) {
            Selector.PseudoClassType type = ((Selector.PseudoClass)selpart).getType();
            return type == this.pseudo && e.getTagName().equalsIgnoreCase("a");
        }
        return false;
    }

    public Object clone() {
        return new MatchConditionImpl(this.pseudo);
    }
}

