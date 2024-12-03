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
public interface SynchronyRequests
extends Entity {
    @NotNull
    @Indexed
    public long getContentId();

    public void setContentId(long var1);

    public String getType();

    public void setType(String var1);

    public String getUrl();

    public void setUrl(String var1);

    public String getPayload();

    public void setPayload(String var1);

    public boolean isSuccessful();

    public void setSuccessful(boolean var1);

    @NotNull
    @Indexed
    public Date getInserted();

    public void setInserted(Date var1);
}

