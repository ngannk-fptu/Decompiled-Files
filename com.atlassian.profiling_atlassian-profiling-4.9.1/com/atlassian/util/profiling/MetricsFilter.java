/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.util.profiling;

import com.atlassian.annotations.Internal;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@FunctionalInterface
@Internal
public interface MetricsFilter {
    public static final MetricsFilter ACCEPT_ALL = name -> true;
    public static final MetricsFilter DENY_ALL = name -> false;

    public static MetricsFilter deny(String ... names) {
        return MetricsFilter.deny(Arrays.stream(names).collect(Collectors.toSet()));
    }

    public static MetricsFilter deny(Collection<String> names) {
        return name -> !names.contains(name);
    }

    public boolean accepts(String var1);
}

