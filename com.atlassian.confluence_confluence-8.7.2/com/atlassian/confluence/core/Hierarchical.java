/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core;

import java.util.List;

public interface Hierarchical<T> {
    public boolean isRootLevel();

    public T getParent();

    public void setParent(T var1);

    public List<T> getChildren();

    public boolean hasChildren();

    public void setChildren(List<T> var1);

    public void addChild(T var1);

    public void removeChild(T var1);

    public List<T> getAncestors();
}

