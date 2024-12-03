/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.css.sac;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CharacterDataSelector;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionalSelector;
import org.w3c.css.sac.DescendantSelector;
import org.w3c.css.sac.ElementSelector;
import org.w3c.css.sac.NegativeSelector;
import org.w3c.css.sac.ProcessingInstructionSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SiblingSelector;
import org.w3c.css.sac.SimpleSelector;

public interface SelectorFactory {
    public ConditionalSelector createConditionalSelector(SimpleSelector var1, Condition var2) throws CSSException;

    public SimpleSelector createAnyNodeSelector() throws CSSException;

    public SimpleSelector createRootNodeSelector() throws CSSException;

    public NegativeSelector createNegativeSelector(SimpleSelector var1) throws CSSException;

    public ElementSelector createElementSelector(String var1, String var2) throws CSSException;

    public CharacterDataSelector createTextNodeSelector(String var1) throws CSSException;

    public CharacterDataSelector createCDataSectionSelector(String var1) throws CSSException;

    public ProcessingInstructionSelector createProcessingInstructionSelector(String var1, String var2) throws CSSException;

    public CharacterDataSelector createCommentSelector(String var1) throws CSSException;

    public ElementSelector createPseudoElementSelector(String var1, String var2) throws CSSException;

    public DescendantSelector createDescendantSelector(Selector var1, SimpleSelector var2) throws CSSException;

    public DescendantSelector createChildSelector(Selector var1, SimpleSelector var2) throws CSSException;

    public SiblingSelector createDirectAdjacentSelector(short var1, Selector var2, SimpleSelector var3) throws CSSException;
}

