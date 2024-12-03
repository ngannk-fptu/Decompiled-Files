/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.Namespace
 *  com.atlassian.confluence.content.render.xhtml.XhtmlConstants
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.text.StringEscapeUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.masterdetail.services;

import com.atlassian.confluence.content.render.xhtml.Namespace;
import com.atlassian.confluence.content.render.xhtml.XhtmlConstants;
import com.atlassian.confluence.extra.masterdetail.analytics.DetailsSummaryMacroMetricsEvent;
import com.atlassian.confluence.extra.masterdetail.services.DetailsMacroBodyHandler;
import com.atlassian.confluence.plugins.pageproperties.api.model.PageProperty;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DetailsMacroBodyHandlerFastParse
implements DetailsMacroBodyHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DetailsMacroBodyHandlerFastParse.class);
    public static final String CHARSET_UTF8 = "UTF-8";
    private final Map<String, ImmutableList<ImmutableMap<String, PageProperty>>> detailsById;
    private static final String XHTML_NAMESPACE_PREFIX = "xhtml";
    private static final String XPATH_TBODY = "//xhtml:tbody";
    private static final QName TR_QNAME = new QName("http://www.w3.org/1999/xhtml", "tr");
    private static final QName TD_QNAME = new QName("http://www.w3.org/1999/xhtml", "td");
    private static final QName TH_QNAME = new QName("http://www.w3.org/1999/xhtml", "th");
    private static final Map<String, String> NAMESPACE_MAP = new ConcurrentHashMap<String, String>(XhtmlConstants.STORAGE_NAMESPACES.size());
    private final DetailsSummaryMacroMetricsEvent.Builder metrics;
    private final XMLEventReaderSupplier xmlEventReaderSupplier;

    DetailsMacroBodyHandlerFastParse(DetailsSummaryMacroMetricsEvent.Builder metrics, XMLEventReaderSupplier xmlEventReaderSupplier) {
        this.metrics = metrics;
        this.detailsById = Maps.newHashMap();
        this.xmlEventReaderSupplier = xmlEventReaderSupplier;
    }

    public static String readElementBody(XMLEventReader eventReader) throws XMLStreamException {
        StringWriter buf = new StringWriter(1024);
        int depth = 0;
        while (eventReader.hasNext()) {
            XMLEvent xmlEvent = eventReader.peek();
            if (xmlEvent.isStartElement()) {
                ++depth;
            } else if (xmlEvent.isEndElement() && --depth < 0) break;
            xmlEvent = eventReader.nextEvent();
            if (xmlEvent.isCharacters()) {
                Characters xmlEventCharacters = xmlEvent.asCharacters();
                if (xmlEventCharacters.isCData()) {
                    buf.append("<![CDATA[").append(xmlEventCharacters.getData()).append("]]>");
                    continue;
                }
                buf.append(StringEscapeUtils.escapeHtml4((String)xmlEventCharacters.getData()));
                continue;
            }
            xmlEvent.writeAsEncodedUnicode(buf);
        }
        return buf.getBuffer().toString();
    }

    public static String readElementBodyCharacters(XMLEventReader eventReader) throws XMLStreamException {
        StringWriter buf = new StringWriter(1024);
        int depth = 0;
        while (eventReader.hasNext()) {
            XMLEvent xmlEvent = eventReader.peek();
            if (xmlEvent.isStartElement()) {
                ++depth;
            } else if (xmlEvent.isEndElement() && --depth < 0) break;
            if (!(xmlEvent = eventReader.nextEvent()).isCharacters()) continue;
            xmlEvent.asCharacters().writeAsEncodedUnicode(buf);
        }
        String body = buf.getBuffer().toString();
        return StringEscapeUtils.escapeHtml4((String)body);
    }

    @Override
    public void handle(MacroDefinition macroDefinition) {
        if (!"details".equals(macroDefinition.getName())) {
            return;
        }
        String bodyText = macroDefinition.getBodyText();
        String detailsId = StringUtils.trim((String)macroDefinition.getParameter("id"));
        if (detailsId == null) {
            detailsId = "";
        }
        if (StringUtils.isBlank((CharSequence)bodyText)) {
            this.addToDetails(detailsId, (ImmutableMap<String, PageProperty>)ImmutableMap.of());
            return;
        }
        try {
            this.metrics.detailsExtractionStart();
            ImmutableMap<String, PageProperty> extractedDetails = this.extractDetails(bodyText);
            this.metrics.detailsExtractionFinish(extractedDetails.size());
            this.addToDetails(detailsId, extractedDetails);
        }
        catch (Exception e) {
            LOG.error(String.format("Unable to parse detailsById in detailsById macro\n%s", bodyText), (Throwable)e);
        }
    }

    @Override
    public List<? extends Map<String, PageProperty>> getDetails(String detailsId) {
        return (List)this.detailsById.get(detailsId);
    }

    @Override
    public ImmutableMap<String, ImmutableList<ImmutableMap<String, PageProperty>>> getDetails() {
        return ImmutableMap.copyOf(this.detailsById);
    }

    private void addToDetails(String id, ImmutableMap<String, PageProperty> details) {
        List currentDetails = (List)this.detailsById.get(id);
        ArrayList newDetails = currentDetails == null ? Lists.newArrayList() : Lists.newArrayList((Iterable)currentDetails);
        newDetails.add(details);
        this.detailsById.put(id, (ImmutableList<ImmutableMap<String, PageProperty>>)ImmutableList.copyOf((Collection)newDetails));
    }

    private ImmutableMap<String, PageProperty> extractDetails(String macroBodyXhtml) throws IOException, XMLStreamException {
        XMLEventReader reader = this.getEventReader(macroBodyXhtml);
        ArrayList outer = Lists.newArrayList();
        ArrayList inner = Lists.newArrayList();
        int rowIndex = -1;
        int columnIndex = -1;
        boolean firstRowIsThs = false;
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            switch (event.getEventType()) {
                case 1: {
                    StartElement startElement = event.asStartElement();
                    if (TR_QNAME.equals(startElement.getName())) {
                        ++rowIndex;
                        break;
                    }
                    if (TD_QNAME.equals(startElement.getName())) {
                        if (rowIndex == 0 && firstRowIsThs) {
                            firstRowIsThs = false;
                        }
                        ++columnIndex;
                        inner.add(DetailsMacroBodyHandlerFastParse.readElementBody(reader));
                        break;
                    }
                    if (!TH_QNAME.equals(startElement.getName())) break;
                    inner.add(DetailsMacroBodyHandlerFastParse.readElementBody(reader));
                    if (rowIndex == 0 && ++columnIndex == 0) {
                        firstRowIsThs = true;
                        break;
                    }
                    if (columnIndex != 0 || !firstRowIsThs) break;
                    firstRowIsThs = false;
                    break;
                }
                case 2: {
                    if (!TR_QNAME.equals(event.asEndElement().getName())) break;
                    outer.add(inner);
                    inner = Lists.newArrayList();
                    columnIndex = -1;
                    break;
                }
            }
        }
        reader.close();
        HashMap<String, PageProperty> results = new HashMap<String, PageProperty>();
        if (firstRowIsThs) {
            List keys = (List)outer.get(0);
            List values = outer.size() > 1 ? (List)outer.get(1) : keys;
            for (int i = 0; i < keys.size(); ++i) {
                String key = (String)keys.get(i);
                String value = (String)values.get(i);
                String keyText = this.getKeyText(key);
                results.put(keyText, new PageProperty(value, key));
            }
        } else {
            for (List row : outer) {
                String key = (String)row.get(0);
                String value = row.size() > 1 ? (String)row.get(1) : key;
                String keyText = this.getKeyText(key);
                if (results.containsKey(key)) continue;
                results.put(keyText, new PageProperty(value, key));
            }
        }
        return ImmutableMap.copyOf(results);
    }

    private String getKeyText(String keyMarkup) {
        String keyMarkupNoNBSP = StringUtils.remove((String)keyMarkup, (String)"&nbsp;");
        if (!keyMarkup.contains("<")) {
            return keyMarkupNoNBSP;
        }
        try {
            return DetailsMacroBodyHandlerFastParse.readElementBodyCharacters(this.getEventReader(StringEscapeUtils.unescapeHtml4((String)keyMarkupNoNBSP)));
        }
        catch (Exception e) {
            return keyMarkupNoNBSP;
        }
    }

    private XMLEventReader getEventReader(String macroBodyXhtml) throws XMLStreamException, UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE xml>").append("<xml");
        for (Namespace namespace : XhtmlConstants.STORAGE_NAMESPACES) {
            builder.append(" xmlns");
            if (!namespace.isDefaultNamespace()) {
                builder.append(":").append(namespace.getPrefix());
            }
            builder.append("=\"").append(namespace.getUri()).append("\"");
            if (!namespace.isDefaultNamespace()) continue;
            builder.append(" xmlns:xhtml=\"").append(namespace.getUri()).append("\"");
        }
        builder.append(">").append(macroBodyXhtml).append("</xml>");
        StringReader xmlStringReader = new StringReader(builder.toString());
        return this.xmlEventReaderSupplier.supplyXMLEventReader(xmlStringReader);
    }

    static {
        for (Namespace namespace : XhtmlConstants.STORAGE_NAMESPACES) {
            NAMESPACE_MAP.put(namespace.getPrefix() != null ? namespace.getPrefix() : XHTML_NAMESPACE_PREFIX, namespace.getUri());
        }
    }

    @FunctionalInterface
    public static interface XMLEventReaderSupplier {
        public XMLEventReader supplyXMLEventReader(Reader var1) throws XMLStreamException;
    }
}

