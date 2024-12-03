/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.Nonnull
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.activeobject.dao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.plugins.mobile.activeobject.dao.PushNotificationDao;
import com.atlassian.confluence.plugins.mobile.activeobject.entity.PushNotificationAO;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PushNotificationDaoImpl
implements PushNotificationDao {
    private final ActiveObjects ao;

    @Autowired
    public PushNotificationDaoImpl(@ComponentImport ActiveObjects ao) {
        this.ao = ao;
    }

    @Override
    public PushNotificationAO findById(@Nonnull String id) {
        return (PushNotificationAO)this.ao.get(PushNotificationAO.class, (Object)id);
    }

    @Override
    public List<PushNotificationAO> findById(@Nonnull Set<String> ids) {
        String[] idArray = ids.toArray(new String[ids.size()]);
        return (List)this.ao.executeInTransaction(() -> Arrays.asList((PushNotificationAO[])this.ao.get(PushNotificationAO.class, (Object[])idArray)));
    }

    @Override
    public List<PushNotificationAO> findByUserNameAndAppNameAndDeviceId(@Nonnull String userName, @Nonnull String appName, @Nonnull String deviceId) {
        return Arrays.asList((PushNotificationAO[])this.ao.find(PushNotificationAO.class, Query.select().where("USER_NAME = ? AND APP_NAME = ? AND DEVICE_ID = ?", new Object[]{userName, appName, deviceId})));
    }

    @Override
    public List<PushNotificationAO> findByUserNameAndAppNameAndDeviceIdOrToken(@Nonnull String userName, @Nonnull String appName, @Nonnull String deviceId, @Nonnull String token) {
        return Arrays.asList((PushNotificationAO[])this.ao.find(PushNotificationAO.class, Query.select().where("USER_NAME = ? AND APP_NAME = ? AND (DEVICE_ID = ? OR TOKEN = ?)", new Object[]{userName, appName, deviceId, token})));
    }

    @Override
    public List<PushNotificationAO> findByUserNames(@Nonnull Set<String> userNames, boolean isActive) {
        String whereClause = "USER_NAME IN (" + this.buildQuestionPlaceholder(userNames) + ") AND ACTIVE = ?";
        ArrayList<String> params = new ArrayList<String>(userNames);
        params.add((String)((Object)Boolean.valueOf(isActive)));
        return (List)this.ao.executeInTransaction(() -> Arrays.asList((PushNotificationAO[])this.ao.find(PushNotificationAO.class, Query.select().where(whereClause, params.toArray()))));
    }

    @Override
    public PushNotificationAO create(@Nonnull Map<String, Object> data) {
        return (PushNotificationAO)this.ao.create(PushNotificationAO.class, data);
    }

    @Override
    public void delete(PushNotificationAO ... pushNotificationAOs) {
        this.ao.delete((RawEntity[])pushNotificationAOs);
    }

    @Override
    public boolean deleteByToken(@Nonnull String token) {
        return this.ao.deleteWithSQL(PushNotificationAO.class, "TOKEN = ?", new Object[]{token}) > 0;
    }

    @Override
    public void delete(@Nonnull List<PushNotificationAO> pushNotificationAOs) {
        PushNotificationAO[] pushNotificationArray = pushNotificationAOs.toArray(new PushNotificationAO[pushNotificationAOs.size()]);
        this.delete(pushNotificationArray);
    }

    @Override
    public void deleteByIds(@Nonnull Set<String> ids) {
        String whereClause = "ID IN (" + this.buildQuestionPlaceholder(ids) + ")";
        this.ao.executeInTransaction(() -> this.ao.deleteWithSQL(PushNotificationAO.class, whereClause, ids.toArray()));
    }

    @Override
    public void deleteByUsername(@Nonnull String userName) {
        this.ao.deleteWithSQL(PushNotificationAO.class, "USER_NAME = ?", new Object[]{userName});
    }

    @Override
    public void update(@Nonnull List<PushNotificationAO> pushNotificationAOs) {
        this.ao.executeInTransaction(() -> {
            pushNotificationAOs.forEach(RawEntity::save);
            return null;
        });
    }

    private String buildQuestionPlaceholder(Collection<String> collection) {
        return collection.stream().map(name -> "?").collect(Collectors.joining(","));
    }
}

