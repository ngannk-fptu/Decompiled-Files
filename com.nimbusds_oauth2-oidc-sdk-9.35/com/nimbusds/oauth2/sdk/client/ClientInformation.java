/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.oauth2.sdk.client;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.client.ClientCredentialsParser;
import com.nimbusds.oauth2.sdk.client.ClientMetadata;
import com.nimbusds.oauth2.sdk.client.ClientType;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class ClientInformation {
    private static final Set<String> REGISTERED_PARAMETER_NAMES;
    private final ClientID id;
    private final Date issueDate;
    private final ClientMetadata metadata;
    private final Secret secret;
    private final URI registrationURI;
    private final BearerAccessToken accessToken;

    public ClientInformation(ClientID id, ClientMetadata metadata) {
        this(id, null, metadata, null);
    }

    public ClientInformation(ClientID id, Date issueDate, ClientMetadata metadata, Secret secret) {
        this(id, issueDate, metadata, secret, null, null);
    }

    public ClientInformation(ClientID id, Date issueDate, ClientMetadata metadata, Secret secret, URI registrationURI, BearerAccessToken accessToken) {
        if (id == null) {
            throw new IllegalArgumentException("The client identifier must not be null");
        }
        this.id = id;
        this.issueDate = issueDate;
        if (metadata == null) {
            throw new IllegalArgumentException("The client metadata must not be null");
        }
        this.metadata = metadata;
        this.secret = secret;
        this.registrationURI = registrationURI;
        this.accessToken = accessToken;
    }

    public static Set<String> getRegisteredParameterNames() {
        return REGISTERED_PARAMETER_NAMES;
    }

    public ClientID getID() {
        return this.id;
    }

    public Date getIDIssueDate() {
        return this.issueDate;
    }

    public ClientMetadata getMetadata() {
        return this.metadata;
    }

    public Secret getSecret() {
        return this.secret;
    }

    public ClientType inferClientType() {
        return this.secret == null && ClientAuthenticationMethod.NONE.equals(this.getMetadata().getTokenEndpointAuthMethod()) && this.getMetadata().getJWKSetURI() == null && this.getMetadata().getJWKSet() == null ? ClientType.PUBLIC : ClientType.CONFIDENTIAL;
    }

    public URI getRegistrationURI() {
        return this.registrationURI;
    }

    public BearerAccessToken getRegistrationAccessToken() {
        return this.accessToken;
    }

    public JSONObject toJSONObject() {
        JSONObject o = this.metadata.toJSONObject();
        o.put((Object)"client_id", (Object)this.id.getValue());
        if (this.issueDate != null) {
            o.put((Object)"client_id_issued_at", (Object)(this.issueDate.getTime() / 1000L));
        }
        if (this.secret != null) {
            o.put((Object)"client_secret", (Object)this.secret.getValue());
            if (this.secret.getExpirationDate() != null) {
                o.put((Object)"client_secret_expires_at", (Object)(this.secret.getExpirationDate().getTime() / 1000L));
            } else {
                o.put((Object)"client_secret_expires_at", (Object)0L);
            }
        }
        if (this.registrationURI != null) {
            o.put((Object)"registration_client_uri", (Object)this.registrationURI.toString());
        }
        if (this.accessToken != null) {
            o.put((Object)"registration_access_token", (Object)this.accessToken.getValue());
        }
        return o;
    }

    public static ClientInformation parse(JSONObject jsonObject) throws ParseException {
        return new ClientInformation(ClientCredentialsParser.parseID(jsonObject), ClientCredentialsParser.parseIDIssueDate(jsonObject), ClientMetadata.parse(jsonObject), ClientCredentialsParser.parseSecret(jsonObject), ClientCredentialsParser.parseRegistrationURI(jsonObject), ClientCredentialsParser.parseRegistrationAccessToken(jsonObject));
    }

    static {
        HashSet<String> p = new HashSet<String>(ClientMetadata.getRegisteredParameterNames());
        p.add("client_id");
        p.add("client_id_issued_at");
        p.add("client_secret");
        p.add("client_secret_expires_at");
        p.add("registration_access_token");
        p.add("registration_client_uri");
        REGISTERED_PARAMETER_NAMES = Collections.unmodifiableSet(p);
    }
}

