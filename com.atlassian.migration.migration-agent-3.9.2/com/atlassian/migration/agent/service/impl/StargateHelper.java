/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  okhttp3.Request$Builder
 */
package com.atlassian.migration.agent.service.impl;

import java.util.Objects;
import javax.annotation.ParametersAreNonnullByDefault;
import okhttp3.Request;

@ParametersAreNonnullByDefault
public final class StargateHelper {
    private static final String BEARER = "Bearer ";
    private static final String CLOUD_ID_HEADER = "ATL-TEST-CLOUD_ID";
    private static final String CLOUD_URL_HEADER = "ATL-TEST-CLOUD_URL";
    private static final String USER_ID_HEADER = "ATL-TEST-USER_ID";
    private static final int TOKEN_PARTS_IN_BYPASS_MODE = 3;

    private StargateHelper() {
        throw new IllegalStateException("StargateHelper class");
    }

    public static Request.Builder requestBuilder(String containerToken, boolean bypassStargate) {
        Objects.requireNonNull(containerToken);
        if (bypassStargate) {
            String[] containerTokenParts = StargateHelper.getTestContainerTokenParts(containerToken);
            return new Request.Builder().addHeader(CLOUD_ID_HEADER, containerTokenParts[0]).addHeader(CLOUD_URL_HEADER, containerTokenParts[1]).addHeader(USER_ID_HEADER, containerTokenParts[2]);
        }
        return new Request.Builder().addHeader("Authorization", BEARER + containerToken);
    }

    private static String[] getTestContainerTokenParts(String containerToken) {
        String[] keyParts = containerToken.split(",", 3);
        if (keyParts.length != 3) {
            throw new IllegalArgumentException("containerToken malformed");
        }
        return keyParts;
    }
}

