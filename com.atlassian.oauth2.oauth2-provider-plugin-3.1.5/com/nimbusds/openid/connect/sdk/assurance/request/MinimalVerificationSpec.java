/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.request;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.assurance.IdentityTrustFramework;
import com.nimbusds.openid.connect.sdk.assurance.request.VerificationSpec;
import java.util.List;
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
        this.jsonObject.put("trust_framework", null);
    }

    public MinimalVerificationSpec(IdentityTrustFramework trustFramework) {
        this();
        if (trustFramework != null) {
            JSONObject tfSpec = new JSONObject();
            tfSpec.put("value", trustFramework.getValue());
            this.jsonObject.put("trust_framework", tfSpec);
        }
    }

    public MinimalVerificationSpec(List<IdentityTrustFramework> trustFrameworks) {
        this();
        if (CollectionUtils.isNotEmpty(trustFrameworks)) {
            JSONObject tfSpec = new JSONObject();
            JSONArray tfValues = new JSONArray();
            for (IdentityTrustFramework tf : trustFrameworks) {
                if (tf == null) continue;
                tfValues.add(tf.getValue());
            }
            tfSpec.put("values", tfValues);
            this.jsonObject.put("trust_framework", tfSpec);
        }
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        o.putAll(this.jsonObject);
        return o;
    }

    public static MinimalVerificationSpec parse(JSONObject jsonObject) throws ParseException {
        if (!jsonObject.containsKey("trust_framework")) {
            throw new ParseException("Missing required trust_framework key");
        }
        if (jsonObject.get("trust_framework") != null) {
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
        if (tfSpec.containsKey("value")) {
            value = JSONObjectUtils.getString(tfSpec, "value");
        }
        List<String> values = null;
        if (tfSpec.containsKey("values") && (values = JSONObjectUtils.getStringList(tfSpec, "values")).isEmpty()) {
            throw new ParseException("The values JSON array must not be empty");
        }
        if (value != null && values != null) {
            throw new ParseException("Value and values must not be set together");
        }
    }
}

