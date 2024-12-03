/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.system.task;

import com.atlassian.confluence.impl.system.task.SystemMaintenanceTask;

public interface SystemMaintenanceTaskMarshalling {
    public String marshal(SystemMaintenanceTask var1);

    public <T extends SystemMaintenanceTask> T unmarshal(Class<T> var1, String var2);
}

