/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query;

import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.commons.query.QueryNodeVisitor;
import org.apache.jackrabbit.spi.commons.query.QueryTreeDump;

public abstract class QueryNode {
    public static final int TYPE_ROOT = 1;
    public static final int TYPE_RELATION = 2;
    public static final int TYPE_ORDER = 3;
    public static final int TYPE_TEXTSEARCH = 4;
    public static final int TYPE_EXACT = 5;
    public static final int TYPE_NODETYPE = 6;
    public static final int TYPE_AND = 7;
    public static final int TYPE_OR = 8;
    public static final int TYPE_NOT = 9;
    public static final int TYPE_LOCATION = 10;
    public static final int TYPE_PATH = 11;
    public static final int TYPE_DEREF = 12;
    public static final int TYPE_PROP_FUNCTION = 13;
    private final QueryNode parent;

    public QueryNode(QueryNode parent) {
        this.parent = parent;
    }

    public QueryNode getParent() {
        return this.parent;
    }

    public String dump() throws RepositoryException {
        StringBuffer tmp = new StringBuffer();
        QueryTreeDump.dump(this, tmp);
        return tmp.toString();
    }

    public abstract Object accept(QueryNodeVisitor var1, Object var2) throws RepositoryException;

    public abstract int getType();

    public abstract boolean equals(Object var1);

    public abstract boolean needsSystemTree();
}

