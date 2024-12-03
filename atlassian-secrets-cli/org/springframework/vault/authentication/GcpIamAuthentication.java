/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.api.client.googleapis.auth.oauth2.GoogleCredential
 *  com.google.api.client.http.HttpRequestInitializer
 *  com.google.api.client.http.HttpTransport
 *  com.google.api.client.http.javanet.NetHttpTransport
 *  com.google.api.client.json.JsonFactory
 *  com.google.api.client.json.jackson2.JacksonFactory
 *  com.google.api.services.iam.v1.Iam
 *  com.google.api.services.iam.v1.Iam$Builder
 *  com.google.api.services.iam.v1.Iam$Projects$ServiceAccounts$SignJwt
 *  com.google.api.services.iam.v1.model.SignJwtRequest
 *  com.google.api.services.iam.v1.model.SignJwtResponse
 */
package org.springframework.vault.authentication;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.iam.v1.Iam;
import com.google.api.services.iam.v1.model.SignJwtRequest;
import com.google.api.services.iam.v1.model.SignJwtResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.util.Assert;
import org.springframework.vault.VaultException;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.GcpIamAuthenticationOptions;
import org.springframework.vault.authentication.GcpJwtAuthenticationSupport;
import org.springframework.vault.authentication.VaultLoginException;
import org.springframework.vault.support.VaultToken;
import org.springframework.web.client.RestOperations;

@Deprecated
public class GcpIamAuthentication
extends GcpJwtAuthenticationSupport
implements ClientAuthentication {
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static final String SCOPE = "https://www.googleapis.com/auth/iam";
    private final GcpIamAuthenticationOptions options;
    private final HttpTransport httpTransport;
    private final GoogleCredential credential;

    public GcpIamAuthentication(GcpIamAuthenticationOptions options, RestOperations restOperations) {
        this(options, restOperations, (HttpTransport)new NetHttpTransport());
    }

    public GcpIamAuthentication(GcpIamAuthenticationOptions options, RestOperations restOperations, HttpTransport httpTransport) {
        super(restOperations);
        Assert.notNull((Object)options, "GcpIamAuthenticationOptions must not be null");
        Assert.notNull((Object)restOperations, "RestOperations must not be null");
        Assert.notNull((Object)httpTransport, "HttpTransport must not be null");
        this.options = options;
        this.httpTransport = httpTransport;
        this.credential = options.getCredentialSupplier().get().createScoped(Collections.singletonList(SCOPE));
    }

    @Override
    public VaultToken login() throws VaultException {
        String signedJwt = this.signJwt();
        return this.doLogin("GCP-IAM", signedJwt, this.options.getPath(), this.options.getRole());
    }

    protected String signJwt() {
        String projectId = this.getProjectId();
        String serviceAccount = this.getServiceAccountId();
        Map<String, Object> jwtPayload = GcpIamAuthentication.getJwtPayload(this.options, serviceAccount);
        Iam iam = new Iam.Builder(this.httpTransport, JSON_FACTORY, (HttpRequestInitializer)this.credential).setApplicationName("Spring Vault/" + this.getClass().getName()).build();
        try {
            String payload = JSON_FACTORY.toString(jwtPayload);
            SignJwtRequest request = new SignJwtRequest();
            request.setPayload(payload);
            Iam.Projects.ServiceAccounts.SignJwt signJwt = iam.projects().serviceAccounts().signJwt(String.format("projects/%s/serviceAccounts/%s", projectId, serviceAccount), request);
            SignJwtResponse response = (SignJwtResponse)signJwt.execute();
            return response.getSignedJwt();
        }
        catch (IOException e) {
            throw new VaultLoginException("Cannot sign JWT", e);
        }
    }

    private String getServiceAccountId() {
        return this.options.getServiceAccountIdAccessor().getServiceAccountId(this.credential);
    }

    private String getProjectId() {
        return this.options.getProjectIdAccessor().getProjectId(this.credential);
    }

    private static Map<String, Object> getJwtPayload(GcpIamAuthenticationOptions options, String serviceAccount) {
        Instant validUntil = options.getClock().instant().plus(options.getJwtValidity());
        LinkedHashMap<String, Object> payload = new LinkedHashMap<String, Object>();
        payload.put("sub", serviceAccount);
        payload.put("aud", "vault/" + options.getRole());
        payload.put("exp", validUntil.getEpochSecond());
        return payload;
    }
}

