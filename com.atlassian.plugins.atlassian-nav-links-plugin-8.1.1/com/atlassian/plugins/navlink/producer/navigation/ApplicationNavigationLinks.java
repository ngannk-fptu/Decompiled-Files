/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.plugins.navlink.producer.navigation;

import com.atlassian.plugins.navlink.producer.navigation.NavigationLink;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class ApplicationNavigationLinks {
    private final Locale locale;
    private final Set<NavigationLink> navigationLinks;

    public ApplicationNavigationLinks(Locale locale, Set<NavigationLink> navigationLinks) {
        this.locale = Objects.requireNonNull(locale, "locale");
        this.navigationLinks = Collections.unmodifiableSet(new HashSet(Objects.requireNonNull(navigationLinks, "navigationLinks")));
    }

    public Locale getLocale() {
        return this.locale;
    }

    @Deprecated
    public ImmutableSet<NavigationLink> getNavigationLinks() {
        return ImmutableSet.copyOf(this.navigationLinks);
    }

    public Set<NavigationLink> getAllNavigationLinks() {
        return this.navigationLinks;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ApplicationNavigationLinks that = (ApplicationNavigationLinks)o;
        return this.locale.equals(that.locale) && this.navigationLinks.equals(that.navigationLinks);
    }

    public int hashCode() {
        return Objects.hash(this.locale, this.navigationLinks);
    }

    public String toString() {
        return "ApplicationNavigationLinks{locale=" + this.locale + ", navigationLinks=" + this.navigationLinks + '}';
    }
}

