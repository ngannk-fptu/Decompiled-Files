/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.nodetype;

import javax.jcr.RangeIterator;
import javax.jcr.nodetype.NodeType;

public interface NodeTypeIterator
extends RangeIterator {
    public NodeType nextNodeType();
}

