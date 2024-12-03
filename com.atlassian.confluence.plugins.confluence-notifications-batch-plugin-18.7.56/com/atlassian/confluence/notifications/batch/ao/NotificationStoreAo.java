/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Entity
 *  net.java.ao.Preload
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.StringLength
 *  net.java.ao.schema.Table
 */
package com.atlassian.confluence.notifications.batch.ao;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;

@Preload
@Table(value="BATCH_NOTIFICATION")
public interface NotificationStoreAo
extends Entity {
    @NotNull
    public String getNotificationKey();

    public void setNotificationKey(String var1);

    @NotNull
    @StringLength(value=-1)
    public String getPayload();

    public void setPayload(String var1);

    @NotNull
    public String getBatchingColumn();

    public void setBatchingColumn(String var1);

    @NotNull
    public String getContentType();

    public void setContentType(String var1);
}

