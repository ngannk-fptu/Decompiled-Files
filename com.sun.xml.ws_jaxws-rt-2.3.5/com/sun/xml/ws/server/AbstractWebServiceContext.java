/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.xml.ws.EndpointReference
 *  javax.xml.ws.handler.MessageContext
 *  javax.xml.ws.wsaddressing.W3CEndpointReference
 */
package com.sun.xml.ws.server;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.server.WSWebServiceContext;
import com.sun.xml.ws.server.EndpointMessageContextImpl;
import java.security.Principal;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import org.w3c.dom.Element;

public abstract class AbstractWebServiceContext
implements WSWebServiceContext {
    private final WSEndpoint endpoint;

    public AbstractWebServiceContext(@NotNull WSEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    public MessageContext getMessageContext() {
        Packet packet = this.getRequestPacket();
        if (packet == null) {
            throw new IllegalStateException("getMessageContext() can only be called while servicing a request");
        }
        return new EndpointMessageContextImpl(packet);
    }

    public Principal getUserPrincipal() {
        Packet packet = this.getRequestPacket();
        if (packet == null) {
            throw new IllegalStateException("getUserPrincipal() can only be called while servicing a request");
        }
        return packet.webServiceContextDelegate.getUserPrincipal(packet);
    }

    public boolean isUserInRole(String role) {
        Packet packet = this.getRequestPacket();
        if (packet == null) {
            throw new IllegalStateException("isUserInRole() can only be called while servicing a request");
        }
        return packet.webServiceContextDelegate.isUserInRole(packet, role);
    }

    public EndpointReference getEndpointReference(Element ... referenceParameters) {
        return this.getEndpointReference(W3CEndpointReference.class, referenceParameters);
    }

    public <T extends EndpointReference> T getEndpointReference(Class<T> clazz, Element ... referenceParameters) {
        Packet packet = this.getRequestPacket();
        if (packet == null) {
            throw new IllegalStateException("getEndpointReference() can only be called while servicing a request");
        }
        String address = packet.webServiceContextDelegate.getEPRAddress(packet, this.endpoint);
        String wsdlAddress = null;
        if (this.endpoint.getServiceDefinition() != null) {
            wsdlAddress = packet.webServiceContextDelegate.getWSDLAddress(packet, this.endpoint);
        }
        return (T)((EndpointReference)clazz.cast(this.endpoint.getEndpointReference(clazz, address, wsdlAddress, referenceParameters)));
    }
}

