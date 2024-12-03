/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query;

import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.name.NameFactoryImpl;
import org.apache.jackrabbit.spi.commons.query.NAryQueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryNodeVisitor;

public class LocationStepQueryNode
extends NAryQueryNode<QueryNode> {
    public static final int LAST = Integer.MIN_VALUE;
    public static final int NONE = -2147483647;
    public static final Name EMPTY_NAME = NameFactoryImpl.getInstance().create("", "");
    private static final QueryNode[] EMPTY = new QueryNode[0];
    private Name nameTest = EMPTY_NAME;
    private boolean includeDescendants = false;
    private int index = -2147483647;

    protected LocationStepQueryNode(QueryNode parent) {
        super(parent);
    }

    public Name getNameTest() {
        return this.nameTest;
    }

    public void setNameTest(Name nameTest) {
        this.nameTest = nameTest;
    }

    public boolean getIncludeDescendants() {
        return this.includeDescendants;
    }

    public void setIncludeDescendants(boolean include) {
        this.includeDescendants = include;
    }

    public void addPredicate(QueryNode predicate) {
        this.addOperand(predicate);
    }

    public QueryNode[] getPredicates() {
        if (this.operands == null) {
            return EMPTY;
        }
        return this.operands.toArray(new QueryNode[this.operands.size()]);
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    @Override
    public Object accept(QueryNodeVisitor visitor, Object data) throws RepositoryException {
        return visitor.visit(this, data);
    }

    @Override
    public int getType() {
        return 10;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LocationStepQueryNode) {
            LocationStepQueryNode other = (LocationStepQueryNode)obj;
            return super.equals(other) && this.includeDescendants == other.includeDescendants && this.index == other.index && (this.nameTest == null ? other.nameTest == null : this.nameTest.equals(other.nameTest));
        }
        return false;
    }
}

