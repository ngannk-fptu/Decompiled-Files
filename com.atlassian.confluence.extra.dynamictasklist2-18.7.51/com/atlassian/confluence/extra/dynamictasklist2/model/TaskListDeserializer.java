/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.dynamictasklist2.model;

import com.atlassian.confluence.extra.dynamictasklist2.dao.WikiFormatTaskListDao;
import com.atlassian.confluence.extra.dynamictasklist2.model.MalformedRowException;
import com.atlassian.confluence.extra.dynamictasklist2.model.Task;
import com.atlassian.confluence.extra.dynamictasklist2.model.TaskList;
import com.atlassian.confluence.extra.dynamictasklist2.util.TaskListUtil;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class TaskListDeserializer {
    public TaskList deserialize(String listName, int occurance, Map parameters, String body) {
        TaskList tasklist = new TaskList(listName, occurance);
        tasklist.getConfig().load(parameters);
        this.parseTable(body, tasklist);
        return tasklist;
    }

    protected void parseTable(String tableStr, TaskList taskList) {
        String[] rows = tableStr.split("\n");
        boolean rteEscaped = rows.length > 0 && rows[0].startsWith("\\|\\|");
        List<String> columnList = null;
        for (int i1 = 0; i1 < rows.length; ++i1) {
            String row = rows[i1];
            row = (rteEscaped ? row.replaceAll("\\\\\\|", "|") : row).trim();
            if (row.startsWith("||") && row.endsWith("||")) {
                String[] headers = row.split("\\|\\|");
                columnList = new ArrayList(headers.length);
                for (int i = 1; i < headers.length; ++i) {
                    columnList.add(headers[i].trim());
                }
                continue;
            }
            if (row.startsWith("||") || !row.startsWith("|") || !row.endsWith("|")) continue;
            if (columnList == null) {
                columnList = this.getListOfColumns();
            }
            this.parseRow(row, taskList, columnList);
        }
    }

    protected List getListOfColumns() {
        WikiFormatTaskListDao.Column[] columns = WikiFormatTaskListDao.Column.values();
        ArrayList<String> columnList = new ArrayList<String>(columns.length);
        for (int i = 0; i < columns.length; ++i) {
            WikiFormatTaskListDao.Column column = columns[i];
            columnList.add(column.toString());
        }
        return columnList;
    }

    protected void parseRow(String row, TaskList taskList, List columnList) {
        String[] cells = this.splitRow(row = row.substring(1, row.length() - 1));
        if (cells.length != columnList.size()) {
            throw new MalformedRowException("Malformed table row '" + row + "' in task list '" + taskList.getName() + "'");
        }
        Task task = new Task(TaskListUtil.desanitizeTaskName(this.getValue(WikiFormatTaskListDao.Column.NAME, columnList, cells)), taskList);
        task.setCompleted(this.getBooleanValue(WikiFormatTaskListDao.Column.COMPLETED, columnList, cells));
        task.setPriority(Task.Priority.from(this.getValue(WikiFormatTaskListDao.Column.PRIORITY, columnList, cells)));
        task.setLocked(this.getBooleanValue(WikiFormatTaskListDao.Column.LOCKED, columnList, cells));
        task.setCreatedDate(this.getTimeValue(WikiFormatTaskListDao.Column.CREATED_DATE, columnList, cells));
        task.setCompletedDate(this.getTimeValue(WikiFormatTaskListDao.Column.COMPLETED_DATE, columnList, cells));
        task.setAssignee(this.getValue(WikiFormatTaskListDao.Column.ASSIGNEE, columnList, cells));
        taskList.addTask(task);
    }

    protected String getValue(WikiFormatTaskListDao.Column column, List columnList, String[] cells) {
        int index = columnList.indexOf(column.toString());
        String value = index < 0 || index >= cells.length ? null : cells[index];
        return value != null ? value.trim() : "";
    }

    protected boolean getBooleanValue(WikiFormatTaskListDao.Column column, List columnList, String[] cells) {
        String value = this.getValue(column, columnList, cells);
        return Boolean.valueOf(value) != false || "Y".equalsIgnoreCase(value) || "T".equalsIgnoreCase(value);
    }

    protected long getTimeValue(WikiFormatTaskListDao.Column column, List columnList, String[] cells) {
        String value = this.getValue(column, columnList, cells);
        if (StringUtils.isEmpty((CharSequence)value)) {
            return 0L;
        }
        try {
            return Long.valueOf(value);
        }
        catch (NumberFormatException nfe) {
            try {
                Date parsed = this.getDateFormat().parse(value);
                return parsed.getTime();
            }
            catch (ParseException pe) {
                throw new IllegalArgumentException("Unable to parse date value '" + value + "'");
            }
        }
    }

    protected String[] splitRow(String s) {
        ArrayList<String> fields = new ArrayList<String>();
        StringBuffer tmp = new StringBuffer();
        boolean escaped = false;
        char[] chars = s.trim().toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            char c = chars[i];
            if (c == '|') {
                if (i == 0) continue;
                if (escaped) {
                    tmp.append(c);
                    escaped = false;
                    continue;
                }
                fields.add(tmp.toString());
                tmp = new StringBuffer();
                continue;
            }
            if (c == '\\') {
                escaped = !escaped;
                tmp.append(c);
                continue;
            }
            tmp.append(c);
        }
        if (tmp.length() != 0) {
            fields.add(tmp.toString());
        }
        return fields.toArray(new String[fields.size()]);
    }

    protected DateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }
}

