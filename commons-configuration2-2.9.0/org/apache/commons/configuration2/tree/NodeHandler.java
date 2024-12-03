/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.tree;

import java.util.List;
import java.util.Set;
import org.apache.commons.configuration2.tree.NodeMatcher;

public interface NodeHandler<T> {
    public String nodeName(T var1);

    public Object getValue(T var1);

    public T getParent(T var1);

    public List<T> getChildren(T var1);

    public List<T> getChildren(T var1, String var2);

    public <C> List<T> getMatchingChildren(T var1, NodeMatcher<C> var2, C var3);

    public T getChild(T var1, int var2);

    public int indexOfChild(T var1, T var2);

    public int getChildrenCount(T var1, String var2);

    public <C> int getMatchingChildrenCount(T var1, NodeMatcher<C> var2, C var3);

    public Set<String> getAttributes(T var1);

    public boolean hasAttributes(T var1);

    public Object getAttributeValue(T var1, String var2);

    public boolean isDefined(T var1);

    public T getRootNode();
}

