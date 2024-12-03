/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.plugins.navlink.consumer.menu.services;

import com.atlassian.plugins.navlink.producer.navigation.NavigationLink;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.List;

final class LegacyNavLinkComparator
implements Comparator<NavigationLink> {
    public static final String APPLICATION_TYPE_BAMBOO = "bamboo";
    public static final String APPLICATION_TYPE_BITBUCKET = "bitbucket";
    public static final String APPLICATION_TYPE_CONFLUENCE = "confluence";
    public static final String APPLICATION_TYPE_FECRU = "fecru";
    public static final String APPLICATION_TYPE_HIPCHAT = "hipchat";
    public static final String APPLICATION_TYPE_JIRA = "jira";
    public static final String APPLICATION_TYPE_STASH = "stash";
    static final LegacyNavLinkComparator INSTANCE = new LegacyNavLinkComparator();
    private List<String> orderedAppTypes = ImmutableList.of((Object)"jira", (Object)"confluence", (Object)"stash", (Object)"bitbucket", (Object)"bamboo", (Object)"fecru", (Object)"hipchat");

    LegacyNavLinkComparator() {
    }

    @Override
    public int compare(NavigationLink o1, NavigationLink o2) {
        if (o1 == o2) {
            return 0;
        }
        int comparison = this.compareAppTypes(o1.getApplicationType(), o2.getApplicationType());
        if (comparison == 0) {
            comparison = this.compareStrings(o1.getLabel(), o2.getLabel());
        }
        if (comparison == 0) {
            comparison = this.compareStrings(o1.getHref(), o2.getHref());
        }
        return comparison;
    }

    private int compareAppTypes(String applicationType1, String applicationType2) {
        int index1 = this.orderedAppTypes.indexOf(this.normalizeApplicationType(applicationType1));
        int index2 = this.orderedAppTypes.indexOf(this.normalizeApplicationType(applicationType2));
        if (index1 == -1 && index2 == -1) {
            if (applicationType1 != null && applicationType2 != null) {
                return this.compareStrings(applicationType1, applicationType2);
            }
            if (applicationType1 == null && applicationType2 == null) {
                return 0;
            }
            if (applicationType1 == null) {
                return 1;
            }
            if (applicationType2 == null) {
                return -1;
            }
        }
        if (index1 == -1) {
            return 1;
        }
        if (index2 == -1) {
            return -1;
        }
        if (index1 < index2) {
            return -1;
        }
        if (index1 > index2) {
            return 1;
        }
        return 0;
    }

    private String normalizeApplicationType(String applicationType) {
        return Strings.nullToEmpty((String)applicationType).replace("FishEye / Crucible", APPLICATION_TYPE_FECRU).toLowerCase();
    }

    private int compareStrings(String string1, String string2) {
        int i = string1.compareTo(string2);
        if (i == 0) {
            return 0;
        }
        return i < 0 ? -1 : 1;
    }
}

