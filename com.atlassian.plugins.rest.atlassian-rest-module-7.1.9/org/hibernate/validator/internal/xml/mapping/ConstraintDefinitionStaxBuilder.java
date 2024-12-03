/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 */
package org.hibernate.validator.internal.xml.mapping;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintValidator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorDescriptor;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.xml.AbstractStaxBuilder;
import org.hibernate.validator.internal.xml.mapping.ClassLoadingHelper;
import org.hibernate.validator.internal.xml.mapping.DefaultPackageStaxBuilder;

class ConstraintDefinitionStaxBuilder
extends AbstractStaxBuilder {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final String CONSTRAINT_DEFINITION_QNAME_LOCAL_PART = "constraint-definition";
    private static final QName ANNOTATION_QNAME = new QName("annotation");
    private final ClassLoadingHelper classLoadingHelper;
    private final ConstraintHelper constraintHelper;
    private final DefaultPackageStaxBuilder defaultPackageStaxBuilder;
    private String annotation;
    private ValidatedByStaxBuilder validatedByStaxBuilder;

    ConstraintDefinitionStaxBuilder(ClassLoadingHelper classLoadingHelper, ConstraintHelper constraintHelper, DefaultPackageStaxBuilder defaultPackageStaxBuilder) {
        this.classLoadingHelper = classLoadingHelper;
        this.constraintHelper = constraintHelper;
        this.defaultPackageStaxBuilder = defaultPackageStaxBuilder;
        this.validatedByStaxBuilder = new ValidatedByStaxBuilder(classLoadingHelper, defaultPackageStaxBuilder);
    }

    @Override
    protected String getAcceptableQName() {
        return CONSTRAINT_DEFINITION_QNAME_LOCAL_PART;
    }

    @Override
    protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
        this.annotation = this.readAttribute(xmlEvent.asStartElement(), ANNOTATION_QNAME).get();
        while (!xmlEvent.isEndElement() || !xmlEvent.asEndElement().getName().getLocalPart().equals(this.getAcceptableQName())) {
            this.validatedByStaxBuilder.process(xmlEventReader, xmlEvent);
            xmlEvent = xmlEventReader.nextEvent();
        }
    }

    void build(Set<String> alreadyProcessedConstraintDefinitions) {
        this.checkProcessedAnnotations(alreadyProcessedConstraintDefinitions);
        String defaultPackage = this.defaultPackageStaxBuilder.build().orElse("");
        Class<?> clazz = this.classLoadingHelper.loadClass(this.annotation, defaultPackage);
        if (!clazz.isAnnotation()) {
            throw LOG.getIsNotAnAnnotationException(clazz);
        }
        Class<?> annotationClass = clazz;
        this.addValidatorDefinitions(annotationClass);
    }

    private void checkProcessedAnnotations(Set<String> alreadyProcessedConstraintDefinitions) {
        if (alreadyProcessedConstraintDefinitions.contains(this.annotation)) {
            throw LOG.getOverridingConstraintDefinitionsInMultipleMappingFilesException(this.annotation);
        }
        alreadyProcessedConstraintDefinitions.add(this.annotation);
    }

    private <A extends Annotation> void addValidatorDefinitions(Class<A> annotationClass) {
        this.constraintHelper.putValidatorDescriptors(annotationClass, this.validatedByStaxBuilder.build(annotationClass), this.validatedByStaxBuilder.isIncludeExistingValidators());
    }

    private static class ValidatedByStaxBuilder
    extends AbstractStaxBuilder {
        private static final String VALIDATED_BY_QNAME_LOCAL_PART = "validated-by";
        private static final String VALUE_QNAME_LOCAL_PART = "value";
        private static final QName INCLUDE_EXISTING_VALIDATORS_QNAME = new QName("include-existing-validators");
        private final ClassLoadingHelper classLoadingHelper;
        private final DefaultPackageStaxBuilder defaultPackageStaxBuilder;
        private boolean includeExistingValidators;
        private final List<String> values;

        protected ValidatedByStaxBuilder(ClassLoadingHelper classLoadingHelper, DefaultPackageStaxBuilder defaultPackageStaxBuilder) {
            this.classLoadingHelper = classLoadingHelper;
            this.defaultPackageStaxBuilder = defaultPackageStaxBuilder;
            this.values = new ArrayList<String>();
        }

        @Override
        protected String getAcceptableQName() {
            return VALIDATED_BY_QNAME_LOCAL_PART;
        }

        @Override
        protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
            this.includeExistingValidators = this.readAttribute(xmlEvent.asStartElement(), INCLUDE_EXISTING_VALIDATORS_QNAME).map(Boolean::parseBoolean).orElse(true);
            while (!xmlEvent.isEndElement() || !xmlEvent.asEndElement().getName().getLocalPart().equals(this.getAcceptableQName())) {
                if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equals(VALUE_QNAME_LOCAL_PART)) {
                    this.values.add(this.readSingleElement(xmlEventReader));
                }
                xmlEvent = xmlEventReader.nextEvent();
            }
        }

        <A extends Annotation> List<ConstraintValidatorDescriptor<A>> build(Class<A> annotation) {
            String defaultPackage = this.defaultPackageStaxBuilder.build().orElse("");
            return this.values.stream().map(value -> this.classLoadingHelper.loadClass((String)value, defaultPackage)).peek(this::checkValidatorAssignability).map(clazz -> clazz).map(validatorClass -> ConstraintValidatorDescriptor.forClass(validatorClass, annotation)).collect(Collectors.toList());
        }

        public boolean isIncludeExistingValidators() {
            return this.includeExistingValidators;
        }

        private void checkValidatorAssignability(Class<?> validatorClass) {
            if (!ConstraintValidator.class.isAssignableFrom(validatorClass)) {
                throw LOG.getIsNotAConstraintValidatorClassException(validatorClass);
            }
        }
    }
}

