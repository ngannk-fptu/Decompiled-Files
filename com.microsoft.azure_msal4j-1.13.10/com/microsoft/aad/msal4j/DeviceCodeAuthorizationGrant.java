/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.AbstractMsalAuthorizationGrant;
import com.microsoft.aad.msal4j.ClaimsRequest;
import com.microsoft.aad.msal4j.DeviceCode;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class DeviceCodeAuthorizationGrant
extends AbstractMsalAuthorizationGrant {
    private static final String GRANT_TYPE = "device_code";
    private final DeviceCode deviceCode;
    private final String scopes;
    private String correlationId;

    DeviceCodeAuthorizationGrant(DeviceCode deviceCode, String scopes, ClaimsRequest claims) {
        this.deviceCode = deviceCode;
        this.correlationId = deviceCode.correlationId();
        this.scopes = scopes;
        this.claims = claims;
    }

    @Override
    public Map<String, List<String>> toParameters() {
        LinkedHashMap<String, List<String>> outParams = new LinkedHashMap<String, List<String>>();
        outParams.put("scope", Collections.singletonList("openid profile offline_access " + this.scopes));
        outParams.put("grant_type", Collections.singletonList(GRANT_TYPE));
        outParams.put(GRANT_TYPE, Collections.singletonList(this.deviceCode.deviceCode()));
        outParams.put("client_info", Collections.singletonList("1"));
        if (this.claims != null) {
            outParams.put("claims", Collections.singletonList(this.claims.formatAsJSONString()));
        }
        return outParams;
    }

    public String getCorrelationId() {
        return this.correlationId;
    }
}

