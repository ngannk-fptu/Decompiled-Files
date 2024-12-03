/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.parser;

import org.apache.batik.css.parser.AbstractAttributeCondition;

public class DefaultPseudoClassCondition
extends AbstractAttributeCondition {
    protected String namespaceURI;

    public DefaultPseudoClassCondition(String namespaceURI, String value) {
        super(value);
        this.namespaceURI = namespaceURI;
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

    public String toString() {
        return ":" + this.getValue();
    }
}

