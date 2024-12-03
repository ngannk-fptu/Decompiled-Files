/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.navlink.consumer.menu.services;

import com.atlassian.plugins.navlink.consumer.menu.services.LegacyNavLinkComparator;
import com.atlassian.plugins.navlink.producer.navigation.NavigationLink;
import java.util.Comparator;

public class NavigationLinkComparator
implements Comparator<NavigationLink> {
    public static final NavigationLinkComparator INSTANCE = new NavigationLinkComparator();

    @Override
    public int compare(NavigationLink first, NavigationLink second) {
        if (first == second) {
            return 0;
        }
        if (this.weightsNotYetSupported(first, second)) {
            return LegacyNavLinkComparator.INSTANCE.compare(first, second);
        }
        int comparison = this.compareInts(first.weight(), second.weight());
        if (comparison == 0) {
            if (this.sameApplicationType(first, second)) {
                return this.compareSameApps(first, second);
            }
            return this.compareDifferentApps(first, second);
        }
        return comparison;
    }

    private boolean weightsNotYetSupported(NavigationLink one, NavigationLink two) {
        return one.weight() == Weights.UNSPECIFIED.value() || two.weight() == Weights.UNSPECIFIED.value();
    }

    private int compareInts(int one, int two) {
        return one < two ? -1 : (one == two ? 0 : 1);
    }

    private boolean sameApplicationType(NavigationLink first, NavigationLink second) {
        return first.getApplicationType().equals(second.getApplicationType());
    }

    private int compareSameApps(NavigationLink first, NavigationLink second) {
        int comparison = this.compareStrings(first.getLabel(), second.getLabel());
        if (comparison == 0) {
            comparison = this.compareStrings(first.getHref(), second.getHref());
        }
        return comparison;
    }

    private int compareDifferentApps(NavigationLink first, NavigationLink second) {
        int comparison = second.getBuildDateTime().compareTo(first.getBuildDateTime());
        if (comparison == 0) {
            comparison = this.compareStrings(first.getLabel(), second.getLabel());
        }
        if (comparison == 0) {
            comparison = this.compareStrings(first.getHref(), second.getHref());
        }
        return comparison;
    }

    private int compareStrings(String string1, String string2) {
        return String.CASE_INSENSITIVE_ORDER.compare(string1, string2);
    }

    public static enum Weights {
        UNSPECIFIED(Integer.MAX_VALUE),
        MAX(0x7FFFFFFE);

        private final int value;

        private Weights(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }
    }
}

