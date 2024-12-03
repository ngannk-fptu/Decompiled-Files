/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  javax.annotation.Nonnull
 *  net.minidev.json.JSONObject
 */
package com.atlassian.jwt.core.writer;

import com.atlassian.jwt.core.TimeUtil;
import com.atlassian.jwt.writer.JwtJsonBuilder;
import com.google.common.base.Objects;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minidev.json.JSONObject;

public class JsonSmartJwtJsonBuilder
implements JwtJsonBuilder {
    private final JSONObject json = new JSONObject();

    public JsonSmartJwtJsonBuilder() {
        this.issuedAt(TimeUtil.currentTimeSeconds());
        this.expirationTime(TimeUtil.currentTimePlusNSeconds(180L));
    }

    @Override
    @Nonnull
    public JwtJsonBuilder audience(@Nonnull String aud) {
        this.json.put((Object)"aud", (Object)aud);
        return this;
    }

    @Override
    @Nonnull
    public JwtJsonBuilder expirationTime(long exp) {
        this.json.put((Object)"exp", (Object)exp);
        return this;
    }

    @Override
    public boolean isClaimSet(@Nonnull String name) {
        return this.json.containsKey((Object)name);
    }

    @Override
    @Nonnull
    public JwtJsonBuilder issuedAt(long iat) {
        this.json.put((Object)"iat", (Object)iat);
        return this;
    }

    @Override
    @Nonnull
    public JwtJsonBuilder issuer(@Nonnull String iss) {
        this.json.put((Object)"iss", (Object)iss);
        return this;
    }

    @Override
    @Nonnull
    public JwtJsonBuilder jwtId(@Nonnull String jti) {
        this.json.put((Object)"jti", (Object)jti);
        return this;
    }

    @Override
    @Nonnull
    public JwtJsonBuilder notBefore(long nbf) {
        this.json.put((Object)"nbf", (Object)nbf);
        return this;
    }

    @Override
    @Nonnull
    public JwtJsonBuilder subject(@Nonnull String sub) {
        this.json.put((Object)"sub", (Object)sub);
        return this;
    }

    @Override
    @Nonnull
    public JwtJsonBuilder type(@Nonnull String typ) {
        this.json.put((Object)"typ", (Object)typ);
        return this;
    }

    @Override
    @Nonnull
    public JwtJsonBuilder queryHash(@Nonnull String qsh) {
        this.json.put((Object)"qsh", (Object)qsh);
        return this;
    }

    @Override
    @Nonnull
    public JwtJsonBuilder claim(@Nonnull String name, @Nonnull Object obj) {
        Object current = this.json.get((Object)name);
        this.json.put((Object)name, this.merge(name, current, obj));
        return this;
    }

    @Override
    @Nonnull
    public String build() {
        return this.json.toString();
    }

    public String toString() {
        return this.json.toString();
    }

    private Object merge(String name, Object first, Object second) {
        if (first instanceof List && second instanceof List) {
            ArrayList merged = new ArrayList((List)first);
            merged.addAll((List)second);
            return merged;
        }
        if (first instanceof Map && second instanceof Map) {
            HashMap merged = new HashMap((Map)first);
            Set entries = ((Map)second).entrySet();
            for (Map.Entry entry : entries) {
                merged.put(entry.getKey(), this.merge(name + "." + entry.getKey(), merged.get(entry.getKey()), entry.getValue()));
            }
            return merged;
        }
        if (first != null && second != null && !Objects.equal((Object)first, (Object)second)) {
            throw new IllegalStateException("Cannot set claim '" + name + "' to '" + second + "'; it's already set as '" + first + "'");
        }
        return second == null ? first : second;
    }
}

