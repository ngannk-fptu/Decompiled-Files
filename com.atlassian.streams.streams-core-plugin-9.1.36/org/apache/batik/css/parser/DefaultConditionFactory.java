/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.parser;

import org.apache.batik.css.parser.DefaultAndCondition;
import org.apache.batik.css.parser.DefaultAttributeCondition;
import org.apache.batik.css.parser.DefaultBeginHyphenAttributeCondition;
import org.apache.batik.css.parser.DefaultClassCondition;
import org.apache.batik.css.parser.DefaultIdCondition;
import org.apache.batik.css.parser.DefaultLangCondition;
import org.apache.batik.css.parser.DefaultOneOfAttributeCondition;
import org.apache.batik.css.parser.DefaultPseudoClassCondition;
import org.w3c.css.sac.AttributeCondition;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CombinatorCondition;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionFactory;
import org.w3c.css.sac.ContentCondition;
import org.w3c.css.sac.LangCondition;
import org.w3c.css.sac.NegativeCondition;
import org.w3c.css.sac.PositionalCondition;

public class DefaultConditionFactory
implements ConditionFactory {
    public static final ConditionFactory INSTANCE = new DefaultConditionFactory();

    protected DefaultConditionFactory() {
    }

    @Override
    public CombinatorCondition createAndCondition(Condition first, Condition second) throws CSSException {
        return new DefaultAndCondition(first, second);
    }

    @Override
    public CombinatorCondition createOrCondition(Condition first, Condition second) throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }

    @Override
    public NegativeCondition createNegativeCondition(Condition condition) throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }

    @Override
    public PositionalCondition createPositionalCondition(int position, boolean typeNode, boolean type) throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }

    @Override
    public AttributeCondition createAttributeCondition(String localName, String namespaceURI, boolean specified, String value) throws CSSException {
        return new DefaultAttributeCondition(localName, namespaceURI, specified, value);
    }

    @Override
    public AttributeCondition createIdCondition(String value) throws CSSException {
        return new DefaultIdCondition(value);
    }

    @Override
    public LangCondition createLangCondition(String lang) throws CSSException {
        return new DefaultLangCondition(lang);
    }

    @Override
    public AttributeCondition createOneOfAttributeCondition(String localName, String nsURI, boolean specified, String value) throws CSSException {
        return new DefaultOneOfAttributeCondition(localName, nsURI, specified, value);
    }

    @Override
    public AttributeCondition createBeginHyphenAttributeCondition(String localName, String namespaceURI, boolean specified, String value) throws CSSException {
        return new DefaultBeginHyphenAttributeCondition(localName, namespaceURI, specified, value);
    }

    @Override
    public AttributeCondition createClassCondition(String namespaceURI, String value) throws CSSException {
        return new DefaultClassCondition(namespaceURI, value);
    }

    @Override
    public AttributeCondition createPseudoClassCondition(String namespaceURI, String value) throws CSSException {
        return new DefaultPseudoClassCondition(namespaceURI, value);
    }

    @Override
    public Condition createOnlyChildCondition() throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }

    @Override
    public Condition createOnlyTypeCondition() throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }

    @Override
    public ContentCondition createContentCondition(String data) throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }
}

