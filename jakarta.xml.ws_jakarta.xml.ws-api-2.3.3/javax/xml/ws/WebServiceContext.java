/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.ws;

import java.security.Principal;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.handler.MessageContext;
import org.w3c.dom.Element;

public interface WebServiceContext {
    public MessageContext getMessageContext();

    public Principal getUserPrincipal();

    public boolean isUserInRole(String var1);

    public EndpointReference getEndpointReference(Element ... var1);

    public <T extends EndpointReference> T getEndpointReference(Class<T> var1, Element ... var2);
}

