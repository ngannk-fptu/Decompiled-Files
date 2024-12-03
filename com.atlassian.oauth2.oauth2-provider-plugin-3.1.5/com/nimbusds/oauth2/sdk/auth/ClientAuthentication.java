/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.auth;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.ClientSecretPost;
import com.nimbusds.oauth2.sdk.auth.JWTAuthentication;
import com.nimbusds.oauth2.sdk.auth.PKITLSClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.SelfSignedTLSClientAuthentication;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.x500.X500Principal;

public abstract class ClientAuthentication {
    private final ClientAuthenticationMethod method;
    private final ClientID clientID;

    protected ClientAuthentication(ClientAuthenticationMethod method, ClientID clientID) {
        if (method == null) {
            throw new IllegalArgumentException("The client authentication method must not be null");
        }
        this.method = method;
        if (clientID == null) {
            throw new IllegalArgumentException("The client identifier must not be null");
        }
        this.clientID = clientID;
    }

    public ClientAuthenticationMethod getMethod() {
        return this.method;
    }

    public ClientID getClientID() {
        return this.clientID;
    }

    public abstract Set<String> getFormParameterNames();

    public static ClientAuthentication parse(HTTPRequest httpRequest) throws ParseException {
        if (httpRequest.getAuthorization() != null && httpRequest.getAuthorization().startsWith("Basic")) {
            return ClientSecretBasic.parse(httpRequest);
        }
        if (httpRequest.getMethod() != HTTPRequest.Method.POST && !httpRequest.getEntityContentType().matches(ContentType.APPLICATION_URLENCODED)) {
            return null;
        }
        Map<String, List<String>> params = httpRequest.getQueryParameters();
        if (StringUtils.isNotBlank(MultivaluedMapUtils.getFirstValue(params, "client_id")) && StringUtils.isNotBlank(MultivaluedMapUtils.getFirstValue(params, "client_secret"))) {
            return ClientSecretPost.parse(httpRequest);
        }
        if (StringUtils.isNotBlank(MultivaluedMapUtils.getFirstValue(params, "client_assertion")) && StringUtils.isNotBlank(MultivaluedMapUtils.getFirstValue(params, "client_assertion_type"))) {
            return JWTAuthentication.parse(httpRequest);
        }
        if (httpRequest.getClientX509Certificate() != null && StringUtils.isNotBlank(MultivaluedMapUtils.getFirstValue(params, "client_id"))) {
            X500Principal issuer = httpRequest.getClientX509Certificate().getIssuerX500Principal();
            X500Principal subject = httpRequest.getClientX509Certificate().getSubjectX500Principal();
            if (issuer != null && issuer.equals(subject)) {
                if (httpRequest.getClientX509CertificateRootDN() != null && !httpRequest.getClientX509CertificateRootDN().equalsIgnoreCase(issuer.toString())) {
                    throw new ParseException("Client X.509 certificate issuer DN doesn't match HTTP request metadata");
                }
                if (httpRequest.getClientX509CertificateSubjectDN() != null && !httpRequest.getClientX509CertificateSubjectDN().equalsIgnoreCase(subject.toString())) {
                    throw new ParseException("Client X.509 certificate subject DN doesn't match HTTP request metadata");
                }
                return SelfSignedTLSClientAuthentication.parse(httpRequest);
            }
            return PKITLSClientAuthentication.parse(httpRequest);
        }
        return null;
    }

    public abstract void applyTo(HTTPRequest var1);
}

