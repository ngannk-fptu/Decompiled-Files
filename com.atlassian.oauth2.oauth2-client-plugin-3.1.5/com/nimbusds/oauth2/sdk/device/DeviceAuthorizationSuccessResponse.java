/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.device;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.SuccessResponse;
import com.nimbusds.oauth2.sdk.device.DeviceAuthorizationResponse;
import com.nimbusds.oauth2.sdk.device.DeviceCode;
import com.nimbusds.oauth2.sdk.device.UserCode;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class DeviceAuthorizationSuccessResponse
extends DeviceAuthorizationResponse
implements SuccessResponse {
    private static final Set<String> REGISTERED_PARAMETER_NAMES;
    private final DeviceCode deviceCode;
    private final UserCode userCode;
    private final URI verificationURI;
    private final URI verificationURIComplete;
    private final long lifetime;
    private final long interval;
    private final Map<String, Object> customParams;

    public DeviceAuthorizationSuccessResponse(DeviceCode deviceCode, UserCode userCode, URI verificationURI, long lifetime) {
        this(deviceCode, userCode, verificationURI, null, lifetime, 5L, null);
    }

    public DeviceAuthorizationSuccessResponse(DeviceCode deviceCode, UserCode userCode, URI verificationURI, URI verificationURIComplete, long lifetime, long interval, Map<String, Object> customParams) {
        if (deviceCode == null) {
            throw new IllegalArgumentException("The device_code must not be null");
        }
        this.deviceCode = deviceCode;
        if (userCode == null) {
            throw new IllegalArgumentException("The user_code must not be null");
        }
        this.userCode = userCode;
        if (verificationURI == null) {
            throw new IllegalArgumentException("The verification_uri must not be null");
        }
        this.verificationURI = verificationURI;
        this.verificationURIComplete = verificationURIComplete;
        if (lifetime <= 0L) {
            throw new IllegalArgumentException("The lifetime must be greater than 0");
        }
        this.lifetime = lifetime;
        this.interval = interval;
        this.customParams = customParams;
    }

    public static Set<String> getRegisteredParameterNames() {
        return REGISTERED_PARAMETER_NAMES;
    }

    @Override
    public boolean indicatesSuccess() {
        return true;
    }

    public DeviceCode getDeviceCode() {
        return this.deviceCode;
    }

    public UserCode getUserCode() {
        return this.userCode;
    }

    public URI getVerificationURI() {
        return this.verificationURI;
    }

    @Deprecated
    public URI getVerificationUri() {
        return this.getVerificationURI();
    }

    public URI getVerificationURIComplete() {
        return this.verificationURIComplete;
    }

    @Deprecated
    public URI getVerificationUriComplete() {
        return this.getVerificationURIComplete();
    }

    public long getLifetime() {
        return this.lifetime;
    }

    public long getInterval() {
        return this.interval;
    }

    public Map<String, Object> getCustomParameters() {
        if (this.customParams == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(this.customParams);
    }

    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        o.put("device_code", this.getDeviceCode());
        o.put("user_code", this.getUserCode());
        o.put("verification_uri", this.getVerificationURI().toString());
        if (this.getVerificationURIComplete() != null) {
            o.put("verification_uri_complete", this.getVerificationURIComplete().toString());
        }
        o.put("expires_in", this.getLifetime());
        if (this.getInterval() > 0L) {
            o.put("interval", this.getInterval());
        }
        if (this.customParams != null) {
            o.putAll(this.customParams);
        }
        return o;
    }

    @Override
    public HTTPResponse toHTTPResponse() {
        HTTPResponse httpResponse = new HTTPResponse(200);
        httpResponse.setEntityContentType(ContentType.APPLICATION_JSON);
        httpResponse.setCacheControl("no-store");
        httpResponse.setPragma("no-cache");
        httpResponse.setContent(this.toJSONObject().toString());
        return httpResponse;
    }

    public static DeviceAuthorizationSuccessResponse parse(JSONObject jsonObject) throws ParseException {
        long lifetime;
        DeviceCode deviceCode = new DeviceCode(JSONObjectUtils.getString(jsonObject, "device_code"));
        UserCode userCode = new UserCode(JSONObjectUtils.getString(jsonObject, "user_code"));
        URI verificationURI = JSONObjectUtils.getURI(jsonObject, "verification_uri");
        URI verificationURIComplete = JSONObjectUtils.getURI(jsonObject, "verification_uri_complete", null);
        if (jsonObject.get("expires_in") instanceof Number) {
            lifetime = JSONObjectUtils.getLong(jsonObject, "expires_in");
        } else {
            String lifetimeStr = JSONObjectUtils.getString(jsonObject, "expires_in");
            try {
                lifetime = Long.parseLong(lifetimeStr);
            }
            catch (NumberFormatException e) {
                throw new ParseException("Invalid expires_in parameter, must be integer");
            }
        }
        long interval = 5L;
        if (jsonObject.containsKey("interval")) {
            if (jsonObject.get("interval") instanceof Number) {
                interval = JSONObjectUtils.getLong(jsonObject, "interval");
            } else {
                String intervalStr = JSONObjectUtils.getString(jsonObject, "interval");
                try {
                    interval = Long.parseLong(intervalStr);
                }
                catch (NumberFormatException e) {
                    throw new ParseException("Invalid interval parameter, must be integer");
                }
            }
        }
        HashSet customParamNames = new HashSet(jsonObject.keySet());
        customParamNames.removeAll(DeviceAuthorizationSuccessResponse.getRegisteredParameterNames());
        LinkedHashMap<String, Object> customParams = null;
        if (!customParamNames.isEmpty()) {
            customParams = new LinkedHashMap<String, Object>();
            for (String name : customParamNames) {
                customParams.put(name, jsonObject.get(name));
            }
        }
        return new DeviceAuthorizationSuccessResponse(deviceCode, userCode, verificationURI, verificationURIComplete, lifetime, interval, customParams);
    }

    public static DeviceAuthorizationSuccessResponse parse(HTTPResponse httpResponse) throws ParseException {
        httpResponse.ensureStatusCode(200);
        JSONObject jsonObject = httpResponse.getContentAsJSONObject();
        return DeviceAuthorizationSuccessResponse.parse(jsonObject);
    }

    static {
        HashSet<String> p = new HashSet<String>();
        p.add("device_code");
        p.add("user_code");
        p.add("verification_uri");
        p.add("verification_uri_complete");
        p.add("expires_in");
        p.add("interval");
        REGISTERED_PARAMETER_NAMES = Collections.unmodifiableSet(p);
    }
}

