/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.ws;

import java.util.Map;
import javax.xml.ws.Binding;
import javax.xml.ws.EndpointReference;

public interface BindingProvider {
    public static final String USERNAME_PROPERTY = "javax.xml.ws.security.auth.username";
    public static final String PASSWORD_PROPERTY = "javax.xml.ws.security.auth.password";
    public static final String ENDPOINT_ADDRESS_PROPERTY = "javax.xml.ws.service.endpoint.address";
    public static final String SESSION_MAINTAIN_PROPERTY = "javax.xml.ws.session.maintain";
    public static final String SOAPACTION_USE_PROPERTY = "javax.xml.ws.soap.http.soapaction.use";
    public static final String SOAPACTION_URI_PROPERTY = "javax.xml.ws.soap.http.soapaction.uri";

    public Map<String, Object> getRequestContext();

    public Map<String, Object> getResponseContext();

    public Binding getBinding();

    public EndpointReference getEndpointReference();

    public <T extends EndpointReference> T getEndpointReference(Class<T> var1);
}

