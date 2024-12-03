/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.device;

import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.GrantType;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.device.DeviceCode;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.Immutable;

@Immutable
public class DeviceCodeGrant
extends AuthorizationGrant {
    public static final GrantType GRANT_TYPE = GrantType.DEVICE_CODE;
    private final DeviceCode deviceCode;

    public DeviceCodeGrant(DeviceCode deviceCode) {
        super(GRANT_TYPE);
        if (deviceCode == null) {
            throw new IllegalArgumentException("The device code must not be null");
        }
        this.deviceCode = deviceCode;
    }

    public DeviceCode getDeviceCode() {
        return this.deviceCode;
    }

    @Override
    public Map<String, List<String>> toParameters() {
        LinkedHashMap<String, List<String>> params = new LinkedHashMap<String, List<String>>();
        params.put("grant_type", Collections.singletonList(GRANT_TYPE.getValue()));
        params.put("device_code", Collections.singletonList(this.deviceCode.getValue()));
        return params;
    }

    public static DeviceCodeGrant parse(Map<String, List<String>> params) throws ParseException {
        GrantType.ensure(GRANT_TYPE, params);
        String deviceCodeString = MultivaluedMapUtils.getFirstValue(params, "device_code");
        if (deviceCodeString == null || deviceCodeString.trim().isEmpty()) {
            String msg = "Missing or empty device_code parameter";
            throw new ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg));
        }
        DeviceCode deviceCode = new DeviceCode(deviceCodeString);
        return new DeviceCodeGrant(deviceCode);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeviceCodeGrant)) {
            return false;
        }
        DeviceCodeGrant deviceCodeGrant = (DeviceCodeGrant)o;
        return this.deviceCode.equals(deviceCodeGrant.deviceCode);
    }

    public int hashCode() {
        return this.deviceCode.hashCode();
    }
}

