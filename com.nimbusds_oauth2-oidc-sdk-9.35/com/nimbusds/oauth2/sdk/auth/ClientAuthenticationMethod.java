/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.oauth2.sdk.auth;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class ClientAuthenticationMethod
extends Identifier {
    private static final long serialVersionUID = 1L;
    public static final ClientAuthenticationMethod CLIENT_SECRET_BASIC = new ClientAuthenticationMethod("client_secret_basic");
    public static final ClientAuthenticationMethod CLIENT_SECRET_POST = new ClientAuthenticationMethod("client_secret_post");
    public static final ClientAuthenticationMethod CLIENT_SECRET_JWT = new ClientAuthenticationMethod("client_secret_jwt");
    public static final ClientAuthenticationMethod PRIVATE_KEY_JWT = new ClientAuthenticationMethod("private_key_jwt");
    public static final ClientAuthenticationMethod TLS_CLIENT_AUTH = new ClientAuthenticationMethod("tls_client_auth");
    public static final ClientAuthenticationMethod SELF_SIGNED_TLS_CLIENT_AUTH = new ClientAuthenticationMethod("self_signed_tls_client_auth");
    public static final ClientAuthenticationMethod REQUEST_OBJECT = new ClientAuthenticationMethod("request_object");
    public static final ClientAuthenticationMethod NONE = new ClientAuthenticationMethod("none");

    public static ClientAuthenticationMethod getDefault() {
        return CLIENT_SECRET_BASIC;
    }

    public ClientAuthenticationMethod(String value) {
        super(value);
    }

    public static ClientAuthenticationMethod parse(String value) {
        if (value.equals(CLIENT_SECRET_BASIC.getValue())) {
            return CLIENT_SECRET_BASIC;
        }
        if (value.equals(CLIENT_SECRET_POST.getValue())) {
            return CLIENT_SECRET_POST;
        }
        if (value.equals(CLIENT_SECRET_JWT.getValue())) {
            return CLIENT_SECRET_JWT;
        }
        if (value.equals(PRIVATE_KEY_JWT.getValue())) {
            return PRIVATE_KEY_JWT;
        }
        if (value.equalsIgnoreCase(TLS_CLIENT_AUTH.getValue())) {
            return TLS_CLIENT_AUTH;
        }
        if (value.equalsIgnoreCase(SELF_SIGNED_TLS_CLIENT_AUTH.getValue())) {
            return SELF_SIGNED_TLS_CLIENT_AUTH;
        }
        if (value.equalsIgnoreCase(REQUEST_OBJECT.getValue())) {
            return REQUEST_OBJECT;
        }
        if (value.equals(NONE.getValue())) {
            return NONE;
        }
        return new ClientAuthenticationMethod(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof ClientAuthenticationMethod && this.toString().equals(object.toString());
    }
}

