/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.xml.mapping;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.XMLEvent;
import org.hibernate.validator.internal.xml.AbstractStaxBuilder;

class ValidStaxBuilder
extends AbstractStaxBuilder {
    private static final String VALID_QNAME_LOCAL_PART = "valid";
    private Boolean cascading;

    ValidStaxBuilder() {
    }

    @Override
    protected String getAcceptableQName() {
        return VALID_QNAME_LOCAL_PART;
    }

    @Override
    protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) {
        this.cascading = true;
    }

    public boolean build() {
        return this.cascading != null;
    }
}

