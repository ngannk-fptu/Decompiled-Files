/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.dom;

import freemarker.core._UnexpectedTypeErrorExplainerTemplateModel;
import freemarker.ext.dom.AtAtKey;
import freemarker.ext.dom.AttributeNodeModel;
import freemarker.ext.dom.CharacterDataNodeModel;
import freemarker.ext.dom.DocumentModel;
import freemarker.ext.dom.DocumentTypeModel;
import freemarker.ext.dom.ElementModel;
import freemarker.ext.dom.NodeListModel;
import freemarker.ext.dom.NodeOutputter;
import freemarker.ext.dom.PINodeModel;
import freemarker.ext.dom.XPathSupport;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.log.Logger;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNodeModel;
import freemarker.template.TemplateNodeModelEx;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateSequenceModel;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public abstract class NodeModel
implements TemplateNodeModelEx,
TemplateHashModel,
TemplateSequenceModel,
AdapterTemplateModel,
WrapperTemplateModel,
_UnexpectedTypeErrorExplainerTemplateModel {
    private static final Logger LOG = Logger.getLogger("freemarker.dom");
    private static final Object STATIC_LOCK = new Object();
    private static DocumentBuilderFactory docBuilderFactory;
    private static final Map xpathSupportMap;
    private static XPathSupport jaxenXPathSupport;
    private static ErrorHandler errorHandler;
    static Class xpathSupportClass;
    final Node node;
    private TemplateSequenceModel children;
    private NodeModel parent;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    public static void setDocumentBuilderFactory(DocumentBuilderFactory docBuilderFactory) {
        Object object = STATIC_LOCK;
        synchronized (object) {
            NodeModel.docBuilderFactory = docBuilderFactory;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static DocumentBuilderFactory getDocumentBuilderFactory() {
        Object object = STATIC_LOCK;
        synchronized (object) {
            if (docBuilderFactory == null) {
                DocumentBuilderFactory newFactory = DocumentBuilderFactory.newInstance();
                newFactory.setNamespaceAware(true);
                newFactory.setIgnoringElementContentWhitespace(true);
                docBuilderFactory = newFactory;
            }
            return docBuilderFactory;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    public static void setErrorHandler(ErrorHandler errorHandler) {
        Object object = STATIC_LOCK;
        synchronized (object) {
            NodeModel.errorHandler = errorHandler;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ErrorHandler getErrorHandler() {
        Object object = STATIC_LOCK;
        synchronized (object) {
            return errorHandler;
        }
    }

    public static NodeModel parse(InputSource is, boolean removeComments, boolean removePIs) throws SAXException, IOException, ParserConfigurationException {
        Document doc;
        DocumentBuilder builder = NodeModel.getDocumentBuilderFactory().newDocumentBuilder();
        ErrorHandler errorHandler = NodeModel.getErrorHandler();
        if (errorHandler != null) {
            builder.setErrorHandler(errorHandler);
        }
        try {
            doc = builder.parse(is);
        }
        catch (MalformedURLException e) {
            if (is.getSystemId() == null && is.getCharacterStream() == null && is.getByteStream() == null) {
                throw new MalformedURLException("The SAX InputSource has systemId == null && characterStream == null && byteStream == null. This is often because it was created with a null InputStream or Reader, which is often because the XML file it should point to was not found. (The original exception was: " + e + ")");
            }
            throw e;
        }
        if (removeComments && removePIs) {
            NodeModel.simplify(doc);
        } else {
            if (removeComments) {
                NodeModel.removeComments(doc);
            }
            if (removePIs) {
                NodeModel.removePIs(doc);
            }
            NodeModel.mergeAdjacentText(doc);
        }
        return NodeModel.wrap(doc);
    }

    public static NodeModel parse(InputSource is) throws SAXException, IOException, ParserConfigurationException {
        return NodeModel.parse(is, true, true);
    }

    public static NodeModel parse(File f, boolean removeComments, boolean removePIs) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilder builder = NodeModel.getDocumentBuilderFactory().newDocumentBuilder();
        ErrorHandler errorHandler = NodeModel.getErrorHandler();
        if (errorHandler != null) {
            builder.setErrorHandler(errorHandler);
        }
        Document doc = builder.parse(f);
        if (removeComments && removePIs) {
            NodeModel.simplify(doc);
        } else {
            if (removeComments) {
                NodeModel.removeComments(doc);
            }
            if (removePIs) {
                NodeModel.removePIs(doc);
            }
            NodeModel.mergeAdjacentText(doc);
        }
        return NodeModel.wrap(doc);
    }

    public static NodeModel parse(File f) throws SAXException, IOException, ParserConfigurationException {
        return NodeModel.parse(f, true, true);
    }

    protected NodeModel(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return this.node;
    }

    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        if (key.startsWith("@@")) {
            if (key.equals(AtAtKey.TEXT.getKey())) {
                return new SimpleScalar(NodeModel.getText(this.node));
            }
            if (key.equals(AtAtKey.NAMESPACE.getKey())) {
                String nsURI = this.node.getNamespaceURI();
                return nsURI == null ? null : new SimpleScalar(nsURI);
            }
            if (key.equals(AtAtKey.LOCAL_NAME.getKey())) {
                String localName = this.node.getLocalName();
                if (localName == null) {
                    localName = this.getNodeName();
                }
                return new SimpleScalar(localName);
            }
            if (key.equals(AtAtKey.MARKUP.getKey())) {
                StringBuilder buf = new StringBuilder();
                NodeOutputter nu = new NodeOutputter(this.node);
                nu.outputContent(this.node, buf);
                return new SimpleScalar(buf.toString());
            }
            if (key.equals(AtAtKey.NESTED_MARKUP.getKey())) {
                StringBuilder buf = new StringBuilder();
                NodeOutputter nu = new NodeOutputter(this.node);
                nu.outputContent(this.node.getChildNodes(), buf);
                return new SimpleScalar(buf.toString());
            }
            if (key.equals(AtAtKey.QNAME.getKey())) {
                String qname = this.getQualifiedName();
                return qname != null ? new SimpleScalar(qname) : null;
            }
            if (AtAtKey.containsKey(key)) {
                throw new TemplateModelException("\"" + key + "\" is not supported for an XML node of type \"" + this.getNodeType() + "\".");
            }
            throw new TemplateModelException("Unsupported @@ key: " + key);
        }
        XPathSupport xps = this.getXPathSupport();
        if (xps == null) {
            throw new TemplateModelException("No XPath support is available (add Apache Xalan or Jaxen as dependency). This is either malformed, or an XPath expression: " + key);
        }
        return xps.executeQuery(this.node, key);
    }

    @Override
    public TemplateNodeModel getParentNode() {
        if (this.parent == null) {
            Node parentNode = this.node.getParentNode();
            if (parentNode == null && this.node instanceof Attr) {
                parentNode = ((Attr)this.node).getOwnerElement();
            }
            this.parent = NodeModel.wrap(parentNode);
        }
        return this.parent;
    }

    @Override
    public TemplateNodeModelEx getPreviousSibling() throws TemplateModelException {
        return NodeModel.wrap(this.node.getPreviousSibling());
    }

    @Override
    public TemplateNodeModelEx getNextSibling() throws TemplateModelException {
        return NodeModel.wrap(this.node.getNextSibling());
    }

    @Override
    public TemplateSequenceModel getChildNodes() {
        if (this.children == null) {
            this.children = new NodeListModel(this.node.getChildNodes(), this);
        }
        return this.children;
    }

    @Override
    public final String getNodeType() throws TemplateModelException {
        short nodeType = this.node.getNodeType();
        switch (nodeType) {
            case 2: {
                return "attribute";
            }
            case 4: {
                return "text";
            }
            case 8: {
                return "comment";
            }
            case 11: {
                return "document_fragment";
            }
            case 9: {
                return "document";
            }
            case 10: {
                return "document_type";
            }
            case 1: {
                return "element";
            }
            case 6: {
                return "entity";
            }
            case 5: {
                return "entity_reference";
            }
            case 12: {
                return "notation";
            }
            case 7: {
                return "pi";
            }
            case 3: {
                return "text";
            }
        }
        throw new TemplateModelException("Unknown node type: " + nodeType + ". This should be impossible!");
    }

    public TemplateModel exec(List args) throws TemplateModelException {
        if (args.size() != 1) {
            throw new TemplateModelException("Expecting exactly one arguments");
        }
        String query = (String)args.get(0);
        XPathSupport xps = this.getXPathSupport();
        if (xps == null) {
            throw new TemplateModelException("No XPath support available");
        }
        return xps.executeQuery(this.node, query);
    }

    @Override
    public final int size() {
        return 1;
    }

    @Override
    public final TemplateModel get(int i) {
        return i == 0 ? this : null;
    }

    @Override
    public String getNodeNamespace() {
        short nodeType = this.node.getNodeType();
        if (nodeType != 2 && nodeType != 1) {
            return null;
        }
        String result = this.node.getNamespaceURI();
        if (result == null && nodeType == 1) {
            result = "";
        } else if ("".equals(result) && nodeType == 2) {
            result = null;
        }
        return result;
    }

    public final int hashCode() {
        return this.node.hashCode();
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        return other.getClass() == this.getClass() && ((NodeModel)other).node.equals(this.node);
    }

    public static NodeModel wrap(Node node) {
        if (node == null) {
            return null;
        }
        NodeModel result = null;
        switch (node.getNodeType()) {
            case 9: {
                result = new DocumentModel((Document)node);
                break;
            }
            case 1: {
                result = new ElementModel((Element)node);
                break;
            }
            case 2: {
                result = new AttributeNodeModel((Attr)node);
                break;
            }
            case 3: 
            case 4: 
            case 8: {
                result = new CharacterDataNodeModel((CharacterData)node);
                break;
            }
            case 7: {
                result = new PINodeModel((ProcessingInstruction)node);
                break;
            }
            case 10: {
                result = new DocumentTypeModel((DocumentType)node);
            }
        }
        return result;
    }

    public static void removeComments(Node parent) {
        Node child = parent.getFirstChild();
        while (child != null) {
            Node nextSibling = child.getNextSibling();
            if (child.getNodeType() == 8) {
                parent.removeChild(child);
            } else if (child.hasChildNodes()) {
                NodeModel.removeComments(child);
            }
            child = nextSibling;
        }
    }

    public static void removePIs(Node parent) {
        Node child = parent.getFirstChild();
        while (child != null) {
            Node nextSibling = child.getNextSibling();
            if (child.getNodeType() == 7) {
                parent.removeChild(child);
            } else if (child.hasChildNodes()) {
                NodeModel.removePIs(child);
            }
            child = nextSibling;
        }
    }

    public static void mergeAdjacentText(Node parent) {
        NodeModel.mergeAdjacentText(parent, new StringBuilder(0));
    }

    private static void mergeAdjacentText(Node parent, StringBuilder collectorBuf) {
        Node child = parent.getFirstChild();
        while (child != null) {
            Node next = child.getNextSibling();
            if (child instanceof Text) {
                boolean atFirstText = true;
                while (next instanceof Text) {
                    if (atFirstText) {
                        collectorBuf.setLength(0);
                        collectorBuf.ensureCapacity(child.getNodeValue().length() + next.getNodeValue().length());
                        collectorBuf.append(child.getNodeValue());
                        atFirstText = false;
                    }
                    collectorBuf.append(next.getNodeValue());
                    parent.removeChild(next);
                    next = child.getNextSibling();
                }
                if (!atFirstText && collectorBuf.length() != 0) {
                    ((CharacterData)child).setData(collectorBuf.toString());
                }
            } else {
                NodeModel.mergeAdjacentText(child, collectorBuf);
            }
            child = next;
        }
    }

    public static void simplify(Node parent) {
        NodeModel.simplify(parent, new StringBuilder(0));
    }

    private static void simplify(Node parent, StringBuilder collectorTextChildBuff) {
        Node collectorTextChild = null;
        Node child = parent.getFirstChild();
        while (child != null) {
            Node next = child.getNextSibling();
            if (child.hasChildNodes()) {
                if (collectorTextChild != null) {
                    if (collectorTextChildBuff.length() != 0) {
                        ((CharacterData)collectorTextChild).setData(collectorTextChildBuff.toString());
                        collectorTextChildBuff.setLength(0);
                    }
                    collectorTextChild = null;
                }
                NodeModel.simplify(child, collectorTextChildBuff);
            } else {
                short type = child.getNodeType();
                if (type == 3 || type == 4) {
                    if (collectorTextChild != null) {
                        if (collectorTextChildBuff.length() == 0) {
                            collectorTextChildBuff.ensureCapacity(collectorTextChild.getNodeValue().length() + child.getNodeValue().length());
                            collectorTextChildBuff.append(collectorTextChild.getNodeValue());
                        }
                        collectorTextChildBuff.append(child.getNodeValue());
                        parent.removeChild(child);
                    } else {
                        collectorTextChild = child;
                        collectorTextChildBuff.setLength(0);
                    }
                } else if (type == 8) {
                    parent.removeChild(child);
                } else if (type == 7) {
                    parent.removeChild(child);
                } else if (collectorTextChild != null) {
                    if (collectorTextChildBuff.length() != 0) {
                        ((CharacterData)collectorTextChild).setData(collectorTextChildBuff.toString());
                        collectorTextChildBuff.setLength(0);
                    }
                    collectorTextChild = null;
                }
            }
            child = next;
        }
        if (collectorTextChild != null && collectorTextChildBuff.length() != 0) {
            ((CharacterData)collectorTextChild).setData(collectorTextChildBuff.toString());
            collectorTextChildBuff.setLength(0);
        }
    }

    NodeModel getDocumentNodeModel() {
        if (this.node instanceof Document) {
            return this;
        }
        return NodeModel.wrap(this.node.getOwnerDocument());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void useDefaultXPathSupport() {
        Object object = STATIC_LOCK;
        synchronized (object) {
            xpathSupportClass = null;
            jaxenXPathSupport = null;
            try {
                NodeModel.useXalanXPathSupport();
            }
            catch (ClassNotFoundException classNotFoundException) {
            }
            catch (Exception e) {
                LOG.debug("Failed to use Xalan XPath support.", e);
            }
            catch (IllegalAccessError e) {
                LOG.debug("Failed to use Xalan internal XPath support.", e);
            }
            if (xpathSupportClass == null) {
                try {
                    NodeModel.useSunInternalXPathSupport();
                }
                catch (Exception e) {
                    LOG.debug("Failed to use Sun internal XPath support.", e);
                }
                catch (IllegalAccessError e) {
                    LOG.debug("Failed to use Sun internal XPath support. Tip: On Java 9+, you may need Xalan or Jaxen+Saxpath.", e);
                }
            }
            if (xpathSupportClass == null) {
                try {
                    NodeModel.useJaxenXPathSupport();
                }
                catch (ClassNotFoundException e) {
                }
                catch (Exception | IllegalAccessError e) {
                    LOG.debug("Failed to use Jaxen XPath support.", e);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void useJaxenXPathSupport() throws Exception {
        Class.forName("org.jaxen.dom.DOMXPath");
        Class<?> c = Class.forName("freemarker.ext.dom.JaxenXPathSupport");
        jaxenXPathSupport = (XPathSupport)c.newInstance();
        Object object = STATIC_LOCK;
        synchronized (object) {
            xpathSupportClass = c;
        }
        LOG.debug("Using Jaxen classes for XPath support");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void useXalanXPathSupport() throws Exception {
        Class.forName("org.apache.xpath.XPath");
        Class<?> c = Class.forName("freemarker.ext.dom.XalanXPathSupport");
        Object object = STATIC_LOCK;
        synchronized (object) {
            xpathSupportClass = c;
        }
        LOG.debug("Using Xalan classes for XPath support");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void useSunInternalXPathSupport() throws Exception {
        Class.forName("com.sun.org.apache.xpath.internal.XPath");
        Class<?> c = Class.forName("freemarker.ext.dom.SunInternalXalanXPathSupport");
        Object object = STATIC_LOCK;
        synchronized (object) {
            xpathSupportClass = c;
        }
        LOG.debug("Using Sun's internal Xalan classes for XPath support");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void setXPathSupportClass(Class cl) {
        if (cl != null && !XPathSupport.class.isAssignableFrom(cl)) {
            throw new RuntimeException("Class " + cl.getName() + " does not implement freemarker.ext.dom.XPathSupport");
        }
        Object object = STATIC_LOCK;
        synchronized (object) {
            xpathSupportClass = cl;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Class getXPathSupportClass() {
        Object object = STATIC_LOCK;
        synchronized (object) {
            return xpathSupportClass;
        }
    }

    private static String getText(Node node) {
        String result = "";
        if (node instanceof Text || node instanceof CDATASection) {
            result = ((CharacterData)node).getData();
        } else if (node instanceof Element) {
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); ++i) {
                result = result + NodeModel.getText(children.item(i));
            }
        } else if (node instanceof Document) {
            result = NodeModel.getText(((Document)node).getDocumentElement());
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    XPathSupport getXPathSupport() {
        if (jaxenXPathSupport != null) {
            return jaxenXPathSupport;
        }
        XPathSupport xps = null;
        Document doc = this.node.getOwnerDocument();
        if (doc == null) {
            doc = (Document)this.node;
        }
        Document document = doc;
        synchronized (document) {
            WeakReference ref = (WeakReference)xpathSupportMap.get(doc);
            if (ref != null) {
                xps = (XPathSupport)ref.get();
            }
            if (xps == null && xpathSupportClass != null) {
                try {
                    xps = (XPathSupport)xpathSupportClass.newInstance();
                    xpathSupportMap.put(doc, new WeakReference<XPathSupport>(xps));
                }
                catch (Exception e) {
                    LOG.error("Error instantiating xpathSupport class", e);
                }
            }
        }
        return xps;
    }

    String getQualifiedName() throws TemplateModelException {
        return this.getNodeName();
    }

    public Object getAdaptedObject(Class hint) {
        return this.node;
    }

    @Override
    public Object getWrappedObject() {
        return this.node;
    }

    @Override
    public Object[] explainTypeError(Class[] expectedClasses) {
        for (int i = 0; i < expectedClasses.length; ++i) {
            Class expectedClass = expectedClasses[i];
            if (!TemplateDateModel.class.isAssignableFrom(expectedClass) && !TemplateNumberModel.class.isAssignableFrom(expectedClass) && !TemplateBooleanModel.class.isAssignableFrom(expectedClass)) continue;
            return new Object[]{"XML node values are always strings (text), that is, they can't be used as number, date/time/datetime or boolean without explicit conversion (such as someNode?number, someNode?datetime.xs, someNode?date.xs, someNode?time.xs, someNode?boolean)."};
        }
        return null;
    }

    static {
        xpathSupportMap = Collections.synchronizedMap(new WeakHashMap());
        try {
            NodeModel.useDefaultXPathSupport();
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (xpathSupportClass == null && LOG.isWarnEnabled()) {
            LOG.warn("No XPath support is available. If you need it, add Apache Xalan or Jaxen as dependency.");
        }
    }
}

