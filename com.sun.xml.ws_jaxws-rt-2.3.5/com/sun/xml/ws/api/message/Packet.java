/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  com.sun.xml.bind.marshaller.SAX2DOMEx
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPMessage
 *  javax.xml.ws.BindingProvider
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.soap.MTOMFeature
 */
package com.sun.xml.ws.api.message;

import com.oracle.webservices.api.message.BaseDistributedPropertySet;
import com.oracle.webservices.api.message.BasePropertySet;
import com.oracle.webservices.api.message.ContentType;
import com.oracle.webservices.api.message.MessageContext;
import com.oracle.webservices.api.message.PropertySet;
import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.bind.marshaller.SAX2DOMEx;
import com.sun.xml.ws.addressing.WsaPropertyBag;
import com.sun.xml.ws.addressing.WsaTubeHelper;
import com.sun.xml.ws.api.Component;
import com.sun.xml.ws.api.DistributedPropertySet;
import com.sun.xml.ws.api.EndpointAddress;
import com.sun.xml.ws.api.PropertySet;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.message.AddressingUtils;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.api.message.MessageMetadata;
import com.sun.xml.ws.api.message.MessageWrapper;
import com.sun.xml.ws.api.message.MessageWritable;
import com.sun.xml.ws.api.message.saaj.SAAJFactory;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.WSDLOperationMapping;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.server.TransportBackChannel;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.ws.api.streaming.XMLStreamWriterFactory;
import com.sun.xml.ws.client.ContentNegotiation;
import com.sun.xml.ws.client.HandlerConfiguration;
import com.sun.xml.ws.client.Stub;
import com.sun.xml.ws.message.RelatesToHeader;
import com.sun.xml.ws.message.StringHeader;
import com.sun.xml.ws.resources.AddressingMessages;
import com.sun.xml.ws.util.DOMUtil;
import com.sun.xml.ws.util.xml.XmlUtil;
import com.sun.xml.ws.wsdl.DispatchException;
import com.sun.xml.ws.wsdl.OperationDispatcher;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.WritableByteChannel;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.MTOMFeature;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public final class Packet
extends BaseDistributedPropertySet
implements MessageContext,
MessageMetadata {
    private Message message;
    private String userStateId;
    private WSDLOperationMapping wsdlOperationMapping = null;
    private QName wsdlOperation;
    public boolean wasTransportSecure;
    public static final String INBOUND_TRANSPORT_HEADERS = "com.sun.xml.ws.api.message.packet.inbound.transport.headers";
    public static final String OUTBOUND_TRANSPORT_HEADERS = "com.sun.xml.ws.api.message.packet.outbound.transport.headers";
    public static final String HA_INFO = "com.sun.xml.ws.api.message.packet.hainfo";
    @PropertySet.Property(value={"com.sun.xml.ws.handler.config"})
    public HandlerConfiguration handlerConfig;
    @PropertySet.Property(value={"com.sun.xml.ws.client.handle"})
    public BindingProvider proxy;
    public boolean isAdapterDeliversNonAnonymousResponse;
    public boolean packetTakesPriorityOverRequestContext = false;
    public EndpointAddress endpointAddress;
    public ContentNegotiation contentNegotiation;
    public String acceptableMimeTypes;
    public WebServiceContextDelegate webServiceContextDelegate;
    @Nullable
    public TransportBackChannel transportBackChannel;
    public Component component;
    @PropertySet.Property(value={"com.sun.xml.ws.api.server.WSEndpoint"})
    public WSEndpoint endpoint;
    @PropertySet.Property(value={"javax.xml.ws.soap.http.soapaction.uri"})
    public String soapAction;
    @PropertySet.Property(value={"com.sun.xml.ws.server.OneWayOperation"})
    public Boolean expectReply;
    @Deprecated
    public Boolean isOneWay;
    public Boolean isSynchronousMEP;
    public Boolean nonNullAsyncHandlerGiven;
    private Boolean isRequestReplyMEP;
    private Set<String> handlerScopePropertyNames;
    public final Map<String, Object> invocationProperties;
    private static final BasePropertySet.PropertyMap model = Packet.parse(Packet.class);
    private static final Logger LOGGER = Logger.getLogger(Packet.class.getName());
    public Codec codec = null;
    private ContentType contentType;
    private Boolean mtomRequest;
    private Boolean mtomAcceptable;
    private MTOMFeature mtomFeature;
    Boolean checkMtomAcceptable;
    private Boolean fastInfosetAcceptable;
    private State state = State.ServerRequest;
    private boolean isFastInfosetDisabled;
    private SAAJFactory saajFactory;

    public Packet(Message request) {
        this();
        this.message = request;
        if (this.message != null) {
            this.message.setMessageMedadata(this);
        }
    }

    public Packet() {
        this.invocationProperties = new HashMap<String, Object>();
    }

    private Packet(Packet that) {
        this.relatePackets(that, true);
        this.invocationProperties = that.invocationProperties;
    }

    public Packet copy(boolean copyMessage) {
        Packet copy = new Packet(this);
        if (copyMessage && this.message != null) {
            copy.message = this.message.copy();
        }
        if (copy.message != null) {
            copy.message.setMessageMedadata(copy);
        }
        return copy;
    }

    public Message getMessage() {
        if (this.message != null && !(this.message instanceof MessageWrapper)) {
            this.message = new MessageWrapper(this, this.message);
        }
        return this.message;
    }

    public Message getInternalMessage() {
        return this.message instanceof MessageWrapper ? ((MessageWrapper)this.message).delegate : this.message;
    }

    public WSBinding getBinding() {
        if (this.endpoint != null) {
            return this.endpoint.getBinding();
        }
        if (this.proxy != null) {
            return (WSBinding)this.proxy.getBinding();
        }
        return null;
    }

    public void setMessage(Message message) {
        this.message = message;
        if (message != null) {
            this.message.setMessageMedadata(this);
        }
    }

    public boolean isProtocolMessage() {
        return this.message != null && this.message.isProtocolMessage();
    }

    public void setIsProtocolMessage() {
        assert (this.message != null);
        this.message.setIsProtocolMessage();
    }

    public String getUserStateId() {
        return this.userStateId;
    }

    public void setUserStateId(String x) {
        assert (x != null && x.length() <= 256);
        this.userStateId = x;
    }

    @PropertySet.Property(value={"javax.xml.ws.wsdl.operation"})
    @Nullable
    public final QName getWSDLOperation() {
        if (this.wsdlOperation != null) {
            return this.wsdlOperation;
        }
        if (this.wsdlOperationMapping == null) {
            this.wsdlOperationMapping = this.getWSDLOperationMapping();
        }
        if (this.wsdlOperationMapping != null) {
            this.wsdlOperation = this.wsdlOperationMapping.getOperationName();
        }
        return this.wsdlOperation;
    }

    @Override
    public WSDLOperationMapping getWSDLOperationMapping() {
        if (this.wsdlOperationMapping != null) {
            return this.wsdlOperationMapping;
        }
        OperationDispatcher opDispatcher = null;
        if (this.endpoint != null) {
            opDispatcher = this.endpoint.getOperationDispatcher();
        } else if (this.proxy != null) {
            opDispatcher = ((Stub)this.proxy).getOperationDispatcher();
        }
        if (opDispatcher != null) {
            try {
                this.wsdlOperationMapping = opDispatcher.getWSDLOperationMapping(this);
            }
            catch (DispatchException dispatchException) {
                // empty catch block
            }
        }
        return this.wsdlOperationMapping;
    }

    public void setWSDLOperation(QName wsdlOp) {
        this.wsdlOperation = wsdlOp;
    }

    @PropertySet.Property(value={"javax.xml.ws.service.endpoint.address"})
    public String getEndPointAddressString() {
        if (this.endpointAddress == null) {
            return null;
        }
        return this.endpointAddress.toString();
    }

    public void setEndPointAddressString(String s) {
        this.endpointAddress = s == null ? null : EndpointAddress.create(s);
    }

    @PropertySet.Property(value={"com.sun.xml.ws.client.ContentNegotiation"})
    public String getContentNegotiationString() {
        return this.contentNegotiation != null ? this.contentNegotiation.toString() : null;
    }

    public void setContentNegotiationString(String s) {
        if (s == null) {
            this.contentNegotiation = null;
        } else {
            try {
                this.contentNegotiation = ContentNegotiation.valueOf(s);
            }
            catch (IllegalArgumentException e) {
                this.contentNegotiation = ContentNegotiation.none;
            }
        }
    }

    @PropertySet.Property(value={"javax.xml.ws.reference.parameters"})
    @NotNull
    public List<Element> getReferenceParameters() {
        Message msg = this.getMessage();
        ArrayList<Element> refParams = new ArrayList<Element>();
        if (msg == null) {
            return refParams;
        }
        MessageHeaders hl = msg.getHeaders();
        for (Header h : hl.asList()) {
            String attr = h.getAttribute(AddressingVersion.W3C.nsUri, "IsReferenceParameter");
            if (attr == null || !attr.equals("true") && !attr.equals("1")) continue;
            Document d = DOMUtil.createDom();
            SAX2DOMEx s2d = new SAX2DOMEx((Node)d);
            try {
                h.writeTo((ContentHandler)s2d, XmlUtil.DRACONIAN_ERROR_HANDLER);
                refParams.add((Element)d.getLastChild());
            }
            catch (SAXException e) {
                throw new WebServiceException((Throwable)e);
            }
        }
        return refParams;
    }

    @PropertySet.Property(value={"com.sun.xml.ws.api.message.HeaderList"})
    MessageHeaders getHeaderList() {
        Message msg = this.getMessage();
        if (msg == null) {
            return null;
        }
        return msg.getHeaders();
    }

    public TransportBackChannel keepTransportBackChannelOpen() {
        TransportBackChannel r = this.transportBackChannel;
        this.transportBackChannel = null;
        return r;
    }

    public Boolean isRequestReplyMEP() {
        return this.isRequestReplyMEP;
    }

    public void setRequestReplyMEP(Boolean x) {
        this.isRequestReplyMEP = x;
    }

    public final Set<String> getHandlerScopePropertyNames(boolean readOnly) {
        Set<String> o = this.handlerScopePropertyNames;
        if (o == null) {
            if (readOnly) {
                return Collections.emptySet();
            }
            this.handlerScopePropertyNames = o = new HashSet<String>();
        }
        return o;
    }

    public final Set<String> getApplicationScopePropertyNames(boolean readOnly) {
        assert (false);
        return new HashSet<String>();
    }

    @Deprecated
    public Packet createResponse(Message msg) {
        Packet response = new Packet(this);
        response.setMessage(msg);
        return response;
    }

    public Packet createClientResponse(Message msg) {
        Packet response = new Packet(this);
        response.setMessage(msg);
        this.finishCreateRelateClientResponse(response);
        return response;
    }

    public Packet relateClientResponse(Packet response) {
        response.relatePackets(this, true);
        this.finishCreateRelateClientResponse(response);
        return response;
    }

    private void finishCreateRelateClientResponse(Packet response) {
        response.soapAction = null;
        response.setState(State.ClientResponse);
    }

    public Packet createServerResponse(@Nullable Message responseMessage, @Nullable WSDLPort wsdlPort, @Nullable SEIModel seiModel, @NotNull WSBinding binding) {
        Packet r = this.createClientResponse(responseMessage);
        return this.relateServerResponse(r, wsdlPort, seiModel, binding);
    }

    public void copyPropertiesTo(@Nullable Packet response) {
        this.relatePackets(response, false);
    }

    private void relatePackets(@Nullable Packet packet, boolean isCopy) {
        Packet response;
        Packet request;
        if (!isCopy) {
            request = this;
            response = packet;
            response.soapAction = null;
            response.invocationProperties.putAll(request.invocationProperties);
            if (this.getState().equals((Object)State.ServerRequest)) {
                response.setState(State.ServerResponse);
            }
        } else {
            request = packet;
            response = this;
            response.soapAction = request.soapAction;
            response.setState(request.getState());
        }
        request.copySatelliteInto(response);
        response.isAdapterDeliversNonAnonymousResponse = request.isAdapterDeliversNonAnonymousResponse;
        response.handlerConfig = request.handlerConfig;
        response.handlerScopePropertyNames = request.handlerScopePropertyNames;
        response.contentNegotiation = request.contentNegotiation;
        response.wasTransportSecure = request.wasTransportSecure;
        response.transportBackChannel = request.transportBackChannel;
        response.endpointAddress = request.endpointAddress;
        response.wsdlOperation = request.wsdlOperation;
        response.wsdlOperationMapping = request.wsdlOperationMapping;
        response.acceptableMimeTypes = request.acceptableMimeTypes;
        response.endpoint = request.endpoint;
        response.proxy = request.proxy;
        response.webServiceContextDelegate = request.webServiceContextDelegate;
        response.expectReply = request.expectReply;
        response.component = request.component;
        response.mtomAcceptable = request.mtomAcceptable;
        response.mtomRequest = request.mtomRequest;
        response.userStateId = request.userStateId;
    }

    public Packet relateServerResponse(@Nullable Packet r, @Nullable WSDLPort wsdlPort, @Nullable SEIModel seiModel, @NotNull WSBinding binding) {
        this.relatePackets(r, false);
        r.setState(State.ServerResponse);
        AddressingVersion av = binding.getAddressingVersion();
        if (av == null) {
            return r;
        }
        if (this.getMessage() == null) {
            return r;
        }
        String inputAction = AddressingUtils.getAction(this.getMessage().getHeaders(), av, binding.getSOAPVersion());
        if (inputAction == null) {
            return r;
        }
        if (r.getMessage() == null || wsdlPort != null && this.getMessage().isOneWay(wsdlPort)) {
            return r;
        }
        this.populateAddressingHeaders(binding, r, wsdlPort, seiModel);
        return r;
    }

    public Packet createServerResponse(@Nullable Message responseMessage, @NotNull AddressingVersion addressingVersion, @NotNull SOAPVersion soapVersion, @NotNull String action) {
        Packet responsePacket = this.createClientResponse(responseMessage);
        responsePacket.setState(State.ServerResponse);
        if (addressingVersion == null) {
            return responsePacket;
        }
        String inputAction = AddressingUtils.getAction(this.getMessage().getHeaders(), addressingVersion, soapVersion);
        if (inputAction == null) {
            return responsePacket;
        }
        this.populateAddressingHeaders(responsePacket, addressingVersion, soapVersion, action, false);
        return responsePacket;
    }

    public void setResponseMessage(@NotNull Packet request, @Nullable Message responseMessage, @NotNull AddressingVersion addressingVersion, @NotNull SOAPVersion soapVersion, @NotNull String action) {
        Packet temp = request.createServerResponse(responseMessage, addressingVersion, soapVersion, action);
        this.setMessage(temp.getMessage());
    }

    private void populateAddressingHeaders(Packet responsePacket, AddressingVersion av, SOAPVersion sv, String action, boolean mustUnderstand) {
        if (av == null) {
            return;
        }
        if (responsePacket.getMessage() == null) {
            return;
        }
        MessageHeaders hl = responsePacket.getMessage().getHeaders();
        WsaPropertyBag wpb = this.getSatellite(WsaPropertyBag.class);
        Message msg = this.getMessage();
        WSEndpointReference replyTo = null;
        Header replyToFromRequestMsg = AddressingUtils.getFirstHeader(msg.getHeaders(), av.replyToTag, true, sv);
        Header replyToFromResponseMsg = hl.get(av.toTag, false);
        boolean replaceToTag = true;
        try {
            if (replyToFromRequestMsg != null) {
                replyTo = replyToFromRequestMsg.readAsEPR(av);
            }
            if (replyToFromResponseMsg != null && replyTo == null) {
                replaceToTag = false;
            }
        }
        catch (XMLStreamException e) {
            throw new WebServiceException(AddressingMessages.REPLY_TO_CANNOT_PARSE(), (Throwable)e);
        }
        if (replyTo == null) {
            replyTo = AddressingUtils.getReplyTo(msg.getHeaders(), av, sv);
        }
        if (AddressingUtils.getAction(responsePacket.getMessage().getHeaders(), av, sv) == null) {
            hl.add(new StringHeader(av.actionTag, action, sv, mustUnderstand));
        }
        if (responsePacket.getMessage().getHeaders().get(av.messageIDTag, false) == null) {
            String newID = Message.generateMessageID();
            hl.add(new StringHeader(av.messageIDTag, newID));
        }
        String mid = null;
        if (wpb != null) {
            mid = wpb.getMessageID();
        }
        if (mid == null) {
            mid = AddressingUtils.getMessageID(msg.getHeaders(), av, sv);
        }
        if (mid != null) {
            hl.addOrReplace(new RelatesToHeader(av.relatesToTag, mid));
        }
        WSEndpointReference refpEPR = null;
        if (responsePacket.getMessage().isFault()) {
            if (wpb != null) {
                refpEPR = wpb.getFaultToFromRequest();
            }
            if (refpEPR == null) {
                refpEPR = AddressingUtils.getFaultTo(msg.getHeaders(), av, sv);
            }
            if (refpEPR == null) {
                refpEPR = replyTo;
            }
        } else {
            refpEPR = replyTo;
        }
        if (replaceToTag && refpEPR != null) {
            hl.addOrReplace(new StringHeader(av.toTag, refpEPR.getAddress()));
            refpEPR.addReferenceParametersToList(hl);
        }
    }

    private void populateAddressingHeaders(WSBinding binding, Packet responsePacket, WSDLPort wsdlPort, SEIModel seiModel) {
        String action;
        AddressingVersion addressingVersion = binding.getAddressingVersion();
        if (addressingVersion == null) {
            return;
        }
        WsaTubeHelper wsaHelper = addressingVersion.getWsaHelper(wsdlPort, seiModel, binding);
        String string = action = responsePacket.getMessage().isFault() ? wsaHelper.getFaultAction(this, responsePacket) : wsaHelper.getOutputAction(this);
        if (action == null) {
            LOGGER.info("WSA headers are not added as value for wsa:Action cannot be resolved for this message");
            return;
        }
        this.populateAddressingHeaders(responsePacket, addressingVersion, binding.getSOAPVersion(), action, AddressingVersion.isRequired(binding));
    }

    public String toShortString() {
        return super.toString();
    }

    public String toString() {
        String content;
        StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        try {
            Message msg = this.getMessage();
            if (msg != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                XMLStreamWriter xmlWriter = XMLStreamWriterFactory.create(baos, "UTF-8");
                msg.copy().writeTo(xmlWriter);
                xmlWriter.flush();
                xmlWriter.close();
                baos.flush();
                XMLStreamWriterFactory.recycle(xmlWriter);
                byte[] bytes = baos.toByteArray();
                content = new String(bytes, "UTF-8");
            } else {
                content = "<none>";
            }
        }
        catch (Throwable t) {
            throw new WebServiceException(t);
        }
        buf.append(" Content: ").append(content);
        return buf.toString();
    }

    @Override
    protected BasePropertySet.PropertyMap getPropertyMap() {
        return model;
    }

    public Map<String, Object> asMapIncludingInvocationProperties() {
        final Map<String, Object> asMap = this.asMap();
        return new AbstractMap<String, Object>(){

            @Override
            public Object get(Object key) {
                Object o = asMap.get(key);
                if (o != null) {
                    return o;
                }
                return Packet.this.invocationProperties.get(key);
            }

            @Override
            public int size() {
                return asMap.size() + Packet.this.invocationProperties.size();
            }

            @Override
            public boolean containsKey(Object key) {
                if (asMap.containsKey(key)) {
                    return true;
                }
                return Packet.this.invocationProperties.containsKey(key);
            }

            @Override
            public Set<Map.Entry<String, Object>> entrySet() {
                final Set asMapEntries = asMap.entrySet();
                final Set<Map.Entry<String, Object>> ipEntries = Packet.this.invocationProperties.entrySet();
                return new AbstractSet<Map.Entry<String, Object>>(){

                    @Override
                    public Iterator<Map.Entry<String, Object>> iterator() {
                        final Iterator asMapIt = asMapEntries.iterator();
                        final Iterator ipIt = ipEntries.iterator();
                        return new Iterator<Map.Entry<String, Object>>(){

                            @Override
                            public boolean hasNext() {
                                return asMapIt.hasNext() || ipIt.hasNext();
                            }

                            @Override
                            public Map.Entry<String, Object> next() {
                                if (asMapIt.hasNext()) {
                                    return (Map.Entry)asMapIt.next();
                                }
                                return (Map.Entry)ipIt.next();
                            }

                            @Override
                            public void remove() {
                                throw new UnsupportedOperationException();
                            }
                        };
                    }

                    @Override
                    public int size() {
                        return asMap.size() + Packet.this.invocationProperties.size();
                    }
                };
            }

            @Override
            public Object put(String key, Object value) {
                if (Packet.this.supports(key)) {
                    return asMap.put(key, value);
                }
                return Packet.this.invocationProperties.put(key, value);
            }

            @Override
            public void clear() {
                asMap.clear();
                Packet.this.invocationProperties.clear();
            }

            @Override
            public Object remove(Object key) {
                if (Packet.this.supports(key)) {
                    return asMap.remove(key);
                }
                return Packet.this.invocationProperties.remove(key);
            }
        };
    }

    @Override
    public SOAPMessage getSOAPMessage() throws SOAPException {
        return this.getAsSOAPMessage();
    }

    @Override
    public SOAPMessage getAsSOAPMessage() throws SOAPException {
        Message msg = this.getMessage();
        if (msg == null) {
            return null;
        }
        if (msg instanceof MessageWritable) {
            ((MessageWritable)((Object)msg)).setMTOMConfiguration(this.mtomFeature);
        }
        return msg.readAsSOAPMessage(this, this.getState().isInbound());
    }

    public Codec getCodec() {
        WSBinding wsb;
        if (this.codec != null) {
            return this.codec;
        }
        if (this.endpoint != null) {
            this.codec = this.endpoint.createCodec();
        }
        if ((wsb = this.getBinding()) != null) {
            this.codec = wsb.getBindingId().createEncoder(wsb);
        }
        return this.codec;
    }

    @Override
    public ContentType writeTo(OutputStream out) throws IOException {
        Message msg = this.getInternalMessage();
        if (msg instanceof MessageWritable) {
            ((MessageWritable)((Object)msg)).setMTOMConfiguration(this.mtomFeature);
            return ((MessageWritable)((Object)msg)).writeTo(out);
        }
        return this.getCodec().encode(this, out);
    }

    public ContentType writeTo(WritableByteChannel buffer) {
        return this.getCodec().encode(this, buffer);
    }

    public Boolean getMtomRequest() {
        return this.mtomRequest;
    }

    public void setMtomRequest(Boolean mtomRequest) {
        this.mtomRequest = mtomRequest;
    }

    public Boolean getMtomAcceptable() {
        return this.mtomAcceptable;
    }

    public void checkMtomAcceptable() {
        if (this.checkMtomAcceptable == null) {
            this.checkMtomAcceptable = this.acceptableMimeTypes == null || this.isFastInfosetDisabled ? Boolean.valueOf(false) : Boolean.valueOf(this.acceptableMimeTypes.indexOf("application/xop+xml") != -1);
        }
        this.mtomAcceptable = this.checkMtomAcceptable;
    }

    public Boolean getFastInfosetAcceptable(String fiMimeType) {
        if (this.fastInfosetAcceptable == null) {
            this.fastInfosetAcceptable = this.acceptableMimeTypes == null || this.isFastInfosetDisabled ? Boolean.valueOf(false) : Boolean.valueOf(this.acceptableMimeTypes.indexOf(fiMimeType) != -1);
        }
        return this.fastInfosetAcceptable;
    }

    public void setMtomFeature(MTOMFeature mtomFeature) {
        this.mtomFeature = mtomFeature;
    }

    public MTOMFeature getMtomFeature() {
        WSBinding binding = this.getBinding();
        if (binding != null) {
            return binding.getFeature(MTOMFeature.class);
        }
        return this.mtomFeature;
    }

    @Override
    public ContentType getContentType() {
        if (this.contentType == null) {
            this.contentType = this.getInternalContentType();
        }
        if (this.contentType == null) {
            this.contentType = this.getCodec().getStaticContentType(this);
        }
        if (this.contentType == null) {
            // empty if block
        }
        return this.contentType;
    }

    public ContentType getInternalContentType() {
        Message msg = this.getInternalMessage();
        if (msg instanceof MessageWritable) {
            MessageWritable mw = (MessageWritable)((Object)msg);
            mw.setMTOMConfiguration(this.mtomFeature);
            return mw.getContentType();
        }
        return this.contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public boolean shouldUseMtom() {
        if (this.getState().isInbound()) {
            return this.isMtomContentType();
        }
        return this.shouldUseMtomOutbound();
    }

    private boolean shouldUseMtomOutbound() {
        MTOMFeature myMtomFeature = this.getMtomFeature();
        if (myMtomFeature != null && myMtomFeature.isEnabled()) {
            ContentType curContentType = this.getInternalContentType();
            if (curContentType != null && !this.isMtomContentType(curContentType)) {
                return false;
            }
            if (this.getMtomAcceptable() == null && this.getMtomRequest() == null) {
                return true;
            }
            if (this.getMtomAcceptable() != null && this.getMtomAcceptable().booleanValue() && this.getState().equals((Object)State.ServerResponse)) {
                return true;
            }
            if (this.getMtomRequest() != null && this.getMtomRequest().booleanValue() && this.getState().equals((Object)State.ServerResponse)) {
                return true;
            }
            if (this.getMtomRequest() != null && this.getMtomRequest().booleanValue() && this.getState().equals((Object)State.ClientRequest)) {
                return true;
            }
        }
        return false;
    }

    private boolean isMtomContentType() {
        return this.getInternalContentType() != null && this.isMtomContentType(this.getInternalContentType());
    }

    private boolean isMtomContentType(ContentType cType) {
        return cType.getContentType().contains("application/xop+xml");
    }

    public void addSatellite(@NotNull PropertySet satellite) {
        super.addSatellite(satellite);
    }

    public void addSatellite(@NotNull Class keyClass, @NotNull PropertySet satellite) {
        super.addSatellite(keyClass, satellite);
    }

    public void copySatelliteInto(@NotNull DistributedPropertySet r) {
        super.copySatelliteInto(r);
    }

    public void removeSatellite(PropertySet satellite) {
        super.removeSatellite(satellite);
    }

    public void setFastInfosetDisabled(boolean b) {
        this.isFastInfosetDisabled = b;
    }

    public SAAJFactory getSAAJFactory() {
        return this.saajFactory;
    }

    public void setSAAJFactory(SAAJFactory saajFactory) {
        this.saajFactory = saajFactory;
    }

    public static enum State {
        ServerRequest(true),
        ClientRequest(false),
        ServerResponse(false),
        ClientResponse(true);

        private boolean inbound;

        private State(boolean inbound) {
            this.inbound = inbound;
        }

        public boolean isInbound() {
            return this.inbound;
        }
    }

    public static enum Status {
        Request,
        Response,
        Unknown;


        public boolean isRequest() {
            return Request.equals((Object)this);
        }

        public boolean isResponse() {
            return Response.equals((Object)this);
        }
    }
}

