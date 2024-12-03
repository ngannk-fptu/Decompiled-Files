/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.oauth2.sdk.device;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Response;
import com.nimbusds.oauth2.sdk.device.DeviceAuthorizationErrorResponse;
import com.nimbusds.oauth2.sdk.device.DeviceAuthorizationSuccessResponse;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import net.minidev.json.JSONObject;

public abstract class DeviceAuthorizationResponse
implements Response {
    public DeviceAuthorizationSuccessResponse toSuccessResponse() {
        return (DeviceAuthorizationSuccessResponse)this;
    }

    public DeviceAuthorizationErrorResponse toErrorResponse() {
        return (DeviceAuthorizationErrorResponse)this;
    }

    public static DeviceAuthorizationResponse parse(JSONObject jsonObject) throws ParseException {
        if (jsonObject.containsKey((Object)"device_code")) {
            return DeviceAuthorizationSuccessResponse.parse(jsonObject);
        }
        return DeviceAuthorizationErrorResponse.parse(jsonObject);
    }

    public static DeviceAuthorizationResponse parse(HTTPResponse httpResponse) throws ParseException {
        if (httpResponse.getStatusCode() == 200) {
            return DeviceAuthorizationSuccessResponse.parse(httpResponse);
        }
        return DeviceAuthorizationErrorResponse.parse(httpResponse);
    }
}

