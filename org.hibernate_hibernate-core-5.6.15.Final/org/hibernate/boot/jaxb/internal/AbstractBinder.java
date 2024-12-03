/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.bind.ValidationEventHandler
 *  org.jboss.logging.Logger
 */
package org.hibernate.boot.jaxb.internal;

import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import org.hibernate.boot.MappingException;
import org.hibernate.boot.jaxb.Origin;
import org.hibernate.boot.jaxb.internal.ContextProvidingValidationEventHandler;
import org.hibernate.boot.jaxb.internal.stax.BufferedXMLEventReader;
import org.hibernate.boot.jaxb.internal.stax.LocalXmlResourceResolver;
import org.hibernate.boot.jaxb.spi.Binder;
import org.hibernate.boot.jaxb.spi.Binding;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.internal.util.StringHelper;
import org.jboss.logging.Logger;

public abstract class AbstractBinder
implements Binder {
    private static final Logger log = Logger.getLogger(AbstractBinder.class);
    private final LocalXmlResourceResolver xmlResourceResolver;
    private final boolean validateXml;
    private XMLInputFactory staxFactory;

    protected AbstractBinder(ClassLoaderService classLoaderService) {
        this(classLoaderService, true);
    }

    protected AbstractBinder(ClassLoaderService classLoaderService, boolean validateXml) {
        this.xmlResourceResolver = new LocalXmlResourceResolver(classLoaderService);
        this.validateXml = validateXml;
    }

    public boolean isValidationEnabled() {
        return this.validateXml;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Binding bind(InputStream stream, Origin origin) {
        XMLEventReader eventReader = this.createReader(stream, origin);
        try {
            Binding binding = this.doBind(eventReader, origin);
            return binding;
        }
        finally {
            try {
                eventReader.close();
            }
            catch (XMLStreamException e) {
                log.debug((Object)"Unable to close StAX reader", (Throwable)e);
            }
        }
    }

    protected XMLEventReader createReader(InputStream stream, Origin origin) {
        try {
            XMLEventReader staxReader = this.staxFactory().createXMLEventReader(stream);
            return new BufferedXMLEventReader(staxReader, 100);
        }
        catch (XMLStreamException e) {
            throw new MappingException("Unable to create stax reader", e, origin);
        }
    }

    @Override
    public Binding bind(Source source, Origin origin) {
        XMLEventReader eventReader = this.createReader(source, origin);
        return this.doBind(eventReader, origin);
    }

    protected XMLEventReader createReader(Source source, Origin origin) {
        try {
            XMLEventReader staxReader = this.staxFactory().createXMLEventReader(source);
            return new BufferedXMLEventReader(staxReader, 100);
        }
        catch (XMLStreamException e) {
            throw new MappingException("Unable to create stax reader", e, origin);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Binding doBind(XMLEventReader eventReader, Origin origin) {
        try {
            StartElement rootElementStartEvent = this.seekRootElementStartEvent(eventReader, origin);
            Binding binding = this.doBind(eventReader, rootElementStartEvent, origin);
            return binding;
        }
        finally {
            try {
                eventReader.close();
            }
            catch (Exception e) {
                log.debug((Object)"Unable to close StAX reader", (Throwable)e);
            }
        }
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

    protected StartElement seekRootElementStartEvent(XMLEventReader staxEventReader, Origin origin) {
        XMLEvent rootElementStartEvent;
        try {
            rootElementStartEvent = staxEventReader.peek();
            while (rootElementStartEvent != null && !rootElementStartEvent.isStartElement()) {
                staxEventReader.nextEvent();
                rootElementStartEvent = staxEventReader.peek();
            }
        }
        catch (Exception e) {
            throw new MappingException("Error accessing stax stream", e, origin);
        }
        if (rootElementStartEvent == null) {
            throw new MappingException("Could not locate root element", origin);
        }
        return rootElementStartEvent.asStartElement();
    }

    protected abstract Binding doBind(XMLEventReader var1, StartElement var2, Origin var3);

    protected static boolean hasNamespace(StartElement startElement) {
        return StringHelper.isNotEmpty(startElement.getName().getNamespaceURI());
    }

    protected <T> T jaxb(XMLEventReader reader, Schema xsd, JAXBContext jaxbContext, Origin origin) {
        ContextProvidingValidationEventHandler handler = new ContextProvidingValidationEventHandler();
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            if (this.isValidationEnabled()) {
                unmarshaller.setSchema(xsd);
            } else {
                unmarshaller.setSchema(null);
            }
            unmarshaller.setEventHandler((ValidationEventHandler)handler);
            return (T)unmarshaller.unmarshal(reader);
        }
        catch (JAXBException e) {
            throw new MappingException("Unable to perform unmarshalling at line number " + handler.getLineNumber() + " and column " + handler.getColumnNumber() + ". Message: " + handler.getMessage(), e, origin);
        }
    }
}

