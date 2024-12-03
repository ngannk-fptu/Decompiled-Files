/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Entity
 *  net.java.ao.Preload
 *  net.java.ao.schema.Default
 *  net.java.ao.schema.Indexed
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.StringLength
 *  net.java.ao.schema.Table
 */
package com.atlassian.confluence.extra.calendar3.model.persistence;

import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.Default;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;

@Preload
@Table(value="tc_jira_remi_events")
public interface JiraReminderEventEntity
extends Entity {
    @Indexed
    @NotNull
    public String getKeyId();

    public void setKeyId(String var1);

    @NotNull
    public SubCalendarEntity getSubCalendar();

    public void setSubCalendar(SubCalendarEntity var1);

    public String getUserId();

    public void setUserId(String var1);

    public String getJQL();

    public void setJQL(String var1);

    public String getTicketId();

    public void setTicketId(String var1);

    public String getAssignee();

    public void setAssignee(String var1);

    public String getStatus();

    public void setStatus(String var1);

    public String getTitle();

    public void setTitle(String var1);

    @StringLength(value=-1)
    public String getDescription();

    public void setDescription(String var1);

    public String getEventType();

    public void setEventType(String var1);

    @Indexed
    public long getUtcStart();

    public void setUtcStart(long var1);

    @Indexed
    public long getUtcEnd();

    public void setUtcEnd(long var1);

    @NotNull
    @Default(value="false")
    public boolean isAllDay();

    public void setAllDay(boolean var1);

    public String getIssueLink();

    public void setIssueLink(String var1);

    public String getIssueIconUrl();

    public void setIssueIconUrl(String var1);
}

