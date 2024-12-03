/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.notifications.api.medium;

import com.atlassian.plugin.notifications.api.medium.RecipientType;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import java.util.Map;

public interface TemplateManager {
    public String renderMessage(RecipientType var1, Map<String, Object> var2, ServerConfiguration var3);

    public String renderSubject(RecipientType var1, Map<String, Object> var2, ServerConfiguration var3);
}

