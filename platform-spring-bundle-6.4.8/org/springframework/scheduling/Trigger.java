/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scheduling;

import java.util.Date;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TriggerContext;

public interface Trigger {
    @Nullable
    public Date nextExecutionTime(TriggerContext var1);
}

