/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.user.User
 *  javax.annotation.Nonnull
 *  net.java.ao.DBParam
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 *  org.springframework.stereotype.Component
 */
package com.atlassian.mywork.host.dao.ao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.usercompatibility.UserCompatibilityHelper;
import com.atlassian.mywork.host.dao.UserDao;
import com.atlassian.mywork.host.dao.ao.AOUser;
import com.atlassian.mywork.host.dao.ao.AOUserApplicationLink;
import com.atlassian.mywork.host.dao.ao.AbstractAODao;
import com.atlassian.mywork.host.service.TaskOrder;
import com.atlassian.sal.usercompatibility.UserKey;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.Date;
import javax.annotation.Nonnull;
import net.java.ao.DBParam;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.springframework.stereotype.Component;

@Component
public class AOUserDao
extends AbstractAODao<AOUser, Long>
implements UserDao {
    public AOUserDao(ActiveObjects ao) {
        super(AOUser.class, ao);
    }

    @Override
    public long getLastReadNotificationId(String username) {
        AOUser user = (AOUser)this.findOnly(Query.select().where("USERNAME = ?", new Object[]{UserCompatibilityHelper.getStringKeyForUsername(username)}));
        return user != null ? user.getLastReadNotificationId() : 0L;
    }

    @Override
    private AOUser getAO(UserKey userKey) {
        AOUser user = (AOUser)this.findOnly(Query.select().where("USERNAME = ?", new Object[]{userKey.getStringValue()}));
        if (user == null) {
            user = (AOUser)this.ao.create(AOUser.class, new DBParam[]{new DBParam("USERNAME", (Object)userKey.getStringValue())});
            user.setCreated(new Date());
            user.setUpdated(user.getCreated());
        } else {
            user.setUpdated(new Date());
        }
        return user;
    }

    @Override
    public void setLastReadNotificationId(String username, Long notificationId) {
        AOUser user = this.getAO(UserCompatibilityHelper.getKeyForUsername(username));
        user.setLastReadNotificationId(notificationId);
        user.save();
    }

    @Override
    public TaskOrder getTaskOrdering(String username) {
        AOUser aoUser = (AOUser)this.findOnly(Query.select().where("USERNAME = ?", new Object[]{UserCompatibilityHelper.getStringKeyForUsername(username)}));
        return aoUser != null ? new TaskOrder(aoUser.getTaskOrdering()) : new TaskOrder(null);
    }

    @Override
    public void setTaskOrdering(String username, TaskOrder taskOrdering) {
        AOUser user = this.getAO(UserCompatibilityHelper.getKeyForUsername(username));
        user.setTaskOrdering(taskOrdering.getString());
        user.save();
    }

    @Override
    public void delete(@Nonnull UserKey userKey) {
        AOUser user = (AOUser)this.findOnly(Query.select().where("USERNAME = ?", new Object[]{userKey.getStringValue()}));
        if (user != null) {
            this.ao.delete(this.ao.find(AOUserApplicationLink.class, Query.select().where("USER_ID = ?", new Object[]{user.getId()})));
            this.ao.delete(new RawEntity[]{user});
        }
    }

    @Override
    public int deleteRemovedUsers() {
        ArrayList<UserKey> userKeys = new ArrayList<UserKey>();
        int offset = 0;
        Query query = Query.select((String)"ID, USERNAME").offset(offset).limit(1000);
        AOUser[] aoUsers = (AOUser[])this.ao.find(AOUser.class, query);
        while (aoUsers.length > 0) {
            for (AOUser aoUser : aoUsers) {
                User user = UserCompatibilityHelper.getUserForKey(aoUser.getUserKey());
                if (user != null) continue;
                userKeys.add(new UserKey(aoUser.getUserKey()));
            }
            query.offset(offset += aoUsers.length);
            aoUsers = (AOUser[])this.ao.find(AOUser.class, query);
        }
        for (UserKey userKey : userKeys) {
            this.delete(userKey);
        }
        return userKeys.size();
    }
}

