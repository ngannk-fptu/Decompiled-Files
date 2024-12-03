/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm;

import groovyjarjarasm.asm.Label;
import groovyjarjarasm.asm.MethodVisitor;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.AssertStatement;
import org.codehaus.groovy.classgen.asm.MethodCaller;
import org.codehaus.groovy.classgen.asm.OperandStack;
import org.codehaus.groovy.classgen.asm.WriterController;
import org.codehaus.groovy.control.Janitor;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.powerassert.SourceText;
import org.codehaus.groovy.runtime.powerassert.SourceTextNotAvailableException;
import org.codehaus.groovy.syntax.Token;

public class AssertionWriter {
    private static final MethodCaller assertFailedMethod = MethodCaller.newStatic(ScriptBytecodeAdapter.class, "assertFailed");
    private WriterController controller;
    private AssertionTracker assertionTracker;
    private AssertionTracker disabledTracker;

    public AssertionWriter(WriterController wc) {
        this.controller = wc;
    }

    public void writeAssertStatement(AssertStatement statement) {
        MethodVisitor mv = this.controller.getMethodVisitor();
        OperandStack operandStack = this.controller.getOperandStack();
        boolean rewriteAssert = true;
        rewriteAssert = statement.getMessageExpression() == ConstantExpression.NULL;
        AssertionTracker oldTracker = this.assertionTracker;
        Janitor janitor = new Janitor();
        Label tryStart = new Label();
        if (rewriteAssert) {
            this.assertionTracker = new AssertionTracker();
            try {
                this.assertionTracker.sourceText = new SourceText(statement, this.controller.getSourceUnit(), janitor);
                mv.visitTypeInsn(187, "org/codehaus/groovy/runtime/powerassert/ValueRecorder");
                mv.visitInsn(89);
                mv.visitMethodInsn(183, "org/codehaus/groovy/runtime/powerassert/ValueRecorder", "<init>", "()V", false);
                this.controller.getOperandStack().push(ClassHelper.OBJECT_TYPE);
                this.assertionTracker.recorderIndex = this.controller.getCompileStack().defineTemporaryVariable("recorder", true);
                mv.visitLabel(tryStart);
            }
            catch (SourceTextNotAvailableException e) {
                this.assertionTracker = null;
                rewriteAssert = false;
            }
        }
        statement.getBooleanExpression().visit(this.controller.getAcg());
        Label exceptionThrower = operandStack.jump(153);
        if (rewriteAssert) {
            mv.visitVarInsn(25, this.assertionTracker.recorderIndex);
            mv.visitMethodInsn(182, "org/codehaus/groovy/runtime/powerassert/ValueRecorder", "clear", "()V", false);
        }
        Label afterAssert = new Label();
        mv.visitJumpInsn(167, afterAssert);
        mv.visitLabel(exceptionThrower);
        if (rewriteAssert) {
            mv.visitLdcInsn(this.assertionTracker.sourceText.getNormalizedText());
            mv.visitVarInsn(25, this.assertionTracker.recorderIndex);
            mv.visitMethodInsn(184, "org/codehaus/groovy/runtime/powerassert/AssertionRenderer", "render", "(Ljava/lang/String;Lorg/codehaus/groovy/runtime/powerassert/ValueRecorder;)Ljava/lang/String;", false);
        } else {
            this.writeSourcelessAssertText(statement);
        }
        operandStack.push(ClassHelper.STRING_TYPE);
        AssertionTracker savedTracker = this.assertionTracker;
        this.assertionTracker = null;
        statement.getMessageExpression().visit(this.controller.getAcg());
        operandStack.box();
        assertFailedMethod.call(mv);
        operandStack.remove(2);
        if (rewriteAssert) {
            Label tryEnd = new Label();
            mv.visitLabel(tryEnd);
            mv.visitJumpInsn(167, afterAssert);
            Label catchAny = new Label();
            mv.visitLabel(catchAny);
            mv.visitVarInsn(25, savedTracker.recorderIndex);
            mv.visitMethodInsn(182, "org/codehaus/groovy/runtime/powerassert/ValueRecorder", "clear", "()V", false);
            mv.visitInsn(191);
            this.controller.getCompileStack().addExceptionBlock(tryStart, tryEnd, catchAny, null);
        }
        mv.visitLabel(afterAssert);
        if (rewriteAssert) {
            this.controller.getCompileStack().removeVar(savedTracker.recorderIndex);
        }
        this.assertionTracker = oldTracker;
        janitor.cleanup();
    }

    private void writeSourcelessAssertText(AssertStatement statement) {
        MethodVisitor mv = this.controller.getMethodVisitor();
        OperandStack operandStack = this.controller.getOperandStack();
        BooleanExpression booleanExpression = statement.getBooleanExpression();
        String expressionText = booleanExpression.getText();
        ArrayList<String> list = new ArrayList<String>();
        this.addVariableNames(booleanExpression, list);
        if (list.isEmpty()) {
            mv.visitLdcInsn(expressionText);
        } else {
            boolean first = true;
            mv.visitTypeInsn(187, "java/lang/StringBuffer");
            mv.visitInsn(89);
            mv.visitLdcInsn(expressionText + ". Values: ");
            mv.visitMethodInsn(183, "java/lang/StringBuffer", "<init>", "(Ljava/lang/String;)V", false);
            operandStack.push(ClassHelper.OBJECT_TYPE);
            int tempIndex = this.controller.getCompileStack().defineTemporaryVariable("assert", true);
            for (String name : list) {
                String text = name + " = ";
                if (first) {
                    first = false;
                } else {
                    text = ", " + text;
                }
                mv.visitVarInsn(25, tempIndex);
                mv.visitLdcInsn(text);
                mv.visitMethodInsn(182, "java/lang/StringBuffer", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuffer;", false);
                mv.visitInsn(87);
                mv.visitVarInsn(25, tempIndex);
                new VariableExpression(name).visit(this.controller.getAcg());
                operandStack.box();
                mv.visitMethodInsn(184, "org/codehaus/groovy/runtime/InvokerHelper", "toString", "(Ljava/lang/Object;)Ljava/lang/String;", false);
                mv.visitMethodInsn(182, "java/lang/StringBuffer", "append", "(Ljava/lang/String;)Ljava/lang/StringBuffer;", false);
                mv.visitInsn(87);
                operandStack.remove(1);
            }
            mv.visitVarInsn(25, tempIndex);
            this.controller.getCompileStack().removeVar(tempIndex);
        }
    }

    public void record(Expression expression) {
        if (this.assertionTracker == null) {
            return;
        }
        this.record(this.assertionTracker.sourceText.getNormalizedColumn(expression.getLineNumber(), expression.getColumnNumber()));
    }

    public void record(Token op) {
        if (this.assertionTracker == null) {
            return;
        }
        this.record(this.assertionTracker.sourceText.getNormalizedColumn(op.getStartLine(), op.getStartColumn()));
    }

    private void record(int normalizedColumn) {
        if (this.assertionTracker == null) {
            return;
        }
        MethodVisitor mv = this.controller.getMethodVisitor();
        OperandStack operandStack = this.controller.getOperandStack();
        operandStack.dup();
        operandStack.box();
        mv.visitVarInsn(25, this.assertionTracker.recorderIndex);
        operandStack.push(ClassHelper.OBJECT_TYPE);
        operandStack.swap();
        mv.visitLdcInsn(normalizedColumn);
        mv.visitMethodInsn(182, "org/codehaus/groovy/runtime/powerassert/ValueRecorder", "record", "(Ljava/lang/Object;I)Ljava/lang/Object;", false);
        mv.visitInsn(87);
        operandStack.remove(2);
    }

    private void addVariableNames(Expression expression, List<String> list) {
        if (expression instanceof BooleanExpression) {
            BooleanExpression boolExp = (BooleanExpression)expression;
            this.addVariableNames(boolExp.getExpression(), list);
        } else if (expression instanceof BinaryExpression) {
            BinaryExpression binExp = (BinaryExpression)expression;
            this.addVariableNames(binExp.getLeftExpression(), list);
            this.addVariableNames(binExp.getRightExpression(), list);
        } else if (expression instanceof VariableExpression) {
            VariableExpression varExp = (VariableExpression)expression;
            list.add(varExp.getName());
        }
    }

    public void disableTracker() {
        if (this.assertionTracker == null) {
            return;
        }
        this.disabledTracker = this.assertionTracker;
        this.assertionTracker = null;
    }

    public void reenableTracker() {
        if (this.disabledTracker == null) {
            return;
        }
        this.assertionTracker = this.disabledTracker;
        this.disabledTracker = null;
    }

    private static class AssertionTracker {
        int recorderIndex;
        SourceText sourceText;

        private AssertionTracker() {
        }
    }
}

