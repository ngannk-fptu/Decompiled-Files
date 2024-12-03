/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.xml.soap.SOAPFault
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.soap.AddressingFeature
 *  javax.xml.ws.soap.SOAPBinding
 */
package com.sun.xml.ws.addressing;

import com.sun.istack.NotNull;
import com.sun.xml.ws.addressing.WsaTubeHelper;
import com.sun.xml.ws.addressing.model.InvalidAddressingHeaderException;
import com.sun.xml.ws.addressing.model.MissingAddressingHeaderException;
import com.sun.xml.ws.addressing.v200408.WsaTubeHelperImpl;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.message.AddressingUtils;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Messages;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.ws.developer.MemberSubmissionAddressingFeature;
import com.sun.xml.ws.message.FaultDetailHeader;
import com.sun.xml.ws.resources.AddressingMessages;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPFault;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.AddressingFeature;
import javax.xml.ws.soap.SOAPBinding;

abstract class WsaTube
extends AbstractFilterTubeImpl {
    @NotNull
    protected final WSDLPort wsdlPort;
    protected final WSBinding binding;
    final WsaTubeHelper helper;
    @NotNull
    protected final AddressingVersion addressingVersion;
    protected final SOAPVersion soapVersion;
    private final boolean addressingRequired;
    private static final Logger LOGGER = Logger.getLogger(WsaTube.class.getName());

    public WsaTube(WSDLPort wsdlPort, WSBinding binding, Tube next) {
        super(next);
        this.wsdlPort = wsdlPort;
        this.binding = binding;
        this.addKnownHeadersToBinding(binding);
        this.addressingVersion = binding.getAddressingVersion();
        this.soapVersion = binding.getSOAPVersion();
        this.helper = this.getTubeHelper();
        this.addressingRequired = AddressingVersion.isRequired(binding);
    }

    public WsaTube(WsaTube that, TubeCloner cloner) {
        super(that, cloner);
        this.wsdlPort = that.wsdlPort;
        this.binding = that.binding;
        this.helper = that.helper;
        this.addressingVersion = that.addressingVersion;
        this.soapVersion = that.soapVersion;
        this.addressingRequired = that.addressingRequired;
    }

    private void addKnownHeadersToBinding(WSBinding binding) {
        for (AddressingVersion addrVersion : AddressingVersion.values()) {
            binding.addKnownHeader(addrVersion.actionTag);
            binding.addKnownHeader(addrVersion.faultDetailTag);
            binding.addKnownHeader(addrVersion.faultToTag);
            binding.addKnownHeader(addrVersion.fromTag);
            binding.addKnownHeader(addrVersion.messageIDTag);
            binding.addKnownHeader(addrVersion.relatesToTag);
            binding.addKnownHeader(addrVersion.replyToTag);
            binding.addKnownHeader(addrVersion.toTag);
        }
    }

    @Override
    @NotNull
    public NextAction processException(Throwable t) {
        return super.processException(t);
    }

    protected WsaTubeHelper getTubeHelper() {
        if (this.binding.isFeatureEnabled(AddressingFeature.class)) {
            return new com.sun.xml.ws.addressing.WsaTubeHelperImpl(this.wsdlPort, null, this.binding);
        }
        if (this.binding.isFeatureEnabled(MemberSubmissionAddressingFeature.class)) {
            return new WsaTubeHelperImpl(this.wsdlPort, null, this.binding);
        }
        throw new WebServiceException(AddressingMessages.ADDRESSING_NOT_ENABLED(this.getClass().getSimpleName()));
    }

    protected Packet validateInboundHeaders(Packet packet) {
        FaultDetailHeader s11FaultDetailHeader;
        SOAPFault soapFault;
        try {
            this.checkMessageAddressingProperties(packet);
            return packet;
        }
        catch (InvalidAddressingHeaderException e) {
            LOGGER.log(Level.WARNING, this.addressingVersion.getInvalidMapText() + ", Problem header:" + e.getProblemHeader() + ", Reason: " + e.getSubsubcode(), (Throwable)((Object)e));
            soapFault = this.helper.createInvalidAddressingHeaderFault(e, this.addressingVersion);
            s11FaultDetailHeader = new FaultDetailHeader(this.addressingVersion, this.addressingVersion.problemHeaderQNameTag.getLocalPart(), e.getProblemHeader());
        }
        catch (MissingAddressingHeaderException e) {
            LOGGER.log(Level.WARNING, this.addressingVersion.getMapRequiredText() + ", Problem header:" + e.getMissingHeaderQName(), (Throwable)((Object)e));
            soapFault = this.helper.newMapRequiredFault(e);
            s11FaultDetailHeader = new FaultDetailHeader(this.addressingVersion, this.addressingVersion.problemHeaderQNameTag.getLocalPart(), e.getMissingHeaderQName());
        }
        if (soapFault != null) {
            if (this.wsdlPort != null && packet.getMessage().isOneWay(this.wsdlPort)) {
                return packet.createServerResponse(null, this.wsdlPort, null, this.binding);
            }
            Message m = Messages.create(soapFault);
            if (this.soapVersion == SOAPVersion.SOAP_11) {
                m.getHeaders().add(s11FaultDetailHeader);
            }
            return packet.createServerResponse(m, this.wsdlPort, null, this.binding);
        }
        return packet;
    }

    protected void checkMessageAddressingProperties(Packet packet) {
        this.checkCardinality(packet);
    }

    final boolean isAddressingEngagedOrRequired(Packet packet, WSBinding binding) {
        if (AddressingVersion.isRequired(binding)) {
            return true;
        }
        if (packet == null) {
            return false;
        }
        if (packet.getMessage() == null) {
            return false;
        }
        if (packet.getMessage().getHeaders() != null) {
            return false;
        }
        String action = AddressingUtils.getAction(packet.getMessage().getHeaders(), this.addressingVersion, this.soapVersion);
        if (action == null) {
            return true;
        }
        return true;
    }

    protected void checkCardinality(Packet packet) {
        Message message = packet.getMessage();
        if (message == null) {
            if (this.addressingRequired) {
                throw new WebServiceException(AddressingMessages.NULL_MESSAGE());
            }
            return;
        }
        Iterator<Header> hIter = message.getHeaders().getHeaders(this.addressingVersion.nsUri, true);
        if (!hIter.hasNext()) {
            if (this.addressingRequired) {
                throw new MissingAddressingHeaderException(this.addressingVersion.actionTag, packet);
            }
            return;
        }
        boolean foundFrom = false;
        boolean foundTo = false;
        boolean foundReplyTo = false;
        boolean foundFaultTo = false;
        boolean foundAction = false;
        boolean foundMessageId = false;
        boolean foundRelatesTo = false;
        QName duplicateHeader = null;
        while (hIter.hasNext()) {
            Header h = hIter.next();
            if (!this.isInCurrentRole(h, this.binding)) continue;
            String local = h.getLocalPart();
            if (local.equals(this.addressingVersion.fromTag.getLocalPart())) {
                if (foundFrom) {
                    duplicateHeader = this.addressingVersion.fromTag;
                    break;
                }
                foundFrom = true;
                continue;
            }
            if (local.equals(this.addressingVersion.toTag.getLocalPart())) {
                if (foundTo) {
                    duplicateHeader = this.addressingVersion.toTag;
                    break;
                }
                foundTo = true;
                continue;
            }
            if (local.equals(this.addressingVersion.replyToTag.getLocalPart())) {
                if (foundReplyTo) {
                    duplicateHeader = this.addressingVersion.replyToTag;
                    break;
                }
                foundReplyTo = true;
                try {
                    h.readAsEPR(this.addressingVersion);
                    continue;
                }
                catch (XMLStreamException e) {
                    throw new WebServiceException(AddressingMessages.REPLY_TO_CANNOT_PARSE(), (Throwable)e);
                }
            }
            if (local.equals(this.addressingVersion.faultToTag.getLocalPart())) {
                if (foundFaultTo) {
                    duplicateHeader = this.addressingVersion.faultToTag;
                    break;
                }
                foundFaultTo = true;
                try {
                    h.readAsEPR(this.addressingVersion);
                    continue;
                }
                catch (XMLStreamException e) {
                    throw new WebServiceException(AddressingMessages.FAULT_TO_CANNOT_PARSE(), (Throwable)e);
                }
            }
            if (local.equals(this.addressingVersion.actionTag.getLocalPart())) {
                if (foundAction) {
                    duplicateHeader = this.addressingVersion.actionTag;
                    break;
                }
                foundAction = true;
                continue;
            }
            if (local.equals(this.addressingVersion.messageIDTag.getLocalPart())) {
                if (foundMessageId) {
                    duplicateHeader = this.addressingVersion.messageIDTag;
                    break;
                }
                foundMessageId = true;
                continue;
            }
            if (local.equals(this.addressingVersion.relatesToTag.getLocalPart())) {
                foundRelatesTo = true;
                continue;
            }
            if (local.equals(this.addressingVersion.faultDetailTag.getLocalPart())) continue;
            System.err.println(AddressingMessages.UNKNOWN_WSA_HEADER());
        }
        if (duplicateHeader != null) {
            throw new InvalidAddressingHeaderException(duplicateHeader, this.addressingVersion.invalidCardinalityTag);
        }
        boolean engaged = foundAction;
        if (engaged || this.addressingRequired) {
            this.checkMandatoryHeaders(packet, foundAction, foundTo, foundReplyTo, foundFaultTo, foundMessageId, foundRelatesTo);
        }
    }

    final boolean isInCurrentRole(Header header, WSBinding binding) {
        if (binding == null) {
            return true;
        }
        return ((SOAPBinding)binding).getRoles().contains(header.getRole(this.soapVersion));
    }

    protected final WSDLBoundOperation getWSDLBoundOperation(Packet packet) {
        if (this.wsdlPort == null) {
            return null;
        }
        QName opName = packet.getWSDLOperation();
        if (opName != null) {
            return this.wsdlPort.getBinding().get(opName);
        }
        return null;
    }

    protected void validateSOAPAction(Packet packet) {
        String gotA = AddressingUtils.getAction(packet.getMessage().getHeaders(), this.addressingVersion, this.soapVersion);
        if (gotA == null) {
            throw new WebServiceException(AddressingMessages.VALIDATION_SERVER_NULL_ACTION());
        }
        if (packet.soapAction != null && !packet.soapAction.equals("\"\"") && !packet.soapAction.equals("\"" + gotA + "\"")) {
            throw new InvalidAddressingHeaderException(this.addressingVersion.actionTag, this.addressingVersion.actionMismatchTag);
        }
    }

    protected abstract void validateAction(Packet var1);

    protected void checkMandatoryHeaders(Packet packet, boolean foundAction, boolean foundTo, boolean foundReplyTo, boolean foundFaultTo, boolean foundMessageId, boolean foundRelatesTo) {
        if (!foundAction) {
            throw new MissingAddressingHeaderException(this.addressingVersion.actionTag, packet);
        }
        this.validateSOAPAction(packet);
    }
}

