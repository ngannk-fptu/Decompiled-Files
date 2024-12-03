/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.qom;

import javax.jcr.query.qom.DynamicOperand;
import javax.jcr.query.qom.UpperCase;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.query.qom.DynamicOperandImpl;
import org.apache.jackrabbit.spi.commons.query.qom.QOMTreeVisitor;

public class UpperCaseImpl
extends DynamicOperandImpl
implements UpperCase {
    private final DynamicOperandImpl operand;

    UpperCaseImpl(NamePathResolver resolver, DynamicOperandImpl operand) {
        super(resolver, operand.getSelectorQName());
        this.operand = operand;
    }

    @Override
    public DynamicOperand getOperand() {
        return this.operand;
    }

    @Override
    public Object accept(QOMTreeVisitor visitor, Object data) throws Exception {
        return visitor.visit(this, data);
    }

    public String toString() {
        return "UPPER(" + this.operand + ")";
    }
}

