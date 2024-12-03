/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.wrapper;

import javax.xml.stream.XMLOutputFactory;
import org.apache.axiom.util.stax.wrapper.XMLOutputFactoryWrapper;

public class ImmutableXMLOutputFactory
extends XMLOutputFactoryWrapper {
    public ImmutableXMLOutputFactory(XMLOutputFactory parent) {
        super(parent);
    }

    public void setProperty(String name, Object value) throws IllegalArgumentException {
        throw new IllegalStateException("This factory is immutable");
    }
}

