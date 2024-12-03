/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.google.common.base.Supplier
 *  javax.annotation.Nonnull
 *  javax.servlet.http.HttpServletRequest
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.api.impl.sal;

import com.atlassian.confluence.cluster.ClusterConfigurationHelper;
import com.atlassian.confluence.setup.BuildInformation;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.web.context.HttpContext;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.google.common.base.Supplier;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ConfluenceApplicationProperties
implements ApplicationProperties {
    private final Option<SettingsManager> settingsManager;
    private final ClusterConfigurationHelper clusterConfigurationHelper;
    private final FilesystemPath sharedHome;
    private final FilesystemPath localHome;
    private final FilesystemPath confluenceHome;
    private final HttpContext httpContext;
    private final Supplier<String> CANONICAL_BASE_URL_SUPPLIER = this::getCanonicalBaseUrl;
    private final Supplier<String> CANONICAL_CONTEXT_PATH_SUPPLIER = this::getCanonicalContextPath;

    public ConfluenceApplicationProperties(SettingsManager settingsManager, ClusterConfigurationHelper clusterConfigurationHelper, HttpContext httpContext, FilesystemPath sharedHome, FilesystemPath localHome, FilesystemPath confluenceHome) {
        this.settingsManager = Option.option((Object)settingsManager);
        this.clusterConfigurationHelper = clusterConfigurationHelper;
        this.httpContext = httpContext;
        this.sharedHome = sharedHome;
        this.localHome = localHome;
        this.confluenceHome = confluenceHome;
    }

    public String getBaseUrl() {
        return this.getCanonicalBaseUrl();
    }

    public String getBaseUrl(UrlMode urlMode) {
        switch (urlMode) {
            case CANONICAL: {
                return this.getCanonicalBaseUrl();
            }
            case ABSOLUTE: {
                return (String)this.getBaseUrlFromRequest(this.httpContext.getRequest()).getOrElse(this.CANONICAL_BASE_URL_SUPPLIER);
            }
            case RELATIVE: {
                return (String)this.getContextPathFromRequest(this.httpContext.getRequest()).getOrElse(this.CANONICAL_CONTEXT_PATH_SUPPLIER);
            }
            case RELATIVE_CANONICAL: {
                return this.getCanonicalContextPath();
            }
            case AUTO: {
                return (String)this.getContextPathFromRequest(this.httpContext.getRequest()).getOrElse(this.CANONICAL_BASE_URL_SUPPLIER);
            }
        }
        throw new UnsupportedOperationException("Not implemented yet");
    }

    protected String getCanonicalBaseUrl() {
        if (this.settingsManager.isDefined()) {
            return ((SettingsManager)this.settingsManager.get()).getGlobalSettings().getBaseUrl();
        }
        try {
            return (String)this.getBaseUrlFromRequest(this.getHttpContext().getRequest()).get();
        }
        catch (NoSuchElementException e) {
            throw new IllegalStateException("Unable to determine Base URL from request", e);
        }
    }

    private String getCanonicalContextPath() {
        String baseUrl = this.getCanonicalBaseUrl();
        try {
            return new URL(baseUrl).getPath();
        }
        catch (MalformedURLException e) {
            throw new IllegalStateException("Base URL misconfigured: " + (String)(baseUrl == null ? "<null>" : "'" + baseUrl + "'"), e);
        }
    }

    protected Option<String> getBaseUrlFromRequest(HttpServletRequest request) {
        if (request != null) {
            return Option.some((Object)GeneralUtil.lookupDomainName(request));
        }
        return Option.none();
    }

    private Option<String> getContextPathFromRequest(HttpServletRequest request) {
        if (request != null) {
            return Option.some((Object)request.getContextPath());
        }
        return Option.none();
    }

    public String getDisplayName() {
        return "Confluence";
    }

    public String getVersion() {
        return GeneralUtil.getVersionNumber();
    }

    public Date getBuildDate() {
        return GeneralUtil.getBuildDate();
    }

    public String getBuildNumber() {
        return BuildInformation.INSTANCE.getMarketplaceBuildNumber();
    }

    public File getHomeDirectory() {
        return this.confluenceHome.asJavaFile();
    }

    @Nonnull
    public Optional<Path> getLocalHomeDirectory() {
        return Optional.of(this.localHome.asJavaPath());
    }

    @Nonnull
    public Optional<Path> getSharedHomeDirectory() {
        return this.clusterConfigurationHelper.isClusterHomeConfigured() ? Optional.of(this.sharedHome.asJavaPath()) : Optional.empty();
    }

    public String getPropertyValue(String key) {
        throw new UnsupportedOperationException("Confluence does not support retrieving generic property values");
    }

    public HttpContext getHttpContext() {
        return this.httpContext;
    }

    public String getApplicationName() {
        return this.getDisplayName();
    }

    public @NonNull String getPlatformId() {
        return "conf";
    }

    @Nonnull
    public String getApplicationFileEncoding() {
        return (String)this.settingsManager.map(SettingsManager::getGlobalSettings).map(Settings::getDefaultEncoding).filter(Objects::nonNull).getOrElse((Object)StandardCharsets.UTF_8.name());
    }
}

