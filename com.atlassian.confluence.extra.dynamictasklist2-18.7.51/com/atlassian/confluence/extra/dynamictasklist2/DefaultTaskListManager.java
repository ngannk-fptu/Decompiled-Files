/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.dynamictasklist2;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.dynamictasklist2.TaskListManager;
import com.atlassian.confluence.extra.dynamictasklist2.dao.WikiFormatTaskListDao;
import com.atlassian.confluence.extra.dynamictasklist2.model.TaskList;
import com.atlassian.confluence.extra.dynamictasklist2.model.TaskListId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultTaskListManager
implements TaskListManager {
    private final WikiFormatTaskListDao wikiFormatTaskListDao;

    @Autowired
    public DefaultTaskListManager(WikiFormatTaskListDao wikiFormatTaskListDao) {
        this.wikiFormatTaskListDao = wikiFormatTaskListDao;
    }

    @Override
    public TaskList getTaskList(ContentEntityObject contentObject, String listId) {
        return this.getTaskList(contentObject, new TaskListId(listId));
    }

    @Override
    public TaskList getTaskList(ContentEntityObject contentObject, TaskListId listId) {
        return this.getTaskListWithNameFromContent(contentObject, listId.getListName(), listId.getOccurrence());
    }

    @Override
    public TaskList getTaskListWithNameFromContent(ContentEntityObject contentObject, String listName, int occurance) {
        return this.wikiFormatTaskListDao.getTaskList(listName, occurance, contentObject);
    }

    @Override
    public void saveTaskList(ContentEntityObject contentObject, TaskList list, String comment) {
        this.wikiFormatTaskListDao.save(list, contentObject, comment);
    }
}

