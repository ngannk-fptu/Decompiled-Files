/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.tree;

import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.tree.InMemoryNodeModel;
import org.apache.commons.configuration2.tree.NodeModelSupport;

public interface InMemoryNodeModelSupport
extends NodeModelSupport<ImmutableNode> {
    public InMemoryNodeModel getNodeModel();
}

