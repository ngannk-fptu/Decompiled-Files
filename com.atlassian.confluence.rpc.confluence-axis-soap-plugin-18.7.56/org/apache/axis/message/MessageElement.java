/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.message;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.TypeMapping;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.constants.Style;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.message.EnvelopeHandler;
import org.apache.axis.message.NodeImpl;
import org.apache.axis.message.NodeListImpl;
import org.apache.axis.message.NullAttributes;
import org.apache.axis.message.PrefixedQName;
import org.apache.axis.message.SAX2EventRecorder;
import org.apache.axis.message.SAXOutputter;
import org.apache.axis.message.SOAPDocumentImpl;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.message.Text;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Mapping;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class MessageElement
extends NodeImpl
implements SOAPElement,
Serializable,
NodeList,
Cloneable {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$message$MessageElement == null ? (class$org$apache$axis$message$MessageElement = MessageElement.class$("org.apache.axis.message.MessageElement")) : class$org$apache$axis$message$MessageElement).getName());
    private static final Mapping enc11Mapping = new Mapping("http://schemas.xmlsoap.org/soap/encoding/", "SOAP-ENC");
    private static final Mapping enc12Mapping = new Mapping("http://www.w3.org/2003/05/soap-encoding", "SOAP-ENC");
    protected String id;
    protected String href;
    protected boolean _isRoot = true;
    protected SOAPEnvelope message = null;
    protected transient DeserializationContext context;
    protected transient QName typeQName = null;
    protected Vector qNameAttrs = null;
    protected transient SAX2EventRecorder recorder = null;
    protected int startEventIndex = 0;
    protected int startContentsIndex = 0;
    protected int endEventIndex = -1;
    public ArrayList namespaces = null;
    protected String encodingStyle = null;
    private Object objectValue = null;
    protected Deserializer fixupDeserializer;
    static /* synthetic */ Class class$org$apache$axis$message$MessageElement;

    public MessageElement() {
    }

    public MessageElement(String namespace, String localPart) {
        this.namespaceURI = namespace;
        this.name = localPart;
    }

    public MessageElement(String localPart, String prefix, String namespace) {
        this.namespaceURI = namespace;
        this.name = localPart;
        this.prefix = prefix;
        this.addMapping(new Mapping(namespace, prefix));
    }

    public MessageElement(Name eltName) {
        this(eltName.getLocalName(), eltName.getPrefix(), eltName.getURI());
    }

    public MessageElement(String namespace, String localPart, Object value) {
        this(namespace, localPart);
        this.objectValue = value;
    }

    public MessageElement(QName name) {
        this(name.getNamespaceURI(), name.getLocalPart());
    }

    public MessageElement(QName name, Object value) {
        this(name.getNamespaceURI(), name.getLocalPart());
        this.objectValue = value;
    }

    public MessageElement(Element elem) {
        this.namespaceURI = elem.getNamespaceURI();
        this.name = elem.getLocalName();
        this.copyNode(elem);
    }

    public MessageElement(CharacterData text) {
        this.textRep = text;
        this.namespaceURI = text.getNamespaceURI();
        this.name = text.getLocalName();
    }

    public MessageElement(String namespace, String localPart, String prefix, Attributes attributes, DeserializationContext context) throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug((Object)Messages.getMessage("newElem00", super.toString(), "{" + prefix + "}" + localPart));
            for (int i = 0; attributes != null && i < attributes.getLength(); ++i) {
                log.debug((Object)("  " + attributes.getQName(i) + " = '" + attributes.getValue(i) + "'"));
            }
        }
        this.namespaceURI = namespace;
        this.name = localPart;
        this.prefix = prefix;
        this.context = context;
        this.startEventIndex = context.getStartOfMappingsPos();
        this.setNSMappings(context.getCurrentNSMappings());
        this.recorder = context.getRecorder();
        if (attributes != null && attributes.getLength() > 0) {
            TypeMapping tm;
            MessageContext mc;
            this.attributes = attributes;
            this.typeQName = context.getTypeFromAttributes(namespace, localPart, attributes);
            String rootVal = attributes.getValue(Constants.URI_DEFAULT_SOAP_ENC, "root");
            if (rootVal != null) {
                this._isRoot = "1".equals(rootVal);
            }
            this.id = attributes.getValue("id");
            if (this.id != null) {
                context.registerElementByID(this.id, this);
                if (this.recorder == null) {
                    this.recorder = new SAX2EventRecorder();
                    context.setRecorder(this.recorder);
                }
            }
            SOAPConstants sc = (mc = context.getMessageContext()) != null ? mc.getSOAPConstants() : SOAPConstants.SOAP11_CONSTANTS;
            this.href = attributes.getValue(sc.getAttrHref());
            if (attributes.getValue(Constants.URI_DEFAULT_SOAP_ENC, "arrayType") != null) {
                this.typeQName = Constants.SOAP_ARRAY;
            }
            this.encodingStyle = attributes.getValue(sc.getEncodingURI(), "encodingStyle");
            if ("http://www.w3.org/2003/05/soap-envelope/encoding/none".equals(this.encodingStyle)) {
                this.encodingStyle = null;
            }
            if (this.encodingStyle != null && sc.equals(SOAPConstants.SOAP12_CONSTANTS) && mc.getOperationStyle() != Style.MESSAGE && ((tm = mc.getTypeMappingRegistry().getTypeMapping(this.encodingStyle)) == null || tm.equals(mc.getTypeMappingRegistry().getDefaultTypeMapping()))) {
                AxisFault badEncodingFault = new AxisFault(Constants.FAULT_SOAP12_DATAENCODINGUNKNOWN, "bad encoding style", null, null);
                throw badEncodingFault;
            }
        }
    }

    public DeserializationContext getDeserializationContext() {
        return this.context;
    }

    public void setFixupDeserializer(Deserializer dser) {
        this.fixupDeserializer = dser;
    }

    public Deserializer getFixupDeserializer() {
        return this.fixupDeserializer;
    }

    public void setEndIndex(int endIndex) {
        this.endEventIndex = endIndex;
    }

    public boolean isRoot() {
        return this._isRoot;
    }

    public String getID() {
        return this.id;
    }

    public String getHref() {
        return this.href;
    }

    public Attributes getAttributesEx() {
        return this.attributes;
    }

    public Node cloneNode(boolean deep) {
        try {
            MessageElement clonedSelf = (MessageElement)this.cloning();
            if (deep && this.children != null) {
                for (int i = 0; i < this.children.size(); ++i) {
                    NodeImpl child = (NodeImpl)this.children.get(i);
                    if (child == null) continue;
                    NodeImpl clonedChild = (NodeImpl)child.cloneNode(deep);
                    clonedChild.setParent(clonedSelf);
                    clonedChild.setOwnerDocument(this.getOwnerDocument());
                    clonedSelf.childDeepCloned(child, clonedChild);
                }
            }
            return clonedSelf;
        }
        catch (Exception e) {
            return null;
        }
    }

    protected void childDeepCloned(NodeImpl oldNode, NodeImpl newNode) {
    }

    protected Object cloning() throws CloneNotSupportedException {
        try {
            MessageElement clonedME = null;
            clonedME = (MessageElement)this.clone();
            clonedME.setName(this.name);
            clonedME.setNamespaceURI(this.namespaceURI);
            clonedME.setPrefix(this.prefix);
            clonedME.setAllAttributes(new AttributesImpl(this.attributes));
            clonedME.namespaces = new ArrayList();
            if (this.namespaces != null) {
                for (int i = 0; i < this.namespaces.size(); ++i) {
                    Mapping namespace = (Mapping)this.namespaces.get(i);
                    clonedME.addNamespaceDeclaration(namespace.getPrefix(), namespace.getNamespaceURI());
                }
            }
            clonedME.children = new ArrayList();
            clonedME.parent = null;
            clonedME.setDirty(this._isDirty);
            if (this.encodingStyle != null) {
                clonedME.setEncodingStyle(this.encodingStyle);
            }
            return clonedME;
        }
        catch (Exception ex) {
            return null;
        }
    }

    public void setAllAttributes(Attributes attrs) {
        this.attributes = attrs;
    }

    public void detachAllChildren() {
        this.removeContents();
    }

    public Attributes getCompleteAttributes() {
        if (this.namespaces == null) {
            return this.attributes;
        }
        AttributesImpl attrs = null;
        attrs = this.attributes == NullAttributes.singleton ? new AttributesImpl() : new AttributesImpl(this.attributes);
        Iterator iterator = this.namespaces.iterator();
        while (iterator.hasNext()) {
            Mapping mapping = (Mapping)iterator.next();
            String prefix = mapping.getPrefix();
            String nsURI = mapping.getNamespaceURI();
            attrs.addAttribute("http://www.w3.org/2000/xmlns/", prefix, "xmlns:" + prefix, nsURI, "CDATA");
        }
        return attrs;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public QName getQName() {
        return new QName(this.namespaceURI, this.name);
    }

    public void setQName(QName qName) {
        this.name = qName.getLocalPart();
        this.namespaceURI = qName.getNamespaceURI();
    }

    public void setNamespaceURI(String nsURI) {
        this.namespaceURI = nsURI;
    }

    public QName getType() {
        MessageElement referent;
        if (this.typeQName == null && this.href != null && this.context != null && (referent = this.context.getElementByID(this.href)) != null) {
            this.typeQName = referent.getType();
        }
        return this.typeQName;
    }

    public void setType(QName qname) {
        this.typeQName = qname;
    }

    public SAX2EventRecorder getRecorder() {
        return this.recorder;
    }

    public void setRecorder(SAX2EventRecorder rec) {
        this.recorder = rec;
    }

    public String getEncodingStyle() {
        if (this.encodingStyle == null) {
            if (this.parent == null) {
                return "";
            }
            return ((MessageElement)this.parent).getEncodingStyle();
        }
        return this.encodingStyle;
    }

    public void removeContents() {
        if (this.children != null) {
            for (int i = 0; i < this.children.size(); ++i) {
                try {
                    ((NodeImpl)this.children.get(i)).setParent(null);
                    continue;
                }
                catch (SOAPException e) {
                    log.debug((Object)"ignoring", (Throwable)e);
                }
            }
            this.children.clear();
            this.setDirty(true);
        }
    }

    public Iterator getVisibleNamespacePrefixes() {
        Iterator mine;
        Iterator parentsPrefixes;
        Vector prefixes = new Vector();
        if (this.parent != null && (parentsPrefixes = ((MessageElement)this.parent).getVisibleNamespacePrefixes()) != null) {
            while (parentsPrefixes.hasNext()) {
                prefixes.add(parentsPrefixes.next());
            }
        }
        if ((mine = this.getNamespacePrefixes()) != null) {
            while (mine.hasNext()) {
                prefixes.add(mine.next());
            }
        }
        return prefixes.iterator();
    }

    public void setEncodingStyle(String encodingStyle) throws SOAPException {
        if (encodingStyle == null) {
            encodingStyle = "";
        }
        this.encodingStyle = encodingStyle;
        if (encodingStyle.equals("http://schemas.xmlsoap.org/soap/encoding/")) {
            this.addMapping(enc11Mapping);
        } else if (encodingStyle.equals("http://www.w3.org/2003/05/soap-encoding")) {
            this.addMapping(enc12Mapping);
        }
    }

    public void addChild(MessageElement el) throws SOAPException {
        if (this.objectValue != null) {
            IllegalStateException exc = new IllegalStateException(Messages.getMessage("valuePresent"));
            log.error((Object)Messages.getMessage("valuePresent"), (Throwable)exc);
            throw exc;
        }
        this.initializeChildren();
        this.children.add(el);
        el.parent = this;
    }

    public List getChildren() {
        return this.children;
    }

    public void setContentsIndex(int index) {
        this.startContentsIndex = index;
    }

    public void setNSMappings(ArrayList namespaces) {
        this.namespaces = namespaces;
    }

    public String getPrefix(String searchNamespaceURI) {
        if (searchNamespaceURI == null || "".equals(searchNamespaceURI)) {
            return null;
        }
        if (this.href != null && this.getRealElement() != null) {
            return this.getRealElement().getPrefix(searchNamespaceURI);
        }
        for (int i = 0; this.namespaces != null && i < this.namespaces.size(); ++i) {
            Mapping map = (Mapping)this.namespaces.get(i);
            if (!map.getNamespaceURI().equals(searchNamespaceURI)) continue;
            return map.getPrefix();
        }
        if (this.parent != null) {
            return ((MessageElement)this.parent).getPrefix(searchNamespaceURI);
        }
        return null;
    }

    public String getNamespaceURI(String searchPrefix) {
        if (searchPrefix == null) {
            searchPrefix = "";
        }
        if (this.href != null && this.getRealElement() != null) {
            return this.getRealElement().getNamespaceURI(searchPrefix);
        }
        for (int i = 0; this.namespaces != null && i < this.namespaces.size(); ++i) {
            Mapping map = (Mapping)this.namespaces.get(i);
            if (!map.getPrefix().equals(searchPrefix)) continue;
            return map.getNamespaceURI();
        }
        if (this.parent != null) {
            return ((MessageElement)this.parent).getNamespaceURI(searchPrefix);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)Messages.getMessage("noPrefix00", "" + this, searchPrefix));
        }
        return null;
    }

    public Object getObjectValue() {
        Object obj = null;
        try {
            obj = this.getObjectValue(null);
        }
        catch (Exception e) {
            log.debug((Object)"getValue()", (Throwable)e);
        }
        return obj;
    }

    public Object getObjectValue(Class cls) throws Exception {
        if (this.objectValue == null) {
            this.objectValue = this.getValueAsType(this.getType(), cls);
        }
        return this.objectValue;
    }

    public void setObjectValue(Object newValue) throws SOAPException {
        if (this.children != null && !this.children.isEmpty()) {
            SOAPException exc = new SOAPException(Messages.getMessage("childPresent"));
            log.error((Object)Messages.getMessage("childPresent"), (Throwable)exc);
            throw exc;
        }
        if (this.textRep != null) {
            SOAPException exc = new SOAPException(Messages.getMessage("xmlPresent"));
            log.error((Object)Messages.getMessage("xmlPresent"), (Throwable)exc);
            throw exc;
        }
        this.objectValue = newValue;
    }

    public Object getValueAsType(QName type) throws Exception {
        return this.getValueAsType(type, null);
    }

    public Object getValueAsType(QName type, Class cls) throws Exception {
        if (this.context == null) {
            throw new Exception(Messages.getMessage("noContext00"));
        }
        Deserializer dser = null;
        dser = cls == null ? this.context.getDeserializerForType(type) : this.context.getDeserializerForClass(cls);
        if (dser == null) {
            throw new Exception(Messages.getMessage("noDeser00", "" + type));
        }
        boolean oldVal = this.context.isDoneParsing();
        this.context.deserializing(true);
        this.context.pushElementHandler(new EnvelopeHandler((SOAPHandler)((Object)dser)));
        this.publishToHandler(this.context);
        this.context.deserializing(oldVal);
        return dser.getValue();
    }

    public void addAttribute(String namespace, String localName, QName value) {
        if (this.qNameAttrs == null) {
            this.qNameAttrs = new Vector();
        }
        QNameAttr attr = new QNameAttr();
        attr.name = new QName(namespace, localName);
        attr.value = value;
        this.qNameAttrs.addElement(attr);
    }

    public void addAttribute(String namespace, String localName, String value) {
        AttributesImpl attributes = this.makeAttributesEditable();
        attributes.addAttribute(namespace, localName, "", "CDATA", value);
    }

    public void addAttribute(String attrPrefix, String namespace, String localName, String value) {
        AttributesImpl attributes = this.makeAttributesEditable();
        String attrName = localName;
        if (attrPrefix != null && attrPrefix.length() > 0) {
            attrName = attrPrefix + ":" + localName;
        }
        attributes.addAttribute(namespace, localName, attrName, "CDATA", value);
    }

    public void setAttribute(String namespace, String localName, String value) {
        AttributesImpl attributes = this.makeAttributesEditable();
        int idx = attributes.getIndex(namespace, localName);
        if (idx > -1) {
            if (value != null) {
                attributes.setValue(idx, value);
            } else {
                attributes.removeAttribute(idx);
            }
            return;
        }
        this.addAttribute(namespace, localName, value);
    }

    public String getAttributeValue(String localName) {
        if (this.attributes == null) {
            return null;
        }
        return this.attributes.getValue(localName);
    }

    public void setEnvelope(SOAPEnvelope env) {
        env.setDirty(true);
        this.message = env;
    }

    public SOAPEnvelope getEnvelope() {
        return this.message;
    }

    public MessageElement getRealElement() {
        if (this.href == null) {
            return this;
        }
        Object obj = this.context.getObjectByRef(this.href);
        if (obj == null) {
            return null;
        }
        if (!(obj instanceof MessageElement)) {
            return null;
        }
        return (MessageElement)obj;
    }

    public Document getAsDocument() throws Exception {
        String elementString = this.getAsString();
        StringReader reader = new StringReader(elementString);
        Document doc = XMLUtils.newDocument(new InputSource(reader));
        if (doc == null) {
            throw new Exception(Messages.getMessage("noDoc00", elementString));
        }
        return doc;
    }

    public String getAsString() throws Exception {
        SerializationContext serializeContext = null;
        StringWriter writer = new StringWriter();
        MessageContext msgContext = this.context != null ? this.context.getMessageContext() : MessageContext.getCurrentContext();
        serializeContext = new SerializationContext(writer, msgContext);
        serializeContext.setSendDecl(false);
        this.setDirty(false);
        this.output(serializeContext);
        writer.close();
        return writer.getBuffer().toString();
    }

    public Element getAsDOM() throws Exception {
        return this.getAsDocument().getDocumentElement();
    }

    public void publishToHandler(ContentHandler handler) throws SAXException {
        if (this.recorder == null) {
            throw new SAXException(Messages.getMessage("noRecorder00"));
        }
        this.recorder.replay(this.startEventIndex, this.endEventIndex, handler);
    }

    public void publishContents(ContentHandler handler) throws SAXException {
        if (this.recorder == null) {
            throw new SAXException(Messages.getMessage("noRecorder00"));
        }
        this.recorder.replay(this.startContentsIndex, this.endEventIndex - 1, handler);
    }

    public final void output(SerializationContext outputContext) throws Exception {
        if (this.recorder != null && !this._isDirty) {
            this.recorder.replay(this.startEventIndex, this.endEventIndex, new SAXOutputter(outputContext));
            return;
        }
        if (this.qNameAttrs != null) {
            for (int i = 0; i < this.qNameAttrs.size(); ++i) {
                QNameAttr attr = (QNameAttr)this.qNameAttrs.get(i);
                QName attrName = attr.name;
                this.setAttribute(attrName.getNamespaceURI(), attrName.getLocalPart(), outputContext.qName2String(attr.value));
            }
        }
        if (this.encodingStyle != null) {
            SOAPConstants soapConstants;
            MessageContext mc = outputContext.getMessageContext();
            SOAPConstants sOAPConstants = soapConstants = mc != null ? mc.getSOAPConstants() : SOAPConstants.SOAP11_CONSTANTS;
            if (this.parent == null) {
                if (!"".equals(this.encodingStyle)) {
                    this.setAttribute(soapConstants.getEnvelopeURI(), "encodingStyle", this.encodingStyle);
                }
            } else if (!this.encodingStyle.equals(((MessageElement)this.parent).getEncodingStyle())) {
                this.setAttribute(soapConstants.getEnvelopeURI(), "encodingStyle", this.encodingStyle);
            }
        }
        this.outputImpl(outputContext);
    }

    protected void outputImpl(SerializationContext outputContext) throws Exception {
        if (this.textRep != null) {
            boolean oldPretty = outputContext.getPretty();
            outputContext.setPretty(false);
            if (this.textRep instanceof CDATASection) {
                outputContext.writeString("<![CDATA[");
                outputContext.writeString(this.textRep.getData());
                outputContext.writeString("]]>");
            } else if (this.textRep instanceof Comment) {
                outputContext.writeString("<!--");
                outputContext.writeString(this.textRep.getData());
                outputContext.writeString("-->");
            } else if (this.textRep instanceof org.w3c.dom.Text) {
                outputContext.writeSafeString(this.textRep.getData());
            }
            outputContext.setPretty(oldPretty);
            return;
        }
        if (this.prefix != null) {
            outputContext.registerPrefixForURI(this.prefix, this.namespaceURI);
        }
        if (this.namespaces != null) {
            Iterator i = this.namespaces.iterator();
            while (i.hasNext()) {
                Mapping mapping = (Mapping)i.next();
                outputContext.registerPrefixForURI(mapping.getPrefix(), mapping.getNamespaceURI());
            }
        }
        if (this.objectValue != null) {
            outputContext.serialize(new QName(this.namespaceURI, this.name), this.attributes, this.objectValue);
            return;
        }
        outputContext.startElement(new QName(this.namespaceURI, this.name), this.attributes);
        if (this.children != null) {
            Iterator it = this.children.iterator();
            while (it.hasNext()) {
                ((NodeImpl)it.next()).output(outputContext);
            }
        }
        outputContext.endElement();
    }

    public String toString() {
        try {
            return this.getAsString();
        }
        catch (Exception exp) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)exp);
            return super.toString();
        }
    }

    public void addMapping(Mapping map) {
        if (this.namespaces == null) {
            this.namespaces = new ArrayList();
        }
        this.namespaces.add(map);
    }

    public SOAPElement addChildElement(Name childName) throws SOAPException {
        MessageElement child = new MessageElement(childName.getLocalName(), childName.getPrefix(), childName.getURI());
        this.addChild(child);
        return child;
    }

    public SOAPElement addChildElement(String localName) throws SOAPException {
        MessageElement child = new MessageElement(this.getNamespaceURI(), localName);
        this.addChild(child);
        return child;
    }

    public SOAPElement addChildElement(String localName, String prefixName) throws SOAPException {
        MessageElement child = new MessageElement(this.getNamespaceURI(prefixName), localName);
        child.setPrefix(prefixName);
        this.addChild(child);
        return child;
    }

    public SOAPElement addChildElement(String localName, String childPrefix, String uri) throws SOAPException {
        MessageElement child = new MessageElement(uri, localName);
        child.setPrefix(childPrefix);
        child.addNamespaceDeclaration(childPrefix, uri);
        this.addChild(child);
        return child;
    }

    public SOAPElement addChildElement(SOAPElement element) throws SOAPException {
        try {
            this.addChild((MessageElement)element);
            this.setDirty(true);
            return element;
        }
        catch (ClassCastException e) {
            throw new SOAPException(e);
        }
    }

    public SOAPElement addTextNode(String s) throws SOAPException {
        try {
            org.w3c.dom.Text text = this.getOwnerDocument().createTextNode(s);
            ((Text)text).setParentElement(this);
            return this;
        }
        catch (IncompatibleClassChangeError e) {
            Text text = new Text(s);
            this.appendChild(text);
            return this;
        }
        catch (ClassCastException e) {
            throw new SOAPException(e);
        }
    }

    public SOAPElement addAttribute(Name attrName, String value) throws SOAPException {
        try {
            this.addAttribute(attrName.getPrefix(), attrName.getURI(), attrName.getLocalName(), value);
        }
        catch (RuntimeException t) {
            throw new SOAPException(t);
        }
        return this;
    }

    public SOAPElement addNamespaceDeclaration(String prefix, String uri) throws SOAPException {
        try {
            Mapping map = new Mapping(uri, prefix);
            this.addMapping(map);
        }
        catch (RuntimeException t) {
            throw new SOAPException(t);
        }
        return this;
    }

    public String getAttributeValue(Name attrName) {
        return this.attributes.getValue(attrName.getURI(), attrName.getLocalName());
    }

    public Iterator getAllAttributes() {
        int num = this.attributes.getLength();
        Vector<PrefixedQName> attrs = new Vector<PrefixedQName>(num);
        for (int i = 0; i < num; ++i) {
            String q = this.attributes.getQName(i);
            String prefix = "";
            if (q != null) {
                int idx = q.indexOf(":");
                prefix = idx > 0 ? q.substring(0, idx) : "";
            }
            attrs.add(new PrefixedQName(this.attributes.getURI(i), this.attributes.getLocalName(i), prefix));
        }
        return attrs.iterator();
    }

    public Iterator getNamespacePrefixes() {
        Vector<String> prefixes = new Vector<String>();
        for (int i = 0; this.namespaces != null && i < this.namespaces.size(); ++i) {
            prefixes.add(((Mapping)this.namespaces.get(i)).getPrefix());
        }
        return prefixes.iterator();
    }

    public Name getElementName() {
        return new PrefixedQName(this.getNamespaceURI(), this.getName(), this.getPrefix());
    }

    public boolean removeAttribute(Name attrName) {
        AttributesImpl attributes = this.makeAttributesEditable();
        boolean removed = false;
        for (int i = 0; i < attributes.getLength() && !removed; ++i) {
            if (!attributes.getURI(i).equals(attrName.getURI()) || !attributes.getLocalName(i).equals(attrName.getLocalName())) continue;
            attributes.removeAttribute(i);
            removed = true;
        }
        return removed;
    }

    public boolean removeNamespaceDeclaration(String namespacePrefix) {
        this.makeAttributesEditable();
        boolean removed = false;
        for (int i = 0; this.namespaces != null && i < this.namespaces.size() && !removed; ++i) {
            if (!((Mapping)this.namespaces.get(i)).getPrefix().equals(namespacePrefix)) continue;
            this.namespaces.remove(i);
            removed = true;
        }
        return removed;
    }

    public Iterator getChildElements() {
        this.initializeChildren();
        return this.children.iterator();
    }

    public MessageElement getChildElement(QName qname) {
        if (this.children != null) {
            Iterator i = this.children.iterator();
            while (i.hasNext()) {
                MessageElement child = (MessageElement)i.next();
                if (!child.getQName().equals(qname)) continue;
                return child;
            }
        }
        return null;
    }

    public Iterator getChildElements(QName qname) {
        this.initializeChildren();
        int num = this.children.size();
        Vector<MessageElement> c = new Vector<MessageElement>(num);
        for (int i = 0; i < num; ++i) {
            MessageElement child = (MessageElement)this.children.get(i);
            Name cname = child.getElementName();
            if (!cname.getURI().equals(qname.getNamespaceURI()) || !cname.getLocalName().equals(qname.getLocalPart())) continue;
            c.add(child);
        }
        return c.iterator();
    }

    public Iterator getChildElements(Name childName) {
        return this.getChildElements(new QName(childName.getURI(), childName.getLocalName()));
    }

    public String getTagName() {
        return this.prefix == null ? this.name : this.prefix + ":" + this.name;
    }

    public void removeAttribute(String attrName) throws DOMException {
        AttributesImpl impl = (AttributesImpl)this.attributes;
        int index = impl.getIndex(attrName);
        if (index >= 0) {
            AttributesImpl newAttrs = new AttributesImpl();
            for (int i = 0; i < impl.getLength(); ++i) {
                if (i == index) continue;
                String uri = impl.getURI(i);
                String local = impl.getLocalName(i);
                String qname = impl.getQName(i);
                String type = impl.getType(i);
                String value = impl.getValue(i);
                newAttrs.addAttribute(uri, local, qname, type, value);
            }
            this.attributes = newAttrs;
        }
    }

    public boolean hasAttribute(String attrName) {
        if (attrName == null) {
            attrName = "";
        }
        for (int i = 0; i < this.attributes.getLength(); ++i) {
            if (!attrName.equals(this.attributes.getQName(i))) continue;
            return true;
        }
        return false;
    }

    public String getAttribute(String attrName) {
        return this.attributes.getValue(attrName);
    }

    public void removeAttributeNS(String namespace, String localName) throws DOMException {
        this.makeAttributesEditable();
        PrefixedQName name = new PrefixedQName(namespace, localName, null);
        this.removeAttribute(name);
    }

    public void setAttribute(String name, String value) throws DOMException {
        AttributesImpl impl = this.makeAttributesEditable();
        int index = impl.getIndex(name);
        if (index < 0) {
            String uri = "";
            String localname = name;
            String qname = name;
            String type = "CDDATA";
            impl.addAttribute(uri, localname, qname, type, value);
        } else {
            impl.setLocalName(index, value);
        }
    }

    public boolean hasAttributeNS(String namespace, String localName) {
        if (namespace == null) {
            namespace = "";
        }
        if (localName == null) {
            localName = "";
        }
        for (int i = 0; i < this.attributes.getLength(); ++i) {
            if (!namespace.equals(this.attributes.getURI(i)) || !localName.equals(this.attributes.getLocalName(i))) continue;
            return true;
        }
        return false;
    }

    public Attr getAttributeNode(String attrName) {
        return null;
    }

    public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
        this.makeAttributesEditable();
        PrefixedQName name = new PrefixedQName(oldAttr.getNamespaceURI(), oldAttr.getLocalName(), oldAttr.getPrefix());
        this.removeAttribute(name);
        return oldAttr;
    }

    public Attr setAttributeNode(Attr newAttr) throws DOMException {
        return newAttr;
    }

    public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
        AttributesImpl attributes = this.makeAttributesEditable();
        attributes.addAttribute(newAttr.getNamespaceURI(), newAttr.getLocalName(), newAttr.getLocalName(), "CDATA", newAttr.getValue());
        return null;
    }

    public NodeList getElementsByTagName(String tagName) {
        NodeListImpl nodelist = new NodeListImpl();
        for (int i = 0; this.children != null && i < this.children.size(); ++i) {
            if (!(this.children.get(i) instanceof Node)) continue;
            Node el = (Node)this.children.get(i);
            if (el.getLocalName() != null && el.getLocalName().equals(tagName)) {
                nodelist.addNode(el);
            }
            if (!(el instanceof Element)) continue;
            NodeList grandchildren = ((Element)el).getElementsByTagName(tagName);
            for (int j = 0; j < grandchildren.getLength(); ++j) {
                nodelist.addNode(grandchildren.item(j));
            }
        }
        return nodelist;
    }

    public String getAttributeNS(String namespaceURI, String localName) {
        if (namespaceURI == null) {
            namespaceURI = "";
        }
        for (int i = 0; i < this.attributes.getLength(); ++i) {
            if (!this.attributes.getURI(i).equals(namespaceURI) || !this.attributes.getLocalName(i).equals(localName)) continue;
            return this.attributes.getValue(i);
        }
        return null;
    }

    public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
        AttributesImpl attributes = this.makeAttributesEditable();
        String localName = qualifiedName.substring(qualifiedName.indexOf(":") + 1, qualifiedName.length());
        if (namespaceURI == null) {
            namespaceURI = "intentionalNullURI";
        }
        attributes.addAttribute(namespaceURI, localName, qualifiedName, "CDATA", value);
    }

    public Attr getAttributeNodeNS(String namespace, String localName) {
        return null;
    }

    public NodeList getElementsByTagNameNS(String namespace, String localName) {
        return this.getElementsNS(this, namespace, localName);
    }

    protected NodeList getElementsNS(Element parentElement, String namespace, String localName) {
        NodeList children = parentElement.getChildNodes();
        NodeListImpl matches = new NodeListImpl();
        for (int i = 0; i < children.getLength(); ++i) {
            if (children.item(i) instanceof org.w3c.dom.Text) continue;
            Element child = (Element)children.item(i);
            if (namespace.equals(child.getNamespaceURI()) && localName.equals(child.getLocalName())) {
                matches.addNode(child);
            }
            matches.addNodeList(child.getElementsByTagNameNS(namespace, localName));
        }
        return matches;
    }

    public Node item(int index) {
        if (this.children != null && this.children.size() > index) {
            return (Node)this.children.get(index);
        }
        return null;
    }

    public int getLength() {
        return this.children == null ? 0 : this.children.size();
    }

    protected MessageElement findElement(Vector vec, String namespace, String localPart) {
        if (vec.isEmpty()) {
            return null;
        }
        QName qname = new QName(namespace, localPart);
        Enumeration e = vec.elements();
        while (e.hasMoreElements()) {
            MessageElement element = (MessageElement)e.nextElement();
            if (!element.getQName().equals(qname)) continue;
            return element;
        }
        return null;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof MessageElement)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!this.getLocalName().equals(((MessageElement)obj).getLocalName())) {
            return false;
        }
        return this.toString().equals(obj.toString());
    }

    private void copyNode(Node element) {
        this.copyNode(this, element);
    }

    private void copyNode(MessageElement dest, Node source) {
        dest.setPrefix(source.getPrefix());
        if (source.getLocalName() != null) {
            dest.setQName(new QName(source.getNamespaceURI(), source.getLocalName()));
        } else {
            dest.setQName(new QName(source.getNamespaceURI(), source.getNodeName()));
        }
        NamedNodeMap attrs = source.getAttributes();
        for (int i = 0; i < attrs.getLength(); ++i) {
            Node att = attrs.item(i);
            if (att.getNamespaceURI() != null && att.getPrefix() != null && att.getNamespaceURI().equals("http://www.w3.org/2000/xmlns/") && "xmlns".equals(att.getPrefix())) {
                Mapping map = new Mapping(att.getNodeValue(), att.getLocalName());
                dest.addMapping(map);
            }
            if (att.getLocalName() != null) {
                dest.addAttribute(att.getPrefix(), att.getNamespaceURI() != null ? att.getNamespaceURI() : "", att.getLocalName(), att.getNodeValue());
                continue;
            }
            if (att.getNodeName() == null) continue;
            dest.addAttribute(att.getPrefix(), att.getNamespaceURI() != null ? att.getNamespaceURI() : "", att.getNodeName(), att.getNodeValue());
        }
        NodeList children = source.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            NodeImpl childElement;
            Node child = children.item(i);
            if (child.getNodeType() == 3 || child.getNodeType() == 4 || child.getNodeType() == 8) {
                childElement = new Text((CharacterData)child);
                dest.appendChild(childElement);
                continue;
            }
            childElement = new MessageElement();
            dest.appendChild(childElement);
            this.copyNode((MessageElement)childElement, child);
        }
    }

    public String getValue() {
        if (this.textRep != null) {
            return this.textRep.getNodeValue();
        }
        if (this.objectValue != null) {
            return this.getValueDOM();
        }
        Iterator i = this.getChildElements();
        while (i.hasNext()) {
            NodeImpl n = (NodeImpl)i.next();
            if (!(n instanceof org.w3c.dom.Text)) continue;
            return ((org.w3c.dom.Text)((Object)n)).getNodeValue();
        }
        return null;
    }

    protected String getValueDOM() {
        try {
            Node node;
            Element element = this.getAsDOM();
            if (element.hasChildNodes() && (node = element.getFirstChild()).getNodeType() == 3) {
                return node.getNodeValue();
            }
        }
        catch (Exception t) {
            log.debug((Object)"getValue()", (Throwable)t);
        }
        return null;
    }

    public void setValue(String value) {
        if (this.children == null) {
            try {
                this.setObjectValue(value);
            }
            catch (SOAPException soape) {
                log.debug((Object)"setValue()", (Throwable)soape);
            }
        }
        super.setValue(value);
    }

    public Document getOwnerDocument() {
        Document doc = null;
        if (this.context != null && this.context.getEnvelope() != null && this.context.getEnvelope().getOwnerDocument() != null) {
            doc = this.context.getEnvelope().getOwnerDocument();
        }
        if (doc == null) {
            doc = super.getOwnerDocument();
        }
        if (doc == null) {
            doc = new SOAPDocumentImpl(null);
        }
        return doc;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    protected static class QNameAttr {
        public QName name;
        public QName value;

        protected QNameAttr() {
        }
    }
}

