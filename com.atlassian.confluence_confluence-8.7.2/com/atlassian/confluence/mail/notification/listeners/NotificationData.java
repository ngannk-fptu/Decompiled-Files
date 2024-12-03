/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.google.common.collect.Sets
 *  javax.activation.DataSource
 */
package com.atlassian.confluence.mail.notification.listeners;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.activation.DataSource;

public class NotificationData {
    private final NotificationContext context = new NotificationContext();
    private String templateName;
    private String subject;
    private final User modifier;
    private final List<String> doNotNotifyAgain = new ArrayList<String>();
    private final ConfluenceEntityObject permissionEntity;
    private Set<DataSource> templateImageDataSources = Sets.newHashSet();

    public NotificationData(User modifier, boolean shouldNotifyOnOwnActions, ConfluenceEntityObject permissionEntity) {
        this.modifier = modifier;
        this.permissionEntity = permissionEntity;
        if (modifier != null) {
            this.context.put("modifier", modifier);
            this.context.setActor(modifier);
            if (!shouldNotifyOnOwnActions) {
                this.doNotNotifyAgain.add(modifier.getName());
            }
        }
    }

    public NotificationData(User modifier, boolean shouldNotifyOnOwnActions, ConfluenceEntityObject permissionEntity, String subject, String templateName, Collection<DataSource> templateImageDataSources) {
        this(modifier, shouldNotifyOnOwnActions, permissionEntity);
        this.subject = subject;
        this.templateName = templateName;
        if (templateImageDataSources != null) {
            this.templateImageDataSources.addAll(templateImageDataSources);
        }
    }

    public void addToContext(String key, Serializable value) {
        this.context.put(key, value);
    }

    public void addAllToContext(Map<String, Serializable> map) {
        this.context.putAll(map);
    }

    public String getTemplateName() {
        return this.templateName;
    }

    public String getSubject() {
        return this.subject;
    }

    public User getModifier() {
        return this.modifier;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public boolean doNotNotifyAgain(String userName) {
        return this.doNotNotifyAgain.contains(userName);
    }

    public void addDoNotNotifyAgain(String userName) {
        this.doNotNotifyAgain.add(userName);
    }

    public ConfluenceEntityObject getPermissionEntity() {
        return this.permissionEntity;
    }

    public String toString() {
        return "NotificationData: templateName='" + this.templateName + "'";
    }

    public void addTemplateImage(DataSource dataSource) {
        this.templateImageDataSources.add(dataSource);
    }

    public NotificationContext cloneContext() {
        return new NotificationContext(this.context, this.templateImageDataSources);
    }

    public NotificationContext cloneContextForRecipient(ConfluenceUser recipient) {
        NotificationContext clone = this.cloneContext();
        clone.setRecipient(recipient);
        return clone;
    }

    public NotificationContext getCommonContext() {
        return this.context;
    }
}

