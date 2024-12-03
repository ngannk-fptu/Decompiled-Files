/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.encoding;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.namespace.QName;
import javax.xml.parsers.SAXParser;
import javax.xml.rpc.JAXRPCException;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.attachments.Attachments;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.constants.Use;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerFactory;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.encoding.TypeMappingRegistryImpl;
import org.apache.axis.message.EnvelopeBuilder;
import org.apache.axis.message.EnvelopeHandler;
import org.apache.axis.message.IDResolver;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.NullAttributes;
import org.apache.axis.message.SAX2EventRecorder;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.schema.SchemaVersion;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.NSStack;
import org.apache.axis.utils.XMLUtils;
import org.apache.axis.utils.cache.MethodCache;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

public class DeserializationContext
extends DefaultHandler
implements javax.xml.rpc.encoding.DeserializationContext,
LexicalHandler {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$DeserializationContext == null ? (class$org$apache$axis$encoding$DeserializationContext = DeserializationContext.class$("org.apache.axis.encoding.DeserializationContext")) : class$org$apache$axis$encoding$DeserializationContext).getName());
    private final boolean debugEnabled = log.isDebugEnabled();
    static final SchemaVersion[] schemaVersions = new SchemaVersion[]{SchemaVersion.SCHEMA_1999, SchemaVersion.SCHEMA_2000, SchemaVersion.SCHEMA_2001};
    private NSStack namespaces = new NSStack();
    private Locator locator;
    private Class destClass;
    private SOAPHandler topHandler = null;
    private ArrayList pushedDownHandlers = new ArrayList();
    private SAX2EventRecorder recorder = null;
    private SOAPEnvelope envelope;
    private HashMap idMap;
    private LocalIDResolver localIDs;
    private HashMap fixups;
    static final SOAPHandler nullHandler = new SOAPHandler();
    protected MessageContext msgContext;
    private boolean doneParsing = false;
    protected InputSource inputSource = null;
    private MessageElement curElement;
    protected int startOfMappingsPos = -1;
    private static final Class[] DESERIALIZER_CLASSES = new Class[]{class$java$lang$String == null ? (class$java$lang$String = DeserializationContext.class$("java.lang.String")) : class$java$lang$String, class$java$lang$Class == null ? (class$java$lang$Class = DeserializationContext.class$("java.lang.Class")) : class$java$lang$Class, class$javax$xml$namespace$QName == null ? (class$javax$xml$namespace$QName = DeserializationContext.class$("javax.xml.namespace.QName")) : class$javax$xml$namespace$QName};
    private static final String DESERIALIZER_METHOD = "getDeserializer";
    protected boolean haveSeenSchemaNS = false;
    private SOAPConstants soapConstants = null;
    boolean processingRef = false;
    private static final NullLexicalHandler nullLexicalHandler = new NullLexicalHandler();
    static /* synthetic */ Class class$org$apache$axis$encoding$DeserializationContext;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$lang$Class;
    static /* synthetic */ Class class$javax$xml$namespace$QName;
    static /* synthetic */ Class class$javax$xml$rpc$holders$Holder;

    public void deserializing(boolean isDeserializing) {
        this.doneParsing = isDeserializing;
    }

    public DeserializationContext(MessageContext ctx, SOAPHandler initialHandler) {
        this.msgContext = ctx;
        if (ctx == null || ctx.isHighFidelity()) {
            this.recorder = new SAX2EventRecorder();
        }
        if (initialHandler instanceof EnvelopeBuilder) {
            this.envelope = ((EnvelopeBuilder)initialHandler).getEnvelope();
            this.envelope.setRecorder(this.recorder);
        }
        this.pushElementHandler(new EnvelopeHandler(initialHandler));
    }

    public DeserializationContext(InputSource is, MessageContext ctx, String messageType) {
        this.msgContext = ctx;
        EnvelopeBuilder builder = new EnvelopeBuilder(messageType, null);
        if (ctx == null || ctx.isHighFidelity()) {
            this.recorder = new SAX2EventRecorder();
        }
        this.envelope = builder.getEnvelope();
        this.envelope.setRecorder(this.recorder);
        this.pushElementHandler(new EnvelopeHandler(builder));
        this.inputSource = is;
    }

    public SOAPConstants getSOAPConstants() {
        if (this.soapConstants != null) {
            return this.soapConstants;
        }
        if (this.msgContext != null) {
            this.soapConstants = this.msgContext.getSOAPConstants();
            return this.soapConstants;
        }
        return Constants.DEFAULT_SOAP_VERSION;
    }

    public DeserializationContext(InputSource is, MessageContext ctx, String messageType, SOAPEnvelope env) {
        EnvelopeBuilder builder = new EnvelopeBuilder(env, messageType);
        this.msgContext = ctx;
        if (ctx == null || ctx.isHighFidelity()) {
            this.recorder = new SAX2EventRecorder();
        }
        this.envelope = builder.getEnvelope();
        this.envelope.setRecorder(this.recorder);
        this.pushElementHandler(new EnvelopeHandler(builder));
        this.inputSource = is;
    }

    public void parse() throws SAXException {
        if (this.inputSource != null) {
            SAXParser parser = XMLUtils.getSAXParser();
            try {
                parser.setProperty("http://xml.org/sax/properties/lexical-handler", this);
                parser.parse(this.inputSource, (DefaultHandler)this);
                try {
                    parser.setProperty("http://xml.org/sax/properties/lexical-handler", nullLexicalHandler);
                }
                catch (Exception e) {
                    // empty catch block
                }
                XMLUtils.releaseSAXParser(parser);
            }
            catch (IOException e) {
                throw new SAXException(e);
            }
            this.inputSource = null;
        }
    }

    public MessageElement getCurElement() {
        return this.curElement;
    }

    public void setCurElement(MessageElement el) {
        this.curElement = el;
        if (this.curElement != null && this.curElement.getRecorder() != this.recorder) {
            this.recorder = this.curElement.getRecorder();
        }
    }

    public MessageContext getMessageContext() {
        return this.msgContext;
    }

    public String getEncodingStyle() {
        return this.msgContext == null ? Use.ENCODED.getEncoding() : this.msgContext.getEncodingStyle();
    }

    public SOAPEnvelope getEnvelope() {
        return this.envelope;
    }

    public SAX2EventRecorder getRecorder() {
        return this.recorder;
    }

    public void setRecorder(SAX2EventRecorder recorder) {
        this.recorder = recorder;
    }

    public ArrayList getCurrentNSMappings() {
        return this.namespaces.cloneFrame();
    }

    public String getNamespaceURI(String prefix) {
        String result = this.namespaces.getNamespaceURI(prefix);
        if (result != null) {
            return result;
        }
        if (this.curElement != null) {
            return this.curElement.getNamespaceURI(prefix);
        }
        return null;
    }

    public QName getQNameFromString(String qNameStr) {
        if (qNameStr == null) {
            return null;
        }
        int i = qNameStr.indexOf(58);
        String nsURI = i == -1 ? this.getNamespaceURI("") : this.getNamespaceURI(qNameStr.substring(0, i));
        return new QName(nsURI, qNameStr.substring(i + 1));
    }

    public QName getTypeFromXSITypeAttr(String namespace, String localName, Attributes attrs) {
        String type = Constants.getValue(attrs, Constants.URIS_SCHEMA_XSI, "type");
        if (type != null) {
            return this.getQNameFromString(type);
        }
        return null;
    }

    public QName getTypeFromAttributes(String namespace, String localName, Attributes attrs) {
        QName typeQName = this.getTypeFromXSITypeAttr(namespace, localName, attrs);
        if (typeQName == null && Constants.isSOAP_ENC(namespace)) {
            if (namespace.equals("http://www.w3.org/2003/05/soap-encoding")) {
                typeQName = new QName(namespace, localName);
            } else if (localName.equals(Constants.SOAP_ARRAY.getLocalPart())) {
                typeQName = Constants.SOAP_ARRAY;
            } else if (localName.equals(Constants.SOAP_STRING.getLocalPart())) {
                typeQName = Constants.SOAP_STRING;
            } else if (localName.equals(Constants.SOAP_BOOLEAN.getLocalPart())) {
                typeQName = Constants.SOAP_BOOLEAN;
            } else if (localName.equals(Constants.SOAP_DOUBLE.getLocalPart())) {
                typeQName = Constants.SOAP_DOUBLE;
            } else if (localName.equals(Constants.SOAP_FLOAT.getLocalPart())) {
                typeQName = Constants.SOAP_FLOAT;
            } else if (localName.equals(Constants.SOAP_INT.getLocalPart())) {
                typeQName = Constants.SOAP_INT;
            } else if (localName.equals(Constants.SOAP_LONG.getLocalPart())) {
                typeQName = Constants.SOAP_LONG;
            } else if (localName.equals(Constants.SOAP_SHORT.getLocalPart())) {
                typeQName = Constants.SOAP_SHORT;
            } else if (localName.equals(Constants.SOAP_BYTE.getLocalPart())) {
                typeQName = Constants.SOAP_BYTE;
            }
        }
        if (typeQName == null && attrs != null) {
            String encURI = this.getSOAPConstants().getEncodingURI();
            String itemType = this.getSOAPConstants().getAttrItemType();
            for (int i = 0; i < attrs.getLength(); ++i) {
                if (!encURI.equals(attrs.getURI(i)) || !itemType.equals(attrs.getLocalName(i))) continue;
                return new QName(encURI, "Array");
            }
        }
        return typeQName;
    }

    public boolean isNil(Attributes attrs) {
        return JavaUtils.isTrueExplicitly(Constants.getValue(attrs, Constants.QNAMES_NIL), false);
    }

    public final Deserializer getDeserializer(Class cls, QName xmlType) {
        if (xmlType == null) {
            return null;
        }
        DeserializerFactory dserF = null;
        Deserializer dser = null;
        try {
            dserF = (DeserializerFactory)this.getTypeMapping().getDeserializer(cls, xmlType);
        }
        catch (JAXRPCException e) {
            log.error((Object)Messages.getMessage("noFactory00", xmlType.toString()));
        }
        if (dserF != null) {
            try {
                dser = (Deserializer)dserF.getDeserializerAs("Axis SAX Mechanism");
            }
            catch (JAXRPCException e) {
                log.error((Object)Messages.getMessage("noDeser00", xmlType.toString()));
            }
        }
        return dser;
    }

    public Deserializer getDeserializerForClass(Class cls) {
        if (cls == null) {
            cls = this.destClass;
        }
        if (cls == null) {
            return null;
        }
        if ((class$javax$xml$rpc$holders$Holder == null ? (class$javax$xml$rpc$holders$Holder = DeserializationContext.class$("javax.xml.rpc.holders.Holder")) : class$javax$xml$rpc$holders$Holder).isAssignableFrom(cls)) {
            try {
                cls = cls.getField("value").getType();
            }
            catch (Exception e) {
                // empty catch block
            }
        }
        Deserializer dser = null;
        QName type = this.getTypeMapping().getTypeQName(cls);
        dser = this.getDeserializer(cls, type);
        if (dser != null) {
            return dser;
        }
        try {
            TypeDesc typedesc;
            Method method = MethodCache.getInstance().getMethod(cls, DESERIALIZER_METHOD, DESERIALIZER_CLASSES);
            if (method != null && (typedesc = TypeDesc.getTypeDescForClass(cls)) != null) {
                dser = (Deserializer)method.invoke(null, this.getEncodingStyle(), cls, typedesc.getXmlType());
            }
        }
        catch (Exception e) {
            log.error((Object)Messages.getMessage("noDeser00", cls.getName()));
        }
        return dser;
    }

    public void setDestinationClass(Class destClass) {
        this.destClass = destClass;
    }

    public Class getDestinationClass() {
        return this.destClass;
    }

    public final Deserializer getDeserializerForType(QName xmlType) {
        return this.getDeserializer(null, xmlType);
    }

    public TypeMapping getTypeMapping() {
        if (this.msgContext == null || this.msgContext.getTypeMappingRegistry() == null) {
            return (TypeMapping)new TypeMappingRegistryImpl().getTypeMapping(null);
        }
        TypeMappingRegistry tmr = this.msgContext.getTypeMappingRegistry();
        return (TypeMapping)tmr.getTypeMapping(this.getEncodingStyle());
    }

    public TypeMappingRegistry getTypeMappingRegistry() {
        return this.msgContext.getTypeMappingRegistry();
    }

    public MessageElement getElementByID(String id) {
        Object ret;
        IDResolver resolver;
        if (this.idMap != null && (resolver = (IDResolver)this.idMap.get(id)) != null && (ret = resolver.getReferencedObject(id)) instanceof MessageElement) {
            return (MessageElement)ret;
        }
        return null;
    }

    public Object getObjectByRef(String href) {
        Object ret = null;
        if (href != null) {
            IDResolver resolver;
            if (this.idMap != null && (resolver = (IDResolver)this.idMap.get(href)) != null) {
                ret = resolver.getReferencedObject(href);
            }
            if (null == ret && !href.startsWith("#")) {
                Message msg = null;
                msg = this.msgContext.getCurrentMessage();
                if (null != msg) {
                    Attachments attch = null;
                    attch = msg.getAttachmentsImpl();
                    if (null != attch) {
                        try {
                            ret = attch.getAttachmentByReference(href);
                        }
                        catch (AxisFault e) {
                            throw new RuntimeException(e.toString() + JavaUtils.stackToString(e));
                        }
                    }
                }
            }
        }
        return ret;
    }

    public void addObjectById(String id, Object obj) {
        String idStr = '#' + id;
        if (this.idMap == null || id == null) {
            return;
        }
        IDResolver resolver = (IDResolver)this.idMap.get(idStr);
        if (resolver == null) {
            return;
        }
        resolver.addReferencedObject(idStr, obj);
    }

    public void registerFixup(String href, Deserializer dser) {
        Deserializer prev;
        if (this.fixups == null) {
            this.fixups = new HashMap();
        }
        if ((prev = this.fixups.put(href, dser)) != null && prev != dser) {
            dser.moveValueTargets(prev);
            if (dser.getDefaultType() == null) {
                dser.setDefaultType(prev.getDefaultType());
            }
        }
    }

    public void registerElementByID(String id, MessageElement elem) {
        Deserializer dser;
        if (this.localIDs == null) {
            this.localIDs = new LocalIDResolver();
        }
        String absID = '#' + id;
        this.localIDs.addReferencedObject(absID, elem);
        this.registerResolverForID(absID, this.localIDs);
        if (this.fixups != null && (dser = (Deserializer)this.fixups.get(absID)) != null) {
            elem.setFixupDeserializer(dser);
        }
    }

    public void registerResolverForID(String id, IDResolver resolver) {
        if (id == null || resolver == null) {
            return;
        }
        if (this.idMap == null) {
            this.idMap = new HashMap();
        }
        this.idMap.put(id, resolver);
    }

    public boolean hasElementsByID() {
        return this.idMap == null ? false : this.idMap.size() > 0;
    }

    public int getCurrentRecordPos() {
        if (this.recorder == null) {
            return -1;
        }
        return this.recorder.getLength() - 1;
    }

    public int getStartOfMappingsPos() {
        if (this.startOfMappingsPos == -1) {
            return this.getCurrentRecordPos() + 1;
        }
        return this.startOfMappingsPos;
    }

    public void pushNewElement(MessageElement elem) {
        if (this.debugEnabled) {
            log.debug((Object)("Pushing element " + elem.getName()));
        }
        if (!this.doneParsing && this.recorder != null) {
            this.recorder.newElement(elem);
        }
        try {
            if (this.curElement != null) {
                elem.setParentElement(this.curElement);
            }
        }
        catch (Exception e) {
            log.fatal((Object)Messages.getMessage("exception00"), (Throwable)e);
        }
        this.curElement = elem;
        if (elem.getRecorder() != this.recorder) {
            this.recorder = elem.getRecorder();
        }
    }

    public void pushElementHandler(SOAPHandler handler) {
        if (this.debugEnabled) {
            log.debug((Object)Messages.getMessage("pushHandler00", "" + handler));
        }
        if (this.topHandler != null) {
            this.pushedDownHandlers.add(this.topHandler);
        }
        this.topHandler = handler;
    }

    public void replaceElementHandler(SOAPHandler handler) {
        this.topHandler = handler;
    }

    public SOAPHandler popElementHandler() {
        SOAPHandler result = this.topHandler;
        int size = this.pushedDownHandlers.size();
        this.topHandler = size > 0 ? (SOAPHandler)this.pushedDownHandlers.remove(size - 1) : null;
        if (this.debugEnabled) {
            if (result == null) {
                log.debug((Object)Messages.getMessage("popHandler00", "(null)"));
            } else {
                log.debug((Object)Messages.getMessage("popHandler00", "" + result));
            }
        }
        return result;
    }

    public void setProcessingRef(boolean ref) {
        this.processingRef = ref;
    }

    public boolean isProcessingRef() {
        return this.processingRef;
    }

    public void startDocument() throws SAXException {
        if (!this.doneParsing && this.recorder != null) {
            this.recorder.startDocument();
        }
    }

    public void endDocument() throws SAXException {
        if (this.debugEnabled) {
            log.debug((Object)"Enter: DeserializationContext::endDocument()");
        }
        if (!this.doneParsing && this.recorder != null) {
            this.recorder.endDocument();
        }
        this.doneParsing = true;
        if (this.debugEnabled) {
            log.debug((Object)"Exit: DeserializationContext::endDocument()");
        }
    }

    public boolean isDoneParsing() {
        return this.doneParsing;
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        if (this.debugEnabled) {
            log.debug((Object)("Enter: DeserializationContext::startPrefixMapping(" + prefix + ", " + uri + ")"));
        }
        if (!this.doneParsing && this.recorder != null) {
            this.recorder.startPrefixMapping(prefix, uri);
        }
        if (this.startOfMappingsPos == -1) {
            this.namespaces.push();
            this.startOfMappingsPos = this.getCurrentRecordPos();
        }
        if (prefix != null) {
            this.namespaces.add(uri, prefix);
        } else {
            this.namespaces.add(uri, "");
        }
        if (!this.haveSeenSchemaNS && this.msgContext != null) {
            for (int i = 0; !this.haveSeenSchemaNS && i < schemaVersions.length; ++i) {
                SchemaVersion schemaVersion = schemaVersions[i];
                if (!uri.equals(schemaVersion.getXsdURI()) && !uri.equals(schemaVersion.getXsiURI())) continue;
                this.msgContext.setSchemaVersion(schemaVersion);
                this.haveSeenSchemaNS = true;
            }
        }
        if (this.topHandler != null) {
            this.topHandler.startPrefixMapping(prefix, uri);
        }
        if (this.debugEnabled) {
            log.debug((Object)"Exit: DeserializationContext::startPrefixMapping()");
        }
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        if (this.debugEnabled) {
            log.debug((Object)("Enter: DeserializationContext::endPrefixMapping(" + prefix + ")"));
        }
        if (!this.doneParsing && this.recorder != null) {
            this.recorder.endPrefixMapping(prefix);
        }
        if (this.topHandler != null) {
            this.topHandler.endPrefixMapping(prefix);
        }
        if (this.debugEnabled) {
            log.debug((Object)"Exit: DeserializationContext::endPrefixMapping()");
        }
    }

    public void setDocumentLocator(Locator locator) {
        if (!this.doneParsing && this.recorder != null) {
            this.recorder.setDocumentLocator(locator);
        }
        this.locator = locator;
    }

    public Locator getDocumentLocator() {
        return this.locator;
    }

    public void characters(char[] p1, int p2, int p3) throws SAXException {
        if (!this.doneParsing && this.recorder != null) {
            this.recorder.characters(p1, p2, p3);
        }
        if (this.topHandler != null) {
            this.topHandler.characters(p1, p2, p3);
        }
    }

    public void ignorableWhitespace(char[] p1, int p2, int p3) throws SAXException {
        if (!this.doneParsing && this.recorder != null) {
            this.recorder.ignorableWhitespace(p1, p2, p3);
        }
        if (this.topHandler != null) {
            this.topHandler.ignorableWhitespace(p1, p2, p3);
        }
    }

    public void processingInstruction(String p1, String p2) throws SAXException {
        throw new SAXException(Messages.getMessage("noInstructions00"));
    }

    public void skippedEntity(String p1) throws SAXException {
        if (!this.doneParsing && this.recorder != null) {
            this.recorder.skippedEntity(p1);
        }
        this.topHandler.skippedEntity(p1);
    }

    public void startElement(String namespace, String localName, String qName, Attributes attributes) throws SAXException {
        if (this.debugEnabled) {
            log.debug((Object)("Enter: DeserializationContext::startElement(" + namespace + ", " + localName + ")"));
        }
        if (attributes == null || attributes.getLength() == 0) {
            attributes = NullAttributes.singleton;
        } else {
            attributes = new AttributesImpl(attributes);
            SOAPConstants soapConstants = this.getSOAPConstants();
            if (soapConstants == SOAPConstants.SOAP12_CONSTANTS && attributes.getValue(soapConstants.getAttrHref()) != null && attributes.getValue("id") != null) {
                AxisFault fault = new AxisFault(Constants.FAULT_SOAP12_SENDER, null, Messages.getMessage("noIDandHREFonSameElement"), null, null, null);
                throw new SAXException(fault);
            }
        }
        SOAPHandler nextHandler = null;
        String prefix = "";
        int idx = qName.indexOf(58);
        if (idx > 0) {
            prefix = qName.substring(0, idx);
        }
        if (this.topHandler != null) {
            nextHandler = this.topHandler.onStartChild(namespace, localName, prefix, attributes, this);
        }
        if (nextHandler == null) {
            nextHandler = new SOAPHandler();
        }
        this.pushElementHandler(nextHandler);
        nextHandler.startElement(namespace, localName, prefix, attributes, this);
        if (!this.doneParsing && this.recorder != null) {
            this.recorder.startElement(namespace, localName, qName, attributes);
            if (!this.doneParsing) {
                this.curElement.setContentsIndex(this.recorder.getLength());
            }
        }
        if (this.startOfMappingsPos != -1) {
            this.startOfMappingsPos = -1;
        } else {
            this.namespaces.push();
        }
        if (this.debugEnabled) {
            log.debug((Object)"Exit: DeserializationContext::startElement()");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void endElement(String namespace, String localName, String qName) throws SAXException {
        block8: {
            if (this.debugEnabled) {
                log.debug((Object)("Enter: DeserializationContext::endElement(" + namespace + ", " + localName + ")"));
            }
            if (!this.doneParsing && this.recorder != null) {
                this.recorder.endElement(namespace, localName, qName);
            }
            try {
                SOAPHandler handler = this.popElementHandler();
                handler.endElement(namespace, localName, this);
                if (this.topHandler != null) {
                    this.topHandler.onEndChild(namespace, localName, this);
                }
                Object var6_5 = null;
                if (this.curElement == null) break block8;
            }
            catch (Throwable throwable) {
                Object var6_6 = null;
                if (this.curElement != null) {
                    this.curElement = (MessageElement)this.curElement.getParentElement();
                }
                this.namespaces.pop();
                if (this.debugEnabled) {
                    String name = this.curElement != null ? this.curElement.getClass().getName() + ":" + this.curElement.getName() : null;
                    log.debug((Object)("Popped element stack to " + name));
                    log.debug((Object)"Exit: DeserializationContext::endElement()");
                }
                throw throwable;
            }
            this.curElement = (MessageElement)this.curElement.getParentElement();
        }
        this.namespaces.pop();
        if (this.debugEnabled) {
            String name = this.curElement != null ? this.curElement.getClass().getName() + ":" + this.curElement.getName() : null;
            log.debug((Object)("Popped element stack to " + name));
            log.debug((Object)"Exit: DeserializationContext::endElement()");
        }
    }

    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        throw new SAXException(Messages.getMessage("noInstructions00"));
    }

    public void endDTD() throws SAXException {
        if (this.recorder != null) {
            this.recorder.endDTD();
        }
    }

    public void startEntity(String name) throws SAXException {
        if (this.recorder != null) {
            this.recorder.startEntity(name);
        }
    }

    public void endEntity(String name) throws SAXException {
        if (this.recorder != null) {
            this.recorder.endEntity(name);
        }
    }

    public void startCDATA() throws SAXException {
        if (this.recorder != null) {
            this.recorder.startCDATA();
        }
    }

    public void endCDATA() throws SAXException {
        if (this.recorder != null) {
            this.recorder.endCDATA();
        }
    }

    public void comment(char[] ch, int start, int length) throws SAXException {
        if (this.recorder != null) {
            this.recorder.comment(ch, start, length);
        }
    }

    public InputSource resolveEntity(String publicId, String systemId) {
        return XMLUtils.getEmptyInputSource();
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    private static class NullLexicalHandler
    implements LexicalHandler {
        private NullLexicalHandler() {
        }

        public void startDTD(String arg0, String arg1, String arg2) throws SAXException {
        }

        public void endDTD() throws SAXException {
        }

        public void startEntity(String arg0) throws SAXException {
        }

        public void endEntity(String arg0) throws SAXException {
        }

        public void startCDATA() throws SAXException {
        }

        public void endCDATA() throws SAXException {
        }

        public void comment(char[] arg0, int arg1, int arg2) throws SAXException {
        }
    }

    private static class LocalIDResolver
    implements IDResolver {
        HashMap idMap = null;

        private LocalIDResolver() {
        }

        public void addReferencedObject(String id, Object referent) {
            if (this.idMap == null) {
                this.idMap = new HashMap();
            }
            this.idMap.put(id, referent);
        }

        public Object getReferencedObject(String href) {
            if (this.idMap == null || href == null) {
                return null;
            }
            return this.idMap.get(href);
        }
    }
}

