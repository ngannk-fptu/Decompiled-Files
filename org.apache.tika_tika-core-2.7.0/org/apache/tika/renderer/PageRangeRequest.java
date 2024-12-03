/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.renderer;

import java.util.Objects;
import org.apache.tika.renderer.RenderRequest;

public class PageRangeRequest
implements RenderRequest {
    public static PageRangeRequest RENDER_ALL = new PageRangeRequest(1, -1);
    private final int from;
    private final int to;

    public PageRangeRequest(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public int getFrom() {
        return this.from;
    }

    public int getTo() {
        return this.to;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PageRangeRequest that = (PageRangeRequest)o;
        return this.from == that.from && this.to == that.to;
    }

    public int hashCode() {
        return Objects.hash(this.from, this.to);
    }
}

