/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.HelpPath
 *  com.atlassian.sal.api.message.HelpPathResolver
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Maps
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.osgi.framework.Version
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.internal.common.docs;

import com.atlassian.applinks.core.AppLinkPluginUtil;
import com.atlassian.applinks.internal.common.docs.DocumentationLinker;
import com.atlassian.applinks.internal.common.net.Uris;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.HelpPath;
import com.atlassian.sal.api.message.HelpPathResolver;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.Version;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultDocumentationLinker
implements DocumentationLinker {
    public static final String HELP_PATH_RESOURCE = "com/atlassian/applinks/ual-help-paths.properties";
    private final Properties helpPathProperties;
    private final URI documentationBaseUrl;
    private final HelpPathResolver helpPathResolver;
    private final ApplicationProperties applicationProperties;

    @Autowired
    public DefaultDocumentationLinker(AppLinkPluginUtil applinkPluginUtil, HelpPathResolver helpPathResolver, ApplicationProperties applicationProperties) {
        this(applinkPluginUtil, HELP_PATH_RESOURCE, helpPathResolver, applicationProperties);
    }

    @VisibleForTesting
    DefaultDocumentationLinker(AppLinkPluginUtil applinkPluginUtil, String helpPathsResource, HelpPathResolver helpPathResolver, ApplicationProperties applicationProperties) {
        this.documentationBaseUrl = this.createDocumentationBaseUrl(applinkPluginUtil);
        this.helpPathProperties = this.loadHelpPaths(helpPathsResource);
        this.helpPathResolver = helpPathResolver;
        this.applicationProperties = applicationProperties;
    }

    @Override
    @Nonnull
    public URI getLink(String pageKey) {
        return this.getLink(pageKey, null);
    }

    @Override
    @Nonnull
    public URI getLink(String pageKey, String sectionKey) {
        String pageName = this.helpPathProperties.getProperty(pageKey);
        if (!StringUtils.isEmpty((CharSequence)sectionKey)) {
            String sectionPrefix = StringUtils.remove((String)pageName, (String)"+");
            pageName = pageName + "#" + sectionPrefix + "-" + sectionKey;
        }
        return Uris.uncheckedConcatenate(this.getDocumentationBaseUrl(), "/" + pageName);
    }

    @Override
    @Nonnull
    public String getOAuth2HelpLink(String pageKey) {
        String product = this.applicationProperties.getDisplayName();
        HelpPath helpPath = this.helpPathResolver.getHelpPath(pageKey + "." + product.toLowerCase());
        if (helpPath == null) {
            return "";
        }
        return StringUtils.defaultString((String)this.setJiraHelplink(helpPath.getUrl()));
    }

    private String setJiraHelplink(String url) {
        if (this.applicationProperties.getPlatformId().equals("jira")) {
            if (url.contains("/jira/jcore")) {
                return url.replace("jcore", "jadm");
            }
            if (url.contains("/jira/jsw")) {
                return url.replace("jsw", "jadm");
            }
            if (url.contains("/jira/jsd")) {
                return url.replace("jsd", "jadm");
            }
            return url;
        }
        return url;
    }

    @Override
    @Nonnull
    public URI getDocumentationBaseUrl() {
        return this.documentationBaseUrl;
    }

    @Override
    @Nonnull
    public Map<String, String> getAllLinkMappings() {
        return Maps.fromProperties((Properties)this.helpPathProperties);
    }

    private Properties loadHelpPaths(String helpPathsResource) {
        Properties props = new Properties();
        InputStream propertiesFile = this.getClass().getClassLoader().getResourceAsStream(helpPathsResource);
        Objects.requireNonNull(propertiesFile, "Could not find help paths at: " + helpPathsResource);
        try {
            props.load(propertiesFile);
        }
        catch (IOException e) {
            throw new RuntimeException("Could not find help paths at: " + helpPathsResource, e);
        }
        return props;
    }

    private URI createDocumentationBaseUrl(AppLinkPluginUtil applinkPluginUtil) {
        Version version = applinkPluginUtil.getVersion();
        String documentationSpaceKey = String.format("APPLINKS-%02d%d/", version.getMajor(), version.getMinor());
        return URI.create("https://confluence.atlassian.com/display/" + documentationSpaceKey);
    }
}

