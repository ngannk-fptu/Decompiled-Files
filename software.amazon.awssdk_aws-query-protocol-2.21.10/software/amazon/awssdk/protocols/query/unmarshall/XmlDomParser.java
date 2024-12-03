/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.utils.LookaheadInputStream
 */
package software.amazon.awssdk.protocols.query.unmarshall;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.protocols.query.unmarshall.XmlElement;
import software.amazon.awssdk.utils.LookaheadInputStream;

@SdkProtectedApi
public final class XmlDomParser {
    private static final ThreadLocal<XMLInputFactory> FACTORY = ThreadLocal.withInitial(XmlDomParser::createXmlInputFactory);

    private XmlDomParser() {
    }

    public static XmlElement parse(InputStream inputStream) {
        LookaheadInputStream stream = new LookaheadInputStream(inputStream);
        try {
            XMLEvent nextEvent;
            if (stream.peek() == -1) {
                return XmlElement.empty();
            }
            XMLEventReader reader = FACTORY.get().createXMLEventReader((InputStream)stream);
            do {
                nextEvent = reader.nextEvent();
            } while (reader.hasNext() && !nextEvent.isStartElement());
            return XmlDomParser.parseElement(nextEvent.asStartElement(), reader);
        }
        catch (IOException | XMLStreamException e) {
            throw SdkClientException.create((String)"Could not parse XML response.", (Throwable)e);
        }
    }

    private static XmlElement parseElement(StartElement startElement, XMLEventReader reader) throws XMLStreamException {
        XMLEvent nextEvent;
        XmlElement.Builder elementBuilder = XmlElement.builder().elementName(startElement.getName().getLocalPart());
        if (startElement.getAttributes().hasNext()) {
            XmlDomParser.parseAttributes(startElement, elementBuilder);
        }
        do {
            if ((nextEvent = reader.nextEvent()).isStartElement()) {
                elementBuilder.addChildElement(XmlDomParser.parseElement(nextEvent.asStartElement(), reader));
                continue;
            }
            if (!nextEvent.isCharacters()) continue;
            elementBuilder.textContent(XmlDomParser.readText(reader, nextEvent.asCharacters().getData()));
        } while (!nextEvent.isEndElement());
        return elementBuilder.build();
    }

    private static void parseAttributes(StartElement startElement, XmlElement.Builder elementBuilder) {
        Iterator<Attribute> iterator = startElement.getAttributes();
        HashMap<String, String> attributes = new HashMap<String, String>();
        iterator.forEachRemaining(a -> {
            String key = a.getName().getPrefix() + ":" + a.getName().getLocalPart();
            attributes.put(key, a.getValue());
        });
        elementBuilder.attributes(attributes);
    }

    private static String readText(XMLEventReader eventReader, String firstChunk) throws XMLStreamException {
        XMLEvent event;
        StringBuilder sb = new StringBuilder(firstChunk);
        while ((event = eventReader.peek()).isCharacters()) {
            eventReader.nextEvent();
            sb.append(event.asCharacters().getData());
        }
        return sb.toString();
    }

    private static XMLInputFactory createXmlInputFactory() {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty("javax.xml.stream.supportDTD", false);
        factory.setProperty("javax.xml.stream.isSupportingExternalEntities", false);
        return factory;
    }
}

