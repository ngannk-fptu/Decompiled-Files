/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.logging.Logger
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.runtime.config;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.config.metro.dev.FeatureReader;
import com.sun.xml.ws.config.metro.util.ParserUtil;
import com.sun.xml.ws.runtime.config.TubelineFeature;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.ws.WebServiceException;

public class TubelineFeatureReader
implements FeatureReader {
    private static final Logger LOGGER = Logger.getLogger(TubelineFeatureReader.class);
    private static final QName NAME_ATTRIBUTE_NAME = new QName("name");

    public TubelineFeature parse(XMLEventReader reader) throws WebServiceException {
        try {
            StartElement element = reader.nextEvent().asStartElement();
            boolean attributeEnabled = true;
            Iterator<Attribute> iterator = element.getAttributes();
            while (iterator.hasNext()) {
                Attribute nextAttribute = iterator.next();
                QName attributeName = nextAttribute.getName();
                if (ENABLED_ATTRIBUTE_NAME.equals(attributeName)) {
                    attributeEnabled = ParserUtil.parseBooleanValue(nextAttribute.getValue());
                    continue;
                }
                if (NAME_ATTRIBUTE_NAME.equals(attributeName)) continue;
                throw (WebServiceException)LOGGER.logSevereException((Throwable)new WebServiceException("Unexpected attribute"));
            }
            return this.parseFactories(attributeEnabled, element, reader);
        }
        catch (XMLStreamException e) {
            throw (WebServiceException)LOGGER.logSevereException((Throwable)new WebServiceException("Failed to unmarshal XML document", (Throwable)e));
        }
    }

    private TubelineFeature parseFactories(boolean enabled, StartElement element, XMLEventReader reader) throws WebServiceException {
        int elementRead = 0;
        block8: while (reader.hasNext()) {
            try {
                XMLEvent event = reader.nextEvent();
                switch (event.getEventType()) {
                    case 5: {
                        break;
                    }
                    case 4: {
                        if (event.asCharacters().isWhiteSpace()) break;
                        throw (WebServiceException)LOGGER.logSevereException((Throwable)new WebServiceException("No character data allowed, was " + event.asCharacters()));
                    }
                    case 1: {
                        ++elementRead;
                        break;
                    }
                    case 2: {
                        if (--elementRead >= 0) continue block8;
                        EndElement endElement = event.asEndElement();
                        if (element.getName().equals(endElement.getName())) break block8;
                        throw (WebServiceException)LOGGER.logSevereException((Throwable)new WebServiceException("End element does not match " + endElement));
                    }
                    default: {
                        throw (WebServiceException)LOGGER.logSevereException((Throwable)new WebServiceException("Unexpected event, was " + event));
                    }
                }
            }
            catch (XMLStreamException e) {
                throw (WebServiceException)LOGGER.logSevereException((Throwable)new WebServiceException("Failed to unmarshal XML document", (Throwable)e));
            }
        }
        return new TubelineFeature(enabled);
    }
}

