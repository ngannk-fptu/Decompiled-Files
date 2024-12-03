/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.asm.MethodVisitor
 *  org.springframework.util.Assert
 *  org.springframework.util.NumberUtils
 */
package org.springframework.expression.spel.ast;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.springframework.asm.MethodVisitor;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Operation;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.ast.Operator;
import org.springframework.expression.spel.ast.SpelNodeImpl;
import org.springframework.util.Assert;
import org.springframework.util.NumberUtils;

public class OpMultiply
extends Operator {
    private static final int MAX_REPEATED_TEXT_SIZE = 256;

    public OpMultiply(int startPos, int endPos, SpelNodeImpl ... operands) {
        super("*", startPos, endPos, operands);
    }

    @Override
    public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        Object leftOperand = this.getLeftOperand().getValueInternal(state).getValue();
        Object rightOperand = this.getRightOperand().getValueInternal(state).getValue();
        if (leftOperand instanceof Number && rightOperand instanceof Number) {
            Number leftNumber = (Number)leftOperand;
            Number rightNumber = (Number)rightOperand;
            if (leftNumber instanceof BigDecimal || rightNumber instanceof BigDecimal) {
                BigDecimal leftBigDecimal = (BigDecimal)NumberUtils.convertNumberToTargetClass((Number)leftNumber, BigDecimal.class);
                BigDecimal rightBigDecimal = (BigDecimal)NumberUtils.convertNumberToTargetClass((Number)rightNumber, BigDecimal.class);
                return new TypedValue(leftBigDecimal.multiply(rightBigDecimal));
            }
            if (leftNumber instanceof Double || rightNumber instanceof Double) {
                this.exitTypeDescriptor = "D";
                return new TypedValue(leftNumber.doubleValue() * rightNumber.doubleValue());
            }
            if (leftNumber instanceof Float || rightNumber instanceof Float) {
                this.exitTypeDescriptor = "F";
                return new TypedValue(Float.valueOf(leftNumber.floatValue() * rightNumber.floatValue()));
            }
            if (leftNumber instanceof BigInteger || rightNumber instanceof BigInteger) {
                BigInteger leftBigInteger = (BigInteger)NumberUtils.convertNumberToTargetClass((Number)leftNumber, BigInteger.class);
                BigInteger rightBigInteger = (BigInteger)NumberUtils.convertNumberToTargetClass((Number)rightNumber, BigInteger.class);
                return new TypedValue(leftBigInteger.multiply(rightBigInteger));
            }
            if (leftNumber instanceof Long || rightNumber instanceof Long) {
                this.exitTypeDescriptor = "J";
                return new TypedValue(leftNumber.longValue() * rightNumber.longValue());
            }
            if (CodeFlow.isIntegerForNumericOp(leftNumber) || CodeFlow.isIntegerForNumericOp(rightNumber)) {
                this.exitTypeDescriptor = "I";
                return new TypedValue(leftNumber.intValue() * rightNumber.intValue());
            }
            return new TypedValue(leftNumber.doubleValue() * rightNumber.doubleValue());
        }
        if (leftOperand instanceof String && rightOperand instanceof Integer) {
            String text = (String)leftOperand;
            int count = (Integer)rightOperand;
            int requestedSize = text.length() * count;
            this.checkRepeatedTextSize(requestedSize);
            StringBuilder result = new StringBuilder(requestedSize);
            for (int i = 0; i < count; ++i) {
                result.append(text);
            }
            return new TypedValue(result.toString());
        }
        return state.operate(Operation.MULTIPLY, leftOperand, rightOperand);
    }

    private void checkRepeatedTextSize(int requestedSize) {
        if (requestedSize > 256) {
            throw new SpelEvaluationException(this.getStartPosition(), SpelMessage.MAX_REPEATED_TEXT_SIZE_EXCEEDED, 256);
        }
    }

    @Override
    public boolean isCompilable() {
        if (!this.getLeftOperand().isCompilable()) {
            return false;
        }
        if (this.children.length > 1 && !this.getRightOperand().isCompilable()) {
            return false;
        }
        return this.exitTypeDescriptor != null;
    }

    @Override
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        this.getLeftOperand().generateCode(mv, cf);
        String leftDesc = this.getLeftOperand().exitTypeDescriptor;
        String exitDesc = this.exitTypeDescriptor;
        Assert.state((exitDesc != null ? 1 : 0) != 0, (String)"No exit type descriptor");
        char targetDesc = exitDesc.charAt(0);
        CodeFlow.insertNumericUnboxOrPrimitiveTypeCoercion(mv, leftDesc, targetDesc);
        if (this.children.length > 1) {
            cf.enterCompilationScope();
            this.getRightOperand().generateCode(mv, cf);
            String rightDesc = this.getRightOperand().exitTypeDescriptor;
            cf.exitCompilationScope();
            CodeFlow.insertNumericUnboxOrPrimitiveTypeCoercion(mv, rightDesc, targetDesc);
            switch (targetDesc) {
                case 'I': {
                    mv.visitInsn(104);
                    break;
                }
                case 'J': {
                    mv.visitInsn(105);
                    break;
                }
                case 'F': {
                    mv.visitInsn(106);
                    break;
                }
                case 'D': {
                    mv.visitInsn(107);
                    break;
                }
                default: {
                    throw new IllegalStateException("Unrecognized exit type descriptor: '" + this.exitTypeDescriptor + "'");
                }
            }
        }
        cf.pushDescriptor(this.exitTypeDescriptor);
    }
}

