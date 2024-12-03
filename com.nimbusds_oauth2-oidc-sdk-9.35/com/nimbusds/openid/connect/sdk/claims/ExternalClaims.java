/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.openid.connect.sdk.claims;

import java.util.Set;
import net.minidev.json.JSONObject;

abstract class ExternalClaims {
    private final String sourceID;
    private final Set<String> names;

    protected ExternalClaims(String sourceID, Set<String> names) {
        if (sourceID == null || sourceID.trim().isEmpty()) {
            throw new IllegalArgumentException("The claims source identifier must not be null or empty");
        }
        this.sourceID = sourceID;
        if (names == null || names.isEmpty()) {
            throw new IllegalArgumentException("The claim names must not be null or empty");
        }
        this.names = names;
    }

    public String getSourceID() {
        return this.sourceID;
    }

    public Set<String> getNames() {
        return this.names;
    }

    abstract void mergeInto(JSONObject var1);
}

