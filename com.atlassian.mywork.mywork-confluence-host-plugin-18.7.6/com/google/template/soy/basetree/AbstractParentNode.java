/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.basetree;

import com.google.template.soy.basetree.AbstractNode;
import com.google.template.soy.basetree.MixinParentNode;
import com.google.template.soy.basetree.Node;
import com.google.template.soy.basetree.ParentNode;
import java.util.List;

public abstract class AbstractParentNode<N extends Node>
extends AbstractNode
implements ParentNode<N> {
    private final MixinParentNode<N> parentMixin;

    public AbstractParentNode() {
        this.parentMixin = new MixinParentNode(this);
    }

    protected AbstractParentNode(AbstractParentNode<N> orig) {
        super(orig);
        this.parentMixin = new MixinParentNode<N>(orig.parentMixin, this);
    }

    @Override
    public int numChildren() {
        return this.parentMixin.numChildren();
    }

    @Override
    public N getChild(int index) {
        return this.parentMixin.getChild(index);
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

