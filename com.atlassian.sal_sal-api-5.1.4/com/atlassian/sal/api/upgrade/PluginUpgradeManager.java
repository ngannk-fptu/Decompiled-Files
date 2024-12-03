/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.upgrade;

import com.atlassian.sal.api.message.Message;
import java.util.List;

public interface PluginUpgradeManager {
    public List<Message> upgrade();
}

