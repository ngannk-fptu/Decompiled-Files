/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.sac;

import java.util.Set;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.sac.AbstractAttributeCondition;
import org.w3c.dom.Element;

public class CSSPseudoClassCondition
extends AbstractAttributeCondition {
    protected String namespaceURI;

    public CSSPseudoClassCondition(String namespaceURI, String value) {
        super(value);
        this.namespaceURI = namespaceURI;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        CSSPseudoClassCondition c = (CSSPseudoClassCondition)obj;
        return c.namespaceURI.equals(this.namespaceURI);
    }

    @Override
    public int hashCode() {
        return this.namespaceURI.hashCode();
    }

    @Override
    public short getConditionType() {
        return 10;
    }

    @Override
    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    @Override
    public String getLocalName() {
        return null;
    }

    @Override
    public boolean getSpecified() {
        return false;
    }

    @Override
    public boolean match(Element e, String pseudoE) {
        return e instanceof CSSStylableElement ? ((CSSStylableElement)e).isPseudoInstanceOf(this.getValue()) : false;
    }

    @Override
    public void fillAttributeSet(Set attrSet) {
    }

    public String toString() {
        return ":" + this.getValue();
    }
}

