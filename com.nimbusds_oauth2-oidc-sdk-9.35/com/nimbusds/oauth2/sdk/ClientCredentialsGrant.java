/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.GrantType;
import com.nimbusds.oauth2.sdk.ParseException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.Immutable;

@Immutable
public class ClientCredentialsGrant
extends AuthorizationGrant {
    public static final GrantType GRANT_TYPE = GrantType.CLIENT_CREDENTIALS;

    public ClientCredentialsGrant() {
        super(GRANT_TYPE);
    }

    @Override
    public Map<String, List<String>> toParameters() {
        LinkedHashMap<String, List<String>> params = new LinkedHashMap<String, List<String>>();
        params.put("grant_type", Collections.singletonList(GRANT_TYPE.getValue()));
        return params;
    }

    public static ClientCredentialsGrant parse(Map<String, List<String>> params) throws ParseException {
        GrantType.ensure(GRANT_TYPE, params);
        return new ClientCredentialsGrant();
    }
}

