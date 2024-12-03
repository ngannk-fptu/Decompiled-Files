/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Either
 *  com.atlassian.plugin.notifications.api.event.NotificationEvent
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Maps
 *  org.apache.commons.beanutils.PropertyUtilsBean
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.notifications.impl;

import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.confluence.notifications.impl.NotificationEventFactory;
import com.atlassian.confluence.notifications.impl.ObjectMapperFactory;
import com.atlassian.confluence.notifications.impl.descriptors.NotificationDescriptor;
import com.atlassian.fugue.Either;
import com.atlassian.plugin.notifications.api.event.NotificationEvent;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class DescriptorBasedNotificationEventFactory<PAYLOAD extends NotificationPayload>
implements NotificationEventFactory<PAYLOAD> {
    private final NotificationDescriptor<PAYLOAD> descriptor;
    private final ObjectMapperFactory objectMapperFactory;
    private final Map<String, Method> zeroParameterReadMethods;

    public DescriptorBasedNotificationEventFactory(NotificationDescriptor<PAYLOAD> descriptor, ObjectMapperFactory objectMapperFactory) {
        this.descriptor = descriptor;
        this.objectMapperFactory = objectMapperFactory;
        this.zeroParameterReadMethods = this.extractZeroParameterReadMethods(descriptor);
    }

    @Override
    public NotificationEvent<Notification<PAYLOAD>> create(final Notification<PAYLOAD> notification) {
        PAYLOAD payload = this.verifyPayload(notification.getPayload());
        final Date timestamp = new Date();
        final Map<String, Object> renderContext = this.createContextFromNotificationPayload(payload);
        return new NotificationEvent<Notification<PAYLOAD>>(){

            public Map<String, Object> getParams(I18nResolver i18nResolver, UserKey recipient) {
                return renderContext;
            }

            public Date getTime() {
                return timestamp;
            }

            public Notification<PAYLOAD> getOriginalEvent() {
                return notification;
            }

            public String getSubject() {
                String name = DescriptorBasedNotificationEventFactory.this.descriptor.getName();
                return name == null ? this.getKey() : name;
            }

            public String getName(I18nResolver i18nResolver) {
                String i18nNameKey = DescriptorBasedNotificationEventFactory.this.descriptor.getI18nNameKey();
                return i18nNameKey == null ? this.getSubject() : i18nResolver.getText(i18nNameKey);
            }

            public String getKey() {
                return DescriptorBasedNotificationEventFactory.this.descriptor.getCompleteKey();
            }

            public UserKey getAuthor() {
                return (UserKey)notification.getOriginator().getOrNull();
            }
        };
    }

    private PAYLOAD verifyPayload(PAYLOAD payload) {
        Preconditions.checkArgument((boolean)this.descriptor.getModuleClass().isAssignableFrom(payload.getClass()), (String)"Given payload class [%s] does not implement [%s].", (Object)payload.getClass().getName(), (Object)this.descriptor.getModuleClass().getName());
        return this.verifySerializability(payload);
    }

    private PAYLOAD verifySerializability(PAYLOAD payload) {
        Preconditions.checkNotNull(payload);
        Either<IllegalStateException, PAYLOAD> payloadEither = this.objectMapperFactory.verifyObjectSerializable(payload);
        if (payloadEither.isRight()) {
            return (PAYLOAD)((NotificationPayload)payloadEither.right().get());
        }
        throw (IllegalStateException)payloadEither.left().get();
    }

    private ImmutableMap<String, Method> extractZeroParameterReadMethods(NotificationDescriptor descriptor) {
        ImmutableMap.Builder methodReferenceBuilder = ImmutableMap.builder();
        for (PropertyDescriptor payloadClassProperty : new PropertyUtilsBean().getPropertyDescriptors(descriptor.getModuleClass())) {
            Method propertyMethod = payloadClassProperty.getReadMethod();
            Class<?>[] methodParameterTypes = propertyMethod.getParameterTypes();
            if (methodParameterTypes.length > 0) {
                throw new IllegalArgumentException(String.format("The payload class [%s] should only declare zero parameter read methods, but it declared method [%s] which takes the parameters [%s].", descriptor.getModuleClass().getName(), propertyMethod.getName(), ToStringBuilder.reflectionToString(methodParameterTypes)));
            }
            methodReferenceBuilder.put((Object)payloadClassProperty.getName(), (Object)payloadClassProperty.getReadMethod());
        }
        return methodReferenceBuilder.build();
    }

    private Map<String, Object> createContextFromNotificationPayload(PAYLOAD payload) {
        return Maps.transformEntries(this.zeroParameterReadMethods, (propertyName, propertyMethod) -> {
            try {
                return propertyMethod.invoke(payload, new Object[0]);
            }
            catch (IllegalAccessException e) {
                throw new IllegalArgumentException(e);
            }
            catch (InvocationTargetException e) {
                throw new IllegalArgumentException(e);
            }
        });
    }
}

