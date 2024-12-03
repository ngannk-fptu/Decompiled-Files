/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import org.aspectj.weaver.IHasPosition;

public class Position
implements IHasPosition {
    private int start;
    private int end;

    public Position(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public int getEnd() {
        return this.end;
    }

    @Override
    public int getStart() {
        return this.start;
    }
}

