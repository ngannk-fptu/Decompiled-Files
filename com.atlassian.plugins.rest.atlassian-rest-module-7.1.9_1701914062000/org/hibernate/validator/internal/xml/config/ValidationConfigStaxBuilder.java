/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.BootstrapConfiguration
 *  javax.validation.executable.ExecutableType
 */
package org.hibernate.validator.internal.xml.config;

import java.lang.invoke.MethodHandles;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.validation.BootstrapConfiguration;
import javax.validation.executable.ExecutableType;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.xml.AbstractStaxBuilder;
import org.hibernate.validator.internal.xml.config.BootstrapConfigurationImpl;

class ValidationConfigStaxBuilder
extends AbstractStaxBuilder {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final String VALIDATION_CONFIG_QNAME = "validation-config";
    private final SimpleConfigurationsStaxBuilder simpleConfigurationsStaxBuilder = new SimpleConfigurationsStaxBuilder();
    private final PropertyStaxBuilder propertyStaxBuilder = new PropertyStaxBuilder();
    private final ValueExtractorsStaxBuilder valueExtractorsStaxBuilder = new ValueExtractorsStaxBuilder();
    private final ConstraintMappingsStaxBuilder constraintMappingsStaxBuilder = new ConstraintMappingsStaxBuilder();
    private final ExecutableValidationStaxBuilder executableValidationStaxBuilder = new ExecutableValidationStaxBuilder();
    private final Map<String, AbstractStaxBuilder> builders = new HashMap<String, AbstractStaxBuilder>();

    public ValidationConfigStaxBuilder(XMLEventReader xmlEventReader) throws XMLStreamException {
        this.builders.put(this.propertyStaxBuilder.getAcceptableQName(), this.propertyStaxBuilder);
        this.builders.put(this.valueExtractorsStaxBuilder.getAcceptableQName(), this.valueExtractorsStaxBuilder);
        this.builders.put(this.constraintMappingsStaxBuilder.getAcceptableQName(), this.constraintMappingsStaxBuilder);
        this.builders.put(this.executableValidationStaxBuilder.getAcceptableQName(), this.executableValidationStaxBuilder);
        for (String name : SimpleConfigurationsStaxBuilder.getProcessedElementNames()) {
            this.builders.put(name, this.simpleConfigurationsStaxBuilder);
        }
        while (xmlEventReader.hasNext()) {
            this.process(xmlEventReader, xmlEventReader.nextEvent());
        }
    }

    @Override
    protected String getAcceptableQName() {
        return VALIDATION_CONFIG_QNAME;
    }

    @Override
    protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
        while (!xmlEvent.isEndElement() || !xmlEvent.asEndElement().getName().getLocalPart().equals(VALIDATION_CONFIG_QNAME)) {
            XMLEvent currentEvent;
            xmlEvent = currentEvent = xmlEventReader.nextEvent();
            if (!currentEvent.isStartElement()) continue;
            StartElement startElement = currentEvent.asStartElement();
            String localPart = startElement.getName().getLocalPart();
            AbstractStaxBuilder builder = this.builders.get(localPart);
            if (builder != null) {
                builder.process(xmlEventReader, xmlEvent);
                continue;
            }
            LOG.logUnknownElementInXmlConfiguration(localPart);
        }
    }

    public BootstrapConfiguration build() {
        Map<String, String> properties = this.propertyStaxBuilder.build();
        return new BootstrapConfigurationImpl(this.simpleConfigurationsStaxBuilder.getDefaultProvider(), this.simpleConfigurationsStaxBuilder.getConstraintValidatorFactory(), this.simpleConfigurationsStaxBuilder.getMessageInterpolator(), this.simpleConfigurationsStaxBuilder.getTraversableResolver(), this.simpleConfigurationsStaxBuilder.getParameterNameProvider(), this.simpleConfigurationsStaxBuilder.getClockProvider(), this.valueExtractorsStaxBuilder.build(), this.executableValidationStaxBuilder.build(), this.executableValidationStaxBuilder.isEnabled(), this.constraintMappingsStaxBuilder.build(), properties);
    }

    private static class ExecutableValidationStaxBuilder
    extends AbstractStaxBuilder {
        private static final String EXECUTABLE_VALIDATION_QNAME_LOCAL_PART = "executable-validation";
        private static final String EXECUTABLE_TYPE_QNAME_LOCAL_PART = "executable-type";
        private static final QName ENABLED_QNAME = new QName("enabled");
        private Boolean enabled;
        private EnumSet<ExecutableType> executableTypes = EnumSet.noneOf(ExecutableType.class);

        private ExecutableValidationStaxBuilder() {
        }

        @Override
        protected String getAcceptableQName() {
            return EXECUTABLE_VALIDATION_QNAME_LOCAL_PART;
        }

        @Override
        protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
            Optional<String> enabledAttribute = this.readAttribute(xmlEvent.asStartElement(), ENABLED_QNAME);
            if (enabledAttribute.isPresent()) {
                this.enabled = Boolean.parseBoolean(enabledAttribute.get());
            }
            while (!xmlEvent.isEndElement() || !xmlEvent.asEndElement().getName().getLocalPart().equals(EXECUTABLE_VALIDATION_QNAME_LOCAL_PART)) {
                XMLEvent currentEvent;
                xmlEvent = currentEvent = xmlEventReader.nextEvent();
                if (!currentEvent.isStartElement() || !currentEvent.asStartElement().getName().getLocalPart().equals(EXECUTABLE_TYPE_QNAME_LOCAL_PART)) continue;
                this.executableTypes.add(ExecutableType.valueOf((String)this.readSingleElement(xmlEventReader)));
            }
        }

        public boolean isEnabled() {
            return this.enabled == null ? true : this.enabled;
        }

        public EnumSet<ExecutableType> build() {
            return this.executableTypes.isEmpty() ? null : this.executableTypes;
        }
    }

    private static class ConstraintMappingsStaxBuilder
    extends AbstractStaxBuilder {
        private static final String CONSTRAINT_MAPPING_QNAME_LOCAL_PART = "constraint-mapping";
        private final Set<String> constraintMappings = new HashSet<String>();

        private ConstraintMappingsStaxBuilder() {
        }

        @Override
        protected String getAcceptableQName() {
            return CONSTRAINT_MAPPING_QNAME_LOCAL_PART;
        }

        @Override
        protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
            String value = this.readSingleElement(xmlEventReader);
            this.constraintMappings.add(value);
        }

        public Set<String> build() {
            return this.constraintMappings;
        }
    }

    private static class ValueExtractorsStaxBuilder
    extends AbstractStaxBuilder {
        private static final String VALUE_EXTRACTOR_QNAME_LOCAL_PART = "value-extractor";
        private final Set<String> valueExtractors = new HashSet<String>();

        private ValueExtractorsStaxBuilder() {
        }

        @Override
        protected String getAcceptableQName() {
            return VALUE_EXTRACTOR_QNAME_LOCAL_PART;
        }

        @Override
        protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
            String value = this.readSingleElement(xmlEventReader);
            if (!this.valueExtractors.add(value)) {
                throw LOG.getDuplicateDefinitionsOfValueExtractorException(value);
            }
        }

        public Set<String> build() {
            return this.valueExtractors;
        }
    }

    private static class PropertyStaxBuilder
    extends AbstractStaxBuilder {
        private static final String PROPERTY_QNAME_LOCAL_PART = "property";
        private static final QName NAME_QNAME = new QName("name");
        private final Map<String, String> properties = new HashMap<String, String>();

        private PropertyStaxBuilder() {
        }

        @Override
        protected String getAcceptableQName() {
            return PROPERTY_QNAME_LOCAL_PART;
        }

        @Override
        protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
            StartElement startElement = xmlEvent.asStartElement();
            String name = this.readAttribute(startElement, NAME_QNAME).get();
            String value = this.readSingleElement(xmlEventReader);
            if (LOG.isDebugEnabled()) {
                LOG.debugf("Found property '%s' with value '%s' in validation.xml.", (Object)name, (Object)value);
            }
            this.properties.put(name, value);
        }

        public Map<String, String> build() {
            return this.properties;
        }
    }

    private static class SimpleConfigurationsStaxBuilder
    extends AbstractStaxBuilder {
        private static final String DEFAULT_PROVIDER = "default-provider";
        private static final String MESSAGE_INTERPOLATOR = "message-interpolator";
        private static final String TRAVERSABLE_RESOLVER = "traversable-resolver";
        private static final String CONSTRAINT_VALIDATOR_FACTORY = "constraint-validator-factory";
        private static final String PARAMETER_NAME_PROVIDER = "parameter-name-provider";
        private static final String CLOCK_PROVIDER = "clock-provider";
        private static final Set<String> SINGLE_ELEMENTS = CollectionHelper.toImmutableSet(CollectionHelper.asSet("default-provider", "message-interpolator", "traversable-resolver", "constraint-validator-factory", "parameter-name-provider", "clock-provider"));
        private final Map<String, String> singleValuedElements = new HashMap<String, String>();

        private SimpleConfigurationsStaxBuilder() {
        }

        @Override
        protected String getAcceptableQName() {
            throw new UnsupportedOperationException("this method shouldn't be called");
        }

        @Override
        protected boolean accept(XMLEvent xmlEvent) {
            return xmlEvent.isStartElement() && SINGLE_ELEMENTS.contains(xmlEvent.asStartElement().getName().getLocalPart());
        }

        @Override
        protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
            String localPart = xmlEvent.asStartElement().getName().getLocalPart();
            this.singleValuedElements.put(localPart, this.readSingleElement(xmlEventReader));
        }

        public String getDefaultProvider() {
            return this.singleValuedElements.get(DEFAULT_PROVIDER);
        }

        public String getMessageInterpolator() {
            return this.singleValuedElements.get(MESSAGE_INTERPOLATOR);
        }

        public String getTraversableResolver() {
            return this.singleValuedElements.get(TRAVERSABLE_RESOLVER);
        }

        public String getClockProvider() {
            return this.singleValuedElements.get(CLOCK_PROVIDER);
        }

        public String getConstraintValidatorFactory() {
            return this.singleValuedElements.get(CONSTRAINT_VALIDATOR_FACTORY);
        }

        public String getParameterNameProvider() {
            return this.singleValuedElements.get(PARAMETER_NAME_PROVIDER);
        }

        public static Set<String> getProcessedElementNames() {
            return SINGLE_ELEMENTS;
        }
    }
}

