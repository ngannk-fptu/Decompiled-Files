/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.whisper.plugin.api.UserPropertyManager
 *  javax.inject.Inject
 *  javax.inject.Named
 *  net.java.ao.DBParam
 *  net.java.ao.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.whisper.plugin.impl;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.whisper.plugin.ao.UserPropertyAO;
import com.atlassian.whisper.plugin.api.UserPropertyManager;
import javax.inject.Inject;
import javax.inject.Named;
import net.java.ao.DBParam;
import net.java.ao.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named(value="userPropertyManager")
@ExportAsService
public class DefaultUserPropertyManager
implements UserPropertyManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultUserPropertyManager.class);
    private final ActiveObjects activeObjects;

    @Inject
    public DefaultUserPropertyManager(@ComponentImport ActiveObjects activeObjects) {
        this.activeObjects = activeObjects;
    }

    public void setValue(UserKey user, String key, String value) {
        log.debug("Setting property '{}' for user {} with value {}", new Object[]{key, user.getStringValue(), value});
        this.activeObjects.executeInTransaction(() -> {
            UserPropertyAO[] properties = this.getUserPropertyEntity(user, key);
            if (properties.length == 0) {
                this.activeObjects.create(UserPropertyAO.class, new DBParam[]{new DBParam("USER", (Object)user.getStringValue()), new DBParam("KEY", (Object)key), new DBParam("VALUE", (Object)value)});
            } else {
                properties[0].setValue(value);
                properties[0].save();
            }
            return null;
        });
    }

    public void deleteValue(UserKey user, String key) {
        log.debug("Deleting property '{}' for user {}", (Object)key, (Object)user.getStringValue());
        this.activeObjects.executeInTransaction(() -> this.activeObjects.deleteWithSQL(UserPropertyAO.class, "USER = ? and KEY = ?", new Object[]{user.getStringValue(), key}));
    }

    public String getValue(UserKey user, String key) {
        UserPropertyAO[] properties = (UserPropertyAO[])this.activeObjects.executeInTransaction(() -> this.getUserPropertyEntity(user, key));
        if (properties.length == 0) {
            return null;
        }
        return properties[0].getValue();
    }

    public void clear(UserKey user) {
        log.debug("Deleting all properties for user {}", (Object)user.getStringValue());
        this.activeObjects.executeInTransaction(() -> this.activeObjects.deleteWithSQL(UserPropertyAO.class, "USER = ?", new Object[]{user.getStringValue()}));
    }

    private UserPropertyAO[] getUserPropertyEntity(UserKey user, String key) {
        return (UserPropertyAO[])this.activeObjects.find(UserPropertyAO.class, Query.select().where("USER = ? and KEY = ?", new Object[]{user.getStringValue(), key}));
    }
}

