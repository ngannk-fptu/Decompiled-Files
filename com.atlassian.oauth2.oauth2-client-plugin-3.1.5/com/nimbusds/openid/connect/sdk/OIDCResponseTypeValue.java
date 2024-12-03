/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk;

import com.nimbusds.oauth2.sdk.ResponseType;
import net.jcip.annotations.Immutable;

@Immutable
public class OIDCResponseTypeValue {
    public static final ResponseType.Value ID_TOKEN = new ResponseType.Value("id_token");
    public static final ResponseType.Value NONE = new ResponseType.Value("none");

    private OIDCResponseTypeValue() {
    }
}

