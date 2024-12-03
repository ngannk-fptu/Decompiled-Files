/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.config.annotation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.util.CollectionUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistration;

public class CorsRegistry {
    private final List<CorsRegistration> registrations = new ArrayList<CorsRegistration>();

    public CorsRegistration addMapping(String pathPattern) {
        CorsRegistration registration = new CorsRegistration(pathPattern);
        this.registrations.add(registration);
        return registration;
    }

    protected Map<String, CorsConfiguration> getCorsConfigurations() {
        LinkedHashMap<String, CorsConfiguration> configs = CollectionUtils.newLinkedHashMap(this.registrations.size());
        for (CorsRegistration registration : this.registrations) {
            configs.put(registration.getPathPattern(), registration.getCorsConfiguration());
        }
        return configs;
    }
}

