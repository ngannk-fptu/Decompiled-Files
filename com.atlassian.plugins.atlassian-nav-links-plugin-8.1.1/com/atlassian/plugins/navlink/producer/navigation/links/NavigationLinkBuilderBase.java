/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 */
package com.atlassian.plugins.navlink.producer.navigation.links;

import com.atlassian.plugins.navlink.producer.capabilities.services.ApplicationTypeService;
import com.atlassian.plugins.navlink.producer.navigation.links.LinkSource;
import com.atlassian.plugins.navlink.producer.navigation.links.NavigationLinkBase;
import com.atlassian.plugins.navlink.util.date.JodaDateToJavaTimeUtil;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import org.joda.time.DateTime;

public abstract class NavigationLinkBuilderBase<B extends NavigationLinkBuilderBase<B, NL>, NL extends NavigationLinkBase> {
    private final Class<B> builderClass;
    protected String key;
    protected String href;
    protected String baseUrl;
    protected String iconUrl;
    protected LinkSource source = LinkSource.unknown();
    protected int weight = Integer.MAX_VALUE;
    protected boolean self = false;
    protected String applicationType = ApplicationTypeService.DEFAULT_APPLICATION_TYPE;
    protected ZonedDateTime buildDateTime = Instant.ofEpochSecond(0L).atZone(ZoneId.systemDefault());

    protected NavigationLinkBuilderBase(Class<B> bClass) {
        this.builderClass = Objects.requireNonNull(bClass);
    }

    public final B copy(NavigationLinkBase that) {
        return ((NavigationLinkBuilderBase)((NavigationLinkBuilderBase)((NavigationLinkBuilderBase)((NavigationLinkBuilderBase)((NavigationLinkBuilderBase)((NavigationLinkBuilderBase)((NavigationLinkBuilderBase)((NavigationLinkBuilderBase)this.key(that.getKey())).href(that.getHref())).baseUrl(that.getBaseUrl())).iconUrl(that.getIconUrl())).applicationType(that.getApplicationType())).buildDateTime(that.getBuildDateTime())).source(that.getSource())).weight(that.weight())).self(that.isSelf());
    }

    public final B key(String key) {
        this.key = key;
        return this.asTargetInstance();
    }

    public final B href(String href) {
        this.href = href;
        return this.asTargetInstance();
    }

    public final B baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this.asTargetInstance();
    }

    public final B iconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
        return this.asTargetInstance();
    }

    public final B applicationType(String applicationType) {
        this.applicationType = applicationType;
        return this.asTargetInstance();
    }

    public final B source(LinkSource source) {
        this.source = source;
        return this.asTargetInstance();
    }

    public final B weight(int weight) {
        this.weight = weight;
        return this.asTargetInstance();
    }

    public final B self(boolean self) {
        this.self = self;
        return this.asTargetInstance();
    }

    @Deprecated
    public final B buildDate(DateTime buildDate) {
        this.buildDateTime = buildDate != null ? JodaDateToJavaTimeUtil.jodaToJavaTime(buildDate) : null;
        return this.asTargetInstance();
    }

    public final B buildDateTime(ZonedDateTime buildDateTime) {
        this.buildDateTime = buildDateTime;
        return this.asTargetInstance();
    }

    protected final B asTargetInstance() {
        return (B)((NavigationLinkBuilderBase)this.builderClass.cast(this));
    }
}

