/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.troubleshooting.stp.task;

import java.util.Arrays;
import java.util.Optional;
import org.apache.commons.lang3.Validate;

public enum TaskType {
    HERCULES("hercules"),
    HERCULES_REPORT("hercules-report"),
    SUPPORT_REQUEST("support-request"),
    SUPPORT_ZIP("support-zip");

    private final String key;

    private TaskType(String key) {
        Validate.notBlank((CharSequence)key, (String)"Invalid key '%s'", (Object[])new Object[]{key});
        this.key = key;
    }

    public static Optional<TaskType> valueOfKey(String key) {
        return Arrays.stream(TaskType.values()).filter(type -> type.getKey().equals(key)).findFirst();
    }

    public String getKey() {
        return this.key;
    }
}

