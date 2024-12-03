/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.api.message;

import com.sun.istack.NotNull;
import com.sun.xml.ws.addressing.WsaTubeHelper;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.addressing.AddressingPropertySet;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.addressing.OneWayFeature;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.message.SuppressAutomaticWSARequestHeadersFeature;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.message.RelatesToHeader;
import com.sun.xml.ws.message.StringHeader;
import com.sun.xml.ws.resources.AddressingMessages;
import com.sun.xml.ws.resources.ClientMessages;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.WebServiceException;

public class AddressingUtils {
    public static void fillRequestAddressingHeaders(MessageHeaders headers, Packet packet, AddressingVersion av, SOAPVersion sv, boolean oneway, String action) {
        AddressingUtils.fillRequestAddressingHeaders(headers, packet, av, sv, oneway, action, false);
    }

    public static void fillRequestAddressingHeaders(MessageHeaders headers, Packet packet, AddressingVersion av, SOAPVersion sv, boolean oneway, String action, boolean mustUnderstand) {
        AddressingUtils.fillCommonAddressingHeaders(headers, packet, av, sv, action, mustUnderstand);
        if (!oneway) {
            WSEndpointReference epr = av.anonymousEpr;
            if (headers.get(av.replyToTag, false) == null) {
                headers.add(epr.createHeader(av.replyToTag));
            }
            if (headers.get(av.faultToTag, false) == null) {
                headers.add(epr.createHeader(av.faultToTag));
            }
            if (packet.getMessage().getHeaders().get(av.messageIDTag, false) == null && headers.get(av.messageIDTag, false) == null) {
                StringHeader h = new StringHeader(av.messageIDTag, Message.generateMessageID());
                headers.add(h);
            }
        }
    }

    public static void fillRequestAddressingHeaders(MessageHeaders headers, WSDLPort wsdlPort, WSBinding binding, Packet packet) {
        WSDLBoundOperation wbo;
        boolean oneway;
        if (binding == null) {
            throw new IllegalArgumentException(AddressingMessages.NULL_BINDING());
        }
        if (binding.isFeatureEnabled(SuppressAutomaticWSARequestHeadersFeature.class)) {
            return;
        }
        MessageHeaders hl = packet.getMessage().getHeaders();
        String action = AddressingUtils.getAction(hl, binding.getAddressingVersion(), binding.getSOAPVersion());
        if (action != null) {
            return;
        }
        AddressingVersion addressingVersion = binding.getAddressingVersion();
        WsaTubeHelper wsaHelper = addressingVersion.getWsaHelper(wsdlPort, null, binding);
        String effectiveInputAction = wsaHelper.getEffectiveInputAction(packet);
        if (effectiveInputAction == null || effectiveInputAction.equals("") && binding.getSOAPVersion() == SOAPVersion.SOAP_11) {
            throw new WebServiceException(ClientMessages.INVALID_SOAP_ACTION());
        }
        boolean bl = oneway = packet.expectReply == false;
        if (wsdlPort != null && !oneway && packet.getMessage() != null && packet.getWSDLOperation() != null && (wbo = wsdlPort.getBinding().get(packet.getWSDLOperation())) != null && wbo.getAnonymous() == WSDLBoundOperation.ANONYMOUS.prohibited) {
            throw new WebServiceException(AddressingMessages.WSAW_ANONYMOUS_PROHIBITED());
        }
        OneWayFeature oneWayFeature = binding.getFeature(OneWayFeature.class);
        AddressingPropertySet addressingPropertySet = packet.getSatellite(AddressingPropertySet.class);
        OneWayFeature oneWayFeature2 = oneWayFeature = addressingPropertySet == null ? oneWayFeature : new OneWayFeature(addressingPropertySet, addressingVersion);
        if (oneWayFeature == null || !oneWayFeature.isEnabled()) {
            AddressingUtils.fillRequestAddressingHeaders(headers, packet, addressingVersion, binding.getSOAPVersion(), oneway, effectiveInputAction, AddressingVersion.isRequired(binding));
        } else {
            AddressingUtils.fillRequestAddressingHeaders(headers, packet, addressingVersion, binding.getSOAPVersion(), oneWayFeature, oneway, effectiveInputAction);
        }
    }

    public static String getAction(@NotNull MessageHeaders headers, @NotNull AddressingVersion av, @NotNull SOAPVersion sv) {
        if (av == null) {
            throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION());
        }
        String action = null;
        Header h = AddressingUtils.getFirstHeader(headers, av.actionTag, true, sv);
        if (h != null) {
            action = h.getStringContent();
        }
        return action;
    }

    public static WSEndpointReference getFaultTo(@NotNull MessageHeaders headers, @NotNull AddressingVersion av, @NotNull SOAPVersion sv) {
        if (av == null) {
            throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION());
        }
        Header h = AddressingUtils.getFirstHeader(headers, av.faultToTag, true, sv);
        WSEndpointReference faultTo = null;
        if (h != null) {
            try {
                faultTo = h.readAsEPR(av);
            }
            catch (XMLStreamException e) {
                throw new WebServiceException(AddressingMessages.FAULT_TO_CANNOT_PARSE(), (Throwable)e);
            }
        }
        return faultTo;
    }

    public static String getMessageID(@NotNull MessageHeaders headers, @NotNull AddressingVersion av, @NotNull SOAPVersion sv) {
        if (av == null) {
            throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION());
        }
        Header h = AddressingUtils.getFirstHeader(headers, av.messageIDTag, true, sv);
        String messageId = null;
        if (h != null) {
            messageId = h.getStringContent();
        }
        return messageId;
    }

    public static String getRelatesTo(@NotNull MessageHeaders headers, @NotNull AddressingVersion av, @NotNull SOAPVersion sv) {
        if (av == null) {
            throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION());
        }
        Header h = AddressingUtils.getFirstHeader(headers, av.relatesToTag, true, sv);
        String relatesTo = null;
        if (h != null) {
            relatesTo = h.getStringContent();
        }
        return relatesTo;
    }

    public static WSEndpointReference getReplyTo(@NotNull MessageHeaders headers, @NotNull AddressingVersion av, @NotNull SOAPVersion sv) {
        WSEndpointReference replyTo;
        if (av == null) {
            throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION());
        }
        Header h = AddressingUtils.getFirstHeader(headers, av.replyToTag, true, sv);
        if (h != null) {
            try {
                replyTo = h.readAsEPR(av);
            }
            catch (XMLStreamException e) {
                throw new WebServiceException(AddressingMessages.REPLY_TO_CANNOT_PARSE(), (Throwable)e);
            }
        } else {
            replyTo = av.anonymousEpr;
        }
        return replyTo;
    }

    public static String getTo(MessageHeaders headers, AddressingVersion av, SOAPVersion sv) {
        if (av == null) {
            throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION());
        }
        Header h = AddressingUtils.getFirstHeader(headers, av.toTag, true, sv);
        String to = h != null ? h.getStringContent() : av.anonymousUri;
        return to;
    }

    public static Header getFirstHeader(MessageHeaders headers, QName name, boolean markUnderstood, SOAPVersion sv) {
        if (sv == null) {
            throw new IllegalArgumentException(AddressingMessages.NULL_SOAP_VERSION());
        }
        Iterator<Header> iter = headers.getHeaders(name.getNamespaceURI(), name.getLocalPart(), markUnderstood);
        while (iter.hasNext()) {
            Header h = iter.next();
            if (!h.getRole(sv).equals(sv.implicitRole)) continue;
            return h;
        }
        return null;
    }

    private static void fillRequestAddressingHeaders(@NotNull MessageHeaders headers, @NotNull Packet packet, @NotNull AddressingVersion av, @NotNull SOAPVersion sv, @NotNull OneWayFeature oneWayFeature, boolean oneway, @NotNull String action) {
        if (!oneway && !oneWayFeature.isUseAsyncWithSyncInvoke() && Boolean.TRUE.equals(packet.isSynchronousMEP)) {
            AddressingUtils.fillRequestAddressingHeaders(headers, packet, av, sv, oneway, action);
        } else {
            WSEndpointReference faultToEpr;
            WSEndpointReference replyToEpr;
            AddressingUtils.fillCommonAddressingHeaders(headers, packet, av, sv, action, false);
            boolean isMessageIdAdded = false;
            if (headers.get(av.replyToTag, false) == null && (replyToEpr = oneWayFeature.getReplyTo()) != null) {
                headers.add(replyToEpr.createHeader(av.replyToTag));
                if (packet.getMessage().getHeaders().get(av.messageIDTag, false) == null) {
                    String newID = oneWayFeature.getMessageId() == null ? Message.generateMessageID() : oneWayFeature.getMessageId();
                    headers.add(new StringHeader(av.messageIDTag, newID));
                    isMessageIdAdded = true;
                }
            }
            String messageId = oneWayFeature.getMessageId();
            if (!isMessageIdAdded && messageId != null) {
                headers.add(new StringHeader(av.messageIDTag, messageId));
            }
            if (headers.get(av.faultToTag, false) == null && (faultToEpr = oneWayFeature.getFaultTo()) != null) {
                headers.add(faultToEpr.createHeader(av.faultToTag));
                if (headers.get(av.messageIDTag, false) == null) {
                    headers.add(new StringHeader(av.messageIDTag, Message.generateMessageID()));
                }
            }
            if (oneWayFeature.getFrom() != null) {
                headers.addOrReplace(oneWayFeature.getFrom().createHeader(av.fromTag));
            }
            if (oneWayFeature.getRelatesToID() != null) {
                headers.addOrReplace(new RelatesToHeader(av.relatesToTag, oneWayFeature.getRelatesToID()));
            }
        }
    }

    private static void fillCommonAddressingHeaders(MessageHeaders headers, Packet packet, @NotNull AddressingVersion av, @NotNull SOAPVersion sv, @NotNull String action, boolean mustUnderstand) {
        StringHeader h;
        if (packet == null) {
            throw new IllegalArgumentException(AddressingMessages.NULL_PACKET());
        }
        if (av == null) {
            throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION());
        }
        if (sv == null) {
            throw new IllegalArgumentException(AddressingMessages.NULL_SOAP_VERSION());
        }
        if (action == null && !sv.httpBindingId.equals("http://www.w3.org/2003/05/soap/bindings/HTTP/")) {
            throw new IllegalArgumentException(AddressingMessages.NULL_ACTION());
        }
        if (headers.get(av.toTag, false) == null) {
            h = new StringHeader(av.toTag, packet.endpointAddress.toString());
            headers.add(h);
        }
        if (action != null) {
            packet.soapAction = action;
            if (headers.get(av.actionTag, false) == null) {
                h = new StringHeader(av.actionTag, action, sv, mustUnderstand);
                headers.add(h);
            }
        }
    }
}

