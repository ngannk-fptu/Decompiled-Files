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
    private final Map<PathPattern, CorsConfiguration> corsConfigurations = new LinkedHashMap<PathPattern, CorsConfiguration>();
    private final PathPatternParser patternParser;

    public UrlBasedCorsConfigurationSource() {
        this(new PathPatternParser());
    }

    public UrlBasedCorsConfigurationSource(PathPatternParser patternParser) {
        this.patternParser = patternParser;
    }

    public void setCorsConfigurations(@Nullable Map<String, CorsConfiguration> corsConfigurations) {
        this.corsConfigurations.clear();
        if (corsConfigurations != null) {
            corsConfigurations.forEach(this::registerCorsConfiguration);
        }
    }

    public void registerCorsConfiguration(String path, CorsConfiguration config) {
        this.corsConfigurations.put(this.patternParser.parse(path), config);
    }

    @Override
    @Nullable
    public CorsConfiguration getCorsConfiguration(ServerWebExchange exchange2) {
        PathContainer lookupPath = exchange2.getRequest().getPath().pathWithinApplication();
        return this.corsConfigurations.entrySet().stream().filter(entry -> ((PathPattern)entry.getKey()).matches(lookupPath)).map(Map.Entry::getValue).findFirst().orElse(null);
    }
}

