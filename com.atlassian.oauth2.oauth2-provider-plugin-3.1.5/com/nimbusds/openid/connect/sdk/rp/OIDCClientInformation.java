/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.rp;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.client.ClientCredentialsParser;
import com.nimbusds.oauth2.sdk.client.ClientInformation;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientMetadata;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public final class OIDCClientInformation
extends ClientInformation {
    private static final Set<String> REGISTERED_PARAMETER_NAMES;

    public OIDCClientInformation(ClientID id, OIDCClientMetadata metadata) {
        this(id, null, metadata, null);
    }

    public OIDCClientInformation(ClientID id, Date issueDate, OIDCClientMetadata metadata, Secret secret) {
        this(id, issueDate, metadata, secret, null, null);
    }

    public OIDCClientInformation(ClientID id, Date issueDate, OIDCClientMetadata metadata, Secret secret, URI registrationURI, BearerAccessToken accessToken) {
        super(id, issueDate, metadata, secret, registrationURI, accessToken);
    }

    public static Set<String> getRegisteredParameterNames() {
        return REGISTERED_PARAMETER_NAMES;
    }

    public OIDCClientMetadata getOIDCMetadata() {
        return (OIDCClientMetadata)this.getMetadata();
    }

    public static OIDCClientInformation parse(JSONObject jsonObject) throws ParseException {
        return new OIDCClientInformation(ClientCredentialsParser.parseID(jsonObject), ClientCredentialsParser.parseIDIssueDate(jsonObject), OIDCClientMetadata.parse(jsonObject), ClientCredentialsParser.parseSecret(jsonObject), ClientCredentialsParser.parseRegistrationURI(jsonObject), ClientCredentialsParser.parseRegistrationAccessToken(jsonObject));
    }

    static {
        HashSet<String> p = new HashSet<String>(ClientInformation.getRegisteredParameterNames());
        p.addAll(OIDCClientMetadata.getRegisteredParameterNames());
        REGISTERED_PARAMETER_NAMES = Collections.unmodifiableSet(p);
    }
}

