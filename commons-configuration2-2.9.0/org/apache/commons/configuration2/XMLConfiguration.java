/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package org.apache.commons.configuration2;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.configuration2.BaseHierarchicalConfiguration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.XMLDocumentHelper;
import org.apache.commons.configuration2.XMLListReference;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.ConfigurationLogger;
import org.apache.commons.configuration2.io.FileLocator;
import org.apache.commons.configuration2.io.FileLocatorAware;
import org.apache.commons.configuration2.io.InputStreamSupport;
import org.apache.commons.configuration2.resolver.DefaultEntityResolver;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.tree.NodeTreeWalker;
import org.apache.commons.configuration2.tree.ReferenceNodeHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLConfiguration
extends BaseHierarchicalConfiguration
implements FileBasedConfiguration,
FileLocatorAware,
InputStreamSupport {
    static final int DEFAULT_INDENT_SIZE = 2;
    static final String INDENT_AMOUNT_PROPERTY = "{http://xml.apache.org/xslt}indent-amount";
    private static final String DEFAULT_ROOT_NAME = "configuration";
    private static final String ATTR_SPACE = "xml:space";
    private static final String ATTR_SPACE_INTERNAL = "config-xml:space";
    private static final String VALUE_PRESERVE = "preserve";
    private static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    private static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    private String rootElementName;
    private String publicID;
    private String systemID;
    private DocumentBuilder documentBuilder;
    private boolean validating;
    private boolean schemaValidation;
    private EntityResolver entityResolver = new DefaultEntityResolver();
    private FileLocator locator;

    public XMLConfiguration() {
        this.initLogger(new ConfigurationLogger(XMLConfiguration.class));
    }

    public XMLConfiguration(HierarchicalConfiguration<ImmutableNode> c) {
        super(c);
        this.rootElementName = c != null ? c.getRootElementName() : null;
        this.initLogger(new ConfigurationLogger(XMLConfiguration.class));
    }

    @Override
    protected String getRootElementNameInternal() {
        Document doc = this.getDocument();
        if (doc == null) {
            return this.rootElementName == null ? DEFAULT_ROOT_NAME : this.rootElementName;
        }
        return doc.getDocumentElement().getNodeName();
    }

    public void setRootElementName(String name) {
        this.beginRead(true);
        try {
            if (this.getDocument() != null) {
                throw new UnsupportedOperationException("The name of the root element cannot be changed when loaded from an XML document!");
            }
            this.rootElementName = name;
        }
        finally {
            this.endRead();
        }
    }

    public DocumentBuilder getDocumentBuilder() {
        return this.documentBuilder;
    }

    public void setDocumentBuilder(DocumentBuilder documentBuilder) {
        this.documentBuilder = documentBuilder;
    }

    public String getPublicID() {
        this.beginRead(false);
        try {
            String string = this.publicID;
            return string;
        }
        finally {
            this.endRead();
        }
    }

    public void setPublicID(String publicID) {
        this.beginWrite(false);
        try {
            this.publicID = publicID;
        }
        finally {
            this.endWrite();
        }
    }

    public String getSystemID() {
        this.beginRead(false);
        try {
            String string = this.systemID;
            return string;
        }
        finally {
            this.endRead();
        }
    }

    public void setSystemID(String systemID) {
        this.beginWrite(false);
        try {
            this.systemID = systemID;
        }
        finally {
            this.endWrite();
        }
    }

    public boolean isValidating() {
        return this.validating;
    }

    public void setValidating(boolean validating) {
        if (!this.schemaValidation) {
            this.validating = validating;
        }
    }

    public boolean isSchemaValidation() {
        return this.schemaValidation;
    }

    public void setSchemaValidation(boolean schemaValidation) {
        this.schemaValidation = schemaValidation;
        if (schemaValidation) {
            this.validating = true;
        }
    }

    public void setEntityResolver(EntityResolver resolver) {
        this.entityResolver = resolver;
    }

    public EntityResolver getEntityResolver() {
        return this.entityResolver;
    }

    public Document getDocument() {
        XMLDocumentHelper docHelper = this.getDocumentHelper();
        return docHelper != null ? docHelper.getDocument() : null;
    }

    private XMLDocumentHelper getDocumentHelper() {
        ReferenceNodeHandler handler = this.getReferenceHandler();
        return (XMLDocumentHelper)handler.getReference((ImmutableNode)handler.getRootNode());
    }

    private ReferenceNodeHandler getReferenceHandler() {
        return this.getSubConfigurationParentModel().getReferenceNodeHandler();
    }

    private void initProperties(XMLDocumentHelper docHelper, boolean elemRefs) {
        Document document = docHelper.getDocument();
        this.setPublicID(docHelper.getSourcePublicID());
        this.setSystemID(docHelper.getSourceSystemID());
        ImmutableNode.Builder rootBuilder = new ImmutableNode.Builder();
        MutableObject rootValue = new MutableObject();
        HashMap<ImmutableNode, Object> elemRefMap = elemRefs ? new HashMap<ImmutableNode, Object>() : null;
        Map<String, String> attributes = this.constructHierarchy(rootBuilder, (MutableObject<String>)rootValue, document.getDocumentElement(), elemRefMap, true, 0);
        attributes.remove(ATTR_SPACE_INTERNAL);
        ImmutableNode top = rootBuilder.value(rootValue.getValue()).addAttributes(attributes).create();
        this.getSubConfigurationParentModel().mergeRoot(top, document.getDocumentElement().getTagName(), elemRefMap, elemRefs ? docHelper : null, this);
    }

    private Map<String, String> constructHierarchy(ImmutableNode.Builder node, MutableObject<String> refValue, Element element, Map<ImmutableNode, Object> elemRefs, boolean trim, int level) {
        String text;
        boolean trimFlag = XMLConfiguration.shouldTrim(element, trim);
        Map<String, String> attributes = XMLConfiguration.processAttributes(element);
        attributes.put(ATTR_SPACE_INTERNAL, String.valueOf(trimFlag));
        StringBuilder buffer = new StringBuilder();
        NodeList list = element.getChildNodes();
        boolean hasChildren = false;
        for (int i = 0; i < list.getLength(); ++i) {
            Node w3cNode = list.item(i);
            if (w3cNode instanceof Element) {
                Element child = (Element)w3cNode;
                ImmutableNode.Builder childNode = new ImmutableNode.Builder();
                childNode.name(child.getTagName());
                MutableObject refChildValue = new MutableObject();
                Map<String, String> attrmap = this.constructHierarchy(childNode, (MutableObject<String>)refChildValue, child, elemRefs, trimFlag, level + 1);
                boolean childTrim = Boolean.parseBoolean(attrmap.remove(ATTR_SPACE_INTERNAL));
                childNode.addAttributes(attrmap);
                ImmutableNode newChild = this.createChildNodeWithValue(node, childNode, child, (String)refChildValue.getValue(), childTrim, attrmap, elemRefs);
                if (elemRefs != null && !elemRefs.containsKey(newChild)) {
                    elemRefs.put(newChild, child);
                }
                hasChildren = true;
                continue;
            }
            if (!(w3cNode instanceof Text)) continue;
            Text data = (Text)w3cNode;
            buffer.append(data.getData());
        }
        boolean childrenFlag = false;
        if (hasChildren || trimFlag) {
            boolean bl = childrenFlag = hasChildren || attributes.size() > 1;
        }
        if (!(text = XMLConfiguration.determineValue(buffer.toString(), childrenFlag, trimFlag)).isEmpty() || !childrenFlag && level != 0) {
            refValue.setValue((Object)text);
        }
        return attributes;
    }

    private static String determineValue(String content, boolean hasChildren, boolean trimFlag) {
        boolean shouldTrim = trimFlag || StringUtils.isBlank((CharSequence)content) && hasChildren;
        return shouldTrim ? content.trim() : content;
    }

    private static Map<String, String> processAttributes(Element element) {
        NamedNodeMap attributes = element.getAttributes();
        HashMap<String, String> attrmap = new HashMap<String, String>();
        for (int i = 0; i < attributes.getLength(); ++i) {
            Node w3cNode = attributes.item(i);
            if (!(w3cNode instanceof Attr)) continue;
            Attr attr = (Attr)w3cNode;
            attrmap.put(attr.getName(), attr.getValue());
        }
        return attrmap;
    }

    private ImmutableNode createChildNodeWithValue(ImmutableNode.Builder parent, ImmutableNode.Builder child, Element elem, String value, boolean trim, Map<String, String> attrmap, Map<ImmutableNode, Object> elemRefs) {
        ImmutableNode addedChildNode;
        Collection<Object> values = value != null ? this.getListDelimiterHandler().split(value, trim) : Collections.emptyList();
        if (values.size() > 1) {
            Map<ImmutableNode, Object> refs = XMLConfiguration.isSingleElementList(elem) ? elemRefs : null;
            Iterator<Object> it = values.iterator();
            child.value(it.next());
            addedChildNode = child.create();
            parent.addChild(addedChildNode);
            XMLListReference.assignListReference(refs, addedChildNode, elem);
            while (it.hasNext()) {
                ImmutableNode.Builder c = new ImmutableNode.Builder();
                c.name(addedChildNode.getNodeName());
                c.value(it.next());
                c.addAttributes(attrmap);
                ImmutableNode newChild = c.create();
                parent.addChild(newChild);
                XMLListReference.assignListReference(refs, newChild, null);
            }
        } else {
            if (values.size() == 1) {
                child.value(values.iterator().next());
            }
            addedChildNode = child.create();
            parent.addChild(addedChildNode);
        }
        return addedChildNode;
    }

    private static boolean isSingleElementList(Element element) {
        Node parentNode = element.getParentNode();
        return XMLConfiguration.countChildElements(parentNode, element.getTagName()) == 1;
    }

    private static int countChildElements(Node parent, String name) {
        NodeList childNodes = parent.getChildNodes();
        int count = 0;
        for (int i = 0; i < childNodes.getLength(); ++i) {
            Node item = childNodes.item(i);
            if (!(item instanceof Element) || !name.equals(((Element)item).getTagName())) continue;
            ++count;
        }
        return count;
    }

    private static boolean shouldTrim(Element element, boolean currentTrim) {
        Attr attr = element.getAttributeNode(ATTR_SPACE);
        if (attr == null) {
            return currentTrim;
        }
        return !VALUE_PRESERVE.equals(attr.getValue());
    }

    protected DocumentBuilder createDocumentBuilder() throws ParserConfigurationException {
        if (this.getDocumentBuilder() != null) {
            return this.getDocumentBuilder();
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        if (this.isValidating()) {
            factory.setValidating(true);
            if (this.isSchemaValidation()) {
                factory.setNamespaceAware(true);
                factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
            }
        }
        DocumentBuilder result = factory.newDocumentBuilder();
        result.setEntityResolver(this.entityResolver);
        if (this.isValidating()) {
            result.setErrorHandler(new DefaultHandler(){

                @Override
                public void error(SAXParseException ex) throws SAXException {
                    throw ex;
                }
            });
        }
        return result;
    }

    protected Transformer createTransformer() throws ConfigurationException {
        Transformer transformer = XMLDocumentHelper.createTransformer();
        transformer.setOutputProperty("indent", "yes");
        transformer.setOutputProperty(INDENT_AMOUNT_PROPERTY, Integer.toString(2));
        if (this.locator != null && this.locator.getEncoding() != null) {
            transformer.setOutputProperty("encoding", this.locator.getEncoding());
        }
        if (this.publicID != null) {
            transformer.setOutputProperty("doctype-public", this.publicID);
        }
        if (this.systemID != null) {
            transformer.setOutputProperty("doctype-system", this.systemID);
        }
        return transformer;
    }

    private Document createDocument() throws ConfigurationException {
        ReferenceNodeHandler handler = this.getReferenceHandler();
        XMLDocumentHelper docHelper = (XMLDocumentHelper)handler.getReference((ImmutableNode)handler.getRootNode());
        XMLDocumentHelper newHelper = docHelper == null ? XMLDocumentHelper.forNewDocument(this.getRootElementName()) : docHelper.createCopy();
        XMLBuilderVisitor builder = new XMLBuilderVisitor(newHelper, this.getListDelimiterHandler());
        builder.handleRemovedNodes(handler);
        builder.processDocument(handler);
        this.initRootElementText(newHelper.getDocument(), ((ImmutableNode)this.getModel().getNodeHandler().getRootNode()).getValue());
        return newHelper.getDocument();
    }

    private void initRootElementText(Document doc, Object value) {
        Element elem = doc.getDocumentElement();
        NodeList children = elem.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node nd = children.item(i);
            if (nd.getNodeType() != 3) continue;
            elem.removeChild(nd);
        }
        if (value != null) {
            elem.appendChild(doc.createTextNode(String.valueOf(value)));
        }
    }

    @Override
    public void initFileLocator(FileLocator loc) {
        this.locator = loc;
    }

    @Override
    public void read(Reader in) throws ConfigurationException, IOException {
        this.load(new InputSource(in));
    }

    @Override
    public void read(InputStream in) throws ConfigurationException, IOException {
        this.load(new InputSource(in));
    }

    private void load(InputSource source) throws ConfigurationException {
        if (this.locator == null) {
            throw new ConfigurationException("Load operation not properly initialized! Do not call read(InputStream) directly, but use a FileHandler to load a configuration.");
        }
        try {
            URL sourceURL = this.locator.getSourceURL();
            if (sourceURL != null) {
                source.setSystemId(sourceURL.toString());
            }
            DocumentBuilder builder = this.createDocumentBuilder();
            Document newDocument = builder.parse(source);
            Document oldDocument = this.getDocument();
            this.initProperties(XMLDocumentHelper.forSourceDocument(newDocument), oldDocument == null);
        }
        catch (SAXParseException spe) {
            throw new ConfigurationException("Error parsing " + source.getSystemId(), spe);
        }
        catch (Exception e) {
            this.getLogger().debug("Unable to load the configuration: " + e);
            throw new ConfigurationException("Unable to load the configuration", e);
        }
    }

    @Override
    public void write(Writer writer) throws ConfigurationException, IOException {
        this.write(writer, this.createTransformer());
    }

    public void write(Writer writer, Transformer transformer) throws ConfigurationException {
        DOMSource source = new DOMSource(this.createDocument());
        StreamResult result = new StreamResult(writer);
        XMLDocumentHelper.transform(transformer, source, result);
    }

    public void validate() throws ConfigurationException {
        this.beginWrite(false);
        try {
            Transformer transformer = this.createTransformer();
            DOMSource source = new DOMSource(this.createDocument());
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            XMLDocumentHelper.transform(transformer, source, result);
            StringReader reader = new StringReader(writer.getBuffer().toString());
            DocumentBuilder builder = this.createDocumentBuilder();
            builder.parse(new InputSource(reader));
        }
        catch (IOException | ParserConfigurationException | SAXException pce) {
            throw new ConfigurationException("Validation failed", pce);
        }
        finally {
            this.endWrite();
        }
    }

    static class XMLBuilderVisitor
    extends BaseHierarchicalConfiguration.BuilderVisitor {
        private final Document document;
        private final Map<Node, Node> elementMapping;
        private final Map<ImmutableNode, Element> newElements;
        private final ListDelimiterHandler listDelimiterHandler;

        public XMLBuilderVisitor(XMLDocumentHelper docHelper, ListDelimiterHandler handler) {
            this.document = docHelper.getDocument();
            this.elementMapping = docHelper.getElementMapping();
            this.listDelimiterHandler = handler;
            this.newElements = new HashMap<ImmutableNode, Element>();
        }

        public void processDocument(ReferenceNodeHandler refHandler) {
            XMLBuilderVisitor.updateAttributes((ImmutableNode)refHandler.getRootNode(), this.document.getDocumentElement());
            NodeTreeWalker.INSTANCE.walkDFS((ImmutableNode)refHandler.getRootNode(), this, refHandler);
        }

        public void handleRemovedNodes(ReferenceNodeHandler refHandler) {
            refHandler.removedReferences().stream().filter(Node.class::isInstance).forEach(ref -> this.removeReference(this.elementMapping.get(ref)));
        }

        @Override
        protected void insert(ImmutableNode newNode, ImmutableNode parent, ImmutableNode sibling1, ImmutableNode sibling2, ReferenceNodeHandler refHandler) {
            if (XMLListReference.isListNode(newNode, refHandler)) {
                return;
            }
            Element elem = this.document.createElement(newNode.getNodeName());
            this.newElements.put(newNode, elem);
            XMLBuilderVisitor.updateAttributes(newNode, elem);
            if (newNode.getValue() != null) {
                String txt = String.valueOf(this.listDelimiterHandler.escape(newNode.getValue(), ListDelimiterHandler.NOOP_TRANSFORMER));
                elem.appendChild(this.document.createTextNode(txt));
            }
            if (sibling2 == null) {
                this.getElement(parent, refHandler).appendChild(elem);
            } else if (sibling1 != null) {
                this.getElement(parent, refHandler).insertBefore(elem, this.getElement(sibling1, refHandler).getNextSibling());
            } else {
                this.getElement(parent, refHandler).insertBefore(elem, this.getElement(parent, refHandler).getFirstChild());
            }
        }

        @Override
        protected void update(ImmutableNode node, Object reference, ReferenceNodeHandler refHandler) {
            if (XMLListReference.isListNode(node, refHandler)) {
                if (XMLListReference.isFirstListItem(node, refHandler)) {
                    String value = XMLListReference.listValue(node, refHandler, this.listDelimiterHandler);
                    this.updateElement(node, refHandler, value);
                }
            } else {
                Object value = this.listDelimiterHandler.escape(refHandler.getValue(node), ListDelimiterHandler.NOOP_TRANSFORMER);
                this.updateElement(node, refHandler, value);
            }
        }

        private void updateElement(ImmutableNode node, ReferenceNodeHandler refHandler, Object value) {
            Element element = this.getElement(node, refHandler);
            this.updateElement(element, value);
            XMLBuilderVisitor.updateAttributes(node, element);
        }

        private void updateElement(Element element, Object value) {
            Text txtNode = XMLBuilderVisitor.findTextNodeForUpdate(element);
            if (value == null) {
                if (txtNode != null) {
                    element.removeChild(txtNode);
                }
            } else {
                String newValue = String.valueOf(value);
                if (txtNode == null) {
                    txtNode = this.document.createTextNode(newValue);
                    if (element.getFirstChild() != null) {
                        element.insertBefore(txtNode, element.getFirstChild());
                    } else {
                        element.appendChild(txtNode);
                    }
                } else {
                    txtNode.setNodeValue(newValue);
                }
            }
        }

        private void removeReference(Node element) {
            Node parentElem = element.getParentNode();
            if (parentElem != null) {
                parentElem.removeChild(element);
            }
        }

        private Element getElement(ImmutableNode node, ReferenceNodeHandler refHandler) {
            Element elementNew = this.newElements.get(node);
            if (elementNew != null) {
                return elementNew;
            }
            Object reference = refHandler.getReference(node);
            Node element = reference instanceof XMLDocumentHelper ? ((XMLDocumentHelper)reference).getDocument().getDocumentElement() : (reference instanceof XMLListReference ? ((XMLListReference)reference).getElement() : (Node)reference);
            return element != null ? (Element)this.elementMapping.get(element) : this.document.getDocumentElement();
        }

        private static void updateAttributes(ImmutableNode node, Element elem) {
            if (node != null && elem != null) {
                XMLBuilderVisitor.clearAttributes(elem);
                node.getAttributes().forEach((k, v) -> {
                    if (v != null) {
                        elem.setAttribute((String)k, v.toString());
                    }
                });
            }
        }

        private static void clearAttributes(Element elem) {
            NamedNodeMap attributes = elem.getAttributes();
            for (int i = 0; i < attributes.getLength(); ++i) {
                elem.removeAttribute(attributes.item(i).getNodeName());
            }
        }

        private static Text findTextNodeForUpdate(Element elem) {
            Text result = null;
            NodeList children = elem.getChildNodes();
            ArrayList<Node> textNodes = new ArrayList<Node>();
            for (int i = 0; i < children.getLength(); ++i) {
                Node nd = children.item(i);
                if (!(nd instanceof Text)) continue;
                if (result == null) {
                    result = (Text)nd;
                    continue;
                }
                textNodes.add(nd);
            }
            if (result instanceof CDATASection) {
                textNodes.add(result);
                result = null;
            }
            textNodes.forEach(elem::removeChild);
            return result;
        }
    }
}

