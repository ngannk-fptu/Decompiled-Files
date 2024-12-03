/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jose.util.Base64URL
 *  com.nimbusds.oauth2.sdk.GrantType
 *  com.nimbusds.oauth2.sdk.SAML2BearerGrant
 */
package com.microsoft.aad.msal4j;

import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.oauth2.sdk.GrantType;
import com.nimbusds.oauth2.sdk.SAML2BearerGrant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

class SAML11BearerGrant
extends SAML2BearerGrant {
    public static GrantType grantType = new GrantType("urn:ietf:params:oauth:grant-type:saml1_1-bearer");

    public SAML11BearerGrant(Base64URL assertion) {
        super(assertion);
    }

    public Map<String, List<String>> toParameters() {
        Map params = super.toParameters();
        params.put("grant_type", Collections.singletonList(grantType.getValue()));
        return params;
    }
}

