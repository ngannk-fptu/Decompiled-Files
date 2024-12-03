/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.tasklist.macro;

import com.atlassian.confluence.plugins.tasklist.macro.TaskEntity;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(value=XmlAccessType.FIELD)
public class TasksDetailPaginated {
    private int currentPage;
    private Integer totalPages;
    private boolean adaptive;
    private List<TaskEntity> detailLines;

    public TasksDetailPaginated() {
    }

    public TasksDetailPaginated(int currentPage, int totalPages, List<TaskEntity> detailLines) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.detailLines = detailLines;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getTotalPages() {
        return this.totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public List<TaskEntity> getDetailLines() {
        return this.detailLines;
    }

    public void setDetailLines(List<TaskEntity> detailLines) {
        this.detailLines = detailLines;
    }

    public boolean isAdaptive() {
        return this.adaptive;
    }

    public void setAdaptive(boolean adaptive) {
        this.adaptive = adaptive;
    }
}

