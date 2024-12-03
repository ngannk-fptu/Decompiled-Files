/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.Container;
import java.util.Set;

@Deprecated
public class JiraProjectContainer
extends Container {
    private static final String CONTAINER_TYPE = "JiraProject";
    private final Set<String> keys;

    public JiraProjectContainer(Set<String> keys) {
        this.keys = keys;
    }

    public Set<String> getKeys() {
        return this.keys;
    }

    @Override
    public String getType() {
        return CONTAINER_TYPE;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        JiraProjectContainer that = (JiraProjectContainer)o;
        return this.keys != null ? this.keys.equals(that.keys) : that.keys == null;
    }

    public int hashCode() {
        return this.keys != null ? this.keys.hashCode() : 0;
    }
}

