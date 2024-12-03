/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Option
 */
package com.atlassian.plugin.notifications.api.medium;

import com.atlassian.fugue.Either;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.event.NotificationEvent;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import java.util.Map;

public interface RenderContextFactory<NE extends NotificationEvent> {
    public Map<String, Object> create(NE var1, ServerConfiguration var2) throws IllegalArgumentException;

    public Map<String, Object> create(NE var1, ServerConfiguration var2, Either<NotificationAddress, RoleRecipient> var3) throws IllegalArgumentException;

    default public Option<Map<String, Object>> createContext(NE event, ServerConfiguration serverConfig, Either<NotificationAddress, RoleRecipient> recipientData) throws IllegalArgumentException {
        return Option.some(this.create(event, serverConfig, recipientData));
    }

    public Class<NE> consumes();
}

