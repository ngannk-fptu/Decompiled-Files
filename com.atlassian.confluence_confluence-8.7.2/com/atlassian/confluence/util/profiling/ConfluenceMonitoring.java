/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.util.profiling.ConfluenceMonitoringNameFactory;
import com.atlassian.confluence.util.profiling.Counter;
import com.atlassian.confluence.util.profiling.Split;
import java.util.Collections;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

@ParametersAreNonnullByDefault
@Internal
public interface ConfluenceMonitoring {
    @Deprecated
    public @NonNull Counter fetchCounter(String var1, String ... var2);

    default public @NonNull Counter fetchCounter(String name) {
        return this.fetchCounter(name, Collections.emptyMap());
    }

    public @NonNull Counter fetchCounter(String var1, Map<String, String> var2);

    @Deprecated
    public @NonNull Split startSplit(String var1, String ... var2);

    default public @NonNull Split startSplit(String name) {
        return this.startSplit(name, Collections.emptyMap());
    }

    public @NonNull Split startSplit(String var1, Map<String, String> var2);

    default public @NonNull String createName(String name, String ... optional) {
        return ConfluenceMonitoringNameFactory.createName(name, optional);
    }
}

