/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.google.common.base.Predicate
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugins.navlink.producer.navigation.services;

import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.plugins.navlink.producer.navigation.NavigationLink;
import com.atlassian.plugins.navlink.producer.navigation.NavigationLinkBuilder;
import com.atlassian.plugins.navlink.producer.navigation.NavigationLinks;
import com.atlassian.plugins.navlink.producer.navigation.services.LocalNavigationLinkService;
import com.atlassian.plugins.navlink.producer.navigation.services.LocalNavigationLinks;
import com.atlassian.plugins.navlink.producer.navigation.services.RawNavigationLink;
import com.atlassian.plugins.navlink.util.i18n.LocaleSupportingI18nResolverWorkAround;
import com.atlassian.plugins.navlink.util.url.UrlFactory;
import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class LocalNavigationLinkServiceImpl
implements LocalNavigationLinkService {
    private final LocalNavigationLinks localNavigationLinks;
    private final UrlFactory urlFactory;
    private final InternalHostApplication internalHostApplication;
    private final LocaleSupportingI18nResolverWorkAround i18nResolverWorkAround;

    public LocalNavigationLinkServiceImpl(LocalNavigationLinks localNavigationLinks, UrlFactory urlFactory, InternalHostApplication internalHostApplication, LocaleSupportingI18nResolverWorkAround i18nResolverWorkAround) {
        this.localNavigationLinks = localNavigationLinks;
        this.urlFactory = urlFactory;
        this.internalHostApplication = internalHostApplication;
        this.i18nResolverWorkAround = i18nResolverWorkAround;
    }

    @Override
    @Nonnull
    public Set<NavigationLink> all(@Nonnull Locale locale) {
        return this.localNavigationLinks.all().stream().filter(Objects::nonNull).map(this.toNavigationLinkEntity(locale)).collect(Collectors.toSet());
    }

    @Override
    @Deprecated
    @Nonnull
    public Set<NavigationLink> matching(@Nonnull Locale locale, @Nonnull com.google.common.base.Predicate<NavigationLink> criteria) {
        return this.matching(locale, (Predicate<NavigationLink>)criteria);
    }

    @Override
    @Nonnull
    public Set<NavigationLink> matching(@Nonnull Locale locale, @Nonnull Predicate<NavigationLink> criteria) {
        return this.all(locale).stream().filter(criteria).collect(Collectors.toSet());
    }

    @Nonnull
    private Function<? super RawNavigationLink, ? extends NavigationLink> toNavigationLinkEntity(@Nonnull Locale locale) {
        return localLink -> {
            if (localLink != null) {
                String href = this.urlFactory.toAbsoluteUrl(localLink.getHref());
                String label = this.i18nResolverWorkAround.getText(locale, localLink.getLabelKey(), new Serializable[]{this.internalHostApplication.getName()});
                String tooltip = this.i18nResolverWorkAround.getText(locale, localLink.getTooltipKey());
                String iconUrl = localLink.getIconUrl();
                String icon = iconUrl == null || iconUrl.isEmpty() ? null : this.urlFactory.toAbsoluteUrl(iconUrl);
                return ((NavigationLinkBuilder)((NavigationLinkBuilder)NavigationLinks.copyOf(localLink).href(href)).iconUrl(icon)).label(label).tooltip(tooltip).build();
            }
            return null;
        };
    }
}

