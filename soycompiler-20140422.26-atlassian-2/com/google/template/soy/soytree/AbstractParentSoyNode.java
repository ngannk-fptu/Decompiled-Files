/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.soytree;

import com.google.template.soy.basetree.MixinParentNode;
import com.google.template.soy.soytree.AbstractSoyNode;
import com.google.template.soy.soytree.SoyNode;
import java.util.List;

public abstract class AbstractParentSoyNode<N extends SoyNode>
extends AbstractSoyNode
implements SoyNode.ParentSoyNode<N> {
    private final MixinParentNode<N> parentMixin;

    public AbstractParentSoyNode(int id) {
        super(id);
        this.parentMixin = new MixinParentNode(this);
    }

    protected AbstractParentSoyNode(AbstractParentSoyNode<N> orig) {
        super(orig);
        this.parentMixin = new MixinParentNode<N>(orig.parentMixin, this);
    }

    @Override
    public void setNeedsEnvFrameDuringInterp(Boolean needsEnvFrameDuringInterp) {
        this.parentMixin.setNeedsEnvFrameDuringInterp(needsEnvFrameDuringInterp);
    }

    @Override
    public Boolean needsEnvFrameDuringInterp() {
        return this.parentMixin.needsEnvFrameDuringInterp();
    }

    @Override
    public int numChildren() {
        return this.parentMixin.numChildren();
    }

    @Override
    public N getChild(int index) {
        return (N)((SoyNode)this.parentMixin.getChild(index));
    }

    @Override
    public int getChildIndex(N child) {
        return this.parentMixin.getChildIndex(child);
    }

    @Override
    public List<N> getChildren() {
        return this.parentMixin.getChildren();
    }

    @Override
    public void addChild(N child) {
        this.parentMixin.addChild(child);
    }

    @Override
    public void addChild(int index, N child) {
        this.parentMixin.addChild(index, child);
    }

    @Override
    public void removeChild(int index) {
        this.parentMixin.removeChild(index);
    }

    @Override
    public void removeChild(N child) {
        this.parentMixin.removeChild(child);
    }

    @Override
    public void replaceChild(int index, N newChild) {
        this.parentMixin.replaceChild(index, (int)newChild);
    }

    @Override
    public void replaceChild(N currChild, N newChild) {
        this.parentMixin.replaceChild(currChild, newChild);
    }

    @Override
    public void clearChildren() {
        this.parentMixin.clearChildren();
    }

    @Override
    public void addChildren(List<? extends N> children) {
        this.parentMixin.addChildren(children);
    }

    @Override
    public void addChildren(int index, List<? extends N> children) {
        this.parentMixin.addChildren(index, children);
    }

    @Override
    public void appendSourceStringForChildren(StringBuilder sb) {
        this.parentMixin.appendSourceStringForChildren(sb);
    }

    @Override
    public void appendTreeStringForChildren(StringBuilder sb, int indent) {
        this.parentMixin.appendTreeStringForChildren(sb, indent);
    }

    @Override
    public String toTreeString(int indent) {
        return this.parentMixin.toTreeString(indent);
    }
}

