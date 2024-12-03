/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.xml.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptionsImpl;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.xml.AbstractStaxBuilder;
import org.hibernate.validator.internal.xml.mapping.ClassLoadingHelper;
import org.hibernate.validator.internal.xml.mapping.ConstrainedParameterStaxBuilder;
import org.hibernate.validator.internal.xml.mapping.CrossParameterStaxBuilder;
import org.hibernate.validator.internal.xml.mapping.DefaultPackageStaxBuilder;
import org.hibernate.validator.internal.xml.mapping.ReturnValueStaxBuilder;

abstract class AbstractConstrainedExecutableElementStaxBuilder
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
    protected final List<ConstrainedParameterStaxBuilder> constrainedParameterStaxBuilders;
    private CrossParameterStaxBuilder crossParameterStaxBuilder;
    private ReturnValueStaxBuilder returnValueStaxBuilder;

    AbstractConstrainedExecutableElementStaxBuilder(ClassLoadingHelper classLoadingHelper, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, DefaultPackageStaxBuilder defaultPackageStaxBuilder, AnnotationProcessingOptionsImpl annotationProcessingOptions) {
        this.classLoadingHelper = classLoadingHelper;
        this.defaultPackageStaxBuilder = defaultPackageStaxBuilder;
        this.constraintHelper = constraintHelper;
        this.typeResolutionHelper = typeResolutionHelper;
        this.valueExtractorManager = valueExtractorManager;
        this.annotationProcessingOptions = annotationProcessingOptions;
        this.constrainedParameterStaxBuilders = new ArrayList<ConstrainedParameterStaxBuilder>();
    }

    abstract Optional<QName> getMainAttributeValueQname();

    @Override
    protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
        Optional<QName> mainAttributeValueQname = this.getMainAttributeValueQname();
        if (mainAttributeValueQname.isPresent()) {
            this.mainAttributeValue = this.readAttribute(xmlEvent.asStartElement(), mainAttributeValueQname.get()).get();
        }
        this.ignoreAnnotations = this.readAttribute(xmlEvent.asStartElement(), IGNORE_ANNOTATIONS_QNAME).map(Boolean::parseBoolean);
        ConstrainedParameterStaxBuilder constrainedParameterStaxBuilder = this.getNewConstrainedParameterStaxBuilder();
        ReturnValueStaxBuilder localReturnValueStaxBuilder = this.getNewReturnValueStaxBuilder();
        CrossParameterStaxBuilder localCrossParameterStaxBuilder = this.getNewCrossParameterStaxBuilder();
        while (!xmlEvent.isEndElement() || !xmlEvent.asEndElement().getName().getLocalPart().equals(this.getAcceptableQName())) {
            xmlEvent = xmlEventReader.nextEvent();
            if (constrainedParameterStaxBuilder.process(xmlEventReader, xmlEvent)) {
                this.constrainedParameterStaxBuilders.add(constrainedParameterStaxBuilder);
                constrainedParameterStaxBuilder = this.getNewConstrainedParameterStaxBuilder();
                continue;
            }
            if (localReturnValueStaxBuilder.process(xmlEventReader, xmlEvent)) {
                this.returnValueStaxBuilder = localReturnValueStaxBuilder;
                continue;
            }
            if (!localCrossParameterStaxBuilder.process(xmlEventReader, xmlEvent)) continue;
            this.crossParameterStaxBuilder = localCrossParameterStaxBuilder;
        }
    }

    private ConstrainedParameterStaxBuilder getNewConstrainedParameterStaxBuilder() {
        return new ConstrainedParameterStaxBuilder(this.classLoadingHelper, this.constraintHelper, this.typeResolutionHelper, this.valueExtractorManager, this.defaultPackageStaxBuilder, this.annotationProcessingOptions);
    }

    private CrossParameterStaxBuilder getNewCrossParameterStaxBuilder() {
        return new CrossParameterStaxBuilder(this.classLoadingHelper, this.constraintHelper, this.typeResolutionHelper, this.valueExtractorManager, this.defaultPackageStaxBuilder, this.annotationProcessingOptions);
    }

    private ReturnValueStaxBuilder getNewReturnValueStaxBuilder() {
        return new ReturnValueStaxBuilder(this.classLoadingHelper, this.constraintHelper, this.typeResolutionHelper, this.valueExtractorManager, this.defaultPackageStaxBuilder, this.annotationProcessingOptions);
    }

    public Optional<ReturnValueStaxBuilder> getReturnValueStaxBuilder() {
        return Optional.ofNullable(this.returnValueStaxBuilder);
    }

    public Optional<CrossParameterStaxBuilder> getCrossParameterStaxBuilder() {
        return Optional.ofNullable(this.crossParameterStaxBuilder);
    }
}

