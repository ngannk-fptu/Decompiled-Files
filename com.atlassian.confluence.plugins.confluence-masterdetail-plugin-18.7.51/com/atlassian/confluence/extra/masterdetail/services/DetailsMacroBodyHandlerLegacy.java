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
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Document
 *  org.dom4j.DocumentException
 *  org.dom4j.Element
 *  org.dom4j.Node
 *  org.dom4j.XPath
 *  org.dom4j.io.SAXReader
 *  org.dom4j.tree.DefaultElement
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.masterdetail.services;

import com.atlassian.confluence.content.render.xhtml.Namespace;
import com.atlassian.confluence.content.render.xhtml.XhtmlConstants;
import com.atlassian.confluence.extra.masterdetail.analytics.DetailsSummaryMacroMetricsEvent;
import com.atlassian.confluence.extra.masterdetail.services.DetailsMacroBodyHandler;
import com.atlassian.confluence.extra.masterdetail.services.DetailsMacroBodyHandlerFastParse;
import com.atlassian.confluence.plugins.pageproperties.api.model.PageProperty;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class DetailsMacroBodyHandlerLegacy
implements DetailsMacroBodyHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DetailsMacroBodyHandlerFastParse.class);
    public static final String CHARSET_UTF8 = "UTF-8";
    private final Map<String, ImmutableList<ImmutableMap<String, PageProperty>>> detailsById;
    private static final String XHTML_NAMESPACE_PREFIX = "xhtml";
    private static final String XPATH_TBODY = "//xhtml:tbody";
    private static final Map<String, String> NAMESPACE_MAP = new ConcurrentHashMap<String, String>(XhtmlConstants.STORAGE_NAMESPACES.size());
    private final DetailsSummaryMacroMetricsEvent.Builder metrics;

    DetailsMacroBodyHandlerLegacy(DetailsSummaryMacroMetricsEvent.Builder metrics) {
        this.metrics = metrics;
        this.detailsById = Maps.newHashMap();
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

    private ImmutableMap<String, PageProperty> extractDetails(String macroBodyXhtml) throws IOException, DocumentException, ParserConfigurationException, SAXException {
        Document macroBodyDoc = this.getMacroBodyDocument(macroBodyXhtml);
        XPath xpath = macroBodyDoc.createXPath(XPATH_TBODY);
        xpath.setNamespaceURIs(NAMESPACE_MAP);
        ImmutableMap emptyMap = ImmutableMap.of();
        Element tableElement = (Element)xpath.selectSingleNode((Object)macroBodyDoc);
        if (tableElement == null) {
            return emptyMap;
        }
        List rowElements = tableElement.elements("tr");
        if (rowElements == null) {
            return emptyMap;
        }
        return this.loadDetailPairsFromTableRows(rowElements);
    }

    private ImmutableMap<String, PageProperty> loadDetailPairsFromTableRows(List<Element> rowElements) throws IOException {
        boolean firstRowIsThs = false;
        ArrayList keyElements = Lists.newArrayList();
        ArrayList valueElements = Lists.newArrayList();
        for (Element rowElem : rowElements) {
            List tds = rowElem.elements("td");
            List ths = rowElem.elements("th");
            if (!tds.isEmpty()) {
                if (firstRowIsThs) {
                    valueElements = Lists.newArrayList((Iterable)tds);
                    break;
                }
                if (!ths.isEmpty()) {
                    keyElements.add((Element)ths.get(0));
                    valueElements.add((Element)tds.get(0));
                    continue;
                }
                keyElements.add((Element)tds.get(0));
                valueElements.add(tds.size() > 1 ? (Element)tds.get(1) : null);
                continue;
            }
            if (ths.isEmpty()) continue;
            keyElements = Lists.newArrayList((Iterable)ths);
            firstRowIsThs = true;
        }
        HashMap<String, PageProperty> results = new HashMap<String, PageProperty>();
        for (int i = 0; i < keyElements.size(); ++i) {
            Element keyElement = (Element)keyElements.get(i);
            Element valueElement = valueElements.size() > i ? (Element)valueElements.get(i) : null;
            String key = this.getKeyText(keyElement);
            if (results.containsKey(key)) continue;
            results.put(key, new PageProperty(this.getInnerHtml(valueElement), this.getInnerHtml(keyElement)));
        }
        return ImmutableMap.copyOf(results);
    }

    private String getKeyText(Element element) throws IOException {
        String key;
        if (element == null) {
            return "";
        }
        if (element.isTextOnly()) {
            key = element.getText();
        } else {
            StringWriter stringWriter = new StringWriter();
            Iterator it = element.nodeIterator();
            while (it.hasNext()) {
                Node node = (Node)it.next();
                if (node instanceof DefaultElement) {
                    DefaultElement defaultElement = (DefaultElement)node;
                    ((Writer)stringWriter).append(defaultElement.getStringValue());
                    continue;
                }
                ((Writer)stringWriter).append(node.getText());
            }
            key = ((Object)stringWriter).toString();
        }
        return StringUtils.remove((String)StringEscapeUtils.escapeHtml4((String)key), (String)"&nbsp;");
    }

    private String getInnerHtml(Element element) throws IOException {
        if (element == null) {
            return "";
        }
        if (element.isTextOnly()) {
            return StringEscapeUtils.escapeHtml4((String)element.getText());
        }
        StringWriter stringWriter = new StringWriter();
        Iterator it = element.nodeIterator();
        while (it.hasNext()) {
            Node node = (Node)it.next();
            node.write((Writer)stringWriter);
        }
        return ((Object)stringWriter).toString();
    }

    private Document getMacroBodyDocument(String macroBodyXhtml) throws DocumentException, UnsupportedEncodingException, ParserConfigurationException, SAXException {
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
        ByteArrayInputStream is = new ByteArrayInputStream(builder.toString().getBytes(CHARSET_UTF8));
        SAXReader saxReader = new SAXReader(false);
        return saxReader.read((InputStream)is);
    }

    static {
        for (Namespace namespace : XhtmlConstants.STORAGE_NAMESPACES) {
            NAMESPACE_MAP.put(namespace.getPrefix() != null ? namespace.getPrefix() : XHTML_NAMESPACE_PREFIX, namespace.getUri());
        }
    }
}

