/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.google.template.soy.basetree;

import com.google.common.collect.Lists;
import com.google.template.soy.basetree.Node;
import com.google.template.soy.basetree.ParentNode;
import java.util.ArrayList;
import java.util.List;

public final class MixinParentNode<N extends Node> {
    protected static final String SPACES = "                                        ";
    private final ParentNode<N> master;
    private final List<N> children;
    private Boolean needsEnvFrameDuringInterp;

    public MixinParentNode(ParentNode<N> master) {
        this.master = master;
        this.needsEnvFrameDuringInterp = null;
        this.children = Lists.newArrayList();
    }

    public MixinParentNode(MixinParentNode<N> orig, ParentNode<N> newMaster) {
        this.master = newMaster;
        this.needsEnvFrameDuringInterp = orig.needsEnvFrameDuringInterp;
        this.children = Lists.newArrayListWithCapacity((int)orig.children.size());
        for (Node origChild : orig.children) {
            Node newChild = origChild.clone();
            this.children.add(newChild);
            newChild.setParent(this.master);
        }
    }

    public void setNeedsEnvFrameDuringInterp(Boolean needsEnvFrameDuringInterp) {
        this.needsEnvFrameDuringInterp = needsEnvFrameDuringInterp;
    }

    public Boolean needsEnvFrameDuringInterp() {
        return this.needsEnvFrameDuringInterp;
    }

    public int numChildren() {
        return this.children.size();
    }

    public N getChild(int index) {
        return (N)((Node)this.children.get(index));
    }

    public int getChildIndex(N child) {
        return this.children.indexOf(child);
    }

    public List<N> getChildren() {
        return this.children;
    }

    public void addChild(N child) {
        this.children.add(child);
        child.setParent(this.master);
    }

    public void addChild(int index, N child) {
        this.children.add(index, child);
        child.setParent(this.master);
    }

    public void removeChild(int index) {
        Node child = (Node)this.children.remove(index);
        child.setParent(null);
    }

    public void removeChild(N child) {
        this.children.remove(child);
        child.setParent(null);
    }

    public void replaceChild(int index, N newChild) {
        Node oldChild = (Node)this.children.set(index, newChild);
        oldChild.setParent(null);
        newChild.setParent(this.master);
    }

    public void replaceChild(N currChild, N newChild) {
        this.replaceChild((N)this.getChildIndex(currChild), newChild);
    }

    public void clearChildren() {
        this.children.clear();
    }

    public void addChildren(List<? extends N> children) {
        for (Node child : children) {
            this.addChild(child);
        }
    }

    public void addChildren(int index, List<? extends N> children) {
        ArrayList origChildren = Lists.newArrayList(this.children);
        int origNumChildren = this.children.size();
        for (int i = origNumChildren - 1; i >= index; --i) {
            this.removeChild(i);
        }
        this.addChildren(children);
        this.addChildren(origChildren.subList(index, origNumChildren));
    }

    public void appendSourceStringForChildren(StringBuilder sb) {
        for (Node child : this.children) {
            sb.append(child.toSourceString());
        }
    }

    public void appendTreeStringForChildren(StringBuilder sb, int indent) {
        for (Node child : this.children) {
            sb.append(child.toTreeString(indent + 3));
        }
    }

    public String toTreeString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(SPACES.substring(0, indent)).append("[").append(this.master.toString()).append("]\n");
        this.appendTreeStringForChildren(sb, indent);
        return sb.toString();
    }
}

