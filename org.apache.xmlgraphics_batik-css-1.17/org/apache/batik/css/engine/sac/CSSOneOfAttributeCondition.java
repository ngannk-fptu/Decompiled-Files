/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.sac;

import org.apache.batik.css.engine.sac.CSSAttributeCondition;
import org.w3c.dom.Element;

public class CSSOneOfAttributeCondition
extends CSSAttributeCondition {
    public CSSOneOfAttributeCondition(String localName, String namespaceURI, boolean specified, String value) {
        super(localName, namespaceURI, specified, value);
    }

    @Override
    public short getConditionType() {
        return 7;
    }

    @Override
    public boolean match(Element e, String pseudoE) {
        String val;
        String attr = e.getAttribute(this.getLocalName());
        int i = attr.indexOf(val = this.getValue());
        if (i == -1) {
            return false;
        }
        if (i != 0 && !Character.isSpaceChar(attr.charAt(i - 1))) {
            return false;
        }
        int j = i + val.length();
        return j == attr.length() || j < attr.length() && Character.isSpaceChar(attr.charAt(j));
    }

    @Override
    public String toString() {
        return "[" + this.getLocalName() + "~=\"" + this.getValue() + "\"]";
    }
}

