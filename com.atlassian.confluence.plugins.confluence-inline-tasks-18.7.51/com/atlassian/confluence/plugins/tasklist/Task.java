/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.tasklist;

import com.atlassian.confluence.plugins.tasklist.TaskStatus;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;
import net.jcip.annotations.Immutable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@Immutable
public class Task {
    private final long globalId;
    private final long id;
    private final long contentId;
    private final TaskStatus status;
    private final String title;
    private final String description;
    private final String pageTitle;
    private final String body;
    private final String creator;
    private final String assignee;
    private final String assigneeKey;
    private final String assigneeName;
    private final String completeUser;
    private final Date createDate;
    private final Date dueDate;
    private final Date updateDate;
    private final Date completeDate;

    @JsonCreator
    private Task(@JsonProperty(value="globalId") long globalId, @JsonProperty(value="id") long id, @JsonProperty(value="contentId") long contentId, @JsonProperty(value="status") TaskStatus status, @JsonProperty(value="title") String title, @JsonProperty(value="pageTitle") String pageTitle, @JsonProperty(value="description") String description, @JsonProperty(value="body") String body, @JsonProperty(value="creator") String creator, @JsonProperty(value="assignee") String assignee, @JsonProperty(value="assigneeKey") String assigneeKey, @JsonProperty(value="assigneeName") String assigneeName, @JsonProperty(value="createDate") Date createDate, @JsonProperty(value="dueDate") Date dueDate, @JsonProperty(value="updateDate") Date updateDate, @JsonProperty(value="completeDate") Date completeDate, @JsonProperty(value="completeUser") String completeUser) {
        this.globalId = globalId;
        this.id = id;
        this.contentId = contentId;
        this.status = status;
        this.title = title;
        this.pageTitle = pageTitle;
        this.description = description;
        this.body = body;
        this.creator = creator;
        this.assignee = assignee;
        this.assigneeKey = assigneeKey;
        this.assigneeName = assigneeName;
        this.createDate = createDate;
        this.dueDate = dueDate;
        this.updateDate = updateDate;
        this.completeDate = completeDate;
        this.completeUser = completeUser;
    }

    public long getGlobalId() {
        return this.globalId;
    }

    public long getId() {
        return this.id;
    }

    public long getContentId() {
        return this.contentId;
    }

    public TaskStatus getStatus() {
        return this.status;
    }

    public String getStatusAsString() {
        return this.status.toString();
    }

    public String getTitle() {
        return this.title;
    }

    public String getPageTitle() {
        return this.pageTitle;
    }

    public String getDescription() {
        return this.description;
    }

    public String getBody() {
        return this.body;
    }

    public String getCreator() {
        return this.creator;
    }

    public String getAssignee() {
        return this.assignee;
    }

    public String getAssigneeKey() {
        return this.assigneeKey;
    }

    public String getAssigneeName() {
        return this.assigneeName;
    }

    public String getCompleteUser() {
        return this.completeUser;
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public Date getDueDate() {
        return this.dueDate;
    }

    public Date getUpdateDate() {
        return this.updateDate;
    }

    public Date getCompleteDate() {
        return this.completeDate;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Task task = (Task)o;
        if (this.globalId != task.globalId) {
            return false;
        }
        if (this.id != task.id) {
            return false;
        }
        if (this.contentId != task.contentId) {
            return false;
        }
        if (this.status != task.status) {
            return false;
        }
        if (!Objects.equals(this.title, task.title)) {
            return false;
        }
        if (!Objects.equals(this.pageTitle, task.pageTitle)) {
            return false;
        }
        if (!Objects.equals(this.description, task.description)) {
            return false;
        }
        if (!Objects.equals(this.body, task.body)) {
            return false;
        }
        if (!Objects.equals(this.creator, task.creator)) {
            return false;
        }
        if (!Objects.equals(this.assignee, task.assignee)) {
            return false;
        }
        if (!Objects.equals(this.assigneeKey, task.assigneeKey)) {
            return false;
        }
        if (!Objects.equals(this.assigneeName, task.assigneeName)) {
            return false;
        }
        if (this.createDate != null ? !Task.checkDatesEqual(this.createDate, task.createDate) : task.createDate != null) {
            return false;
        }
        if (this.dueDate != null ? !Task.checkDatesEqual(this.dueDate, task.dueDate) : task.dueDate != null) {
            return false;
        }
        if (this.updateDate != null ? !Task.checkDatesEqual(this.updateDate, task.updateDate) : task.updateDate != null) {
            return false;
        }
        if (this.completeDate != null ? !Task.checkDatesEqual(this.completeDate, task.completeDate) : task.completeDate != null) {
            return false;
        }
        return Objects.equals(this.completeUser, task.completeUser);
    }

    public static boolean checkDatesEqual(Date date1, Date date2) {
        if (date1 != null && date2 != null) {
            GregorianCalendar cal1 = new GregorianCalendar();
            cal1.setTime(date1);
            GregorianCalendar cal2 = new GregorianCalendar();
            cal2.setTime(date2);
            return cal1.get(1) == cal2.get(1) && cal1.get(2) == cal2.get(2) && cal1.get(5) == cal2.get(5);
        }
        return false;
    }

    public int hashCode() {
        int result = (int)(this.globalId ^ this.globalId >>> 32);
        result = 31 * result + (int)(this.id ^ this.id >>> 32);
        result = 31 * result + (int)(this.contentId ^ this.contentId >>> 32);
        result = 31 * result + (this.status != null ? this.status.hashCode() : 0);
        result = 31 * result + (this.title != null ? this.title.hashCode() : 0);
        result = 31 * result + (this.pageTitle != null ? this.pageTitle.hashCode() : 0);
        result = 31 * result + (this.description != null ? this.description.hashCode() : 0);
        result = 31 * result + (this.body != null ? this.body.hashCode() : 0);
        result = 31 * result + (this.creator != null ? this.creator.hashCode() : 0);
        result = 31 * result + (this.assignee != null ? this.assignee.hashCode() : 0);
        result = 31 * result + (this.assigneeKey != null ? this.assigneeKey.hashCode() : 0);
        result = 31 * result + (this.assigneeName != null ? this.assigneeName.hashCode() : 0);
        result = 31 * result + (this.createDate != null ? this.createDate.hashCode() : 0);
        result = 31 * result + (this.dueDate != null ? this.dueDate.hashCode() : 0);
        result = 31 * result + (this.updateDate != null ? this.updateDate.hashCode() : 0);
        result = 31 * result + (this.completeDate != null ? this.completeDate.hashCode() : 0);
        result = 31 * result + (this.completeUser != null ? this.completeUser.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "Task [globalId=" + this.globalId + ", id=" + this.id + ", contentId=" + this.contentId + ", taskStatus=" + this.status + ", title=" + this.title + ", description=" + this.description + ", body=" + this.body + ", pageTitle=" + this.pageTitle + ", creator=" + this.creator + ", assigneeKey=" + this.assigneeKey + ", assignee=" + this.assignee + ", assigneeName=" + this.assigneeName + ", createDate=" + this.createDate + ", dueDate=" + this.dueDate + ", updateDate=" + this.updateDate + ", completeUser=" + this.completeUser + ", completeDate=" + this.completeDate + "]";
    }

    public static class Builder {
        private long globalId;
        private long id;
        private long contentId;
        private TaskStatus status;
        private String title;
        private String pageTitle;
        private String description;
        private String body;
        private String creator;
        private String assignee;
        private String assigneeKey;
        private String assigneeName;
        private String completeUser;
        private Date createDate;
        private Date dueDate;
        private Date updateDate;
        private Date completeDate;

        public Builder() {
        }

        public Builder(Task task) {
            this.globalId = task.globalId;
            this.id = task.id;
            this.contentId = task.contentId;
            this.status = task.status;
            this.title = task.title;
            this.pageTitle = task.pageTitle;
            this.description = task.description;
            this.body = task.body;
            this.creator = task.creator;
            this.assignee = task.assignee;
            this.assigneeKey = task.assigneeKey;
            this.assigneeName = task.assigneeName;
            this.completeUser = task.completeUser;
            this.createDate = task.createDate;
            this.dueDate = task.dueDate;
            this.updateDate = task.updateDate;
            this.completeDate = task.completeDate;
        }

        public Builder withGlobalId(long globalId) {
            this.globalId = globalId;
            return this;
        }

        public Builder withId(long id) {
            this.id = id;
            return this;
        }

        public Builder withContentId(long contentId) {
            this.contentId = contentId;
            return this;
        }

        public Builder withStatus(TaskStatus status) {
            this.status = status;
            return this;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withPageTitle(String pageTitle) {
            this.pageTitle = pageTitle;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withBody(String body) {
            this.body = body;
            return this;
        }

        public Builder withCreator(String creator) {
            this.creator = creator;
            return this;
        }

        public Builder withAssignee(String assignee) {
            this.assignee = assignee;
            return this;
        }

        public Builder withAssigneeKey(String assigneeKey) {
            this.assigneeKey = assigneeKey;
            return this;
        }

        public Builder withAssigneeName(String assigneeName) {
            this.assigneeName = assigneeName;
            return this;
        }

        public Builder withCompleteUser(String completeUser) {
            this.completeUser = completeUser;
            return this;
        }

        public Builder withCreateDate(Date createDate) {
            this.createDate = createDate;
            return this;
        }

        public Builder withDueDate(Date dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public Builder withUpdateDate(Date updateDate) {
            this.updateDate = updateDate;
            return this;
        }

        public Builder withCompleteDate(Date completeDate) {
            this.completeDate = completeDate;
            return this;
        }

        public Task build() {
            return new Task(this.globalId, this.id, this.contentId, this.status, this.title, this.pageTitle, this.description, this.body, this.creator, this.assignee, this.assigneeKey, this.assigneeName, this.createDate, this.dueDate, this.updateDate, this.completeDate, this.completeUser);
        }
    }
}

