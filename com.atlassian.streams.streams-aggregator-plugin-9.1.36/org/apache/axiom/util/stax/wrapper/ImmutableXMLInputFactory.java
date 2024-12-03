/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.wrapper;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.util.XMLEventAllocator;
import org.apache.axiom.util.stax.wrapper.XMLInputFactoryWrapper;

public class ImmutableXMLInputFactory
extends XMLInputFactoryWrapper {
    public ImmutableXMLInputFactory(XMLInputFactory parent) {
        super(parent);
    }

    public void setEventAllocator(XMLEventAllocator allocator) {
        throw new IllegalStateException("This factory is immutable");
    }

    public void setProperty(String name, Object value) throws IllegalArgumentException {
        throw new IllegalStateException("This factory is immutable");
    }

    public void setXMLReporter(XMLReporter reporter) {
        throw new IllegalStateException("This factory is immutable");
    }

    public void setXMLResolver(XMLResolver resolver) {
        throw new IllegalStateException("This factory is immutable");
    }
}

