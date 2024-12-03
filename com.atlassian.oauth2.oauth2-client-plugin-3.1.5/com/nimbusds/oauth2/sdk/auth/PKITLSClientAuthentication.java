/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.auth;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.auth.TLSClientAuthentication;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.nimbusds.oauth2.sdk.util.URLUtils;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLSocketFactory;
import net.jcip.annotations.Immutable;

@Immutable
public class PKITLSClientAuthentication
extends TLSClientAuthentication {
    private final String certSubjectDN;

    public PKITLSClientAuthentication(ClientID clientID, SSLSocketFactory sslSocketFactory) {
        super(ClientAuthenticationMethod.TLS_CLIENT_AUTH, clientID, sslSocketFactory);
        this.certSubjectDN = null;
    }

    @Deprecated
    public PKITLSClientAuthentication(ClientID clientID, String certSubjectDN) {
        super(ClientAuthenticationMethod.TLS_CLIENT_AUTH, clientID, (X509Certificate)null);
        if (certSubjectDN == null) {
            throw new IllegalArgumentException("The X.509 client certificate subject DN must not be null");
        }
        this.certSubjectDN = certSubjectDN;
    }

    public PKITLSClientAuthentication(ClientID clientID, X509Certificate certificate) {
        super(ClientAuthenticationMethod.TLS_CLIENT_AUTH, clientID, certificate);
        if (certificate == null) {
            throw new IllegalArgumentException("The X.509 client certificate must not be null");
        }
        this.certSubjectDN = certificate.getSubjectX500Principal().getName();
    }

    public String getClientX509CertificateSubjectDN() {
        return this.certSubjectDN;
    }

    public static PKITLSClientAuthentication parse(HTTPRequest httpRequest) throws ParseException {
        String query = httpRequest.getQuery();
        if (query == null) {
            throw new ParseException("Missing HTTP POST request entity body");
        }
        Map<String, List<String>> params = URLUtils.parseParameters(query);
        String clientIDString = MultivaluedMapUtils.getFirstValue(params, "client_id");
        if (StringUtils.isBlank(clientIDString)) {
            throw new ParseException("Missing client_id parameter");
        }
        if (httpRequest.getClientX509Certificate() == null) {
            throw new ParseException("Missing client X.509 certificate");
        }
        return new PKITLSClientAuthentication(new ClientID(clientIDString), httpRequest.getClientX509Certificate());
    }
}

