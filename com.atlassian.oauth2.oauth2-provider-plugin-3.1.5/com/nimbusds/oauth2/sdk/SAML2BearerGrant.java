/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.oauth2.sdk.AssertionGrant;
import com.nimbusds.oauth2.sdk.GrantType;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.Immutable;

@Immutable
public class SAML2BearerGrant
extends AssertionGrant {
    public static final GrantType GRANT_TYPE = GrantType.SAML2_BEARER;
    private final Base64URL assertion;

    public SAML2BearerGrant(Base64URL assertion) {
        super(GRANT_TYPE);
        if (assertion == null) {
            throw new IllegalArgumentException("The SAML 2.0 bearer assertion must not be null");
        }
        this.assertion = assertion;
    }

    public Base64URL getSAML2Assertion() {
        return this.assertion;
    }

    @Override
    public String getAssertion() {
        return this.assertion.toString();
    }

    @Override
    public Map<String, List<String>> toParameters() {
        LinkedHashMap<String, List<String>> params = new LinkedHashMap<String, List<String>>();
        params.put("grant_type", Collections.singletonList(GRANT_TYPE.getValue()));
        params.put("assertion", Collections.singletonList(this.assertion.toString()));
        return params;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SAML2BearerGrant that = (SAML2BearerGrant)o;
        return this.assertion.equals(that.assertion);
    }

    public int hashCode() {
        return this.assertion.hashCode();
    }

    public static SAML2BearerGrant parse(Map<String, List<String>> params) throws ParseException {
        GrantType.ensure(GRANT_TYPE, params);
        String assertionString = MultivaluedMapUtils.getFirstValue(params, "assertion");
        if (assertionString == null || assertionString.trim().isEmpty()) {
            throw MISSING_ASSERTION_PARAM_EXCEPTION;
        }
        return new SAML2BearerGrant(new Base64URL(assertionString));
    }
}

