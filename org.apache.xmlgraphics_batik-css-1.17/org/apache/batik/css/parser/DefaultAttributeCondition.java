/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.parser;

import org.apache.batik.css.parser.AbstractAttributeCondition;

public class DefaultAttributeCondition
extends AbstractAttributeCondition {
    protected String localName;
    protected String namespaceURI;
    protected boolean specified;

    public DefaultAttributeCondition(String localName, String namespaceURI, boolean specified, String value) {
        super(value);
        this.localName = localName;
        this.namespaceURI = namespaceURI;
        this.specified = specified;
    }

    public short getConditionType() {
        return 4;
    }

    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    public String getLocalName() {
        return this.localName;
    }

    public boolean getSpecified() {
        return this.specified;
    }

    public String toString() {
        if (this.value == null) {
            return "[" + this.localName + "]";
        }
        return "[" + this.localName + "=\"" + this.value + "\"]";
    }
}

