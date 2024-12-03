/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.ContextClassLoaderSwitchingUtil
 *  com.atlassian.spring.container.ContainerManager
 *  com.ctc.wstx.exc.WstxException
 *  com.ctc.wstx.exc.WstxLazyException
 *  com.ctc.wstx.exc.WstxParsingException
 *  com.ctc.wstx.exc.WstxValidationException
 *  com.ctc.wstx.stax.WstxEventFactory
 *  io.atlassian.util.concurrent.LazyReference
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XhtmlParsingException;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.content.render.xhtml.editor.macro.EditorConstants;
import com.atlassian.plugin.util.ContextClassLoaderSwitchingUtil;
import com.atlassian.spring.container.ContainerManager;
import com.ctc.wstx.exc.WstxException;
import com.ctc.wstx.exc.WstxLazyException;
import com.ctc.wstx.exc.WstxParsingException;
import com.ctc.wstx.exc.WstxValidationException;
import com.ctc.wstx.stax.WstxEventFactory;
import io.atlassian.util.concurrent.LazyReference;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StaxUtils {
    private static final Logger log = LoggerFactory.getLogger(StaxUtils.class);
    private static final Supplier<XMLEventFactory> XML_EVENT_FACTORY = new LazyReference<XMLEventFactory>(){

        protected XMLEventFactory create() throws Exception {
            return (XMLEventFactory)ContextClassLoaderSwitchingUtil.runInContext((ClassLoader)StaxUtils.class.getClassLoader(), WstxEventFactory::new);
        }
    };
    private static final Set<QName> VOID_ELEMENTS = Set.of(new QName("http://www.w3.org/1999/xhtml", "area"), new QName("http://www.w3.org/1999/xhtml", "base"), new QName("http://www.w3.org/1999/xhtml", "col"), new QName("http://www.w3.org/1999/xhtml", "br"), new QName("http://www.w3.org/1999/xhtml", "command"), new QName("http://www.w3.org/1999/xhtml", "embed"), new QName("http://www.w3.org/1999/xhtml", "hr"), new QName("http://www.w3.org/1999/xhtml", "img"), new QName("http://www.w3.org/1999/xhtml", "input"), new QName("http://www.w3.org/1999/xhtml", "keygen"), new QName("http://www.w3.org/1999/xhtml", "link"), new QName("http://www.w3.org/1999/xhtml", "meta"), new QName("http://www.w3.org/1999/xhtml", "param"), new QName("http://www.w3.org/1999/xhtml", "source"), new QName("http://www.w3.org/1999/xhtml", "track"), new QName("http://www.w3.org/1999/xhtml", "wbr"));
    private static final Set<QName> RAW_TEXT_ELEMENTS = Set.of(new QName("http://www.w3.org/1999/xhtml", "textarea"), new QName("http://www.w3.org/1999/xhtml", "title"));

    public static boolean isHTML5VoidElement(QName name) {
        return VOID_ELEMENTS.contains(name);
    }

    public static boolean isHTML5RawTextlement(QName name) {
        return RAW_TEXT_ELEMENTS.contains(name);
    }

    public static void writeStartElement(XMLStreamWriter writer, QName element) throws XMLStreamException {
        writer.writeStartElement(element.getPrefix(), element.getLocalPart(), element.getNamespaceURI());
    }

    public static void writeAttribute(XMLStreamWriter writer, QName attribute, String value) throws XMLStreamException {
        writer.writeAttribute(attribute.getPrefix(), attribute.getNamespaceURI(), attribute.getLocalPart(), value);
    }

    public static void writeRawXML(XMLStreamWriter writer, Writer out, Streamable streamable) throws IOException, XMLStreamException {
        writer.writeCharacters("");
        writer.flush();
        streamable.writeTo(out);
    }

    public static void closeQuietly(@Nullable XMLEventReader xmlEventReader) {
        if (xmlEventReader != null) {
            try {
                xmlEventReader.close();
            }
            catch (Exception e) {
                log.debug("Error closing reader", (Throwable)e);
            }
        }
    }

    public static void closeQuietly(XMLStreamReader xmlStreamReader) {
        if (xmlStreamReader != null) {
            try {
                xmlStreamReader.close();
            }
            catch (Exception e) {
                log.debug("Error closing reader", (Throwable)e);
            }
        }
    }

    public static void closeQuietly(@Nullable XMLEventWriter xmlEventWriter) {
        if (xmlEventWriter != null) {
            try {
                xmlEventWriter.close();
            }
            catch (Exception e) {
                log.debug("Error closing writer", (Throwable)e);
            }
        }
    }

    public static void closeQuietly(XMLStreamWriter xmlStreamWriter) {
        if (xmlStreamWriter != null) {
            try {
                xmlStreamWriter.close();
            }
            catch (Exception e) {
                log.debug("Error closing writer", (Throwable)e);
            }
        }
    }

    public static String getAttributeValue(StartElement startElement, String attributeName) {
        return StaxUtils.getAttributeValue(startElement, attributeName, null);
    }

    public static String getAttributeValue(StartElement startElement, QName attributeQName) {
        return StaxUtils.getAttributeValue(startElement, attributeQName, null);
    }

    public static String getAttributeValue(StartElement startElement, String attributeName, String defaultValue) {
        return StaxUtils.getAttributeValue(startElement, new QName(attributeName), defaultValue);
    }

    public static String getAttributeValue(StartElement startElement, QName attributeQName, String defaultValue) {
        Attribute attribute = startElement.getAttributeByName(attributeQName);
        return attribute != null ? StringUtils.defaultString((String)attribute.getValue()) : defaultValue;
    }

    public static boolean hasAttributes(StartElement startElement, String ... attributeNames) {
        for (String attributeName : attributeNames) {
            if (startElement.getAttributeByName(new QName(attributeName)) != null) continue;
            return false;
        }
        return true;
    }

    public static boolean hasClass(StartElement startElement, String className) {
        Attribute classAttr = startElement.getAttributeByName(EditorConstants.CLASS_ATTRIBUTE_NAME);
        if (classAttr != null && StringUtils.isNotBlank((CharSequence)classAttr.getValue())) {
            String[] classes;
            for (String cls : classes = StringUtils.split((String)classAttr.getValue(), (char)' ')) {
                if (!cls.equals(className)) continue;
                return true;
            }
        }
        return false;
    }

    public static String collectWhitespace(XMLEventReader reader) throws XMLStreamException {
        StringBuilder builder = new StringBuilder();
        return StaxUtils.collectWhitespace(builder, reader).toString();
    }

    public static StringBuilder collectWhitespace(StringBuilder builder, XMLEventReader reader) throws XMLStreamException {
        if (reader.peek() != null && reader.peek().isCharacters() && reader.peek().asCharacters().isWhiteSpace()) {
            XMLEvent peekedEvent;
            while (reader.hasNext() && (peekedEvent = reader.peek()).isCharacters() && peekedEvent.asCharacters().isWhiteSpace()) {
                XMLEvent event = reader.nextEvent();
                builder.append(event.asCharacters().getData());
            }
        }
        return builder;
    }

    public static void skipWhitespace(XMLEventReader reader) throws XMLStreamException {
        if (reader.peek() != null && reader.peek().isCharacters() && reader.peek().asCharacters().isWhiteSpace()) {
            XMLEvent peekedEvent;
            while (reader.hasNext() && (peekedEvent = reader.peek()).isCharacters() && peekedEvent.asCharacters().isWhiteSpace()) {
                reader.nextEvent();
            }
        }
    }

    public static String readCharactersAndEntities(XMLEventReader reader) throws XMLStreamException {
        XMLEvent peekedEvent;
        StringBuilder builder = new StringBuilder();
        while (reader.hasNext() && ((peekedEvent = reader.peek()).isCharacters() || peekedEvent.isEntityReference())) {
            XMLEvent event = reader.nextEvent();
            if (event.isCharacters()) {
                builder.append(event.asCharacters().getData());
                continue;
            }
            if (!event.isEntityReference()) continue;
            EntityReference reference = (EntityReference)event;
            builder.append("&").append(reference.getName()).append(";");
        }
        return builder.toString();
    }

    @Deprecated
    public static String toString(XMLEventReader reader) {
        return StaxUtils.toXmlString(reader, (XmlOutputFactory)ContainerManager.getComponent((String)"xmlFragmentOutputFactory"));
    }

    public static String toXmlString(XMLEventReader reader, XmlOutputFactory xmlOutputFactory) {
        try {
            StringWriter result = new StringWriter();
            XMLEventWriter eventWriter = xmlOutputFactory.createXMLEventWriter(result);
            eventWriter.add(reader);
            eventWriter.close();
            return result.toString();
        }
        catch (XMLStreamException e) {
            throw new RuntimeException("Error converting XML event reader to string", e);
        }
    }

    public static String toXmlStringWithoutTag(XMLEventReader reader, XmlOutputFactory xmlOutputFactory, Set<QName> ignoredElements) {
        try {
            StringWriter result = new StringWriter();
            XMLEventWriter eventWriter = xmlOutputFactory.createXMLEventWriter(result);
            eventWriter.add(XML_EVENT_FACTORY.get().createStartElement("", "", "root"));
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                if (!event.isStartElement() || !ignoredElements.contains(event.asStartElement().getName())) {
                    eventWriter.add(event);
                    continue;
                }
                int ignoredElementNestingLevel = 1;
                while (reader.hasNext() && ignoredElementNestingLevel > 0) {
                    XMLEvent nestedEvent = reader.nextEvent();
                    if (nestedEvent.isStartElement() && ignoredElements.contains(nestedEvent.asStartElement().getName())) {
                        ++ignoredElementNestingLevel;
                        continue;
                    }
                    if (!nestedEvent.isEndElement() || !ignoredElements.contains(nestedEvent.asEndElement().getName())) continue;
                    --ignoredElementNestingLevel;
                }
            }
            eventWriter.add(XML_EVENT_FACTORY.get().createEndElement("", "", "root"));
            eventWriter.close();
            String resultStr = result.toString();
            if ("<root />".equals(resultStr) || "<root/>".equals(resultStr)) {
                return "";
            }
            return resultStr.substring("<root>".length(), resultStr.length() - "</root>".length());
        }
        catch (XMLStreamException e) {
            throw new RuntimeException("Error converting XML event reader to string", e);
        }
    }

    public static void flushEventWriter(XMLEventWriter eventWriter) throws XMLStreamException {
        eventWriter.add(XML_EVENT_FACTORY.get().createIgnorableSpace(""));
        eventWriter.flush();
    }

    public static List<String> splitCData(String data) {
        if (data == null) {
            return Collections.singletonList(null);
        }
        String cdataEnd = "]]>";
        ArrayList<String> sections = new ArrayList<String>();
        int index = data.indexOf(cdataEnd);
        while (index != -1) {
            sections.add(data.substring(0, index + cdataEnd.length() - 1));
            data = data.substring(index + cdataEnd.length() - 1);
            index = data.indexOf(cdataEnd);
        }
        sections.add(data);
        return sections;
    }

    public static XhtmlException convertToXhtmlException(WstxLazyException ex) {
        if (ex.getCause() instanceof WstxParsingException) {
            WstxParsingException wpe = (WstxParsingException)ex.getCause();
            return new XhtmlParsingException(wpe.getLocation().getLineNumber(), wpe.getLocation().getColumnNumber(), wpe.getMessage(), (Throwable)wpe);
        }
        return new XhtmlException("The XML content could not be parsed.", ex.getCause());
    }

    public static <E extends XhtmlException> E processWrappedWstxExceptionOrTrowMapped(RuntimeException ex, Function<RuntimeException, E> transformer) {
        if (StaxUtils.isWrappedAnyWstxException(ex)) {
            if (ex.getCause() instanceof WstxParsingException) {
                WstxParsingException wpe = (WstxParsingException)ex.getCause();
                int lineNumber = wpe.getLocation() == null ? -1 : wpe.getLocation().getLineNumber();
                int columnNumber = wpe.getLocation() == null ? -1 : wpe.getLocation().getColumnNumber();
                return (E)new XhtmlParsingException(lineNumber, columnNumber, wpe.getMessage(), (Throwable)wpe);
            }
            if (ex.getCause() instanceof WstxLazyException) {
                return (E)StaxUtils.convertToXhtmlException((WstxLazyException)ex.getCause());
            }
            return (E)new XhtmlException("The XML content could not be parsed.", ex.getCause());
        }
        return (E)((XhtmlException)transformer.apply(ex));
    }

    public static boolean isWrappedAnyWstxException(RuntimeException ex) {
        Throwable cause = ex.getCause();
        return cause instanceof WstxLazyException || cause instanceof WstxException || cause instanceof WstxValidationException;
    }

    public static @NonNull CharSequence stripIllegalControlChars(@NonNull CharSequence unclean) {
        ArrayList<Integer> illegalCharIndexes = new ArrayList<Integer>();
        int l = unclean.length();
        for (int i = 0; i < l; ++i) {
            char c = unclean.charAt(i);
            if (c >= ' ' || c == '\t' || c == '\n' || c == '\r') continue;
            illegalCharIndexes.add(i);
        }
        if (illegalCharIndexes.isEmpty()) {
            return unclean;
        }
        StringBuffer clean = new StringBuffer();
        int lastIndex = 0;
        for (Integer index : illegalCharIndexes) {
            if (lastIndex < index) {
                clean.append(unclean.subSequence(lastIndex, index));
            }
            lastIndex = index + 1;
        }
        if (lastIndex != unclean.length()) {
            clean.append(unclean.subSequence(lastIndex, unclean.length()));
        }
        return clean;
    }
}

