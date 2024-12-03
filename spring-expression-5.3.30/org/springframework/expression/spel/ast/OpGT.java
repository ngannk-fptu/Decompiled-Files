/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.asm.MethodVisitor
 *  org.springframework.util.NumberUtils
 */
package org.springframework.expression.spel.ast;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.springframework.asm.MethodVisitor;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.ast.Operator;
import org.springframework.expression.spel.ast.SpelNodeImpl;
import org.springframework.expression.spel.support.BooleanTypedValue;
import org.springframework.util.NumberUtils;

public class OpGT
extends Operator {
    public OpGT(int startPos, int endPos, SpelNodeImpl ... operands) {
        super(">", startPos, endPos, operands);
        this.exitTypeDescriptor = "Z";
    }

    @Override
    public BooleanTypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        Object left = this.getLeftOperand().getValueInternal(state).getValue();
        Object right = this.getRightOperand().getValueInternal(state).getValue();
        this.leftActualDescriptor = CodeFlow.toDescriptorFromObject(left);
        this.rightActualDescriptor = CodeFlow.toDescriptorFromObject(right);
        if (left instanceof Number && right instanceof Number) {
            Number leftNumber = (Number)left;
            Number rightNumber = (Number)right;
            if (leftNumber instanceof BigDecimal || rightNumber instanceof BigDecimal) {
                BigDecimal rightBigDecimal;
                BigDecimal leftBigDecimal = (BigDecimal)NumberUtils.convertNumberToTargetClass((Number)leftNumber, BigDecimal.class);
                return BooleanTypedValue.forValue(leftBigDecimal.compareTo(rightBigDecimal = (BigDecimal)NumberUtils.convertNumberToTargetClass((Number)rightNumber, BigDecimal.class)) > 0);
            }
            if (leftNumber instanceof Double || rightNumber instanceof Double) {
                return BooleanTypedValue.forValue(leftNumber.doubleValue() > rightNumber.doubleValue());
            }
            if (leftNumber instanceof Float || rightNumber instanceof Float) {
                return BooleanTypedValue.forValue(leftNumber.floatValue() > rightNumber.floatValue());
            }
            if (leftNumber instanceof BigInteger || rightNumber instanceof BigInteger) {
                BigInteger rightBigInteger;
                BigInteger leftBigInteger = (BigInteger)NumberUtils.convertNumberToTargetClass((Number)leftNumber, BigInteger.class);
                return BooleanTypedValue.forValue(leftBigInteger.compareTo(rightBigInteger = (BigInteger)NumberUtils.convertNumberToTargetClass((Number)rightNumber, BigInteger.class)) > 0);
            }
            if (leftNumber instanceof Long || rightNumber instanceof Long) {
                return BooleanTypedValue.forValue(leftNumber.longValue() > rightNumber.longValue());
            }
            if (leftNumber instanceof Integer || rightNumber instanceof Integer) {
                return BooleanTypedValue.forValue(leftNumber.intValue() > rightNumber.intValue());
            }
            if (leftNumber instanceof Short || rightNumber instanceof Short) {
                return BooleanTypedValue.forValue(leftNumber.shortValue() > rightNumber.shortValue());
            }
            if (leftNumber instanceof Byte || rightNumber instanceof Byte) {
                return BooleanTypedValue.forValue(leftNumber.byteValue() > rightNumber.byteValue());
            }
            return BooleanTypedValue.forValue(leftNumber.doubleValue() > rightNumber.doubleValue());
        }
        if (left instanceof CharSequence && right instanceof CharSequence) {
            left = left.toString();
            right = right.toString();
        }
        return BooleanTypedValue.forValue(state.getTypeComparator().compare(left, right) > 0);
    }

    @Override
    public boolean isCompilable() {
        return this.isCompilableOperatorUsingNumerics();
    }

    @Override
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        this.generateComparisonCode(mv, cf, 158, 164);
    }
}

