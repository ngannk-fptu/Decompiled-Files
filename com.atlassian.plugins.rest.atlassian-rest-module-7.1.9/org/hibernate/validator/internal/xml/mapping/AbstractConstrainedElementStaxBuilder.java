/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.xml.mapping;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptionsImpl;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.xml.AbstractStaxBuilder;
import org.hibernate.validator.internal.xml.mapping.ClassLoadingHelper;
import org.hibernate.validator.internal.xml.mapping.ConstraintTypeStaxBuilder;
import org.hibernate.validator.internal.xml.mapping.ContainerElementTypeConfigurationBuilder;
import org.hibernate.validator.internal.xml.mapping.ContainerElementTypeStaxBuilder;
import org.hibernate.validator.internal.xml.mapping.DefaultPackageStaxBuilder;
import org.hibernate.validator.internal.xml.mapping.GroupConversionStaxBuilder;
import org.hibernate.validator.internal.xml.mapping.ValidStaxBuilder;

abstract class AbstractConstrainedElementStaxBuilder
extends AbstractStaxBuilder {
    private static final QName IGNORE_ANNOTATIONS_QNAME = new QName("ignore-annotations");
    protected final ClassLoadingHelper classLoadingHelper;
    protected final ConstraintHelper constraintHelper;
    protected final TypeResolutionHelper typeResolutionHelper;
    protected final ValueExtractorManager valueExtractorManager;
    protected final DefaultPackageStaxBuilder defaultPackageStaxBuilder;
    protected final AnnotationProcessingOptionsImpl annotationProcessingOptions;
    protected String mainAttributeValue;
    protected Optional<Boolean> ignoreAnnotations;
    protected final GroupConversionStaxBuilder groupConversionBuilder;
    protected final ValidStaxBuilder validStaxBuilder;
    protected final List<ConstraintTypeStaxBuilder> constraintTypeStaxBuilders;
    protected final ContainerElementTypeConfigurationBuilder containerElementTypeConfigurationBuilder;

    AbstractConstrainedElementStaxBuilder(ClassLoadingHelper classLoadingHelper, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, DefaultPackageStaxBuilder defaultPackageStaxBuilder, AnnotationProcessingOptionsImpl annotationProcessingOptions) {
        this.classLoadingHelper = classLoadingHelper;
        this.defaultPackageStaxBuilder = defaultPackageStaxBuilder;
        this.constraintHelper = constraintHelper;
        this.typeResolutionHelper = typeResolutionHelper;
        this.valueExtractorManager = valueExtractorManager;
        this.groupConversionBuilder = new GroupConversionStaxBuilder(classLoadingHelper, defaultPackageStaxBuilder);
        this.validStaxBuilder = new ValidStaxBuilder();
        this.containerElementTypeConfigurationBuilder = new ContainerElementTypeConfigurationBuilder();
        this.annotationProcessingOptions = annotationProcessingOptions;
        this.constraintTypeStaxBuilders = new ArrayList<ConstraintTypeStaxBuilder>();
    }

    abstract Optional<QName> getMainAttributeValueQname();

    @Override
    protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
        Optional<QName> mainAttributeValueQname = this.getMainAttributeValueQname();
        if (mainAttributeValueQname.isPresent()) {
            this.mainAttributeValue = this.readAttribute(xmlEvent.asStartElement(), mainAttributeValueQname.get()).get();
        }
        this.ignoreAnnotations = this.readAttribute(xmlEvent.asStartElement(), IGNORE_ANNOTATIONS_QNAME).map(Boolean::parseBoolean);
        ConstraintTypeStaxBuilder constraintTypeStaxBuilder = this.getNewConstraintTypeStaxBuilder();
        ContainerElementTypeStaxBuilder containerElementTypeStaxBuilder = this.getNewContainerElementTypeStaxBuilder();
        while (!xmlEvent.isEndElement() || !xmlEvent.asEndElement().getName().getLocalPart().equals(this.getAcceptableQName())) {
            xmlEvent = xmlEventReader.nextEvent();
            this.validStaxBuilder.process(xmlEventReader, xmlEvent);
            this.groupConversionBuilder.process(xmlEventReader, xmlEvent);
            if (constraintTypeStaxBuilder.process(xmlEventReader, xmlEvent)) {
                this.constraintTypeStaxBuilders.add(constraintTypeStaxBuilder);
                constraintTypeStaxBuilder = this.getNewConstraintTypeStaxBuilder();
            }
            if (!containerElementTypeStaxBuilder.process(xmlEventReader, xmlEvent)) continue;
            this.containerElementTypeConfigurationBuilder.add(containerElementTypeStaxBuilder);
            containerElementTypeStaxBuilder = this.getNewContainerElementTypeStaxBuilder();
        }
    }

    private ConstraintTypeStaxBuilder getNewConstraintTypeStaxBuilder() {
        return new ConstraintTypeStaxBuilder(this.classLoadingHelper, this.constraintHelper, this.typeResolutionHelper, this.valueExtractorManager, this.defaultPackageStaxBuilder);
    }

    private ContainerElementTypeStaxBuilder getNewContainerElementTypeStaxBuilder() {
        return new ContainerElementTypeStaxBuilder(this.classLoadingHelper, this.constraintHelper, this.typeResolutionHelper, this.valueExtractorManager, this.defaultPackageStaxBuilder);
    }

    protected ContainerElementTypeConfigurationBuilder.ContainerElementTypeConfiguration getContainerElementTypeConfiguration(Type type, ConstraintLocation constraintLocation) {
        return this.containerElementTypeConfigurationBuilder.build(constraintLocation, type);
    }

    protected CascadingMetaDataBuilder getCascadingMetaData(Map<TypeVariable<?>, CascadingMetaDataBuilder> containerElementTypesCascadingMetaData, Type type) {
        return CascadingMetaDataBuilder.annotatedObject(type, this.validStaxBuilder.build(), containerElementTypesCascadingMetaData, this.groupConversionBuilder.build());
    }
}

