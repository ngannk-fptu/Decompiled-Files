/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.api;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.nimbusds.openid.connect.sdk.federation.api.FederationAPIRequest;
import com.nimbusds.openid.connect.sdk.federation.api.OperationType;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityID;
import com.nimbusds.openid.connect.sdk.federation.entities.FederationMetadataType;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.Immutable;

@Immutable
public class TrustNegotiationRequest
extends FederationAPIRequest {
    private final EntityID respondent;
    private final EntityID peer;
    private final FederationMetadataType metadataType;
    private final EntityID anchor;

    public TrustNegotiationRequest(URI endpoint, EntityID respondent, EntityID peer, FederationMetadataType metadataType, EntityID anchor) {
        super(endpoint, OperationType.RESOLVE_METADATA);
        if (respondent == null) {
            throw new IllegalArgumentException("The respondent must not be null");
        }
        this.respondent = respondent;
        if (peer == null) {
            throw new IllegalArgumentException("The peer must not be null");
        }
        this.peer = peer;
        if (metadataType == null) {
            throw new IllegalArgumentException("The metadata type must not be null");
        }
        this.metadataType = metadataType;
        if (anchor == null) {
            throw new IllegalArgumentException("The anchor must not be null");
        }
        this.anchor = anchor;
    }

    public EntityID getRespondent() {
        return this.respondent;
    }

    public EntityID getPeer() {
        return this.peer;
    }

    public FederationMetadataType getMetadataType() {
        return this.metadataType;
    }

    public EntityID getTrustAnchor() {
        return this.anchor;
    }

    @Override
    public Map<String, List<String>> toParameters() {
        LinkedHashMap<String, List<String>> params = new LinkedHashMap<String, List<String>>();
        params.put("operation", Collections.singletonList(this.getOperationType().getValue()));
        params.put("respondent", Collections.singletonList(this.getRespondent().getValue()));
        params.put("peer", Collections.singletonList(this.getPeer().getValue()));
        params.put("type", Collections.singletonList(this.getMetadataType().getValue()));
        params.put("anchor", Collections.singletonList(this.getTrustAnchor().getValue()));
        return params;
    }

    public static TrustNegotiationRequest parse(Map<String, List<String>> params) throws ParseException {
        String value = MultivaluedMapUtils.getFirstValue(params, "operation");
        if (StringUtils.isBlank(value)) {
            throw new ParseException("Missing operation type");
        }
        if (!OperationType.RESOLVE_METADATA.getValue().equals(value)) {
            throw new ParseException("The operation type must be " + OperationType.RESOLVE_METADATA);
        }
        value = MultivaluedMapUtils.getFirstValue(params, "respondent");
        if (StringUtils.isBlank(value)) {
            throw new ParseException("Missing respondent");
        }
        EntityID respondent = new EntityID(value);
        value = MultivaluedMapUtils.getFirstValue(params, "peer");
        if (StringUtils.isBlank(value)) {
            throw new ParseException("Missing peer");
        }
        EntityID peer = new EntityID(value);
        value = MultivaluedMapUtils.getFirstValue(params, "type");
        if (StringUtils.isBlank(value)) {
            throw new ParseException("Missing metadata type");
        }
        FederationMetadataType metadataType = new FederationMetadataType(value);
        value = MultivaluedMapUtils.getFirstValue(params, "anchor");
        if (StringUtils.isBlank(value)) {
            throw new ParseException("Missing anchor");
        }
        EntityID anchor = new EntityID(value);
        return new TrustNegotiationRequest(null, respondent, peer, metadataType, anchor);
    }

    public static TrustNegotiationRequest parse(HTTPRequest httpRequest) throws ParseException {
        httpRequest.ensureMethod(HTTPRequest.Method.GET);
        TrustNegotiationRequest request = TrustNegotiationRequest.parse(httpRequest.getQueryParameters());
        return new TrustNegotiationRequest(httpRequest.getURI(), request.respondent, request.getPeer(), request.getMetadataType(), request.getTrustAnchor());
    }
}

