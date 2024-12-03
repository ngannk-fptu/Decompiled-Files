/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.sisyphus.application.properties;

import com.atlassian.sisyphus.application.properties.meta.InstanceInfoMeta;
import com.atlassian.sisyphus.application.properties.meta.JvmMeta;
import com.atlassian.sisyphus.application.properties.meta.ParseMeta;
import com.atlassian.sisyphus.application.properties.meta.PluginMeta;
import com.atlassian.sisyphus.application.properties.meta.ServiceMeta;
import com.atlassian.sisyphus.application.properties.meta.UpgradeMeta;
import com.atlassian.sisyphus.dm.PropScanResult;
import com.atlassian.sisyphus.dm.ScannedProperty;
import com.atlassian.sisyphus.dm.ScannedPropertySet;
import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class PropertyParser {
    private static final Logger log = LoggerFactory.getLogger(PropertyParser.class);
    private final XPath xpath;
    private List<ParseMeta> metaset = Lists.newArrayList();
    private Document doc;

    public Document getDoc() {
        return this.doc;
    }

    public PropertyParser() {
        this.metaset.add(new InstanceInfoMeta());
        this.metaset.add(new JvmMeta());
        this.metaset.add(new PluginMeta());
        this.metaset.add(new ServiceMeta());
        this.metaset.add(new UpgradeMeta());
        this.xpath = XPathFactory.newInstance().newXPath();
    }

    private InputStream clean(BufferedReader br) throws IOException {
        String line;
        StringBuilder cleanedData = new StringBuilder();
        Pattern pattern = Pattern.compile(".*?<(.+?\\s+.+?)>(.*?)<.+?\\s+.+?>.*?");
        boolean ignore = false;
        while ((line = br.readLine()) != null) {
            if (line.contains("<application-properties>")) {
                ignore = true;
            }
            if (!ignore) {
                if (line.contains("org-apache-kahadb-util-LockFile")) continue;
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    String tag = matcher.group(1).replaceAll(" ", "-");
                    if (tag.contains("Support-Entitlement-Number")) {
                        tag = "sen";
                    }
                    line = String.format("<%s>%s</%s>", tag, matcher.group(2), tag);
                }
                cleanedData.append(line);
            }
            if (!line.contains("</application-properties>")) continue;
            ignore = false;
        }
        return new ByteArrayInputStream(cleanedData.toString().getBytes());
    }

    public PropScanResult parse(BufferedReader input) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            this.doc = builder.parse(this.clean(input));
            PropScanResult propScanResult = new PropScanResult();
            for (ParseMeta meta : this.metaset) {
                this.processMeta(meta, propScanResult);
            }
            return propScanResult;
        }
        catch (IOException e) {
            log.error("IOException parsing for properties", (Throwable)e);
        }
        catch (ParserConfigurationException e) {
            log.error("Error configuring parser for parsing for properties", (Throwable)e);
        }
        catch (SAXException e) {
            log.error("SAX error configuring parser for parsing for properties", (Throwable)e);
        }
        return null;
    }

    private void processMeta(ParseMeta meta, PropScanResult propScanResult) {
        try {
            List<Node> nodes = this.getParentNodes(meta);
            for (Node node : nodes) {
                ScannedPropertySet properties = new ScannedPropertySet();
                properties.setTitle(meta.getTitle());
                propScanResult.addProperties(properties);
                this.fillProperties(meta, properties, node);
            }
        }
        catch (XPathExpressionException e) {
            log.error("Processing failed for meta group {}", (Object)meta.getTitle(), (Object)e);
        }
    }

    private void fillProperties(ParseMeta meta, ScannedPropertySet properties, Node node) throws XPathExpressionException {
        if (meta.getPathMap().size() == 0) {
            log.error("Empty mapping paths");
            return;
        }
        for (Map.Entry<String, List<String>> entry : meta.getPathMap().entrySet()) {
            List<String> paths = entry.getValue();
            boolean found = false;
            if (paths.size() == 0) {
                log.error("Empty paths for meta {}", (Object)meta.getTitle());
            }
            for (String path : paths) {
                XPathExpression expr = this.xpath.compile(path);
                Object result = expr.evaluate(node, XPathConstants.NODESET);
                NodeList nodes = (NodeList)result;
                if (nodes.getLength() <= 0) continue;
                log.debug("Setting {} to {}", (Object)entry.getKey(), (Object)nodes.item(0).getTextContent());
                ScannedProperty property = new ScannedProperty();
                property.setName(entry.getKey());
                if (!path.contains("@")) {
                    property.setValue(nodes.item(0).getTextContent());
                } else {
                    Matcher matcher = Pattern.compile(".+?\\[@(.+?)\\]").matcher(path);
                    if (matcher.find()) {
                        property.setValue(nodes.item(0).getAttributes().getNamedItem(matcher.group(1)).getNodeValue());
                    } else {
                        log.error("Unable to get attribute value");
                    }
                }
                properties.addScannedProperty(property);
                found = true;
                break;
            }
            if (found) continue;
            log.error("No matching entries found for - {}", (Object)meta.getTitle());
        }
        if (properties.getProperties().size() == 0) {
            log.error("Parse error, no properties detected");
        }
    }

    private List<Node> getParentNodes(ParseMeta meta) throws XPathExpressionException {
        ArrayList nodes = Lists.newArrayList();
        if (meta.getGroupNode().size() != 0) {
            for (String group : meta.getGroupNode()) {
                XPathExpression expr = this.xpath.compile(group);
                Object result = expr.evaluate(this.doc, XPathConstants.NODESET);
                NodeList parentNodes = (NodeList)result;
                for (int i = 0; i < parentNodes.getLength(); ++i) {
                    nodes.add(parentNodes.item(i));
                }
            }
            if (nodes.size() == 0) {
                log.error("No parent nodes found - {}", (Object)meta.getTitle());
            }
        } else {
            nodes.add(this.doc.getDocumentElement());
        }
        return nodes;
    }
}

