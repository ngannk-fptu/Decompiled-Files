/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.xml.mapping;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptionsImpl;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.xml.AbstractStaxBuilder;
import org.hibernate.validator.internal.xml.mapping.ClassConstraintTypeStaxBuilder;
import org.hibernate.validator.internal.xml.mapping.ClassLoadingHelper;
import org.hibernate.validator.internal.xml.mapping.ConstrainedConstructorStaxBuilder;
import org.hibernate.validator.internal.xml.mapping.ConstrainedFieldStaxBuilder;
import org.hibernate.validator.internal.xml.mapping.ConstrainedGetterStaxBuilder;
import org.hibernate.validator.internal.xml.mapping.ConstrainedMethodStaxBuilder;
import org.hibernate.validator.internal.xml.mapping.DefaultPackageStaxBuilder;

class BeanStaxBuilder
extends AbstractStaxBuilder {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final QName IGNORE_ANNOTATIONS_QNAME = new QName("ignore-annotations");
    private static final QName CLASS_QNAME = new QName("class");
    private static final String BEAN_QNAME_LOCAL_PART = "bean";
    private final ClassLoadingHelper classLoadingHelper;
    private final ConstraintHelper constraintHelper;
    private final TypeResolutionHelper typeResolutionHelper;
    private final ValueExtractorManager valueExtractorManager;
    private final DefaultPackageStaxBuilder defaultPackageStaxBuilder;
    private final AnnotationProcessingOptionsImpl annotationProcessingOptions;
    private final Map<Class<?>, List<Class<?>>> defaultSequences;
    protected String className;
    protected Optional<Boolean> ignoreAnnotations;
    private ClassConstraintTypeStaxBuilder classConstraintTypeStaxBuilder;
    private final List<ConstrainedFieldStaxBuilder> constrainedFieldStaxBuilders;
    private final List<ConstrainedGetterStaxBuilder> constrainedGetterStaxBuilders;
    private final List<ConstrainedMethodStaxBuilder> constrainedMethodStaxBuilders;
    private final List<ConstrainedConstructorStaxBuilder> constrainedConstructorStaxBuilders;

    BeanStaxBuilder(ClassLoadingHelper classLoadingHelper, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, DefaultPackageStaxBuilder defaultPackageStaxBuilder, AnnotationProcessingOptionsImpl annotationProcessingOptions, Map<Class<?>, List<Class<?>>> defaultSequences) {
        this.classLoadingHelper = classLoadingHelper;
        this.defaultPackageStaxBuilder = defaultPackageStaxBuilder;
        this.constraintHelper = constraintHelper;
        this.typeResolutionHelper = typeResolutionHelper;
        this.valueExtractorManager = valueExtractorManager;
        this.annotationProcessingOptions = annotationProcessingOptions;
        this.defaultSequences = defaultSequences;
        this.constrainedFieldStaxBuilders = new ArrayList<ConstrainedFieldStaxBuilder>();
        this.constrainedGetterStaxBuilders = new ArrayList<ConstrainedGetterStaxBuilder>();
        this.constrainedMethodStaxBuilders = new ArrayList<ConstrainedMethodStaxBuilder>();
        this.constrainedConstructorStaxBuilders = new ArrayList<ConstrainedConstructorStaxBuilder>();
    }

    @Override
    protected String getAcceptableQName() {
        return BEAN_QNAME_LOCAL_PART;
    }

    @Override
    protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
        this.className = this.readAttribute(xmlEvent.asStartElement(), CLASS_QNAME).get();
        this.ignoreAnnotations = this.readAttribute(xmlEvent.asStartElement(), IGNORE_ANNOTATIONS_QNAME).map(Boolean::parseBoolean);
        ConstrainedFieldStaxBuilder fieldStaxBuilder = this.getNewConstrainedFieldStaxBuilder();
        ConstrainedGetterStaxBuilder getterStaxBuilder = this.getNewConstrainedGetterStaxBuilder();
        ConstrainedMethodStaxBuilder methodStaxBuilder = this.getNewConstrainedMethodStaxBuilder();
        ConstrainedConstructorStaxBuilder constructorStaxBuilder = this.getNewConstrainedConstructorStaxBuilder();
        ClassConstraintTypeStaxBuilder localClassConstraintTypeStaxBuilder = new ClassConstraintTypeStaxBuilder(this.classLoadingHelper, this.constraintHelper, this.typeResolutionHelper, this.valueExtractorManager, this.defaultPackageStaxBuilder, this.annotationProcessingOptions, this.defaultSequences);
        while (!xmlEvent.isEndElement() || !xmlEvent.asEndElement().getName().getLocalPart().equals(this.getAcceptableQName())) {
            xmlEvent = xmlEventReader.nextEvent();
            if (fieldStaxBuilder.process(xmlEventReader, xmlEvent)) {
                this.constrainedFieldStaxBuilders.add(fieldStaxBuilder);
                fieldStaxBuilder = this.getNewConstrainedFieldStaxBuilder();
                continue;
            }
            if (getterStaxBuilder.process(xmlEventReader, xmlEvent)) {
                this.constrainedGetterStaxBuilders.add(getterStaxBuilder);
                getterStaxBuilder = this.getNewConstrainedGetterStaxBuilder();
                continue;
            }
            if (methodStaxBuilder.process(xmlEventReader, xmlEvent)) {
                this.constrainedMethodStaxBuilders.add(methodStaxBuilder);
                methodStaxBuilder = this.getNewConstrainedMethodStaxBuilder();
                continue;
            }
            if (constructorStaxBuilder.process(xmlEventReader, xmlEvent)) {
                this.constrainedConstructorStaxBuilders.add(constructorStaxBuilder);
                constructorStaxBuilder = this.getNewConstrainedConstructorStaxBuilder();
                continue;
            }
            if (!localClassConstraintTypeStaxBuilder.process(xmlEventReader, xmlEvent)) continue;
            this.classConstraintTypeStaxBuilder = localClassConstraintTypeStaxBuilder;
        }
    }

    private ConstrainedFieldStaxBuilder getNewConstrainedFieldStaxBuilder() {
        return new ConstrainedFieldStaxBuilder(this.classLoadingHelper, this.constraintHelper, this.typeResolutionHelper, this.valueExtractorManager, this.defaultPackageStaxBuilder, this.annotationProcessingOptions);
    }

    private ConstrainedGetterStaxBuilder getNewConstrainedGetterStaxBuilder() {
        return new ConstrainedGetterStaxBuilder(this.classLoadingHelper, this.constraintHelper, this.typeResolutionHelper, this.valueExtractorManager, this.defaultPackageStaxBuilder, this.annotationProcessingOptions);
    }

    private ConstrainedMethodStaxBuilder getNewConstrainedMethodStaxBuilder() {
        return new ConstrainedMethodStaxBuilder(this.classLoadingHelper, this.constraintHelper, this.typeResolutionHelper, this.valueExtractorManager, this.defaultPackageStaxBuilder, this.annotationProcessingOptions);
    }

    private ConstrainedConstructorStaxBuilder getNewConstrainedConstructorStaxBuilder() {
        return new ConstrainedConstructorStaxBuilder(this.classLoadingHelper, this.constraintHelper, this.typeResolutionHelper, this.valueExtractorManager, this.defaultPackageStaxBuilder, this.annotationProcessingOptions);
    }

    void build(Set<Class<?>> processedClasses, Map<Class<?>, Set<ConstrainedElement>> constrainedElementsByType) {
        Class<?> beanClass = this.classLoadingHelper.loadClass(this.className, this.defaultPackageStaxBuilder.build().orElse(""));
        this.checkClassHasNotBeenProcessed(processedClasses, beanClass);
        this.annotationProcessingOptions.ignoreAnnotationConstraintForClass(beanClass, this.ignoreAnnotations.orElse(true));
        if (this.classConstraintTypeStaxBuilder != null) {
            this.addConstrainedElements(constrainedElementsByType, beanClass, Collections.singleton(this.classConstraintTypeStaxBuilder.build(beanClass)));
        }
        ArrayList alreadyProcessedFieldNames = new ArrayList(this.constrainedFieldStaxBuilders.size());
        this.addConstrainedElements(constrainedElementsByType, beanClass, this.constrainedFieldStaxBuilders.stream().map(builder -> builder.build(beanClass, alreadyProcessedFieldNames)).collect(Collectors.toList()));
        ArrayList alreadyProcessedGetterNames = new ArrayList(this.constrainedGetterStaxBuilders.size());
        this.addConstrainedElements(constrainedElementsByType, beanClass, this.constrainedGetterStaxBuilders.stream().map(builder -> builder.build(beanClass, alreadyProcessedGetterNames)).collect(Collectors.toList()));
        ArrayList alreadyProcessedMethods = new ArrayList(this.constrainedMethodStaxBuilders.size());
        this.addConstrainedElements(constrainedElementsByType, beanClass, this.constrainedMethodStaxBuilders.stream().map(builder -> builder.build(beanClass, alreadyProcessedMethods)).collect(Collectors.toList()));
        ArrayList alreadyProcessedConstructors = new ArrayList(this.constrainedConstructorStaxBuilders.size());
        this.addConstrainedElements(constrainedElementsByType, beanClass, this.constrainedConstructorStaxBuilders.stream().map(builder -> builder.build(beanClass, alreadyProcessedConstructors)).collect(Collectors.toList()));
    }

    private void addConstrainedElements(Map<Class<?>, Set<ConstrainedElement>> constrainedElementsbyType, Class<?> beanClass, Collection<? extends ConstrainedElement> newConstrainedElements) {
        if (constrainedElementsbyType.containsKey(beanClass)) {
            Set<ConstrainedElement> existingConstrainedElements = constrainedElementsbyType.get(beanClass);
            for (ConstrainedElement constrainedElement : newConstrainedElements) {
                if (!existingConstrainedElements.contains(constrainedElement)) continue;
                throw LOG.getConstrainedElementConfiguredMultipleTimesException(constrainedElement.toString());
            }
            existingConstrainedElements.addAll(newConstrainedElements);
        } else {
            HashSet tmpSet = CollectionHelper.newHashSet();
            tmpSet.addAll(newConstrainedElements);
            constrainedElementsbyType.put(beanClass, tmpSet);
        }
    }

    private void checkClassHasNotBeenProcessed(Set<Class<?>> processedClasses, Class<?> beanClass) {
        if (processedClasses.contains(beanClass)) {
            throw LOG.getBeanClassHasAlreadyBeenConfiguredInXmlException(beanClass);
        }
        processedClasses.add(beanClass);
    }
}

