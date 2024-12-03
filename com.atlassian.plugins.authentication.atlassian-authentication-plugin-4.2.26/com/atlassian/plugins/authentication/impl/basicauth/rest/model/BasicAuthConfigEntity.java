/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugins.authentication.impl.basicauth.rest.model;

import com.atlassian.plugins.authentication.impl.basicauth.BasicAuthConfig;
import com.atlassian.plugins.authentication.impl.basicauth.util.BasicAuthMatcherUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class BasicAuthConfigEntity {
    @JsonProperty(value="block-requests")
    private Boolean blockRequests;
    @JsonProperty(value="allowed-paths")
    private List<String> allowedPaths;
    @JsonProperty(value="allowed-users")
    private List<String> allowedUsers;
    @JsonProperty(value="show-warning-message")
    private Boolean showWarningMessage;

    @JsonCreator
    public BasicAuthConfigEntity(@Nullable @JsonProperty(value="block-requests") Boolean blockRequests, @Nullable @JsonProperty(value="allowed-paths") List<String> allowedPaths, @Nullable @JsonProperty(value="allowed-users") List<String> allowedUsers, @Nullable @JsonProperty(value="show-warning-message") Boolean showWarningMessage) {
        this.blockRequests = blockRequests;
        this.allowedPaths = allowedPaths;
        this.allowedUsers = allowedUsers;
        this.showWarningMessage = showWarningMessage;
    }

    @Nonnull
    public static BasicAuthConfigEntity fromConfig(@Nonnull BasicAuthConfig config) {
        return new BasicAuthConfigEntity(config.isBlockRequests(), new ArrayList<String>(config.getAllowedPaths()), new ArrayList<String>(config.getAllowedUsers()), config.isShowWarningMessage());
    }

    @Nonnull
    public BasicAuthConfig toConfig(@Nonnull BasicAuthConfig current) {
        return new BasicAuthConfig(Optional.ofNullable(this.blockRequests).orElseGet(current::isBlockRequests), Optional.ofNullable(this.allowedPaths).map(allowlist -> allowlist.stream().filter(StringUtils::isNotEmpty).map(BasicAuthMatcherUtils::normalizePathPattern).collect(Collectors.toList())).orElseGet(current::getAllowedPaths), Optional.ofNullable(this.allowedUsers).map(allowlist -> allowlist.stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList())).orElseGet(current::getAllowedUsers), Optional.ofNullable(this.showWarningMessage).orElseGet(current::isShowWarningMessage));
    }

    public Boolean getBlockRequests() {
        return this.blockRequests;
    }

    public void setBlockRequests(Boolean blockRequests) {
        this.blockRequests = blockRequests;
    }

    public List<String> getAllowedPaths() {
        return this.allowedPaths;
    }

    public void setAllowedPaths(List<String> allowedPaths) {
        this.allowedPaths = allowedPaths;
    }

    public List<String> getAllowedUsers() {
        return this.allowedUsers;
    }

    public void setAllowedUsers(List<String> allowedUsers) {
        this.allowedUsers = allowedUsers;
    }

    public Boolean getShowWarningMessage() {
        return this.showWarningMessage;
    }

    public void setShowWarningMessage(Boolean showWarningMessage) {
        this.showWarningMessage = showWarningMessage;
    }

    public static interface Config {
        public static final String BLOCK_REQUESTS = "block-requests";
        public static final String ALLOWED_PATHS = "allowed-paths";
        public static final String ALLOWED_USERS = "allowed-users";
        public static final String SHOW_WARNING_MESSAGE = "show-warning-message";
    }
}

