/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.flat;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.commons.flat.Sequence;

public interface NodeSequence
extends Sequence<Node> {
    public Node addNode(String var1, String var2) throws RepositoryException;

    public void removeNode(String var1) throws RepositoryException;
}

