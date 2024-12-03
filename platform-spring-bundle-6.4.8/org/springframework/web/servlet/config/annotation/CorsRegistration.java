/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.config.annotation;

import java.util.Arrays;
import org.springframework.web.cors.CorsConfiguration;

public class CorsRegistration {
    private final String pathPattern;
    private CorsConfiguration config;

    public CorsRegistration(String pathPattern) {
        this.pathPattern = pathPattern;
        this.config = new CorsConfiguration().applyPermitDefaultValues();
    }

    public CorsRegistration allowedOrigins(String ... origins) {
        this.config.setAllowedOrigins(Arrays.asList(origins));
        return this;
    }

    public CorsRegistration allowedOriginPatterns(String ... patterns) {
        this.config.setAllowedOriginPatterns(Arrays.asList(patterns));
        return this;
    }

    public CorsRegistration allowedMethods(String ... methods) {
        this.config.setAllowedMethods(Arrays.asList(methods));
        return this;
    }

    public CorsRegistration allowedHeaders(String ... headers) {
        this.config.setAllowedHeaders(Arrays.asList(headers));
        return this;
    }

    public CorsRegistration exposedHeaders(String ... headers) {
        this.config.setExposedHeaders(Arrays.asList(headers));
        return this;
    }

    public CorsRegistration allowCredentials(boolean allowCredentials) {
        this.config.setAllowCredentials(allowCredentials);
        return this;
    }

    public CorsRegistration maxAge(long maxAge) {
        this.config.setMaxAge(maxAge);
        return this;
    }

    public CorsRegistration combine(CorsConfiguration other) {
        this.config = this.config.combine(other);
        return this;
    }

    protected String getPathPattern() {
        return this.pathPattern;
    }

    protected CorsConfiguration getCorsConfiguration() {
        return this.config;
    }
}

