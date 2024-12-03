/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.util.ClassUtils
 *  com.google.common.base.Preconditions
 */
package com.atlassian.plugin.notifications.api.medium;

import com.atlassian.fugue.Either;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.event.NotificationEvent;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.RenderContextFactory;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.plugin.notifications.api.medium.recipient.UserKeyRoleRecipient;
import com.atlassian.plugin.util.ClassUtils;
import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.Map;

public abstract class RenderContextFactoryTemplate<NE extends NotificationEvent>
implements RenderContextFactory<NE> {
    private Class<NE> consumesType;

    protected RenderContextFactoryTemplate() {
        this.consumesType = (Class)ClassUtils.getTypeArguments(RenderContextFactoryTemplate.class, this.getClass()).get(0);
        if (this.consumesType == null) {
            throw new IllegalArgumentException(String.format("Expected class [%s] to be parametrized with a specific [%s] type. Use the overloaded constructor if the type is provided at runtime.", this.getClass().getName(), NotificationEvent.class.getName()));
        }
    }

    protected RenderContextFactoryTemplate(Class<NE> consumesType) {
        this.consumesType = consumesType;
    }

    @Override
    public final Class<NE> consumes() {
        return this.consumesType;
    }

    @Override
    public final Map<String, Object> create(NE event, ServerConfiguration serverConfiguration) {
        return this.create(event, serverConfiguration, (Either<NotificationAddress, RoleRecipient>)Either.right((Object)UserKeyRoleRecipient.UNKNOWN));
    }

    @Override
    public final Map<String, Object> create(NE event, ServerConfiguration serverConfiguration, Either<NotificationAddress, RoleRecipient> recipientData) {
        this.performChecks(event, serverConfiguration);
        return this.createChecked(event, serverConfiguration, recipientData);
    }

    @Override
    public final Option<Map<String, Object>> createContext(NE event, ServerConfiguration serverConfig, Either<NotificationAddress, RoleRecipient> recipientData) throws IllegalArgumentException {
        this.performChecks(event, serverConfig);
        return this.createContextChecked(event, serverConfig, recipientData);
    }

    private void performChecks(NE event, ServerConfiguration serverConfiguration) {
        Preconditions.checkNotNull(event, (Object)"The given event is null.");
        Preconditions.checkNotNull((Object)serverConfiguration, (Object)"The given serverConfiguration is null.");
        if (!this.consumesType.isInstance(event)) {
            throw new IllegalArgumentException(String.format("The given event of type [%s] is not assignable to type [%s].", event.getClass().getName(), this.consumesType.getName()));
        }
    }

    protected Map<String, Object> createChecked(NE event, ServerConfiguration serverConfiguration, Either<NotificationAddress, RoleRecipient> recipientData) {
        return Collections.emptyMap();
    }

    protected Option<Map<String, Object>> createContextChecked(NE event, ServerConfiguration serverConfiguration, Either<NotificationAddress, RoleRecipient> recipientData) {
        return Option.some(this.createChecked(event, serverConfiguration, recipientData));
    }
}

