/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.java.ao.Accessor
 *  net.java.ao.Entity
 *  net.java.ao.Mutator
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.Table
 */
package com.atlassian.webhooks.internal.dao.ao;

import com.atlassian.webhooks.internal.dao.ao.AoWebhook;
import javax.annotation.Nonnull;
import net.java.ao.Accessor;
import net.java.ao.Entity;
import net.java.ao.Mutator;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.Table;

@Table(value="WEBHOOK_EVENT")
public interface AoWebhookEvent
extends Entity {
    public static final String EVENT_ID_COLUMN = "EVENT_ID";
    public static final String TABLE_NAME = "WEBHOOK_EVENT";
    public static final String WEBHOOK_COLUMN = "WEBHOOK";
    public static final String WEBHOOK_COLUMN_QUERY = "WEBHOOKID";

    @NotNull
    @Accessor(value="EVENT_ID")
    public String getEventId();

    @NotNull
    @Accessor(value="WEBHOOK")
    public AoWebhook getWebhook();

    @Mutator(value="EVENT_ID")
    public void setEventId(@Nonnull String var1);

    @Mutator(value="WEBHOOK")
    public void setWebhook(@Nonnull AoWebhook var1);
}

