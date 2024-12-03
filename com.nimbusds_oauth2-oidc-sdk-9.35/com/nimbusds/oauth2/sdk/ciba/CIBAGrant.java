/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.oauth2.sdk.ciba;

import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.GrantType;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ciba.AuthRequestID;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.jcip.annotations.Immutable;

@Immutable
public class CIBAGrant
extends AuthorizationGrant {
    public static final GrantType GRANT_TYPE = GrantType.CIBA;
    private final AuthRequestID authRequestID;

    public CIBAGrant(AuthRequestID authRequestID) {
        super(GRANT_TYPE);
        if (authRequestID == null) {
            throw new IllegalArgumentException("The auth_req_id must not be null");
        }
        this.authRequestID = authRequestID;
    }

    public AuthRequestID getAuthRequestID() {
        return this.authRequestID;
    }

    @Override
    public Map<String, List<String>> toParameters() {
        LinkedHashMap<String, List<String>> params = new LinkedHashMap<String, List<String>>();
        params.put("grant_type", Collections.singletonList(GRANT_TYPE.getValue()));
        params.put("auth_req_id", Collections.singletonList(this.authRequestID.getValue()));
        return params;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CIBAGrant)) {
            return false;
        }
        CIBAGrant cibaGrant = (CIBAGrant)o;
        return this.getAuthRequestID().equals(cibaGrant.getAuthRequestID());
    }

    public int hashCode() {
        return Objects.hash(this.getAuthRequestID());
    }

    public static CIBAGrant parse(Map<String, List<String>> params) throws ParseException {
        GrantType.ensure(GRANT_TYPE, params);
        String authReqIDString = MultivaluedMapUtils.getFirstValue(params, "auth_req_id");
        if (authReqIDString == null || authReqIDString.trim().isEmpty()) {
            String msg = "Missing or empty auth_req_id parameter";
            throw new ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg));
        }
        AuthRequestID authRequestID = AuthRequestID.parse(authReqIDString);
        return new CIBAGrant(authRequestID);
    }
}

