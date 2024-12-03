/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.qom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.jcr.query.InvalidQueryException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.query.qom.AbstractQOMNode;
import org.apache.jackrabbit.spi.commons.query.qom.ChildNodeImpl;
import org.apache.jackrabbit.spi.commons.query.qom.ColumnImpl;
import org.apache.jackrabbit.spi.commons.query.qom.ConstraintImpl;
import org.apache.jackrabbit.spi.commons.query.qom.DefaultTraversingQOMTreeVisitor;
import org.apache.jackrabbit.spi.commons.query.qom.DescendantNodeImpl;
import org.apache.jackrabbit.spi.commons.query.qom.EquiJoinConditionImpl;
import org.apache.jackrabbit.spi.commons.query.qom.FullTextSearchImpl;
import org.apache.jackrabbit.spi.commons.query.qom.FullTextSearchScoreImpl;
import org.apache.jackrabbit.spi.commons.query.qom.NodeLocalNameImpl;
import org.apache.jackrabbit.spi.commons.query.qom.NodeNameImpl;
import org.apache.jackrabbit.spi.commons.query.qom.OrderingImpl;
import org.apache.jackrabbit.spi.commons.query.qom.PropertyExistenceImpl;
import org.apache.jackrabbit.spi.commons.query.qom.PropertyValueImpl;
import org.apache.jackrabbit.spi.commons.query.qom.QOMTreeVisitor;
import org.apache.jackrabbit.spi.commons.query.qom.SameNodeImpl;
import org.apache.jackrabbit.spi.commons.query.qom.SameNodeJoinConditionImpl;
import org.apache.jackrabbit.spi.commons.query.qom.SelectorImpl;
import org.apache.jackrabbit.spi.commons.query.qom.SourceImpl;

public class QueryObjectModelTree
extends AbstractQOMNode {
    private final SourceImpl source;
    private final ConstraintImpl constraint;
    private final OrderingImpl[] orderings;
    private final ColumnImpl[] columns;
    private final Map selectors = new HashMap();

    public QueryObjectModelTree(NamePathResolver resolver, SourceImpl source, ConstraintImpl constraint, OrderingImpl[] orderings, ColumnImpl[] columns) throws InvalidQueryException {
        super(resolver);
        this.source = source;
        this.constraint = constraint;
        this.orderings = orderings;
        this.columns = columns;
        for (SelectorImpl selector : Arrays.asList(source.getSelectors())) {
            if (this.selectors.put(selector.getSelectorQName(), selector) == null) continue;
            throw new InvalidQueryException("Duplicate selector name: " + selector.getSelectorName());
        }
        if (this.selectors.size() == 1) {
            this.selectors.put(null, this.selectors.values().iterator().next());
        }
        this.checkQuery();
    }

    public SourceImpl getSource() {
        return this.source;
    }

    public ConstraintImpl getConstraint() {
        return this.constraint;
    }

    public OrderingImpl[] getOrderings() {
        OrderingImpl[] temp = new OrderingImpl[this.orderings.length];
        System.arraycopy(this.orderings, 0, temp, 0, this.orderings.length);
        return temp;
    }

    public ColumnImpl[] getColumns() {
        ColumnImpl[] temp = new ColumnImpl[this.columns.length];
        System.arraycopy(this.columns, 0, temp, 0, this.columns.length);
        return temp;
    }

    public SelectorImpl getSelector(Name name) {
        return (SelectorImpl)this.selectors.get(name);
    }

    @Override
    public Object accept(QOMTreeVisitor visitor, Object data) throws Exception {
        return visitor.visit(this, data);
    }

    private void checkQuery() throws InvalidQueryException {
        try {
            this.accept(new DefaultTraversingQOMTreeVisitor(){

                @Override
                public Object visit(ChildNodeImpl node, Object data) throws Exception {
                    return this.checkSelector(node.getSelectorQName());
                }

                @Override
                public Object visit(ColumnImpl node, Object data) throws Exception {
                    return this.checkSelector(node.getSelectorQName());
                }

                @Override
                public Object visit(DescendantNodeImpl node, Object data) throws Exception {
                    return this.checkSelector(node.getSelectorQName());
                }

                @Override
                public Object visit(EquiJoinConditionImpl node, Object data) throws Exception {
                    this.checkSelector(node.getSelector1QName());
                    return this.checkSelector(node.getSelector2QName());
                }

                @Override
                public Object visit(FullTextSearchImpl node, Object data) throws Exception {
                    return this.checkSelector(node.getSelectorQName());
                }

                @Override
                public Object visit(FullTextSearchScoreImpl node, Object data) throws Exception {
                    return this.checkSelector(node.getSelectorQName());
                }

                @Override
                public Object visit(NodeLocalNameImpl node, Object data) throws Exception {
                    return this.checkSelector(node.getSelectorQName());
                }

                @Override
                public Object visit(NodeNameImpl node, Object data) throws Exception {
                    return this.checkSelector(node.getSelectorQName());
                }

                @Override
                public Object visit(PropertyExistenceImpl node, Object data) throws Exception {
                    return this.checkSelector(node.getSelectorQName());
                }

                @Override
                public Object visit(PropertyValueImpl node, Object data) throws Exception {
                    return this.checkSelector(node.getSelectorQName());
                }

                @Override
                public Object visit(SameNodeImpl node, Object data) throws Exception {
                    return this.checkSelector(node.getSelectorQName());
                }

                @Override
                public Object visit(SameNodeJoinConditionImpl node, Object data) throws Exception {
                    this.checkSelector(node.getSelector1QName());
                    return this.checkSelector(node.getSelector2QName());
                }

                private Object checkSelector(Name selectorName) throws InvalidQueryException {
                    if (!QueryObjectModelTree.this.selectors.containsKey(selectorName)) {
                        String msg = "Unknown selector: ";
                        msg = selectorName != null ? msg + QueryObjectModelTree.this.getJCRName(selectorName) : msg + "<default>";
                        throw new InvalidQueryException(msg);
                    }
                    return null;
                }
            }, null);
        }
        catch (Exception e) {
            throw new InvalidQueryException(e.getMessage());
        }
    }

    public String toString() {
        int i;
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");
        if (this.columns != null && this.columns.length > 0) {
            for (i = 0; i < this.columns.length; ++i) {
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append(this.columns[i]);
            }
        } else {
            builder.append("*");
        }
        builder.append(" FROM ");
        builder.append(this.source);
        if (this.constraint != null) {
            builder.append(" WHERE ");
            builder.append(this.constraint);
        }
        if (this.orderings != null && this.orderings.length > 0) {
            builder.append(" ORDER BY ");
            for (i = 0; i < this.orderings.length; ++i) {
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append(this.orderings[i]);
            }
        }
        return builder.toString();
    }
}

