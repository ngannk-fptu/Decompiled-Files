/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.parser;

import org.apache.batik.css.parser.DefaultAttributeCondition;

public class DefaultClassCondition
extends DefaultAttributeCondition {
    public DefaultClassCondition(String namespaceURI, String value) {
        super("class", namespaceURI, true, value);
    }

    @Override
    public short getConditionType() {
        return 9;
    }

    @Override
    public String toString() {
        return "." + this.getValue();
    }
}

