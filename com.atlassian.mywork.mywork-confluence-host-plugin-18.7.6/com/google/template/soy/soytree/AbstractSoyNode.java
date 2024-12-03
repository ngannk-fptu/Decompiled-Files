/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.google.template.soy.soytree;

import com.google.common.base.Preconditions;
import com.google.template.soy.base.SourceLocation;
import com.google.template.soy.basetree.AbstractNode;
import com.google.template.soy.soytree.SoyNode;

public abstract class AbstractSoyNode
extends AbstractNode
implements SoyNode {
    private int id;
    private SourceLocation srcLoc;

    protected AbstractSoyNode(int id) {
        this.id = id;
        this.srcLoc = SourceLocation.UNKNOWN;
    }

    protected AbstractSoyNode(AbstractSoyNode orig) {
        super(orig);
        this.id = orig.id;
        this.srcLoc = orig.srcLoc;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public SourceLocation getSourceLocation() {
        return this.srcLoc;
    }

    @Override
    public void setSourceLocation(SourceLocation srcLoc) {
        Preconditions.checkNotNull((Object)srcLoc);
        this.srcLoc = srcLoc;
    }

    @Override
    public SoyNode.ParentSoyNode<?> getParent() {
        return (SoyNode.ParentSoyNode)super.getParent();
    }

    @Override
    public abstract SoyNode clone();

    @Override
    public String toString() {
        return super.toString() + "_" + this.id;
    }
}

