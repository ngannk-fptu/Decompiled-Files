/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm.indy;

import groovyjarjarasm.asm.Handle;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.EmptyExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.tools.WideningCategories;
import org.codehaus.groovy.classgen.AsmClassGenerator;
import org.codehaus.groovy.classgen.asm.BytecodeHelper;
import org.codehaus.groovy.classgen.asm.CompileStack;
import org.codehaus.groovy.classgen.asm.InvocationWriter;
import org.codehaus.groovy.classgen.asm.MethodCallerMultiAdapter;
import org.codehaus.groovy.classgen.asm.OperandStack;
import org.codehaus.groovy.classgen.asm.WriterController;
import org.codehaus.groovy.runtime.wrappers.Wrapper;
import org.codehaus.groovy.vmplugin.v7.IndyInterface;

public class InvokeDynamicWriter
extends InvocationWriter {
    private static final String INDY_INTERFACE_NAME = IndyInterface.class.getName().replace('.', '/');
    private static final String BSM_METHOD_TYPE_DESCRIPTOR = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, String.class, Integer.TYPE).toMethodDescriptorString();
    private static final Handle BSM = new Handle(6, INDY_INTERFACE_NAME, "bootstrap", BSM_METHOD_TYPE_DESCRIPTOR);
    private WriterController controller;

    public InvokeDynamicWriter(WriterController wc) {
        super(wc);
        this.controller = wc;
    }

    @Override
    protected boolean makeCachedCall(Expression origin, ClassExpression sender, Expression receiver, Expression message, Expression arguments, MethodCallerMultiAdapter adapter, boolean safe, boolean spreadSafe, boolean implicitThis, boolean containsSpreadExpression) {
        String methodName;
        if (!(adapter != null && adapter != invokeMethod && adapter != invokeMethodOnCurrent && adapter != invokeStaticMethod || spreadSafe || (methodName = this.getMethodName(message)) == null)) {
            this.makeIndyCall(adapter, receiver, implicitThis, safe, methodName, arguments);
            return true;
        }
        return false;
    }

    private String prepareIndyCall(Expression receiver, boolean implicitThis) {
        CompileStack compileStack = this.controller.getCompileStack();
        OperandStack operandStack = this.controller.getOperandStack();
        compileStack.pushLHS(false);
        compileStack.pushImplicitThis(implicitThis);
        receiver.visit(this.controller.getAcg());
        compileStack.popImplicitThis();
        return "(" + BytecodeHelper.getTypeDescription(operandStack.getTopOperand());
    }

    private void finishIndyCall(Handle bsmHandle, String methodName, String sig, int numberOfArguments, Object ... bsmArgs) {
        CompileStack compileStack = this.controller.getCompileStack();
        OperandStack operandStack = this.controller.getOperandStack();
        this.controller.getMethodVisitor().visitInvokeDynamicInsn(methodName, sig, bsmHandle, bsmArgs);
        operandStack.replace(ClassHelper.OBJECT_TYPE, numberOfArguments);
        compileStack.popLHS();
    }

    private void makeIndyCall(MethodCallerMultiAdapter adapter, Expression receiver, boolean implicitThis, boolean safe, String methodName, Expression arguments) {
        OperandStack operandStack = this.controller.getOperandStack();
        StringBuilder sig = new StringBuilder(this.prepareIndyCall(receiver, implicitThis));
        int numberOfArguments = 1;
        ArgumentListExpression ae = InvokeDynamicWriter.makeArgumentList(arguments);
        boolean containsSpreadExpression = AsmClassGenerator.containsSpreadExpression(arguments);
        if (containsSpreadExpression) {
            this.controller.getAcg().despreadList(ae.getExpressions(), true);
            sig.append(BytecodeHelper.getTypeDescription(Object[].class));
        } else {
            for (Expression arg : ae.getExpressions()) {
                arg.visit(this.controller.getAcg());
                if (arg instanceof CastExpression) {
                    operandStack.box();
                    this.controller.getAcg().loadWrapper(arg);
                    sig.append(BytecodeHelper.getTypeDescription(Wrapper.class));
                } else {
                    sig.append(BytecodeHelper.getTypeDescription(operandStack.getTopOperand()));
                }
                ++numberOfArguments;
            }
        }
        sig.append(")Ljava/lang/Object;");
        String callSiteName = IndyInterface.CALL_TYPES.METHOD.getCallSiteName();
        if (adapter == null) {
            callSiteName = IndyInterface.CALL_TYPES.INIT.getCallSiteName();
        }
        int flags = InvokeDynamicWriter.getMethodCallFlags(adapter, safe, containsSpreadExpression);
        this.finishIndyCall(BSM, callSiteName, sig.toString(), numberOfArguments, methodName, flags);
    }

    private static int getMethodCallFlags(MethodCallerMultiAdapter adapter, boolean safe, boolean spread) {
        int ret = 0;
        if (safe) {
            ret |= 1;
        }
        if (adapter == invokeMethodOnCurrent) {
            ret |= 2;
        }
        if (spread) {
            ret |= 0x10;
        }
        return ret;
    }

    @Override
    public void makeSingleArgumentCall(Expression receiver, String message, Expression arguments) {
        this.makeIndyCall(invokeMethod, receiver, false, false, message, arguments);
    }

    private static int getPropertyFlags(boolean safe, boolean implicitThis, boolean groovyObject) {
        int flags = 0;
        if (implicitThis) {
            flags |= 8;
        }
        if (groovyObject) {
            flags |= 4;
        }
        if (safe) {
            flags |= 1;
        }
        return flags;
    }

    protected void writeGetProperty(Expression receiver, String propertyName, boolean safe, boolean implicitThis, boolean groovyObject) {
        String sig = this.prepareIndyCall(receiver, implicitThis);
        sig = sig + ")Ljava/lang/Object;";
        int flags = InvokeDynamicWriter.getPropertyFlags(safe, implicitThis, groovyObject);
        this.finishIndyCall(BSM, IndyInterface.CALL_TYPES.GET.getCallSiteName(), sig, 1, propertyName, flags);
    }

    @Override
    protected void writeNormalConstructorCall(ConstructorCallExpression call) {
        this.makeCall(call, new ClassExpression(call.getType()), new ConstantExpression("<init>"), call.getArguments(), null, false, false, false);
    }

    @Override
    public void coerce(ClassNode from, ClassNode target) {
        ClassNode wrapper = ClassHelper.getWrapper(target);
        this.makeIndyCall(invokeMethod, EmptyExpression.INSTANCE, false, false, "asType", new ClassExpression(wrapper));
        if (ClassHelper.boolean_TYPE.equals(target) || ClassHelper.Boolean_TYPE.equals(target)) {
            this.writeIndyCast(ClassHelper.OBJECT_TYPE, target);
        } else {
            BytecodeHelper.doCast(this.controller.getMethodVisitor(), wrapper);
            this.controller.getOperandStack().replace(wrapper);
            this.controller.getOperandStack().doGroovyCast(target);
        }
    }

    @Override
    public void castToNonPrimitiveIfNecessary(ClassNode sourceType, ClassNode targetType) {
        ClassNode boxedType = ClassHelper.getWrapper(sourceType);
        if (WideningCategories.implementsInterfaceOrSubclassOf(boxedType, targetType)) {
            this.controller.getOperandStack().box();
            return;
        }
        this.writeIndyCast(sourceType, targetType);
    }

    private void writeIndyCast(ClassNode sourceType, ClassNode targetType) {
        StringBuilder sig = new StringBuilder();
        sig.append('(');
        sig.append(BytecodeHelper.getTypeDescription(sourceType));
        sig.append(')');
        sig.append(BytecodeHelper.getTypeDescription(targetType));
        this.controller.getMethodVisitor().visitInvokeDynamicInsn(IndyInterface.CALL_TYPES.CAST.getCallSiteName(), sig.toString(), BSM, "()", 0);
        this.controller.getOperandStack().replace(targetType);
    }

    @Override
    public void castNonPrimitiveToBool(ClassNode sourceType) {
        this.writeIndyCast(sourceType, ClassHelper.boolean_TYPE);
    }
}

