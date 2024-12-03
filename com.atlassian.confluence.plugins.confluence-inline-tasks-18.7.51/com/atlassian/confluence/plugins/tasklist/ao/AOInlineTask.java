/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Preload
 *  net.java.ao.RawEntity
 *  net.java.ao.schema.AutoIncrement
 *  net.java.ao.schema.Indexed
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.PrimaryKey
 *  net.java.ao.schema.StringLength
 */
package com.atlassian.confluence.plugins.tasklist.ao;

import com.atlassian.confluence.plugins.tasklist.TaskStatus;
import java.util.Date;
import net.java.ao.Preload;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.StringLength;

@Preload
public interface AOInlineTask
extends RawEntity<Long> {
    @AutoIncrement
    @NotNull
    @PrimaryKey
    public long getGlobalId();

    public long getId();

    public void setId(long var1);

    @Indexed
    public long getContentId();

    public void setContentId(long var1);

    @Indexed
    public TaskStatus getTaskStatus();

    public void setTaskStatus(TaskStatus var1);

    @StringLength(value=-1)
    public String getBody();

    public void setBody(String var1);

    @Indexed
    public String getCreatorUserKey();

    public void setCreatorUserKey(String var1);

    @Indexed
    public String getAssigneeUserKey();

    public void setAssigneeUserKey(String var1);

    public String getCompleteUserKey();

    public void setCompleteUserKey(String var1);

    @Indexed
    public Date getCreateDate();

    public void setCreateDate(Date var1);

    @Indexed
    public Date getDueDate();

    public void setDueDate(Date var1);

    public Date getUpdateDate();

    public void setUpdateDate(Date var1);

    public Date getCompleteDate();

    public void setCompleteDate(Date var1);
}

