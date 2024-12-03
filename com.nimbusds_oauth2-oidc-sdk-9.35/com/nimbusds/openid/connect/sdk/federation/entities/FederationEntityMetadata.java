/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jwt.SignedJWT
 *  net.minidev.json.JSONArray
 *  net.minidev.json.JSONAware
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.openid.connect.sdk.federation.entities;

import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.nimbusds.oauth2.sdk.util.JSONArrayUtils;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityID;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;

public class FederationEntityMetadata
implements JSONAware {
    private final URI federationAPIEndpoint;
    private EntityID trustAnchorID;
    private String name;
    private List<String> contacts;
    private URI policyURI;
    private URI homepageURI;
    private List<SignedJWT> trustMarks;

    public FederationEntityMetadata(URI federationEndpoint) {
        this.federationAPIEndpoint = federationEndpoint;
    }

    public URI getFederationAPIEndpointURI() {
        return this.federationAPIEndpoint;
    }

    public EntityID getTrustAnchorID() {
        return this.trustAnchorID;
    }

    public void setTrustAnchorID(EntityID trustAnchorID) {
        this.trustAnchorID = trustAnchorID;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getContacts() {
        return this.contacts;
    }

    public void setContacts(List<String> contacts) {
        this.contacts = contacts;
    }

    public URI getPolicyURI() {
        return this.policyURI;
    }

    public void setPolicyURI(URI policyURI) {
        this.policyURI = policyURI;
    }

    public URI getHomepageURI() {
        return this.homepageURI;
    }

    public void setHomepageURI(URI homepageURI) {
        this.homepageURI = homepageURI;
    }

    public List<SignedJWT> getTrustMarks() {
        return this.trustMarks;
    }

    public void setTrustMarks(List<SignedJWT> trustMarks) {
        this.trustMarks = trustMarks;
    }

    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        if (this.getFederationAPIEndpointURI() != null) {
            o.put((Object)"federation_api_endpoint", (Object)this.getFederationAPIEndpointURI().toString());
        }
        if (this.getTrustAnchorID() != null) {
            o.put((Object)"trust_anchor_id", (Object)this.getTrustAnchorID().getValue());
        }
        if (this.getName() != null) {
            o.put((Object)"name", (Object)this.getName());
        }
        if (this.getContacts() != null) {
            o.put((Object)"contacts", this.getContacts());
        }
        if (this.getPolicyURI() != null) {
            o.put((Object)"policy_uri", (Object)this.getPolicyURI().toString());
        }
        if (this.getHomepageURI() != null) {
            o.put((Object)"homepage_uri", (Object)this.getHomepageURI().toString());
        }
        if (CollectionUtils.isNotEmpty(this.trustMarks)) {
            JSONArray jsonArray = new JSONArray();
            for (SignedJWT jwt : this.trustMarks) {
                jsonArray.add((Object)jwt.serialize());
            }
            o.put((Object)"trust_marks", (Object)jsonArray);
        }
        return o;
    }

    public String toJSONString() {
        return this.toJSONObject().toJSONString();
    }

    public static FederationEntityMetadata parse(JSONObject jsonObject) throws ParseException {
        URI federationAPIEndpoint = JSONObjectUtils.getURI(jsonObject, "federation_api_endpoint", null);
        FederationEntityMetadata metadata = new FederationEntityMetadata(federationAPIEndpoint);
        if (jsonObject.get((Object)"trust_anchor_id") != null) {
            metadata.setTrustAnchorID(new EntityID(JSONObjectUtils.getString(jsonObject, "trust_anchor_id")));
        }
        metadata.setName(JSONObjectUtils.getString(jsonObject, "name", null));
        metadata.setContacts(JSONObjectUtils.getStringList(jsonObject, "contacts", null));
        metadata.setPolicyURI(JSONObjectUtils.getURI(jsonObject, "policy_uri", null));
        metadata.setHomepageURI(JSONObjectUtils.getURI(jsonObject, "homepage_uri", null));
        JSONArray trustMarksArray = JSONObjectUtils.getJSONArray(jsonObject, "trust_marks", null);
        LinkedList<SignedJWT> trustMarks = null;
        if (CollectionUtils.isNotEmpty(trustMarksArray)) {
            trustMarks = new LinkedList<SignedJWT>();
            for (String jwtString : JSONArrayUtils.toStringList(trustMarksArray)) {
                try {
                    trustMarks.add(SignedJWT.parse((String)jwtString));
                }
                catch (java.text.ParseException e) {
                    throw new ParseException("Invalid trust mark JWT: " + e.getMessage());
                }
            }
        }
        metadata.setTrustMarks(trustMarks);
        return metadata;
    }

    public static FederationEntityMetadata parse(String json) throws ParseException {
        return FederationEntityMetadata.parse(JSONObjectUtils.parse(json));
    }
}

