/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.sac;

import org.apache.batik.css.engine.sac.CSSAttributeCondition;
import org.w3c.dom.Element;

public class CSSBeginHyphenAttributeCondition
extends CSSAttributeCondition {
    public CSSBeginHyphenAttributeCondition(String localName, String namespaceURI, boolean specified, String value) {
        super(localName, namespaceURI, specified, value);
    }

    @Override
    public short getConditionType() {
        return 8;
    }

    @Override
    public boolean match(Element e, String pseudoE) {
        return e.getAttribute(this.getLocalName()).startsWith(this.getValue());
    }

    @Override
    public String toString() {
        return '[' + this.getLocalName() + "|=\"" + this.getValue() + "\"]";
    }
}

