/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.encoding;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;
import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.holders.QNameHolder;
import org.apache.axis.AxisProperties;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.attachments.Attachments;
import org.apache.axis.components.encoding.XMLEncoder;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.constants.Use;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.DefaultTypeMappingImpl;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.SerializerFactory;
import org.apache.axis.encoding.SimpleValueSerializer;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.encoding.ser.BaseSerializerFactory;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.schema.SchemaVersion;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.IDKey;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Mapping;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.NSStack;
import org.apache.axis.utils.XMLUtils;
import org.apache.axis.utils.cache.MethodCache;
import org.apache.axis.wsdl.symbolTable.SchemaUtils;
import org.apache.axis.wsdl.symbolTable.Utils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public class SerializationContext
implements javax.xml.rpc.encoding.SerializationContext {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$SerializationContext == null ? (class$org$apache$axis$encoding$SerializationContext = SerializationContext.class$("org.apache.axis.encoding.SerializationContext")) : class$org$apache$axis$encoding$SerializationContext).getName());
    private final boolean debugEnabled = log.isDebugEnabled();
    private NSStack nsStack = null;
    private boolean writingStartTag = false;
    private boolean onlyXML = true;
    private int indent = 0;
    private Stack elementStack = new Stack();
    private Writer writer;
    private int lastPrefixIndex = 1;
    private MessageContext msgContext;
    private QName currentXMLType;
    private QName itemQName;
    private QName itemType;
    private SOAPConstants soapConstants = SOAPConstants.SOAP11_CONSTANTS;
    private static QName multirefQName = new QName("", "multiRef");
    private static Class[] SERIALIZER_CLASSES = new Class[]{class$java$lang$String == null ? (class$java$lang$String = SerializationContext.class$("java.lang.String")) : class$java$lang$String, class$java$lang$Class == null ? (class$java$lang$Class = SerializationContext.class$("java.lang.Class")) : class$java$lang$Class, class$javax$xml$namespace$QName == null ? (class$javax$xml$namespace$QName = SerializationContext.class$("javax.xml.namespace.QName")) : class$javax$xml$namespace$QName};
    private static final String SERIALIZER_METHOD = "getSerializer";
    private boolean doMultiRefs = false;
    private boolean disablePrettyXML = false;
    private boolean enableNamespacePrefixOptimization = false;
    private boolean pretty = false;
    private boolean sendXMLDecl = true;
    private boolean sendXSIType = true;
    private Boolean sendNull = Boolean.TRUE;
    private HashMap multiRefValues = null;
    private int multiRefIndex = -1;
    private boolean noNamespaceMappings = true;
    private QName writeXMLType;
    private XMLEncoder encoder = null;
    protected boolean startOfDocument = true;
    private String encoding = "UTF-8";
    private HashSet secondLevelObjects = null;
    private Object forceSer = null;
    private boolean outputMultiRefsFlag = false;
    SchemaVersion schemaVersion = SchemaVersion.SCHEMA_2001;
    HashMap preferredPrefixes = new HashMap();
    static /* synthetic */ Class class$org$apache$axis$encoding$SerializationContext;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$lang$Class;
    static /* synthetic */ Class class$javax$xml$namespace$QName;
    static /* synthetic */ Class class$java$util$Calendar;
    static /* synthetic */ Class class$java$util$Date;
    static /* synthetic */ Class class$org$apache$axis$types$HexBinary;
    static /* synthetic */ Class class$org$w3c$dom$Element;
    static /* synthetic */ Class array$B;

    public SerializationContext(Writer writer) {
        this.writer = writer;
        this.initialize();
    }

    private void initialize() {
        this.preferredPrefixes.put(this.soapConstants.getEncodingURI(), "soapenc");
        this.preferredPrefixes.put("http://www.w3.org/XML/1998/namespace", "xml");
        this.preferredPrefixes.put(this.schemaVersion.getXsdURI(), "xsd");
        this.preferredPrefixes.put(this.schemaVersion.getXsiURI(), "xsi");
        this.preferredPrefixes.put(this.soapConstants.getEnvelopeURI(), "soapenv");
        this.nsStack = new NSStack(this.enableNamespacePrefixOptimization);
    }

    public SerializationContext(Writer writer, MessageContext msgContext) {
        this.writer = writer;
        this.msgContext = msgContext;
        if (msgContext != null) {
            Boolean shouldDisableNamespacePrefixOptimization;
            Boolean shouldDisablePrettyXML;
            Boolean shouldSendMultiRefs;
            this.soapConstants = msgContext.getSOAPConstants();
            this.schemaVersion = msgContext.getSchemaVersion();
            Boolean shouldSendDecl = (Boolean)msgContext.getProperty("sendXMLDeclaration");
            if (shouldSendDecl != null) {
                this.sendXMLDecl = shouldSendDecl;
            }
            if ((shouldSendMultiRefs = (Boolean)msgContext.getProperty("sendMultiRefs")) != null) {
                this.doMultiRefs = shouldSendMultiRefs;
            }
            if ((shouldDisablePrettyXML = (Boolean)msgContext.getProperty("disablePrettyXML")) != null) {
                this.disablePrettyXML = shouldDisablePrettyXML;
            }
            this.enableNamespacePrefixOptimization = (shouldDisableNamespacePrefixOptimization = (Boolean)msgContext.getProperty("enableNamespacePrefixOptimization")) != null ? shouldDisableNamespacePrefixOptimization : JavaUtils.isTrue(AxisProperties.getProperty("enableNamespacePrefixOptimization", "true"));
            boolean sendTypesDefault = this.sendXSIType;
            OperationDesc operation = msgContext.getOperation();
            if (operation != null) {
                if (operation.getUse() != Use.ENCODED) {
                    this.doMultiRefs = false;
                    sendTypesDefault = false;
                }
            } else {
                SOAPService service = msgContext.getService();
                if (service != null && service.getUse() != Use.ENCODED) {
                    this.doMultiRefs = false;
                    sendTypesDefault = false;
                }
            }
            if (!msgContext.isPropertyTrue("sendXsiTypes", sendTypesDefault)) {
                this.sendXSIType = false;
            }
        } else {
            this.enableNamespacePrefixOptimization = JavaUtils.isTrue(AxisProperties.getProperty("enableNamespacePrefixOptimization", "true"));
        }
        this.initialize();
    }

    public boolean getPretty() {
        return this.pretty;
    }

    public void setPretty(boolean pretty) {
        if (!this.disablePrettyXML) {
            this.pretty = pretty;
        }
    }

    public boolean getDoMultiRefs() {
        return this.doMultiRefs;
    }

    public void setDoMultiRefs(boolean shouldDo) {
        this.doMultiRefs = shouldDo;
    }

    public void setSendDecl(boolean sendDecl) {
        this.sendXMLDecl = sendDecl;
    }

    public boolean shouldSendXSIType() {
        return this.sendXSIType;
    }

    public TypeMapping getTypeMapping() {
        if (this.msgContext == null) {
            return DefaultTypeMappingImpl.getSingletonDelegate();
        }
        String encodingStyle = this.msgContext.getEncodingStyle();
        if (encodingStyle == null) {
            encodingStyle = this.soapConstants.getEncodingURI();
        }
        return (TypeMapping)this.msgContext.getTypeMappingRegistry().getTypeMapping(encodingStyle);
    }

    public TypeMappingRegistry getTypeMappingRegistry() {
        if (this.msgContext == null) {
            return null;
        }
        return this.msgContext.getTypeMappingRegistry();
    }

    public String getPrefixForURI(String uri) {
        return this.getPrefixForURI(uri, null, false);
    }

    public String getPrefixForURI(String uri, String defaultPrefix) {
        return this.getPrefixForURI(uri, defaultPrefix, false);
    }

    public String getPrefixForURI(String uri, String defaultPrefix, boolean attribute) {
        if (uri == null || uri.length() == 0) {
            return null;
        }
        String prefix = this.nsStack.getPrefix(uri, attribute);
        if (prefix == null) {
            prefix = (String)this.preferredPrefixes.get(uri);
            if (prefix == null) {
                if (defaultPrefix == null) {
                    prefix = "ns" + this.lastPrefixIndex++;
                    while (this.nsStack.getNamespaceURI(prefix) != null) {
                        prefix = "ns" + this.lastPrefixIndex++;
                    }
                } else {
                    prefix = defaultPrefix;
                }
            }
            this.registerPrefixForURI(prefix, uri);
        }
        return prefix;
    }

    public void registerPrefixForURI(String prefix, String uri) {
        if (this.debugEnabled) {
            log.debug((Object)Messages.getMessage("register00", prefix, uri));
        }
        if (uri != null && prefix != null) {
            String activePrefix;
            if (this.noNamespaceMappings) {
                this.nsStack.push();
                this.noNamespaceMappings = false;
            }
            if ((activePrefix = this.nsStack.getPrefix(uri, true)) == null || !activePrefix.equals(prefix)) {
                this.nsStack.add(uri, prefix);
            }
        }
    }

    public Message getCurrentMessage() {
        if (this.msgContext == null) {
            return null;
        }
        return this.msgContext.getCurrentMessage();
    }

    public MessageContext getMessageContext() {
        return this.msgContext;
    }

    public String getEncodingStyle() {
        return this.msgContext == null ? Use.DEFAULT.getEncoding() : this.msgContext.getEncodingStyle();
    }

    public boolean isEncoded() {
        return Constants.isSOAP_ENC(this.getEncodingStyle());
    }

    public String qName2String(QName qName, boolean writeNS) {
        String prefix = null;
        String namespaceURI = qName.getNamespaceURI();
        String localPart = qName.getLocalPart();
        if (localPart != null && localPart.length() > 0) {
            int index = localPart.indexOf(58);
            if (index != -1) {
                prefix = localPart.substring(0, index);
                if (prefix.length() > 0 && !prefix.equals("urn")) {
                    this.registerPrefixForURI(prefix, namespaceURI);
                    localPart = localPart.substring(index + 1);
                } else {
                    prefix = null;
                }
            }
            localPart = Utils.getLastLocalPart(localPart);
        }
        if (namespaceURI.length() == 0) {
            String defaultNS;
            if (writeNS && (defaultNS = this.nsStack.getNamespaceURI("")) != null && defaultNS.length() > 0) {
                this.registerPrefixForURI("", "");
            }
        } else {
            prefix = this.getPrefixForURI(namespaceURI);
        }
        if (prefix == null || prefix.length() == 0) {
            return localPart;
        }
        return prefix + ':' + localPart;
    }

    public String qName2String(QName qName) {
        return this.qName2String(qName, false);
    }

    public String attributeQName2String(QName qName) {
        String prefix = null;
        String uri = qName.getNamespaceURI();
        if (uri.length() > 0) {
            prefix = this.getPrefixForURI(uri, null, true);
        }
        if (prefix == null || prefix.length() == 0) {
            return qName.getLocalPart();
        }
        return prefix + ':' + qName.getLocalPart();
    }

    public QName getQNameForClass(Class cls) {
        return this.getTypeMapping().getTypeQName(cls);
    }

    public boolean isPrimitive(Object value) {
        if (value == null) {
            return true;
        }
        Class<?> javaType = value.getClass();
        if (javaType.isPrimitive()) {
            return true;
        }
        if (javaType == (class$java$lang$String == null ? (class$java$lang$String = SerializationContext.class$("java.lang.String")) : class$java$lang$String)) {
            return true;
        }
        if ((class$java$util$Calendar == null ? (class$java$util$Calendar = SerializationContext.class$("java.util.Calendar")) : class$java$util$Calendar).isAssignableFrom(javaType)) {
            return true;
        }
        if ((class$java$util$Date == null ? (class$java$util$Date = SerializationContext.class$("java.util.Date")) : class$java$util$Date).isAssignableFrom(javaType)) {
            return true;
        }
        if ((class$org$apache$axis$types$HexBinary == null ? (class$org$apache$axis$types$HexBinary = SerializationContext.class$("org.apache.axis.types.HexBinary")) : class$org$apache$axis$types$HexBinary).isAssignableFrom(javaType)) {
            return true;
        }
        if ((class$org$w3c$dom$Element == null ? (class$org$w3c$dom$Element = SerializationContext.class$("org.w3c.dom.Element")) : class$org$w3c$dom$Element).isAssignableFrom(javaType)) {
            return true;
        }
        if (javaType == (array$B == null ? (array$B = SerializationContext.class$("[B")) : array$B)) {
            return true;
        }
        if (javaType.isArray()) {
            return true;
        }
        QName qName = this.getQNameForClass(javaType);
        return qName != null && Constants.isSchemaXSD(qName.getNamespaceURI()) && SchemaUtils.isSimpleSchemaType(qName);
    }

    public void serialize(QName elemQName, Attributes attributes, Object value) throws IOException {
        this.serialize(elemQName, attributes, value, null, null, null);
    }

    public void serialize(QName elemQName, Attributes attributes, Object value, QName xmlType) throws IOException {
        this.serialize(elemQName, attributes, value, xmlType, null, null);
    }

    public void serialize(QName elemQName, Attributes attributes, Object value, QName xmlType, boolean sendNull, Boolean sendType) throws IOException {
        this.serialize(elemQName, attributes, value, xmlType, sendNull ? Boolean.TRUE : Boolean.FALSE, sendType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void serialize(QName elemQName, Attributes attributes, Object value, QName xmlType, Boolean sendNull, Boolean sendType) throws IOException {
        boolean sendXSITypeCache = this.sendXSIType;
        if (sendType != null) {
            this.sendXSIType = sendType;
        }
        boolean shouldSendType = this.shouldSendXSIType();
        try {
            Attachments attachments;
            Boolean sendNullCache = this.sendNull;
            if (sendNull != null) {
                this.sendNull = sendNull;
            } else {
                sendNull = this.sendNull;
            }
            if (value == null) {
                if (this.sendNull.booleanValue()) {
                    AttributesImpl attrs = new AttributesImpl();
                    if (attributes != null && 0 < attributes.getLength()) {
                        attrs.setAttributes(attributes);
                    }
                    if (shouldSendType) {
                        attrs = (AttributesImpl)this.setTypeAttribute(attrs, xmlType);
                    }
                    String nil = this.schemaVersion.getNilQName().getLocalPart();
                    attrs.addAttribute(this.schemaVersion.getXsiURI(), nil, "xsi:" + nil, "CDATA", "true");
                    this.startElement(elemQName, attrs);
                    this.endElement();
                }
                this.sendNull = sendNullCache;
                return;
            }
            Message msg = this.getCurrentMessage();
            if (null != msg && null != (attachments = this.getCurrentMessage().getAttachmentsImpl()) && attachments.isAttachment(value)) {
                this.serializeActual(elemQName, attributes, value, xmlType, sendType);
                this.sendNull = sendNullCache;
                return;
            }
            if (this.doMultiRefs && this.isEncoded() && value != this.forceSer && !this.isPrimitive(value)) {
                String id;
                MultiRefItem mri;
                if (this.multiRefIndex == -1) {
                    this.multiRefValues = new HashMap();
                }
                if ((mri = (MultiRefItem)this.multiRefValues.get(this.getIdentityKey(value))) == null) {
                    ++this.multiRefIndex;
                    id = "id" + this.multiRefIndex;
                    mri = new MultiRefItem(id, xmlType, sendType, value);
                    this.multiRefValues.put(this.getIdentityKey(value), mri);
                    if (this.soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
                        AttributesImpl attrs = new AttributesImpl();
                        if (attributes != null && 0 < attributes.getLength()) {
                            attrs.setAttributes(attributes);
                        }
                        attrs.addAttribute("", "id", "id", "CDATA", id);
                        this.serializeActual(elemQName, attrs, value, xmlType, sendType);
                        this.sendNull = sendNullCache;
                        return;
                    }
                    if (this.outputMultiRefsFlag) {
                        if (this.secondLevelObjects == null) {
                            this.secondLevelObjects = new HashSet();
                        }
                        this.secondLevelObjects.add(this.getIdentityKey(value));
                    }
                } else {
                    id = mri.id;
                }
                AttributesImpl attrs = new AttributesImpl();
                if (attributes != null && 0 < attributes.getLength()) {
                    attrs.setAttributes(attributes);
                }
                attrs.addAttribute("", this.soapConstants.getAttrHref(), this.soapConstants.getAttrHref(), "CDATA", '#' + id);
                this.startElement(elemQName, attrs);
                this.endElement();
                this.sendNull = sendNullCache;
                return;
            }
            if (value == this.forceSer) {
                this.forceSer = null;
            }
            this.serializeActual(elemQName, attributes, value, xmlType, sendType);
        }
        finally {
            this.sendXSIType = sendXSITypeCache;
        }
    }

    private IDKey getIdentityKey(Object value) {
        return new IDKey(value);
    }

    public void outputMultiRefs() throws IOException {
        if (!this.doMultiRefs || this.multiRefValues == null || this.soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
            return;
        }
        this.outputMultiRefsFlag = true;
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", "", "", "", "");
        String encodingURI = this.soapConstants.getEncodingURI();
        String prefix = this.getPrefixForURI(encodingURI);
        String root = prefix + ":root";
        attrs.addAttribute(encodingURI, "root", root, "CDATA", "0");
        String encodingStyle = this.msgContext != null ? this.msgContext.getEncodingStyle() : this.soapConstants.getEncodingURI();
        String encStyle = this.getPrefixForURI(this.soapConstants.getEnvelopeURI()) + ':' + "encodingStyle";
        attrs.addAttribute(this.soapConstants.getEnvelopeURI(), "encodingStyle", encStyle, "CDATA", encodingStyle);
        HashSet keys = new HashSet();
        keys.addAll(this.multiRefValues.keySet());
        Iterator i = keys.iterator();
        while (i.hasNext()) {
            while (i.hasNext()) {
                AttributesImpl attrs2 = new AttributesImpl(attrs);
                Object val = i.next();
                MultiRefItem mri = (MultiRefItem)this.multiRefValues.get(val);
                attrs2.setAttribute(0, "", "id", "id", "CDATA", mri.id);
                this.forceSer = mri.value;
                this.serialize(multirefQName, (Attributes)attrs2, mri.value, mri.xmlType, this.sendNull, Boolean.TRUE);
            }
            if (this.secondLevelObjects == null) continue;
            i = this.secondLevelObjects.iterator();
            this.secondLevelObjects = null;
        }
        this.forceSer = null;
        this.outputMultiRefsFlag = false;
        this.multiRefValues = null;
        this.multiRefIndex = -1;
        this.secondLevelObjects = null;
    }

    public void writeXMLDeclaration() throws IOException {
        this.writer.write("<?xml version=\"1.0\" encoding=\"");
        this.writer.write(this.encoding);
        this.writer.write("\"?>");
        this.startOfDocument = false;
    }

    public void startElement(QName qName, Attributes attributes) throws IOException {
        ArrayList<String> vecQNames = null;
        if (this.debugEnabled) {
            log.debug((Object)Messages.getMessage("startElem00", "[" + qName.getNamespaceURI() + "]:" + qName.getLocalPart()));
        }
        if (this.startOfDocument && this.sendXMLDecl) {
            this.writeXMLDeclaration();
        }
        if (this.writingStartTag) {
            this.writer.write(62);
            if (this.pretty) {
                this.writer.write(10);
            }
            ++this.indent;
        }
        if (this.pretty) {
            for (int i = 0; i < this.indent; ++i) {
                this.writer.write(32);
            }
        }
        String elementQName = this.qName2String(qName, true);
        this.writer.write(60);
        this.writer.write(elementQName);
        if (this.writeXMLType != null) {
            attributes = this.setTypeAttribute(attributes, this.writeXMLType);
            this.writeXMLType = null;
        }
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); ++i) {
                String qname = attributes.getQName(i);
                this.writer.write(32);
                String prefix = "";
                String uri = attributes.getURI(i);
                if (uri != null && uri.length() > 0) {
                    if (qname.length() == 0) {
                        prefix = this.getPrefixForURI(uri);
                    } else {
                        int idx = qname.indexOf(58);
                        if (idx > -1) {
                            prefix = qname.substring(0, idx);
                            prefix = this.getPrefixForURI(uri, prefix, true);
                        }
                    }
                    qname = prefix.length() > 0 ? prefix + ':' + attributes.getLocalName(i) : attributes.getLocalName(i);
                } else {
                    qname = attributes.getQName(i);
                    if (qname.length() == 0) {
                        qname = attributes.getLocalName(i);
                    }
                }
                if (qname.startsWith("xmlns")) {
                    if (vecQNames == null) {
                        vecQNames = new ArrayList<String>();
                    }
                    vecQNames.add(qname);
                }
                this.writer.write(qname);
                this.writer.write("=\"");
                this.getEncoder().writeEncoded(this.writer, attributes.getValue(i));
                this.writer.write(34);
            }
        }
        if (this.noNamespaceMappings) {
            this.nsStack.push();
        } else {
            Mapping map = this.nsStack.topOfFrame();
            while (map != null) {
                if (!(map.getNamespaceURI().equals("http://www.w3.org/2000/xmlns/") && map.getPrefix().equals("xmlns") || map.getNamespaceURI().equals("http://www.w3.org/XML/1998/namespace") && map.getPrefix().equals("xml"))) {
                    StringBuffer sb = new StringBuffer("xmlns");
                    if (map.getPrefix().length() > 0) {
                        sb.append(':');
                        sb.append(map.getPrefix());
                    }
                    if (vecQNames == null || vecQNames.indexOf(sb.toString()) == -1) {
                        this.writer.write(32);
                        sb.append("=\"");
                        sb.append(map.getNamespaceURI());
                        sb.append('\"');
                        this.writer.write(sb.toString());
                    }
                }
                map = this.nsStack.next();
            }
            this.noNamespaceMappings = true;
        }
        this.writingStartTag = true;
        this.elementStack.push(elementQName);
        this.onlyXML = true;
    }

    public void endElement() throws IOException {
        String elementQName = (String)this.elementStack.pop();
        if (this.debugEnabled) {
            log.debug((Object)Messages.getMessage("endElem00", "" + elementQName));
        }
        this.nsStack.pop();
        if (this.writingStartTag) {
            this.writer.write("/>");
            if (this.pretty) {
                this.writer.write(10);
            }
            this.writingStartTag = false;
            return;
        }
        if (this.onlyXML) {
            --this.indent;
            if (this.pretty) {
                for (int i = 0; i < this.indent; ++i) {
                    this.writer.write(32);
                }
            }
        }
        this.writer.write("</");
        this.writer.write(elementQName);
        this.writer.write(62);
        if (this.pretty && this.indent > 0) {
            this.writer.write(10);
        }
        this.onlyXML = true;
    }

    public void writeChars(char[] p1, int p2, int p3) throws IOException {
        if (this.startOfDocument && this.sendXMLDecl) {
            this.writeXMLDeclaration();
        }
        if (this.writingStartTag) {
            this.writer.write(62);
            this.writingStartTag = false;
        }
        this.writeSafeString(String.valueOf(p1, p2, p3));
        this.onlyXML = false;
    }

    public void writeString(String string) throws IOException {
        if (this.startOfDocument && this.sendXMLDecl) {
            this.writeXMLDeclaration();
        }
        if (this.writingStartTag) {
            this.writer.write(62);
            this.writingStartTag = false;
        }
        this.writer.write(string);
        this.onlyXML = false;
    }

    public void writeSafeString(String string) throws IOException {
        if (this.startOfDocument && this.sendXMLDecl) {
            this.writeXMLDeclaration();
        }
        if (this.writingStartTag) {
            this.writer.write(62);
            this.writingStartTag = false;
        }
        this.getEncoder().writeEncoded(this.writer, string);
        this.onlyXML = false;
    }

    public void writeDOMElement(Element el) throws IOException {
        if (this.startOfDocument && this.sendXMLDecl) {
            this.writeXMLDeclaration();
        }
        if (el instanceof org.apache.axis.message.Text) {
            this.writeSafeString(((Text)((Object)el)).getData());
            return;
        }
        AttributesImpl attributes = null;
        NamedNodeMap attrMap = el.getAttributes();
        if (attrMap.getLength() > 0) {
            attributes = new AttributesImpl();
            for (int i = 0; i < attrMap.getLength(); ++i) {
                Attr attr = (Attr)attrMap.item(i);
                String tmp = attr.getNamespaceURI();
                if (tmp != null && tmp.equals("http://www.w3.org/2000/xmlns/")) {
                    String prefix = attr.getLocalName();
                    if (prefix == null) continue;
                    if (prefix.equals("xmlns")) {
                        prefix = "";
                    }
                    String nsURI = attr.getValue();
                    this.registerPrefixForURI(prefix, nsURI);
                    continue;
                }
                attributes.addAttribute(attr.getNamespaceURI(), attr.getLocalName(), attr.getName(), "CDATA", attr.getValue());
            }
        }
        String namespaceURI = el.getNamespaceURI();
        String localPart = el.getLocalName();
        if (namespaceURI == null || namespaceURI.length() == 0) {
            localPart = el.getNodeName();
        }
        QName qName = new QName(namespaceURI, localPart);
        this.startElement(qName, attributes);
        NodeList children = el.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node child = children.item(i);
            if (child instanceof Element) {
                this.writeDOMElement((Element)child);
                continue;
            }
            if (child instanceof CDATASection) {
                this.writeString("<![CDATA[");
                this.writeString(((Text)child).getData());
                this.writeString("]]>");
                continue;
            }
            if (child instanceof Comment) {
                this.writeString("<!--");
                this.writeString(((CharacterData)child).getData());
                this.writeString("-->");
                continue;
            }
            if (!(child instanceof Text)) continue;
            this.writeSafeString(((Text)child).getData());
        }
        this.endElement();
    }

    public final Serializer getSerializerForJavaType(Class javaType) {
        SerializerFactory serF = null;
        Serializer ser = null;
        try {
            serF = (SerializerFactory)this.getTypeMapping().getSerializer(javaType);
            if (serF != null) {
                ser = (Serializer)serF.getSerializerAs("Axis SAX Mechanism");
            }
        }
        catch (JAXRPCException e) {
            // empty catch block
        }
        return ser;
    }

    public Attributes setTypeAttribute(Attributes attributes, QName type) {
        if (type == null || type.getLocalPart().indexOf(">") >= 0 || attributes != null && attributes.getIndex("http://www.w3.org/2001/XMLSchema-instance", "type") != -1) {
            return attributes;
        }
        AttributesImpl attrs = new AttributesImpl();
        if (attributes != null && 0 < attributes.getLength()) {
            attrs.setAttributes(attributes);
        }
        String prefix = this.getPrefixForURI("http://www.w3.org/2001/XMLSchema-instance", "xsi");
        attrs.addAttribute("http://www.w3.org/2001/XMLSchema-instance", "type", prefix + ":type", "CDATA", this.attributeQName2String(type));
        return attrs;
    }

    private void serializeActual(QName elemQName, Attributes attributes, Object value, QName xmlType, Boolean sendType) throws IOException {
        boolean shouldSendType;
        boolean bl = shouldSendType = sendType == null ? this.shouldSendXSIType() : sendType.booleanValue();
        if (value != null) {
            QNameHolder actualXMLType;
            Serializer ser;
            Class<?> javaType = value.getClass();
            TypeMapping tm = this.getTypeMapping();
            if (tm == null) {
                throw new IOException(Messages.getMessage("noSerializer00", value.getClass().getName(), "" + this));
            }
            this.currentXMLType = xmlType;
            if (Constants.equals(Constants.XSD_ANYTYPE, xmlType)) {
                xmlType = null;
                shouldSendType = true;
            }
            if ((ser = this.getSerializer(javaType, xmlType, actualXMLType = new QNameHolder())) != null) {
                if (shouldSendType || xmlType != null && !xmlType.equals(actualXMLType.value)) {
                    if (!this.isEncoded()) {
                        if (!(Constants.isSOAP_ENC(actualXMLType.value.getNamespaceURI()) || javaType.isArray() && xmlType != null && Constants.isSchemaXSD(xmlType.getNamespaceURI()))) {
                            this.writeXMLType = actualXMLType.value;
                        }
                    } else {
                        this.writeXMLType = actualXMLType.value;
                    }
                }
                ser.serialize(elemQName, attributes, value, this);
                return;
            }
            throw new IOException(Messages.getMessage("noSerializer00", value.getClass().getName(), "" + tm));
        }
    }

    private Serializer getSerializerFromClass(Class javaType, QName qname) {
        Serializer serializer = null;
        try {
            Method method = MethodCache.getInstance().getMethod(javaType, SERIALIZER_METHOD, SERIALIZER_CLASSES);
            if (method != null) {
                serializer = (Serializer)method.invoke(null, this.getEncodingStyle(), javaType, qname);
            }
        }
        catch (NoSuchMethodException e) {
        }
        catch (IllegalAccessException e) {
        }
        catch (InvocationTargetException e) {
            // empty catch block
        }
        return serializer;
    }

    public QName getCurrentXMLType() {
        return this.currentXMLType;
    }

    private SerializerFactory getSerializerFactoryFromInterface(Class javaType, QName xmlType, TypeMapping tm) {
        SerializerFactory serFactory = null;
        Class<?>[] interfaces = javaType.getInterfaces();
        if (interfaces != null) {
            for (int i = 0; i < interfaces.length; ++i) {
                Class<?> iface = interfaces[i];
                serFactory = (SerializerFactory)tm.getSerializer(iface, xmlType);
                if (serFactory == null) {
                    serFactory = this.getSerializerFactoryFromInterface(iface, xmlType, tm);
                }
                if (serFactory != null) break;
            }
        }
        return serFactory;
    }

    private Serializer getSerializer(Class javaType, QName xmlType, QNameHolder actualXMLType) {
        SerializerFactory serFactory = null;
        TypeMapping tm = this.getTypeMapping();
        if (actualXMLType != null) {
            actualXMLType.value = null;
        }
        while (javaType != null && (serFactory = (SerializerFactory)tm.getSerializer(javaType, xmlType)) == null) {
            Serializer serializer = this.getSerializerFromClass(javaType, xmlType);
            if (serializer != null) {
                TypeDesc typedesc;
                if (actualXMLType != null && (typedesc = TypeDesc.getTypeDescForClass(javaType)) != null) {
                    actualXMLType.value = typedesc.getXmlType();
                }
                return serializer;
            }
            serFactory = this.getSerializerFactoryFromInterface(javaType, xmlType, tm);
            if (serFactory != null) break;
            javaType = javaType.getSuperclass();
        }
        Serializer ser = null;
        if (serFactory != null) {
            ser = (Serializer)serFactory.getSerializerAs("Axis SAX Mechanism");
            if (actualXMLType != null) {
                if (serFactory instanceof BaseSerializerFactory) {
                    actualXMLType.value = ((BaseSerializerFactory)serFactory).getXMLType();
                }
                boolean encoded = this.isEncoded();
                if (actualXMLType.value == null || !encoded && (actualXMLType.value.equals(Constants.SOAP_ARRAY) || actualXMLType.value.equals(Constants.SOAP_ARRAY12))) {
                    actualXMLType.value = tm.getXMLType(javaType, xmlType, encoded);
                }
            }
        }
        return ser;
    }

    public String getValueAsString(Object value, QName xmlType) throws IOException {
        Class<?> cls = value.getClass();
        Serializer ser = this.getSerializer(cls, xmlType, null);
        if (!(ser instanceof SimpleValueSerializer)) {
            throw new IOException(Messages.getMessage("needSimpleValueSer", ser.getClass().getName()));
        }
        SimpleValueSerializer simpleSer = (SimpleValueSerializer)ser;
        return simpleSer.getValueAsString(value, this);
    }

    public void setWriteXMLType(QName type) {
        this.writeXMLType = type;
    }

    public XMLEncoder getEncoder() {
        if (this.encoder == null) {
            this.encoder = XMLUtils.getXMLEncoder(this.encoding);
        }
        return this.encoder;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public QName getItemQName() {
        return this.itemQName;
    }

    public void setItemQName(QName itemQName) {
        this.itemQName = itemQName;
    }

    public QName getItemType() {
        return this.itemType;
    }

    public void setItemType(QName itemType) {
        this.itemType = itemType;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    class MultiRefItem {
        String id;
        QName xmlType;
        Boolean sendType;
        Object value;

        MultiRefItem(String id, QName xmlType, Boolean sendType, Object value) {
            this.id = id;
            this.xmlType = xmlType;
            this.sendType = sendType;
            this.value = value;
        }
    }
}

