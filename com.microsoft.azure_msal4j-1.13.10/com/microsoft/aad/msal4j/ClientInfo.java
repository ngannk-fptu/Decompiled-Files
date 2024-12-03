/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  com.nimbusds.jose.util.StandardCharset
 */
package com.microsoft.aad.msal4j;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.aad.msal4j.JsonHelper;
import com.microsoft.aad.msal4j.StringHelper;
import com.nimbusds.jose.util.StandardCharset;
import java.util.Base64;

class ClientInfo {
    @JsonProperty(value="uid")
    private String uniqueIdentifier;
    @JsonProperty(value="utid")
    private String uniqueTenantIdentifier;

    ClientInfo() {
    }

    public static ClientInfo createFromJson(String clientInfoJsonBase64Encoded) {
        if (StringHelper.isBlank(clientInfoJsonBase64Encoded)) {
            return null;
        }
        byte[] decodedInput = Base64.getUrlDecoder().decode(clientInfoJsonBase64Encoded.getBytes(StandardCharset.UTF_8));
        return JsonHelper.convertJsonToObject(new String(decodedInput, StandardCharset.UTF_8), ClientInfo.class);
    }

    String toAccountIdentifier() {
        return this.uniqueIdentifier + "." + this.uniqueTenantIdentifier;
    }

    String getUniqueIdentifier() {
        return this.uniqueIdentifier;
    }

    String getUniqueTenantIdentifier() {
        return this.uniqueTenantIdentifier;
    }
}

