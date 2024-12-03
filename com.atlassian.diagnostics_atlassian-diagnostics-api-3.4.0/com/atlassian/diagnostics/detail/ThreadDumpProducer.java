/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.diagnostics.detail;

import com.atlassian.diagnostics.detail.ThreadDump;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ThreadDumpProducer {
    @Nonnull
    public List<ThreadDump> produce(@Nonnull Set<Thread> var1);

    @Nullable
    public String toStackTraceString(List<StackTraceElement> var1);
}

