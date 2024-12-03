/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.api.UserWithAttributes
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.google.common.base.MoreObjects
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.model.user;

import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserTemplate;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.google.common.base.MoreObjects;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public class UserTemplateWithAttributes
extends UserTemplate
implements UserWithAttributes {
    private final Map<String, Set<String>> attributes = new HashMap<String, Set<String>>();

    public UserTemplateWithAttributes(String username, long directoryId) {
        super(username, directoryId);
    }

    public UserTemplateWithAttributes(UserWithAttributes user) {
        super((User)user);
        for (String key : user.getKeys()) {
            Set values = user.getValues(key);
            if (values != null) {
                this.attributes.put(key, new HashSet(values));
                continue;
            }
            throw new ConcurrentModificationException("user attributes have changed");
        }
    }

    @Deprecated
    public static UserTemplateWithAttributes ofUserWithNoAttributes(User user) {
        return UserTemplateWithAttributes.toUserWithNoAttributes(user);
    }

    public UserTemplateWithAttributes(com.atlassian.crowd.embedded.api.UserWithAttributes user) {
        super((com.atlassian.crowd.embedded.api.User)user);
        for (String key : user.getKeys()) {
            this.attributes.put(key, new HashSet(user.getValues(key)));
        }
    }

    protected UserTemplateWithAttributes(User user) {
        super(user);
    }

    protected UserTemplateWithAttributes(com.atlassian.crowd.embedded.api.User user) {
        super(user);
    }

    public static UserTemplateWithAttributes toUserWithNoAttributes(User user) {
        return new UserTemplateWithAttributes(user);
    }

    public static UserTemplateWithAttributes toUserWithNoAttributes(com.atlassian.crowd.embedded.api.User user) {
        return new UserTemplateWithAttributes(user);
    }

    public Map<String, Set<String>> getAttributes() {
        return this.attributes;
    }

    @Nullable
    public Set<String> getValues(String name) {
        return this.attributes.get(name);
    }

    @Nullable
    public String getValue(String name) {
        Set<String> values = this.getValues(name);
        if (values != null && !values.isEmpty()) {
            return values.iterator().next();
        }
        return null;
    }

    public Set<String> getKeys() {
        return this.attributes.keySet();
    }

    public boolean isEmpty() {
        return this.attributes.isEmpty();
    }

    public void setAttribute(String name, String value) {
        this.attributes.put(name, Collections.singleton(value));
    }

    public void setAttribute(String name, Set<String> values) {
        this.attributes.put(name, values);
    }

    public void removeAttribute(String name) {
        this.attributes.remove(name);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("name", (Object)this.getName()).add("directoryId", this.getDirectoryId()).add("active", this.isActive()).add("emailAddress", (Object)this.getEmailAddress()).add("firstName", (Object)this.getFirstName()).add("lastName", (Object)this.getLastName()).add("displayName", (Object)this.getDisplayName()).add("externalId", (Object)this.getExternalId()).add("attributes", this.getAttributes()).toString();
    }
}

