/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.util;

import com.nimbusds.jwt.JWTClaimsSet;
import java.util.List;
import java.util.Map;

public final class JWTClaimsSetUtils {
    public static JWTClaimsSet toJWTClaimsSet(Map<String, List<String>> params) {
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
        for (Map.Entry<String, List<String>> en : params.entrySet()) {
            if (en.getValue().size() == 1) {
                String singleValue = en.getValue().get(0);
                builder.claim(en.getKey(), singleValue);
                continue;
            }
            if (en.getValue().size() <= 0) continue;
            List<String> multiValue = en.getValue();
            builder.claim(en.getKey(), multiValue);
        }
        return builder.build();
    }

    private JWTClaimsSetUtils() {
    }
}

