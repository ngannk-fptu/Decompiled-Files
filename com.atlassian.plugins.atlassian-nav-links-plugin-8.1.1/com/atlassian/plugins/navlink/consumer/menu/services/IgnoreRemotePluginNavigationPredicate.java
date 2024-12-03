/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.applinks.api.application.bamboo.BambooApplicationType
 *  com.atlassian.applinks.api.application.confluence.ConfluenceApplicationType
 *  com.atlassian.applinks.api.application.crowd.CrowdApplicationType
 *  com.atlassian.applinks.api.application.fecru.FishEyeCrucibleApplicationType
 *  com.atlassian.applinks.api.application.jira.JiraApplicationType
 *  com.atlassian.applinks.api.application.refapp.RefAppApplicationType
 *  com.atlassian.applinks.api.application.stash.StashApplicationType
 *  io.atlassian.fugue.Option
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 *  org.apache.commons.lang3.BooleanUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.navlink.consumer.menu.services;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.api.application.bamboo.BambooApplicationType;
import com.atlassian.applinks.api.application.confluence.ConfluenceApplicationType;
import com.atlassian.applinks.api.application.crowd.CrowdApplicationType;
import com.atlassian.applinks.api.application.fecru.FishEyeCrucibleApplicationType;
import com.atlassian.applinks.api.application.jira.JiraApplicationType;
import com.atlassian.applinks.api.application.refapp.RefAppApplicationType;
import com.atlassian.applinks.api.application.stash.StashApplicationType;
import io.atlassian.fugue.Option;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Immutable
public class IgnoreRemotePluginNavigationPredicate
implements Predicate<ReadOnlyApplicationLink> {
    public static final String STRICT_MODE_SYSTEM_PROPERTY = "navlink.applicationlinks.strictmode";
    private static final String MAGIC_PROPERTY = "IS_ACTIVITY_ITEM_PROVIDER";
    private static final Set<Class<? extends ApplicationType>> ATLASSIAN_PRODUCTS = Stream.of(BambooApplicationType.class, ConfluenceApplicationType.class, CrowdApplicationType.class, FishEyeCrucibleApplicationType.class, JiraApplicationType.class, RefAppApplicationType.class, StashApplicationType.class).collect(Collectors.toSet());
    private static final Logger logger = LoggerFactory.getLogger(IgnoreRemotePluginNavigationPredicate.class);
    private final boolean strictModeOn = BooleanUtils.toBoolean((String)System.getProperty("navlink.applicationlinks.strictmode", "true"));
    private final ApplicationLinkService applicationLinkService;

    public IgnoreRemotePluginNavigationPredicate(ApplicationLinkService applicationLinkService) {
        this.applicationLinkService = Objects.requireNonNull(applicationLinkService, "applicationLinkService");
    }

    @Override
    public boolean test(@Nullable ReadOnlyApplicationLink input) {
        if (input == null) {
            return false;
        }
        if (!this.isAtlassianProduct(input)) {
            boolean ignoreApplicationLinkToNonAtlassianProduct = this.strictModeOn || this.hasMagicPropertySetToFalse(input);
            logger.debug("Application link to non-Atlassian product found, it will be {} (strict mode {}, id {}).", new Object[]{ignoreApplicationLinkToNonAtlassianProduct ? "ignored" : "accepted", this.strictModeOn, input.getId()});
            return ignoreApplicationLinkToNonAtlassianProduct;
        }
        logger.debug("Application link to Atlassian product accepted (id {})", (Object)input.getId());
        return false;
    }

    private boolean isAtlassianProduct(ReadOnlyApplicationLink input) {
        ApplicationType type = input.getType();
        return type != null && ATLASSIAN_PRODUCTS.stream().anyMatch(product -> product.isAssignableFrom(type.getClass()));
    }

    private boolean hasMagicPropertySetToFalse(@Nonnull ReadOnlyApplicationLink readOnlyApplicationLink) {
        Option<ApplicationLink> applicationLink = this.getApplicationLink(readOnlyApplicationLink);
        if (applicationLink.isDefined()) {
            String propertyValue = this.getStringProperty((ApplicationLink)applicationLink.get(), MAGIC_PROPERTY);
            return propertyValue != null && propertyValue.equalsIgnoreCase(Boolean.FALSE.toString());
        }
        return false;
    }

    private Option<ApplicationLink> getApplicationLink(ReadOnlyApplicationLink applicationLink) {
        try {
            return Option.option((Object)this.applicationLinkService.getApplicationLink(applicationLink.getId()));
        }
        catch (TypeNotInstalledException e) {
            return Option.none();
        }
    }

    @Nullable
    private String getStringProperty(@Nonnull ApplicationLink applicationLink, @Nonnull String propertyKey) {
        Object value = applicationLink.getProperty(propertyKey);
        return value != null && value instanceof String ? (String)String.class.cast(value) : null;
    }
}

