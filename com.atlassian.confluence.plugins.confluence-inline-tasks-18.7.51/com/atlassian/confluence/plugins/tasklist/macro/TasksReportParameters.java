/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.Lists
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.confluence.plugins.tasklist.macro;

import com.atlassian.confluence.plugins.tasklist.macro.ColumnNameMapper;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.map.ObjectMapper;

@XmlRootElement
@XmlAccessorType(value=XmlAccessType.FIELD)
public class TasksReportParameters {
    private static final int DEFAULT_PAGE_SIZE = 20;
    private List<String> columns;
    private List<String> spaces;
    private List<String> pages;
    private String duedateFrom;
    private String duedateTo;
    private String createdateFrom;
    private String createdateTo;
    private List<String> assignees;
    private List<String> creators;
    private String status;
    private List<String> labels;
    private int pageSize;
    private String sortColumn;
    private boolean reverseSort;

    public TasksReportParameters() {
        this.columns = new ColumnNameMapper().getColumnNames();
    }

    public TasksReportParameters(Map<String, String> parameters) {
        ColumnNameMapper columnMapper = new ColumnNameMapper(parameters.get("columns"));
        this.columns = columnMapper.getColumnNames();
        this.pageSize = parameters.containsKey("pageSize") ? Integer.parseInt(parameters.get("pageSize")) : 20;
        this.pages = this.parseIds(parameters.get("pages"));
        this.spaces = this.parseIds(parameters.get("spaces"));
        this.status = parameters.get("status");
        this.labels = this.parseIds(parameters.get("labels"));
        this.assignees = this.parseIds(parameters.get("assignees"));
        this.duedateFrom = parameters.get("duedateFrom");
        this.duedateTo = parameters.get("duedateTo");
        this.createdateFrom = parameters.get("createddateFrom");
        this.createdateTo = parameters.get("createddateTo");
        this.creators = this.parseIds(parameters.get("creators"));
        this.sortColumn = parameters.get("sortBy");
        this.reverseSort = "true".equals(parameters.get("reverseSort"));
    }

    public static TasksReportParameters fromString(String json) throws IOException {
        return (TasksReportParameters)new ObjectMapper().readValue(json, TasksReportParameters.class);
    }

    public List<String> getColumns() {
        return this.columns;
    }

    public List<String> getSpaces() {
        return this.spaces;
    }

    public List<String> getPages() {
        return this.pages;
    }

    public String getDuedateFrom() {
        return this.duedateFrom;
    }

    public String getDuedateTo() {
        return this.duedateTo;
    }

    public String getCreatedateFrom() {
        return this.createdateFrom;
    }

    public String getCreatedateTo() {
        return this.createdateTo;
    }

    public List<String> getCreators() {
        return this.creators;
    }

    public List<String> getAssignees() {
        return this.assignees;
    }

    public String getStatus() {
        return this.status;
    }

    public List<String> getLabels() {
        return this.labels;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public String getSortColumn() {
        return this.sortColumn;
    }

    public boolean isReverseSort() {
        return this.reverseSort;
    }

    private List<String> parseIds(String ids) {
        if (ids == null) {
            return Collections.emptyList();
        }
        return Lists.newArrayList((Iterable)Splitter.on((char)',').trimResults().split((CharSequence)ids));
    }
}

