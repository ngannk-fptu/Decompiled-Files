/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask
 *  com.atlassian.activeobjects.external.ModelVersion
 *  com.atlassian.fugue.Effect
 *  com.atlassian.fugue.Option
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  net.java.ao.EntityStreamCallback
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 */
package com.atlassian.mywork.host.upgrade.v8;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask;
import com.atlassian.activeobjects.external.ModelVersion;
import com.atlassian.confluence.usercompatibility.UserCompatibilityHelper;
import com.atlassian.fugue.Effect;
import com.atlassian.fugue.Option;
import com.atlassian.mywork.host.upgrade.v8.AONotification;
import com.atlassian.mywork.host.upgrade.v8.AORegistration;
import com.atlassian.mywork.host.upgrade.v8.AOTask;
import com.atlassian.mywork.host.upgrade.v8.AOUser;
import com.atlassian.mywork.host.upgrade.v8.AOUserApplicationLink;
import com.atlassian.sal.usercompatibility.IdentifierUtils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.ArrayList;
import java.util.HashMap;
import net.java.ao.EntityStreamCallback;
import net.java.ao.Query;
import net.java.ao.RawEntity;

public class UserIdMigrationUpgradeTask
implements ActiveObjectsUpgradeTask {
    public ModelVersion getModelVersion() {
        return ModelVersion.valueOf((String)(UserCompatibilityHelper.isRenameUserImplemented() ? "8" : "0"));
    }

    public void upgrade(ModelVersion currentVersion, final ActiveObjects ao) {
        final LoadingCache userKeyByUsername = CacheBuilder.newBuilder().build((CacheLoader)new CacheLoader<String, Option<String>>(){

            public Option<String> load(String username) {
                return Option.option((Object)UserCompatibilityHelper.getStringKeyForUsername(username));
            }
        });
        ao.migrate(new Class[]{AONotification.class, AORegistration.class, AOTask.class, AOUser.class, AOUserApplicationLink.class});
        this.deleteDuplicateUsers(ao);
        UserIdMigrationUpgradeTask.update(ao, AOUser.class, new Effect<AOUser>(){

            public void apply(AOUser aoUser) {
                String userKey = (String)((Option)userKeyByUsername.getUnchecked((Object)aoUser.getUsername())).getOrNull();
                if (userKey == null) {
                    ao.delete(ao.find(AOUserApplicationLink.class, Query.select().where("USER_ID = ?", new Object[]{aoUser.getId()})));
                    ao.delete(new RawEntity[]{aoUser});
                } else {
                    aoUser.setUserKey(userKey);
                    aoUser.save();
                }
            }
        });
        UserIdMigrationUpgradeTask.update(ao, AONotification.class, new Effect<AONotification>(){

            public void apply(AONotification notification) {
                String userKey = (String)((Option)userKeyByUsername.getUnchecked((Object)notification.getUser())).getOrNull();
                if (userKey == null) {
                    ao.delete(new RawEntity[]{notification});
                } else {
                    notification.setUserKey(userKey);
                    notification.save();
                }
            }
        });
        UserIdMigrationUpgradeTask.update(ao, AOTask.class, new Effect<AOTask>(){

            public void apply(AOTask task) {
                String userKey = (String)((Option)userKeyByUsername.getUnchecked((Object)task.getUser())).getOrNull();
                if (userKey == null) {
                    ao.delete(new RawEntity[]{task});
                } else {
                    task.setUserKey(userKey);
                    task.save();
                }
            }
        });
    }

    private static <T extends RawEntity<K>, K> void update(ActiveObjects ao, Class<T> type, Effect<T> action) {
        RawEntity[] entities;
        int BATCH_SIZE = 1000;
        int offset = 0;
        do {
            for (RawEntity entity : entities = ao.find(type, Query.select().order("ID").offset(offset).limit(1000))) {
                action.apply((Object)entity);
            }
            offset += 1000;
        } while (entities.length == 1000);
    }

    private int deleteDuplicateUsers(ActiveObjects ao) {
        final HashMap userMap = new HashMap(1024);
        final ArrayList duplicateUserIds = new ArrayList();
        ao.stream(AOUser.class, (EntityStreamCallback)new EntityStreamCallback<AOUser, Long>(){

            public void onRowRead(AOUser aoUser) {
                String username = IdentifierUtils.toLowerCase(aoUser.getUsername());
                Long oldUserId = (Long)userMap.get(username);
                if (oldUserId != null) {
                    if (oldUserId < aoUser.getId()) {
                        duplicateUserIds.add(oldUserId);
                        userMap.put(username, aoUser.getId());
                    } else {
                        duplicateUserIds.add(aoUser.getId());
                    }
                } else {
                    userMap.put(username, aoUser.getId());
                }
            }
        });
        for (Long userId : duplicateUserIds) {
            this.delete(ao, userId);
        }
        return duplicateUserIds.size();
    }

    private void delete(ActiveObjects ao, Long userId) {
        AOUser user = (AOUser)ao.get(AOUser.class, (Object)userId);
        if (user != null) {
            ao.delete(ao.find(AOUserApplicationLink.class, Query.select().where("USER_ID = ?", new Object[]{user.getId()})));
            ao.delete(new RawEntity[]{user});
        }
    }
}

