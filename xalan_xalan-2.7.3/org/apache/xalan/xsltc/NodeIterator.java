/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc;

public interface NodeIterator
extends Cloneable {
    public static final int END = -1;

    public int next();

    public NodeIterator reset();

    public int getLast();

    public int getPosition();

    public void setMark();

    public void gotoMark();

    public NodeIterator setStartNode(int var1);

    public boolean isReverse();

    public NodeIterator cloneIterator();

    public void setRestartable(boolean var1);
}

