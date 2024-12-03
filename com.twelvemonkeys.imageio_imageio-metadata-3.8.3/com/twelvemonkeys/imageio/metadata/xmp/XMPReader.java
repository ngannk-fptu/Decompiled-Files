/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.util.IIOUtil
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.metadata.xmp;

import com.twelvemonkeys.imageio.metadata.Directory;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.MetadataReader;
import com.twelvemonkeys.imageio.metadata.xmp.RDFDescription;
import com.twelvemonkeys.imageio.metadata.xmp.XMP;
import com.twelvemonkeys.imageio.metadata.xmp.XMPDirectory;
import com.twelvemonkeys.imageio.metadata.xmp.XMPEntry;
import com.twelvemonkeys.imageio.util.IIOUtil;
import com.twelvemonkeys.lang.Validate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.IIOException;
import javax.imageio.stream.ImageInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public final class XMPReader
extends MetadataReader {
    @Override
    public Directory read(ImageInputStream imageInputStream) throws IOException {
        Validate.notNull((Object)imageInputStream, (String)"input");
        try {
            DocumentBuilderFactory documentBuilderFactory = this.createDocumentBuilderFactory();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            documentBuilder.setErrorHandler(new DefaultHandler());
            Document document = documentBuilder.parse(new InputSource(IIOUtil.createStreamAdapter((ImageInputStream)imageInputStream)));
            String string = this.getToolkit(document);
            Node node = document.getElementsByTagNameNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "RDF").item(0);
            NodeList nodeList = document.getElementsByTagNameNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "Description");
            return this.parseDirectories(node, nodeList, string);
        }
        catch (SAXException sAXException) {
            throw new IIOException(sAXException.getMessage(), sAXException);
        }
        catch (ParserConfigurationException parserConfigurationException) {
            throw new RuntimeException(parserConfigurationException);
        }
    }

    private DocumentBuilderFactory createDocumentBuilderFactory() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setXIncludeAware(false);
        documentBuilderFactory.setExpandEntityReferences(false);
        documentBuilderFactory.setAttribute("http://javax.xml.XMLConstants/feature/secure-processing", true);
        documentBuilderFactory.setAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD", "");
        documentBuilderFactory.setAttribute("http://javax.xml.XMLConstants/property/accessExternalSchema", "");
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        return documentBuilderFactory;
    }

    private String getToolkit(Document document) {
        NodeList nodeList = document.getElementsByTagNameNS("adobe:ns:meta/", "xmpmeta");
        if (nodeList == null || nodeList.getLength() <= 0) {
            return null;
        }
        Node node = nodeList.item(0).getAttributes().getNamedItemNS("adobe:ns:meta/", "xmptk");
        return node != null ? node.getNodeValue() : null;
    }

    private XMPDirectory parseDirectories(Node node, NodeList nodeList, String string) {
        LinkedHashMap<String, List<Entry>> linkedHashMap = new LinkedHashMap<String, List<Entry>>();
        for (Node object : this.asIterable(nodeList)) {
            if (object.getParentNode() != node) continue;
            this.parseAttributesForKnownElements(linkedHashMap, object);
            for (Node node2 : this.asIterable(object.getChildNodes())) {
                Object object2;
                if (node2.getNodeType() != 1) continue;
                ArrayList<XMPEntry> arrayList = (ArrayList<XMPEntry>)linkedHashMap.get(node2.getNamespaceURI());
                if (arrayList == null) {
                    arrayList = new ArrayList<XMPEntry>();
                    linkedHashMap.put(node2.getNamespaceURI(), arrayList);
                }
                if (this.isResourceType(node2)) {
                    object2 = this.parseAsResource(node2);
                } else {
                    LinkedHashMap<String, List<Entry>> linkedHashMap2 = new LinkedHashMap<String, List<Entry>>();
                    this.parseAttributesForKnownElements(linkedHashMap2, node2);
                    if (!linkedHashMap2.isEmpty()) {
                        ArrayList arrayList2 = new ArrayList(linkedHashMap2.size());
                        for (Map.Entry entry : linkedHashMap2.entrySet()) {
                            arrayList2.addAll((Collection)entry.getValue());
                        }
                        object2 = new RDFDescription(arrayList2);
                    } else {
                        object2 = this.getChildTextValue(node2);
                    }
                }
                arrayList.add(new XMPEntry(node2.getNamespaceURI() + node2.getLocalName(), node2.getLocalName(), object2));
            }
        }
        ArrayList arrayList = new ArrayList(linkedHashMap.size());
        for (Map.Entry entry : linkedHashMap.entrySet()) {
            arrayList.add(new RDFDescription((String)entry.getKey(), (Collection)entry.getValue()));
        }
        return new XMPDirectory(arrayList, string);
    }

    private boolean isResourceType(Node node) {
        Node node2 = node.getAttributes().getNamedItemNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "parseType");
        return node2 != null && "Resource".equals(node2.getNodeValue());
    }

    private RDFDescription parseAsResource(Node node) {
        ArrayList<XMPEntry> arrayList = new ArrayList<XMPEntry>();
        for (Node node2 : this.asIterable(node.getChildNodes())) {
            if (node2.getNodeType() != 1) continue;
            arrayList.add(new XMPEntry(node2.getNamespaceURI() + node2.getLocalName(), node2.getLocalName(), this.getChildTextValue(node2)));
        }
        return new RDFDescription(arrayList);
    }

    private void parseAttributesForKnownElements(Map<String, List<Entry>> map, Node node) {
        NamedNodeMap namedNodeMap = node.getAttributes();
        for (Node node2 : this.asIterable(namedNodeMap)) {
            if (!XMP.ELEMENTS.contains(node2.getNamespaceURI())) continue;
            List<Entry> list = map.get(node2.getNamespaceURI());
            if (list == null) {
                list = new ArrayList<Entry>();
                map.put(node2.getNamespaceURI(), list);
            }
            list.add(new XMPEntry(node2.getNamespaceURI() + node2.getLocalName(), node2.getLocalName(), node2.getNodeValue()));
        }
    }

    private Object getChildTextValue(Node node) {
        for (Node object2 : this.asIterable(node.getChildNodes())) {
            if ("http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(object2.getNamespaceURI()) && "Alt".equals(object2.getLocalName())) {
                LinkedHashMap<String, Object> arrayList = new LinkedHashMap<String, Object>();
                for (Node node4 : this.asIterable(object2.getChildNodes())) {
                    if (!"http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(node4.getNamespaceURI()) || !"li".equals(node4.getLocalName())) continue;
                    NamedNodeMap object3 = node4.getAttributes();
                    Node node2 = object3.getNamedItem("xml:lang");
                    arrayList.put(node2 == null ? null : node2.getTextContent(), this.getChildTextValue(node4));
                }
                return arrayList;
            }
            if (!"http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(object2.getNamespaceURI()) || !"Seq".equals(object2.getLocalName()) && !"Bag".equals(object2.getLocalName())) continue;
            ArrayList<Object> arrayList = new ArrayList<Object>();
            for (Node node3 : this.asIterable(object2.getChildNodes())) {
                if (!"http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(node3.getNamespaceURI()) || !"li".equals(node3.getLocalName())) continue;
                Object object = this.getChildTextValue(node3);
                arrayList.add(object);
            }
            return Collections.unmodifiableList(arrayList);
        }
        if (this.isResourceType(node)) {
            return this.parseAsResource(node);
        }
        Node node5 = node.getFirstChild();
        String string = node5 != null ? node5.getNodeValue() : null;
        return string != null ? string.trim() : "";
    }

    private Iterable<? extends Node> asIterable(final NamedNodeMap namedNodeMap) {
        return new Iterable<Node>(){

            @Override
            public Iterator<Node> iterator() {
                return new Iterator<Node>(){
                    private int index;

                    @Override
                    public boolean hasNext() {
                        return namedNodeMap != null && namedNodeMap.getLength() > this.index;
                    }

                    @Override
                    public Node next() {
                        return namedNodeMap.item(this.index++);
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("Method remove not supported");
                    }
                };
            }
        };
    }

    private Iterable<? extends Node> asIterable(final NodeList nodeList) {
        return new Iterable<Node>(){

            @Override
            public Iterator<Node> iterator() {
                return new Iterator<Node>(){
                    private int index;

                    @Override
                    public boolean hasNext() {
                        return nodeList != null && nodeList.getLength() > this.index;
                    }

                    @Override
                    public Node next() {
                        return nodeList.item(this.index++);
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("Method remove not supported");
                    }
                };
            }
        };
    }
}

