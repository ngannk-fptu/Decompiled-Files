/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.addressing.v200408;

import com.sun.xml.ws.addressing.WsaClientTube;
import com.sun.xml.ws.addressing.model.MissingAddressingHeaderException;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.message.AddressingUtils;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.developer.MemberSubmissionAddressing;
import com.sun.xml.ws.developer.MemberSubmissionAddressingFeature;

public class MemberSubmissionWsaClientTube
extends WsaClientTube {
    private final MemberSubmissionAddressing.Validation validation;

    public MemberSubmissionWsaClientTube(WSDLPort wsdlPort, WSBinding binding, Tube next) {
        super(wsdlPort, binding, next);
        this.validation = binding.getFeature(MemberSubmissionAddressingFeature.class).getValidation();
    }

    public MemberSubmissionWsaClientTube(MemberSubmissionWsaClientTube that, TubeCloner cloner) {
        super(that, cloner);
        this.validation = that.validation;
    }

    @Override
    public MemberSubmissionWsaClientTube copy(TubeCloner cloner) {
        return new MemberSubmissionWsaClientTube(this, cloner);
    }

    @Override
    protected void checkMandatoryHeaders(Packet packet, boolean foundAction, boolean foundTo, boolean foundReplyTo, boolean foundFaultTo, boolean foundMessageID, boolean foundRelatesTo) {
        super.checkMandatoryHeaders(packet, foundAction, foundTo, foundReplyTo, foundFaultTo, foundMessageID, foundRelatesTo);
        if (!foundTo) {
            throw new MissingAddressingHeaderException(this.addressingVersion.toTag, packet);
        }
        if (!this.validation.equals((Object)MemberSubmissionAddressing.Validation.LAX) && this.expectReply && packet.getMessage() != null && !foundRelatesTo) {
            String action = AddressingUtils.getAction(packet.getMessage().getHeaders(), this.addressingVersion, this.soapVersion);
            if (!packet.getMessage().isFault() || !action.equals(this.addressingVersion.getDefaultFaultAction())) {
                throw new MissingAddressingHeaderException(this.addressingVersion.relatesToTag, packet);
            }
        }
    }
}

