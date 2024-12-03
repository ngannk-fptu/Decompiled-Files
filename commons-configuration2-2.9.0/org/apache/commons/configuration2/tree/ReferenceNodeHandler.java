/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.tree;

import java.util.List;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.tree.NodeHandler;

public interface ReferenceNodeHandler
extends NodeHandler<ImmutableNode> {
    public Object getReference(ImmutableNode var1);

    public List<Object> removedReferences();
}

