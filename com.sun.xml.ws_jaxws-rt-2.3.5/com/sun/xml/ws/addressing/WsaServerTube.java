/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.xml.soap.SOAPFault
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.addressing;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.addressing.WsaPropertyBag;
import com.sun.xml.ws.addressing.WsaTube;
import com.sun.xml.ws.addressing.model.ActionNotSupportedException;
import com.sun.xml.ws.addressing.model.InvalidAddressingHeaderException;
import com.sun.xml.ws.api.EndpointAddress;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.addressing.NonAnonymousResponseProcessor;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.message.AddressingUtils;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.api.message.Messages;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.Fiber;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.ThrowableContainerPropertySet;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.client.Stub;
import com.sun.xml.ws.message.FaultDetailHeader;
import com.sun.xml.ws.resources.AddressingMessages;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.WebServiceException;

public class WsaServerTube
extends WsaTube {
    private WSEndpoint endpoint;
    private WSEndpointReference replyTo;
    private WSEndpointReference faultTo;
    private boolean isAnonymousRequired = false;
    protected boolean isEarlyBackchannelCloseAllowed = true;
    private WSDLBoundOperation wbo;
    public static final String REQUEST_MESSAGE_ID = "com.sun.xml.ws.addressing.request.messageID";
    private static final Logger LOGGER = Logger.getLogger(WsaServerTube.class.getName());

    public WsaServerTube(WSEndpoint endpoint, @NotNull WSDLPort wsdlPort, WSBinding binding, Tube next) {
        super(wsdlPort, binding, next);
        this.endpoint = endpoint;
    }

    public WsaServerTube(WsaServerTube that, TubeCloner cloner) {
        super(that, cloner);
        this.endpoint = that.endpoint;
    }

    @Override
    public WsaServerTube copy(TubeCloner cloner) {
        return new WsaServerTube(this, cloner);
    }

    @Override
    @NotNull
    public NextAction processRequest(Packet request) {
        String msgId;
        Message msg = request.getMessage();
        if (msg == null) {
            return this.doInvoke(this.next, request);
        }
        request.addSatellite(new WsaPropertyBag(this.addressingVersion, this.soapVersion, request));
        MessageHeaders hl = request.getMessage().getHeaders();
        try {
            this.replyTo = AddressingUtils.getReplyTo(hl, this.addressingVersion, this.soapVersion);
            this.faultTo = AddressingUtils.getFaultTo(hl, this.addressingVersion, this.soapVersion);
            msgId = AddressingUtils.getMessageID(hl, this.addressingVersion, this.soapVersion);
        }
        catch (InvalidAddressingHeaderException e) {
            LOGGER.log(Level.WARNING, this.addressingVersion.getInvalidMapText() + ", Problem header:" + e.getProblemHeader() + ", Reason: " + e.getSubsubcode(), (Throwable)((Object)e));
            hl.remove(e.getProblemHeader());
            SOAPFault soapFault = this.helper.createInvalidAddressingHeaderFault(e, this.addressingVersion);
            if (this.wsdlPort != null && request.getMessage().isOneWay(this.wsdlPort)) {
                Packet response = request.createServerResponse(null, this.wsdlPort, null, this.binding);
                return this.doReturnWith(response);
            }
            Message m = Messages.create(soapFault);
            if (this.soapVersion == SOAPVersion.SOAP_11) {
                FaultDetailHeader s11FaultDetailHeader = new FaultDetailHeader(this.addressingVersion, this.addressingVersion.problemHeaderQNameTag.getLocalPart(), e.getProblemHeader());
                m.getHeaders().add(s11FaultDetailHeader);
            }
            Packet response = request.createServerResponse(m, this.wsdlPort, null, this.binding);
            return this.doReturnWith(response);
        }
        if (this.replyTo == null) {
            this.replyTo = this.addressingVersion.anonymousEpr;
        }
        if (this.faultTo == null) {
            this.faultTo = this.replyTo;
        }
        request.put("com.sun.xml.ws.addressing.WsaPropertyBag.ReplyToFromRequest", this.replyTo);
        request.put("com.sun.xml.ws.addressing.WsaPropertyBag.FaultToFromRequest", this.faultTo);
        request.put("com.sun.xml.ws.addressing.WsaPropertyBag.MessageIdFromRequest", msgId);
        this.wbo = this.getWSDLBoundOperation(request);
        this.isAnonymousRequired = this.isAnonymousRequired(this.wbo);
        Packet p = this.validateInboundHeaders(request);
        if (p.getMessage() == null) {
            return this.doReturnWith(p);
        }
        if (p.getMessage().isFault()) {
            if (this.isEarlyBackchannelCloseAllowed && !this.isAnonymousRequired && !this.faultTo.isAnonymous() && request.transportBackChannel != null) {
                request.transportBackChannel.close();
            }
            return this.processResponse(p);
        }
        if (this.isEarlyBackchannelCloseAllowed && !this.isAnonymousRequired && !this.replyTo.isAnonymous() && !this.faultTo.isAnonymous() && request.transportBackChannel != null) {
            request.transportBackChannel.close();
        }
        return this.doInvoke(this.next, p);
    }

    protected boolean isAnonymousRequired(@Nullable WSDLBoundOperation wbo) {
        return false;
    }

    protected void checkAnonymousSemantics(WSDLBoundOperation wbo, WSEndpointReference replyTo, WSEndpointReference faultTo) {
    }

    @Override
    @NotNull
    public NextAction processException(Throwable t) {
        Packet response = Fiber.current().getPacket();
        ThrowableContainerPropertySet tc = response.getSatellite(ThrowableContainerPropertySet.class);
        if (tc == null) {
            tc = new ThrowableContainerPropertySet(t);
            response.addSatellite(tc);
        } else if (t != tc.getThrowable()) {
            tc.setThrowable(t);
        }
        return this.processResponse(response.endpoint.createServiceResponseForException(tc, response, this.soapVersion, this.wsdlPort, response.endpoint.getSEIModel(), this.binding));
    }

    @Override
    @NotNull
    public NextAction processResponse(Packet response) {
        EndpointAddress adrs;
        WSEndpointReference target;
        Message msg = response.getMessage();
        if (msg == null) {
            return this.doReturnWith(response);
        }
        String to = AddressingUtils.getTo(msg.getHeaders(), this.addressingVersion, this.soapVersion);
        if (to != null) {
            this.replyTo = this.faultTo = new WSEndpointReference(to, this.addressingVersion);
        }
        if (this.replyTo == null) {
            this.replyTo = (WSEndpointReference)response.get("com.sun.xml.ws.addressing.WsaPropertyBag.ReplyToFromRequest");
        }
        if (this.faultTo == null) {
            this.faultTo = (WSEndpointReference)response.get("com.sun.xml.ws.addressing.WsaPropertyBag.FaultToFromRequest");
        }
        WSEndpointReference wSEndpointReference = target = msg.isFault() ? this.faultTo : this.replyTo;
        if (target == null && response.proxy instanceof Stub) {
            target = ((Stub)response.proxy).getWSEndpointReference();
        }
        if (target == null || target.isAnonymous() || this.isAnonymousRequired) {
            return this.doReturnWith(response);
        }
        if (target.isNone()) {
            response.setMessage(null);
            return this.doReturnWith(response);
        }
        if (this.wsdlPort != null && response.getMessage().isOneWay(this.wsdlPort)) {
            LOGGER.fine(AddressingMessages.NON_ANONYMOUS_RESPONSE_ONEWAY());
            return this.doReturnWith(response);
        }
        if (this.wbo != null || response.soapAction == null) {
            String action;
            String string = action = response.getMessage().isFault() ? this.helper.getFaultAction(this.wbo, response) : this.helper.getOutputAction(this.wbo);
            if (response.soapAction == null || action != null && !action.equals("http://jax-ws.dev.java.net/addressing/output-action-not-set")) {
                response.soapAction = action;
            }
        }
        response.expectReply = false;
        try {
            adrs = new EndpointAddress(URI.create(target.getAddress()));
        }
        catch (NullPointerException e) {
            throw new WebServiceException((Throwable)e);
        }
        catch (IllegalArgumentException e) {
            throw new WebServiceException((Throwable)e);
        }
        response.endpointAddress = adrs;
        if (response.isAdapterDeliversNonAnonymousResponse) {
            return this.doReturnWith(response);
        }
        return this.doReturnWith(NonAnonymousResponseProcessor.getDefault().process(response));
    }

    @Override
    protected void validateAction(Packet packet) {
        WSDLBoundOperation wsdlBoundOperation = this.getWSDLBoundOperation(packet);
        if (wsdlBoundOperation == null) {
            return;
        }
        String gotA = AddressingUtils.getAction(packet.getMessage().getHeaders(), this.addressingVersion, this.soapVersion);
        if (gotA == null) {
            throw new WebServiceException(AddressingMessages.VALIDATION_SERVER_NULL_ACTION());
        }
        String expected = this.helper.getInputAction(packet);
        String soapAction = this.helper.getSOAPAction(packet);
        if (this.helper.isInputActionDefault(packet) && soapAction != null && !soapAction.equals("")) {
            expected = soapAction;
        }
        if (expected != null && !gotA.equals(expected)) {
            throw new ActionNotSupportedException(gotA);
        }
    }

    @Override
    protected void checkMessageAddressingProperties(Packet packet) {
        super.checkMessageAddressingProperties(packet);
        WSDLBoundOperation wsdlBoundOperation = this.getWSDLBoundOperation(packet);
        this.checkAnonymousSemantics(wsdlBoundOperation, this.replyTo, this.faultTo);
        this.checkNonAnonymousAddresses(this.replyTo, this.faultTo);
    }

    private void checkNonAnonymousAddresses(WSEndpointReference replyTo, WSEndpointReference faultTo) {
        if (!replyTo.isAnonymous()) {
            try {
                new EndpointAddress(URI.create(replyTo.getAddress()));
            }
            catch (Exception e) {
                throw new InvalidAddressingHeaderException(this.addressingVersion.replyToTag, this.addressingVersion.invalidAddressTag);
            }
        }
    }
}

