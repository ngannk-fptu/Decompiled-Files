/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.jrcs.diff.myers;

import org.apache.commons.jrcs.diff.myers.PathNode;

public final class DiffNode
extends PathNode {
    public DiffNode(int i, int j, PathNode prev) {
        super(i, j, prev == null ? null : prev.previousSnake());
    }

    public boolean isSnake() {
        return false;
    }
}

