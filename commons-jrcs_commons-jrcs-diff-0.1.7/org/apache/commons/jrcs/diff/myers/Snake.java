/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.jrcs.diff.myers;

import org.apache.commons.jrcs.diff.myers.PathNode;

public final class Snake
extends PathNode {
    public Snake(int i, int j, PathNode prev) {
        super(i, j, prev);
    }

    public boolean isSnake() {
        return true;
    }
}

