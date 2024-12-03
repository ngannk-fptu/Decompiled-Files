/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.quartz.SchedulerContext
 *  org.springframework.beans.factory.Aware
 */
package org.springframework.scheduling.quartz;

import org.quartz.SchedulerContext;
import org.springframework.beans.factory.Aware;

public interface SchedulerContextAware
extends Aware {
    public void setSchedulerContext(SchedulerContext var1);
}

