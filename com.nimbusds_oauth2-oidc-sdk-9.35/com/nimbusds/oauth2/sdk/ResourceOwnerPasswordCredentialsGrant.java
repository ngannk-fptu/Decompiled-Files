/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.GrantType;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.Immutable;

@Immutable
public class ResourceOwnerPasswordCredentialsGrant
extends AuthorizationGrant {
    public static final GrantType GRANT_TYPE = GrantType.PASSWORD;
    private final String username;
    private final Secret password;

    public ResourceOwnerPasswordCredentialsGrant(String username, Secret password) {
        super(GRANT_TYPE);
        if (username == null) {
            throw new IllegalArgumentException("The username must not be null");
        }
        this.username = username;
        if (password == null) {
            throw new IllegalArgumentException("The password must not be null");
        }
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public Secret getPassword() {
        return this.password;
    }

    @Override
    public Map<String, List<String>> toParameters() {
        LinkedHashMap<String, List<String>> params = new LinkedHashMap<String, List<String>>();
        params.put("grant_type", Collections.singletonList(GRANT_TYPE.getValue()));
        params.put("username", Collections.singletonList(this.username));
        params.put("password", Collections.singletonList(this.password.getValue()));
        return params;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ResourceOwnerPasswordCredentialsGrant that = (ResourceOwnerPasswordCredentialsGrant)o;
        if (!this.username.equals(that.username)) {
            return false;
        }
        return this.password.equals(that.password);
    }

    public int hashCode() {
        int result = this.username.hashCode();
        result = 31 * result + this.password.hashCode();
        return result;
    }

    public static ResourceOwnerPasswordCredentialsGrant parse(Map<String, List<String>> params) throws ParseException {
        GrantType.ensure(GRANT_TYPE, params);
        String username = MultivaluedMapUtils.getFirstValue(params, "username");
        if (username == null || username.trim().isEmpty()) {
            String msg = "Missing or empty username parameter";
            throw new ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg));
        }
        String passwordString = MultivaluedMapUtils.getFirstValue(params, "password");
        if (passwordString == null || passwordString.trim().isEmpty()) {
            String msg = "Missing or empty password parameter";
            throw new ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg));
        }
        Secret password = new Secret(passwordString);
        return new ResourceOwnerPasswordCredentialsGrant(username, password);
    }
}

