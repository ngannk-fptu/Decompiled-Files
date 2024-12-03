/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.groups.Default
 */
package org.hibernate.validator.internal.xml.mapping;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.groups.Default;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.xml.AbstractStaxBuilder;
import org.hibernate.validator.internal.xml.mapping.ClassLoadingHelper;
import org.hibernate.validator.internal.xml.mapping.DefaultPackageStaxBuilder;

class GroupConversionStaxBuilder
extends AbstractStaxBuilder {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final String GROUP_CONVERSION_TYPE_QNAME_LOCAL_PART = "convert-group";
    private static final QName FROM_QNAME = new QName("from");
    private static final QName TO_QNAME = new QName("to");
    private static final String DEFAULT_GROUP_NAME = Default.class.getName();
    private final ClassLoadingHelper classLoadingHelper;
    private final DefaultPackageStaxBuilder defaultPackageStaxBuilder;
    private final Map<String, List<String>> groupConversionRules;

    GroupConversionStaxBuilder(ClassLoadingHelper classLoadingHelper, DefaultPackageStaxBuilder defaultPackageStaxBuilder) {
        this.classLoadingHelper = classLoadingHelper;
        this.defaultPackageStaxBuilder = defaultPackageStaxBuilder;
        this.groupConversionRules = new HashMap<String, List<String>>();
    }

    @Override
    protected String getAcceptableQName() {
        return GROUP_CONVERSION_TYPE_QNAME_LOCAL_PART;
    }

    @Override
    protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) {
        StartElement startElement = xmlEvent.asStartElement();
        String from = this.readAttribute(startElement, FROM_QNAME).orElse(DEFAULT_GROUP_NAME);
        String to = this.readAttribute(startElement, TO_QNAME).get();
        this.groupConversionRules.merge(from, Collections.singletonList(to), (v1, v2) -> Stream.concat(v1.stream(), v2.stream()).collect(Collectors.toList()));
    }

    Map<Class<?>, Class<?>> build() {
        String defaultPackage = this.defaultPackageStaxBuilder.build().orElse("");
        Map<Class, List> resultingMapping = this.groupConversionRules.entrySet().stream().collect(Collectors.groupingBy(entry -> this.classLoadingHelper.loadClass((String)entry.getKey(), defaultPackage), Collectors.collectingAndThen(Collectors.toList(), entries -> entries.stream().flatMap(entry -> ((List)entry.getValue()).stream()).map(className -> this.classLoadingHelper.loadClass((String)className, defaultPackage)).collect(Collectors.toList()))));
        for (Map.Entry<Class, List> entry2 : resultingMapping.entrySet()) {
            if (entry2.getValue().size() <= 1) continue;
            throw LOG.getMultipleGroupConversionsForSameSourceException(entry2.getKey(), entry2.getValue());
        }
        return resultingMapping.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> (Class)((List)entry.getValue()).get(0)));
    }
}

