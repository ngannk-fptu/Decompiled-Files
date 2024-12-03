/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.commons.query.QueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryNodeVisitor;

public abstract class NAryQueryNode<T extends QueryNode>
extends QueryNode {
    private static final Object[] EMPTY = new Object[0];
    protected List<T> operands = null;

    public NAryQueryNode(QueryNode parent) {
        super(parent);
    }

    public NAryQueryNode(QueryNode parent, T[] operands) {
        super(parent);
        if (operands.length > 0) {
            this.operands = new ArrayList<T>();
            this.operands.addAll(Arrays.asList(operands));
        }
    }

    public void addOperand(T operand) {
        if (this.operands == null) {
            this.operands = new ArrayList<T>();
        }
        this.operands.add(operand);
    }

    public boolean removeOperand(T operand) {
        if (this.operands == null) {
            return false;
        }
        Iterator<T> it = this.operands.iterator();
        while (it.hasNext()) {
            if (it.next() != operand) continue;
            it.remove();
            return true;
        }
        return false;
    }

    public QueryNode[] getOperands() {
        if (this.operands == null) {
            return new QueryNode[0];
        }
        return this.operands.toArray(new QueryNode[this.operands.size()]);
    }

    public int getNumOperands() {
        if (this.operands == null) {
            return 0;
        }
        return this.operands.size();
    }

    public Object[] acceptOperands(QueryNodeVisitor visitor, Object data) throws RepositoryException {
        if (this.operands == null) {
            return EMPTY;
        }
        ArrayList<Object> result = new ArrayList<Object>(this.operands.size());
        for (QueryNode operand : this.operands) {
            Object r = operand.accept(visitor, data);
            if (r == null) continue;
            result.add(r);
        }
        return result.toArray();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NAryQueryNode) {
            NAryQueryNode other = (NAryQueryNode)obj;
            return this.operands == null ? other.operands == null : this.operands.equals(other.operands);
        }
        return false;
    }

    @Override
    public boolean needsSystemTree() {
        if (this.operands == null) {
            return false;
        }
        for (QueryNode operand : this.operands) {
            if (!operand.needsSystemTree()) continue;
            return true;
        }
        return false;
    }
}

