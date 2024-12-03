/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonProcessingException
 *  com.fasterxml.jackson.databind.ObjectMapper
 */
package com.atlassian.confluence.impl.system.task;

import com.atlassian.confluence.impl.system.task.SystemMaintenanceTask;
import com.atlassian.confluence.impl.system.task.SystemMaintenanceTaskMarshalling;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonSystemMaintenanceTaskMarshalling
implements SystemMaintenanceTaskMarshalling {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String marshal(SystemMaintenanceTask task) {
        try {
            return this.objectMapper.writeValueAsString((Object)task);
        }
        catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Cannot convert task to JSON", e);
        }
    }

    @Override
    public <T extends SystemMaintenanceTask> T unmarshal(Class<T> taskClazz, String taskString) {
        try {
            return (T)((SystemMaintenanceTask)this.objectMapper.readValue(taskString, taskClazz));
        }
        catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Cannot parse task as JSON", e);
        }
    }
}

