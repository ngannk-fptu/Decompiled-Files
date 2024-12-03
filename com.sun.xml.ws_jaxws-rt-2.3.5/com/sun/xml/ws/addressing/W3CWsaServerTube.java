/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.xml.ws.soap.AddressingFeature
 *  javax.xml.ws.soap.AddressingFeature$Responses
 */
package com.sun.xml.ws.addressing;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.addressing.W3CAddressingConstants;
import com.sun.xml.ws.addressing.WsaServerTube;
import com.sun.xml.ws.addressing.model.InvalidAddressingHeaderException;
import com.sun.xml.ws.addressing.model.MissingAddressingHeaderException;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.server.WSEndpoint;
import javax.xml.ws.soap.AddressingFeature;

public class W3CWsaServerTube
extends WsaServerTube {
    private final AddressingFeature af;

    public W3CWsaServerTube(WSEndpoint endpoint, @NotNull WSDLPort wsdlPort, WSBinding binding, Tube next) {
        super(endpoint, wsdlPort, binding, next);
        this.af = binding.getFeature(AddressingFeature.class);
    }

    public W3CWsaServerTube(W3CWsaServerTube that, TubeCloner cloner) {
        super(that, cloner);
        this.af = that.af;
    }

    @Override
    public W3CWsaServerTube copy(TubeCloner cloner) {
        return new W3CWsaServerTube(this, cloner);
    }

    @Override
    protected void checkMandatoryHeaders(Packet packet, boolean foundAction, boolean foundTo, boolean foundReplyTo, boolean foundFaultTo, boolean foundMessageId, boolean foundRelatesTo) {
        super.checkMandatoryHeaders(packet, foundAction, foundTo, foundReplyTo, foundFaultTo, foundMessageId, foundRelatesTo);
        WSDLBoundOperation wbo = this.getWSDLBoundOperation(packet);
        if (wbo != null && !wbo.getOperation().isOneWay() && !foundMessageId) {
            throw new MissingAddressingHeaderException(this.addressingVersion.messageIDTag, packet);
        }
    }

    @Override
    protected boolean isAnonymousRequired(@Nullable WSDLBoundOperation wbo) {
        return this.getResponseRequirement(wbo) == WSDLBoundOperation.ANONYMOUS.required;
    }

    private WSDLBoundOperation.ANONYMOUS getResponseRequirement(@Nullable WSDLBoundOperation wbo) {
        try {
            if (this.af.getResponses() == AddressingFeature.Responses.ANONYMOUS) {
                return WSDLBoundOperation.ANONYMOUS.required;
            }
            if (this.af.getResponses() == AddressingFeature.Responses.NON_ANONYMOUS) {
                return WSDLBoundOperation.ANONYMOUS.prohibited;
            }
        }
        catch (NoSuchMethodError noSuchMethodError) {
            // empty catch block
        }
        return wbo != null ? wbo.getAnonymous() : WSDLBoundOperation.ANONYMOUS.optional;
    }

    @Override
    protected void checkAnonymousSemantics(WSDLBoundOperation wbo, WSEndpointReference replyTo, WSEndpointReference faultTo) {
        String replyToValue = null;
        String faultToValue = null;
        if (replyTo != null) {
            replyToValue = replyTo.getAddress();
        }
        if (faultTo != null) {
            faultToValue = faultTo.getAddress();
        }
        WSDLBoundOperation.ANONYMOUS responseRequirement = this.getResponseRequirement(wbo);
        switch (responseRequirement) {
            case prohibited: {
                if (replyToValue != null && replyToValue.equals(this.addressingVersion.anonymousUri)) {
                    throw new InvalidAddressingHeaderException(this.addressingVersion.replyToTag, W3CAddressingConstants.ONLY_NON_ANONYMOUS_ADDRESS_SUPPORTED);
                }
                if (faultToValue == null || !faultToValue.equals(this.addressingVersion.anonymousUri)) break;
                throw new InvalidAddressingHeaderException(this.addressingVersion.faultToTag, W3CAddressingConstants.ONLY_NON_ANONYMOUS_ADDRESS_SUPPORTED);
            }
            case required: {
                if (replyToValue != null && !replyToValue.equals(this.addressingVersion.anonymousUri)) {
                    throw new InvalidAddressingHeaderException(this.addressingVersion.replyToTag, W3CAddressingConstants.ONLY_ANONYMOUS_ADDRESS_SUPPORTED);
                }
                if (faultToValue == null || faultToValue.equals(this.addressingVersion.anonymousUri)) break;
                throw new InvalidAddressingHeaderException(this.addressingVersion.faultToTag, W3CAddressingConstants.ONLY_ANONYMOUS_ADDRESS_SUPPORTED);
            }
        }
    }
}

