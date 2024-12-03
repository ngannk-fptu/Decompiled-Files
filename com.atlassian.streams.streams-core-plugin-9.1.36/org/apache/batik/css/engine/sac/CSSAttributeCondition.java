/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.sac;

import java.util.Set;
import org.apache.batik.css.engine.sac.AbstractAttributeCondition;
import org.w3c.dom.Element;

public class CSSAttributeCondition
extends AbstractAttributeCondition {
    protected String localName;
    protected String namespaceURI;
    protected boolean specified;

    public CSSAttributeCondition(String localName, String namespaceURI, boolean specified, String value) {
        super(value);
        this.localName = localName;
        this.namespaceURI = namespaceURI;
        this.specified = specified;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        CSSAttributeCondition c = (CSSAttributeCondition)obj;
        return c.namespaceURI.equals(this.namespaceURI) && c.localName.equals(this.localName) && c.specified == this.specified;
    }

    @Override
    public int hashCode() {
        return this.namespaceURI.hashCode() ^ this.localName.hashCode() ^ (this.specified ? -1 : 0);
    }

    @Override
    public short getConditionType() {
        return 4;
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
        return this.specified;
    }

    @Override
    public boolean match(Element e, String pseudoE) {
        String val = this.getValue();
        if (val == null) {
            return !e.getAttribute(this.getLocalName()).equals("");
        }
        return e.getAttribute(this.getLocalName()).equals(val);
    }

    @Override
    public void fillAttributeSet(Set attrSet) {
        attrSet.add(this.localName);
    }

    public String toString() {
        if (this.value == null) {
            return '[' + this.localName + ']';
        }
        return '[' + this.localName + "=\"" + this.value + "\"]";
    }
}

