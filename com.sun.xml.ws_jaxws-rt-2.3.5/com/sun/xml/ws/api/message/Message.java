/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  com.sun.xml.bind.api.Bridge
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.soap.MimeHeaders
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPMessage
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.api.message;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.bind.api.Bridge;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.message.AddressingUtils;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.api.message.MessageMetadata;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.JavaMethod;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.WSDLOperationMapping;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.fault.SOAPFaultBuilder;
import com.sun.xml.ws.message.AttachmentSetImpl;
import com.sun.xml.ws.message.StringHeader;
import com.sun.xml.ws.spi.db.XMLBridge;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public abstract class Message {
    private boolean isProtocolMessage = false;
    protected AttachmentSet attachmentSet;
    private WSDLBoundOperation operation = null;
    private WSDLOperationMapping wsdlOperationMapping = null;
    private MessageMetadata messageMetadata = null;
    private Boolean isOneWay;

    boolean isProtocolMessage() {
        return this.isProtocolMessage;
    }

    void setIsProtocolMessage() {
        this.isProtocolMessage = true;
    }

    public abstract boolean hasHeaders();

    @NotNull
    public abstract MessageHeaders getHeaders();

    @NotNull
    public AttachmentSet getAttachments() {
        if (this.attachmentSet == null) {
            this.attachmentSet = new AttachmentSetImpl();
        }
        return this.attachmentSet;
    }

    protected boolean hasAttachments() {
        return this.attachmentSet != null;
    }

    public void setMessageMedadata(MessageMetadata metadata) {
        this.messageMetadata = metadata;
    }

    @Deprecated
    @Nullable
    public final WSDLBoundOperation getOperation(@NotNull WSDLBoundPortType boundPortType) {
        if (this.operation == null && this.messageMetadata != null) {
            if (this.wsdlOperationMapping == null) {
                this.wsdlOperationMapping = this.messageMetadata.getWSDLOperationMapping();
            }
            if (this.wsdlOperationMapping != null) {
                this.operation = this.wsdlOperationMapping.getWSDLBoundOperation();
            }
        }
        if (this.operation == null) {
            this.operation = boundPortType.getOperation(this.getPayloadNamespaceURI(), this.getPayloadLocalPart());
        }
        return this.operation;
    }

    @Deprecated
    @Nullable
    public final WSDLBoundOperation getOperation(@NotNull WSDLPort port) {
        return this.getOperation(port.getBinding());
    }

    @Deprecated
    @Nullable
    public final JavaMethod getMethod(@NotNull SEIModel seiModel) {
        String nsUri;
        if (this.wsdlOperationMapping == null && this.messageMetadata != null) {
            this.wsdlOperationMapping = this.messageMetadata.getWSDLOperationMapping();
        }
        if (this.wsdlOperationMapping != null) {
            return this.wsdlOperationMapping.getJavaMethod();
        }
        String localPart = this.getPayloadLocalPart();
        if (localPart == null) {
            localPart = "";
            nsUri = "";
        } else {
            nsUri = this.getPayloadNamespaceURI();
        }
        QName name = new QName(nsUri, localPart);
        return seiModel.getJavaMethod(name);
    }

    public boolean isOneWay(@NotNull WSDLPort port) {
        if (this.isOneWay == null) {
            WSDLBoundOperation op = this.getOperation(port);
            this.isOneWay = op != null ? Boolean.valueOf(op.getOperation().isOneWay()) : Boolean.valueOf(false);
        }
        return this.isOneWay;
    }

    public final void assertOneWay(boolean value) {
        assert (this.isOneWay == null || this.isOneWay == value);
        this.isOneWay = value;
    }

    @Nullable
    public abstract String getPayloadLocalPart();

    public abstract String getPayloadNamespaceURI();

    public abstract boolean hasPayload();

    public boolean isFault() {
        String localPart = this.getPayloadLocalPart();
        if (localPart == null || !localPart.equals("Fault")) {
            return false;
        }
        String nsUri = this.getPayloadNamespaceURI();
        return nsUri.equals(SOAPVersion.SOAP_11.nsUri) || nsUri.equals(SOAPVersion.SOAP_12.nsUri);
    }

    @Nullable
    public QName getFirstDetailEntryName() {
        assert (this.isFault());
        Message msg = this.copy();
        try {
            SOAPFaultBuilder fault = SOAPFaultBuilder.create(msg);
            return fault.getFirstDetailEntryName();
        }
        catch (JAXBException e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    public abstract Source readEnvelopeAsSource();

    public abstract Source readPayloadAsSource();

    public abstract SOAPMessage readAsSOAPMessage() throws SOAPException;

    public SOAPMessage readAsSOAPMessage(Packet packet, boolean inbound) throws SOAPException {
        return this.readAsSOAPMessage();
    }

    public static Map<String, List<String>> getTransportHeaders(Packet packet) {
        return Message.getTransportHeaders(packet, packet.getState().isInbound());
    }

    public static Map<String, List<String>> getTransportHeaders(Packet packet, boolean inbound) {
        String key;
        Map headers = null;
        String string = key = inbound ? "com.sun.xml.ws.api.message.packet.inbound.transport.headers" : "com.sun.xml.ws.api.message.packet.outbound.transport.headers";
        if (packet.supports(key)) {
            headers = (Map)packet.get(key);
        }
        return headers;
    }

    public static void addSOAPMimeHeaders(MimeHeaders mh, Map<String, List<String>> headers) {
        for (Map.Entry<String, List<String>> e : headers.entrySet()) {
            if (e.getKey().equalsIgnoreCase("Content-Type")) continue;
            for (String value : e.getValue()) {
                mh.addHeader(e.getKey(), value);
            }
        }
    }

    public abstract <T> T readPayloadAsJAXB(Unmarshaller var1) throws JAXBException;

    public abstract <T> T readPayloadAsJAXB(Bridge<T> var1) throws JAXBException;

    public abstract <T> T readPayloadAsJAXB(XMLBridge<T> var1) throws JAXBException;

    public abstract XMLStreamReader readPayload() throws XMLStreamException;

    public void consume() {
    }

    public abstract void writePayloadTo(XMLStreamWriter var1) throws XMLStreamException;

    public abstract void writeTo(XMLStreamWriter var1) throws XMLStreamException;

    public abstract void writeTo(ContentHandler var1, ErrorHandler var2) throws SAXException;

    public abstract Message copy();

    public final Message copyFrom(Message m) {
        this.isProtocolMessage = m.isProtocolMessage;
        return this;
    }

    @Deprecated
    @NotNull
    public String getID(@NotNull WSBinding binding) {
        return this.getID(binding.getAddressingVersion(), binding.getSOAPVersion());
    }

    @Deprecated
    @NotNull
    public String getID(AddressingVersion av, SOAPVersion sv) {
        String uuid = null;
        if (av != null) {
            uuid = AddressingUtils.getMessageID(this.getHeaders(), av, sv);
        }
        if (uuid == null) {
            uuid = Message.generateMessageID();
            this.getHeaders().add(new StringHeader(av.messageIDTag, uuid));
        }
        return uuid;
    }

    public static String generateMessageID() {
        return "uuid:" + UUID.randomUUID().toString();
    }

    public SOAPVersion getSOAPVersion() {
        return null;
    }
}

