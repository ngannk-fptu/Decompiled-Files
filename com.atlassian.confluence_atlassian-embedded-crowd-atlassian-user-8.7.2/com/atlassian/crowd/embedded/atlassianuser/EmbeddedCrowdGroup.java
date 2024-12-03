/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Group
 *  com.atlassian.user.Group
 */
package com.atlassian.crowd.embedded.atlassianuser;

import com.atlassian.crowd.embedded.api.Group;

@Deprecated
public class EmbeddedCrowdGroup
implements com.atlassian.user.Group,
Group {
    private final String name;

    EmbeddedCrowdGroup(Group crowdGroup) {
        this.name = crowdGroup.getName();
    }

    public String getName() {
        return this.name;
    }

    public int compareTo(Group group) {
        return this.name.compareToIgnoreCase(group.getName());
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        EmbeddedCrowdGroup that = (EmbeddedCrowdGroup)o;
        return !(this.name != null ? !this.name.equals(that.name) : that.name != null);
    }

    public int hashCode() {
        return this.name != null ? this.name.hashCode() : 0;
    }
}

