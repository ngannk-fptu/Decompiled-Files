/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.xml;

import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.logging.Messages;
import org.hibernate.validator.internal.util.privilegedactions.GetClassLoader;
import org.hibernate.validator.internal.util.privilegedactions.GetResource;
import org.hibernate.validator.internal.util.privilegedactions.NewSchema;

public class XmlParserHelper {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final int NUMBER_OF_SCHEMAS = 4;
    private static final String DEFAULT_VERSION = "1.0";
    private static final QName VERSION_QNAME = new QName("version");
    private final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    private static final ConcurrentMap<String, Schema> schemaCache = new ConcurrentHashMap<String, Schema>(4);

    public String getSchemaVersion(String resourceName, XMLEventReader xmlEventReader) {
        Contracts.assertNotNull(xmlEventReader, Messages.MESSAGES.parameterMustNotBeNull("xmlEventReader"));
        try {
            StartElement rootElement = this.getRootElement(xmlEventReader);
            return this.getVersionValue(rootElement);
        }
        catch (XMLStreamException e) {
            throw LOG.getUnableToDetermineSchemaVersionException(resourceName, e);
        }
    }

    public synchronized XMLEventReader createXmlEventReader(String resourceName, InputStream xmlStream) {
        try {
            return this.xmlInputFactory.createXMLEventReader(xmlStream);
        }
        catch (Exception e) {
            throw LOG.getUnableToCreateXMLEventReader(resourceName, e);
        }
    }

    private String getVersionValue(StartElement startElement) {
        if (startElement == null) {
            return null;
        }
        Attribute versionAttribute = startElement.getAttributeByName(VERSION_QNAME);
        return versionAttribute != null ? versionAttribute.getValue() : DEFAULT_VERSION;
    }

    private StartElement getRootElement(XMLEventReader xmlEventReader) throws XMLStreamException {
        XMLEvent event = xmlEventReader.peek();
        while (event != null && !event.isStartElement()) {
            xmlEventReader.nextEvent();
            event = xmlEventReader.peek();
        }
        return event == null ? null : event.asStartElement();
    }

    public Schema getSchema(String schemaResource) {
        Schema schema = (Schema)schemaCache.get(schemaResource);
        if (schema != null) {
            return schema;
        }
        schema = this.loadSchema(schemaResource);
        if (schema != null) {
            Schema previous = schemaCache.putIfAbsent(schemaResource, schema);
            return previous != null ? previous : schema;
        }
        return null;
    }

    private Schema loadSchema(String schemaResource) {
        ClassLoader loader = this.run(GetClassLoader.fromClass(XmlParserHelper.class));
        URL schemaUrl = this.run(GetResource.action(loader, schemaResource));
        SchemaFactory sf = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema = null;
        try {
            schema = this.run(NewSchema.action(sf, schemaUrl));
        }
        catch (Exception e) {
            LOG.unableToCreateSchema(schemaResource, e.getMessage());
        }
        return schema;
    }

    private <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
    }

    private <T> T run(PrivilegedExceptionAction<T> action) throws Exception {
        return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
    }
}

