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

@Table(value="WEBHOOK_CONFIG")
public interface AoWebhookConfigurationEntry
extends Entity {
    public static final String KEY_COLUMN = "KEY";
    public static final String TABLE_NAME = "WEBHOOK_CONFIG";
    public static final String VALUE_COLUMN = "VALUE";
    public static final String WEBHOOK_COLUMN = "WEBHOOK";
    public static final String WEBHOOK_COLUMN_QUERY = "WEBHOOKID";

    @NotNull
    @Accessor(value="KEY")
    public String getKey();

    @NotNull
    @Accessor(value="VALUE")
    public String getValue();

    @NotNull
    @Accessor(value="WEBHOOK")
    public AoWebhook getWebhook();

    @Mutator(value="KEY")
    public void setKey(@Nonnull String var1);

    @Mutator(value="VALUE")
    public void setValue(@Nonnull String var1);

    @Mutator(value="WEBHOOK")
    public void setWebhook(@Nonnull AoWebhook var1);
}

