/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose;

import com.nimbusds.jose.util.JSONObjectUtils;
import java.text.ParseException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.jcip.annotations.Immutable;

@Immutable
public final class UnprotectedHeader {
    private final Map<String, Object> params;

    private UnprotectedHeader(Map<String, Object> params) {
        Objects.requireNonNull(params);
        this.params = params;
    }

    public String getKeyID() {
        return (String)this.params.get("kid");
    }

    public Object getParam(String name) {
        return this.params.get(name);
    }

    public Set<String> getIncludedParams() {
        return this.params.keySet();
    }

    public Map<String, Object> toJSONObject() {
        Map<String, Object> o = JSONObjectUtils.newJSONObject();
        o.putAll(this.params);
        return o;
    }

    public static UnprotectedHeader parse(Map<String, Object> jsonObject) throws ParseException {
        if (jsonObject == null) {
            return null;
        }
        Builder header = new Builder();
        for (String name : jsonObject.keySet()) {
            header = header.param(name, jsonObject.get(name));
        }
        return header.build();
    }

    public static class Builder {
        private final Map<String, Object> params = JSONObjectUtils.newJSONObject();

        public Builder keyID(String kid) {
            this.params.put("kid", kid);
            return this;
        }

        public Builder param(String name, Object value) {
            this.params.put(name, value);
            return this;
        }

        public UnprotectedHeader build() {
            return new UnprotectedHeader(this.params);
        }
    }
}

