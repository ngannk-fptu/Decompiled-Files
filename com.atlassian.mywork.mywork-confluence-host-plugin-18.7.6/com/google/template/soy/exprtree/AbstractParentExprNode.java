/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.exprtree;

import com.google.template.soy.basetree.MixinParentNode;
import com.google.template.soy.exprtree.AbstractExprNode;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.types.SoyType;
import java.util.List;

public abstract class AbstractParentExprNode
extends AbstractExprNode
implements ExprNode.ParentExprNode {
    private final MixinParentNode<ExprNode> parentMixin;
    private SoyType type;

    protected AbstractParentExprNode() {
        this((SoyType)null);
    }

    protected AbstractParentExprNode(SoyType type) {
        this.parentMixin = new MixinParentNode<ExprNode>(this);
        this.type = type;
    }

    protected AbstractParentExprNode(AbstractParentExprNode orig) {
        super(orig);
        this.parentMixin = new MixinParentNode<ExprNode>(orig.parentMixin, this);
        this.type = orig.type;
    }

    @Override
    public SoyType getType() {
        return this.type;
    }

    public void setType(SoyType type) {
        this.type = type;
    }

    @Override
    public int numChildren() {
        return this.parentMixin.numChildren();
    }

    @Override
    public ExprNode getChild(int index) {
        return this.parentMixin.getChild(index);
    }

    @Override
    public int getChildIndex(ExprNode child) {
        return this.parentMixin.getChildIndex(child);
    }

    @Override
    public List<ExprNode> getChildren() {
        return this.parentMixin.getChildren();
    }

    @Override
    public void addChild(ExprNode child) {
        this.parentMixin.addChild(child);
    }

    @Override
    public void addChild(int index, ExprNode child) {
        this.parentMixin.addChild(index, child);
    }

    @Override
    public void removeChild(int index) {
        this.parentMixin.removeChild(index);
    }

    @Override
    public void removeChild(ExprNode child) {
        this.parentMixin.removeChild(child);
    }

    @Override
    public void replaceChild(int index, ExprNode newChild) {
        this.parentMixin.replaceChild((ExprNode)index, newChild);
    }

    @Override
    public void replaceChild(ExprNode currChild, ExprNode newChild) {
        this.parentMixin.replaceChild(currChild, newChild);
    }

    @Override
    public void clearChildren() {
        this.parentMixin.clearChildren();
    }

    @Override
    public void addChildren(List<? extends ExprNode> children) {
        this.parentMixin.addChildren(children);
    }

    @Override
    public void addChildren(int index, List<? extends ExprNode> children) {
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

