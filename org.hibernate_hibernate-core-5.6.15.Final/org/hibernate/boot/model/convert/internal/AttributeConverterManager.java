/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.annotations.common.reflection.XProperty
 *  org.jboss.logging.Logger
 */
package org.hibernate.boot.model.convert.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.hibernate.AssertionFailure;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.boot.model.convert.spi.AutoApplicableConverterDescriptor;
import org.hibernate.boot.model.convert.spi.ConverterAutoApplyHandler;
import org.hibernate.boot.model.convert.spi.ConverterDescriptor;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.internal.util.StringHelper;
import org.jboss.logging.Logger;

public class AttributeConverterManager
implements ConverterAutoApplyHandler {
    private static final Logger log = Logger.getLogger(AttributeConverterManager.class);
    private Map<Class, ConverterDescriptor> attributeConverterDescriptorsByClass;
    private static StringHelper.Renderer<ConverterDescriptor> RENDERER = value -> value.getAttributeConverterClass().getName();

    public void addConverter(ConverterDescriptor descriptor) {
        ConverterDescriptor old;
        if (this.attributeConverterDescriptorsByClass == null) {
            this.attributeConverterDescriptorsByClass = new ConcurrentHashMap<Class, ConverterDescriptor>();
        }
        if ((old = this.attributeConverterDescriptorsByClass.put(descriptor.getAttributeConverterClass(), descriptor)) != null) {
            throw new AssertionFailure(String.format(Locale.ENGLISH, "AttributeConverter class [%s] registered multiple times", descriptor.getAttributeConverterClass()));
        }
    }

    private Collection<ConverterDescriptor> converterDescriptors() {
        if (this.attributeConverterDescriptorsByClass == null) {
            return Collections.emptyList();
        }
        return this.attributeConverterDescriptorsByClass.values();
    }

    @Override
    public ConverterDescriptor findAutoApplyConverterForAttribute(XProperty xProperty, MetadataBuildingContext context) {
        return this.locateMatchingConverter(xProperty, ConversionSite.ATTRIBUTE, autoApplyDescriptor -> autoApplyDescriptor.getAutoAppliedConverterDescriptorForAttribute(xProperty, context));
    }

    private ConverterDescriptor locateMatchingConverter(XProperty xProperty, ConversionSite conversionSite, Function<AutoApplicableConverterDescriptor, ConverterDescriptor> matcher) {
        ArrayList<ConverterDescriptor> matches = new ArrayList<ConverterDescriptor>();
        for (ConverterDescriptor descriptor : this.converterDescriptors()) {
            log.debugf("Checking auto-apply AttributeConverter [%s] (domain-type=%s) for match against %s : %s.%s (type=%s)", new Object[]{descriptor.getAttributeConverterClass().getName(), descriptor.getDomainValueResolvedType().getSignature(), conversionSite.getSiteDescriptor(), xProperty.getDeclaringClass().getName(), xProperty.getName(), xProperty.getType().getName()});
            ConverterDescriptor match = matcher.apply(descriptor.getAutoApplyDescriptor());
            if (match == null) continue;
            matches.add(descriptor);
        }
        if (matches.isEmpty()) {
            return null;
        }
        if (matches.size() == 1) {
            return (ConverterDescriptor)matches.get(0);
        }
        throw new RuntimeException(String.format(Locale.ROOT, "Multiple auto-apply converters matched %s [%s.%s] : %s", conversionSite.getSiteDescriptor(), xProperty.getDeclaringClass().getName(), xProperty.getName(), StringHelper.join(matches, RENDERER)));
    }

    @Override
    public ConverterDescriptor findAutoApplyConverterForCollectionElement(XProperty xProperty, MetadataBuildingContext context) {
        return this.locateMatchingConverter(xProperty, ConversionSite.COLLECTION_ELEMENT, autoApplyDescriptor -> autoApplyDescriptor.getAutoAppliedConverterDescriptorForCollectionElement(xProperty, context));
    }

    @Override
    public ConverterDescriptor findAutoApplyConverterForMapKey(XProperty xProperty, MetadataBuildingContext context) {
        return this.locateMatchingConverter(xProperty, ConversionSite.MAP_KEY, autoApplyDescriptor -> autoApplyDescriptor.getAutoAppliedConverterDescriptorForMapKey(xProperty, context));
    }

    static enum ConversionSite {
        ATTRIBUTE("basic attribute"),
        COLLECTION_ELEMENT("collection attribute's element"),
        MAP_KEY("map attribute's key");

        private final String siteDescriptor;

        private ConversionSite(String siteDescriptor) {
            this.siteDescriptor = siteDescriptor;
        }

        public String getSiteDescriptor() {
            return this.siteDescriptor;
        }
    }
}

