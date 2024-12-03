/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.homepage;

import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.HtmlUtil;
import com.google.common.annotations.VisibleForTesting;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Homepage {
    private final HomepageType type;
    private final Optional<Space> space;
    private final Optional<ConfluenceUser> user;

    static Homepage userProfileHomepage(ConfluenceUser user) {
        return new Homepage(HomepageType.USER_PROFILE, Optional.empty(), Optional.of(user));
    }

    static Homepage spaceHomepage(Space space) {
        return new Homepage(HomepageType.SPACE_HOME, Optional.of(space), Optional.empty());
    }

    static Homepage dashboardHomepage() {
        return new Homepage(HomepageType.DASHBOARD, Optional.empty(), Optional.empty());
    }

    private Homepage(HomepageType type, Optional<Space> space, Optional<ConfluenceUser> user) {
        this.type = Objects.requireNonNull(type);
        this.space = Objects.requireNonNull(space);
        this.user = Objects.requireNonNull(user);
    }

    @VisibleForTesting
    HomepageType getType() {
        return this.type;
    }

    @VisibleForTesting
    Optional<Space> getSpace() {
        return this.space;
    }

    @VisibleForTesting
    Optional<ConfluenceUser> getUser() {
        return this.user;
    }

    public @NonNull URI getDeepLinkUri() {
        switch (this.type) {
            case DASHBOARD: {
                return URI.create("/dashboard.action");
            }
            case USER_PROFILE: {
                return URI.create(String.format("/display/%s", HtmlUtil.urlEncode("~" + this.user.get().getName())));
            }
            case SPACE_HOME: {
                return this.space.get().getDeepLinkUri();
            }
        }
        throw new IllegalStateException("Don't know how to build a URI for " + this);
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Homepage homepage = (Homepage)o;
        return Objects.equals((Object)this.type, (Object)homepage.type) && Objects.equals(this.space, homepage.space) && Objects.equals(this.user, homepage.user);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.type, this.space, this.user});
    }

    public String toString() {
        return "Homepage{type=" + this.type + ", space=" + this.space + ", user=" + this.user + "}";
    }

    @VisibleForTesting
    static enum HomepageType {
        DASHBOARD,
        SPACE_HOME,
        USER_PROFILE;

    }
}

