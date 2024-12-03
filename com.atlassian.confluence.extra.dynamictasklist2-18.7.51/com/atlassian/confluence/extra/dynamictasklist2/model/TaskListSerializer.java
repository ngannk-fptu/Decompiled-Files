/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.dynamictasklist2.model;

import com.atlassian.confluence.extra.dynamictasklist2.dao.WikiFormatTaskListDao;
import com.atlassian.confluence.extra.dynamictasklist2.model.Task;
import com.atlassian.confluence.extra.dynamictasklist2.model.TaskList;
import com.atlassian.confluence.extra.dynamictasklist2.util.TaskListUtil;
import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class TaskListSerializer {
    private final String PREPARE_DATE_NULL_VALUE = "          ";
    private final String PREPARE_EMPTY_VALUE = " ";

    public String serializeMacro(TaskList tasklist) {
        StringBuffer buffer = new StringBuffer();
        String serializedConfig = this.serializeConfig(tasklist);
        String macro = "tasklist";
        buffer.append('{').append(macro);
        if (serializedConfig != null && serializedConfig.length() > 0) {
            buffer.append(':').append(serializedConfig);
        }
        buffer.append('}').append('\n');
        buffer.append(this.serialize(tasklist));
        buffer.append('{').append(macro).append('}');
        return buffer.toString();
    }

    public String serialize(TaskList tasklist) {
        StringBuffer buffer = new StringBuffer(this.serializeTaskHeader());
        Iterator it = tasklist.iterator();
        while (it.hasNext()) {
            Task task = (Task)it.next();
            buffer.append(this.serializeTask(task));
        }
        return buffer.toString();
    }

    protected String serializeConfig(TaskList tasklist) {
        String serializedConfig;
        StringBuilder buffer = new StringBuilder();
        String name = tasklist.getName();
        boolean nameRendered = false;
        if (StringUtils.isNotBlank((CharSequence)name)) {
            buffer.append(name);
            nameRendered = true;
        }
        if ((serializedConfig = tasklist.getConfig().serialize()).length() > 0 && nameRendered) {
            buffer.append('|');
        }
        buffer.append(serializedConfig);
        return buffer.toString();
    }

    protected String serializeTaskHeader() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < WikiFormatTaskListDao.Column.values().length; ++i) {
            WikiFormatTaskListDao.Column column = WikiFormatTaskListDao.Column.values()[i];
            buffer.append("||");
            buffer.append(column.toString());
        }
        return buffer.append("||\n").toString();
    }

    protected String serializeTask(Task task) {
        StringBuffer buffer = new StringBuffer();
        buffer.append('|');
        buffer.append(this.prepare(task.isCompleted()).toUpperCase().charAt(0));
        buffer.append('|');
        buffer.append(this.prepare(task.getPriority()).toUpperCase().charAt(0));
        buffer.append('|');
        buffer.append(this.prepare(task.isLocked()).toUpperCase().charAt(0));
        buffer.append('|');
        buffer.append(this.prepareDate(task.getCreatedDate()));
        buffer.append('|');
        buffer.append(this.prepareDate(task.getCompletedDate()));
        buffer.append('|');
        buffer.append(this.prepare(task.getAssignee()));
        buffer.append('|');
        buffer.append(TaskListUtil.sanitizeTaskName(task.getName()));
        buffer.append('|');
        buffer.append('\n');
        return buffer.toString();
    }

    protected String prepareDate(long value) {
        return value == 0L ? "          " : String.valueOf(value);
    }

    protected String prepare(String value) {
        return StringUtils.isEmpty((CharSequence)value) ? " " : value;
    }

    protected String prepare(Object value) {
        return value == null ? " " : value.toString();
    }
}

