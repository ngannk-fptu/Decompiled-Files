/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.Payload
 *  javax.validation.ValidationException
 */
package org.hibernate.validator.internal.xml.mapping;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.Payload;
import javax.validation.ValidationException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.core.MetaConstraints;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.annotation.AnnotationDescriptor;
import org.hibernate.validator.internal.util.annotation.ConstraintAnnotationDescriptor;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.privilegedactions.GetMethod;
import org.hibernate.validator.internal.xml.AbstractStaxBuilder;
import org.hibernate.validator.internal.xml.mapping.AbstractMultiValuedElementStaxBuilder;
import org.hibernate.validator.internal.xml.mapping.AbstractOneLineStringStaxBuilder;
import org.hibernate.validator.internal.xml.mapping.ClassLoadingHelper;
import org.hibernate.validator.internal.xml.mapping.DefaultPackageStaxBuilder;

class ConstraintTypeStaxBuilder
extends AbstractStaxBuilder {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final Pattern IS_ONLY_WHITESPACE = Pattern.compile("\\s*");
    private static final String CONSTRAINT_QNAME_LOCAL_PART = "constraint";
    private static final QName CONSTRAINT_ANNOTATION_QNAME = new QName("annotation");
    private final ClassLoadingHelper classLoadingHelper;
    private final ConstraintHelper constraintHelper;
    private final TypeResolutionHelper typeResolutionHelper;
    private final ValueExtractorManager valueExtractorManager;
    private final DefaultPackageStaxBuilder defaultPackageStaxBuilder;
    private final GroupsStaxBuilder groupsStaxBuilder;
    private final PayloadStaxBuilder payloadStaxBuilder;
    private final ConstraintParameterStaxBuilder constrainParameterStaxBuilder;
    private final MessageStaxBuilder messageStaxBuilder;
    private final List<AbstractStaxBuilder> builders;
    private String constraintAnnotation;

    ConstraintTypeStaxBuilder(ClassLoadingHelper classLoadingHelper, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, DefaultPackageStaxBuilder defaultPackageStaxBuilder) {
        this.classLoadingHelper = classLoadingHelper;
        this.defaultPackageStaxBuilder = defaultPackageStaxBuilder;
        this.constraintHelper = constraintHelper;
        this.typeResolutionHelper = typeResolutionHelper;
        this.valueExtractorManager = valueExtractorManager;
        this.groupsStaxBuilder = new GroupsStaxBuilder(classLoadingHelper, defaultPackageStaxBuilder);
        this.payloadStaxBuilder = new PayloadStaxBuilder(classLoadingHelper, defaultPackageStaxBuilder);
        this.constrainParameterStaxBuilder = new ConstraintParameterStaxBuilder(classLoadingHelper, defaultPackageStaxBuilder);
        this.messageStaxBuilder = new MessageStaxBuilder();
        this.builders = Stream.of(this.groupsStaxBuilder, this.payloadStaxBuilder, this.constrainParameterStaxBuilder, this.messageStaxBuilder).collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    @Override
    protected String getAcceptableQName() {
        return CONSTRAINT_QNAME_LOCAL_PART;
    }

    @Override
    protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
        StartElement startElement = xmlEvent.asStartElement();
        this.constraintAnnotation = this.readAttribute(startElement, CONSTRAINT_ANNOTATION_QNAME).get();
        while (!xmlEvent.isEndElement() || !xmlEvent.asEndElement().getName().getLocalPart().equals(CONSTRAINT_QNAME_LOCAL_PART)) {
            XMLEvent currentEvent = xmlEvent;
            this.builders.forEach(builder -> builder.process(xmlEventReader, currentEvent));
            xmlEvent = xmlEventReader.nextEvent();
        }
    }

    <A extends Annotation> MetaConstraint<A> build(ConstraintLocation constraintLocation, ElementType type, ConstraintDescriptorImpl.ConstraintType constraintType) {
        AnnotationDescriptor annotationDescriptor;
        Class<?> annotationClass;
        String defaultPackage = this.defaultPackageStaxBuilder.build().orElse("");
        try {
            annotationClass = this.classLoadingHelper.loadClass(this.constraintAnnotation, defaultPackage);
        }
        catch (ValidationException e) {
            throw LOG.getUnableToLoadConstraintAnnotationClassException(this.constraintAnnotation, (Exception)((Object)e));
        }
        ConstraintAnnotationDescriptor.Builder annotationDescriptorBuilder = new ConstraintAnnotationDescriptor.Builder(annotationClass);
        Optional<String> message = this.messageStaxBuilder.build();
        if (message.isPresent()) {
            annotationDescriptorBuilder.setMessage(message.get());
        }
        annotationDescriptorBuilder.setGroups(this.groupsStaxBuilder.build()).setPayload(this.payloadStaxBuilder.build());
        Map<String, Object> parameters = this.constrainParameterStaxBuilder.build(annotationClass);
        for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
            annotationDescriptorBuilder.setAttribute(parameter.getKey(), parameter.getValue());
        }
        try {
            annotationDescriptor = annotationDescriptorBuilder.build();
        }
        catch (RuntimeException e) {
            throw LOG.getUnableToCreateAnnotationForConfiguredConstraintException(e);
        }
        ConstraintDescriptorImpl constraintDescriptor = new ConstraintDescriptorImpl(this.constraintHelper, constraintLocation.getMember(), annotationDescriptor, type, constraintType);
        return MetaConstraints.create(this.typeResolutionHelper, this.valueExtractorManager, constraintDescriptor, constraintLocation);
    }

    private static class PayloadStaxBuilder
    extends AbstractMultiValuedElementStaxBuilder {
        private static final String PAYLOAD_QNAME_LOCAL_PART = "payload";

        private PayloadStaxBuilder(ClassLoadingHelper classLoadingHelper, DefaultPackageStaxBuilder defaultPackageStaxBuilder) {
            super(classLoadingHelper, defaultPackageStaxBuilder);
        }

        @Override
        public void verifyClass(Class<?> payload) {
            if (!Payload.class.isAssignableFrom(payload)) {
                throw LOG.getWrongPayloadClassException(payload);
            }
        }

        @Override
        protected String getAcceptableQName() {
            return PAYLOAD_QNAME_LOCAL_PART;
        }
    }

    private static class GroupsStaxBuilder
    extends AbstractMultiValuedElementStaxBuilder {
        private static final String GROUPS_QNAME_LOCAL_PART = "groups";

        private GroupsStaxBuilder(ClassLoadingHelper classLoadingHelper, DefaultPackageStaxBuilder defaultPackageStaxBuilder) {
            super(classLoadingHelper, defaultPackageStaxBuilder);
        }

        @Override
        public void verifyClass(Class<?> clazz) {
        }

        @Override
        protected String getAcceptableQName() {
            return GROUPS_QNAME_LOCAL_PART;
        }
    }

    private static class AnnotationParameterStaxBuilder
    extends AbstractStaxBuilder {
        private static final String ANNOTATION_QNAME_LOCAL_PART = "annotation";
        private static final String ELEMENT_QNAME_LOCAL_PART = "element";
        private static final String VALUE_QNAME_LOCAL_PART = "value";
        private static final QName NAME_QNAME = new QName("name");
        private final ClassLoadingHelper classLoadingHelper;
        protected final DefaultPackageStaxBuilder defaultPackageStaxBuilder;
        protected Map<String, List<String>> parameters;
        protected Map<String, List<AnnotationParameterStaxBuilder>> annotationParameters;

        public AnnotationParameterStaxBuilder(ClassLoadingHelper classLoadingHelper, DefaultPackageStaxBuilder defaultPackageStaxBuilder) {
            this.classLoadingHelper = classLoadingHelper;
            this.defaultPackageStaxBuilder = defaultPackageStaxBuilder;
            this.parameters = new HashMap<String, List<String>>();
            this.annotationParameters = new HashMap<String, List<AnnotationParameterStaxBuilder>>();
        }

        @Override
        protected String getAcceptableQName() {
            return ANNOTATION_QNAME_LOCAL_PART;
        }

        @Override
        protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
            while (!xmlEvent.isEndElement() || !xmlEvent.asEndElement().getName().getLocalPart().equals(ANNOTATION_QNAME_LOCAL_PART)) {
                StartElement startElement;
                xmlEvent = xmlEventReader.nextEvent();
                if (!xmlEvent.isStartElement() || !(startElement = xmlEvent.asStartElement()).getName().getLocalPart().equals(ELEMENT_QNAME_LOCAL_PART)) continue;
                String name = this.readAttribute(xmlEvent.asStartElement(), NAME_QNAME).get();
                this.parameters.put(name, Collections.emptyList());
                while (!xmlEvent.isEndElement() || !xmlEvent.asEndElement().getName().getLocalPart().equals(ELEMENT_QNAME_LOCAL_PART)) {
                    this.readElement(xmlEventReader, xmlEvent, name);
                    xmlEvent = xmlEventReader.nextEvent();
                }
            }
        }

        protected void readElement(XMLEventReader xmlEventReader, XMLEvent xmlEvent, String name) throws XMLStreamException {
            if (xmlEvent.isCharacters() && !xmlEvent.asCharacters().getData().trim().isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder(xmlEvent.asCharacters().getData());
                while (xmlEventReader.peek().isCharacters()) {
                    xmlEvent = xmlEventReader.nextEvent();
                    stringBuilder.append(xmlEvent.asCharacters().getData());
                }
                this.addParameterValue(name, stringBuilder.toString().trim());
            } else if (xmlEvent.isStartElement()) {
                StartElement startElement = xmlEvent.asStartElement();
                if (startElement.getName().getLocalPart().equals(VALUE_QNAME_LOCAL_PART)) {
                    this.addParameterValue(name, this.readSingleElement(xmlEventReader));
                } else if (startElement.getName().getLocalPart().equals(ANNOTATION_QNAME_LOCAL_PART)) {
                    this.addAnnotationParameterValue(name, xmlEventReader, xmlEvent);
                }
            }
        }

        protected void addAnnotationParameterValue(String name, XMLEventReader xmlEventReader, XMLEvent xmlEvent) {
            this.checkNameIsValid(name);
            AnnotationParameterStaxBuilder annotationParameterStaxBuilder = new AnnotationParameterStaxBuilder(this.classLoadingHelper, this.defaultPackageStaxBuilder);
            annotationParameterStaxBuilder.process(xmlEventReader, xmlEvent);
            this.annotationParameters.merge(name, Collections.singletonList(annotationParameterStaxBuilder), (v1, v2) -> Stream.concat(v1.stream(), v2.stream()).collect(Collectors.toList()));
        }

        protected void addParameterValue(String name, String value) {
            this.checkNameIsValid(name);
            this.parameters.merge(name, Collections.singletonList(value), (v1, v2) -> Stream.concat(v1.stream(), v2.stream()).collect(Collectors.toList()));
        }

        protected void checkNameIsValid(String name) {
        }

        public <A extends Annotation> Annotation build(Class<A> annotationClass, String defaultPackage) {
            AnnotationDescriptor.Builder<Class<A>> annotationDescriptorBuilder = new AnnotationDescriptor.Builder<Class<A>>(annotationClass);
            for (Map.Entry<String, List<String>> entry : this.parameters.entrySet()) {
                annotationDescriptorBuilder.setAttribute(entry.getKey(), this.getElementValue(entry.getValue(), annotationClass, entry.getKey(), defaultPackage));
            }
            for (Map.Entry<String, List<Object>> entry : this.annotationParameters.entrySet()) {
                annotationDescriptorBuilder.setAttribute(entry.getKey(), this.getAnnotationElementValue(entry.getValue(), annotationClass, entry.getKey(), defaultPackage));
            }
            return annotationDescriptorBuilder.build().getAnnotation();
        }

        protected <A extends Annotation> Object getElementValue(List<String> parsedParameters, Class<A> annotationClass, String name, String defaultPackage) {
            List<String> parameters = AnnotationParameterStaxBuilder.removeEmptyContentElements(parsedParameters);
            Class<?> returnType = AnnotationParameterStaxBuilder.getAnnotationParameterType(annotationClass, name);
            boolean isArray = returnType.isArray();
            if (!isArray) {
                if (parameters.size() == 0) {
                    return "";
                }
                if (parameters.size() > 1) {
                    throw LOG.getAttemptToSpecifyAnArrayWhereSingleValueIsExpectedException();
                }
                return this.convertStringToReturnType(parameters.get(0), returnType, defaultPackage);
            }
            return parameters.stream().map(value -> this.convertStringToReturnType((String)value, returnType.getComponentType(), defaultPackage)).toArray(size -> (Object[])Array.newInstance(returnType.getComponentType(), size));
        }

        protected <A extends Annotation> Object getAnnotationElementValue(List<AnnotationParameterStaxBuilder> parameters, Class<A> annotationClass, String name, String defaultPackage) {
            Class<?> returnType = AnnotationParameterStaxBuilder.getAnnotationParameterType(annotationClass, name);
            boolean isArray = returnType.isArray();
            if (!isArray) {
                if (parameters.size() == 0) {
                    throw LOG.getEmptyElementOnlySupportedWhenCharSequenceIsExpectedExpection();
                }
                if (parameters.size() > 1) {
                    throw LOG.getAttemptToSpecifyAnArrayWhereSingleValueIsExpectedException();
                }
                return parameters.get(0).build(returnType, defaultPackage);
            }
            return parameters.stream().map(value -> value.build(returnType.getComponentType(), defaultPackage)).toArray(size -> (Object[])Array.newInstance(returnType.getComponentType(), size));
        }

        private static List<String> removeEmptyContentElements(List<String> params) {
            return params.stream().filter(content -> !IS_ONLY_WHITESPACE.matcher((CharSequence)content).matches()).collect(Collectors.toList());
        }

        private static <A extends Annotation> Class<?> getAnnotationParameterType(Class<A> annotationClass, String name) {
            Method m = AnnotationParameterStaxBuilder.run(GetMethod.action(annotationClass, name));
            if (m == null) {
                throw LOG.getAnnotationDoesNotContainAParameterException(annotationClass, name);
            }
            return m.getReturnType();
        }

        private Object convertStringToReturnType(String value, Class<?> returnType, String defaultPackage) {
            Class<?> returnValue;
            if (returnType == Byte.TYPE) {
                try {
                    returnValue = Byte.parseByte(value);
                }
                catch (NumberFormatException e) {
                    throw LOG.getInvalidNumberFormatException("byte", e);
                }
            }
            if (returnType == Short.TYPE) {
                try {
                    returnValue = Short.parseShort(value);
                }
                catch (NumberFormatException e) {
                    throw LOG.getInvalidNumberFormatException("short", e);
                }
            }
            if (returnType == Integer.TYPE) {
                try {
                    returnValue = Integer.parseInt(value);
                }
                catch (NumberFormatException e) {
                    throw LOG.getInvalidNumberFormatException("int", e);
                }
            }
            if (returnType == Long.TYPE) {
                try {
                    returnValue = Long.parseLong(value);
                }
                catch (NumberFormatException e) {
                    throw LOG.getInvalidNumberFormatException("long", e);
                }
            }
            if (returnType == Float.TYPE) {
                try {
                    returnValue = Float.valueOf(Float.parseFloat(value));
                }
                catch (NumberFormatException e) {
                    throw LOG.getInvalidNumberFormatException("float", e);
                }
            }
            if (returnType == Double.TYPE) {
                try {
                    returnValue = Double.parseDouble(value);
                }
                catch (NumberFormatException e) {
                    throw LOG.getInvalidNumberFormatException("double", e);
                }
            }
            if (returnType == Boolean.TYPE) {
                returnValue = Boolean.parseBoolean(value);
            } else if (returnType == Character.TYPE) {
                if (value.length() != 1) {
                    throw LOG.getInvalidCharValueException(value);
                }
                returnValue = Character.valueOf(value.charAt(0));
            } else if (returnType == String.class) {
                returnValue = value;
            } else if (returnType == Class.class) {
                returnValue = this.classLoadingHelper.loadClass(value, defaultPackage);
            } else {
                try {
                    Class<?> enumClass = returnType;
                    returnValue = Enum.valueOf(enumClass, value);
                }
                catch (ClassCastException e) {
                    throw LOG.getInvalidReturnTypeException(returnType, e);
                }
            }
            return returnValue;
        }

        private static <T> T run(PrivilegedAction<T> action) {
            return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
        }
    }

    private static class ConstraintParameterStaxBuilder
    extends AnnotationParameterStaxBuilder {
        private static final String ELEMENT_QNAME_LOCAL_PART = "element";
        private static final QName NAME_QNAME = new QName("name");

        public ConstraintParameterStaxBuilder(ClassLoadingHelper classLoadingHelper, DefaultPackageStaxBuilder defaultPackageStaxBuilder) {
            super(classLoadingHelper, defaultPackageStaxBuilder);
        }

        @Override
        protected String getAcceptableQName() {
            return ELEMENT_QNAME_LOCAL_PART;
        }

        @Override
        protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
            String name = this.readAttribute(xmlEvent.asStartElement(), NAME_QNAME).get();
            while (!xmlEvent.isEndElement() || !xmlEvent.asEndElement().getName().getLocalPart().equals(ELEMENT_QNAME_LOCAL_PART)) {
                xmlEvent = xmlEventReader.nextEvent();
                this.readElement(xmlEventReader, xmlEvent, name);
            }
        }

        @Override
        protected void checkNameIsValid(String name) {
            if ("message".equals(name) || "groups".equals(name) || "payload".equals(name)) {
                throw LOG.getReservedParameterNamesException("message", "groups", "payload");
            }
        }

        public <A extends Annotation> Map<String, Object> build(Class<A> annotationClass) {
            String defaultPackage = this.defaultPackageStaxBuilder.build().orElse("");
            HashMap<String, Object> builtParameters = new HashMap<String, Object>();
            for (Map.Entry parameter : this.parameters.entrySet()) {
                builtParameters.put((String)parameter.getKey(), this.getElementValue((List)parameter.getValue(), annotationClass, (String)parameter.getKey(), defaultPackage));
            }
            for (Map.Entry parameter : this.annotationParameters.entrySet()) {
                builtParameters.put((String)parameter.getKey(), this.getAnnotationElementValue((List)parameter.getValue(), annotationClass, (String)parameter.getKey(), defaultPackage));
            }
            return builtParameters;
        }
    }

    private static class MessageStaxBuilder
    extends AbstractOneLineStringStaxBuilder {
        private static final String MESSAGE_PACKAGE_QNAME = "message";

        private MessageStaxBuilder() {
        }

        @Override
        protected String getAcceptableQName() {
            return MESSAGE_PACKAGE_QNAME;
        }
    }
}

