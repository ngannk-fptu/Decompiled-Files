/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.device;

import com.nimbusds.oauth2.sdk.ErrorObject;

public final class DeviceAuthorizationGrantError {
    public static final ErrorObject AUTHORIZATION_PENDING = new ErrorObject("authorization_pending", "Authorization pending", 400);
    public static final ErrorObject SLOW_DOWN = new ErrorObject("slow_down", "Slow down", 400);
    public static final ErrorObject EXPIRED_TOKEN = new ErrorObject("expired_token", "Expired token", 400);

    private DeviceAuthorizationGrantError() {
    }
}

