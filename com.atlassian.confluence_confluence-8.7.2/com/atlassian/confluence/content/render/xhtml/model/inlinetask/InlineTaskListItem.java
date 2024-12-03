/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.model.inlinetask;

public class InlineTaskListItem {
    private final String id;
    private final boolean completed;
    private final String body;

    public InlineTaskListItem(String id, boolean completed, String body) {
        this.id = id;
        this.completed = completed;
        this.body = body;
    }

    public String getId() {
        return this.id;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    public String getBody() {
        return this.body;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InlineTaskListItem)) {
            return false;
        }
        InlineTaskListItem that = (InlineTaskListItem)o;
        if (this.completed != that.completed) {
            return false;
        }
        if (this.id != null ? !this.id.equals(that.id) : that.id != null) {
            return false;
        }
        return !(this.body != null ? !this.body.equals(that.body) : that.body != null);
    }

    public int hashCode() {
        int result = this.id != null ? this.id.hashCode() : 0;
        result = 31 * result + (this.completed ? 1 : 0);
        result = 31 * result + (this.body != null ? this.body.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "InlineTaskListItem{id='" + this.id + "', completed=" + this.completed + ", body='" + this.body + "'}";
    }
}

