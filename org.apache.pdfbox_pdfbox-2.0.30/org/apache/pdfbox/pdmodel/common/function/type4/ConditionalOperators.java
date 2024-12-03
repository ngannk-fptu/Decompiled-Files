/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.common.function.type4;

import java.util.Stack;
import org.apache.pdfbox.pdmodel.common.function.type4.ExecutionContext;
import org.apache.pdfbox.pdmodel.common.function.type4.InstructionSequence;
import org.apache.pdfbox.pdmodel.common.function.type4.Operator;

class ConditionalOperators {
    private ConditionalOperators() {
    }

    static class IfElse
    implements Operator {
        IfElse() {
        }

        @Override
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            InstructionSequence proc2 = (InstructionSequence)stack.pop();
            InstructionSequence proc1 = (InstructionSequence)stack.pop();
            Boolean condition = (Boolean)stack.pop();
            if (condition.booleanValue()) {
                proc1.execute(context);
            } else {
                proc2.execute(context);
            }
        }
    }

    static class If
    implements Operator {
        If() {
        }

        @Override
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            InstructionSequence proc = (InstructionSequence)stack.pop();
            Boolean condition = (Boolean)stack.pop();
            if (condition.booleanValue()) {
                proc.execute(context);
            }
        }
    }
}

