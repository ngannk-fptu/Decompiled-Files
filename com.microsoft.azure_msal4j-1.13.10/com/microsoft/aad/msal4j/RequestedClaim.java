/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonAnyGetter
 *  com.fasterxml.jackson.annotation.JsonIgnore
 *  com.fasterxml.jackson.annotation.JsonInclude
 *  com.fasterxml.jackson.annotation.JsonInclude$Include
 */
package com.microsoft.aad.msal4j;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.microsoft.aad.msal4j.RequestedClaimAdditionalInfo;
import java.util.Collections;
import java.util.Map;

@JsonInclude(value=JsonInclude.Include.NON_NULL)
public class RequestedClaim {
    @JsonIgnore
    public String name;
    RequestedClaimAdditionalInfo requestedClaimAdditionalInfo;

    @JsonAnyGetter
    protected Map<String, Object> any() {
        return Collections.singletonMap(this.name, this.requestedClaimAdditionalInfo);
    }

    public RequestedClaim(String name, RequestedClaimAdditionalInfo requestedClaimAdditionalInfo) {
        this.name = name;
        this.requestedClaimAdditionalInfo = requestedClaimAdditionalInfo;
    }
}

