/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.sac;

import java.util.Set;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.sac.AbstractAttributeCondition;
import org.w3c.dom.Element;

public class CSSIdCondition
extends AbstractAttributeCondition {
    protected String namespaceURI;
    protected String localName;

    public CSSIdCondition(String ns, String ln, String value) {
        super(value);
        this.namespaceURI = ns;
        this.localName = ln;
    }

    @Override
    public short getConditionType() {
        return 5;
    }

    @Override
    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    @Override
    public String getLocalName() {
        return this.localName;
    }

    @Override
    public boolean getSpecified() {
        return true;
    }

    @Override
    public boolean match(Element e, String pseudoE) {
        return e instanceof CSSStylableElement ? ((CSSStylableElement)e).getXMLId().equals(this.getValue()) : false;
    }

    @Override
    public void fillAttributeSet(Set attrSet) {
        attrSet.add(this.localName);
    }

    @Override
    public int getSpecificity() {
        return 65536;
    }

    public String toString() {
        return '#' + this.getValue();
    }
}

