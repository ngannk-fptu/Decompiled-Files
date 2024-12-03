/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.sal.api.upgrade;

import com.atlassian.sal.api.message.Message;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface PluginUpgradeTask {
    public int getBuildNumber();

    @Nonnull
    public String getShortDescription();

    @Nullable
    public Collection<Message> doUpgrade() throws Exception;

    @Nonnull
    public String getPluginKey();
}

