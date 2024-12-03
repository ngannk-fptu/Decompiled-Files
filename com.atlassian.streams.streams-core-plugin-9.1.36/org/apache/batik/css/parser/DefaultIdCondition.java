/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.parser;

import org.apache.batik.css.parser.AbstractAttributeCondition;

public class DefaultIdCondition
extends AbstractAttributeCondition {
    public DefaultIdCondition(String value) {
        super(value);
    }

    @Override
    public short getConditionType() {
        return 5;
    }

    @Override
    public String getNamespaceURI() {
        return null;
    }

    @Override
    public String getLocalName() {
        return "id";
    }

    @Override
    public boolean getSpecified() {
        return true;
    }

    public String toString() {
        return "#" + this.getValue();
    }
}

