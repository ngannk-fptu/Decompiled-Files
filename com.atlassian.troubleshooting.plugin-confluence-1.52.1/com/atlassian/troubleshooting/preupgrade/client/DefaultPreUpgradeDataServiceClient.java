/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.http.HttpEntity
 *  org.apache.http.client.methods.HttpGet
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.util.EntityUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.preupgrade.client;

import com.atlassian.troubleshooting.http.HttpClientFactory;
import com.atlassian.troubleshooting.preupgrade.PupDataResource;
import com.atlassian.troubleshooting.preupgrade.client.PreUpgradeDataServiceClient;
import com.atlassian.troubleshooting.preupgrade.model.SupportedPlatformQuery;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@ParametersAreNonnullByDefault
public class DefaultPreUpgradeDataServiceClient
implements PreUpgradeDataServiceClient {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultPreUpgradeDataServiceClient.class);
    private static final int DEFAULT_CONNECT_TIMEOUT_MS = 3000;
    private final HttpClientFactory httpClientRequestFactory;

    @Autowired
    public DefaultPreUpgradeDataServiceClient(HttpClientFactory httpClientRequestFactory) {
        this.httpClientRequestFactory = Objects.requireNonNull(httpClientRequestFactory);
    }

    @Override
    public Optional<String> findSupportedPlatformInfoJsonForQuery(SupportedPlatformQuery query) {
        if (!(query.getProduct().equals("jira") || query.getProduct().equals("conf") || query.getProduct().equals("bitbucket"))) {
            return Optional.empty();
        }
        try {
            URL pupDataUrl = PupDataResource.INSTANCE.getCachedUrl();
            String url = String.format("%s?product=%s&version=%s", pupDataUrl, query.getProduct(), URLEncoder.encode(query.getVersion(), "UTF-8"));
            return Optional.of(EntityUtils.toString((HttpEntity)this.httpClientRequestFactory.newHttpClient(3000).execute((HttpUriRequest)new HttpGet(url)).getEntity()));
        }
        catch (Exception e) {
            LOG.debug("Error retrieving pre upgrade data.", (Throwable)e);
            return Optional.empty();
        }
    }
}

