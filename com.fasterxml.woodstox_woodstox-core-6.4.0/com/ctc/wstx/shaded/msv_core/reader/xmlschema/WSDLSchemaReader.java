/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaGrammar;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController2;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.EmbeddedSchema;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.MultiSchemaReader;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.WSDLGrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class WSDLSchemaReader {
    private WSDLSchemaReader() {
    }

    public static XMLSchemaGrammar read(Source wsdlSource, SAXParserFactory factory, GrammarReaderController2 controller) throws XPathExpressionException, TransformerConfigurationException, TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        DOMResult wsdlDom = new DOMResult();
        transformerFactory.newTransformer().transform(wsdlSource, wsdlDom);
        Node wsdl = wsdlDom.getNode();
        HashMap<String, String> wsdlNamespaceMappings = new HashMap<String, String>();
        Document wsdlDoc = (Document)wsdl;
        NamedNodeMap attrMap = wsdlDoc.getDocumentElement().getAttributes();
        if (attrMap != null) {
            for (int x = 0; x < attrMap.getLength(); ++x) {
                Attr attr = (Attr)attrMap.item(x);
                String ns = attr.getNamespaceURI();
                if (!"http://www.w3.org/2000/xmlns/".equals(ns)) continue;
                String localName = attr.getLocalName();
                String uri = attr.getValue();
                wsdlNamespaceMappings.put(localName, uri);
            }
        }
        String wsdlSystemId = wsdlSource.getSystemId();
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new SimpleNamespaceContext());
        HashMap<String, EmbeddedSchema> schemas = new HashMap<String, EmbeddedSchema>();
        NodeList schemaNodes = (NodeList)xpath.evaluate("//xs:schema", wsdl, XPathConstants.NODESET);
        for (int x = 0; x < schemaNodes.getLength(); ++x) {
            Element schema = (Element)schemaNodes.item(x);
            String targetNamespace = schema.getAttribute("targetNamespace");
            String systemId = wsdlSystemId + "#" + x;
            EmbeddedSchema embeddedWSDLSchema = new EmbeddedSchema(systemId, schema);
            schemas.put(targetNamespace, embeddedWSDLSchema);
        }
        WSDLGrammarReaderController wsdlController = new WSDLGrammarReaderController(controller, wsdlSystemId, schemas);
        XMLSchemaReader reader = new XMLSchemaReader(wsdlController);
        reader.setAdditionalNamespaceMap(wsdlNamespaceMappings);
        MultiSchemaReader multiSchemaReader = new MultiSchemaReader(reader);
        for (EmbeddedSchema schema : schemas.values()) {
            DOMSource source = new DOMSource(schema.getSchemaElement());
            source.setSystemId(schema.getSystemId());
            multiSchemaReader.parse(source);
        }
        return multiSchemaReader.getResult();
    }

    private static final class SimpleNamespaceContext
    implements NamespaceContext {
        private SimpleNamespaceContext() {
        }

        public String getNamespaceURI(String prefix) {
            if ("xs".equals(prefix)) {
                return "http://www.w3.org/2001/XMLSchema";
            }
            return null;
        }

        public String getPrefix(String namespaceURI) {
            if ("http://www.w3.org/2001/XMLSchema".equals(namespaceURI)) {
                return "xs";
            }
            return null;
        }

        public Iterator getPrefixes(String namespaceURI) {
            ArrayList<String> prefixes = new ArrayList<String>();
            if ("http://www.w3.org/2001/XMLSchema".equals(namespaceURI)) {
                prefixes.add("xs");
            }
            return prefixes.iterator();
        }
    }
}

