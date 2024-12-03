/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugin.notifications.api.medium;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public final class Group
implements Comparable<Group> {
    @JsonProperty
    private final String id;
    @JsonProperty
    private final String name;

    @JsonCreator
    public Group(@JsonProperty(value="id") String id, @JsonProperty(value="name") String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Group group = (Group)o;
        if (!this.id.equals(group.id)) {
            return false;
        }
        return this.name.equals(group.name);
    }

    public int hashCode() {
        int result = this.id.hashCode();
        result = 31 * result + this.name.hashCode();
        return result;
    }

    @Override
    public int compareTo(Group o) {
        if (o != null) {
            return this.getName().compareTo(o.getName());
        }
        return 0;
    }
}

