/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.api.client.json.JsonFactory
 *  com.google.api.client.json.jackson2.JacksonFactory
 *  com.google.api.gax.rpc.TransportChannelProvider
 *  com.google.auth.oauth2.GoogleCredentials
 *  com.google.cloud.iam.credentials.v1.IamCredentialsClient
 *  com.google.cloud.iam.credentials.v1.IamCredentialsSettings
 *  com.google.cloud.iam.credentials.v1.IamCredentialsSettings$Builder
 *  com.google.cloud.iam.credentials.v1.ServiceAccountName
 *  com.google.cloud.iam.credentials.v1.SignJwtResponse
 *  com.google.cloud.iam.credentials.v1.stub.IamCredentialsStubSettings
 *  org.springframework.util.Assert
 *  org.springframework.web.client.RestOperations
 */
package org.springframework.vault.authentication;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.iam.credentials.v1.IamCredentialsClient;
import com.google.cloud.iam.credentials.v1.IamCredentialsSettings;
import com.google.cloud.iam.credentials.v1.ServiceAccountName;
import com.google.cloud.iam.credentials.v1.SignJwtResponse;
import com.google.cloud.iam.credentials.v1.stub.IamCredentialsStubSettings;
import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.util.Assert;
import org.springframework.vault.VaultException;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.GcpIamCredentialsAuthenticationOptions;
import org.springframework.vault.authentication.GcpJwtAuthenticationSupport;
import org.springframework.vault.authentication.VaultLoginException;
import org.springframework.vault.support.VaultToken;
import org.springframework.web.client.RestOperations;

public class GcpIamCredentialsAuthentication
extends GcpJwtAuthenticationSupport
implements ClientAuthentication {
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private final GcpIamCredentialsAuthenticationOptions options;
    private final TransportChannelProvider transportChannelProvider;
    private final GoogleCredentials credentials;

    public GcpIamCredentialsAuthentication(GcpIamCredentialsAuthenticationOptions options, RestOperations restOperations) {
        this(options, restOperations, (TransportChannelProvider)IamCredentialsStubSettings.defaultGrpcTransportProviderBuilder().build());
    }

    public GcpIamCredentialsAuthentication(GcpIamCredentialsAuthenticationOptions options, RestOperations restOperations, TransportChannelProvider transportChannelProvider) {
        super(restOperations);
        Assert.notNull((Object)options, (String)"GcpAuthenticationOptions must not be null");
        Assert.notNull((Object)restOperations, (String)"RestOperations must not be null");
        Assert.notNull((Object)transportChannelProvider, (String)"TransportChannelProvider must not be null");
        this.options = options;
        this.transportChannelProvider = transportChannelProvider;
        this.credentials = options.getCredentialSupplier().get();
    }

    @Override
    public VaultToken login() throws VaultException {
        String signedJwt = this.signJwt();
        return this.doLogin("GCP-IAM", signedJwt, this.options.getPath(), this.options.getRole());
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected String signJwt() {
        String serviceAccount = this.getServiceAccountId();
        Map<String, Object> jwtPayload = GcpIamCredentialsAuthentication.getJwtPayload(this.options, serviceAccount);
        try {
            IamCredentialsSettings credentialsSettings = ((IamCredentialsSettings.Builder)((IamCredentialsSettings.Builder)IamCredentialsSettings.newBuilder().setCredentialsProvider(() -> this.credentials)).setTransportChannelProvider(this.transportChannelProvider)).build();
            try (IamCredentialsClient iamCredentialsClient = IamCredentialsClient.create((IamCredentialsSettings)credentialsSettings);){
                String payload = JSON_FACTORY.toString(jwtPayload);
                ServiceAccountName serviceAccountName = ServiceAccountName.of((String)"-", (String)serviceAccount);
                SignJwtResponse response = iamCredentialsClient.signJwt(serviceAccountName, Collections.emptyList(), payload);
                String string = response.getSignedJwt();
                return string;
            }
        }
        catch (IOException e) {
            throw new VaultLoginException("Cannot sign JWT", e);
        }
    }

    private String getServiceAccountId() {
        return this.options.getServiceAccountIdAccessor().getServiceAccountId(this.credentials);
    }

    private static Map<String, Object> getJwtPayload(GcpIamCredentialsAuthenticationOptions options, String serviceAccount) {
        Instant validUntil = options.getClock().instant().plus(options.getJwtValidity());
        LinkedHashMap<String, Object> payload = new LinkedHashMap<String, Object>();
        payload.put("sub", serviceAccount);
        payload.put("aud", "vault/" + options.getRole());
        payload.put("exp", validUntil.getEpochSecond());
        return payload;
    }
}

