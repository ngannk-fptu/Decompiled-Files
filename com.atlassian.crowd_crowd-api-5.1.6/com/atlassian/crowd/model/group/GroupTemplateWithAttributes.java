/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupType
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.model.group;

import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupTemplate;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public class GroupTemplateWithAttributes
extends GroupTemplate
implements GroupWithAttributes {
    private final Map<String, Set<String>> attributes = new HashMap<String, Set<String>>();

    public GroupTemplateWithAttributes(String groupName, long directoryId, GroupType groupType) {
        super(groupName, directoryId, groupType);
    }

    public GroupTemplateWithAttributes(GroupWithAttributes group) {
        super((Group)group);
        for (String key : group.getKeys()) {
            Set values = group.getValues(key);
            if (values != null) {
                this.attributes.put(key, new HashSet(values));
                continue;
            }
            throw new ConcurrentModificationException("group attributes have changed");
        }
    }

    private GroupTemplateWithAttributes(Group group) {
        super(group);
    }

    public static GroupTemplateWithAttributes ofGroupWithNoAttributes(Group group) {
        return new GroupTemplateWithAttributes(group);
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
        Set<String> vals = this.getValues(name);
        if (vals != null && !vals.isEmpty()) {
            return vals.iterator().next();
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
}

