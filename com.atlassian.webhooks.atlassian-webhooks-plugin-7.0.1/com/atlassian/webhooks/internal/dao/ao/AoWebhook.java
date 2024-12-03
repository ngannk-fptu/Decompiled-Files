/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.java.ao.Accessor
 *  net.java.ao.Entity
 *  net.java.ao.Mutator
 *  net.java.ao.OneToMany
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.StringLength
 *  net.java.ao.schema.Table
 */
package com.atlassian.webhooks.internal.dao.ao;

import com.atlassian.webhooks.internal.dao.ao.AoWebhookConfigurationEntry;
import com.atlassian.webhooks.internal.dao.ao.AoWebhookEvent;
import java.util.Date;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.java.ao.Accessor;
import net.java.ao.Entity;
import net.java.ao.Mutator;
import net.java.ao.OneToMany;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;

@Table(value="WEBHOOK")
public interface AoWebhook
extends Entity {
    public static final String ACTIVE_COLUMN = "ACTIVE";
    public static final String CREATED_COLUMN = "CREATED";
    public static final String NAME_COLUMN = "NAME";
    public static final String SCOPE_ID_COLUMN = "SCOPE_ID";
    public static final String SCOPE_TYPE_COLUMN = "SCOPE_TYPE";
    public static final String TABLE_NAME = "WEBHOOK";
    public static final String UPDATED_COLUMN = "UPDATED";
    public static final String URL_COLUMN = "URL";

    @NotNull
    @OneToMany(reverse="getWebhook")
    public AoWebhookConfigurationEntry[] getConfiguration();

    @NotNull
    @OneToMany(reverse="getWebhook")
    public AoWebhookEvent[] getEvents();

    @Accessor(value="CREATED")
    @NotNull
    public Date getCreatedDate();

    @Accessor(value="NAME")
    @NotNull
    @StringLength(value=255)
    public String getName();

    @Accessor(value="SCOPE_ID")
    @StringLength(value=255)
    public String getScopeId();

    @Accessor(value="SCOPE_TYPE")
    @NotNull
    @StringLength(value=255)
    public String getScopeType();

    @Accessor(value="UPDATED")
    @NotNull
    public Date getUpdatedDate();

    @Accessor(value="URL")
    @NotNull
    @StringLength(value=-1)
    public String getUrl();

    @Accessor(value="ACTIVE")
    public boolean isActive();

    @Mutator(value="ACTIVE")
    public void setActive(boolean var1);

    @Mutator(value="NAME")
    public void setName(@Nonnull String var1);

    @Mutator(value="SCOPE_ID")
    public void setScopeId(@Nullable String var1);

    @Mutator(value="SCOPE_TYPE")
    public void setScopeType(@Nullable String var1);

    @Mutator(value="UPDATED")
    public void setUpdatedDate(@Nonnull Date var1);

    @Mutator(value="URL")
    public void setUrl(@Nonnull String var1);
}

