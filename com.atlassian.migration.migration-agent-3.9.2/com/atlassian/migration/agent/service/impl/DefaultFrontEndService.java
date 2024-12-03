/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.type.TypeReference
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.FrontEndService;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
public class DefaultFrontEndService
implements FrontEndService {
    private static final Logger log = ContextLoggerFactory.getLogger(DefaultFrontEndService.class);
    private static final TypeReference<Map<String, String>> MAP_TYPE_REFERENCE = new TypeReference<Map<String, String>>(){};
    private static final String HASH_PATH = "/fe/hash.json";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final ApplicationProperties applicationProperties;
    private final MigrationAgentConfiguration configuration;
    private final Map<String, String> pathToHashMap;

    public DefaultFrontEndService(ApplicationProperties applicationProperties, MigrationAgentConfiguration configuration) {
        this.applicationProperties = applicationProperties;
        this.configuration = configuration;
        this.pathToHashMap = DefaultFrontEndService.readHashFilesMap();
    }

    @Override
    public String getFullPath(String resourcePath) {
        String mappedPath = this.configuration.isFrontEndDevModeEnabled() ? resourcePath : this.pathToHashMap.get(resourcePath);
        return String.format("%s/download/resources/com.atlassian.migration.agent:static-resources/fe/%s", this.applicationProperties.getBaseUrl(UrlMode.RELATIVE), mappedPath);
    }

    @Override
    public Optional<InputStream> openResourceStream(String resourceHashedPath) {
        String fullResourcePath = "/fe/" + resourceHashedPath;
        if (FrontEndService.class.getResource(fullResourcePath) == null) {
            return Optional.empty();
        }
        return Optional.of(FrontEndService.class.getResourceAsStream(fullResourcePath));
    }

    private static Map<String, String> readHashFilesMap() {
        URL hashFileUrl = DefaultFrontEndService.class.getClassLoader().getResource(HASH_PATH);
        if (hashFileUrl != null) {
            try {
                return (Map)OBJECT_MAPPER.readValue(hashFileUrl, MAP_TYPE_REFERENCE);
            }
            catch (IOException e) {
                log.error("Failed to read {} class path resource", (Object)HASH_PATH, (Object)e);
            }
        }
        return Collections.emptyMap();
    }
}

