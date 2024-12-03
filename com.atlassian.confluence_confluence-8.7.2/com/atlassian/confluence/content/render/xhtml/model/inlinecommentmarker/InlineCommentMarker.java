/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.model.inlinecommentmarker;

import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;

public class InlineCommentMarker {
    private final String ref;
    private final Streamable body;

    public InlineCommentMarker(String ref, Streamable body) {
        this.ref = ref;
        this.body = body;
    }

    public String getRef() {
        return this.ref;
    }

    public Streamable getBodyStream() {
        return this.body;
    }

    public String getBody() {
        return Streamables.writeToString(this.body);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InlineCommentMarker)) {
            return false;
        }
        InlineCommentMarker that = (InlineCommentMarker)o;
        if (this.ref != null ? !this.ref.equals(that.ref) : that.ref != null) {
            return false;
        }
        return !(this.body != null ? !this.body.equals(that.body) : that.body != null);
    }

    public int hashCode() {
        int result = this.ref != null ? this.ref.hashCode() : 0;
        result = 31 * result + (this.body != null ? this.body.hashCode() : 0);
        return result;
    }

    public String toString() {
        return String.format("InlineCommentMarker{ref='%s', body='%s'}", this.ref, this.getBody());
    }
}

