/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.dtm.ref;

import org.apache.xml.dtm.DTMAxisIterator;

public final class EmptyIterator
implements DTMAxisIterator {
    private static final EmptyIterator INSTANCE = new EmptyIterator();

    public static DTMAxisIterator getInstance() {
        return INSTANCE;
    }

    private EmptyIterator() {
    }

    @Override
    public final int next() {
        return -1;
    }

    @Override
    public final DTMAxisIterator reset() {
        return this;
    }

    @Override
    public final int getLast() {
        return 0;
    }

    @Override
    public final int getPosition() {
        return 1;
    }

    @Override
    public final void setMark() {
    }

    @Override
    public final void gotoMark() {
    }

    @Override
    public final DTMAxisIterator setStartNode(int node) {
        return this;
    }

    @Override
    public final int getStartNode() {
        return -1;
    }

    @Override
    public final boolean isReverse() {
        return false;
    }

    @Override
    public final DTMAxisIterator cloneIterator() {
        return this;
    }

    @Override
    public final void setRestartable(boolean isRestartable) {
    }

    @Override
    public final int getNodeByPosition(int position) {
        return -1;
    }
}

