/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.openid.connect.sdk;

import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.id.Identifier;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import net.jcip.annotations.Immutable;

@Immutable
public final class Nonce
extends Identifier {
    private static final long serialVersionUID = 1484679928325180239L;

    public Nonce(String value) {
        super(value);
    }

    public Nonce(int byteLength) {
        super(byteLength);
    }

    public Nonce() {
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Nonce && this.toString().equals(object.toString());
    }

    public static Nonce parse(String s) {
        if (StringUtils.isBlank(s)) {
            return null;
        }
        return new Nonce(s);
    }

    public static boolean isRequired(ResponseType responseType) {
        return responseType.equals(ResponseType.IDTOKEN) || responseType.equals(ResponseType.IDTOKEN_TOKEN) || responseType.equals(ResponseType.CODE_IDTOKEN) || responseType.equals(ResponseType.CODE_IDTOKEN_TOKEN);
    }
}

