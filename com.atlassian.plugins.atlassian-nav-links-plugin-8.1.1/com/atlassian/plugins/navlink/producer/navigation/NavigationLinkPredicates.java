/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  org.apache.commons.text.StringEscapeUtils
 */
package com.atlassian.plugins.navlink.producer.navigation;

import com.atlassian.plugins.custom_apps.api.CustomApp;
import com.atlassian.plugins.navlink.producer.navigation.NavigationLink;
import com.google.common.base.Predicate;
import org.apache.commons.text.StringEscapeUtils;

public class NavigationLinkPredicates {
    @Deprecated
    public static Predicate<NavigationLink> keyEquals(String keyToMatch) {
        return new KeyEquals(keyToMatch);
    }

    public static java.util.function.Predicate<NavigationLink> equalsKey(String keyToMatch) {
        return link -> link.getKey().equals(keyToMatch);
    }

    @Deprecated
    public static Predicate<NavigationLink> matchesCustomApp(CustomApp customApp) {
        return new MatchesCustomApp(customApp);
    }

    public static java.util.function.Predicate<NavigationLink> filterCustomApp(CustomApp customApp) {
        return navLink -> StringEscapeUtils.unescapeHtml4((String)navLink.getLabel()).equals(customApp.getDisplayName()) && navLink.getHref().equals(customApp.getUrl());
    }

    private static class MatchesCustomApp
    implements Predicate<NavigationLink> {
        private final CustomApp customApp;

        private MatchesCustomApp(CustomApp customApp) {
            this.customApp = customApp;
        }

        public boolean apply(NavigationLink navLink) {
            return StringEscapeUtils.unescapeHtml4((String)navLink.getLabel()).equals(this.customApp.getDisplayName()) && navLink.getHref().equals(this.customApp.getUrl());
        }
    }

    private static class KeyEquals
    implements Predicate<NavigationLink> {
        private final String keyToMatch;

        public KeyEquals(String keyToMatch) {
            this.keyToMatch = keyToMatch;
        }

        public boolean apply(NavigationLink link) {
            return link.getKey().equals(this.keyToMatch);
        }
    }
}

