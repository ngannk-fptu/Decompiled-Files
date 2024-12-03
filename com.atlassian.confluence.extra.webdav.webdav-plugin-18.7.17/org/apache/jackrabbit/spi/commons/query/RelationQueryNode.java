/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query;

import java.util.Date;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.commons.name.NameFactoryImpl;
import org.apache.jackrabbit.spi.commons.query.LocationStepQueryNode;
import org.apache.jackrabbit.spi.commons.query.NAryQueryNode;
import org.apache.jackrabbit.spi.commons.query.PathQueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryConstants;
import org.apache.jackrabbit.spi.commons.query.QueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryNodeFactory;
import org.apache.jackrabbit.spi.commons.query.QueryNodeVisitor;

public class RelationQueryNode
extends NAryQueryNode<QueryNode>
implements QueryConstants {
    public static final Name STAR_NAME_TEST = NameFactoryImpl.getInstance().create("internal", "__star__");
    private PathQueryNode relPath;
    private boolean unaryMinus;
    private long valueLong;
    private int valuePosition;
    private double valueDouble;
    private String valueString;
    private Date valueDate;
    private final int operation;
    private int type;
    private final QueryNodeFactory factory;

    protected RelationQueryNode(QueryNode parent, int operation, QueryNodeFactory factory) {
        super(parent);
        this.operation = operation;
        this.factory = factory;
        this.relPath = factory.createPathQueryNode(this);
    }

    @Override
    public Object accept(QueryNodeVisitor visitor, Object data) throws RepositoryException {
        return visitor.visit(this, data);
    }

    @Override
    public int getType() {
        return 2;
    }

    public void setUnaryMinus(boolean b) {
        this.unaryMinus = b;
    }

    public int getValueType() {
        return this.type;
    }

    public PathQueryNode getRelativePath() {
        return this.relPath;
    }

    public void setRelativePath(Path relPath) {
        Path.Element[] elements;
        if (relPath.isAbsolute()) {
            throw new IllegalArgumentException("relPath must be relative");
        }
        for (Path.Element element : elements = relPath.getElements()) {
            this.addPathElement(element);
        }
    }

    public void addPathElement(Path.Element element) {
        LocationStepQueryNode step = this.factory.createLocationStepQueryNode(this.relPath);
        if (element.getName().equals(STAR_NAME_TEST)) {
            step.setNameTest(null);
        } else {
            step.setNameTest(element.getName());
        }
        this.relPath.addPathStep(step);
    }

    public long getLongValue() {
        return this.valueLong;
    }

    public void setLongValue(long value) {
        this.valueLong = this.unaryMinus ? -value : value;
        this.type = 1;
    }

    public int getPositionValue() {
        return this.valuePosition;
    }

    public void setPositionValue(int value) {
        this.valuePosition = value;
        this.type = 6;
    }

    public double getDoubleValue() {
        return this.valueDouble;
    }

    public void setDoubleValue(double value) {
        this.valueDouble = this.unaryMinus ? -value : value;
        this.type = 2;
    }

    public String getStringValue() {
        return this.valueString;
    }

    public void setStringValue(String value) {
        this.valueString = value;
        this.type = 3;
    }

    public Date getDateValue() {
        return this.valueDate;
    }

    public void setDateValue(Date value) {
        this.valueDate = value;
        this.type = 4;
    }

    public int getOperation() {
        return this.operation;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RelationQueryNode) {
            RelationQueryNode other = (RelationQueryNode)obj;
            return this.type == other.type && (this.valueDate == null ? other.valueDate == null : this.valueDate.equals(other.valueDate)) && this.valueDouble == other.valueDouble && this.valueLong == other.valueLong && this.valuePosition == other.valuePosition && (this.valueString == null ? other.valueString == null : this.valueString.equals(other.valueString)) && (this.relPath == null ? other.relPath == null : this.relPath.equals(other.relPath));
        }
        return false;
    }
}

