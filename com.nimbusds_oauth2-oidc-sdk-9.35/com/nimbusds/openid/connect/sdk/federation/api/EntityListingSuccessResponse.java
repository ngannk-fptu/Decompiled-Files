/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.common.contenttype.ContentType
 *  net.jcip.annotations.Immutable
 *  net.minidev.json.JSONArray
 */
package com.nimbusds.openid.connect.sdk.federation.api;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.util.JSONArrayUtils;
import com.nimbusds.openid.connect.sdk.federation.api.EntityListingResponse;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityID;
import java.util.LinkedList;
import java.util.List;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONArray;

@Immutable
public class EntityListingSuccessResponse
extends EntityListingResponse {
    private final List<EntityID> entityIDS;

    public EntityListingSuccessResponse(List<EntityID> entityIDS) {
        if (entityIDS == null) {
            throw new IllegalArgumentException("The entity listing must not be null");
        }
        this.entityIDS = entityIDS;
    }

    public List<EntityID> getEntityListing() {
        return this.entityIDS;
    }

    @Override
    public boolean indicatesSuccess() {
        return true;
    }

    @Override
    public HTTPResponse toHTTPResponse() {
        HTTPResponse httpResponse = new HTTPResponse(200);
        httpResponse.setEntityContentType(ContentType.APPLICATION_JSON);
        JSONArray jsonArray = new JSONArray();
        for (EntityID entityID : this.getEntityListing()) {
            jsonArray.add((Object)entityID.getValue());
        }
        httpResponse.setContent(jsonArray.toJSONString());
        return httpResponse;
    }

    public static EntityListingSuccessResponse parse(JSONArray jsonArray) {
        List<String> values = JSONArrayUtils.toStringList(jsonArray);
        LinkedList<EntityID> entityIDS = new LinkedList<EntityID>();
        for (String v : values) {
            entityIDS.add(new EntityID(v));
        }
        return new EntityListingSuccessResponse(entityIDS);
    }

    public static EntityListingSuccessResponse parse(HTTPResponse httpResponse) throws ParseException {
        httpResponse.ensureStatusCode(200);
        return EntityListingSuccessResponse.parse(httpResponse.getContentAsJSONArray());
    }
}

