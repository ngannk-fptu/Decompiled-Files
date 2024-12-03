/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.addressing;

import com.sun.istack.NotNull;
import com.sun.xml.ws.addressing.WsaPropertyBag;
import com.sun.xml.ws.addressing.WsaTube;
import com.sun.xml.ws.addressing.model.ActionNotSupportedException;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.message.AddressingUtils;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.resources.AddressingMessages;
import javax.xml.ws.WebServiceException;

public class WsaClientTube
extends WsaTube {
    protected boolean expectReply = true;

    public WsaClientTube(WSDLPort wsdlPort, WSBinding binding, Tube next) {
        super(wsdlPort, binding, next);
    }

    public WsaClientTube(WsaClientTube that, TubeCloner cloner) {
        super(that, cloner);
    }

    @Override
    public WsaClientTube copy(TubeCloner cloner) {
        return new WsaClientTube(this, cloner);
    }

    @Override
    @NotNull
    public NextAction processRequest(Packet request) {
        this.expectReply = request.expectReply;
        return this.doInvoke(this.next, request);
    }

    @Override
    @NotNull
    public NextAction processResponse(Packet response) {
        if (response.getMessage() != null) {
            response = this.validateInboundHeaders(response);
            response.addSatellite(new WsaPropertyBag(this.addressingVersion, this.soapVersion, response));
            String msgId = AddressingUtils.getMessageID(response.getMessage().getHeaders(), this.addressingVersion, this.soapVersion);
            response.put("com.sun.xml.ws.addressing.WsaPropertyBag.MessageIdFromRequest", msgId);
        }
        return this.doReturnWith(response);
    }

    @Override
    protected void validateAction(Packet packet) {
        WSDLBoundOperation wbo = this.getWSDLBoundOperation(packet);
        if (wbo == null) {
            return;
        }
        String gotA = AddressingUtils.getAction(packet.getMessage().getHeaders(), this.addressingVersion, this.soapVersion);
        if (gotA == null) {
            throw new WebServiceException(AddressingMessages.VALIDATION_CLIENT_NULL_ACTION());
        }
        String expected = this.helper.getOutputAction(packet);
        if (expected != null && !gotA.equals(expected)) {
            throw new ActionNotSupportedException(gotA);
        }
    }
}

