/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.nodes;

import java.util.List;
import java.util.Map;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;

public interface QueryNode {
    public CharSequence toQueryString(EscapeQuerySyntax var1);

    public String toString();

    public List<QueryNode> getChildren();

    public boolean isLeaf();

    public boolean containsTag(String var1);

    public Object getTag(String var1);

    public QueryNode getParent();

    public QueryNode cloneTree() throws CloneNotSupportedException;

    public void add(QueryNode var1);

    public void add(List<QueryNode> var1);

    public void set(List<QueryNode> var1);

    public void setTag(String var1, Object var2);

    public void unsetTag(String var1);

    public Map<String, Object> getTagMap();
}

