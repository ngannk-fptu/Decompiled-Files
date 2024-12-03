/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage;

import com.atlassian.confluence.content.render.xhtml.ElementTransformer;
import com.atlassian.confluence.content.render.xhtml.XMLEventFactoryProvider;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;

public class StorageHtmlAnchorElementTransformer
implements ElementTransformer {
    private final XMLEventFactory xmlEventFactory;
    private final DarkFeaturesManager darkFeaturesManager;

    public StorageHtmlAnchorElementTransformer(XMLEventFactoryProvider xmlEventFactoryProvider, DarkFeaturesManager darkFeaturesManager) {
        this.xmlEventFactory = Objects.requireNonNull(xmlEventFactoryProvider).getXmlEventFactory();
        this.darkFeaturesManager = Objects.requireNonNull(darkFeaturesManager);
    }

    @Override
    public Set<QName> getHandledElementNames() {
        return Collections.singleton(new QName("http://www.w3.org/1999/xhtml", "a"));
    }

    @Override
    public StartElement transform(StartElement element) {
        if (this.darkFeaturesManager.getDarkFeatures().isFeatureEnabled("link.openInNewWindow")) {
            return element;
        }
        HashSet<Attribute> attributesToWrite = new HashSet<Attribute>();
        Iterator<Attribute> attributesIterator = element.getAttributes();
        while (attributesIterator.hasNext()) {
            Attribute attribute = attributesIterator.next();
            if (attribute.getName().equals(new QName("target"))) continue;
            attributesToWrite.add(attribute);
        }
        return this.xmlEventFactory.createStartElement(element.getName(), attributesToWrite.iterator(), element.getNamespaces());
    }

    @Override
    public EndElement transform(EndElement element) {
        return element;
    }
}

