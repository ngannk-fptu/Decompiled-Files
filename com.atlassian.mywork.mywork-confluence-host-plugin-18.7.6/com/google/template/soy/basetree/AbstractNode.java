/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.template.soy.basetree;

import com.google.template.soy.basetree.Node;
import com.google.template.soy.basetree.ParentNode;
import com.google.template.soy.basetree.SyntaxVersion;
import com.google.template.soy.basetree.SyntaxVersionBound;
import javax.annotation.Nullable;

public abstract class AbstractNode
implements Node {
    protected static final String SPACES = "                                        ";
    @Nullable
    private SyntaxVersionBound syntaxVersionBound;
    private ParentNode<?> parent;

    protected AbstractNode() {
        this.syntaxVersionBound = null;
        this.parent = null;
    }

    protected AbstractNode(AbstractNode orig) {
        this.parent = null;
        this.syntaxVersionBound = orig.syntaxVersionBound;
    }

    @Override
    public void maybeSetSyntaxVersionBound(SyntaxVersionBound newSyntaxVersionBound) {
        this.syntaxVersionBound = SyntaxVersionBound.selectLower(this.syntaxVersionBound, newSyntaxVersionBound);
    }

    @Override
    @Nullable
    public SyntaxVersionBound getSyntaxVersionBound() {
        return this.syntaxVersionBound;
    }

    @Override
    public boolean couldHaveSyntaxVersionAtLeast(SyntaxVersion syntaxVersionCutoff) {
        return this.syntaxVersionBound == null || this.syntaxVersionBound.syntaxVersion.num > syntaxVersionCutoff.num;
    }

    @Override
    public void setParent(ParentNode<?> parent) {
        this.parent = parent;
    }

    @Override
    public ParentNode<?> getParent() {
        return this.parent;
    }

    @Override
    public boolean hasAncestor(Class<? extends Node> ancestorClass) {
        for (Node node = this; node != null; node = node.getParent()) {
            if (!ancestorClass.isInstance(node)) continue;
            return true;
        }
        return false;
    }

    @Override
    public <N extends Node> N getNearestAncestor(Class<N> ancestorClass) {
        for (Node node = this; node != null; node = node.getParent()) {
            if (!ancestorClass.isInstance(node)) continue;
            return (N)((Node)ancestorClass.cast(node));
        }
        return null;
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String toTreeString(int indent) {
        return SPACES.substring(0, indent) + "[" + this.toString() + "]\n";
    }

    @Override
    public abstract Node clone();
}

