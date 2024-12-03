/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ResettableXmlEventReader
 *  com.atlassian.confluence.content.render.xhtml.StaxUtils
 *  com.atlassian.confluence.content.render.xhtml.XMLEventFactoryProvider
 *  com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory
 *  com.atlassian.confluence.content.render.xhtml.XmlOutputFactory
 *  com.atlassian.confluence.content.render.xhtml.storage.inlinetask.StorageInlineTaskConstants
 *  com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers.StorageResourceIdentifierConstants
 *  com.atlassian.confluence.core.BodyContent
 *  com.atlassian.confluence.core.BodyType
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.mentions;

import com.atlassian.confluence.content.render.xhtml.ResettableXmlEventReader;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.XMLEventFactoryProvider;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.content.render.xhtml.storage.inlinetask.StorageInlineTaskConstants;
import com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers.StorageResourceIdentifierConstants;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.mentions.MentionsExcerptor;
import com.atlassian.confluence.user.ConfluenceUser;
import com.google.common.collect.Sets;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;

public class StaxMentionsExcerptor
implements MentionsExcerptor {
    private static final QName USERKEY_ATTRIBUTE_QNAME = new QName("http://atlassian.com/resource/identifier", "userkey", "ri");
    private static final QName PARAGRAPH_QNAME = new QName("http://www.w3.org/1999/xhtml", "p");
    private static final QName LI_QNAME = new QName("http://www.w3.org/1999/xhtml", "li");
    private static final QName TR_QNAME = new QName("http://www.w3.org/1999/xhtml", "tr");
    private static final Set<QName> UNIT_ELEMENTS = Sets.newHashSet((Object[])new QName[]{PARAGRAPH_QNAME, LI_QNAME, TR_QNAME, StorageInlineTaskConstants.TASK_ELEMENT});
    private static final Set<QName> LIST_ELEMENTS = Sets.newHashSet((Object[])new QName[]{new QName("http://www.w3.org/1999/xhtml", "ol"), new QName("http://www.w3.org/1999/xhtml", "ul"), StorageInlineTaskConstants.TASK_LIST_ELEMENT});
    private static final Map<QName, String> DIVIDERS = new HashMap<QName, String>();
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final XmlOutputFactory xmlFragmentOutputFactory;
    private final XMLEventFactory xmlEventFactory;

    public StaxMentionsExcerptor(XmlEventReaderFactory xmlEventReaderFactory, @Qualifier(value="xmlFragmentOutputFactory") XmlOutputFactory xmlFragmentOutputFactory, XMLEventFactoryProvider xmlEventFactoryProvider) {
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.xmlEventFactory = xmlEventFactoryProvider.getXmlEventFactory();
        this.xmlFragmentOutputFactory = xmlFragmentOutputFactory;
    }

    @Override
    public String getExcerpt(ContentEntityObject content, ConfluenceUser mentionedUser) {
        List<? extends CharSequence> excerpts;
        if (mentionedUser == null || StringUtils.isBlank((CharSequence)mentionedUser.getName()) || content == null) {
            return "";
        }
        BodyContent bodyContent = content.getBodyContent();
        if (bodyContent.getBodyType() != BodyType.XHTML) {
            return "";
        }
        XMLEventReader reader = null;
        try {
            reader = this.xmlEventReaderFactory.createStorageXmlEventReader((Reader)new StringReader("<div>" + bodyContent.getBody() + "</div>"));
            excerpts = this.getExcerpts(reader, mentionedUser);
        }
        catch (XMLStreamException exception) {
            try {
                throw new RuntimeException("Error occurred while reading stream", exception);
            }
            catch (Throwable throwable) {
                StaxUtils.closeQuietly(reader);
                throw throwable;
            }
        }
        StaxUtils.closeQuietly((XMLEventReader)reader);
        StringBuilder result = new StringBuilder();
        excerpts.forEach(result::append);
        return result.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private List<? extends CharSequence> getExcerpts(XMLEventReader xmlFragment, ConfluenceUser user) throws XMLStreamException {
        if (xmlFragment == null || xmlFragment.peek() == null) {
            return Collections.emptyList();
        }
        if (!xmlFragment.peek().isStartElement()) {
            throw new IllegalArgumentException("xmlFragmentReader should serve start element of the fragment first.");
        }
        try {
            StartElement xmlFragmentStart = xmlFragment.peek().asStartElement();
            if (UNIT_ELEMENTS.contains(xmlFragmentStart.getName())) {
                List<? extends CharSequence> list = this.getExcerptsUnitElements(xmlFragmentStart, xmlFragment, user);
                return list;
            }
            if (LIST_ELEMENTS.contains(xmlFragmentStart.getName())) {
                List<? extends CharSequence> list = this.getExcerptsListElements(xmlFragmentStart, xmlFragment, user);
                return list;
            }
            if ("table".equals(xmlFragmentStart.getName().getLocalPart())) {
                List<? extends CharSequence> list = this.getExcerptsTableElement(xmlFragment, user);
                return list;
            }
            List<? extends CharSequence> list = this.getExcerptsOtherElements(xmlFragment, user);
            return list;
        }
        finally {
            StaxUtils.closeQuietly((XMLEventReader)xmlFragment);
        }
    }

    private List<? extends CharSequence> getExcerptsUnitElements(StartElement xmlFragmentStart, XMLEventReader xmlFragment, ConfluenceUser user) throws XMLStreamException {
        if (this.fragmentContainsUser(xmlFragment = new ResettableXmlEventReader(xmlFragment), user)) {
            ((ResettableXmlEventReader)xmlFragment).reset();
            return Collections.singletonList(this.toStringBuilder(xmlFragment));
        }
        String divider = DIVIDERS.get(xmlFragmentStart.getName());
        return Collections.singletonList(divider == null ? "" : divider);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private List<? extends CharSequence> getExcerptsListElements(StartElement xmlFragmentStart, XMLEventReader xmlFragment, ConfluenceUser user) throws XMLStreamException {
        LinkedList<StringBuilder> excerpts = new LinkedList<StringBuilder>();
        XMLEventReader xmlFragmentBody = this.xmlEventReaderFactory.createXmlFragmentBodyEventReader(xmlFragment);
        while (xmlFragmentBody.hasNext()) {
            XMLEvent xmlEvent = xmlFragmentBody.peek();
            if (xmlEvent.isStartElement() && UNIT_ELEMENTS.contains(xmlEvent.asStartElement().getName())) {
                XMLEventReader unitFragment = this.xmlEventReaderFactory.createXmlFragmentEventReader(xmlFragmentBody);
                this.getExcerpts(unitFragment, user).stream().filter(excerpt -> excerpt != excerpts.peekLast() && !"".contentEquals((CharSequence)excerpt)).forEach(excerpts::add);
                continue;
            }
            xmlFragmentBody.nextEvent();
        }
        if (excerpts.isEmpty()) {
            return Collections.emptyList();
        }
        StringWriter listFragmentBuffer = new StringWriter();
        XMLEventWriter listFragment = this.xmlFragmentOutputFactory.createXMLEventWriter((Writer)listFragmentBuffer);
        try {
            listFragment.add(xmlFragmentStart);
            listFragment.add(this.xmlEventFactory.createCharacters(""));
            listFragment.flush();
            excerpts.add(0, new StringBuilder(listFragmentBuffer.getBuffer()));
            int position = listFragmentBuffer.getBuffer().length();
            listFragment.add(xmlFragment.nextEvent());
            listFragment.flush();
            excerpts.add(new StringBuilder(listFragmentBuffer.getBuffer().subSequence(position, listFragmentBuffer.getBuffer().length())));
        }
        finally {
            StaxUtils.closeQuietly((XMLEventWriter)listFragment);
        }
        return excerpts;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private List<? extends CharSequence> getExcerptsTableElement(XMLEventReader xmlFragment, ConfluenceUser user) throws XMLStreamException {
        LinkedList<StringBuilder> excerpts = new LinkedList<StringBuilder>();
        LinkedList<XMLEvent> tableEvents = new LinkedList<XMLEvent>();
        tableEvents.add(xmlFragment.nextEvent());
        while (xmlFragment.hasNext()) {
            XMLEvent xmlEvent = xmlFragment.peek();
            if (xmlEvent.isStartElement() && "tr".equals(xmlEvent.asStartElement().getName().getLocalPart())) {
                XMLEventReader trFragment = this.xmlEventReaderFactory.createXmlFragmentEventReader(xmlFragment);
                this.getExcerpts(trFragment, user).stream().filter(excerpt -> excerpt != excerpts.peekLast() && !"".contentEquals((CharSequence)excerpt)).forEach(excerpts::add);
                continue;
            }
            tableEvents.add(xmlFragment.nextEvent());
        }
        if (excerpts.isEmpty()) {
            return Collections.emptyList();
        }
        StringWriter tableFragmentBuffer = new StringWriter();
        XMLEventWriter tableFragment = this.xmlFragmentOutputFactory.createXMLEventWriter((Writer)tableFragmentBuffer);
        try {
            for (XMLEvent tableEvent : tableEvents) {
                if (tableEvent.isStartElement() && "tbody".equals(tableEvent.asStartElement().getName().getLocalPart())) {
                    tableFragment.add(tableEvent);
                    tableFragment.add(this.xmlEventFactory.createCharacters(""));
                    tableFragment.flush();
                    continue;
                }
                tableFragment.add(tableEvent);
            }
        }
        finally {
            StaxUtils.closeQuietly((XMLEventWriter)tableFragment);
        }
        StringBuffer strBuffer = tableFragmentBuffer.getBuffer();
        int indexEndTbody = strBuffer.indexOf(">", strBuffer.indexOf("<tbody")) + 1;
        excerpts.add(0, new StringBuilder(tableFragmentBuffer.getBuffer().subSequence(0, indexEndTbody)));
        excerpts.add(new StringBuilder(tableFragmentBuffer.getBuffer().subSequence(indexEndTbody, tableFragmentBuffer.getBuffer().length())));
        return excerpts;
    }

    private List<? extends CharSequence> getExcerptsOtherElements(XMLEventReader xmlFragment, ConfluenceUser user) throws XMLStreamException {
        xmlFragment.nextEvent();
        LinkedList excerpts = new LinkedList();
        while (xmlFragment.hasNext()) {
            XMLEvent xmlFragmentEvent = xmlFragment.peek();
            if (xmlFragmentEvent.isStartElement()) {
                this.getExcerpts(this.xmlEventReaderFactory.createXmlFragmentEventReader(xmlFragment), user).stream().filter(excerpt -> excerpt != excerpts.peekLast()).forEach(excerpts::add);
                continue;
            }
            xmlFragment.nextEvent();
        }
        return excerpts;
    }

    private boolean fragmentContainsUser(XMLEventReader xmlFragmentReader, ConfluenceUser user) throws XMLStreamException {
        while (xmlFragmentReader.hasNext()) {
            Attribute userKeyAttribute;
            StartElement startElement;
            XMLEvent xmlEvent = xmlFragmentReader.nextEvent();
            if (!xmlEvent.isStartElement() || !(startElement = xmlEvent.asStartElement()).getName().equals(StorageResourceIdentifierConstants.USER_RESOURCE_QNAME) || (userKeyAttribute = startElement.getAttributeByName(USERKEY_ATTRIBUTE_QNAME)) == null || user.getKey() == null || !user.getKey().getStringValue().equals(userKeyAttribute.getValue())) continue;
            return true;
        }
        return false;
    }

    private CharSequence toStringBuilder(XMLEventReader xmlFragmentReader) {
        XMLEventWriter xmlEventWriter = null;
        StringWriter result = new StringWriter();
        try {
            xmlEventWriter = this.xmlFragmentOutputFactory.createXMLEventWriter((Writer)result);
            xmlEventWriter.add(xmlFragmentReader);
        }
        catch (XMLStreamException e) {
            try {
                throw new RuntimeException(e);
            }
            catch (Throwable throwable) {
                StaxUtils.closeQuietly(xmlEventWriter);
                throw throwable;
            }
        }
        StaxUtils.closeQuietly((XMLEventWriter)xmlEventWriter);
        return result.toString();
    }

    static {
        DIVIDERS.put(PARAGRAPH_QNAME, "<p style=\"text-align: left\">&middot;&middot;&middot;</p>");
    }
}

