/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.addressing.v200408;

import com.sun.istack.NotNull;
import com.sun.xml.ws.addressing.WsaServerTube;
import com.sun.xml.ws.addressing.model.MissingAddressingHeaderException;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.developer.MemberSubmissionAddressing;
import com.sun.xml.ws.developer.MemberSubmissionAddressingFeature;

public class MemberSubmissionWsaServerTube
extends WsaServerTube {
    private final MemberSubmissionAddressing.Validation validation;

    public MemberSubmissionWsaServerTube(WSEndpoint endpoint, @NotNull WSDLPort wsdlPort, WSBinding binding, Tube next) {
        super(endpoint, wsdlPort, binding, next);
        this.validation = binding.getFeature(MemberSubmissionAddressingFeature.class).getValidation();
    }

    public MemberSubmissionWsaServerTube(MemberSubmissionWsaServerTube that, TubeCloner cloner) {
        super(that, cloner);
        this.validation = that.validation;
    }

    @Override
    public MemberSubmissionWsaServerTube copy(TubeCloner cloner) {
        return new MemberSubmissionWsaServerTube(this, cloner);
    }

    @Override
    protected void checkMandatoryHeaders(Packet packet, boolean foundAction, boolean foundTo, boolean foundReplyTo, boolean foundFaultTo, boolean foundMessageId, boolean foundRelatesTo) {
        WSDLBoundOperation wbo;
        super.checkMandatoryHeaders(packet, foundAction, foundTo, foundReplyTo, foundFaultTo, foundMessageId, foundRelatesTo);
        if (!foundTo) {
            throw new MissingAddressingHeaderException(this.addressingVersion.toTag, packet);
        }
        if (this.wsdlPort != null && (wbo = this.getWSDLBoundOperation(packet)) != null && !wbo.getOperation().isOneWay() && !foundReplyTo) {
            throw new MissingAddressingHeaderException(this.addressingVersion.replyToTag, packet);
        }
        if (!this.validation.equals((Object)MemberSubmissionAddressing.Validation.LAX) && (foundReplyTo || foundFaultTo) && !foundMessageId) {
            throw new MissingAddressingHeaderException(this.addressingVersion.messageIDTag, packet);
        }
    }
}

