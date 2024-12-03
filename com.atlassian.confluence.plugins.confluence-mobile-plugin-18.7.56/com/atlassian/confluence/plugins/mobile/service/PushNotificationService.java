/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mywork.model.Notification
 *  com.atlassian.user.User
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.mobile.service;

import com.atlassian.confluence.plugins.mobile.dto.notification.RegistrationDto;
import com.atlassian.confluence.plugins.mobile.notification.PushNotificationSetting;
import com.atlassian.confluence.plugins.mobile.notification.PushNotificationStatus;
import com.atlassian.mywork.model.Notification;
import com.atlassian.user.User;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface PushNotificationService {
    public RegistrationDto register(@Nonnull RegistrationDto var1);

    public void unregister(@Nonnull String var1);

    public void updateStatus(@Nullable PushNotificationStatus var1);

    @Nonnull
    public PushNotificationStatus getStatus();

    public void push(List<Notification> var1);

    @Nonnull
    public PushNotificationSetting getSetting(@Nonnull String var1, @Nonnull String var2);

    @Nonnull
    public PushNotificationSetting updateSetting(@Nonnull String var1, @Nonnull String var2, @Nonnull PushNotificationSetting var3);

    public void removePushNotification(@Nonnull User var1);
}

