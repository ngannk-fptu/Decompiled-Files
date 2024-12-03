/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.cors.reactive;

import org.springframework.lang.Nullable;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.server.ServerWebExchange;

public interface CorsConfigurationSource {
    @Nullable
    public CorsConfiguration getCorsConfiguration(ServerWebExchange var1);
}

