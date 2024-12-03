/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.openid.connect.sdk.federation.api;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.nimbusds.openid.connect.sdk.federation.api.EntityListingSpec;
import com.nimbusds.openid.connect.sdk.federation.api.FederationAPIRequest;
import com.nimbusds.openid.connect.sdk.federation.api.OperationType;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.Immutable;

@Immutable
public class EntityListingRequest
extends FederationAPIRequest {
    private final Issuer issuer;
    private final EntityListingSpec listingSpec;

    public EntityListingRequest(URI endpoint, Issuer issuer, EntityListingSpec listingSpec) {
        super(endpoint, OperationType.LISTING);
        if (issuer == null) {
            throw new IllegalArgumentException("The issuer must not be null");
        }
        this.issuer = issuer;
        if (listingSpec == null) {
            throw new IllegalArgumentException("The listing spec must not be null");
        }
        this.listingSpec = listingSpec;
    }

    public Issuer getIssuer() {
        return this.issuer;
    }

    public EntityListingSpec getListingSpec() {
        return this.listingSpec;
    }

    @Override
    public Map<String, List<String>> toParameters() {
        LinkedHashMap<String, List<String>> params = new LinkedHashMap<String, List<String>>();
        params.put("operation", Collections.singletonList(this.getOperationType().getValue()));
        params.put("iss", Collections.singletonList(this.getIssuer().getValue()));
        switch (this.getListingSpec()) {
            case LEAF_ENTITIES_ONLY: {
                params.put("is_leaf", Collections.singletonList("true"));
                break;
            }
            case INTERMEDIATES_ONLY: {
                params.put("is_leaf", Collections.singletonList("false"));
                break;
            }
        }
        return params;
    }

    public static EntityListingRequest parse(Map<String, List<String>> params) throws ParseException {
        String value = MultivaluedMapUtils.getFirstValue(params, "operation");
        if (StringUtils.isBlank(value)) {
            throw new ParseException("Missing operation type");
        }
        if (!OperationType.LISTING.getValue().equals(value)) {
            throw new ParseException("The operation type must be listing");
        }
        value = MultivaluedMapUtils.getFirstValue(params, "iss");
        if (value == null) {
            throw new ParseException("Missing iss (issuer) parameter");
        }
        Issuer issuer = new Issuer(value);
        value = MultivaluedMapUtils.getFirstValue(params, "is_leaf");
        EntityListingSpec listingSpec = EntityListingSpec.ALL;
        if ("true".equals(value)) {
            listingSpec = EntityListingSpec.LEAF_ENTITIES_ONLY;
        } else if ("false".equals(value)) {
            listingSpec = EntityListingSpec.INTERMEDIATES_ONLY;
        }
        return new EntityListingRequest(null, issuer, listingSpec);
    }

    public static EntityListingRequest parse(HTTPRequest httpRequest) throws ParseException {
        httpRequest.ensureMethod(HTTPRequest.Method.GET);
        EntityListingRequest request = EntityListingRequest.parse(httpRequest.getQueryParameters());
        return new EntityListingRequest(httpRequest.getURI(), request.getIssuer(), request.getListingSpec());
    }
}

