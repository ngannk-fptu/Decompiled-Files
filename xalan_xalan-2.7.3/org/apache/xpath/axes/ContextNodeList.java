/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.axes;

import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

public interface ContextNodeList {
    public Node getCurrentNode();

    public int getCurrentPos();

    public void reset();

    public void setShouldCacheNodes(boolean var1);

    public void runTo(int var1);

    public void setCurrentPos(int var1);

    public int size();

    public boolean isFresh();

    public NodeIterator cloneWithReset() throws CloneNotSupportedException;

    public Object clone() throws CloneNotSupportedException;

    public int getLast();

    public void setLast(int var1);
}

