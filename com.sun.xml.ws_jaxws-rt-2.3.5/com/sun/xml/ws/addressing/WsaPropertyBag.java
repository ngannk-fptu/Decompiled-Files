/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.addressing;

import com.oracle.webservices.api.message.BasePropertySet;
import com.oracle.webservices.api.message.PropertySet;
import com.sun.istack.NotNull;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.message.AddressingUtils;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.Packet;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

public class WsaPropertyBag
extends BasePropertySet {
    public static final String WSA_REPLYTO_FROM_REQUEST = "com.sun.xml.ws.addressing.WsaPropertyBag.ReplyToFromRequest";
    public static final String WSA_FAULTTO_FROM_REQUEST = "com.sun.xml.ws.addressing.WsaPropertyBag.FaultToFromRequest";
    public static final String WSA_MSGID_FROM_REQUEST = "com.sun.xml.ws.addressing.WsaPropertyBag.MessageIdFromRequest";
    public static final String WSA_TO = "com.sun.xml.ws.addressing.WsaPropertyBag.To";
    @NotNull
    private final AddressingVersion addressingVersion;
    @NotNull
    private final SOAPVersion soapVersion;
    @NotNull
    private final Packet packet;
    private static final BasePropertySet.PropertyMap model = WsaPropertyBag.parse(WsaPropertyBag.class);
    private WSEndpointReference _replyToFromRequest = null;
    private WSEndpointReference _faultToFromRequest = null;
    private String _msgIdFromRequest = null;

    public WsaPropertyBag(AddressingVersion addressingVersion, SOAPVersion soapVersion, Packet packet) {
        this.addressingVersion = addressingVersion;
        this.soapVersion = soapVersion;
        this.packet = packet;
    }

    @PropertySet.Property(value={"com.sun.xml.ws.api.addressing.to"})
    public String getTo() throws XMLStreamException {
        if (this.packet.getMessage() == null) {
            return null;
        }
        Header h = this.packet.getMessage().getHeaders().get(this.addressingVersion.toTag, false);
        if (h == null) {
            return null;
        }
        return h.getStringContent();
    }

    @PropertySet.Property(value={"com.sun.xml.ws.addressing.WsaPropertyBag.To"})
    public WSEndpointReference getToAsReference() throws XMLStreamException {
        if (this.packet.getMessage() == null) {
            return null;
        }
        Header h = this.packet.getMessage().getHeaders().get(this.addressingVersion.toTag, false);
        if (h == null) {
            return null;
        }
        return new WSEndpointReference(h.getStringContent(), this.addressingVersion);
    }

    @PropertySet.Property(value={"com.sun.xml.ws.api.addressing.from"})
    public WSEndpointReference getFrom() throws XMLStreamException {
        return this.getEPR(this.addressingVersion.fromTag);
    }

    @PropertySet.Property(value={"com.sun.xml.ws.api.addressing.action"})
    public String getAction() {
        if (this.packet.getMessage() == null) {
            return null;
        }
        Header h = this.packet.getMessage().getHeaders().get(this.addressingVersion.actionTag, false);
        if (h == null) {
            return null;
        }
        return h.getStringContent();
    }

    @PropertySet.Property(value={"com.sun.xml.ws.api.addressing.messageId", "com.sun.xml.ws.addressing.request.messageID"})
    public String getMessageID() {
        if (this.packet.getMessage() == null) {
            return null;
        }
        return AddressingUtils.getMessageID(this.packet.getMessage().getHeaders(), this.addressingVersion, this.soapVersion);
    }

    private WSEndpointReference getEPR(QName tag) throws XMLStreamException {
        if (this.packet.getMessage() == null) {
            return null;
        }
        Header h = this.packet.getMessage().getHeaders().get(tag, false);
        if (h == null) {
            return null;
        }
        return h.readAsEPR(this.addressingVersion);
    }

    @Override
    protected BasePropertySet.PropertyMap getPropertyMap() {
        return model;
    }

    @PropertySet.Property(value={"com.sun.xml.ws.addressing.WsaPropertyBag.ReplyToFromRequest"})
    public WSEndpointReference getReplyToFromRequest() {
        return this._replyToFromRequest;
    }

    public void setReplyToFromRequest(WSEndpointReference ref) {
        this._replyToFromRequest = ref;
    }

    @PropertySet.Property(value={"com.sun.xml.ws.addressing.WsaPropertyBag.FaultToFromRequest"})
    public WSEndpointReference getFaultToFromRequest() {
        return this._faultToFromRequest;
    }

    public void setFaultToFromRequest(WSEndpointReference ref) {
        this._faultToFromRequest = ref;
    }

    @PropertySet.Property(value={"com.sun.xml.ws.addressing.WsaPropertyBag.MessageIdFromRequest"})
    public String getMessageIdFromRequest() {
        return this._msgIdFromRequest;
    }

    public void setMessageIdFromRequest(String id) {
        this._msgIdFromRequest = id;
    }
}

