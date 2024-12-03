/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 *  net.minidev.json.JSONArray
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.openid.connect.sdk.assurance.request;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.assurance.IdentityTrustFramework;
import com.nimbusds.openid.connect.sdk.assurance.request.VerificationSpec;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

@Immutable
public class MinimalVerificationSpec
implements VerificationSpec {
    protected final JSONObject jsonObject;

    protected MinimalVerificationSpec(JSONObject jsonObject) {
        Objects.requireNonNull(jsonObject);
        this.jsonObject = jsonObject;
    }

    public MinimalVerificationSpec() {
        this(new JSONObject());
        this.jsonObject.put((Object)"trust_framework", null);
    }

    public MinimalVerificationSpec(IdentityTrustFramework trustFramework) {
        this();
        if (trustFramework != null) {
            JSONObject tfSpec = new JSONObject();
            tfSpec.put((Object)"value", (Object)trustFramework.getValue());
            this.jsonObject.put((Object)"trust_framework", (Object)tfSpec);
        }
    }

    public MinimalVerificationSpec(List<IdentityTrustFramework> trustFrameworks) {
        this();
        if (CollectionUtils.isNotEmpty(trustFrameworks)) {
            JSONObject tfSpec = new JSONObject();
            JSONArray tfValues = new JSONArray();
            for (IdentityTrustFramework tf : trustFrameworks) {
                if (tf == null) continue;
                tfValues.add((Object)tf.getValue());
            }
            tfSpec.put((Object)"values", (Object)tfValues);
            this.jsonObject.put((Object)"trust_framework", (Object)tfSpec);
        }
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        o.putAll((Map)this.jsonObject);
        return o;
    }

    public static MinimalVerificationSpec parse(JSONObject jsonObject) throws ParseException {
        if (!jsonObject.containsKey((Object)"trust_framework")) {
            throw new ParseException("Missing required trust_framework key");
        }
        if (jsonObject.get((Object)"trust_framework") != null) {
            JSONObject tfSpec = JSONObjectUtils.getJSONObject(jsonObject, "trust_framework");
            try {
                MinimalVerificationSpec.validateTrustFrameworkSpec(tfSpec);
            }
            catch (ParseException e) {
                throw new ParseException("Invalid trust_framework spec: " + e.getMessage(), e);
            }
        }
        return new MinimalVerificationSpec(jsonObject);
    }

    private static void validateTrustFrameworkSpec(JSONObject tfSpec) throws ParseException {
        String value = null;
        if (tfSpec.containsKey((Object)"value")) {
            value = JSONObjectUtils.getString(tfSpec, "value");
        }
        List<String> values = null;
        if (tfSpec.containsKey((Object)"values") && (values = JSONObjectUtils.getStringList(tfSpec, "values")).isEmpty()) {
            throw new ParseException("The values JSON array must not be empty");
        }
        if (value != null && values != null) {
            throw new ParseException("Value and values must not be set together");
        }
    }
}

