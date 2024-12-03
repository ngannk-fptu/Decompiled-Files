/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.node.JsonNodeFactory
 *  org.codehaus.jackson.node.ObjectNode
 */
package com.atlassian.mywork.model;

import com.atlassian.mywork.model.ApplicationLinkIdBuilder;
import com.atlassian.mywork.model.Item;
import com.atlassian.mywork.model.Status;
import com.atlassian.mywork.model.Task;
import com.atlassian.mywork.util.JsonHelper;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

public class TaskBuilder
implements ApplicationLinkIdBuilder<TaskBuilder> {
    private long id;
    private String applicationLinkId;
    private String title;
    private String user;
    private String notes;
    private Status status;
    private long created;
    private long updated;
    private String globalId;
    private String iconUrl;
    private String url;
    private String itemTitle;
    private String application;
    private String entity;
    private ObjectNode metadata;

    public TaskBuilder() {
    }

    public TaskBuilder(Task task) {
        this.id = task.getId();
        this.applicationLinkId = task.getApplicationLinkId();
        this.title = task.getTitle();
        this.user = task.getUser();
        this.notes = task.getNotes();
        this.status = task.getStatus();
        this.created = task.getCreated();
        this.updated = task.getUpdated();
        this.globalId = task.getGlobalId();
        this.metadata = task.getMetadata();
        this.iconUrl = task.getItem().getIconUrl();
        this.itemTitle = task.getItem().getTitle();
        this.url = task.getItem().getUrl();
        this.application = task.getApplication();
        this.entity = task.getEntity();
    }

    public TaskBuilder id(long id) {
        this.id = id;
        return this;
    }

    @Override
    public String getApplicationLinkId() {
        return this.applicationLinkId;
    }

    @Override
    public TaskBuilder applicationLinkId(String applicationLinkId) {
        this.applicationLinkId = applicationLinkId;
        return this;
    }

    public TaskBuilder title(String title) {
        this.title = title;
        return this;
    }

    public TaskBuilder user(String user) {
        this.user = user;
        return this;
    }

    public TaskBuilder notes(String notes) {
        this.notes = notes;
        return this;
    }

    public TaskBuilder status(Status status) {
        this.status = status;
        return this;
    }

    public TaskBuilder created(long created) {
        this.created = created;
        return this;
    }

    public TaskBuilder updated(long updated) {
        this.updated = updated;
        return this;
    }

    public TaskBuilder globalId(String globalId) {
        this.globalId = globalId;
        return this;
    }

    public TaskBuilder iconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
        return this;
    }

    public TaskBuilder itemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
        return this;
    }

    public TaskBuilder url(String url) {
        this.url = url;
        return this;
    }

    public TaskBuilder application(String application) {
        this.application = application;
        return this;
    }

    public TaskBuilder entity(String entity) {
        this.entity = entity;
        return this;
    }

    public TaskBuilder metadata(ObjectNode metadata) {
        this.metadata = metadata;
        return this;
    }

    public TaskBuilder metadata(String jsonMetadata) {
        this.metadata = JsonHelper.parseObject(jsonMetadata);
        return this;
    }

    public Task createTask() {
        ObjectNode metadata = this.metadata != null ? JsonHelper.copy(this.metadata) : JsonNodeFactory.instance.objectNode();
        Item item = new Item(this.iconUrl, this.itemTitle, this.url);
        return new Task(this.id, this.applicationLinkId, this.title, this.user, this.notes, this.status, this.application, this.entity, this.created, this.updated, this.globalId, metadata, item);
    }
}

