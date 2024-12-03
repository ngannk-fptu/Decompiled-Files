/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.bind.ValidationEvent
 *  javax.xml.bind.ValidationEventHandler
 *  javax.xml.bind.ValidationEventLocator
 *  org.jboss.logging.Logger
 */
package org.hibernate.boot.cfgxml.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.EventReaderDelegate;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.hibernate.HibernateException;
import org.hibernate.boot.jaxb.Origin;
import org.hibernate.boot.jaxb.cfg.spi.JaxbCfgHibernateConfiguration;
import org.hibernate.boot.jaxb.internal.stax.LocalXmlResourceResolver;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.config.ConfigurationException;
import org.hibernate.internal.util.xml.XsdException;
import org.jboss.logging.Logger;
import org.xml.sax.SAXException;

public class JaxbCfgProcessor {
    private static final Logger log = Logger.getLogger(JaxbCfgProcessor.class);
    public static final String HIBERNATE_CONFIGURATION_URI = "http://www.hibernate.org/xsd/orm/cfg";
    private final ClassLoaderService classLoaderService;
    private final LocalXmlResourceResolver xmlResourceResolver;
    private XMLInputFactory staxFactory;
    private Schema schema;

    public JaxbCfgProcessor(ClassLoaderService classLoaderService) {
        this.classLoaderService = classLoaderService;
        this.xmlResourceResolver = new LocalXmlResourceResolver(classLoaderService);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public JaxbCfgHibernateConfiguration unmarshal(InputStream stream, Origin origin) {
        JaxbCfgHibernateConfiguration jaxbCfgHibernateConfiguration;
        XMLEventReader staxReader = this.staxFactory().createXMLEventReader(stream);
        try {
            jaxbCfgHibernateConfiguration = this.unmarshal(staxReader, origin);
        }
        catch (Throwable throwable) {
            try {
                try {
                    staxReader.close();
                }
                catch (Exception exception) {
                    // empty catch block
                }
                throw throwable;
            }
            catch (XMLStreamException e) {
                throw new HibernateException("Unable to create stax reader", e);
            }
        }
        try {
            staxReader.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
        return jaxbCfgHibernateConfiguration;
    }

    private XMLInputFactory staxFactory() {
        if (this.staxFactory == null) {
            this.staxFactory = this.buildStaxFactory();
        }
        return this.staxFactory;
    }

    private XMLInputFactory buildStaxFactory() {
        XMLInputFactory staxFactory = XMLInputFactory.newInstance();
        staxFactory.setXMLResolver(this.xmlResourceResolver);
        return staxFactory;
    }

    private JaxbCfgHibernateConfiguration unmarshal(XMLEventReader staxEventReader, Origin origin) {
        XMLEvent event;
        try {
            event = staxEventReader.peek();
            while (event != null && !event.isStartElement()) {
                staxEventReader.nextEvent();
                event = staxEventReader.peek();
            }
        }
        catch (Exception e) {
            throw new HibernateException("Error accessing stax stream", e);
        }
        if (event == null) {
            throw new HibernateException("Could not locate root element");
        }
        if (!this.isNamespaced(event.asStartElement())) {
            log.debug((Object)"cfg.xml document did not define namespaces; wrapping in custom event reader to introduce namespace information");
            staxEventReader = new NamespaceAddingEventReader(staxEventReader, HIBERNATE_CONFIGURATION_URI);
        }
        ContextProvidingValidationEventHandler handler = new ContextProvidingValidationEventHandler();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance((Class[])new Class[]{JaxbCfgHibernateConfiguration.class});
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setSchema(this.schema());
            unmarshaller.setEventHandler((ValidationEventHandler)handler);
            return (JaxbCfgHibernateConfiguration)unmarshaller.unmarshal(staxEventReader);
        }
        catch (JAXBException e) {
            throw new ConfigurationException("Unable to perform unmarshalling at line number " + handler.getLineNumber() + " and column " + handler.getColumnNumber() + " in " + origin.getType().name() + " " + origin.getName() + ". Message: " + handler.getMessage(), e);
        }
    }

    private boolean isNamespaced(StartElement startElement) {
        return StringHelper.isNotEmpty(startElement.getName().getNamespaceURI());
    }

    private Schema schema() {
        if (this.schema == null) {
            this.schema = this.resolveLocalSchema("org/hibernate/hibernate-configuration-4.0.xsd");
        }
        return this.schema;
    }

    private Schema resolveLocalSchema(String schemaName) {
        return this.resolveLocalSchema(schemaName, "http://www.w3.org/2001/XMLSchema");
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private Schema resolveLocalSchema(String schemaName, String schemaLanguage) {
        URL url = this.classLoaderService.locateResource(schemaName);
        if (url == null) {
            throw new XsdException("Unable to locate schema [" + schemaName + "] via classpath", schemaName);
        }
        try {
            InputStream schemaStream = url.openStream();
            try {
                StreamSource source = new StreamSource(url.openStream());
                SchemaFactory schemaFactory = SchemaFactory.newInstance(schemaLanguage);
                Schema schema = schemaFactory.newSchema(source);
                return schema;
            }
            catch (SAXException e) {
                throw new XsdException("Unable to load schema [" + schemaName + "]", e, schemaName);
            }
            catch (IOException e) {
                throw new XsdException("Unable to load schema [" + schemaName + "]", e, schemaName);
            }
            finally {
                try {
                    schemaStream.close();
                }
                catch (IOException e) {
                    log.debugf("Problem closing schema stream [%s]", (Object)e.toString());
                }
            }
        }
        catch (IOException e) {
            throw new XsdException("Stream error handling schema url [" + url.toExternalForm() + "]", schemaName);
        }
    }

    public static class NamespaceAddingEventReader
    extends EventReaderDelegate {
        private final XMLEventFactory xmlEventFactory;
        private final String namespaceUri;

        public NamespaceAddingEventReader(XMLEventReader reader, String namespaceUri) {
            this(reader, XMLEventFactory.newInstance(), namespaceUri);
        }

        public NamespaceAddingEventReader(XMLEventReader reader, XMLEventFactory xmlEventFactory, String namespaceUri) {
            super(reader);
            this.xmlEventFactory = xmlEventFactory;
            this.namespaceUri = namespaceUri;
        }

        private StartElement withNamespace(StartElement startElement) {
            ArrayList<Namespace> namespaces = new ArrayList<Namespace>();
            namespaces.add(this.xmlEventFactory.createNamespace("", this.namespaceUri));
            Iterator<Namespace> originalNamespaces = startElement.getNamespaces();
            while (originalNamespaces.hasNext()) {
                namespaces.add(originalNamespaces.next());
            }
            return this.xmlEventFactory.createStartElement(new QName(this.namespaceUri, startElement.getName().getLocalPart()), startElement.getAttributes(), namespaces.iterator());
        }

        @Override
        public XMLEvent nextEvent() throws XMLStreamException {
            XMLEvent event = super.nextEvent();
            if (event.isStartElement()) {
                return this.withNamespace(event.asStartElement());
            }
            return event;
        }

        @Override
        public XMLEvent peek() throws XMLStreamException {
            XMLEvent event = super.peek();
            if (event.isStartElement()) {
                return this.withNamespace(event.asStartElement());
            }
            return event;
        }
    }

    static class ContextProvidingValidationEventHandler
    implements ValidationEventHandler {
        private int lineNumber;
        private int columnNumber;
        private String message;

        ContextProvidingValidationEventHandler() {
        }

        public boolean handleEvent(ValidationEvent validationEvent) {
            ValidationEventLocator locator = validationEvent.getLocator();
            this.lineNumber = locator.getLineNumber();
            this.columnNumber = locator.getColumnNumber();
            this.message = validationEvent.getMessage();
            return false;
        }

        public int getLineNumber() {
            return this.lineNumber;
        }

        public int getColumnNumber() {
            return this.columnNumber;
        }

        public String getMessage() {
            return this.message;
        }
    }
}

