/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Ordering
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.streams.internal;

import com.atlassian.streams.internal.rest.representations.ProviderFilterRepresentation;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;
import java.util.Arrays;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public abstract class ProviderFilterOrdering
extends Ordering<ProviderFilterRepresentation> {
    private final Set<String> prioritized;
    private final String THIRD_PARTY = "thirdparty";

    private ProviderFilterOrdering(Iterable<String> prioritized) {
        this.prioritized = ImmutableSet.copyOf(prioritized);
    }

    public static Ordering<ProviderFilterRepresentation> prioritizing(String ... providerNames) {
        return ProviderFilterOrdering.prioritizing(Arrays.asList(providerNames));
    }

    public static Ordering<ProviderFilterRepresentation> prioritizing(Iterable<String> providerNames) {
        return new ProviderFilterOrdering((Iterable)providerNames){};
    }

    public int compare(ProviderFilterRepresentation p1, ProviderFilterRepresentation p2) {
        if (p1.getKey().equals("streams") || p2.getKey().equals("streams")) {
            if (p1.getKey().equals("streams")) {
                return -1;
            }
            if (p2.getKey().equals("streams")) {
                return 1;
            }
        }
        if (this.prioritized.contains(p1.getName()) && !this.prioritized.contains(p2.getName()) && StringUtils.isEmpty((CharSequence)p1.getApplinkName())) {
            return -1;
        }
        if (!this.prioritized.contains(p1.getName()) && this.prioritized.contains(p2.getName()) && StringUtils.isEmpty((CharSequence)p2.getApplinkName())) {
            return 1;
        }
        if (!p1.getApplinkName().equals(p2.getApplinkName())) {
            return p1.getApplinkName().compareToIgnoreCase(p2.getApplinkName());
        }
        if (p1.getKey().startsWith("thirdparty") && !p2.getKey().startsWith("thirdparty")) {
            return 1;
        }
        if (!p1.getKey().startsWith("thirdparty") && p2.getKey().startsWith("thirdparty")) {
            return -1;
        }
        return p1.getName().compareToIgnoreCase(p2.getName());
    }
}

