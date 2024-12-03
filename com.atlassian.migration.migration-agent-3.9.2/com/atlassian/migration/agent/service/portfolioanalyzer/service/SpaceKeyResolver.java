/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ResponseStatusException
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.reflect.TypeToken
 *  com.google.gson.Gson
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.portfolioanalyzer.service;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.migration.agent.service.portfolioanalyzer.service.WarnLogFileWriter;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseStatusException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceKeyResolver {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(SpaceKeyResolver.class);
    private static final int PAGE_IDS_RESOLUTION_PAGE_SIZE = 500;
    private final ApplicationLinkService applicationLinkService;
    private final WarnLogFileWriter warnLogFileWriter;
    private final Gson gson = new Gson();

    public SpaceKeyResolver(ApplicationLinkService applicationLinkService, WarnLogFileWriter warnLogFileWriter) {
        this.applicationLinkService = applicationLinkService;
        this.warnLogFileWriter = warnLogFileWriter;
    }

    public Map<Long, String> fetchSpaceKeysForPageIds(URI confluenceUrl, Set<Long> pageIds) {
        Optional<ApplicationLink> applink = this.getApplink(confluenceUrl);
        if (!applink.isPresent()) {
            this.warnLogFileWriter.writeError(String.format("Failed to find appLink for Confluence URL: `%s` ", confluenceUrl));
            return Collections.emptyMap();
        }
        List pages = Lists.partition((List)ImmutableList.copyOf(pageIds), (int)500);
        return pages.stream().flatMap(pageIdsPage -> this.doFetchSpaceKeys((ApplicationLink)applink.get(), (List<Long>)pageIdsPage).entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<Long, String> doFetchSpaceKeys(ApplicationLink applink, List<Long> pageIds) {
        Optional<ApplicationLinkRequest> optionalRequest = this.createApplinkRequest(applink);
        if (!optionalRequest.isPresent()) {
            return Collections.emptyMap();
        }
        ApplicationLinkRequest request = optionalRequest.get();
        request.addHeader("X-Atlassian-Token", "no-check");
        request.addHeader("Content-Type", "application/json");
        request.setRequestBody(this.gson.toJson(pageIds));
        Optional<String> execute = this.execute(request, applink.getDisplayUrl().toASCIIString());
        if (!execute.isPresent()) {
            return Collections.emptyMap();
        }
        return (Map)this.gson.fromJson(execute.get(), new TypeToken<Map<Long, String>>(){}.getType());
    }

    private Optional<ApplicationLinkRequest> createApplinkRequest(ApplicationLink applink) {
        try {
            ApplicationLinkRequest request = applink.createAuthenticatedRequestFactory().createRequest(Request.MethodType.POST, "/rest/migration/latest/spaces/resolver/pages");
            return Optional.of(request);
        }
        catch (CredentialsRequiredException e) {
            this.warnLogFileWriter.writeError(String.format("AppLink to '%s' requires authorization. Please open the following link in your browser for authorization: '%s' ", applink.getDisplayUrl(), e.getAuthorisationURI()));
            return Optional.empty();
        }
        catch (Exception e) {
            log.warn("Could not resolve pageIds to space keys due to unhandled error", (Throwable)e);
            this.warnLogFileWriter.writeError(String.format("Could not resolve pageIds to space keys for base URL '%s', due to unhandled error: %s", applink.getDisplayUrl(), e.getMessage()));
            return Optional.empty();
        }
    }

    private Optional<String> execute(ApplicationLinkRequest request, String remoteConfluenceBaseUrl) {
        String remoteConfluenceNotFoundMessage = String.format("Failure to communicate with remote Confluence instance: '%s' . Please check if the instance is available and reachable.", remoteConfluenceBaseUrl);
        try {
            String execute = request.execute();
            return Optional.of(execute);
        }
        catch (ResponseStatusException e) {
            if (e.getResponse().getStatusCode() == 401 || e.getResponse().getStatusCode() == 403) {
                String message = String.format("Insufficient Permissions: The user either does not exist or lacks administrative privileges in the Confluence instance at '%s'. For full relations analysis, the user must hold admin privileges in all Confluence instances linked to this Confluence.", remoteConfluenceBaseUrl);
                this.warnLogFileWriter.writeError(message);
            } else if (e.getResponse().getStatusCode() == 404) {
                String message = String.format("No resolve endpoint found on remote Confluence instance. Please upgrade the CCMA (Confluence Cloud Migration Assistant) to the newest version on the instance: '%s' ", remoteConfluenceBaseUrl);
                this.warnLogFileWriter.writeError(message);
            }
            this.warnLogFileWriter.writeError(remoteConfluenceNotFoundMessage);
            return Optional.empty();
        }
        catch (ResponseException e) {
            this.warnLogFileWriter.writeError(remoteConfluenceNotFoundMessage);
            return Optional.empty();
        }
    }

    private Optional<ApplicationLink> getApplink(URI confluenceUrl) {
        return StreamSupport.stream(this.applicationLinkService.getApplicationLinks().spliterator(), false).filter(applink -> applink.getDisplayUrl().equals(confluenceUrl)).findFirst();
    }
}

