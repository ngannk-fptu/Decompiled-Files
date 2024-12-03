/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.basetree;

import com.google.template.soy.basetree.Node;
import java.util.List;

public interface ParentNode<N extends Node>
extends Node {
    public int numChildren();

    public N getChild(int var1);

    public int getChildIndex(N var1);

    public List<N> getChildren();

    public void addChild(N var1);

    public void addChild(int var1, N var2);

    public void removeChild(int var1);

    public void removeChild(N var1);

    public void replaceChild(int var1, N var2);

    public void replaceChild(N var1, N var2);

    public void clearChildren();

    public void addChildren(List<? extends N> var1);

    public void addChildren(int var1, List<? extends N> var2);

    public void appendSourceStringForChildren(StringBuilder var1);

    public void appendTreeStringForChildren(StringBuilder var1, int var2);
}

