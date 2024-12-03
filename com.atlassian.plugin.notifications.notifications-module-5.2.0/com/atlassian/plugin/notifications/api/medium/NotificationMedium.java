/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.plugin.notifications.api.medium;

import com.atlassian.fugue.Option;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.notifications.api.ErrorCollection;
import com.atlassian.plugin.notifications.api.medium.Message;
import com.atlassian.plugin.notifications.api.medium.RecipientType;
import com.atlassian.plugin.notifications.api.medium.Server;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserKey;
import java.util.Map;

public interface NotificationMedium {
    public String getKey();

    public void init(ModuleDescriptor var1);

    public String getServerConfigurationTemplate(ServerConfiguration var1);

    public ErrorCollection validateAddConfiguration(I18nResolver var1, Map<String, String> var2);

    public Message renderMessage(RecipientType var1, Map<String, Object> var2, ServerConfiguration var3);

    public Server createServer(ServerConfiguration var1);

    public boolean isIndividualNotificationSupported();

    public boolean isGroupNotificationSupported();

    public Option<ServerConfiguration> getStaticConfiguration();

    public boolean isUserConfigured(UserKey var1);
}

