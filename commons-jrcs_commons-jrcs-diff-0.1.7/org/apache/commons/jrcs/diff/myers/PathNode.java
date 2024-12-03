/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.jrcs.diff.myers;

public abstract class PathNode {
    public final int i;
    public final int j;
    public final PathNode prev;

    public PathNode(int i, int j, PathNode prev) {
        this.i = i;
        this.j = j;
        this.prev = prev;
    }

    public abstract boolean isSnake();

    public boolean isBootstrap() {
        return this.i < 0 || this.j < 0;
    }

    public final PathNode previousSnake() {
        if (this.isBootstrap()) {
            return null;
        }
        if (!this.isSnake() && this.prev != null) {
            return this.prev.previousSnake();
        }
        return this;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("[");
        PathNode node = this;
        while (node != null) {
            buf.append("(");
            buf.append(Integer.toString(node.i));
            buf.append(",");
            buf.append(Integer.toString(node.j));
            buf.append(")");
            node = node.prev;
        }
        buf.append("]");
        return buf.toString();
    }
}

