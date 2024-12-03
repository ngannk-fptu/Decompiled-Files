/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.template.soy.basetree;

import com.google.template.soy.basetree.ParentNode;
import com.google.template.soy.basetree.SyntaxVersion;
import com.google.template.soy.basetree.SyntaxVersionBound;
import javax.annotation.Nullable;

public interface Node {
    @Nullable
    public SyntaxVersionBound getSyntaxVersionBound();

    public void maybeSetSyntaxVersionBound(SyntaxVersionBound var1);

    public boolean couldHaveSyntaxVersionAtLeast(SyntaxVersion var1);

    public void setParent(ParentNode<?> var1);

    public ParentNode<?> getParent();

    public boolean hasAncestor(Class<? extends Node> var1);

    public <N extends Node> N getNearestAncestor(Class<N> var1);

    public String toSourceString();

    public String toTreeString(int var1);

    public Node clone();
}

