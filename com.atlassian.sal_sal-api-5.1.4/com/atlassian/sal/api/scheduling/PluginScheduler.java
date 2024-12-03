/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.scheduling;

import com.atlassian.sal.api.scheduling.PluginJob;
import java.util.Date;
import java.util.Map;

@Deprecated
public interface PluginScheduler {
    public void scheduleJob(String var1, Class<? extends PluginJob> var2, Map<String, Object> var3, Date var4, long var5);

    public void unscheduleJob(String var1);
}

