/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.joda.time.DateTime
 */
package com.atlassian.plugins.navlink.producer.navigation.links;

import com.atlassian.plugins.navlink.producer.capabilities.services.ApplicationTypeService;
import com.atlassian.plugins.navlink.producer.navigation.links.LinkSource;
import com.atlassian.plugins.navlink.producer.navigation.links.NavigationLinkBuilderBase;
import com.atlassian.plugins.navlink.util.date.JodaDateToJavaTimeUtil;
import com.google.common.base.Preconditions;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joda.time.DateTime;

public abstract class NavigationLinkBase {
    public static final String CUSTOM_APPS_KEY = "custom-apps";
    public static final String HOME_APPS_KEY = "home";
    public static final String PRODUCT_ENTITY_KEY = "nav-links.product-entity";
    private final String key;
    private final String href;
    private final String baseUrl;
    private final String iconUrl;
    private final LinkSource source;
    private final int weight;
    private final boolean self;
    private final String applicationType;
    private final ZonedDateTime buildDate;

    private NavigationLinkBase(@Nonnull String key, @Nonnull String href, String baseUrl, String iconUrl, String applicationType, LinkSource source, int weight, boolean self, ZonedDateTime buildDate) {
        this.key = (String)Preconditions.checkNotNull((Object)key, (Object)"key");
        this.href = (String)Preconditions.checkNotNull((Object)href, (Object)"href");
        this.baseUrl = baseUrl;
        this.iconUrl = iconUrl;
        this.applicationType = applicationType != null ? applicationType : ApplicationTypeService.DEFAULT_APPLICATION_TYPE;
        this.source = source != null ? source : LinkSource.unknown();
        this.weight = weight;
        this.self = self;
        this.buildDate = buildDate;
    }

    protected NavigationLinkBase(NavigationLinkBuilderBase<?, ?> builder) {
        this(builder.key, builder.href, builder.baseUrl, builder.iconUrl, builder.applicationType, builder.source, builder.weight, builder.self, builder.buildDateTime);
    }

    @Nonnull
    public final String getKey() {
        return this.key;
    }

    @Nonnull
    public final String getHref() {
        return this.href;
    }

    @Nullable
    public final String getBaseUrl() {
        return this.baseUrl;
    }

    @Nullable
    public final String getIconUrl() {
        return this.iconUrl;
    }

    @Nonnull
    public final String getApplicationType() {
        return this.applicationType;
    }

    @Deprecated
    @Nonnull
    public DateTime getBuildDate() {
        return JodaDateToJavaTimeUtil.javaTimeToJoda(this.buildDate);
    }

    @Nonnull
    public ZonedDateTime getBuildDateTime() {
        return this.buildDate;
    }

    @Nonnull
    public final LinkSource getSource() {
        return this.source;
    }

    public final int weight() {
        return this.weight;
    }

    public final boolean isSelf() {
        return this.self;
    }

    protected final boolean isEqualTo(NavigationLinkBase that) {
        return Objects.equals(this.key, that.key) && Objects.equals(this.href, that.href) && Objects.equals(this.baseUrl, that.baseUrl) && Objects.equals(this.iconUrl, that.iconUrl) && Objects.equals(this.applicationType, that.applicationType) && Objects.equals(this.source, that.source) && Objects.equals(this.weight, that.weight) && Objects.equals(this.self, that.self) && Objects.equals(this.buildDate, that.buildDate);
    }

    protected final int hashCodeBase() {
        return Objects.hash(this.key, this.href, this.baseUrl, this.iconUrl, this.applicationType, this.source, this.weight, this.self, this.buildDate);
    }

    protected final String toStringBase() {
        return "key='" + this.key + '\'' + ", href='" + this.href + '\'' + ", baseUrl='" + this.baseUrl + '\'' + ", iconUrl='" + this.iconUrl + '\'' + ", applicationType='" + this.applicationType + '\'' + ", source='" + this.source + '\'' + ", weight='" + this.weight + '\'' + ", self='" + this.self + '\'' + ", buildDate='" + this.buildDate + '\'';
    }
}

