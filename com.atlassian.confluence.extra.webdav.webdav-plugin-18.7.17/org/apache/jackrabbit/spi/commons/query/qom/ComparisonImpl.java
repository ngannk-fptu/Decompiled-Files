/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.qom;

import javax.jcr.query.qom.Comparison;
import javax.jcr.query.qom.DynamicOperand;
import javax.jcr.query.qom.StaticOperand;
import org.apache.jackrabbit.commons.query.qom.Operator;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.query.qom.ConstraintImpl;
import org.apache.jackrabbit.spi.commons.query.qom.DynamicOperandImpl;
import org.apache.jackrabbit.spi.commons.query.qom.QOMTreeVisitor;
import org.apache.jackrabbit.spi.commons.query.qom.StaticOperandImpl;

public class ComparisonImpl
extends ConstraintImpl
implements Comparison {
    private final DynamicOperandImpl operand1;
    private final Operator operator;
    private final StaticOperandImpl operand2;

    ComparisonImpl(NamePathResolver resolver, DynamicOperandImpl operand1, Operator operator, StaticOperandImpl operand2) {
        super(resolver);
        this.operand1 = operand1;
        this.operator = operator;
        this.operand2 = operand2;
    }

    public Operator getOperatorInstance() {
        return this.operator;
    }

    @Override
    public DynamicOperand getOperand1() {
        return this.operand1;
    }

    @Override
    public String getOperator() {
        return this.operator.toString();
    }

    @Override
    public StaticOperand getOperand2() {
        return this.operand2;
    }

    @Override
    public Object accept(QOMTreeVisitor visitor, Object data) throws Exception {
        return visitor.visit(this, data);
    }

    public String toString() {
        return this.operator.formatSql(this.operand1.toString(), this.operand2.toString());
    }
}

