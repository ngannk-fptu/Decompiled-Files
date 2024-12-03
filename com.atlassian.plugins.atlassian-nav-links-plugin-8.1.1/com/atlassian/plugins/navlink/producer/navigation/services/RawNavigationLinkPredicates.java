/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 */
package com.atlassian.plugins.navlink.producer.navigation.services;

import com.atlassian.plugins.navlink.producer.navigation.services.RawNavigationLink;
import com.google.common.base.Predicate;
import java.util.Objects;

public class RawNavigationLinkPredicates {
    @Deprecated
    public static Predicate<RawNavigationLink> keyEquals(String key) {
        return input -> RawNavigationLinkPredicates.equalsKey(key).test((RawNavigationLink)input);
    }

    public static java.util.function.Predicate<RawNavigationLink> equalsKey(String key) {
        Objects.requireNonNull(key, "key");
        return input -> key.equals(input.getKey());
    }
}

