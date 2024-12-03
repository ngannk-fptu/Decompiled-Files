/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.fugue.Pair
 *  com.atlassian.mywork.model.Item
 *  com.atlassian.mywork.model.Status
 *  com.atlassian.mywork.model.Task
 *  com.atlassian.user.User
 *  com.google.common.base.Supplier
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 *  net.java.ao.DBParam
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 *  org.springframework.stereotype.Component
 */
package com.atlassian.mywork.host.dao.ao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.usercompatibility.UserCompatibilityHelper;
import com.atlassian.fugue.Pair;
import com.atlassian.mywork.host.dao.TaskDao;
import com.atlassian.mywork.host.dao.ao.AOTask;
import com.atlassian.mywork.host.dao.ao.AbstractAODao;
import com.atlassian.mywork.host.dao.ao.DateUtil;
import com.atlassian.mywork.model.Item;
import com.atlassian.mywork.model.Status;
import com.atlassian.mywork.model.Task;
import com.atlassian.sal.usercompatibility.UserKey;
import com.atlassian.user.User;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Date;
import javax.annotation.Nonnull;
import net.java.ao.DBParam;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.springframework.stereotype.Component;

@Component
public class AOTaskDao
extends AbstractAODao<AOTask, Long>
implements TaskDao {
    public AOTaskDao(ActiveObjects ao) {
        super(AOTask.class, ao);
    }

    @Override
    public Task get(long id) {
        return this.asTask((AOTask)this.getAO(id));
    }

    @Override
    public Iterable<Task> findAll(String username) {
        ArrayList tasks = Lists.newArrayList();
        for (AOTask aoTask : (AOTask[])this.ao.find(AOTask.class, Query.select().where("USER = ?", new Object[]{UserCompatibilityHelper.getStringKeyForUsername(username)}))) {
            tasks.add(this.asTask(aoTask));
        }
        return tasks;
    }

    @Override
    public Task find(String username, String globalId) {
        return this.asTask((AOTask)this.findOnly(Query.select().where("USER = ? AND GLOBAL_ID = ?", new Object[]{UserCompatibilityHelper.getStringKeyForUsername(username), globalId})));
    }

    @Override
    public Pair<Boolean, Task> createOrUpdate(Task task) {
        AOTask aoTask;
        boolean created;
        String globalId = task.getGlobalId();
        if (globalId != null) {
            AOTask existingAOTask = (AOTask)this.findOnly(Query.select().where("USER = ? AND GLOBAL_ID = ?", new Object[]{UserCompatibilityHelper.getStringKeyForUsername(task.getUser()), globalId}));
            if (existingAOTask != null) {
                created = false;
                aoTask = existingAOTask;
                this.updateAO(aoTask, task);
            } else {
                created = true;
                aoTask = this.createAsLast(task);
            }
        } else {
            created = true;
            aoTask = this.createAsLast(task);
        }
        aoTask.save();
        return Pair.pair((Object)created, (Object)this.asTask(aoTask));
    }

    @Override
    public Task update(Task checkedTask) {
        AOTask aoTask = (AOTask)this.getAO(checkedTask.getId());
        this.updateAO(aoTask, checkedTask);
        aoTask.save();
        return this.asTask(aoTask);
    }

    @Override
    public Task updateNotes(long id, String notes) {
        AOTask aoTask = (AOTask)this.getAO(id);
        aoTask.setDescription(notes);
        aoTask.save();
        return this.asTask(aoTask);
    }

    private AOTask createAsLast(Task task) {
        AOTask aoTask = (AOTask)this.ao.create(AOTask.class, new DBParam[0]);
        this.updateAO(aoTask, task);
        aoTask.setDescription(task.getNotes());
        aoTask.setCreated(aoTask.getUpdated());
        return aoTask;
    }

    @Override
    public Task delete(String username, String globalId) {
        return this.delete(username, (AOTask)this.findOnly(Query.select().where("USER = ? AND GLOBAL_ID = ?", new Object[]{UserCompatibilityHelper.getStringKeyForUsername(username), globalId})));
    }

    @Override
    public Task delete(String username, long id) {
        return this.delete(username, (AOTask)this.getAO(id));
    }

    private Task delete(String username, AOTask aoTask) {
        if (aoTask == null) {
            return null;
        }
        Task deletedTask = this.asTask(aoTask);
        if (!username.equals(deletedTask.getUser())) {
            return null;
        }
        this.ao.delete(new RawEntity[]{aoTask});
        return deletedTask;
    }

    @Override
    public int deleteAll(@Nonnull UserKey userKey) {
        return this.delete((Supplier<Query>)((Supplier)() -> Query.select().where("USER = ?", new Object[]{userKey.getStringValue()})));
    }

    @Override
    public boolean hasTasksToMigrate(String username) {
        int count = this.ao.count(AOTask.class, Query.select((String)"1").limit(1).where("USER = ? AND ((ENTITY = ? AND STATUS = ?) OR (ENTITY = ? AND STATUS = ? AND DESCRIPTION IS NOT NULL))", new Object[]{UserCompatibilityHelper.getStringKeyForUsername(username), "notes", Status.TODO, "inline-task", Status.TODO}));
        return count > 0;
    }

    @Override
    public Iterable<Task> findAllTasksToMigrate(String username) {
        ArrayList tasks = Lists.newArrayList();
        Query query = Query.select().where("USER = ? AND ((ENTITY = ? AND STATUS = ?) OR (ENTITY = ? AND STATUS = ? AND DESCRIPTION IS NOT NULL))", new Object[]{UserCompatibilityHelper.getStringKeyForUsername(username), "notes", Status.TODO, "inline-task", Status.TODO});
        for (AOTask aoTask : (AOTask[])this.ao.find(AOTask.class, query)) {
            tasks.add(this.asTask(aoTask));
        }
        return tasks;
    }

    @Override
    public Iterable<Task> findAllTasksByEntity(String username, String type) {
        ArrayList tasks = Lists.newArrayList();
        for (AOTask aoTask : (AOTask[])this.ao.find(AOTask.class, Query.select().where("USER = ? AND ENTITY = ?", new Object[]{UserCompatibilityHelper.getStringKeyForUsername(username), type}))) {
            tasks.add(this.asTask(aoTask));
        }
        return tasks;
    }

    @Override
    public int deleteOldCompletedTasks(int days) {
        return this.delete((Supplier<Query>)((Supplier)() -> Query.select().where("STATUS = ? AND UPDATED < ?", new Object[]{Status.DONE, DateUtil.getNoDaysAgo(days)})));
    }

    @Override
    public int deleteExpiredTasks(int days) {
        return this.delete((Supplier<Query>)((Supplier)() -> Query.select().where("UPDATED < ? or CREATED < ?", new Object[]{DateUtil.getNoDaysAgo(days), DateUtil.getNoDaysAgo(days)})));
    }

    private Task asTask(AOTask ao) {
        if (ao == null) {
            return null;
        }
        return new Task(ao.getId(), ao.getApplicationLinkId(), ao.getTitle(), this.getUsername(ao.getUserKey()), ao.getDescription(), ao.getStatus(), ao.getApplication(), ao.getEntity(), ao.getCreated().getTime(), ao.getUpdated().getTime(), ao.getGlobalId(), AOTaskDao.toObjectNode(ao.getMetadata()), new Item(ao.getItemIconUrl(), ao.getItemTitle(), ao.getUrl()));
    }

    private void updateAO(AOTask aoTask, Task task) {
        aoTask.setApplicationLinkId(task.getApplicationLinkId());
        aoTask.setTitle(task.getTitle());
        aoTask.setUserKey(UserCompatibilityHelper.getStringKeyForUsername(task.getUser()));
        aoTask.setStatus(task.getStatus());
        aoTask.setApplication(task.getApplication());
        aoTask.setEntity(task.getEntity());
        aoTask.setGlobalId(task.getGlobalId());
        aoTask.setMetadata(task.getMetadata().toString());
        aoTask.setItemIconUrl(task.getItem().getIconUrl());
        aoTask.setItemTitle(task.getItem().getTitle());
        aoTask.setUrl(task.getItem().getUrl());
        aoTask.setUpdated(new Date());
    }

    private String getUsername(String userKey) {
        User user = UserCompatibilityHelper.getUserForKey(userKey);
        return user != null ? user.getName() : null;
    }
}

