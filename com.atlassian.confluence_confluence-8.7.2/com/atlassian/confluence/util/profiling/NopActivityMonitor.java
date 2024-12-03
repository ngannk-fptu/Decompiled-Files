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
import com.atlassian.confluence.util.profiling.ActivityMonitor;
import com.atlassian.confluence.util.profiling.ActivitySnapshot;
import java.util.Collection;
import java.util.Collections;
import org.checkerframework.checker.nullness.qual.NonNull;

@ParametersAreNonnullByDefault
@Internal
public class NopActivityMonitor
implements ActivityMonitor {
    public static final NopActivityMonitor INSTANCE = new NopActivityMonitor();

    private NopActivityMonitor() {
    }

    @Override
    public @NonNull Activity registerStart(String userId, String type, String summary) {
        return NopActivity.INSTANCE;
    }

    @Override
    public @NonNull Collection<ActivitySnapshot> snapshotCurrent() {
        return Collections.emptyList();
    }

    private static class NopActivity
    implements Activity {
        private static final NopActivity INSTANCE = new NopActivity();

        private NopActivity() {
        }

        @Override
        public void close() {
        }
    }
}

