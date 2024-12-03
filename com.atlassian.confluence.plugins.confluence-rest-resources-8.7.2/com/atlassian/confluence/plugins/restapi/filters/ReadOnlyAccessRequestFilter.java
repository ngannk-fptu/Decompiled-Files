/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.api.service.exceptions.ReadOnlyException
 *  com.atlassian.confluence.api.service.settings.SettingsService
 *  com.atlassian.core.util.ClassLoaderUtils
 *  com.google.common.collect.Lists
 *  com.sun.jersey.spi.container.ContainerRequest
 *  com.sun.jersey.spi.container.ContainerRequestFilter
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.restapi.filters;

import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.api.service.exceptions.ReadOnlyException;
import com.atlassian.confluence.api.service.settings.SettingsService;
import com.atlassian.core.util.ClassLoaderUtils;
import com.google.common.collect.Lists;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadOnlyAccessRequestFilter
implements ContainerRequestFilter {
    private final AccessModeService accessModeService;
    private final SettingsService settingsService;
    private final boolean hasReadOnlyAccessBlockedAnnotation;
    private Optional<List<String>> whiteListCache = Optional.empty();
    private static final Logger log = LoggerFactory.getLogger(ReadOnlyAccessRequestFilter.class);
    private final List<String> whiteList = Lists.newArrayList((Object[])new String[]{"/backdoor/", "/webResources/", "/wrm/", "/analytics/", "/plugins/", "/whitelist/", "/applinks/", "/applinks-oauth/", "/hipchat/", "/nativemobile/", "/custom-apps/", "/troubleshooting/", "/nps/"});

    public ReadOnlyAccessRequestFilter(AccessModeService accessModeService, SettingsService settingsService, boolean hasReadOnlyAccessBlockedAnnotation) {
        this.accessModeService = accessModeService;
        this.settingsService = settingsService;
        this.hasReadOnlyAccessBlockedAnnotation = hasReadOnlyAccessBlockedAnnotation;
    }

    public ContainerRequest filter(ContainerRequest containerRequest) {
        if (!this.accessModeService.isReadOnlyAccessModeEnabled()) {
            return containerRequest;
        }
        if (this.hasReadOnlyAccessBlockedAnnotation) {
            throw new ReadOnlyException();
        }
        String baseUrl = this.settingsService.getGlobalSettings().getBaseUrl();
        String path = containerRequest.getRequestUri().toString().substring(baseUrl.length());
        if (this.isWhitelisted(path)) {
            return containerRequest;
        }
        if (this.isMutativeMethod(containerRequest.getMethod())) {
            throw new ReadOnlyException();
        }
        return containerRequest;
    }

    private boolean isMutativeMethod(String method) {
        return StringUtils.equalsAny((CharSequence)method, (CharSequence[])new CharSequence[]{"POST", "PUT", "DELETE"});
    }

    private boolean isWhitelisted(String path) {
        return StringUtils.containsAny((CharSequence)path, (CharSequence[])this.getReadOnlyRestWhitelist());
    }

    private String[] getReadOnlyRestWhitelist() {
        if (!this.whiteListCache.isPresent()) {
            URL whitelist = ClassLoaderUtils.getResource((String)"read-only-rest-whitelist.txt", AccessModeService.class);
            if (whitelist != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(whitelist.openStream(), StandardCharsets.UTF_8));){
                    reader.lines().forEach(this.whiteList::add);
                }
                catch (Exception e) {
                    log.error("An error has occurred while reading read-only-rest-whitelist.txt");
                    log.debug("", (Throwable)e);
                }
            }
            this.whiteListCache = Optional.of(this.whiteList);
        }
        return this.whiteListCache.get().toArray(new String[0]);
    }
}

