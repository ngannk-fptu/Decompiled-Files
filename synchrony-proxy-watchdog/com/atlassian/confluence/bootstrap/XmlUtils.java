/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.bootstrap;

import com.atlassian.security.xml.SecureXmlParserFactory;
import java.io.File;
import java.util.Optional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlUtils {
    public static Optional<String> getAttributeFromXmlFile(File xmlFile, String attributeName, String expression) {
        if (!xmlFile.exists()) {
            return Optional.empty();
        }
        DocumentBuilderFactory factory = SecureXmlParserFactory.newDocumentBuilderFactory();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            XPathFactory xPathfactory = XPathFactory.newInstance("http://java.sun.com/jaxp/xpath/dom");
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr = xpath.compile(expression);
            NodeList nodeList = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
            if (nodeList.getLength() >= 1) {
                String substitutionKey;
                String substitutionValue;
                Node resource = nodeList.item(0);
                Element element = (Element)resource;
                String attributeValue = element.getAttribute(attributeName);
                if (attributeValue.startsWith("${") && attributeValue.endsWith("}") && (substitutionValue = System.getProperty(substitutionKey = attributeValue.replace("${", "").replace("}", ""))) != null && !substitutionValue.isEmpty()) {
                    return Optional.of(substitutionValue);
                }
                return Optional.of(attributeValue);
            }
            return Optional.empty();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }
}

