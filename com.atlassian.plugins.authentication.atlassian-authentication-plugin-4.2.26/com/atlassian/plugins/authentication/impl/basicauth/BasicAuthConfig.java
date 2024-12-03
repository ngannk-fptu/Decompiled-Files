/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 */
package com.atlassian.plugins.authentication.impl.basicauth;

import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class BasicAuthConfig {
    public static final BasicAuthConfig DEFAULT = new BasicAuthConfig(false, Collections.emptyList(), Collections.emptyList(), true);
    private final boolean blockRequests;
    private final Set<String> allowedPaths;
    private final Set<String> allowedUsers;
    private final boolean showWarningMessage;

    public BasicAuthConfig(boolean blockRequests, Iterable<String> allowedPaths, Iterable<String> allowedUsers, boolean showWarningMessage) {
        this.blockRequests = blockRequests;
        this.allowedPaths = allowedPaths != null ? ImmutableSet.copyOf(allowedPaths) : ImmutableSet.of();
        this.allowedUsers = allowedUsers != null ? ImmutableSet.copyOf(allowedUsers) : ImmutableSet.of();
        this.showWarningMessage = showWarningMessage;
    }

    public boolean isBlockRequests() {
        return this.blockRequests;
    }

    public Set<String> getAllowedPaths() {
        return this.allowedPaths;
    }

    public Set<String> getAllowedUsers() {
        return this.allowedUsers;
    }

    public boolean isShowWarningMessage() {
        return this.showWarningMessage;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BasicAuthConfig)) {
            return false;
        }
        BasicAuthConfig that = (BasicAuthConfig)o;
        return this.blockRequests == that.blockRequests && this.showWarningMessage == that.showWarningMessage && Objects.equals(this.allowedPaths, that.allowedPaths) && Objects.equals(this.allowedUsers, that.allowedUsers);
    }

    public int hashCode() {
        return Objects.hash(this.blockRequests, this.allowedPaths, this.allowedUsers, this.showWarningMessage);
    }

    public String toString() {
        return new ToStringBuilder((Object)this, ToStringStyle.SHORT_PREFIX_STYLE).append("blockRequests", this.blockRequests).append("allowedPaths", this.allowedPaths).append("allowedUsers", this.allowedUsers).append("showWarningMessage", this.showWarningMessage).toString();
    }
}

