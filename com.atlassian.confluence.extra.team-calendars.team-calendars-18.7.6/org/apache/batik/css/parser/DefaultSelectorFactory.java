/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.CSSException
 *  org.w3c.css.sac.CharacterDataSelector
 *  org.w3c.css.sac.Condition
 *  org.w3c.css.sac.ConditionalSelector
 *  org.w3c.css.sac.DescendantSelector
 *  org.w3c.css.sac.ElementSelector
 *  org.w3c.css.sac.NegativeSelector
 *  org.w3c.css.sac.ProcessingInstructionSelector
 *  org.w3c.css.sac.Selector
 *  org.w3c.css.sac.SelectorFactory
 *  org.w3c.css.sac.SiblingSelector
 *  org.w3c.css.sac.SimpleSelector
 */
package org.apache.batik.css.parser;

import org.apache.batik.css.parser.DefaultChildSelector;
import org.apache.batik.css.parser.DefaultConditionalSelector;
import org.apache.batik.css.parser.DefaultDescendantSelector;
import org.apache.batik.css.parser.DefaultDirectAdjacentSelector;
import org.apache.batik.css.parser.DefaultElementSelector;
import org.apache.batik.css.parser.DefaultPseudoElementSelector;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CharacterDataSelector;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionalSelector;
import org.w3c.css.sac.DescendantSelector;
import org.w3c.css.sac.ElementSelector;
import org.w3c.css.sac.NegativeSelector;
import org.w3c.css.sac.ProcessingInstructionSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorFactory;
import org.w3c.css.sac.SiblingSelector;
import org.w3c.css.sac.SimpleSelector;

public class DefaultSelectorFactory
implements SelectorFactory {
    public static final SelectorFactory INSTANCE = new DefaultSelectorFactory();

    protected DefaultSelectorFactory() {
    }

    public ConditionalSelector createConditionalSelector(SimpleSelector selector, Condition condition) throws CSSException {
        return new DefaultConditionalSelector(selector, condition);
    }

    public SimpleSelector createAnyNodeSelector() throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }

    public SimpleSelector createRootNodeSelector() throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }

    public NegativeSelector createNegativeSelector(SimpleSelector selector) throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }

    public ElementSelector createElementSelector(String namespaceURI, String tagName) throws CSSException {
        return new DefaultElementSelector(namespaceURI, tagName);
    }

    public CharacterDataSelector createTextNodeSelector(String data) throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }

    public CharacterDataSelector createCDataSectionSelector(String data) throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }

    public ProcessingInstructionSelector createProcessingInstructionSelector(String target, String data) throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }

    public CharacterDataSelector createCommentSelector(String data) throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }

    public ElementSelector createPseudoElementSelector(String namespaceURI, String pseudoName) throws CSSException {
        return new DefaultPseudoElementSelector(namespaceURI, pseudoName);
    }

    public DescendantSelector createDescendantSelector(Selector parent, SimpleSelector descendant) throws CSSException {
        return new DefaultDescendantSelector(parent, descendant);
    }

    public DescendantSelector createChildSelector(Selector parent, SimpleSelector child) throws CSSException {
        return new DefaultChildSelector(parent, child);
    }

    public SiblingSelector createDirectAdjacentSelector(short nodeType, Selector child, SimpleSelector directAdjacent) throws CSSException {
        return new DefaultDirectAdjacentSelector(nodeType, child, directAdjacent);
    }
}

