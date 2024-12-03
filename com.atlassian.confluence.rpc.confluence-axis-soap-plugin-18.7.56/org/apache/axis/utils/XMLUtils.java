/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.soap.SOAPException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.apache.axis.AxisProperties;
import org.apache.axis.InternalException;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.components.encoding.XMLEncoder;
import org.apache.axis.components.encoding.XMLEncoderFactory;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Base64;
import org.apache.axis.utils.DOM2Writer;
import org.apache.axis.utils.DefaultEntityResolver;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class XMLUtils {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$utils$XMLUtils == null ? (class$org$apache$axis$utils$XMLUtils = XMLUtils.class$("org.apache.axis.utils.XMLUtils")) : class$org$apache$axis$utils$XMLUtils).getName());
    public static final String httpAuthCharEncoding = "ISO-8859-1";
    private static final String saxParserFactoryProperty = "javax.xml.parsers.SAXParserFactory";
    private static DocumentBuilderFactory dbf = XMLUtils.getDOMFactory();
    private static SAXParserFactory saxFactory;
    private static Stack saxParsers;
    private static DefaultHandler doNothingContentHandler;
    private static String EMPTY;
    private static ByteArrayInputStream bais;
    private static boolean tryReset;
    protected static boolean enableParserReuse;
    private static ThreadLocalDocumentBuilder documentBuilder;
    static /* synthetic */ Class class$org$apache$axis$utils$XMLUtils;
    static /* synthetic */ Class class$org$apache$axis$utils$XMLUtils$ParserErrorHandler;

    public static String xmlEncodeString(String orig) {
        XMLEncoder encoder = XMLUtils.getXMLEncoder(MessageContext.getCurrentContext());
        return encoder.encode(orig);
    }

    public static XMLEncoder getXMLEncoder(MessageContext msgContext) {
        return XMLUtils.getXMLEncoder(XMLUtils.getEncoding(null, msgContext));
    }

    public static XMLEncoder getXMLEncoder(String encoding) {
        XMLEncoder encoder = null;
        try {
            encoder = XMLEncoderFactory.getEncoder(encoding);
        }
        catch (Exception e) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
            encoder = XMLEncoderFactory.getDefaultEncoder();
        }
        return encoder;
    }

    public static String getEncoding(MessageContext msgContext) {
        XMLEncoder encoder = XMLUtils.getXMLEncoder(msgContext);
        return encoder.getEncoding();
    }

    public static String getEncoding() {
        XMLEncoder encoder = XMLUtils.getXMLEncoder(MessageContext.getCurrentContext());
        return encoder.getEncoding();
    }

    public static void initSAXFactory(String factoryClassName, boolean namespaceAware, boolean validating) {
        if (factoryClassName != null) {
            try {
                saxFactory = (SAXParserFactory)Class.forName(factoryClassName).newInstance();
                if (System.getProperty(saxParserFactoryProperty) == null) {
                    System.setProperty(saxParserFactoryProperty, factoryClassName);
                }
            }
            catch (Exception e) {
                log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
                saxFactory = null;
            }
        } else {
            saxFactory = SAXParserFactory.newInstance();
        }
        saxFactory.setNamespaceAware(namespaceAware);
        saxFactory.setValidating(validating);
        saxParsers.clear();
    }

    private static DocumentBuilderFactory getDOMFactory() {
        DocumentBuilderFactory dbf;
        try {
            dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
        }
        catch (Exception e) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
            dbf = null;
        }
        return dbf;
    }

    public static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        return (DocumentBuilder)documentBuilder.get();
    }

    public static void releaseDocumentBuilder(DocumentBuilder db) {
        try {
            db.setErrorHandler(null);
        }
        catch (Throwable t) {
            log.debug((Object)"Failed to set ErrorHandler to null on DocumentBuilder", t);
        }
        try {
            db.setEntityResolver(null);
        }
        catch (Throwable t) {
            log.debug((Object)"Failed to set EntityResolver to null on DocumentBuilder", t);
        }
    }

    public static synchronized SAXParser getSAXParser() {
        if (enableParserReuse && !saxParsers.empty()) {
            return (SAXParser)saxParsers.pop();
        }
        try {
            SAXParser parser = saxFactory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            try {
                reader.setEntityResolver(new DefaultEntityResolver());
            }
            catch (Throwable t) {
                log.debug((Object)"Failed to set EntityResolver on DocumentBuilder", t);
            }
            reader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
            return parser;
        }
        catch (ParserConfigurationException e) {
            log.error((Object)Messages.getMessage("parserConfigurationException00"), (Throwable)e);
            return null;
        }
        catch (SAXException se) {
            log.error((Object)Messages.getMessage("SAXException00"), (Throwable)se);
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void releaseSAXParser(SAXParser parser) {
        block11: {
            if (!tryReset || !enableParserReuse) {
                return;
            }
            try {
                XMLReader xmlReader = parser.getXMLReader();
                if (null != xmlReader) {
                    xmlReader.setContentHandler(doNothingContentHandler);
                    xmlReader.setDTDHandler(doNothingContentHandler);
                    try {
                        xmlReader.setEntityResolver(doNothingContentHandler);
                    }
                    catch (Throwable t) {
                        log.debug((Object)"Failed to set EntityResolver on DocumentBuilder", t);
                    }
                    try {
                        xmlReader.setErrorHandler(doNothingContentHandler);
                    }
                    catch (Throwable t) {
                        log.debug((Object)"Failed to set ErrorHandler on DocumentBuilder", t);
                    }
                    Class clazz = class$org$apache$axis$utils$XMLUtils == null ? (class$org$apache$axis$utils$XMLUtils = XMLUtils.class$("org.apache.axis.utils.XMLUtils")) : class$org$apache$axis$utils$XMLUtils;
                    synchronized (clazz) {
                        saxParsers.push(parser);
                        break block11;
                    }
                }
                tryReset = false;
            }
            catch (SAXException e) {
                tryReset = false;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Document newDocument() throws ParserConfigurationException {
        DocumentBuilder db = null;
        try {
            Document doc;
            db = XMLUtils.getDocumentBuilder();
            Document document = doc = db.newDocument();
            return document;
        }
        finally {
            if (db != null) {
                XMLUtils.releaseDocumentBuilder(db);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Document newDocument(InputSource inp) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder db = null;
        try {
            Document doc;
            db = XMLUtils.getDocumentBuilder();
            try {
                db.setEntityResolver(new DefaultEntityResolver());
            }
            catch (Throwable t) {
                log.debug((Object)"Failed to set EntityResolver on DocumentBuilder", t);
            }
            try {
                db.setErrorHandler(new ParserErrorHandler());
            }
            catch (Throwable t) {
                log.debug((Object)"Failed to set ErrorHandler on DocumentBuilder", t);
            }
            Document document = doc = db.parse(inp);
            return document;
        }
        finally {
            if (db != null) {
                XMLUtils.releaseDocumentBuilder(db);
            }
        }
    }

    public static Document newDocument(InputStream inp) throws ParserConfigurationException, SAXException, IOException {
        return XMLUtils.newDocument(new InputSource(inp));
    }

    public static Document newDocument(String uri) throws ParserConfigurationException, SAXException, IOException {
        return XMLUtils.newDocument(uri, null, null);
    }

    public static Document newDocument(String uri, String username, String password) throws ParserConfigurationException, SAXException, IOException {
        InputSource ins = XMLUtils.getInputSourceFromURI(uri, username, password);
        Document doc = XMLUtils.newDocument(ins);
        if (ins.getByteStream() != null) {
            ins.getByteStream().close();
        } else if (ins.getCharacterStream() != null) {
            ins.getCharacterStream().close();
        }
        return doc;
    }

    private static String privateElementToString(Element element, boolean omitXMLDecl) {
        return DOM2Writer.nodeToString(element, omitXMLDecl);
    }

    public static String ElementToString(Element element) {
        return XMLUtils.privateElementToString(element, true);
    }

    public static String DocumentToString(Document doc) {
        return XMLUtils.privateElementToString(doc.getDocumentElement(), false);
    }

    public static String PrettyDocumentToString(Document doc) {
        StringWriter sw = new StringWriter();
        XMLUtils.PrettyElementToWriter(doc.getDocumentElement(), sw);
        return sw.toString();
    }

    public static void privateElementToWriter(Element element, Writer writer, boolean omitXMLDecl, boolean pretty) {
        DOM2Writer.serializeAsXML(element, writer, omitXMLDecl, pretty);
    }

    public static void ElementToStream(Element element, OutputStream out) {
        Writer writer = XMLUtils.getWriter(out);
        XMLUtils.privateElementToWriter(element, writer, true, false);
    }

    public static void PrettyElementToStream(Element element, OutputStream out) {
        Writer writer = XMLUtils.getWriter(out);
        XMLUtils.privateElementToWriter(element, writer, true, true);
    }

    public static void ElementToWriter(Element element, Writer writer) {
        XMLUtils.privateElementToWriter(element, writer, true, false);
    }

    public static void PrettyElementToWriter(Element element, Writer writer) {
        XMLUtils.privateElementToWriter(element, writer, true, true);
    }

    public static void DocumentToStream(Document doc, OutputStream out) {
        Writer writer = XMLUtils.getWriter(out);
        XMLUtils.privateElementToWriter(doc.getDocumentElement(), writer, false, false);
    }

    public static void PrettyDocumentToStream(Document doc, OutputStream out) {
        Writer writer = XMLUtils.getWriter(out);
        XMLUtils.privateElementToWriter(doc.getDocumentElement(), writer, false, true);
    }

    private static Writer getWriter(OutputStream os) {
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(os, "UTF-8");
        }
        catch (UnsupportedEncodingException uee) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)uee);
            writer = new OutputStreamWriter(os);
        }
        return writer;
    }

    public static void DocumentToWriter(Document doc, Writer writer) {
        XMLUtils.privateElementToWriter(doc.getDocumentElement(), writer, false, false);
    }

    public static void PrettyDocumentToWriter(Document doc, Writer writer) {
        XMLUtils.privateElementToWriter(doc.getDocumentElement(), writer, false, true);
    }

    public static Element StringToElement(String namespace, String name, String string) {
        try {
            Document doc = XMLUtils.newDocument();
            Element element = doc.createElementNS(namespace, name);
            Text text = doc.createTextNode(string);
            element.appendChild(text);
            return element;
        }
        catch (ParserConfigurationException e) {
            throw new InternalException(e);
        }
    }

    public static String getInnerXMLString(Element element) {
        String elementString = XMLUtils.ElementToString(element);
        int start = elementString.indexOf(">") + 1;
        int end = elementString.lastIndexOf("</");
        if (end > 0) {
            return elementString.substring(start, end);
        }
        return null;
    }

    public static String getPrefix(String uri, Node e) {
        while (e != null && e.getNodeType() == 1) {
            NamedNodeMap attrs = e.getAttributes();
            for (int n = 0; n < attrs.getLength(); ++n) {
                Attr a = (Attr)attrs.item(n);
                String name = a.getName();
                if (!name.startsWith("xmlns:") || !a.getNodeValue().equals(uri)) continue;
                return name.substring(6);
            }
            e = e.getParentNode();
        }
        return null;
    }

    public static String getNamespace(String prefix, Node e, Node stopNode) {
        while (e != null && e.getNodeType() == 1) {
            Attr attr = null;
            attr = prefix == null ? ((Element)e).getAttributeNode("xmlns") : ((Element)e).getAttributeNodeNS("http://www.w3.org/2000/xmlns/", prefix);
            if (attr != null) {
                return attr.getValue();
            }
            if (e == stopNode) {
                return null;
            }
            e = e.getParentNode();
        }
        return null;
    }

    public static String getNamespace(String prefix, Node e) {
        return XMLUtils.getNamespace(prefix, e, null);
    }

    public static QName getQNameFromString(String str, Node e) {
        return XMLUtils.getQNameFromString(str, e, false);
    }

    public static QName getFullQNameFromString(String str, Node e) {
        return XMLUtils.getQNameFromString(str, e, true);
    }

    private static QName getQNameFromString(String str, Node e, boolean defaultNS) {
        String ns;
        if (str == null || e == null) {
            return null;
        }
        int idx = str.indexOf(58);
        if (idx > -1) {
            String prefix = str.substring(0, idx);
            String ns2 = XMLUtils.getNamespace(prefix, e);
            if (ns2 == null) {
                return null;
            }
            return new QName(ns2, str.substring(idx + 1));
        }
        if (defaultNS && (ns = XMLUtils.getNamespace(null, e)) != null) {
            return new QName(ns, str);
        }
        return new QName("", str);
    }

    public static String getStringForQName(QName qname, Element e) {
        String uri = qname.getNamespaceURI();
        String prefix = XMLUtils.getPrefix(uri, e);
        if (prefix == null) {
            int i = 1;
            prefix = "ns" + i;
            while (XMLUtils.getNamespace(prefix, e) != null) {
                prefix = "ns" + ++i;
            }
            e.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + prefix, uri);
        }
        return prefix + ":" + qname.getLocalPart();
    }

    public static String getChildCharacterData(Element parentEl) {
        if (parentEl == null) {
            return null;
        }
        StringBuffer strBuf = new StringBuffer();
        for (Node tempNode = parentEl.getFirstChild(); tempNode != null; tempNode = tempNode.getNextSibling()) {
            switch (tempNode.getNodeType()) {
                case 3: 
                case 4: {
                    CharacterData charData = (CharacterData)tempNode;
                    strBuf.append(charData.getData());
                }
            }
        }
        return strBuf.toString();
    }

    public static InputSource getInputSourceFromURI(String uri) {
        return new InputSource(uri);
    }

    public static InputSource sourceToInputSource(Source source) {
        if (source instanceof SAXSource) {
            return ((SAXSource)source).getInputSource();
        }
        if (source instanceof DOMSource) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Node node = ((DOMSource)source).getNode();
            if (node instanceof Document) {
                node = ((Document)node).getDocumentElement();
            }
            Element domElement = (Element)node;
            XMLUtils.ElementToStream(domElement, baos);
            InputSource isource = new InputSource(source.getSystemId());
            isource.setByteStream(new ByteArrayInputStream(baos.toByteArray()));
            return isource;
        }
        if (source instanceof StreamSource) {
            StreamSource ss = (StreamSource)source;
            InputSource isource = new InputSource(ss.getSystemId());
            isource.setByteStream(ss.getInputStream());
            isource.setCharacterStream(ss.getReader());
            isource.setPublicId(ss.getPublicId());
            return isource;
        }
        return XMLUtils.getInputSourceFromURI(source.getSystemId());
    }

    private static InputSource getInputSourceFromURI(String uri, String username, String password) throws IOException, ProtocolException, UnsupportedEncodingException {
        URL wsdlurl = null;
        try {
            wsdlurl = new URL(uri);
        }
        catch (MalformedURLException e) {
            return new InputSource(uri);
        }
        if (username == null && wsdlurl.getUserInfo() == null) {
            return new InputSource(uri);
        }
        if (!wsdlurl.getProtocol().startsWith("http")) {
            return new InputSource(uri);
        }
        URLConnection connection = wsdlurl.openConnection();
        if (!(connection instanceof HttpURLConnection)) {
            return new InputSource(uri);
        }
        HttpURLConnection uconn = (HttpURLConnection)connection;
        String userinfo = wsdlurl.getUserInfo();
        uconn.setRequestMethod("GET");
        uconn.setAllowUserInteraction(false);
        uconn.setDefaultUseCaches(false);
        uconn.setDoInput(true);
        uconn.setDoOutput(false);
        uconn.setInstanceFollowRedirects(true);
        uconn.setUseCaches(false);
        String auth = null;
        if (userinfo != null) {
            auth = userinfo;
        } else if (username != null) {
            String string = auth = password == null ? username : username + ":" + password;
        }
        if (auth != null) {
            uconn.setRequestProperty("Authorization", "Basic " + XMLUtils.base64encode(auth.getBytes(httpAuthCharEncoding)));
        }
        uconn.connect();
        return new InputSource(uconn.getInputStream());
    }

    public static final String base64encode(byte[] bytes) {
        return new String(Base64.encode(bytes));
    }

    public static InputSource getEmptyInputSource() {
        return new InputSource(bais);
    }

    public static Node findNode(Node node, QName name) {
        if (name.getNamespaceURI().equals(node.getNamespaceURI()) && name.getLocalPart().equals(node.getLocalName())) {
            return node;
        }
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node ret = XMLUtils.findNode(children.item(i), name);
            if (ret == null) continue;
            return ret;
        }
        return null;
    }

    public static void normalize(Node node) {
        char ch;
        String data;
        if (node.getNodeType() == 3 && (data = ((Text)node).getData()).length() > 0 && ((ch = data.charAt(data.length() - 1)) == '\n' || ch == '\r' || ch == ' ')) {
            String data2 = XMLUtils.trim(data);
            ((Text)node).setData(data2);
        }
        for (Node currentChild = node.getFirstChild(); currentChild != null; currentChild = currentChild.getNextSibling()) {
            XMLUtils.normalize(currentChild);
        }
    }

    public static String trim(String str) {
        if (str.length() == 0) {
            return str;
        }
        if (str.length() == 1) {
            if ("\r".equals(str) || "\n".equals(str)) {
                return "";
            }
            return str;
        }
        int lastIdx = str.length() - 1;
        char last = str.charAt(lastIdx);
        while (lastIdx > 0 && (last == '\n' || last == '\r' || last == ' ')) {
            last = str.charAt(--lastIdx);
        }
        if (lastIdx == 0) {
            return "";
        }
        return str.substring(0, lastIdx);
    }

    public static Element[] asElementArray(List list) {
        Element[] elements = new Element[list.size()];
        int i = 0;
        Iterator detailIter = list.iterator();
        while (detailIter.hasNext()) {
            elements[i++] = (Element)detailIter.next();
        }
        return elements;
    }

    public static String getEncoding(Message message, MessageContext msgContext) {
        return XMLUtils.getEncoding(message, msgContext, XMLEncoderFactory.getDefaultEncoder());
    }

    public static String getEncoding(Message message, MessageContext msgContext, XMLEncoder defaultEncoder) {
        String encoding = null;
        try {
            if (message != null) {
                encoding = (String)message.getProperty("javax.xml.soap.character-set-encoding");
            }
        }
        catch (SOAPException e) {
            // empty catch block
        }
        if (msgContext == null) {
            msgContext = MessageContext.getCurrentContext();
        }
        if (msgContext != null && encoding == null) {
            encoding = (String)msgContext.getProperty("javax.xml.soap.character-set-encoding");
        }
        if (msgContext != null && encoding == null && msgContext.getAxisEngine() != null) {
            encoding = (String)msgContext.getAxisEngine().getOption("axis.xmlEncoding");
        }
        if (encoding == null && defaultEncoder != null) {
            encoding = defaultEncoder.getEncoding();
        }
        return encoding;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        saxParsers = new Stack();
        doNothingContentHandler = new DefaultHandler();
        EMPTY = "";
        bais = new ByteArrayInputStream(EMPTY.getBytes());
        tryReset = true;
        enableParserReuse = false;
        documentBuilder = new ThreadLocalDocumentBuilder();
        XMLUtils.initSAXFactory(null, true, false);
        String value = AxisProperties.getProperty("axis.xml.reuseParsers", "false");
        enableParserReuse = value.equalsIgnoreCase("true") || value.equals("1") || value.equalsIgnoreCase("yes");
    }

    public static class ParserErrorHandler
    implements ErrorHandler {
        protected static Log log = LogFactory.getLog((class$org$apache$axis$utils$XMLUtils$ParserErrorHandler == null ? (class$org$apache$axis$utils$XMLUtils$ParserErrorHandler = XMLUtils.class$("org.apache.axis.utils.XMLUtils$ParserErrorHandler")) : class$org$apache$axis$utils$XMLUtils$ParserErrorHandler).getName());

        private String getParseExceptionInfo(SAXParseException spe) {
            String systemId = spe.getSystemId();
            if (systemId == null) {
                systemId = "null";
            }
            String info = "URI=" + systemId + " Line=" + spe.getLineNumber() + ": " + spe.getMessage();
            return info;
        }

        public void warning(SAXParseException spe) throws SAXException {
            if (log.isDebugEnabled()) {
                log.debug((Object)Messages.getMessage("warning00", this.getParseExceptionInfo(spe)));
            }
        }

        public void error(SAXParseException spe) throws SAXException {
            String message = "Error: " + this.getParseExceptionInfo(spe);
            throw new SAXException(message);
        }

        public void fatalError(SAXParseException spe) throws SAXException {
            String message = "Fatal Error: " + this.getParseExceptionInfo(spe);
            throw new SAXException(message);
        }
    }

    private static class ThreadLocalDocumentBuilder
    extends ThreadLocal {
        private ThreadLocalDocumentBuilder() {
        }

        protected Object initialValue() {
            try {
                return XMLUtils.getDOMFactory().newDocumentBuilder();
            }
            catch (ParserConfigurationException e) {
                log.error((Object)Messages.getMessage("parserConfigurationException00"), (Throwable)e);
                return null;
            }
        }
    }
}

