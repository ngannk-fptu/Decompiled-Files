/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.mobile.activeobject.dao;

import com.atlassian.confluence.plugins.mobile.activeobject.entity.PushNotificationAO;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

public interface PushNotificationDao {
    public PushNotificationAO findById(@Nonnull String var1);

    public List<PushNotificationAO> findById(@Nonnull Set<String> var1);

    public List<PushNotificationAO> findByUserNames(@Nonnull Set<String> var1, boolean var2);

    public List<PushNotificationAO> findByUserNameAndAppNameAndDeviceId(@Nonnull String var1, @Nonnull String var2, @Nonnull String var3);

    public List<PushNotificationAO> findByUserNameAndAppNameAndDeviceIdOrToken(@Nonnull String var1, @Nonnull String var2, @Nonnull String var3, @Nonnull String var4);

    public PushNotificationAO create(@Nonnull Map<String, Object> var1);

    public boolean deleteByToken(@Nonnull String var1);

    public void deleteByIds(@Nonnull Set<String> var1);

    public void deleteByUsername(@Nonnull String var1);

    public void delete(PushNotificationAO ... var1);

    public void delete(@Nonnull List<PushNotificationAO> var1);

    public void update(@Nonnull List<PushNotificationAO> var1);
}

