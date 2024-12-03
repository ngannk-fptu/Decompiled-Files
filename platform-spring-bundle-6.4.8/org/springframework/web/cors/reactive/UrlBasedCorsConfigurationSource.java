/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.cors.reactive;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.server.PathContainer;
import org.springframework.lang.Nullable;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

public class UrlBasedCorsConfigurationSource
implements CorsConfigurationSource {
    private final PathPatternParser patternParser;
    private final Map<PathPattern, CorsConfiguration> corsConfigurations = new LinkedHashMap<PathPattern, CorsConfiguration>();

    public UrlBasedCorsConfigurationSource() {
        this(PathPatternParser.defaultInstance);
    }

    public UrlBasedCorsConfigurationSource(PathPatternParser patternParser) {
        this.patternParser = patternParser;
    }

    public void setCorsConfigurations(@Nullable Map<String, CorsConfiguration> configMap) {
        this.corsConfigurations.clear();
        if (configMap != null) {
            configMap.forEach(this::registerCorsConfiguration);
        }
    }

    public void registerCorsConfiguration(String path, CorsConfiguration config) {
        this.corsConfigurations.put(this.patternParser.parse(path), config);
    }

    @Override
    @Nullable
    public CorsConfiguration getCorsConfiguration(ServerWebExchange exchange2) {
        PathContainer path = exchange2.getRequest().getPath().pathWithinApplication();
        for (Map.Entry<PathPattern, CorsConfiguration> entry : this.corsConfigurations.entrySet()) {
            if (!entry.getKey().matches(path)) continue;
            return entry.getValue();
        }
        return null;
    }
}

