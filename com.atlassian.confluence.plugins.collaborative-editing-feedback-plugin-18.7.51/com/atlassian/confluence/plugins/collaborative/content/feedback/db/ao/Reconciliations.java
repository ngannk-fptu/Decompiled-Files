/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Entity
 *  net.java.ao.Preload
 *  net.java.ao.schema.Indexed
 *  net.java.ao.schema.NotNull
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.db.ao;

import java.util.Date;
import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.NotNull;

@Preload
public interface Reconciliations
extends Entity {
    @NotNull
    @Indexed
    public long getContentId();

    public void setContentId(long var1);

    @NotNull
    public String getEventType();

    public void setEventType(String var1);

    public String getAncestor();

    public void setAncestor(String var1);

    public String getRevision();

    public void setRevision(String var1);

    public String getTrigger();

    public void setTrigger(String var1);

    @NotNull
    @Indexed
    public Date getInserted();

    public void setInserted(Date var1);
}

