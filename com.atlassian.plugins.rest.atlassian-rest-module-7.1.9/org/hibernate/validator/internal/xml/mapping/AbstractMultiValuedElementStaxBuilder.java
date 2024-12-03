/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.xml.mapping;

import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.hibernate.validator.internal.xml.AbstractStaxBuilder;
import org.hibernate.validator.internal.xml.mapping.ClassLoadingHelper;
import org.hibernate.validator.internal.xml.mapping.DefaultPackageStaxBuilder;

abstract class AbstractMultiValuedElementStaxBuilder
extends AbstractStaxBuilder {
    private static final String VALUE_QNAME_LOCAL_PART = "value";
    private static final Class<?>[] EMPTY_CLASSES_ARRAY = new Class[0];
    private final ClassLoadingHelper classLoadingHelper;
    private final DefaultPackageStaxBuilder defaultPackageStaxBuilder;
    private final List<String> values;

    protected AbstractMultiValuedElementStaxBuilder(ClassLoadingHelper classLoadingHelper, DefaultPackageStaxBuilder defaultPackageStaxBuilder) {
        this.classLoadingHelper = classLoadingHelper;
        this.defaultPackageStaxBuilder = defaultPackageStaxBuilder;
        this.values = new ArrayList<String>();
    }

    @Override
    protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
        while (!xmlEvent.isEndElement() || !xmlEvent.asEndElement().getName().getLocalPart().equals(this.getAcceptableQName())) {
            xmlEvent = xmlEventReader.nextEvent();
            if (!xmlEvent.isStartElement() || !xmlEvent.asStartElement().getName().getLocalPart().equals(VALUE_QNAME_LOCAL_PART)) continue;
            this.values.add(this.readSingleElement(xmlEventReader));
        }
    }

    public Class<?>[] build() {
        String defaultPackage = this.defaultPackageStaxBuilder.build().orElse("");
        if (this.values.isEmpty()) {
            return EMPTY_CLASSES_ARRAY;
        }
        return (Class[])this.values.stream().map(valueClass -> this.classLoadingHelper.loadClass((String)valueClass, defaultPackage)).peek(this::verifyClass).toArray(Class[]::new);
    }

    public abstract void verifyClass(Class<?> var1);
}

