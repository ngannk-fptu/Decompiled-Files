/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class ResponseMode
extends Identifier {
    public static final ResponseMode QUERY = new ResponseMode("query");
    public static final ResponseMode FRAGMENT = new ResponseMode("fragment");
    public static final ResponseMode FORM_POST = new ResponseMode("form_post");
    public static final ResponseMode QUERY_JWT = new ResponseMode("query.jwt");
    public static final ResponseMode FRAGMENT_JWT = new ResponseMode("fragment.jwt");
    public static final ResponseMode FORM_POST_JWT = new ResponseMode("form_post.jwt");
    public static final ResponseMode JWT = new ResponseMode("jwt");
    private static final long serialVersionUID = -5607166526553472087L;

    public static ResponseMode resolve(ResponseMode rm, ResponseType rt) {
        if (rm != null) {
            if (JWT.equals(rm)) {
                if (rt != null && (rt.impliesImplicitFlow() || rt.impliesHybridFlow())) {
                    return FRAGMENT_JWT;
                }
                return QUERY_JWT;
            }
            return rm;
        }
        if (rt != null && (rt.impliesImplicitFlow() || rt.impliesHybridFlow())) {
            return FRAGMENT;
        }
        return QUERY;
    }

    public static ResponseMode resolveJARM(ResponseType rt) {
        if (rt.impliesImplicitFlow() || rt.impliesHybridFlow()) {
            return FRAGMENT_JWT;
        }
        return QUERY_JWT;
    }

    public ResponseMode(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof ResponseMode && this.toString().equals(object.toString());
    }
}

