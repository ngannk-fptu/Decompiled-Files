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
import com.atlassian.confluence.util.profiling.Activity;
import com.atlassian.confluence.util.profiling.ActivitySnapshot;
import java.util.Collection;
import org.checkerframework.checker.nullness.qual.NonNull;

@ParametersAreNonnullByDefault
@Internal
public interface ActivityMonitor {
    public @NonNull Activity registerStart(String var1, String var2, String var3);

    public @NonNull Collection<ActivitySnapshot> snapshotCurrent();
}

