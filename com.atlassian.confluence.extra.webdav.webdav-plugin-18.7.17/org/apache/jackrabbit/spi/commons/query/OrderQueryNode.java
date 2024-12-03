/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query;

import java.util.ArrayList;
import java.util.List;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.commons.conversion.MalformedPathException;
import org.apache.jackrabbit.spi.commons.name.PathBuilder;
import org.apache.jackrabbit.spi.commons.query.QueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryNodeVisitor;

public class OrderQueryNode
extends QueryNode {
    private final List<OrderSpec> specs = new ArrayList<OrderSpec>();

    protected OrderQueryNode(QueryNode parent) {
        super(parent);
    }

    @Override
    public int getType() {
        return 3;
    }

    public void newOrderSpec() {
        this.specs.add(new OrderSpec((Path)null, true));
    }

    public void setAscending(boolean value) {
        if (this.specs.size() == 0) {
            throw new IllegalStateException("No order specification set");
        }
        OrderSpec orderSpec = this.specs.get(this.specs.size() - 1);
        orderSpec.setAscending(value);
    }

    public void setPath(Path path) {
        if (this.specs.size() == 0) {
            throw new IllegalStateException("No order specification set");
        }
        OrderSpec orderSpec = this.specs.get(this.specs.size() - 1);
        orderSpec.setPath(path);
    }

    public void setFunction(String name) {
        if (this.specs.size() == 0) {
            throw new IllegalStateException("No order specification set");
        }
        OrderSpec orderSpec = this.specs.get(this.specs.size() - 1);
        orderSpec.setFunction(name);
    }

    public boolean isValid() {
        for (OrderSpec spec : this.specs) {
            if (spec.getPropertyPath() != null) continue;
            return false;
        }
        return true;
    }

    public void addOrderSpec(Name property, boolean ascending) {
        this.addOrderSpec(OrderQueryNode.createPath(property), ascending);
    }

    public void addOrderSpec(Path property, boolean ascending) {
        this.specs.add(new OrderSpec(property, ascending));
    }

    public void addOrderSpec(OrderSpec spec) {
        this.specs.add(spec);
    }

    @Override
    public Object accept(QueryNodeVisitor visitor, Object data) throws RepositoryException {
        return visitor.visit(this, data);
    }

    public boolean isAscending(int i) throws IndexOutOfBoundsException {
        return this.specs.get(i).ascending;
    }

    public OrderSpec[] getOrderSpecs() {
        return this.specs.toArray(new OrderSpec[this.specs.size()]);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OrderQueryNode) {
            OrderQueryNode other = (OrderQueryNode)obj;
            return this.specs.equals(other.specs);
        }
        return false;
    }

    @Override
    public boolean needsSystemTree() {
        return false;
    }

    private static Path createPath(Name name) {
        try {
            PathBuilder builder = new PathBuilder();
            builder.addLast(name);
            return builder.getPath();
        }
        catch (MalformedPathException e) {
            throw new InternalError();
        }
    }

    public static final class OrderSpec {
        private Path property;
        private boolean ascending;
        private String function;

        public OrderSpec(Name property, boolean ascending) {
            this(OrderQueryNode.createPath(property), ascending);
        }

        public OrderSpec(Path property, boolean ascending) {
            this.property = property;
            this.ascending = ascending;
        }

        public Name getProperty() {
            return this.property.getName();
        }

        public Path getPropertyPath() {
            return this.property;
        }

        public boolean isAscending() {
            return this.ascending;
        }

        public void setAscending(boolean ascending) {
            this.ascending = ascending;
        }

        public void setPath(Path path) {
            this.property = path;
        }

        public void setFunction(String name) {
            this.function = name;
        }

        public String getFunction() {
            return this.function;
        }

        public boolean equals(Object obj) {
            if (obj instanceof OrderSpec) {
                OrderSpec other = (OrderSpec)obj;
                return (this.property == null ? other.property == null : this.property.equals(other.property)) && this.ascending == other.ascending;
            }
            return false;
        }
    }
}

