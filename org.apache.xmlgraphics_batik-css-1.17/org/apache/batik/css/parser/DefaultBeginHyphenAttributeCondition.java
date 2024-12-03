/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.parser;

import org.apache.batik.css.parser.DefaultAttributeCondition;

public class DefaultBeginHyphenAttributeCondition
extends DefaultAttributeCondition {
    public DefaultBeginHyphenAttributeCondition(String localName, String namespaceURI, boolean specified, String value) {
        super(localName, namespaceURI, specified, value);
    }

    @Override
    public short getConditionType() {
        return 8;
    }

    @Override
    public String toString() {
        return "[" + this.getLocalName() + "|=\"" + this.getValue() + "\"]";
    }
}

