/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serializer;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Transformer;
import org.apache.xml.serializer.AttributesImplSerializer;
import org.apache.xml.serializer.DOMSerializer;
import org.apache.xml.serializer.ElemContext;
import org.apache.xml.serializer.NamespaceMappings;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xml.serializer.SerializerConstants;
import org.apache.xml.serializer.SerializerTrace;
import org.apache.xml.serializer.dom3.DOM3SerializerImpl;
import org.apache.xml.serializer.utils.Utils;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public abstract class SerializerBase
implements SerializationHandler,
SerializerConstants {
    public static final String PKG_NAME;
    public static final String PKG_PATH;
    protected boolean m_needToCallStartDocument = true;
    protected boolean m_cdataTagOpen = false;
    protected AttributesImplSerializer m_attributes = new AttributesImplSerializer();
    protected boolean m_inEntityRef = false;
    protected boolean m_inExternalDTD = false;
    protected String m_doctypeSystem;
    protected String m_doctypePublic;
    boolean m_needToOutputDocTypeDecl = true;
    protected boolean m_shouldNotWriteXMLHeader = false;
    private String m_standalone;
    protected boolean m_standaloneWasSpecified = false;
    protected boolean m_doIndent = false;
    protected int m_indentAmount = 0;
    protected String m_version = null;
    protected String m_mediatype;
    private Transformer m_transformer;
    protected NamespaceMappings m_prefixMap;
    protected SerializerTrace m_tracer;
    protected SourceLocator m_sourceLocator;
    protected Writer m_writer = null;
    protected ElemContext m_elemContext = new ElemContext();
    protected char[] m_charsBuff = new char[60];
    protected char[] m_attrBuff = new char[30];
    protected String m_StringOfCDATASections = null;
    boolean m_docIsEmpty = true;
    protected Hashtable m_CdataElems = null;
    private HashMap m_OutputProps;
    private HashMap m_OutputPropsDefault;

    SerializerBase() {
    }

    protected void fireEndElem(String name) throws SAXException {
        if (this.m_tracer != null) {
            this.flushMyWriter();
            this.m_tracer.fireGenerateEvent(4, name, (Attributes)null);
        }
    }

    protected void fireCharEvent(char[] chars, int start, int length) throws SAXException {
        if (this.m_tracer != null) {
            this.flushMyWriter();
            this.m_tracer.fireGenerateEvent(5, chars, start, length);
        }
    }

    @Override
    public void comment(String data) throws SAXException {
        this.m_docIsEmpty = false;
        int length = data.length();
        if (length > this.m_charsBuff.length) {
            this.m_charsBuff = new char[length * 2 + 1];
        }
        data.getChars(0, length, this.m_charsBuff, 0);
        this.comment(this.m_charsBuff, 0, length);
    }

    protected String patchName(String qname) {
        int lastColon = qname.lastIndexOf(58);
        if (lastColon > 0) {
            int firstColon = qname.indexOf(58);
            String prefix = qname.substring(0, firstColon);
            String localName = qname.substring(lastColon + 1);
            String uri = this.m_prefixMap.lookupNamespace(prefix);
            if (uri != null && uri.length() == 0) {
                return localName;
            }
            if (firstColon != lastColon) {
                return prefix + ':' + localName;
            }
        }
        return qname;
    }

    protected static String getLocalName(String qname) {
        int col = qname.lastIndexOf(58);
        return col > 0 ? qname.substring(col + 1) : qname;
    }

    @Override
    public void setDocumentLocator(Locator locator) {
    }

    @Override
    public void addAttribute(String uri, String localName, String rawName, String type, String value, boolean XSLAttribute) throws SAXException {
        if (this.m_elemContext.m_startTagOpen) {
            this.addAttributeAlways(uri, localName, rawName, type, value, XSLAttribute);
        }
    }

    public boolean addAttributeAlways(String uri, String localName, String rawName, String type, String value, boolean XSLAttribute) {
        boolean was_added;
        int index = localName == null || uri == null || uri.length() == 0 ? this.m_attributes.getIndex(rawName) : this.m_attributes.getIndex(uri, localName);
        if (index >= 0) {
            this.m_attributes.setValue(index, value);
            was_added = false;
        } else {
            this.m_attributes.addAttribute(uri, localName, rawName, type, value);
            was_added = true;
        }
        return was_added;
    }

    @Override
    public void addAttribute(String name, String value) {
        if (this.m_elemContext.m_startTagOpen) {
            String patchedName = this.patchName(name);
            String localName = SerializerBase.getLocalName(patchedName);
            String uri = this.getNamespaceURI(patchedName, false);
            this.addAttributeAlways(uri, localName, patchedName, "CDATA", value, false);
        }
    }

    @Override
    public void addXSLAttribute(String name, String value, String uri) {
        if (this.m_elemContext.m_startTagOpen) {
            String patchedName = this.patchName(name);
            String localName = SerializerBase.getLocalName(patchedName);
            this.addAttributeAlways(uri, localName, patchedName, "CDATA", value, true);
        }
    }

    @Override
    public void addAttributes(Attributes atts) throws SAXException {
        int nAtts = atts.getLength();
        for (int i = 0; i < nAtts; ++i) {
            String uri = atts.getURI(i);
            if (null == uri) {
                uri = "";
            }
            this.addAttributeAlways(uri, atts.getLocalName(i), atts.getQName(i), atts.getType(i), atts.getValue(i), false);
        }
    }

    @Override
    public ContentHandler asContentHandler() throws IOException {
        return this;
    }

    @Override
    public void endEntity(String name) throws SAXException {
        if (name.equals("[dtd]")) {
            this.m_inExternalDTD = false;
        }
        this.m_inEntityRef = false;
        if (this.m_tracer != null) {
            this.fireEndEntity(name);
        }
    }

    @Override
    public void close() {
    }

    protected void initCDATA() {
    }

    @Override
    public String getEncoding() {
        return this.getOutputProperty("encoding");
    }

    @Override
    public void setEncoding(String encoding) {
        this.setOutputProperty("encoding", encoding);
    }

    @Override
    public void setOmitXMLDeclaration(boolean b) {
        String val = b ? "yes" : "no";
        this.setOutputProperty("omit-xml-declaration", val);
    }

    @Override
    public boolean getOmitXMLDeclaration() {
        return this.m_shouldNotWriteXMLHeader;
    }

    @Override
    public String getDoctypePublic() {
        return this.m_doctypePublic;
    }

    @Override
    public void setDoctypePublic(String doctypePublic) {
        this.setOutputProperty("doctype-public", doctypePublic);
    }

    @Override
    public String getDoctypeSystem() {
        return this.m_doctypeSystem;
    }

    @Override
    public void setDoctypeSystem(String doctypeSystem) {
        this.setOutputProperty("doctype-system", doctypeSystem);
    }

    @Override
    public void setDoctype(String doctypeSystem, String doctypePublic) {
        this.setOutputProperty("doctype-system", doctypeSystem);
        this.setOutputProperty("doctype-public", doctypePublic);
    }

    @Override
    public void setStandalone(String standalone) {
        this.setOutputProperty("standalone", standalone);
    }

    protected void setStandaloneInternal(String standalone) {
        this.m_standalone = "yes".equals(standalone) ? "yes" : "no";
    }

    @Override
    public String getStandalone() {
        return this.m_standalone;
    }

    @Override
    public boolean getIndent() {
        return this.m_doIndent;
    }

    @Override
    public String getMediaType() {
        return this.m_mediatype;
    }

    @Override
    public String getVersion() {
        return this.m_version;
    }

    @Override
    public void setVersion(String version) {
        this.setOutputProperty("version", version);
    }

    @Override
    public void setMediaType(String mediaType) {
        this.setOutputProperty("media-type", mediaType);
    }

    @Override
    public int getIndentAmount() {
        return this.m_indentAmount;
    }

    @Override
    public void setIndentAmount(int m_indentAmount) {
        this.m_indentAmount = m_indentAmount;
    }

    @Override
    public void setIndent(boolean doIndent) {
        String val = doIndent ? "yes" : "no";
        this.setOutputProperty("indent", val);
    }

    @Override
    public void namespaceAfterStartElement(String uri, String prefix) throws SAXException {
    }

    @Override
    public DOMSerializer asDOMSerializer() throws IOException {
        return this;
    }

    private static final boolean subPartMatch(String p, String t) {
        return p == t || null != p && p.equals(t);
    }

    protected static final String getPrefixPart(String qname) {
        int col = qname.indexOf(58);
        return col > 0 ? qname.substring(0, col) : null;
    }

    @Override
    public NamespaceMappings getNamespaceMappings() {
        return this.m_prefixMap;
    }

    @Override
    public String getPrefix(String namespaceURI) {
        String prefix = this.m_prefixMap.lookupPrefix(namespaceURI);
        return prefix;
    }

    @Override
    public String getNamespaceURI(String qname, boolean isElement) {
        String prefix;
        String uri = "";
        int col = qname.lastIndexOf(58);
        String string = prefix = col > 0 ? qname.substring(0, col) : "";
        if (!("".equals(prefix) && !isElement || this.m_prefixMap == null || (uri = this.m_prefixMap.lookupNamespace(prefix)) != null || prefix.equals("xmlns"))) {
            throw new RuntimeException(Utils.messages.createMessage("ER_NAMESPACE_PREFIX", new Object[]{qname.substring(0, col)}));
        }
        return uri;
    }

    @Override
    public String getNamespaceURIFromPrefix(String prefix) {
        String uri = null;
        if (this.m_prefixMap != null) {
            uri = this.m_prefixMap.lookupNamespace(prefix);
        }
        return uri;
    }

    @Override
    public void entityReference(String name) throws SAXException {
        this.flushPending();
        this.startEntity(name);
        this.endEntity(name);
        if (this.m_tracer != null) {
            this.fireEntityReference(name);
        }
    }

    @Override
    public void setTransformer(Transformer t) {
        this.m_transformer = t;
        this.m_tracer = this.m_transformer instanceof SerializerTrace && ((SerializerTrace)((Object)this.m_transformer)).hasTraceListeners() ? (SerializerTrace)((Object)this.m_transformer) : null;
    }

    @Override
    public Transformer getTransformer() {
        return this.m_transformer;
    }

    @Override
    public void characters(Node node) throws SAXException {
        this.flushPending();
        String data = node.getNodeValue();
        if (data != null) {
            int length = data.length();
            if (length > this.m_charsBuff.length) {
                this.m_charsBuff = new char[length * 2 + 1];
            }
            data.getChars(0, length, this.m_charsBuff, 0);
            this.characters(this.m_charsBuff, 0, length);
        }
    }

    @Override
    public void error(SAXParseException exc) throws SAXException {
    }

    @Override
    public void fatalError(SAXParseException exc) throws SAXException {
        this.m_elemContext.m_startTagOpen = false;
    }

    @Override
    public void warning(SAXParseException exc) throws SAXException {
    }

    protected void fireStartEntity(String name) throws SAXException {
        if (this.m_tracer != null) {
            this.flushMyWriter();
            this.m_tracer.fireGenerateEvent(9, name);
        }
    }

    private void flushMyWriter() {
        if (this.m_writer != null) {
            try {
                this.m_writer.flush();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    protected void fireCDATAEvent(char[] chars, int start, int length) throws SAXException {
        if (this.m_tracer != null) {
            this.flushMyWriter();
            this.m_tracer.fireGenerateEvent(10, chars, start, length);
        }
    }

    protected void fireCommentEvent(char[] chars, int start, int length) throws SAXException {
        if (this.m_tracer != null) {
            this.flushMyWriter();
            this.m_tracer.fireGenerateEvent(8, new String(chars, start, length));
        }
    }

    public void fireEndEntity(String name) throws SAXException {
        if (this.m_tracer != null) {
            this.flushMyWriter();
        }
    }

    protected void fireStartDoc() throws SAXException {
        if (this.m_tracer != null) {
            this.flushMyWriter();
            this.m_tracer.fireGenerateEvent(1);
        }
    }

    protected void fireEndDoc() throws SAXException {
        if (this.m_tracer != null) {
            this.flushMyWriter();
            this.m_tracer.fireGenerateEvent(2);
        }
    }

    protected void fireStartElem(String elemName) throws SAXException {
        if (this.m_tracer != null) {
            this.flushMyWriter();
            this.m_tracer.fireGenerateEvent(3, elemName, this.m_attributes);
        }
    }

    protected void fireEscapingEvent(String name, String data) throws SAXException {
        if (this.m_tracer != null) {
            this.flushMyWriter();
            this.m_tracer.fireGenerateEvent(7, name, data);
        }
    }

    protected void fireEntityReference(String name) throws SAXException {
        if (this.m_tracer != null) {
            this.flushMyWriter();
            this.m_tracer.fireGenerateEvent(9, name, (Attributes)null);
        }
    }

    @Override
    public void startDocument() throws SAXException {
        this.startDocumentInternal();
        this.m_needToCallStartDocument = false;
    }

    protected void startDocumentInternal() throws SAXException {
        if (this.m_tracer != null) {
            this.fireStartDoc();
        }
    }

    @Override
    public void setSourceLocator(SourceLocator locator) {
        this.m_sourceLocator = locator;
    }

    @Override
    public void setNamespaceMappings(NamespaceMappings mappings) {
        this.m_prefixMap = mappings;
    }

    @Override
    public boolean reset() {
        this.resetSerializerBase();
        return true;
    }

    private void resetSerializerBase() {
        this.m_attributes.clear();
        this.m_CdataElems = null;
        this.m_cdataTagOpen = false;
        this.m_docIsEmpty = true;
        this.m_doctypePublic = null;
        this.m_doctypeSystem = null;
        this.m_doIndent = false;
        this.m_elemContext = new ElemContext();
        this.m_indentAmount = 0;
        this.m_inEntityRef = false;
        this.m_inExternalDTD = false;
        this.m_mediatype = null;
        this.m_needToCallStartDocument = true;
        this.m_needToOutputDocTypeDecl = false;
        if (this.m_OutputProps != null) {
            this.m_OutputProps.clear();
        }
        if (this.m_OutputPropsDefault != null) {
            this.m_OutputPropsDefault.clear();
        }
        if (this.m_prefixMap != null) {
            this.m_prefixMap.reset();
        }
        this.m_shouldNotWriteXMLHeader = false;
        this.m_sourceLocator = null;
        this.m_standalone = null;
        this.m_standaloneWasSpecified = false;
        this.m_StringOfCDATASections = null;
        this.m_tracer = null;
        this.m_transformer = null;
        this.m_version = null;
    }

    final boolean inTemporaryOutputState() {
        return this.getEncoding() == null;
    }

    @Override
    public void addAttribute(String uri, String localName, String rawName, String type, String value) throws SAXException {
        if (this.m_elemContext.m_startTagOpen) {
            this.addAttributeAlways(uri, localName, rawName, type, value, false);
        }
    }

    @Override
    public void notationDecl(String arg0, String arg1, String arg2) throws SAXException {
    }

    @Override
    public void unparsedEntityDecl(String arg0, String arg1, String arg2, String arg3) throws SAXException {
    }

    @Override
    public void setDTDEntityExpansion(boolean expand) {
    }

    void initCdataElems(String s) {
        if (s != null) {
            int max = s.length();
            boolean inCurly = false;
            boolean foundURI = false;
            StringBuffer buf = new StringBuffer();
            String uri = null;
            String localName = null;
            for (int i = 0; i < max; ++i) {
                char c = s.charAt(i);
                if (Character.isWhitespace(c)) {
                    if (!inCurly) {
                        if (buf.length() <= 0) continue;
                        localName = buf.toString();
                        if (!foundURI) {
                            uri = "";
                        }
                        this.addCDATAElement(uri, localName);
                        buf.setLength(0);
                        foundURI = false;
                        continue;
                    }
                    buf.append(c);
                    continue;
                }
                if ('{' == c) {
                    inCurly = true;
                    continue;
                }
                if ('}' == c) {
                    foundURI = true;
                    uri = buf.toString();
                    buf.setLength(0);
                    inCurly = false;
                    continue;
                }
                buf.append(c);
            }
            if (buf.length() > 0) {
                localName = buf.toString();
                if (!foundURI) {
                    uri = "";
                }
                this.addCDATAElement(uri, localName);
            }
        }
    }

    private void addCDATAElement(String uri, String localName) {
        Hashtable<String, String> h;
        if (this.m_CdataElems == null) {
            this.m_CdataElems = new Hashtable();
        }
        if ((h = (Hashtable<String, String>)this.m_CdataElems.get(localName)) == null) {
            h = new Hashtable<String, String>();
            this.m_CdataElems.put(localName, h);
        }
        h.put(uri, uri);
    }

    public boolean documentIsEmpty() {
        return this.m_docIsEmpty && this.m_elemContext.m_currentElemDepth == 0;
    }

    protected boolean isCdataSection() {
        boolean b = false;
        if (null != this.m_StringOfCDATASections) {
            Object obj;
            if (this.m_elemContext.m_elementLocalName == null) {
                String localName;
                this.m_elemContext.m_elementLocalName = localName = SerializerBase.getLocalName(this.m_elemContext.m_elementName);
            }
            if (this.m_elemContext.m_elementURI == null) {
                this.m_elemContext.m_elementURI = this.getElementURI();
            } else if (this.m_elemContext.m_elementURI.length() == 0) {
                if (this.m_elemContext.m_elementName == null) {
                    this.m_elemContext.m_elementName = this.m_elemContext.m_elementLocalName;
                } else if (this.m_elemContext.m_elementLocalName.length() < this.m_elemContext.m_elementName.length()) {
                    this.m_elemContext.m_elementURI = this.getElementURI();
                }
            }
            Hashtable h = (Hashtable)this.m_CdataElems.get(this.m_elemContext.m_elementLocalName);
            if (h != null && (obj = h.get(this.m_elemContext.m_elementURI)) != null) {
                b = true;
            }
        }
        return b;
    }

    private String getElementURI() {
        String uri = null;
        String prefix = SerializerBase.getPrefixPart(this.m_elemContext.m_elementName);
        uri = prefix == null ? this.m_prefixMap.lookupNamespace("") : this.m_prefixMap.lookupNamespace(prefix);
        if (uri == null) {
            uri = "";
        }
        return uri;
    }

    @Override
    public String getOutputProperty(String name) {
        String val = this.getOutputPropertyNonDefault(name);
        if (val == null) {
            val = this.getOutputPropertyDefault(name);
        }
        return val;
    }

    public String getOutputPropertyNonDefault(String name) {
        return this.getProp(name, false);
    }

    @Override
    public Object asDOM3Serializer() throws IOException {
        return new DOM3SerializerImpl(this);
    }

    @Override
    public String getOutputPropertyDefault(String name) {
        return this.getProp(name, true);
    }

    @Override
    public void setOutputProperty(String name, String val) {
        this.setProp(name, val, false);
    }

    @Override
    public void setOutputPropertyDefault(String name, String val) {
        this.setProp(name, val, true);
    }

    Set getOutputPropDefaultKeys() {
        return this.m_OutputPropsDefault.keySet();
    }

    Set getOutputPropKeys() {
        return this.m_OutputProps.keySet();
    }

    private String getProp(String name, boolean defaultVal) {
        if (this.m_OutputProps == null) {
            this.m_OutputProps = new HashMap();
            this.m_OutputPropsDefault = new HashMap();
        }
        String val = defaultVal ? (String)this.m_OutputPropsDefault.get(name) : (String)this.m_OutputProps.get(name);
        return val;
    }

    void setProp(String name, String val, boolean defaultVal) {
        if (this.m_OutputProps == null) {
            this.m_OutputProps = new HashMap();
            this.m_OutputPropsDefault = new HashMap();
        }
        if (defaultVal) {
            this.m_OutputPropsDefault.put(name, val);
        } else if ("cdata-section-elements".equals(name) && val != null) {
            this.initCdataElems(val);
            String oldVal = (String)this.m_OutputProps.get(name);
            String newVal = oldVal == null ? oldVal + ' ' + val : val;
            this.m_OutputProps.put(name, newVal);
        } else {
            this.m_OutputProps.put(name, val);
        }
    }

    static char getFirstCharLocName(String name) {
        int i = name.indexOf(125);
        char first = i < 0 ? name.charAt(0) : name.charAt(i + 1);
        return first;
    }

    static {
        String fullyQualifiedName = SerializerBase.class.getName();
        int lastDot = fullyQualifiedName.lastIndexOf(46);
        PKG_NAME = lastDot < 0 ? "" : fullyQualifiedName.substring(0, lastDot);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < PKG_NAME.length(); ++i) {
            char ch = PKG_NAME.charAt(i);
            if (ch == '.') {
                sb.append('/');
                continue;
            }
            sb.append(ch);
        }
        PKG_PATH = sb.toString();
    }
}

