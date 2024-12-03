/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serializer.SerializationHandler
 */
package org.apache.xalan.templates;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.templates.AVT;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.ElemUse;
import org.apache.xalan.templates.NamespaceAlias;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.XSLTVisitor;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xml.utils.StringVector;
import org.apache.xpath.XPathContext;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;
import org.xml.sax.SAXException;

public class ElemLiteralResult
extends ElemUse {
    static final long serialVersionUID = -8703409074421657260L;
    private static final String EMPTYSTRING = "";
    private boolean isLiteralResultAsStylesheet = false;
    private List m_avts = null;
    private List m_xslAttr = null;
    private String m_namespace;
    private String m_localName;
    private String m_rawName;
    private StringVector m_ExtensionElementURIs;
    private String m_version;
    private StringVector m_excludeResultPrefixes;

    public void setIsLiteralResultAsStylesheet(boolean b) {
        this.isLiteralResultAsStylesheet = b;
    }

    public boolean getIsLiteralResultAsStylesheet() {
        return this.isLiteralResultAsStylesheet;
    }

    @Override
    public void compose(StylesheetRoot sroot) throws TransformerException {
        super.compose(sroot);
        StylesheetRoot.ComposeState cstate = sroot.getComposeState();
        Vector vnames = cstate.getVariableNames();
        if (null != this.m_avts) {
            int nAttrs = this.m_avts.size();
            for (int i = nAttrs - 1; i >= 0; --i) {
                AVT avt = (AVT)this.m_avts.get(i);
                avt.fixupVariables(vnames, cstate.getGlobalsSize());
            }
        }
    }

    public void addLiteralResultAttribute(AVT avt) {
        if (null == this.m_avts) {
            this.m_avts = new ArrayList();
        }
        this.m_avts.add(avt);
    }

    public void addLiteralResultAttribute(String att) {
        if (null == this.m_xslAttr) {
            this.m_xslAttr = new ArrayList();
        }
        this.m_xslAttr.add(att);
    }

    public void setXmlSpace(AVT avt) {
        this.addLiteralResultAttribute(avt);
        String val = avt.getSimpleString();
        if (val.equals("default")) {
            super.setXmlSpace(2);
        } else if (val.equals("preserve")) {
            super.setXmlSpace(1);
        }
    }

    public AVT getLiteralResultAttributeNS(String namespaceURI, String localName) {
        if (null != this.m_avts) {
            int nAttrs = this.m_avts.size();
            for (int i = nAttrs - 1; i >= 0; --i) {
                AVT avt = (AVT)this.m_avts.get(i);
                if (!avt.getName().equals(localName) || !avt.getURI().equals(namespaceURI)) continue;
                return avt;
            }
        }
        return null;
    }

    @Override
    public String getAttributeNS(String namespaceURI, String localName) {
        AVT avt = this.getLiteralResultAttributeNS(namespaceURI, localName);
        if (null != avt) {
            return avt.getSimpleString();
        }
        return EMPTYSTRING;
    }

    public AVT getLiteralResultAttribute(String name) {
        if (null != this.m_avts) {
            int nAttrs = this.m_avts.size();
            String namespace = null;
            for (int i = nAttrs - 1; i >= 0; --i) {
                AVT avt = (AVT)this.m_avts.get(i);
                namespace = avt.getURI();
                if ((namespace == null || namespace.length() == 0 || !(namespace + ":" + avt.getName()).equals(name)) && (namespace != null && namespace.length() != 0 || !avt.getRawName().equals(name))) continue;
                return avt;
            }
        }
        return null;
    }

    @Override
    public String getAttribute(String rawName) {
        AVT avt = this.getLiteralResultAttribute(rawName);
        if (null != avt) {
            return avt.getSimpleString();
        }
        return EMPTYSTRING;
    }

    @Override
    public boolean containsExcludeResultPrefix(String prefix, String uri) {
        if (uri == null || null == this.m_excludeResultPrefixes && null == this.m_ExtensionElementURIs) {
            return super.containsExcludeResultPrefix(prefix, uri);
        }
        if (prefix.length() == 0) {
            prefix = "#default";
        }
        if (this.m_excludeResultPrefixes != null) {
            for (int i = 0; i < this.m_excludeResultPrefixes.size(); ++i) {
                if (!uri.equals(this.getNamespaceForPrefix(this.m_excludeResultPrefixes.elementAt(i)))) continue;
                return true;
            }
        }
        if (this.m_ExtensionElementURIs != null && this.m_ExtensionElementURIs.contains(uri)) {
            return true;
        }
        return super.containsExcludeResultPrefix(prefix, uri);
    }

    @Override
    public void resolvePrefixTables() throws TransformerException {
        NamespaceAlias nsa;
        super.resolvePrefixTables();
        StylesheetRoot stylesheet = this.getStylesheetRoot();
        if (null != this.m_namespace && this.m_namespace.length() > 0 && null != (nsa = stylesheet.getNamespaceAliasComposed(this.m_namespace))) {
            this.m_namespace = nsa.getResultNamespace();
            String resultPrefix = nsa.getStylesheetPrefix();
            this.m_rawName = null != resultPrefix && resultPrefix.length() > 0 ? resultPrefix + ":" + this.m_localName : this.m_localName;
        }
        if (null != this.m_avts) {
            int n = this.m_avts.size();
            for (int i = 0; i < n; ++i) {
                NamespaceAlias nsa2;
                AVT avt = (AVT)this.m_avts.get(i);
                String ns = avt.getURI();
                if (null == ns || ns.length() <= 0 || null == (nsa2 = stylesheet.getNamespaceAliasComposed(this.m_namespace))) continue;
                String namespace = nsa2.getResultNamespace();
                String resultPrefix = nsa2.getStylesheetPrefix();
                String rawName = avt.getName();
                if (null != resultPrefix && resultPrefix.length() > 0) {
                    rawName = resultPrefix + ":" + rawName;
                }
                avt.setURI(namespace);
                avt.setRawName(rawName);
            }
        }
    }

    @Override
    boolean needToCheckExclude() {
        if (null == this.m_excludeResultPrefixes && null == this.getPrefixTable() && this.m_ExtensionElementURIs == null) {
            return false;
        }
        if (null == this.getPrefixTable()) {
            this.setPrefixTable(new ArrayList());
        }
        return true;
    }

    public void setNamespace(String ns) {
        if (null == ns) {
            ns = EMPTYSTRING;
        }
        this.m_namespace = ns;
    }

    public String getNamespace() {
        return this.m_namespace;
    }

    public void setLocalName(String localName) {
        this.m_localName = localName;
    }

    @Override
    public String getLocalName() {
        return this.m_localName;
    }

    public void setRawName(String rawName) {
        this.m_rawName = rawName;
    }

    public String getRawName() {
        return this.m_rawName;
    }

    @Override
    public String getPrefix() {
        int len = this.m_rawName.length() - this.m_localName.length() - 1;
        return len > 0 ? this.m_rawName.substring(0, len) : EMPTYSTRING;
    }

    public void setExtensionElementPrefixes(StringVector v) {
        this.m_ExtensionElementURIs = v;
    }

    @Override
    public NamedNodeMap getAttributes() {
        return new LiteralElementAttributes();
    }

    public String getExtensionElementPrefix(int i) throws ArrayIndexOutOfBoundsException {
        if (null == this.m_ExtensionElementURIs) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return this.m_ExtensionElementURIs.elementAt(i);
    }

    public int getExtensionElementPrefixCount() {
        return null != this.m_ExtensionElementURIs ? this.m_ExtensionElementURIs.size() : 0;
    }

    public boolean containsExtensionElementURI(String uri) {
        if (null == this.m_ExtensionElementURIs) {
            return false;
        }
        return this.m_ExtensionElementURIs.contains(uri);
    }

    @Override
    public int getXSLToken() {
        return 77;
    }

    @Override
    public String getNodeName() {
        return this.m_rawName;
    }

    public void setVersion(String v) {
        this.m_version = v;
    }

    public String getVersion() {
        return this.m_version;
    }

    public void setExcludeResultPrefixes(StringVector v) {
        this.m_excludeResultPrefixes = v;
    }

    private boolean excludeResultNSDecl(String prefix, String uri) throws TransformerException {
        if (null != this.m_excludeResultPrefixes) {
            return this.containsExcludeResultPrefix(prefix, uri);
        }
        return false;
    }

    @Override
    public void execute(TransformerImpl transformer) throws TransformerException {
        SerializationHandler rhandler = transformer.getSerializationHandler();
        try {
            if (transformer.getDebug()) {
                rhandler.flushPending();
                transformer.getTraceManager().fireTraceEvent(this);
            }
            rhandler.startPrefixMapping(this.getPrefix(), this.getNamespace());
            this.executeNSDecls(transformer);
            rhandler.startElement(this.getNamespace(), this.getLocalName(), this.getRawName());
        }
        catch (SAXException se) {
            throw new TransformerException(se);
        }
        TransformerException tException = null;
        try {
            super.execute(transformer);
            if (null != this.m_avts) {
                int nAttrs = this.m_avts.size();
                for (int i = nAttrs - 1; i >= 0; --i) {
                    int sourceNode;
                    XPathContext xctxt;
                    AVT avt = (AVT)this.m_avts.get(i);
                    String stringedValue = avt.evaluate(xctxt = transformer.getXPathContext(), sourceNode = xctxt.getCurrentNode(), this);
                    if (null == stringedValue) continue;
                    rhandler.addAttribute(avt.getURI(), avt.getName(), avt.getRawName(), "CDATA", stringedValue, false);
                }
            }
            transformer.executeChildTemplates((ElemTemplateElement)this, true);
        }
        catch (TransformerException te) {
            tException = te;
        }
        catch (SAXException se) {
            tException = new TransformerException(se);
        }
        try {
            if (transformer.getDebug()) {
                transformer.getTraceManager().fireTraceEndEvent(this);
            }
            rhandler.endElement(this.getNamespace(), this.getLocalName(), this.getRawName());
        }
        catch (SAXException se) {
            if (tException != null) {
                throw tException;
            }
            throw new TransformerException(se);
        }
        if (tException != null) {
            throw tException;
        }
        this.unexecuteNSDecls(transformer);
        try {
            rhandler.endPrefixMapping(this.getPrefix());
        }
        catch (SAXException se) {
            throw new TransformerException(se);
        }
    }

    public Iterator enumerateLiteralResultAttributes() {
        return null == this.m_avts ? null : this.m_avts.iterator();
    }

    @Override
    protected boolean accept(XSLTVisitor visitor) {
        return visitor.visitLiteralResultElement(this);
    }

    @Override
    protected void callChildVisitors(XSLTVisitor visitor, boolean callAttrs) {
        if (callAttrs && null != this.m_avts) {
            int nAttrs = this.m_avts.size();
            for (int i = nAttrs - 1; i >= 0; --i) {
                AVT avt = (AVT)this.m_avts.get(i);
                avt.callVisitors(visitor);
            }
        }
        super.callChildVisitors(visitor, callAttrs);
    }

    public void throwDOMException(short code, String msg) {
        String themsg = XSLMessages.createMessage(msg, null);
        throw new DOMException(code, themsg);
    }

    public class Attribute
    implements Attr {
        private AVT m_attribute;
        private Element m_owner = null;

        public Attribute(AVT avt, Element elem) {
            this.m_attribute = avt;
            this.m_owner = elem;
        }

        @Override
        public Node appendChild(Node newChild) throws DOMException {
            ElemLiteralResult.this.throwDOMException((short)7, "NO_MODIFICATION_ALLOWED_ERR");
            return null;
        }

        @Override
        public Node cloneNode(boolean deep) {
            return new Attribute(this.m_attribute, this.m_owner);
        }

        @Override
        public NamedNodeMap getAttributes() {
            return null;
        }

        @Override
        public NodeList getChildNodes() {
            return new NodeList(){

                @Override
                public int getLength() {
                    return 0;
                }

                @Override
                public Node item(int index) {
                    return null;
                }
            };
        }

        @Override
        public Node getFirstChild() {
            return null;
        }

        @Override
        public Node getLastChild() {
            return null;
        }

        @Override
        public String getLocalName() {
            return this.m_attribute.getName();
        }

        @Override
        public String getNamespaceURI() {
            String uri = this.m_attribute.getURI();
            return uri.length() == 0 ? null : uri;
        }

        @Override
        public Node getNextSibling() {
            return null;
        }

        @Override
        public String getNodeName() {
            String uri = this.m_attribute.getURI();
            String localName = this.getLocalName();
            return uri.length() == 0 ? localName : uri + ":" + localName;
        }

        @Override
        public short getNodeType() {
            return 2;
        }

        @Override
        public String getNodeValue() throws DOMException {
            return this.m_attribute.getSimpleString();
        }

        @Override
        public Document getOwnerDocument() {
            return this.m_owner.getOwnerDocument();
        }

        @Override
        public Node getParentNode() {
            return this.m_owner;
        }

        @Override
        public String getPrefix() {
            String uri = this.m_attribute.getURI();
            String rawName = this.m_attribute.getRawName();
            return uri.length() == 0 ? null : rawName.substring(0, rawName.indexOf(":"));
        }

        @Override
        public Node getPreviousSibling() {
            return null;
        }

        @Override
        public boolean hasAttributes() {
            return false;
        }

        @Override
        public boolean hasChildNodes() {
            return false;
        }

        @Override
        public Node insertBefore(Node newChild, Node refChild) throws DOMException {
            ElemLiteralResult.this.throwDOMException((short)7, "NO_MODIFICATION_ALLOWED_ERR");
            return null;
        }

        @Override
        public boolean isSupported(String feature, String version) {
            return false;
        }

        @Override
        public void normalize() {
        }

        @Override
        public Node removeChild(Node oldChild) throws DOMException {
            ElemLiteralResult.this.throwDOMException((short)7, "NO_MODIFICATION_ALLOWED_ERR");
            return null;
        }

        @Override
        public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
            ElemLiteralResult.this.throwDOMException((short)7, "NO_MODIFICATION_ALLOWED_ERR");
            return null;
        }

        @Override
        public void setNodeValue(String nodeValue) throws DOMException {
            ElemLiteralResult.this.throwDOMException((short)7, "NO_MODIFICATION_ALLOWED_ERR");
        }

        @Override
        public void setPrefix(String prefix) throws DOMException {
            ElemLiteralResult.this.throwDOMException((short)7, "NO_MODIFICATION_ALLOWED_ERR");
        }

        @Override
        public String getName() {
            return this.m_attribute.getName();
        }

        @Override
        public String getValue() {
            return this.m_attribute.getSimpleString();
        }

        @Override
        public Element getOwnerElement() {
            return this.m_owner;
        }

        @Override
        public boolean getSpecified() {
            return true;
        }

        @Override
        public void setValue(String value) throws DOMException {
            ElemLiteralResult.this.throwDOMException((short)7, "NO_MODIFICATION_ALLOWED_ERR");
        }

        @Override
        public TypeInfo getSchemaTypeInfo() {
            return null;
        }

        @Override
        public boolean isId() {
            return false;
        }

        @Override
        public Object setUserData(String key, Object data, UserDataHandler handler) {
            return this.getOwnerDocument().setUserData(key, data, handler);
        }

        @Override
        public Object getUserData(String key) {
            return this.getOwnerDocument().getUserData(key);
        }

        @Override
        public Object getFeature(String feature, String version) {
            return this.isSupported(feature, version) ? this : null;
        }

        @Override
        public boolean isEqualNode(Node arg) {
            return arg == this;
        }

        @Override
        public String lookupNamespaceURI(String specifiedPrefix) {
            return null;
        }

        @Override
        public boolean isDefaultNamespace(String namespaceURI) {
            return false;
        }

        @Override
        public String lookupPrefix(String namespaceURI) {
            return null;
        }

        @Override
        public boolean isSameNode(Node other) {
            return this == other;
        }

        @Override
        public void setTextContent(String textContent) throws DOMException {
            this.setNodeValue(textContent);
        }

        @Override
        public String getTextContent() throws DOMException {
            return this.getNodeValue();
        }

        @Override
        public short compareDocumentPosition(Node other) throws DOMException {
            return 0;
        }

        @Override
        public String getBaseURI() {
            return null;
        }
    }

    public class LiteralElementAttributes
    implements NamedNodeMap {
        private int m_count = -1;

        @Override
        public int getLength() {
            if (this.m_count == -1) {
                this.m_count = null != ElemLiteralResult.this.m_avts ? ElemLiteralResult.this.m_avts.size() : 0;
            }
            return this.m_count;
        }

        @Override
        public Node getNamedItem(String name) {
            if (this.getLength() == 0) {
                return null;
            }
            String uri = null;
            String localName = name;
            int index = name.indexOf(":");
            if (-1 != index) {
                uri = name.substring(0, index);
                localName = name.substring(index + 1);
            }
            Attribute retNode = null;
            for (AVT avt : ElemLiteralResult.this.m_avts) {
                if (!localName.equals(avt.getName())) continue;
                String nsURI = avt.getURI();
                if ((uri != null || nsURI != null) && (uri == null || !uri.equals(nsURI))) continue;
                retNode = new Attribute(avt, ElemLiteralResult.this);
                break;
            }
            return retNode;
        }

        @Override
        public Node getNamedItemNS(String namespaceURI, String localName) {
            if (this.getLength() == 0) {
                return null;
            }
            Attribute retNode = null;
            for (AVT avt : ElemLiteralResult.this.m_avts) {
                if (!localName.equals(avt.getName())) continue;
                String nsURI = avt.getURI();
                if ((namespaceURI != null || nsURI != null) && (namespaceURI == null || !namespaceURI.equals(nsURI))) continue;
                retNode = new Attribute(avt, ElemLiteralResult.this);
                break;
            }
            return retNode;
        }

        @Override
        public Node item(int i) {
            if (this.getLength() == 0 || i >= ElemLiteralResult.this.m_avts.size()) {
                return null;
            }
            return new Attribute((AVT)ElemLiteralResult.this.m_avts.get(i), ElemLiteralResult.this);
        }

        @Override
        public Node removeNamedItem(String name) throws DOMException {
            ElemLiteralResult.this.throwDOMException((short)7, "NO_MODIFICATION_ALLOWED_ERR");
            return null;
        }

        @Override
        public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException {
            ElemLiteralResult.this.throwDOMException((short)7, "NO_MODIFICATION_ALLOWED_ERR");
            return null;
        }

        @Override
        public Node setNamedItem(Node arg) throws DOMException {
            ElemLiteralResult.this.throwDOMException((short)7, "NO_MODIFICATION_ALLOWED_ERR");
            return null;
        }

        @Override
        public Node setNamedItemNS(Node arg) throws DOMException {
            ElemLiteralResult.this.throwDOMException((short)7, "NO_MODIFICATION_ALLOWED_ERR");
            return null;
        }
    }
}

