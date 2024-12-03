/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scheduling;

import java.util.Date;
import org.springframework.lang.Nullable;

public interface TriggerContext {
    @Nullable
    public Date lastScheduledExecutionTime();

    @Nullable
    public Date lastActualExecutionTime();

    @Nullable
    public Date lastCompletionTime();
}

