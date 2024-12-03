/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.sac;

import org.apache.batik.css.engine.sac.CSSAndCondition;
import org.apache.batik.css.engine.sac.CSSAttributeCondition;
import org.apache.batik.css.engine.sac.CSSBeginHyphenAttributeCondition;
import org.apache.batik.css.engine.sac.CSSClassCondition;
import org.apache.batik.css.engine.sac.CSSIdCondition;
import org.apache.batik.css.engine.sac.CSSLangCondition;
import org.apache.batik.css.engine.sac.CSSOneOfAttributeCondition;
import org.apache.batik.css.engine.sac.CSSPseudoClassCondition;
import org.w3c.css.sac.AttributeCondition;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CombinatorCondition;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionFactory;
import org.w3c.css.sac.ContentCondition;
import org.w3c.css.sac.LangCondition;
import org.w3c.css.sac.NegativeCondition;
import org.w3c.css.sac.PositionalCondition;

public class CSSConditionFactory
implements ConditionFactory {
    protected String classNamespaceURI;
    protected String classLocalName;
    protected String idNamespaceURI;
    protected String idLocalName;

    public CSSConditionFactory(String cns, String cln, String idns, String idln) {
        this.classNamespaceURI = cns;
        this.classLocalName = cln;
        this.idNamespaceURI = idns;
        this.idLocalName = idln;
    }

    @Override
    public CombinatorCondition createAndCondition(Condition first, Condition second) throws CSSException {
        return new CSSAndCondition(first, second);
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
        return new CSSAttributeCondition(localName, namespaceURI, specified, value);
    }

    @Override
    public AttributeCondition createIdCondition(String value) throws CSSException {
        return new CSSIdCondition(this.idNamespaceURI, this.idLocalName, value);
    }

    @Override
    public LangCondition createLangCondition(String lang) throws CSSException {
        return new CSSLangCondition(lang);
    }

    @Override
    public AttributeCondition createOneOfAttributeCondition(String localName, String nsURI, boolean specified, String value) throws CSSException {
        return new CSSOneOfAttributeCondition(localName, nsURI, specified, value);
    }

    @Override
    public AttributeCondition createBeginHyphenAttributeCondition(String localName, String namespaceURI, boolean specified, String value) throws CSSException {
        return new CSSBeginHyphenAttributeCondition(localName, namespaceURI, specified, value);
    }

    @Override
    public AttributeCondition createClassCondition(String namespaceURI, String value) throws CSSException {
        return new CSSClassCondition(this.classLocalName, this.classNamespaceURI, value);
    }

    @Override
    public AttributeCondition createPseudoClassCondition(String namespaceURI, String value) throws CSSException {
        return new CSSPseudoClassCondition(namespaceURI, value);
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

