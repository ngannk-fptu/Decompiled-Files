/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.tree;

import java.util.ArrayList;
import java.util.List;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Tree;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;

public abstract class AbstractTree
implements Tree {
    private final Name nodeName;
    private final Name ntName;
    private final String uniqueId;
    private final NamePathResolver resolver;
    private List<Tree> children;

    protected AbstractTree(Name nodeName, Name ntName, String uniqueId, NamePathResolver resolver) {
        this.nodeName = nodeName;
        this.ntName = ntName;
        this.uniqueId = uniqueId;
        this.children = new ArrayList<Tree>();
        this.resolver = resolver;
    }

    protected NamePathResolver getResolver() {
        return this.resolver;
    }

    protected List<Tree> getChildren() {
        return this.children;
    }

    protected abstract Tree createChild(Name var1, Name var2, String var3);

    @Override
    public Name getName() {
        return this.nodeName;
    }

    @Override
    public Name getPrimaryTypeName() {
        return this.ntName;
    }

    @Override
    public String getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public Tree addChild(Name childName, Name primaryTypeName, String uniqueId) {
        Tree child = this.createChild(childName, primaryTypeName, uniqueId);
        this.children.add(child);
        return child;
    }
}

