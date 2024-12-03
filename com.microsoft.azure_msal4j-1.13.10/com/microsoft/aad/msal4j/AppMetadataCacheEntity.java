/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.microsoft.aad.msal4j;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;

class AppMetadataCacheEntity {
    public static final String APP_METADATA_CACHE_ENTITY_ID = "appmetadata";
    @JsonProperty(value="client_id")
    private String clientId;
    @JsonProperty(value="environment")
    private String environment;
    @JsonProperty(value="family_id")
    private String familyId;

    AppMetadataCacheEntity() {
    }

    String getKey() {
        ArrayList<String> keyParts = new ArrayList<String>();
        keyParts.add(APP_METADATA_CACHE_ENTITY_ID);
        keyParts.add(this.environment);
        keyParts.add(this.clientId);
        return String.join((CharSequence)"-", keyParts).toLowerCase();
    }

    public String clientId() {
        return this.clientId;
    }

    public String environment() {
        return this.environment;
    }

    public String familyId() {
        return this.familyId;
    }

    public AppMetadataCacheEntity clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public AppMetadataCacheEntity environment(String environment) {
        this.environment = environment;
        return this;
    }

    public AppMetadataCacheEntity familyId(String familyId) {
        this.familyId = familyId;
        return this;
    }
}

