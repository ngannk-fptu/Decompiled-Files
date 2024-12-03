/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugins.rest.module;

import com.atlassian.plugins.rest.module.ApiVersion;
import com.sun.jersey.api.core.DefaultResourceConfig;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.lang3.StringUtils;

public class RestApiContext {
    public static final String SLASH = "/";
    public static final String LATEST = "/latest";
    public static final String ANY_PATH_PATTERN = "/*";
    private final String restContext;
    private final String apiPath;
    private final ApiVersion version;
    private final Set<String> packages;
    private final boolean indexBundledJars;
    private final AtomicReference<DefaultResourceConfig> osgiResourceConfig = new AtomicReference();

    public RestApiContext(String restContext, String apiContext, ApiVersion version, Set<String> packages, boolean indexBundledJars) {
        this.restContext = this.prependSlash(Objects.requireNonNull(restContext, "restContext can't be null"));
        this.apiPath = this.prependSlash(Objects.requireNonNull(apiContext, "apiContext can't be null"));
        this.version = Objects.requireNonNull(version, "version can't be null");
        this.packages = Objects.requireNonNull(packages, "packages can't be null");
        this.indexBundledJars = indexBundledJars;
    }

    public String getRestContext() {
        return this.restContext;
    }

    public String getApiPath() {
        return this.apiPath;
    }

    public ApiVersion getVersion() {
        return this.version;
    }

    public String getPathToVersion() {
        return this.getPathToVersion(this.version);
    }

    public String getPathToLatest() {
        return this.getPathToVersion(LATEST);
    }

    public String getPathToVersion(String version) {
        return this.restContext + this.getContextlessPathToVersion(version);
    }

    private String getPathToVersion(ApiVersion version) {
        return this.restContext + this.getContextlessPathToVersion(version);
    }

    public String getContextlessPathToVersion() {
        return this.getContextlessPathToVersion(this.version);
    }

    private String getContextlessPathToVersion(String version) {
        return ApiVersion.isNone(version) ? this.apiPath : this.apiPath + this.prependSlash(version);
    }

    private String getContextlessPathToVersion(ApiVersion version) {
        return version.isNone() ? this.apiPath : this.apiPath + this.prependSlash(version.toString());
    }

    private String prependSlash(String path) {
        return StringUtils.startsWith((CharSequence)path, (CharSequence)SLASH) ? path : SLASH + path;
    }

    public Set<String> getPackages() {
        return this.packages;
    }

    public void setConfig(DefaultResourceConfig config) {
        this.osgiResourceConfig.set(config);
    }

    public Optional<DefaultResourceConfig> getConfig() {
        return this.osgiResourceConfig.get() != null ? Optional.of(this.osgiResourceConfig.get()) : Optional.empty();
    }

    public void disabled() {
        this.osgiResourceConfig.set(null);
    }

    public Boolean getIndexBundledJars() {
        return this.indexBundledJars;
    }
}

