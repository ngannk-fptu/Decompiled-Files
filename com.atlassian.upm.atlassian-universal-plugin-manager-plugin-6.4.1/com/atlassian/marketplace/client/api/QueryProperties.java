/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.collect.Iterables
 *  io.atlassian.fugue.Option
 */
package com.atlassian.marketplace.client.api;

import com.atlassian.marketplace.client.api.ApplicationKey;
import com.atlassian.marketplace.client.api.EnumWithKey;
import com.atlassian.marketplace.client.api.HostingType;
import com.atlassian.marketplace.client.api.QueryBounds;
import com.atlassian.marketplace.client.util.Convert;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import io.atlassian.fugue.Option;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public abstract class QueryProperties {
    private QueryProperties() {
    }

    static String describeParams(String className, Iterable<String> ... paramLists) {
        return className + "(" + Joiner.on((String)", ").join(Iterables.concat((Iterable[])paramLists)) + ")";
    }

    static Iterable<String> describeOptBoolean(String name, boolean value) {
        return Convert.iterableOf(value ? Optional.of(name + "(true)") : Optional.empty());
    }

    static <T extends EnumWithKey> Iterable<String> describeOptEnum(String name, Option<T> value) {
        Iterator iterator = value.iterator();
        if (iterator.hasNext()) {
            EnumWithKey v = (EnumWithKey)iterator.next();
            return Option.some((Object)(name + "(" + v.getKey() + ")"));
        }
        return Option.none();
    }

    static <T extends EnumWithKey> Iterable<String> describeOptEnum(String name, Iterable<T> value) {
        Iterator<T> iterator = value.iterator();
        if (iterator.hasNext()) {
            EnumWithKey v = (EnumWithKey)iterator.next();
            return Option.some((Object)(name + "(" + v.getKey() + ")"));
        }
        return Option.none();
    }

    static Iterable<String> describeValues(String name, Iterable<?> values) {
        if (!Iterables.isEmpty(values)) {
            return Option.some((Object)(name + "(" + Joiner.on((String)",").join(values) + ")"));
        }
        return Option.none();
    }

    public static interface WithVersion {
        public boolean isWithVersion();
    }

    public static interface IncludePrivate {
        public boolean isIncludePrivate();
    }

    public static interface MultiHosting {
        public List<HostingType> getHostings();
    }

    public static interface Hosting {
        public Optional<HostingType> safeGetHosting();
    }

    public static interface Cost {
        public Optional<com.atlassian.marketplace.client.api.Cost> safeGetCost();
    }

    public static interface Bounds {
        public QueryBounds getBounds();
    }

    public static interface ApplicationCriteria {
        public Optional<ApplicationKey> safeGetApplication();

        public Optional<Integer> safeGetAppBuildNumber();
    }

    public static interface AccessToken {
        public Optional<String> safeGetAccessToken();
    }
}

