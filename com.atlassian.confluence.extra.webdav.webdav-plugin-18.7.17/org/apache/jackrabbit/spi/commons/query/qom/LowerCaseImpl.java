/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.qom;

import javax.jcr.query.qom.DynamicOperand;
import javax.jcr.query.qom.LowerCase;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.query.qom.DynamicOperandImpl;
import org.apache.jackrabbit.spi.commons.query.qom.QOMTreeVisitor;

public class LowerCaseImpl
extends DynamicOperandImpl
implements LowerCase {
    private final DynamicOperandImpl operand;

    LowerCaseImpl(NamePathResolver resolver, DynamicOperandImpl operand) {
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
        return "LOWER(" + this.operand + ")";
    }
}

