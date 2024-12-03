/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xmlbeans.CDataBookmark;
import org.apache.xmlbeans.QNameCache;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlDocumentProperties;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlLineNumber;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlRuntimeException;
import org.apache.xmlbeans.XmlSaxHandler;
import org.apache.xmlbeans.XmlTokenSource;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.common.ResolverUtil;
import org.apache.xmlbeans.impl.common.SAXHelper;
import org.apache.xmlbeans.impl.common.XmlLocale;
import org.apache.xmlbeans.impl.store.CdataNode;
import org.apache.xmlbeans.impl.store.CharUtil;
import org.apache.xmlbeans.impl.store.Cur;
import org.apache.xmlbeans.impl.store.Cursor;
import org.apache.xmlbeans.impl.store.DomImpl;
import org.apache.xmlbeans.impl.store.Jsr173;
import org.apache.xmlbeans.impl.store.QNameFactory;
import org.apache.xmlbeans.impl.store.Saaj;
import org.apache.xmlbeans.impl.store.SaajCdataNode;
import org.apache.xmlbeans.impl.store.SaajTextNode;
import org.apache.xmlbeans.impl.store.TextNode;
import org.apache.xmlbeans.impl.store.Xobj;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

public final class Locale
implements DOMImplementation,
Saaj.SaajCallback,
XmlLocale {
    private static final Logger LOG = LogManager.getLogger(Locale.class);
    static final int ROOT = 1;
    static final int ELEM = 2;
    static final int ATTR = 3;
    static final int COMMENT = 4;
    static final int PROCINST = 5;
    static final int TEXT = 0;
    static final String _xsi = "http://www.w3.org/2001/XMLSchema-instance";
    static final String _openFragUri = "http://www.openuri.org/fragment";
    static final String _xml1998Uri = "http://www.w3.org/XML/1998/namespace";
    static final String _xmlnsUri = "http://www.w3.org/2000/xmlns/";
    static final QName _xsiNil = new QName("http://www.w3.org/2001/XMLSchema-instance", "nil", "xsi");
    static final QName _xsiType = new QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi");
    static final QName _xsiLoc = new QName("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation", "xsi");
    static final QName _xsiNoLoc = new QName("http://www.w3.org/2001/XMLSchema-instance", "noNamespaceSchemaLocation", "xsi");
    static final QName _openuriFragment = new QName("http://www.openuri.org/fragment", "fragment", "frag");
    static final QName _xmlFragment = new QName("xml-fragment");
    private static final ThreadLocal<SoftReference<ScrubBuffer>> tl_scrubBuffer = ThreadLocal.withInitial(() -> new SoftReference<ScrubBuffer>(new ScrubBuffer()));
    boolean _noSync;
    SchemaTypeLoader _schemaTypeLoader;
    private ReferenceQueue<Ref> _refQueue;
    private int _entryCount;
    int _numTempFramesLeft;
    Cur[] _tempFrames;
    Cur _curPool;
    int _curPoolCount;
    Cur _registered;
    ChangeListener _changeListeners;
    long _versionAll;
    long _versionSansText;
    Cur.Locations _locations;
    private CharUtil _charUtil;
    int _offSrc;
    int _cchSrc;
    Saaj _saaj;
    DomImpl.Dom _ownerDoc;
    QNameFactory _qnameFactory;
    boolean _validateOnSet;
    int _posTemp;
    nthCache _nthCache_A = new nthCache();
    nthCache _nthCache_B = new nthCache();
    domNthCache _domNthCache_A = new domNthCache();
    domNthCache _domNthCache_B = new domNthCache();

    private Locale(SchemaTypeLoader stl, XmlOptions options) {
        options = XmlOptions.maskNull(options);
        this._noSync = options.isUnsynchronized();
        this._numTempFramesLeft = 8;
        this._tempFrames = new Cur[8];
        this._qnameFactory = new DefaultQNameFactory();
        this._locations = new Cur.Locations(this);
        this._schemaTypeLoader = stl;
        this._validateOnSet = options.isValidateOnSet();
        this._saaj = options.getSaaj();
        if (this._saaj != null) {
            this._saaj.setCallback(this);
        }
    }

    public static Locale getLocale(SchemaTypeLoader stl, XmlOptions options) {
        Locale l;
        Object source;
        if (stl == null) {
            stl = XmlBeans.getContextTypeLoader();
        }
        if ((source = (options = XmlOptions.maskNull(options)).getUseSameLocale()) == null) {
            return new Locale(stl, options);
        }
        if (source instanceof Locale) {
            l = (Locale)source;
        } else if (source instanceof XmlTokenSource) {
            l = (Locale)((XmlTokenSource)source).monitor();
        } else {
            throw new IllegalArgumentException("Source locale not understood: " + source);
        }
        if (l._schemaTypeLoader != stl) {
            throw new IllegalArgumentException("Source locale does not support same schema type loader");
        }
        if (l._saaj != null && l._saaj != options.getSaaj()) {
            throw new IllegalArgumentException("Source locale does not support same saaj");
        }
        if (l._validateOnSet && !options.isValidateOnSet()) {
            throw new IllegalArgumentException("Source locale does not support same validate on set");
        }
        return l;
    }

    public static void associateSourceName(Cur c, XmlOptions options) {
        String sourceName;
        String string = sourceName = options == null ? null : options.getDocumentSourceName();
        if (sourceName != null) {
            Locale.getDocProps(c, true).setSourceName(sourceName);
        }
    }

    public static void autoTypeDocument(Cur c, SchemaType requestedType, XmlOptions options) throws XmlException {
        assert (c.isRoot());
        SchemaType optionType = (options = XmlOptions.maskNull(options)).getDocumentType();
        if (optionType != null) {
            c.setType(optionType);
            return;
        }
        SchemaType type = null;
        if (requestedType == null || requestedType.getName() != null) {
            SchemaType xsiSchemaType;
            QName xsiTypeName = c.getXsiTypeName();
            SchemaType schemaType = xsiSchemaType = xsiTypeName == null ? null : c._locale._schemaTypeLoader.findType(xsiTypeName);
            if (requestedType == null || requestedType.isAssignableFrom(xsiSchemaType)) {
                type = xsiSchemaType;
            }
        }
        if (type == null && (requestedType == null || requestedType.isDocumentType())) {
            QName requesteddocElemNameName;
            assert (c.isRoot());
            c.push();
            QName docElemName = !c.hasAttrs() && Locale.toFirstChildElement(c) && !Locale.toNextSiblingElement(c) ? c.getName() : null;
            c.pop();
            if (docElemName != null && (type = c._locale._schemaTypeLoader.findDocumentType(docElemName)) != null && requestedType != null && !(requesteddocElemNameName = requestedType.getDocumentElementName()).equals(docElemName) && !requestedType.isValidSubstitution(docElemName)) {
                throw new XmlException("Element " + QNameHelper.pretty(docElemName) + " is not a valid " + QNameHelper.pretty(requesteddocElemNameName) + " document or a valid substitution.");
            }
        }
        if (type == null && requestedType == null) {
            c.push();
            type = Locale.toFirstNormalAttr(c) && !Locale.toNextNormalAttr(c) ? c._locale._schemaTypeLoader.findAttributeType(c.getName()) : null;
            c.pop();
        }
        if (type == null) {
            type = requestedType;
        }
        if (type == null) {
            type = XmlBeans.NO_TYPE;
        }
        c.setType(type);
        if (requestedType != null) {
            if (type.isDocumentType()) {
                Locale.verifyDocumentType(c, type.getDocumentElementName());
            } else if (type.isAttributeType()) {
                Locale.verifyAttributeType(c, type.getAttributeTypeAttributeName());
            }
        }
    }

    private static boolean namespacesSame(QName n1, QName n2) {
        if (n1 == n2) {
            return true;
        }
        if (n1 == null || n2 == null) {
            return false;
        }
        return Objects.equals(n1.getNamespaceURI(), n2.getNamespaceURI());
    }

    private static void addNamespace(StringBuilder sb, QName name) {
        if (name.getNamespaceURI() == null) {
            sb.append("<no namespace>");
        } else {
            sb.append("\"");
            sb.append(name.getNamespaceURI());
            sb.append("\"");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void verifyDocumentType(Cur c, QName docElemName) throws XmlException {
        assert (c.isRoot());
        c.push();
        try {
            StringBuilder sb = null;
            if (!Locale.toFirstChildElement(c) || Locale.toNextSiblingElement(c)) {
                sb = new StringBuilder();
                sb.append("The document is not a ");
                sb.append(QNameHelper.pretty(docElemName));
                sb.append(c.isRoot() ? ": no document element" : ": multiple document elements");
            } else {
                QName name = c.getName();
                if (!name.equals(docElemName)) {
                    sb = new StringBuilder();
                    sb.append("The document is not a ");
                    sb.append(QNameHelper.pretty(docElemName));
                    if (docElemName.getLocalPart().equals(name.getLocalPart())) {
                        sb.append(": document element namespace mismatch ");
                        sb.append("expected ");
                        Locale.addNamespace(sb, docElemName);
                        sb.append(" got ");
                        Locale.addNamespace(sb, name);
                    } else if (Locale.namespacesSame(docElemName, name)) {
                        sb.append(": document element local name mismatch expected ").append(docElemName.getLocalPart()).append(" got ").append(name.getLocalPart());
                    } else {
                        sb.append(": document element mismatch got ");
                        sb.append(QNameHelper.pretty(name));
                    }
                }
            }
            if (sb != null) {
                XmlError err = XmlError.forCursor(sb.toString(), new Cursor(c));
                throw new XmlException(err.toString(), null, err);
            }
        }
        finally {
            c.pop();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void verifyAttributeType(Cur c, QName attrName) throws XmlException {
        assert (c.isRoot());
        c.push();
        try {
            StringBuilder sb = null;
            if (!Locale.toFirstNormalAttr(c) || Locale.toNextNormalAttr(c)) {
                sb = new StringBuilder();
                sb.append("The document is not a ");
                sb.append(QNameHelper.pretty(attrName));
                sb.append(c.isRoot() ? ": no attributes" : ": multiple attributes");
            } else {
                QName name = c.getName();
                if (!name.equals(attrName)) {
                    sb = new StringBuilder();
                    sb.append("The document is not a ");
                    sb.append(QNameHelper.pretty(attrName));
                    if (attrName.getLocalPart().equals(name.getLocalPart())) {
                        sb.append(": attribute namespace mismatch ");
                        sb.append("expected ");
                        Locale.addNamespace(sb, attrName);
                        sb.append(" got ");
                        Locale.addNamespace(sb, name);
                    } else if (Locale.namespacesSame(attrName, name)) {
                        sb.append(": attribute local name mismatch ");
                        sb.append("expected ").append(attrName.getLocalPart());
                        sb.append(" got ").append(name.getLocalPart());
                    } else {
                        sb.append(": attribute element mismatch ");
                        sb.append("got ");
                        sb.append(QNameHelper.pretty(name));
                    }
                }
            }
            if (sb != null) {
                XmlError err = XmlError.forCursor(sb.toString(), new Cursor(c));
                throw new XmlException(err.toString(), null, err);
            }
        }
        finally {
            c.pop();
        }
    }

    static boolean isFragmentQName(QName name) {
        return name.equals(_openuriFragment) || name.equals(_xmlFragment);
    }

    static boolean isFragment(Cur start, Cur end) {
        int k;
        assert (!end.isAttr());
        start.push();
        end.push();
        int numDocElems = 0;
        boolean isFrag = false;
        while (!start.isSamePos(end) && (k = start.kind()) != 3) {
            if (k == 0 && !Locale.isWhiteSpace(start.getCharsAsString())) {
                isFrag = true;
                break;
            }
            if (k == 2 && ++numDocElems > 1) {
                isFrag = true;
                break;
            }
            if (k != 0) {
                start.toEnd();
            }
            start.next();
        }
        start.pop();
        end.pop();
        return isFrag || numDocElems != 1;
    }

    public static XmlObject newInstance(SchemaTypeLoader stl, SchemaType type, XmlOptions options) {
        try {
            return Locale.syncWrap(stl, options, l -> {
                Cur c = l.tempCur();
                SchemaType sType = XmlOptions.maskNull(options).getDocumentType();
                if (sType == null) {
                    SchemaType schemaType = sType = type == null ? XmlObject.type : type;
                }
                if (sType.isDocumentType()) {
                    c.createDomDocumentRoot();
                } else {
                    c.createRoot();
                }
                c.setType(sType);
                XmlObject x = (XmlObject)((Object)c.getUser());
                c.release();
                return x;
            });
        }
        catch (IOException | XmlException e) {
            assert (false) : "newInstance doesn't throw XmlException or IOException";
            throw new RuntimeException(e);
        }
    }

    public static DOMImplementation newDomImplementation(SchemaTypeLoader stl, XmlOptions options) {
        return Locale.getLocale(stl, options);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static <T> T syncWrap(SchemaTypeLoader stl, XmlOptions options, SyncWrapFun<T> fun) throws XmlException, IOException {
        Locale l = Locale.getLocale(stl, options);
        if (l.noSync()) {
            l.enter();
            try {
                T t = fun.parse(l);
                return t;
            }
            finally {
                l.exit();
            }
        }
        Locale locale = l;
        synchronized (locale) {
            l.enter();
            T t = fun.parse(l);
            return t;
            finally {
                l.exit();
            }
        }
    }

    public static XmlObject parseToXmlObject(SchemaTypeLoader stl, String xmlText, SchemaType type, XmlOptions options) throws XmlException {
        try {
            return Locale.syncWrap(stl, options, l -> {
                try (StringReader r = new StringReader(xmlText);){
                    Cur c = Locale.getSaxLoader(options).load(l, new InputSource(r), options);
                    Locale.autoTypeDocument(c, type, options);
                    XmlObject x = (XmlObject)((Object)c.getUser());
                    c.release();
                    XmlObject xmlObject = x;
                    return xmlObject;
                }
            });
        }
        catch (IOException e) {
            assert (false) : "StringReader should not throw IOException";
            throw new XmlException(e.getMessage(), e);
        }
    }

    public static XmlObject parseToXmlObject(SchemaTypeLoader stl, XMLStreamReader xsr, SchemaType type, XmlOptions options) throws XmlException {
        try {
            return Locale.syncWrap(stl, options, l -> {
                Cur c;
                try {
                    c = l.loadXMLStreamReader(xsr, options);
                }
                catch (XMLStreamException e) {
                    throw new XmlException(e.getMessage(), e);
                }
                Locale.autoTypeDocument(c, type, options);
                XmlObject x = (XmlObject)((Object)c.getUser());
                c.release();
                return x;
            });
        }
        catch (IOException e) {
            assert (false) : "doesn't throw IOException";
            throw new RuntimeException(e);
        }
    }

    private static void lineNumber(XMLStreamReader xsr, LoadContext context) {
        Location loc = xsr.getLocation();
        if (loc != null) {
            context.lineNumber(loc.getLineNumber(), loc.getColumnNumber(), loc.getCharacterOffset());
        }
    }

    private void doAttributes(XMLStreamReader xsr, LoadContext context) {
        int n = xsr.getAttributeCount();
        for (int a = 0; a < n; ++a) {
            context.attr(xsr.getAttributeLocalName(a), xsr.getAttributeNamespace(a), xsr.getAttributePrefix(a), xsr.getAttributeValue(a));
        }
    }

    private void doNamespaces(XMLStreamReader xsr, LoadContext context) {
        int n = xsr.getNamespaceCount();
        for (int a = 0; a < n; ++a) {
            String prefix = xsr.getNamespacePrefix(a);
            if (prefix == null || prefix.length() == 0) {
                context.attr("xmlns", _xmlnsUri, null, xsr.getNamespaceURI(a));
                continue;
            }
            context.attr(prefix, _xmlnsUri, "xmlns", xsr.getNamespaceURI(a));
        }
    }

    private Cur loadXMLStreamReader(XMLStreamReader xsr, XmlOptions options) throws XMLStreamException {
        options = XmlOptions.maskNull(options);
        boolean lineNums = options.isLoadLineNumbers();
        String encoding = null;
        String version = null;
        boolean standAlone = false;
        Cur.CurLoadContext context = new Cur.CurLoadContext(this, options);
        int depth = 0;
        int eventType = xsr.getEventType();
        block13: while (true) {
            switch (eventType) {
                case 7: {
                    ++depth;
                    encoding = xsr.getCharacterEncodingScheme();
                    version = xsr.getVersion();
                    standAlone = xsr.isStandalone();
                    if (!lineNums) break;
                    Locale.lineNumber(xsr, context);
                    break;
                }
                case 8: {
                    --depth;
                    if (!lineNums) break block13;
                    Locale.lineNumber(xsr, context);
                    break block13;
                }
                case 1: {
                    ++depth;
                    ((LoadContext)context).startElement(xsr.getName());
                    if (lineNums) {
                        Locale.lineNumber(xsr, context);
                    }
                    this.doAttributes(xsr, context);
                    this.doNamespaces(xsr, context);
                    break;
                }
                case 2: {
                    --depth;
                    ((LoadContext)context).endElement();
                    if (!lineNums) break;
                    Locale.lineNumber(xsr, context);
                    break;
                }
                case 4: 
                case 12: {
                    ((LoadContext)context).text(xsr.getTextCharacters(), xsr.getTextStart(), xsr.getTextLength());
                    if (!lineNums) break;
                    Locale.lineNumber(xsr, context);
                    break;
                }
                case 5: {
                    String comment = xsr.getText();
                    ((LoadContext)context).comment(comment);
                    if (!lineNums) break;
                    Locale.lineNumber(xsr, context);
                    break;
                }
                case 3: {
                    ((LoadContext)context).procInst(xsr.getPITarget(), xsr.getPIData());
                    if (!lineNums) break;
                    Locale.lineNumber(xsr, context);
                    break;
                }
                case 10: {
                    this.doAttributes(xsr, context);
                    break;
                }
                case 13: {
                    this.doNamespaces(xsr, context);
                    break;
                }
                case 9: {
                    ((LoadContext)context).text(xsr.getText());
                    break;
                }
                case 6: 
                case 11: {
                    break;
                }
                default: {
                    throw new RuntimeException("Unhandled xml event type: " + eventType);
                }
            }
            if (!xsr.hasNext() || depth <= 0) break;
            eventType = xsr.next();
        }
        Cur c = ((LoadContext)context).finish();
        Locale.associateSourceName(c, options);
        XmlDocumentProperties props = Locale.getDocProps(c, true);
        props.setEncoding(encoding);
        props.setVersion(version);
        props.setStandalone(standAlone);
        return c;
    }

    public static XmlObject parseToXmlObject(SchemaTypeLoader stl, InputStream is, SchemaType type, XmlOptions options) throws XmlException, IOException {
        return Locale.syncWrap(stl, options, l -> {
            Cur c = Locale.getSaxLoader(options).load(l, new InputSource(is), options);
            Locale.autoTypeDocument(c, type, options);
            XmlObject x = (XmlObject)((Object)c.getUser());
            c.release();
            return x;
        });
    }

    public static XmlObject parseToXmlObject(SchemaTypeLoader stl, Reader reader, SchemaType type, XmlOptions options) throws XmlException, IOException {
        return Locale.syncWrap(stl, options, l -> {
            Cur c = Locale.getSaxLoader(options).load(l, new InputSource(reader), options);
            Locale.autoTypeDocument(c, type, options);
            XmlObject x = (XmlObject)((Object)c.getUser());
            c.release();
            return x;
        });
    }

    public static XmlObject parseToXmlObject(SchemaTypeLoader stl, Node node, SchemaType type, XmlOptions options) throws XmlException {
        try {
            return Locale.syncWrap(stl, options, l -> {
                Cur.CurLoadContext context = new Cur.CurLoadContext(l, options);
                l.loadNode(node, context);
                Cur c = ((LoadContext)context).finish();
                Locale.associateSourceName(c, options);
                Locale.autoTypeDocument(c, type, options);
                XmlObject x = (XmlObject)((Object)c.getUser());
                c.release();
                return x;
            });
        }
        catch (IOException e) {
            assert (false) : "Doesn't throw IOException";
            throw new RuntimeException(e);
        }
    }

    private void loadNodeChildren(Node n, LoadContext context) {
        for (Node c = n.getFirstChild(); c != null; c = c.getNextSibling()) {
            this.loadNode(c, context);
        }
    }

    public void loadNode(Node n, LoadContext context) {
        switch (n.getNodeType()) {
            case 5: 
            case 9: 
            case 11: {
                this.loadNodeChildren(n, context);
                break;
            }
            case 1: {
                context.startElement(this.makeQualifiedQName(n.getNamespaceURI(), n.getNodeName()));
                NamedNodeMap attrs = n.getAttributes();
                for (int i = 0; i < attrs.getLength(); ++i) {
                    Node a = attrs.item(i);
                    String attrName = a.getNodeName();
                    String attrValue = a.getNodeValue();
                    if (attrName.toLowerCase(java.util.Locale.ROOT).startsWith("xmlns")) {
                        if (attrName.length() == 5) {
                            context.xmlns(null, attrValue);
                            continue;
                        }
                        context.xmlns(attrName.substring(6), attrValue);
                        continue;
                    }
                    context.attr(this.makeQualifiedQName(a.getNamespaceURI(), attrName), attrValue);
                }
                this.loadNodeChildren(n, context);
                context.endElement();
                break;
            }
            case 3: 
            case 4: {
                context.text(n.getNodeValue());
                break;
            }
            case 8: {
                context.comment(n.getNodeValue());
                break;
            }
            case 7: {
                context.procInst(n.getNodeName(), n.getNodeValue());
                break;
            }
            case 6: 
            case 10: 
            case 12: {
                Node next = n.getNextSibling();
                if (next == null) break;
                this.loadNode(next, context);
                break;
            }
            case 2: {
                throw new RuntimeException("Unexpected node");
            }
        }
    }

    public static XmlSaxHandler newSaxHandler(SchemaTypeLoader stl, SchemaType type, XmlOptions options) {
        try {
            return Locale.syncWrap(stl, options, l -> new XmlSaxHandlerImpl(l, type, options));
        }
        catch (IOException | XmlException e) {
            assert (false) : "XmlException or IOException is not thrown";
            throw new RuntimeException(e);
        }
    }

    QName makeQName(String uri, String localPart) {
        assert (localPart != null && localPart.length() > 0);
        return this._qnameFactory.getQName(uri, localPart);
    }

    QName makeQNameNoCheck(String uri, String localPart) {
        return this._qnameFactory.getQName(uri, localPart);
    }

    QName makeQName(String uri, String local, String prefix) {
        return this._qnameFactory.getQName(uri, local, prefix == null ? "" : prefix);
    }

    QName makeQualifiedQName(String uri, String qname) {
        int i;
        if (qname == null) {
            qname = "";
        }
        return (i = qname.indexOf(58)) < 0 ? this._qnameFactory.getQName(uri, qname) : this._qnameFactory.getQName(uri, qname.substring(i + 1), qname.substring(0, i));
    }

    static XmlDocumentProperties getDocProps(Cur c, boolean ensure) {
        c.push();
        while (c.toParent()) {
        }
        DocProps props = (DocProps)c.getBookmark(DocProps.class);
        if (props == null && ensure) {
            props = new DocProps();
            c.setBookmark(DocProps.class, props);
        }
        c.pop();
        return props;
    }

    void registerForChange(ChangeListener listener) {
        if (listener.getNextChangeListener() == null) {
            if (this._changeListeners == null) {
                listener.setNextChangeListener(listener);
            } else {
                listener.setNextChangeListener(this._changeListeners);
            }
            this._changeListeners = listener;
        }
    }

    void notifyChange() {
        while (this._changeListeners != null) {
            this._changeListeners.notifyChange();
            if (this._changeListeners.getNextChangeListener() == this._changeListeners) {
                this._changeListeners.setNextChangeListener(null);
            }
            ChangeListener next = this._changeListeners.getNextChangeListener();
            this._changeListeners.setNextChangeListener(null);
            this._changeListeners = next;
        }
        this._locations.notifyChange();
    }

    static String getTextValue(Cur c) {
        assert (c.isNode());
        if (!c.hasChildren()) {
            return c.getValueAsString();
        }
        StringBuffer sb = new StringBuffer();
        c.push();
        c.next();
        while (!c.isAtEndOfLastPush()) {
            if (c.isText() && (!c._xobj.isComment() && !c._xobj.isProcinst() || c._pos >= c._xobj._cchValue)) {
                CharUtil.getString(sb, c.getChars(-1), c._offSrc, c._cchSrc);
            }
            c.next();
        }
        c.pop();
        return sb.toString();
    }

    static int getTextValue(Cur c, char[] chars, int off, int maxCch) {
        assert (c.isNode());
        String s = c._xobj.getValueAsString(1);
        int n = s.length();
        if (n > maxCch) {
            n = maxCch;
        }
        if (n <= 0) {
            return 0;
        }
        s.getChars(0, n, chars, off);
        return n;
    }

    static String applyWhiteSpaceRule(String s, int wsr) {
        block6: {
            int l;
            block5: {
                int n = l = s == null ? 0 : s.length();
                if (l == 0 || wsr == 1) {
                    return s;
                }
                if (wsr != 2) break block5;
                for (int i = 0; i < l; ++i) {
                    char ch = s.charAt(i);
                    if (ch != '\n' && ch != '\r' && ch != '\t') continue;
                    return Locale.processWhiteSpaceRule(s, wsr);
                }
                break block6;
            }
            if (wsr != 3) break block6;
            if (CharUtil.isWhiteSpace(s.charAt(0)) || CharUtil.isWhiteSpace(s.charAt(l - 1))) {
                return Locale.processWhiteSpaceRule(s, wsr);
            }
            boolean lastWasWhite = false;
            for (int i = 1; i < l; ++i) {
                boolean isWhite = CharUtil.isWhiteSpace(s.charAt(i));
                if (isWhite && lastWasWhite) {
                    return Locale.processWhiteSpaceRule(s, wsr);
                }
                lastWasWhite = isWhite;
            }
        }
        return s;
    }

    static String processWhiteSpaceRule(String s, int wsr) {
        ScrubBuffer sb = Locale.getScrubBuffer(wsr);
        sb.scrub(s, 0, s.length());
        return sb.getResultAsString();
    }

    public static void clearThreadLocals() {
        tl_scrubBuffer.remove();
    }

    static ScrubBuffer getScrubBuffer(int wsr) {
        SoftReference<ScrubBuffer> softRef = tl_scrubBuffer.get();
        ScrubBuffer scrubBuffer = softRef.get();
        if (scrubBuffer == null) {
            scrubBuffer = new ScrubBuffer();
            tl_scrubBuffer.set(new SoftReference<ScrubBuffer>(scrubBuffer));
        }
        scrubBuffer.init(wsr);
        return scrubBuffer;
    }

    static boolean pushToContainer(Cur c) {
        c.push();
        block5: while (true) {
            switch (c.kind()) {
                case 1: 
                case 2: {
                    return true;
                }
                case -2: 
                case -1: {
                    c.pop();
                    return false;
                }
                case 4: 
                case 5: {
                    c.skip();
                    continue block5;
                }
            }
            c.nextWithAttrs();
        }
    }

    static boolean toFirstNormalAttr(Cur c) {
        c.push();
        if (c.toFirstAttr()) {
            do {
                if (c.isXmlns()) continue;
                c.popButStay();
                return true;
            } while (c.toNextAttr());
        }
        c.pop();
        return false;
    }

    static boolean toPrevNormalAttr(Cur c) {
        if (c.isAttr()) {
            block3: {
                c.push();
                do {
                    assert (c.isAttr());
                    if (!c.prev()) break block3;
                    c.prev();
                    if (c.isAttr()) continue;
                    c.prev();
                } while (!c.isNormalAttr());
                c.popButStay();
                return true;
            }
            c.pop();
        }
        return false;
    }

    static boolean toNextNormalAttr(Cur c) {
        c.push();
        while (c.toNextAttr()) {
            if (c.isXmlns()) continue;
            c.popButStay();
            return true;
        }
        c.pop();
        return false;
    }

    Xobj findNthChildElem(Xobj parent, QName name, QNameSet set, int n) {
        int db;
        Xobj x;
        assert (name == null || set == null);
        assert (n >= 0);
        if (parent == null) {
            return null;
        }
        int da = this._nthCache_A.distance(parent, name, set, n);
        Xobj xobj = x = da <= (db = this._nthCache_B.distance(parent, name, set, n)) ? this._nthCache_A.fetch(parent, name, set, n) : this._nthCache_B.fetch(parent, name, set, n);
        if (da == db) {
            nthCache temp = this._nthCache_A;
            this._nthCache_A = this._nthCache_B;
            this._nthCache_B = temp;
        }
        return x;
    }

    int count(Xobj parent, QName name, QNameSet set) {
        int n = 0;
        Xobj x = this.findNthChildElem(parent, name, set, 0);
        while (x != null) {
            if (x.isElem()) {
                if (set == null) {
                    if (x._name.equals(name)) {
                        ++n;
                    }
                } else if (set.contains(x._name)) {
                    ++n;
                }
            }
            x = x._nextSibling;
        }
        return n;
    }

    static boolean toChild(Cur c, QName name, int n) {
        if (n >= 0 && Locale.pushToContainer(c)) {
            Xobj x = c._locale.findNthChildElem(c._xobj, name, null, n);
            c.pop();
            if (x != null) {
                c.moveTo(x);
                return true;
            }
        }
        return false;
    }

    public static boolean toFirstChildElement(Cur c) {
        Xobj originalXobj = c._xobj;
        int originalPos = c._pos;
        block5: while (true) {
            switch (c.kind()) {
                case 1: 
                case 2: {
                    break block5;
                }
                case -2: 
                case -1: {
                    c.moveTo(originalXobj, originalPos);
                    return false;
                }
                case 4: 
                case 5: {
                    c.skip();
                    continue block5;
                }
                default: {
                    c.nextWithAttrs();
                    continue block5;
                }
            }
            break;
        }
        if (!c.toFirstChild() || !c.isElem() && !Locale.toNextSiblingElement(c)) {
            c.moveTo(originalXobj, originalPos);
            return false;
        }
        return true;
    }

    static boolean toLastChildElement(Cur c) {
        if (!Locale.pushToContainer(c)) {
            return false;
        }
        if (!c.toLastChild() || !c.isElem() && !Locale.toPrevSiblingElement(c)) {
            c.pop();
            return false;
        }
        c.popButStay();
        return true;
    }

    static boolean toPrevSiblingElement(Cur cur) {
        if (!cur.hasParent()) {
            return false;
        }
        Cur c = cur.tempCur();
        boolean moved = false;
        int k = c.kind();
        if (k != 3) {
            while (c.prev() && (k = c.kind()) != 1 && k != 2) {
                if (c.kind() != -2) continue;
                c.toParent();
                cur.moveToCur(c);
                moved = true;
                break;
            }
        }
        c.release();
        return moved;
    }

    static boolean toNextSiblingElement(Cur c) {
        if (!c.hasParent()) {
            return false;
        }
        c.push();
        int k = c.kind();
        if (k == 3) {
            c.toParent();
            c.next();
        } else if (k == 2) {
            c.skip();
        }
        while ((k = c.kind()) >= 0) {
            if (k == 2) {
                c.popButStay();
                return true;
            }
            if (k > 0) {
                c.toEnd();
            }
            c.next();
        }
        c.pop();
        return false;
    }

    static boolean toNextSiblingElement(Cur c, Xobj parent) {
        Xobj originalXobj = c._xobj;
        int originalPos = c._pos;
        int k = c.kind();
        if (k == 3) {
            c.moveTo(parent);
            c.next();
        } else if (k == 2) {
            c.skip();
        }
        while ((k = c.kind()) >= 0) {
            if (k == 2) {
                return true;
            }
            if (k > 0) {
                c.toEnd();
            }
            c.next();
        }
        c.moveTo(originalXobj, originalPos);
        return false;
    }

    static void applyNamespaces(Cur c, Map<String, String> namespaces) {
        assert (c.isContainer());
        for (String prefix : namespaces.keySet()) {
            if (prefix.toLowerCase(java.util.Locale.ROOT).startsWith("xml") || c.namespaceForPrefix(prefix, false) != null) continue;
            c.push();
            c.next();
            c.createAttr(c._locale.createXmlns(prefix));
            c.next();
            c.insertString(namespaces.get(prefix));
            c.pop();
        }
    }

    static Map<String, String> getAllNamespaces(Cur c, Map<String, String> filleMe) {
        assert (c.isNode());
        c.push();
        if (!c.isContainer()) {
            c.toParent();
        }
        assert (c.isContainer());
        while (true) {
            if (c.toNextAttr()) {
                if (!c.isXmlns()) continue;
                String prefix = c.getXmlnsPrefix();
                String uri = c.getXmlnsUri();
                if (filleMe == null) {
                    filleMe = new HashMap<String, String>();
                }
                if (filleMe.containsKey(prefix)) continue;
                filleMe.put(prefix, uri);
                continue;
            }
            if (!c.isContainer()) {
                c.toParentRaw();
            }
            if (!c.toParentRaw()) break;
        }
        c.pop();
        return filleMe;
    }

    DomImpl.Dom findDomNthChild(DomImpl.Dom parent, int n) {
        DomImpl.Dom x;
        boolean aInvalidate;
        assert (n >= 0);
        if (parent == null) {
            return null;
        }
        int da = this._domNthCache_A.distance(parent, n);
        int db = this._domNthCache_B.distance(parent, n);
        boolean bInvalidate = db - this._domNthCache_B._len / 2 > 0 && db - this._domNthCache_B._len / 2 - 40 > 0;
        boolean bl = aInvalidate = da - this._domNthCache_A._len / 2 > 0 && da - this._domNthCache_A._len / 2 - 40 > 0;
        if (da <= db) {
            if (!aInvalidate) {
                x = this._domNthCache_A.fetch(parent, n);
            } else {
                this._domNthCache_B._version = -1L;
                x = this._domNthCache_B.fetch(parent, n);
            }
        } else if (!bInvalidate) {
            x = this._domNthCache_B.fetch(parent, n);
        } else {
            this._domNthCache_A._version = -1L;
            x = this._domNthCache_A.fetch(parent, n);
        }
        if (da == db) {
            domNthCache temp = this._domNthCache_A;
            this._domNthCache_A = this._domNthCache_B;
            this._domNthCache_B = temp;
        }
        return x;
    }

    int domLength(DomImpl.Dom parent) {
        int db;
        int len;
        if (parent == null) {
            return 0;
        }
        int da = this._domNthCache_A.distance(parent, 0);
        int n = len = da <= (db = this._domNthCache_B.distance(parent, 0)) ? this._domNthCache_A.length(parent) : this._domNthCache_B.length(parent);
        if (da == db) {
            domNthCache temp = this._domNthCache_A;
            this._domNthCache_A = this._domNthCache_B;
            this._domNthCache_B = temp;
        }
        return len;
    }

    void invalidateDomCaches(DomImpl.Dom d) {
        if (this._domNthCache_A._parent == d) {
            this._domNthCache_A._version = -1L;
        }
        if (this._domNthCache_B._parent == d) {
            this._domNthCache_B._version = -1L;
        }
    }

    CharUtil getCharUtil() {
        if (this._charUtil == null) {
            this._charUtil = new CharUtil(1024);
        }
        return this._charUtil;
    }

    public long version() {
        return this._versionAll;
    }

    Cur weakCur(Object o) {
        assert (o != null && !(o instanceof Ref));
        Cur c = this.getCur();
        assert (c._tempFrame == -1);
        assert (c._ref == null);
        c._ref = new Ref(c, o);
        return c;
    }

    final ReferenceQueue<Ref> refQueue() {
        if (this._refQueue == null) {
            this._refQueue = new ReferenceQueue();
        }
        return this._refQueue;
    }

    Cur tempCur() {
        return this.tempCur(null);
    }

    Cur tempCur(String id) {
        Cur next;
        Cur c = this.getCur();
        assert (c._tempFrame == -1);
        assert (this._numTempFramesLeft < this._tempFrames.length) : "Temp frame not pushed";
        int frame = this._tempFrames.length - this._numTempFramesLeft - 1;
        assert (frame >= 0 && frame < this._tempFrames.length);
        c._nextTemp = next = this._tempFrames[frame];
        assert (c._prevTemp == null);
        if (next != null) {
            assert (next._prevTemp == null);
            next._prevTemp = c;
        }
        this._tempFrames[frame] = c;
        c._tempFrame = frame;
        c._id = id;
        return c;
    }

    Cur getCur() {
        Cur c;
        assert (this._curPool == null || this._curPoolCount > 0);
        if (this._curPool == null) {
            c = new Cur(this);
        } else {
            c = this._curPool;
            this._curPool = this._curPool.listRemove(c);
            --this._curPoolCount;
        }
        assert (c._state == 0);
        assert (c._prev == null && c._next == null);
        assert (c._xobj == null && c._pos == -2);
        assert (c._ref == null);
        this._registered = c.listInsert(this._registered);
        c._state = 1;
        return c;
    }

    void embedCurs() {
        Cur c;
        while ((c = this._registered) != null) {
            assert (c._xobj != null);
            this._registered = c.listRemove(this._registered);
            c._xobj._embedded = c.listInsert(c._xobj._embedded);
            c._state = 2;
        }
    }

    TextNode createTextNode() {
        return this._saaj == null ? new TextNode(this) : new SaajTextNode(this);
    }

    CdataNode createCdataNode() {
        return this._saaj == null ? new CdataNode(this) : new SaajCdataNode(this);
    }

    boolean entered() {
        return this._tempFrames.length - this._numTempFramesLeft > 0;
    }

    public void enter(Locale otherLocale) {
        this.enter();
        if (otherLocale != this) {
            otherLocale.enter();
        }
    }

    @Override
    public void enter() {
        assert (this._numTempFramesLeft >= 0);
        if (--this._numTempFramesLeft <= 0) {
            Cur[] newTempFrames = new Cur[this._tempFrames.length * 2];
            this._numTempFramesLeft = this._tempFrames.length;
            System.arraycopy(this._tempFrames, 0, newTempFrames, 0, this._tempFrames.length);
            this._tempFrames = newTempFrames;
        }
        if (++this._entryCount > 1000) {
            this.pollQueue();
            this._entryCount = 0;
        }
    }

    private void pollQueue() {
        if (this._refQueue != null) {
            Ref ref;
            while ((ref = (Ref)this._refQueue.poll()) != null) {
                if (ref._cur == null) continue;
                ref._cur.release();
            }
        }
    }

    public void exit(Locale otherLocale) {
        this.exit();
        if (otherLocale != this) {
            otherLocale.exit();
        }
    }

    @Override
    public void exit() {
        assert (this._numTempFramesLeft >= 0 && this._numTempFramesLeft <= this._tempFrames.length - 1) : " Temp frames mismanaged. Impossible stack frame. Unsynchronized: " + this.noSync();
        int frame = this._tempFrames.length - ++this._numTempFramesLeft;
        while (this._tempFrames[frame] != null) {
            this._tempFrames[frame].release();
        }
    }

    @Override
    public boolean noSync() {
        return this._noSync;
    }

    @Override
    public boolean sync() {
        return !this._noSync;
    }

    static boolean isWhiteSpace(String s) {
        int l = s.length();
        while (l-- > 0) {
            if (CharUtil.isWhiteSpace(s.charAt(l))) continue;
            return false;
        }
        return true;
    }

    static boolean beginsWithXml(String name) {
        char ch;
        return !(name.length() < 3 || (ch = name.charAt(0)) != 'x' && ch != 'X' || (ch = name.charAt(1)) != 'm' && ch != 'M' || (ch = name.charAt(2)) != 'l' && ch != 'L');
    }

    static boolean isXmlns(QName name) {
        String prefix = name.getPrefix();
        if (prefix.equals("xmlns")) {
            return true;
        }
        return prefix.length() == 0 && name.getLocalPart().equals("xmlns");
    }

    QName createXmlns(String prefix) {
        if (prefix == null) {
            prefix = "";
        }
        return prefix.length() == 0 ? this.makeQName(_xmlnsUri, "xmlns", "") : this.makeQName(_xmlnsUri, prefix, "xmlns");
    }

    static String xmlnsPrefix(QName name) {
        return name.getPrefix().equals("xmlns") ? name.getLocalPart() : "";
    }

    private static SaxLoader getSaxLoader(XmlOptions options) throws XmlException {
        XMLReader xr;
        options = XmlOptions.maskNull(options);
        EntityResolver er = null;
        if (!options.isLoadUseDefaultResolver()) {
            er = options.getEntityResolver();
            if (er == null) {
                er = ResolverUtil.getGlobalEntityResolver();
            }
            if (er == null) {
                er = new DefaultEntityResolver();
            }
        }
        if ((xr = options.getLoadUseXMLReader()) == null) {
            try {
                xr = SAXHelper.newXMLReader(new XmlOptions(options));
            }
            catch (Exception e) {
                throw new XmlException("Problem creating XMLReader", e);
            }
        }
        XmlReaderSaxLoader sl = new XmlReaderSaxLoader(xr);
        if (er != null) {
            xr.setEntityResolver(er);
        }
        return sl;
    }

    private DomImpl.Dom load(InputSource is, XmlOptions options) throws XmlException, IOException {
        return Locale.getSaxLoader(options).load(this, is, options).getDom();
    }

    public DomImpl.Dom load(Reader r) throws XmlException, IOException {
        return this.load(r, null);
    }

    public DomImpl.Dom load(Reader r, XmlOptions options) throws XmlException, IOException {
        return this.load(new InputSource(r), options);
    }

    public DomImpl.Dom load(InputStream in) throws XmlException, IOException {
        return this.load(in, null);
    }

    public DomImpl.Dom load(InputStream in, XmlOptions options) throws XmlException, IOException {
        return this.load(new InputSource(in), options);
    }

    public DomImpl.Dom load(String s) throws XmlException {
        return this.load(s, null);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public DomImpl.Dom load(String s, XmlOptions options) throws XmlException {
        try (StringReader r = new StringReader(s);){
            DomImpl.Dom dom = this.load(r, options);
            return dom;
        }
        catch (IOException e) {
            if ($assertionsDisabled) throw new XmlException(e.getMessage(), e);
            throw new AssertionError((Object)"StringReader should not throw IOException");
        }
    }

    @Override
    public Document createDocument(String uri, String qname, DocumentType doctype) {
        return DomImpl._domImplementation_createDocument(this, uri, qname, doctype);
    }

    @Override
    public DocumentType createDocumentType(String qname, String publicId, String systemId) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean hasFeature(String feature, String version) {
        return DomImpl._domImplementation_hasFeature(this, feature, version);
    }

    @Override
    public Object getFeature(String feature, String version) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    private static DomImpl.Dom checkNode(Node n) {
        if (n == null) {
            throw new IllegalArgumentException("Node is null");
        }
        if (!(n instanceof DomImpl.Dom)) {
            throw new IllegalArgumentException("Node is not an XmlBeans node");
        }
        return (DomImpl.Dom)((Object)n);
    }

    public static XmlCursor nodeToCursor(Node n) {
        return DomImpl._getXmlCursor(Locale.checkNode(n));
    }

    public static XmlObject nodeToXmlObject(Node n) {
        return DomImpl._getXmlObject(Locale.checkNode(n));
    }

    public static XMLStreamReader nodeToXmlStream(Node n) {
        return DomImpl._getXmlStreamReader(Locale.checkNode(n));
    }

    public static Node streamToNode(XMLStreamReader xs) {
        return Jsr173.nodeFromStream(xs);
    }

    @Override
    public void setSaajData(Node n, Object o) {
        assert (n instanceof DomImpl.Dom);
        DomImpl.saajCallback_setSaajData((DomImpl.Dom)((Object)n), o);
    }

    @Override
    public Object getSaajData(Node n) {
        assert (n instanceof DomImpl.Dom);
        return DomImpl.saajCallback_getSaajData((DomImpl.Dom)((Object)n));
    }

    @Override
    public Element createSoapElement(QName name, QName parentName) {
        assert (this._ownerDoc != null);
        return DomImpl.saajCallback_createSoapElement(this._ownerDoc, name, parentName);
    }

    @Override
    public Element importSoapElement(Document doc, Element elem, boolean deep, QName parentName) {
        assert (doc instanceof DomImpl.Dom);
        return DomImpl.saajCallback_importSoapElement((DomImpl.Dom)((Object)doc), elem, deep, parentName);
    }

    public SchemaTypeLoader getSchemaTypeLoader() {
        return this._schemaTypeLoader;
    }

    private static final class DefaultQNameFactory
    implements QNameFactory {
        private final QNameCache _cache = XmlBeans.getQNameCache();

        private DefaultQNameFactory() {
        }

        @Override
        public QName getQName(String uri, String local) {
            return this._cache.getName(uri, local, "");
        }

        @Override
        public QName getQName(String uri, String local, String prefix) {
            return this._cache.getName(uri, local, prefix);
        }

        @Override
        public QName getQName(char[] uriSrc, int uriPos, int uriCch, char[] localSrc, int localPos, int localCch) {
            return this._cache.getName(new String(uriSrc, uriPos, uriCch), new String(localSrc, localPos, localCch), "");
        }

        @Override
        public QName getQName(char[] uriSrc, int uriPos, int uriCch, char[] localSrc, int localPos, int localCch, char[] prefixSrc, int prefixPos, int prefixCch) {
            return this._cache.getName(new String(uriSrc, uriPos, uriCch), new String(localSrc, localPos, localCch), new String(prefixSrc, prefixPos, prefixCch));
        }
    }

    private static abstract class SaxLoader
    extends SaxHandler
    implements ErrorHandler {
        private final XMLReader _xr;

        SaxLoader(XMLReader xr, Locator startLocator) {
            super(startLocator);
            this._xr = xr;
            try {
                this._xr.setFeature("http://xml.org/sax/features/namespaces", true);
                this._xr.setFeature("http://xml.org/sax/features/validation", false);
                this._xr.setProperty("http://xml.org/sax/properties/lexical-handler", this);
                this._xr.setContentHandler(this);
                this._xr.setDTDHandler(this);
                this._xr.setErrorHandler(this);
            }
            catch (Throwable e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            try {
                this._xr.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
            }
            catch (Throwable e) {
                LOG.atWarn().withThrowable(e).log("Secure Processing Feature is not supported");
            }
            try {
                this._xr.setProperty("http://xml.org/sax/properties/declaration-handler", this);
            }
            catch (Throwable e) {
                LOG.atWarn().withThrowable(e).log("SAX Declaration Handler is not supported");
            }
        }

        void postLoad(Cur c) {
            this._locale = null;
            this._context = null;
        }

        public Cur load(Locale l, InputSource is, XmlOptions options) throws XmlException, IOException {
            is.setSystemId("file://");
            this.initSaxHandler(l, options);
            try {
                this._xr.parse(is);
                Cur c = this._context.finish();
                Locale.associateSourceName(c, options);
                this.postLoad(c);
                return c;
            }
            catch (XmlRuntimeException e) {
                this._context.abort();
                throw new XmlException(e);
            }
            catch (SAXParseException e) {
                this._context.abort();
                XmlError err = XmlError.forLocation(e.getMessage(), options == null ? null : options.getDocumentSourceName(), e.getLineNumber(), e.getColumnNumber(), -1);
                throw new XmlException(err.toString(), (Throwable)e, err);
            }
            catch (SAXException e) {
                this._context.abort();
                XmlError err = XmlError.forMessage(e.getMessage());
                throw new XmlException(err.toString(), (Throwable)e, err);
            }
            catch (RuntimeException e) {
                this._context.abort();
                throw e;
            }
        }

        @Override
        public void fatalError(SAXParseException e) throws SAXException {
            throw e;
        }

        @Override
        public void error(SAXParseException e) throws SAXException {
            throw e;
        }

        @Override
        public void warning(SAXParseException e) throws SAXException {
            throw e;
        }
    }

    private static abstract class SaxHandler
    implements ContentHandler,
    LexicalHandler,
    DeclHandler,
    DTDHandler {
        protected Locale _locale;
        protected LoadContext _context;
        private boolean _wantLineNumbers;
        private boolean _wantLineNumbersAtEndElt;
        private boolean _wantCdataBookmarks;
        private Locator _startLocator;
        private boolean _insideCDATA = false;
        private int _entityBytesLimit = 10240;
        private int _entityBytes = 0;
        private int _insideEntity = 0;
        private Map<String, String> delayedPrefixMappings = new LinkedHashMap<String, String>();

        SaxHandler(Locator startLocator) {
            this._startLocator = startLocator;
        }

        void initSaxHandler(Locale l, XmlOptions options) {
            this._locale = l;
            XmlOptions safeOptions = XmlOptions.maskNull(options);
            this._context = new Cur.CurLoadContext(this._locale, safeOptions);
            this._wantLineNumbers = safeOptions.isLoadLineNumbers();
            this._wantLineNumbersAtEndElt = safeOptions.isLoadLineNumbersEndElement();
            this._wantCdataBookmarks = safeOptions.isUseCDataBookmarks();
            Integer limit = safeOptions.getLoadEntityBytesLimit();
            if (limit != null) {
                this._entityBytesLimit = limit;
            }
        }

        @Override
        public void startDocument() throws SAXException {
        }

        @Override
        public void endDocument() throws SAXException {
        }

        @Override
        public void startElement(String uri, String localIgnored, String qName, Attributes atts) throws SAXException {
            if (qName.indexOf(58) >= 0 && uri.length() == 0) {
                XmlError err = XmlError.forMessage("Use of undefined namespace prefix: " + qName.substring(0, qName.indexOf(58)));
                throw new XmlRuntimeException(err.toString(), null, err);
            }
            this._context.startElement(this._locale.makeQualifiedQName(uri, qName));
            if (this._wantLineNumbers && this._startLocator != null) {
                this._context.bookmark(new XmlLineNumber(this._startLocator.getLineNumber(), this._startLocator.getColumnNumber() - 1, -1));
            }
            for (Map.Entry<String, String> nsEntry : this.delayedPrefixMappings.entrySet()) {
                this._context.xmlns(nsEntry.getKey(), nsEntry.getValue());
            }
            this.delayedPrefixMappings.clear();
            int len = atts.getLength();
            for (int i = 0; i < len; ++i) {
                String aqn = atts.getQName(i);
                int colon = aqn.indexOf(58);
                if (colon < 0) {
                    this._context.attr(aqn, atts.getURI(i), null, atts.getValue(i));
                    continue;
                }
                this._context.attr(aqn.substring(colon + 1), atts.getURI(i), aqn.substring(0, colon), atts.getValue(i));
            }
        }

        @Override
        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
            this._context.endElement();
            if (this._wantLineNumbersAtEndElt && this._startLocator != null) {
                this._context.bookmark(new XmlLineNumber(this._startLocator.getLineNumber(), this._startLocator.getColumnNumber() - 1, -1));
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            this._context.text(ch, start, length);
            if (this._wantCdataBookmarks && this._insideCDATA && this._startLocator != null) {
                this._context.bookmarkLastNonAttr(CDataBookmark.CDATA_BOOKMARK);
            }
            if (this._insideEntity != 0 && (this._entityBytes += length) > this._entityBytesLimit) {
                XmlError err = XmlError.forMessage("exceeded-entity-bytes", new Integer[]{this._entityBytesLimit});
                throw new SAXException(err.getMessage());
            }
        }

        @Override
        public void ignorableWhitespace(char[] ch, int start, int length) {
        }

        @Override
        public void comment(char[] ch, int start, int length) throws SAXException {
            this._context.comment(ch, start, length);
        }

        @Override
        public void processingInstruction(String target, String data) throws SAXException {
            this._context.procInst(target, data);
        }

        @Override
        public void startDTD(String name, String publicId, String systemId) throws SAXException {
            this._context.startDTD(name, publicId, systemId);
        }

        @Override
        public void endDTD() {
            this._context.endDTD();
        }

        @Override
        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            if (!(!Locale.beginsWithXml(prefix) || "xml".equals(prefix) && Locale._xml1998Uri.equals(uri))) {
                XmlError err = XmlError.forMessage("Prefix can't begin with XML: " + prefix, 0);
                throw new XmlRuntimeException(err.toString(), null, err);
            }
            this.delayedPrefixMappings.put(prefix, uri);
        }

        @Override
        public void endPrefixMapping(String prefix) throws SAXException {
        }

        @Override
        public void skippedEntity(String name) {
        }

        @Override
        public void startCDATA() {
            this._insideCDATA = true;
        }

        @Override
        public void endCDATA() {
            this._insideCDATA = false;
        }

        @Override
        public void startEntity(String name) throws SAXException {
            ++this._insideEntity;
        }

        @Override
        public void endEntity(String name) throws SAXException {
            --this._insideEntity;
            assert (this._insideEntity >= 0);
            if (this._insideEntity == 0) {
                this._entityBytes = 0;
            }
        }

        @Override
        public void setDocumentLocator(Locator locator) {
            if (this._startLocator == null) {
                this._startLocator = locator;
            }
        }

        @Override
        public void attributeDecl(String eName, String aName, String type, String valueDefault, String value) {
            if (type.equals("ID")) {
                this._context.addIdAttr(eName, aName);
            }
        }

        @Override
        public void elementDecl(String name, String model) {
        }

        @Override
        public void externalEntityDecl(String name, String publicId, String systemId) {
        }

        @Override
        public void internalEntityDecl(String name, String value) {
        }

        @Override
        public void notationDecl(String name, String publicId, String systemId) {
        }

        @Override
        public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) {
        }
    }

    private static class XmlReaderSaxLoader
    extends SaxLoader {
        XmlReaderSaxLoader(XMLReader xr) {
            super(xr, null);
        }
    }

    private static class DefaultEntityResolver
    implements EntityResolver {
        private DefaultEntityResolver() {
        }

        @Override
        public InputSource resolveEntity(String publicId, String systemId) {
            return new InputSource(new StringReader(""));
        }
    }

    public static abstract class LoadContext {
        private Hashtable<String, String> _idAttrs;

        protected abstract void startDTD(String var1, String var2, String var3);

        protected abstract void endDTD();

        protected abstract void startElement(QName var1);

        protected abstract void endElement();

        public abstract void attr(QName var1, String var2);

        protected abstract void attr(String var1, String var2, String var3, String var4);

        protected abstract void xmlns(String var1, String var2);

        protected abstract void comment(char[] var1, int var2, int var3);

        protected abstract void comment(String var1);

        protected abstract void procInst(String var1, String var2);

        protected abstract void text(char[] var1, int var2, int var3);

        protected abstract void text(String var1);

        public abstract Cur finish();

        protected abstract void abort();

        protected abstract void bookmark(XmlCursor.XmlBookmark var1);

        protected abstract void bookmarkLastNonAttr(XmlCursor.XmlBookmark var1);

        protected abstract void bookmarkLastAttr(QName var1, XmlCursor.XmlBookmark var2);

        protected abstract void lineNumber(int var1, int var2, int var3);

        protected void addIdAttr(String eName, String aName) {
            if (this._idAttrs == null) {
                this._idAttrs = new Hashtable();
            }
            this._idAttrs.put(aName, eName);
        }

        protected boolean isAttrOfTypeId(QName aqn, QName eqn) {
            if (this._idAttrs == null) {
                return "id".equalsIgnoreCase(aqn.getLocalPart());
            }
            String pre = aqn.getPrefix();
            String lName = aqn.getLocalPart();
            String urnName = "".equals(pre) ? lName : pre + ":" + lName;
            String eName = this._idAttrs.get(urnName);
            if (eName == null) {
                return false;
            }
            pre = eqn.getPrefix();
            lName = eqn.getLocalPart();
            urnName = "".equals(pre) ? lName : pre + ":" + lName;
            return eName.equals(urnName);
        }
    }

    static final class Ref
    extends PhantomReference {
        Cur _cur;

        Ref(Cur c, Object obj) {
            super(obj, c._locale.refQueue());
            this._cur = c;
        }
    }

    class domNthCache {
        public static final int BLITZ_BOUNDARY = 40;
        private long _version;
        private DomImpl.Dom _parent;
        private DomImpl.Dom _child;
        private int _n;
        private int _len;

        domNthCache() {
        }

        int distance(DomImpl.Dom parent, int n) {
            assert (n >= 0);
            if (this._version != Locale.this.version()) {
                return 0x7FFFFFFE;
            }
            if (parent != this._parent) {
                return Integer.MAX_VALUE;
            }
            return n > this._n ? n - this._n : this._n - n;
        }

        int length(DomImpl.Dom parent) {
            if (this._version != Locale.this.version() || this._parent != parent) {
                this._parent = parent;
                this._version = Locale.this.version();
                this._child = null;
                this._n = -1;
                this._len = -1;
            }
            if (this._len == -1) {
                DomImpl.Dom x;
                if (this._child != null && this._n != -1) {
                    x = this._child;
                    this._len = this._n;
                } else {
                    x = (DomImpl.Dom)((Object)DomImpl.firstChild(this._parent));
                    this._len = 0;
                    this._child = x;
                    this._n = 0;
                }
                while (x != null) {
                    ++this._len;
                    x = (DomImpl.Dom)((Object)DomImpl.nextSibling(x));
                }
            }
            return this._len;
        }

        DomImpl.Dom fetch(DomImpl.Dom parent, int n) {
            block10: {
                block9: {
                    assert (n >= 0);
                    if (this._version != Locale.this.version() || this._parent != parent) {
                        this._parent = parent;
                        this._version = Locale.this.version();
                        this._child = null;
                        this._n = -1;
                        this._len = -1;
                        DomImpl.Dom x = (DomImpl.Dom)((Object)DomImpl.firstChild(this._parent));
                        while (x != null) {
                            ++this._n;
                            if (this._child == null && n == this._n) {
                                this._child = x;
                                break;
                            }
                            x = (DomImpl.Dom)((Object)DomImpl.nextSibling(x));
                        }
                        return this._child;
                    }
                    if (this._n < 0) {
                        return null;
                    }
                    if (n <= this._n) break block9;
                    while (n > this._n) {
                        DomImpl.Dom x = (DomImpl.Dom)((Object)DomImpl.nextSibling(this._child));
                        if (x == null) {
                            return null;
                        }
                        this._child = x;
                        ++this._n;
                    }
                    break block10;
                }
                if (n >= this._n) break block10;
                while (n < this._n) {
                    DomImpl.Dom x = (DomImpl.Dom)((Object)DomImpl.prevSibling(this._child));
                    if (x == null) {
                        return null;
                    }
                    this._child = x;
                    --this._n;
                }
            }
            return this._child;
        }
    }

    class nthCache {
        private long _version;
        private Xobj _parent;
        private QName _name;
        private QNameSet _set;
        private Xobj _child;
        private int _n;

        nthCache() {
        }

        private boolean namesSame(QName pattern, QName name) {
            return pattern == null || pattern.equals(name);
        }

        private boolean setsSame(QNameSet patternSet, QNameSet set) {
            return patternSet != null && patternSet == set;
        }

        private boolean nameHit(QName namePattern, QNameSet setPattern, QName name) {
            return setPattern == null ? this.namesSame(namePattern, name) : setPattern.contains(name);
        }

        private boolean cacheSame(QName namePattern, QNameSet setPattern) {
            return setPattern == null ? this.namesSame(namePattern, this._name) : this.setsSame(setPattern, this._set);
        }

        int distance(Xobj parent, QName name, QNameSet set, int n) {
            assert (n >= 0);
            if (this._version != Locale.this.version()) {
                return 0x7FFFFFFE;
            }
            if (parent != this._parent || !this.cacheSame(name, set)) {
                return Integer.MAX_VALUE;
            }
            return n > this._n ? n - this._n : this._n - n;
        }

        Xobj fetch(Xobj parent, QName name, QNameSet set, int n) {
            block14: {
                Xobj x;
                block13: {
                    assert (n >= 0);
                    if (this._version != Locale.this.version() || this._parent != parent || !this.cacheSame(name, set) || n == 0) {
                        this._version = Locale.this.version();
                        this._parent = parent;
                        this._name = name;
                        this._child = null;
                        this._n = -1;
                        x = parent._firstChild;
                        while (x != null) {
                            if (x.isElem() && this.nameHit(name, set, x._name)) {
                                this._child = x;
                                this._n = 0;
                                break;
                            }
                            x = x._nextSibling;
                        }
                    }
                    if (this._n < 0) {
                        return null;
                    }
                    if (n <= this._n) break block13;
                    block1: while (n > this._n) {
                        x = this._child._nextSibling;
                        while (x != null) {
                            if (x.isElem() && this.nameHit(name, set, x._name)) {
                                this._child = x;
                                ++this._n;
                                continue block1;
                            }
                            x = x._nextSibling;
                        }
                        return null;
                    }
                    break block14;
                }
                if (n >= this._n) break block14;
                block3: while (n < this._n) {
                    x = this._child._prevSibling;
                    while (x != null) {
                        if (x.isElem() && this.nameHit(name, set, x._name)) {
                            this._child = x;
                            --this._n;
                            continue block3;
                        }
                        x = x._prevSibling;
                    }
                    return null;
                }
            }
            return this._child;
        }
    }

    static final class ScrubBuffer {
        private static final int START_STATE = 0;
        private static final int SPACE_SEEN_STATE = 1;
        private static final int NOSPACE_STATE = 2;
        private int _state;
        private int _wsr;
        private char[] _srcBuf = new char[1024];
        private final StringBuffer _sb = new StringBuffer();

        ScrubBuffer() {
        }

        void init(int wsr) {
            this._sb.delete(0, this._sb.length());
            this._wsr = wsr;
            this._state = 0;
        }

        void scrub(Object src, int off, int cch) {
            char[] chars;
            if (cch == 0) {
                return;
            }
            if (this._wsr == 1) {
                CharUtil.getString(this._sb, src, off, cch);
                return;
            }
            if (src instanceof char[]) {
                chars = (char[])src;
            } else {
                if (cch <= this._srcBuf.length) {
                    chars = this._srcBuf;
                } else if (cch <= 16384) {
                    this._srcBuf = new char[16384];
                    chars = this._srcBuf;
                } else {
                    chars = new char[cch];
                }
                CharUtil.getChars(chars, 0, src, off, cch);
                off = 0;
            }
            int start = 0;
            for (int i = 0; i < cch; ++i) {
                char ch = chars[off + i];
                if (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t') {
                    this._sb.append(chars, off + start, i - start);
                    start = i + 1;
                    if (this._wsr == 2) {
                        this._sb.append(' ');
                        continue;
                    }
                    if (this._state != 2) continue;
                    this._state = 1;
                    continue;
                }
                if (this._state == 1) {
                    this._sb.append(' ');
                }
                this._state = 2;
            }
            this._sb.append(chars, off + start, cch - start);
        }

        String getResultAsString() {
            return this._sb.toString();
        }
    }

    static interface ChangeListener {
        public void notifyChange();

        public void setNextChangeListener(ChangeListener var1);

        public ChangeListener getNextChangeListener();
    }

    private static class DocProps
    extends XmlDocumentProperties {
        private final HashMap<Object, Object> _map = new HashMap();

        private DocProps() {
        }

        @Override
        public Object put(Object key, Object value) {
            return this._map.put(key, value);
        }

        @Override
        public Object get(Object key) {
            return this._map.get(key);
        }

        @Override
        public Object remove(Object key) {
            return this._map.remove(key);
        }
    }

    private static class XmlSaxHandlerImpl
    extends SaxHandler
    implements XmlSaxHandler {
        private final SchemaType _type;
        private final XmlOptions _options;

        XmlSaxHandlerImpl(Locale l, SchemaType type, XmlOptions options) {
            super(null);
            this._options = options;
            this._type = type;
            XmlOptions saxHandlerOptions = new XmlOptions(options);
            saxHandlerOptions.setLoadUseLocaleCharUtil(true);
            this.initSaxHandler(l, saxHandlerOptions);
        }

        @Override
        public ContentHandler getContentHandler() {
            return this._context == null ? null : this;
        }

        @Override
        public LexicalHandler getLexicalHandler() {
            return this._context == null ? null : this;
        }

        @Override
        public void bookmarkLastEvent(XmlCursor.XmlBookmark mark) {
            this._context.bookmarkLastNonAttr(mark);
        }

        @Override
        public void bookmarkLastAttr(QName attrName, XmlCursor.XmlBookmark mark) {
            this._context.bookmarkLastAttr(attrName, mark);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public XmlObject getObject() throws XmlException {
            if (this._context == null) {
                return null;
            }
            this._locale.enter();
            try {
                Cur c = this._context.finish();
                Locale.autoTypeDocument(c, this._type, this._options);
                XmlObject x = (XmlObject)((Object)c.getUser());
                c.release();
                this._context = null;
                XmlObject xmlObject = x;
                return xmlObject;
            }
            finally {
                this._locale.exit();
            }
        }
    }

    private static interface SyncWrapFun<T> {
        public T parse(Locale var1) throws XmlException, IOException;
    }
}

