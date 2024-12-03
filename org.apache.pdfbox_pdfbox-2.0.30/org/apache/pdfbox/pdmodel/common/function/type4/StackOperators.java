/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.common.function.type4;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;
import org.apache.pdfbox.pdmodel.common.function.type4.ExecutionContext;
import org.apache.pdfbox.pdmodel.common.function.type4.Operator;

class StackOperators {
    private StackOperators() {
    }

    static class Roll
    implements Operator {
        Roll() {
        }

        @Override
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            int j = ((Number)stack.pop()).intValue();
            int n = ((Number)stack.pop()).intValue();
            if (j == 0) {
                return;
            }
            if (n < 0) {
                throw new IllegalArgumentException("rangecheck: " + n);
            }
            LinkedList<Object> rolled = new LinkedList<Object>();
            LinkedList<Object> moved = new LinkedList<Object>();
            if (j < 0) {
                int i;
                int n1 = n + j;
                for (i = 0; i < n1; ++i) {
                    moved.addFirst(stack.pop());
                }
                for (i = j; i < 0; ++i) {
                    rolled.addFirst(stack.pop());
                }
                stack.addAll(moved);
                stack.addAll(rolled);
            } else {
                int i;
                int n1 = n - j;
                for (i = j; i > 0; --i) {
                    rolled.addFirst(stack.pop());
                }
                for (i = 0; i < n1; ++i) {
                    moved.addFirst(stack.pop());
                }
                stack.addAll(rolled);
                stack.addAll(moved);
            }
        }
    }

    static class Pop
    implements Operator {
        Pop() {
        }

        @Override
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            stack.pop();
        }
    }

    static class Index
    implements Operator {
        Index() {
        }

        @Override
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            int n = ((Number)stack.pop()).intValue();
            if (n < 0) {
                throw new IllegalArgumentException("rangecheck: " + n);
            }
            int size = stack.size();
            stack.push(stack.get(size - n - 1));
        }
    }

    static class Exch
    implements Operator {
        Exch() {
        }

        @Override
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            Object any2 = stack.pop();
            Object any1 = stack.pop();
            stack.push(any2);
            stack.push(any1);
        }
    }

    static class Dup
    implements Operator {
        Dup() {
        }

        @Override
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            stack.push(stack.peek());
        }
    }

    static class Copy
    implements Operator {
        Copy() {
        }

        @Override
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            int n = ((Number)stack.pop()).intValue();
            if (n > 0) {
                int size = stack.size();
                ArrayList copy = new ArrayList(stack.subList(size - n, size));
                stack.addAll(copy);
            }
        }
    }
}

