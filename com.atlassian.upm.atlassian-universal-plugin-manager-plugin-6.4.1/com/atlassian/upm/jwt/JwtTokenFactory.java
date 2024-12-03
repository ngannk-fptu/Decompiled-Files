/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.jwt;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.jwt.UpmJwtToken;
import java.util.Map;

public interface JwtTokenFactory {
    public UpmJwtToken generateToken(String var1, Map<String, String> var2, Option<? extends Object> var3);
}

