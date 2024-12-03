/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.common.function.type4;

import java.util.Stack;
import org.apache.pdfbox.pdmodel.common.function.type4.ExecutionContext;
import org.apache.pdfbox.pdmodel.common.function.type4.Operator;

class BitwiseOperators {
    private BitwiseOperators() {
    }

    static class Xor
    extends AbstractLogicalOperator {
        Xor() {
        }

        @Override
        protected boolean applyForBoolean(boolean bool1, boolean bool2) {
            return bool1 ^ bool2;
        }

        @Override
        protected int applyforInteger(int int1, int int2) {
            return int1 ^ int2;
        }
    }

    static class True
    implements Operator {
        True() {
        }

        @Override
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            stack.push(Boolean.TRUE);
        }
    }

    static class Or
    extends AbstractLogicalOperator {
        Or() {
        }

        @Override
        protected boolean applyForBoolean(boolean bool1, boolean bool2) {
            return bool1 || bool2;
        }

        @Override
        protected int applyforInteger(int int1, int int2) {
            return int1 | int2;
        }
    }

    static class Not
    implements Operator {
        Not() {
        }

        @Override
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            Object op1 = stack.pop();
            if (op1 instanceof Boolean) {
                boolean bool1 = (Boolean)op1;
                boolean result = !bool1;
                stack.push(result);
            } else if (op1 instanceof Integer) {
                int int1 = (Integer)op1;
                int result = -int1;
                stack.push(result);
            } else {
                throw new ClassCastException("Operand must be bool or int");
            }
        }
    }

    static class False
    implements Operator {
        False() {
        }

        @Override
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            stack.push(Boolean.FALSE);
        }
    }

    static class Bitshift
    implements Operator {
        Bitshift() {
        }

        @Override
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            int shift = (Integer)stack.pop();
            int int1 = (Integer)stack.pop();
            if (shift < 0) {
                int result = int1 >> Math.abs(shift);
                stack.push(result);
            } else {
                int result = int1 << shift;
                stack.push(result);
            }
        }
    }

    static class And
    extends AbstractLogicalOperator {
        And() {
        }

        @Override
        protected boolean applyForBoolean(boolean bool1, boolean bool2) {
            return bool1 && bool2;
        }

        @Override
        protected int applyforInteger(int int1, int int2) {
            return int1 & int2;
        }
    }

    private static abstract class AbstractLogicalOperator
    implements Operator {
        private AbstractLogicalOperator() {
        }

        @Override
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            Object op2 = stack.pop();
            Object op1 = stack.pop();
            if (op1 instanceof Boolean && op2 instanceof Boolean) {
                boolean bool1 = (Boolean)op1;
                boolean bool2 = (Boolean)op2;
                boolean result = this.applyForBoolean(bool1, bool2);
                stack.push(result);
            } else if (op1 instanceof Integer && op2 instanceof Integer) {
                int int1 = (Integer)op1;
                int int2 = (Integer)op2;
                int result = this.applyforInteger(int1, int2);
                stack.push(result);
            } else {
                throw new ClassCastException("Operands must be bool/bool or int/int");
            }
        }

        protected abstract boolean applyForBoolean(boolean var1, boolean var2);

        protected abstract int applyforInteger(int var1, int var2);
    }
}

