/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.XMPSchema;
import org.apache.xmpbox.schema.XmpSchemaException;
import org.apache.xmpbox.type.AbstractField;
import org.apache.xmpbox.type.AbstractSimpleProperty;
import org.apache.xmpbox.type.AbstractStructuredType;
import org.apache.xmpbox.type.ArrayProperty;
import org.apache.xmpbox.type.Attribute;
import org.apache.xmpbox.type.BadFieldValueException;
import org.apache.xmpbox.type.Cardinality;
import org.apache.xmpbox.type.ComplexPropertyContainer;
import org.apache.xmpbox.type.PropertiesDescription;
import org.apache.xmpbox.type.PropertyType;
import org.apache.xmpbox.type.TypeMapping;
import org.apache.xmpbox.type.Types;
import org.apache.xmpbox.xml.DomHelper;
import org.apache.xmpbox.xml.PdfaExtensionHelper;
import org.apache.xmpbox.xml.XmpParsingException;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class DomXmpParser {
    private DocumentBuilder dBuilder;
    private NamespaceFinder nsFinder;
    private boolean strictParsing = true;

    public DomXmpParser() throws XmpParsingException {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            dbFactory.setXIncludeAware(false);
            dbFactory.setExpandEntityReferences(false);
            dbFactory.setIgnoringComments(true);
            dbFactory.setNamespaceAware(true);
            this.dBuilder = dbFactory.newDocumentBuilder();
            this.nsFinder = new NamespaceFinder();
        }
        catch (ParserConfigurationException e) {
            throw new XmpParsingException(XmpParsingException.ErrorType.Configuration, "Failed to initialize", e);
        }
    }

    public boolean isStrictParsing() {
        return this.strictParsing;
    }

    public void setStrictParsing(boolean strictParsing) {
        this.strictParsing = strictParsing;
    }

    public XMPMetadata parse(byte[] xmp) throws XmpParsingException {
        ByteArrayInputStream input = new ByteArrayInputStream(xmp);
        return this.parse(input);
    }

    public XMPMetadata parse(InputStream input) throws XmpParsingException {
        Document document = null;
        try {
            this.dBuilder.setErrorHandler(null);
            document = this.dBuilder.parse(input);
        }
        catch (SAXException e) {
            throw new XmpParsingException(XmpParsingException.ErrorType.Undefined, "Failed to parse", e);
        }
        catch (IOException e) {
            throw new XmpParsingException(XmpParsingException.ErrorType.Undefined, "Failed to parse", e);
        }
        XMPMetadata xmp = null;
        this.removeComments(document);
        Node node = document.getFirstChild();
        if (!(node instanceof ProcessingInstruction)) {
            throw new XmpParsingException(XmpParsingException.ErrorType.XpacketBadStart, "xmp should start with a processing instruction");
        }
        xmp = this.parseInitialXpacket((ProcessingInstruction)node);
        node = node.getNextSibling();
        while (node instanceof ProcessingInstruction) {
            node = node.getNextSibling();
        }
        Element root = null;
        if (!(node instanceof Element)) {
            throw new XmpParsingException(XmpParsingException.ErrorType.NoRootElement, "xmp should contain a root element");
        }
        root = (Element)node;
        if (!((node = node.getNextSibling()) instanceof ProcessingInstruction)) {
            throw new XmpParsingException(XmpParsingException.ErrorType.XpacketBadEnd, "xmp should end with a processing instruction");
        }
        this.parseEndPacket(xmp, (ProcessingInstruction)node);
        node = node.getNextSibling();
        if (node != null) {
            throw new XmpParsingException(XmpParsingException.ErrorType.XpacketBadEnd, "xmp should end after xpacket end processing instruction");
        }
        Element rdfRdf = this.findDescriptionsParent(root);
        List<Element> descriptions = DomHelper.getElementChildren(rdfRdf);
        ArrayList<Element> dataDescriptions = new ArrayList<Element>(descriptions.size());
        for (Element description : descriptions) {
            Element first = DomHelper.getFirstChildElement(description);
            if (first != null && "pdfaExtension".equals(first.getPrefix())) {
                PdfaExtensionHelper.validateNaming(xmp, description);
                this.parseDescriptionRoot(xmp, description);
                continue;
            }
            dataDescriptions.add(description);
        }
        PdfaExtensionHelper.populateSchemaMapping(xmp);
        for (Element description : dataDescriptions) {
            this.parseDescriptionRoot(xmp, description);
        }
        return xmp;
    }

    private void parseDescriptionRoot(XMPMetadata xmp, Element description) throws XmpParsingException {
        this.nsFinder.push(description);
        TypeMapping tm = xmp.getTypeMapping();
        try {
            List<Element> properties = DomHelper.getElementChildren(description);
            NamedNodeMap nnm = description.getAttributes();
            for (int i = 0; i < nnm.getLength(); ++i) {
                Attr attr = (Attr)nnm.item(i);
                if ("xmlns".equals(attr.getPrefix()) || "rdf".equals(attr.getPrefix()) && "about".equals(attr.getLocalName()) || attr.getPrefix() == null && "about".equals(attr.getLocalName())) continue;
                this.parseDescriptionRootAttr(xmp, description, attr, tm);
            }
            this.parseChildrenAsProperties(xmp, properties, tm, description);
        }
        catch (XmpSchemaException e) {
            throw new XmpParsingException(XmpParsingException.ErrorType.Undefined, "Parsing failed", e);
        }
        finally {
            this.nsFinder.pop();
        }
    }

    private void parseDescriptionRootAttr(XMPMetadata xmp, Element description, Attr attr, TypeMapping tm) throws XmpSchemaException, XmpParsingException {
        String namespace = attr.getNamespaceURI();
        XMPSchema schema = xmp.getSchema(namespace);
        if (schema == null && tm.getSchemaFactory(namespace) != null) {
            schema = tm.getSchemaFactory(namespace).createXMPSchema(xmp, attr.getPrefix());
            this.loadAttributes(schema, description);
        }
        if (schema != null) {
            ComplexPropertyContainer container = schema.getContainer();
            PropertyType type = this.checkPropertyDefinition(xmp, new QName(attr.getNamespaceURI(), attr.getLocalName()));
            if (type == null) {
                type = TypeMapping.createPropertyType(Types.Text, Cardinality.Simple);
            }
            try {
                AbstractSimpleProperty sp = tm.instanciateSimpleProperty(namespace, schema.getPrefix(), attr.getLocalName(), attr.getValue(), type.type());
                container.addProperty(sp);
            }
            catch (IllegalArgumentException e) {
                throw new XmpParsingException(XmpParsingException.ErrorType.Format, e.getMessage() + " in " + schema.getPrefix() + ":" + attr.getLocalName(), e);
            }
        }
    }

    private void parseChildrenAsProperties(XMPMetadata xmp, List<Element> properties, TypeMapping tm, Element description) throws XmpParsingException, XmpSchemaException {
        for (Element property : properties) {
            String namespace = property.getNamespaceURI();
            PropertyType type = this.checkPropertyDefinition(xmp, DomHelper.getQName(property));
            if (!tm.isDefinedSchema(namespace)) {
                throw new XmpParsingException(XmpParsingException.ErrorType.NoSchema, "This namespace is not a schema or a structured type : " + namespace);
            }
            XMPSchema schema = xmp.getSchema(namespace);
            if (schema == null) {
                schema = tm.getSchemaFactory(namespace).createXMPSchema(xmp, property.getPrefix());
                this.loadAttributes(schema, description);
            }
            ComplexPropertyContainer container = schema.getContainer();
            this.createProperty(xmp, property, type, container);
        }
    }

    private void createProperty(XMPMetadata xmp, Element property, PropertyType type, ComplexPropertyContainer container) throws XmpParsingException {
        String prefix = property.getPrefix();
        String name = property.getLocalName();
        String namespace = property.getNamespaceURI();
        this.nsFinder.push(property);
        try {
            if (type == null) {
                if (this.strictParsing) {
                    throw new XmpParsingException(XmpParsingException.ErrorType.InvalidType, "No type defined for {" + namespace + "}" + name);
                }
                this.manageSimpleType(xmp, property, Types.Text, container);
            } else if (type.type() == Types.LangAlt) {
                this.manageLangAlt(xmp, property, container);
            } else if (type.card().isArray()) {
                this.manageArray(xmp, property, type, container);
            } else if (type.type().isSimple()) {
                this.manageSimpleType(xmp, property, type.type(), container);
            } else if (type.type().isStructured()) {
                this.manageStructuredType(xmp, property, prefix, container);
            } else if (type.type() == Types.DefinedType) {
                this.manageDefinedType(xmp, property, prefix, container);
            }
        }
        catch (IllegalArgumentException e) {
            throw new XmpParsingException(XmpParsingException.ErrorType.Format, e.getMessage() + " in " + prefix + ":" + name, e);
        }
        finally {
            this.nsFinder.pop();
        }
    }

    private void manageDefinedType(XMPMetadata xmp, Element property, String prefix, ComplexPropertyContainer container) throws XmpParsingException {
        if (DomHelper.isParseTypeResource(property)) {
            AbstractStructuredType ast = this.parseLiDescription(xmp, DomHelper.getQName(property), property);
            if (ast == null) {
                throw new XmpParsingException(XmpParsingException.ErrorType.Format, "property should contain child elements : " + property);
            }
            ast.setPrefix(prefix);
            container.addProperty(ast);
        } else {
            Element inner = DomHelper.getFirstChildElement(property);
            if (inner == null) {
                throw new XmpParsingException(XmpParsingException.ErrorType.Format, "property should contain child element : " + property);
            }
            AbstractStructuredType ast = this.parseLiDescription(xmp, DomHelper.getQName(property), inner);
            if (ast == null) {
                throw new XmpParsingException(XmpParsingException.ErrorType.Format, "inner element should contain child elements : " + inner);
            }
            ast.setPrefix(prefix);
            container.addProperty(ast);
        }
    }

    private void manageStructuredType(XMPMetadata xmp, Element property, String prefix, ComplexPropertyContainer container) throws XmpParsingException {
        if (DomHelper.isParseTypeResource(property)) {
            AbstractStructuredType ast = this.parseLiDescription(xmp, DomHelper.getQName(property), property);
            if (ast != null) {
                ast.setPrefix(prefix);
                container.addProperty(ast);
            }
        } else {
            Element inner = DomHelper.getFirstChildElement(property);
            if (inner != null) {
                this.nsFinder.push(inner);
                AbstractStructuredType ast = this.parseLiDescription(xmp, DomHelper.getQName(property), inner);
                if (ast == null) {
                    throw new XmpParsingException(XmpParsingException.ErrorType.Format, "inner element should contain child elements : " + inner);
                }
                ast.setPrefix(prefix);
                container.addProperty(ast);
            }
        }
    }

    private void manageSimpleType(XMPMetadata xmp, Element property, Types type, ComplexPropertyContainer container) {
        TypeMapping tm = xmp.getTypeMapping();
        String prefix = property.getPrefix();
        String name = property.getLocalName();
        String namespace = property.getNamespaceURI();
        AbstractSimpleProperty sp = tm.instanciateSimpleProperty(namespace, prefix, name, property.getTextContent(), type);
        this.loadAttributes(sp, property);
        container.addProperty(sp);
    }

    private void manageArray(XMPMetadata xmp, Element property, PropertyType type, ComplexPropertyContainer container) throws XmpParsingException {
        TypeMapping tm = xmp.getTypeMapping();
        String prefix = property.getPrefix();
        String name = property.getLocalName();
        String namespace = property.getNamespaceURI();
        Element bagOrSeq = DomHelper.getUniqueElementChild(property);
        if (bagOrSeq == null) {
            String whatFound = "nothing";
            if (property.getFirstChild() != null) {
                whatFound = property.getFirstChild().getClass().getName();
            }
            throw new XmpParsingException(XmpParsingException.ErrorType.Format, "Invalid array definition, expecting " + (Object)((Object)type.card()) + " and found " + whatFound + " [prefix=" + prefix + "; name=" + name + "]");
        }
        if (!bagOrSeq.getLocalName().equals(type.card().name())) {
            throw new XmpParsingException(XmpParsingException.ErrorType.Format, "Invalid array type, expecting " + (Object)((Object)type.card()) + " and found " + bagOrSeq.getLocalName() + " [prefix=" + prefix + "; name=" + name + "]");
        }
        ArrayProperty array = tm.createArrayProperty(namespace, prefix, name, type.card());
        container.addProperty(array);
        List<Element> lis = DomHelper.getElementChildren(bagOrSeq);
        for (Element element : lis) {
            QName propertyQName = new QName(element.getLocalName());
            AbstractField ast = this.parseLiElement(xmp, propertyQName, element, type.type());
            if (ast == null) continue;
            array.addProperty(ast);
        }
    }

    private void manageLangAlt(XMPMetadata xmp, Element property, ComplexPropertyContainer container) throws XmpParsingException {
        this.manageArray(xmp, property, TypeMapping.createPropertyType(Types.LangAlt, Cardinality.Alt), container);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void parseDescriptionInner(XMPMetadata xmp, Element description, ComplexPropertyContainer parentContainer) throws XmpParsingException {
        this.nsFinder.push(description);
        TypeMapping tm = xmp.getTypeMapping();
        try {
            List<Element> properties = DomHelper.getElementChildren(description);
            for (Element property : properties) {
                String name = property.getLocalName();
                PropertyType dtype = this.checkPropertyDefinition(xmp, DomHelper.getQName(property));
                PropertyType ptype = tm.getStructuredPropMapping(dtype.type()).getPropertyType(name);
                this.createProperty(xmp, property, ptype, parentContainer);
            }
        }
        finally {
            this.nsFinder.pop();
        }
    }

    private AbstractField parseLiElement(XMPMetadata xmp, QName descriptor, Element liElement, Types type) throws XmpParsingException {
        AbstractStructuredType af;
        if (DomHelper.isParseTypeResource(liElement)) {
            return this.parseLiDescription(xmp, descriptor, liElement);
        }
        Element liChild = DomHelper.getUniqueElementChild(liElement);
        if (liChild != null) {
            this.nsFinder.push(liChild);
            return this.parseLiDescription(xmp, descriptor, liChild);
        }
        String text = liElement.getTextContent();
        TypeMapping tm = xmp.getTypeMapping();
        if (type.isSimple()) {
            AbstractSimpleProperty af2 = tm.instanciateSimpleProperty(descriptor.getNamespaceURI(), descriptor.getPrefix(), descriptor.getLocalPart(), text, type);
            this.loadAttributes(af2, liElement);
            return af2;
        }
        try {
            af = tm.instanciateStructuredType(type, descriptor.getLocalPart());
        }
        catch (BadFieldValueException ex) {
            throw new XmpParsingException(XmpParsingException.ErrorType.InvalidType, "Parsing of structured type failed", ex);
        }
        this.loadAttributes(af, liElement);
        return af;
    }

    private void loadAttributes(AbstractField sp, Element element) {
        NamedNodeMap nnm = element.getAttributes();
        for (int i = 0; i < nnm.getLength(); ++i) {
            Attr attr = (Attr)nnm.item(i);
            if ("xmlns".equals(attr.getPrefix())) continue;
            if ("rdf".equals(attr.getPrefix()) && "about".equals(attr.getLocalName())) {
                if (!(sp instanceof XMPSchema)) continue;
                ((XMPSchema)sp).setAboutAsSimple(attr.getValue());
                continue;
            }
            Attribute attribute = new Attribute("http://www.w3.org/XML/1998/namespace", attr.getLocalName(), attr.getValue());
            sp.setAttribute(attribute);
        }
    }

    private AbstractStructuredType parseLiDescription(XMPMetadata xmp, QName descriptor, Element liElement) throws XmpParsingException {
        TypeMapping tm = xmp.getTypeMapping();
        List<Element> elements = DomHelper.getElementChildren(liElement);
        if (elements.isEmpty()) {
            return null;
        }
        Element first = elements.get(0);
        PropertyType ctype = this.checkPropertyDefinition(xmp, DomHelper.getQName(first));
        if (ctype == null) {
            throw new XmpParsingException(XmpParsingException.ErrorType.NoType, "ctype is null, first: " + first + ", DomHelper.getQName(first): " + DomHelper.getQName(first));
        }
        Types tt = ctype.type();
        AbstractStructuredType ast = this.instanciateStructured(tm, tt, descriptor.getLocalPart(), first.getNamespaceURI());
        ast.setNamespace(descriptor.getNamespaceURI());
        ast.setPrefix(descriptor.getPrefix());
        PropertiesDescription pm = tt.isStructured() ? tm.getStructuredPropMapping(tt) : tm.getDefinedDescriptionByNamespace(first.getNamespaceURI());
        for (Element element : elements) {
            String prefix = element.getPrefix();
            String name = element.getLocalName();
            String namespace = element.getNamespaceURI();
            PropertyType type = pm.getPropertyType(name);
            if (type == null) {
                throw new XmpParsingException(XmpParsingException.ErrorType.NoType, "Type '" + name + "' not defined in " + element.getNamespaceURI());
            }
            if (type.card().isArray()) {
                ArrayProperty array = tm.createArrayProperty(namespace, prefix, name, type.card());
                ast.getContainer().addProperty(array);
                Element bagOrSeq = DomHelper.getUniqueElementChild(element);
                List<Element> lis = DomHelper.getElementChildren(bagOrSeq);
                for (Element element2 : lis) {
                    AbstractField ast2 = this.parseLiElement(xmp, descriptor, element2, type.type());
                    if (ast2 == null) continue;
                    array.addProperty(ast2);
                }
                continue;
            }
            if (type.type().isSimple()) {
                AbstractSimpleProperty sp = tm.instanciateSimpleProperty(namespace, prefix, name, element.getTextContent(), type.type());
                this.loadAttributes(sp, element);
                ast.getContainer().addProperty(sp);
                continue;
            }
            if (type.type().isStructured()) {
                AbstractStructuredType inner = this.instanciateStructured(tm, type.type(), name, null);
                inner.setNamespace(namespace);
                inner.setPrefix(prefix);
                ast.getContainer().addProperty(inner);
                ComplexPropertyContainer cpc = inner.getContainer();
                if (DomHelper.isParseTypeResource(element)) {
                    this.parseDescriptionInner(xmp, element, cpc);
                    continue;
                }
                Element descElement = DomHelper.getFirstChildElement(element);
                if (descElement == null) continue;
                this.parseDescriptionInner(xmp, descElement, cpc);
                continue;
            }
            throw new XmpParsingException(XmpParsingException.ErrorType.NoType, "Unidentified element to parse " + element + " (type=" + type + ")");
        }
        return ast;
    }

    private XMPMetadata parseInitialXpacket(ProcessingInstruction pi) throws XmpParsingException {
        if (!"xpacket".equals(pi.getNodeName())) {
            throw new XmpParsingException(XmpParsingException.ErrorType.XpacketBadStart, "Bad processing instruction name : " + pi.getNodeName());
        }
        String data = pi.getData();
        StringTokenizer tokens = new StringTokenizer(data, " ");
        String id = null;
        String begin = null;
        String bytes = null;
        String encoding = null;
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            if (!token.endsWith("\"") && !token.endsWith("'")) {
                throw new XmpParsingException(XmpParsingException.ErrorType.XpacketBadStart, "Cannot understand PI data part : '" + token + "' in '" + data + "'");
            }
            String quote = token.substring(token.length() - 1);
            int pos = token.indexOf("=" + quote);
            if (pos <= 0) {
                throw new XmpParsingException(XmpParsingException.ErrorType.XpacketBadStart, "Cannot understand PI data part : '" + token + "' in '" + data + "'");
            }
            String name = token.substring(0, pos);
            if (token.length() - 1 < pos + 2) {
                throw new XmpParsingException(XmpParsingException.ErrorType.XpacketBadStart, "Cannot understand PI data part : '" + token + "' in '" + data + "'");
            }
            String value = token.substring(pos + 2, token.length() - 1);
            if ("id".equals(name)) {
                id = value;
                continue;
            }
            if ("begin".equals(name)) {
                begin = value;
                continue;
            }
            if ("bytes".equals(name)) {
                bytes = value;
                continue;
            }
            if ("encoding".equals(name)) {
                encoding = value;
                continue;
            }
            throw new XmpParsingException(XmpParsingException.ErrorType.XpacketBadStart, "Unknown attribute in xpacket PI : '" + token + "'");
        }
        return XMPMetadata.createXMPMetadata(begin, id, bytes, encoding);
    }

    private void parseEndPacket(XMPMetadata metadata, ProcessingInstruction pi) throws XmpParsingException {
        char end;
        String xpackData = pi.getData();
        if (xpackData.startsWith("end=")) {
            end = xpackData.charAt(5);
            if (end != 'r' && end != 'w') {
                throw new XmpParsingException(XmpParsingException.ErrorType.XpacketBadEnd, "Excepted xpacket 'end' attribute with value 'r' or 'w' ");
            }
        } else {
            throw new XmpParsingException(XmpParsingException.ErrorType.XpacketBadEnd, "Excepted xpacket 'end' attribute (must be present and placed in first)");
        }
        metadata.setEndXPacket(Character.toString(end));
    }

    private Element findDescriptionsParent(Element root) throws XmpParsingException {
        Element rdfRdf;
        if (!"http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(root.getNamespaceURI())) {
            this.expectNaming(root, "adobe:ns:meta/", "x", "xmpmeta");
            NodeList nl = root.getChildNodes();
            if (nl.getLength() == 0) {
                throw new XmpParsingException(XmpParsingException.ErrorType.Format, "No rdf description found in xmp");
            }
            if (nl.getLength() > 1) {
                throw new XmpParsingException(XmpParsingException.ErrorType.Format, "More than one element found in x:xmpmeta");
            }
            if (!(root.getFirstChild() instanceof Element)) {
                throw new XmpParsingException(XmpParsingException.ErrorType.Format, "x:xmpmeta does not contains rdf:RDF element");
            }
            rdfRdf = (Element)root.getFirstChild();
        } else {
            rdfRdf = root;
        }
        this.expectNaming(rdfRdf, "http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf", "RDF");
        return rdfRdf;
    }

    private void expectNaming(Element element, String ns, String prefix, String ln) throws XmpParsingException {
        if (ns != null && !ns.equals(element.getNamespaceURI())) {
            throw new XmpParsingException(XmpParsingException.ErrorType.Format, "Expecting namespace '" + ns + "' and found '" + element.getNamespaceURI() + "'");
        }
        if (prefix != null && !prefix.equals(element.getPrefix())) {
            throw new XmpParsingException(XmpParsingException.ErrorType.Format, "Expecting prefix '" + prefix + "' and found '" + element.getPrefix() + "'");
        }
        if (ln != null && !ln.equals(element.getLocalName())) {
            throw new XmpParsingException(XmpParsingException.ErrorType.Format, "Expecting local name '" + ln + "' and found '" + element.getLocalName() + "'");
        }
    }

    private void removeComments(Node root) {
        ArrayList<Node> forDeletion = new ArrayList<Node>();
        NodeList nl = root.getChildNodes();
        if (nl.getLength() <= 1) {
            return;
        }
        for (int i = 0; i < nl.getLength(); ++i) {
            Node node = nl.item(i);
            if (node instanceof Comment) {
                forDeletion.add(node);
                continue;
            }
            if (node instanceof Text) {
                if (!node.getTextContent().trim().isEmpty()) continue;
                forDeletion.add(node);
                continue;
            }
            if (!(node instanceof Element)) continue;
            this.removeComments(node);
        }
        for (Node node : forDeletion) {
            root.removeChild(node);
        }
    }

    private AbstractStructuredType instanciateStructured(TypeMapping tm, Types type, String name, String structuredNamespace) throws XmpParsingException {
        try {
            if (type.isStructured()) {
                return tm.instanciateStructuredType(type, name);
            }
            if (type.isDefined()) {
                return tm.instanciateDefinedType(name, structuredNamespace);
            }
            throw new XmpParsingException(XmpParsingException.ErrorType.InvalidType, "Type not structured : " + (Object)((Object)type));
        }
        catch (BadFieldValueException e) {
            throw new XmpParsingException(XmpParsingException.ErrorType.InvalidType, "Parsing failed", e);
        }
    }

    private PropertyType checkPropertyDefinition(XMPMetadata xmp, QName prop) throws XmpParsingException {
        TypeMapping tm = xmp.getTypeMapping();
        if (!this.nsFinder.containsNamespace(prop.getNamespaceURI())) {
            throw new XmpParsingException(XmpParsingException.ErrorType.NoSchema, "Schema is not set in this document : " + prop.getNamespaceURI());
        }
        String nsuri = prop.getNamespaceURI();
        if (!tm.isDefinedNamespace(nsuri)) {
            throw new XmpParsingException(XmpParsingException.ErrorType.NoSchema, "Cannot find a definition for the namespace " + prop.getNamespaceURI());
        }
        try {
            return tm.getSpecifiedPropertyType(prop);
        }
        catch (BadFieldValueException e) {
            throw new XmpParsingException(XmpParsingException.ErrorType.InvalidType, "Failed to retrieve property definition", e);
        }
    }

    protected static class NamespaceFinder {
        private final Deque<Map<String, String>> stack = new ArrayDeque<Map<String, String>>();

        protected NamespaceFinder() {
        }

        protected void push(Element description) {
            NamedNodeMap nnm = description.getAttributes();
            HashMap<String, String> map = new HashMap<String, String>(nnm.getLength());
            for (int j = 0; j < nnm.getLength(); ++j) {
                Attr no = (Attr)nnm.item(j);
                if (!"http://www.w3.org/2000/xmlns/".equals(no.getNamespaceURI())) continue;
                map.put(no.getLocalName(), no.getValue());
            }
            this.stack.push(map);
        }

        protected Map<String, String> pop() {
            return this.stack.pop();
        }

        protected boolean containsNamespace(String namespace) {
            for (Map<String, String> map : this.stack) {
                if (!map.containsValue(namespace)) continue;
                return true;
            }
            return false;
        }
    }
}

