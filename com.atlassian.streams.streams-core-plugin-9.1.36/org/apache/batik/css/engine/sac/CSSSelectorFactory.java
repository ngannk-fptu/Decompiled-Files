/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.sac;

import org.apache.batik.css.engine.sac.CSSChildSelector;
import org.apache.batik.css.engine.sac.CSSConditionalSelector;
import org.apache.batik.css.engine.sac.CSSDescendantSelector;
import org.apache.batik.css.engine.sac.CSSDirectAdjacentSelector;
import org.apache.batik.css.engine.sac.CSSElementSelector;
import org.apache.batik.css.engine.sac.CSSPseudoElementSelector;
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

public class CSSSelectorFactory
implements SelectorFactory {
    public static final SelectorFactory INSTANCE = new CSSSelectorFactory();

    protected CSSSelectorFactory() {
    }

    @Override
    public ConditionalSelector createConditionalSelector(SimpleSelector selector, Condition condition) throws CSSException {
        return new CSSConditionalSelector(selector, condition);
    }

    @Override
    public SimpleSelector createAnyNodeSelector() throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }

    @Override
    public SimpleSelector createRootNodeSelector() throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }

    @Override
    public NegativeSelector createNegativeSelector(SimpleSelector selector) throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }

    @Override
    public ElementSelector createElementSelector(String namespaceURI, String tagName) throws CSSException {
        return new CSSElementSelector(namespaceURI, tagName);
    }

    @Override
    public CharacterDataSelector createTextNodeSelector(String data) throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }

    @Override
    public CharacterDataSelector createCDataSectionSelector(String data) throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }

    @Override
    public ProcessingInstructionSelector createProcessingInstructionSelector(String target, String data) throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }

    @Override
    public CharacterDataSelector createCommentSelector(String data) throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }

    @Override
    public ElementSelector createPseudoElementSelector(String namespaceURI, String pseudoName) throws CSSException {
        return new CSSPseudoElementSelector(namespaceURI, pseudoName);
    }

    @Override
    public DescendantSelector createDescendantSelector(Selector parent, SimpleSelector descendant) throws CSSException {
        return new CSSDescendantSelector(parent, descendant);
    }

    @Override
    public DescendantSelector createChildSelector(Selector parent, SimpleSelector child) throws CSSException {
        return new CSSChildSelector(parent, child);
    }

    @Override
    public SiblingSelector createDirectAdjacentSelector(short nodeType, Selector child, SimpleSelector directAdjacent) throws CSSException {
        return new CSSDirectAdjacentSelector(nodeType, child, directAdjacent);
    }
}

