/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm;

import groovyjarjarasm.asm.Label;
import groovyjarjarasm.asm.MethodVisitor;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.InterfaceHelperClassNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.classgen.AsmClassGenerator;
import org.codehaus.groovy.classgen.asm.BytecodeHelper;
import org.codehaus.groovy.classgen.asm.CompileStack;
import org.codehaus.groovy.classgen.asm.MethodCallerMultiAdapter;
import org.codehaus.groovy.classgen.asm.OperandStack;
import org.codehaus.groovy.classgen.asm.WriterController;
import org.codehaus.groovy.runtime.callsite.CallSite;

public class CallSiteWriter {
    private static final int SIG_ARRAY_LENGTH = 255;
    private static String[] sig = new String[255];
    private static final int MOD_PRIVSS = 4106;
    private static final int MOD_PUBSS = 4105;
    private static final ClassNode CALLSITE_ARRAY_NODE = ClassHelper.make(CallSite[].class);
    private static final String GET_CALLSITE_METHOD = "$getCallSiteArray";
    private static final String CALLSITE_CLASS = "org/codehaus/groovy/runtime/callsite/CallSite";
    private static final String CALLSITE_DESC = "[Lorg/codehaus/groovy/runtime/callsite/CallSite;";
    private static final String GET_CALLSITE_DESC = "()[Lorg/codehaus/groovy/runtime/callsite/CallSite;";
    private static final String CALLSITE_ARRAY_CLASS = "org/codehaus/groovy/runtime/callsite/CallSiteArray";
    private static final String GET_CALLSITEARRAY_DESC = "()Lorg/codehaus/groovy/runtime/callsite/CallSiteArray;";
    private static final String CALLSITE_FIELD = "$callSiteArray";
    private static final String REF_CLASS = "java/lang/ref/SoftReference";
    private static final String REF_DESC = "Ljava/lang/ref/SoftReference;";
    private static final String METHOD_OO_DESC = "(Ljava/lang/Object;)Ljava/lang/Object;";
    private static final String CREATE_CSA_METHOD = "$createCallSiteArray";
    public static final String CONSTRUCTOR = "<$constructor$>";
    private final List<String> callSites = new ArrayList<String>(32);
    private int callSiteArrayVarIndex = -1;
    private WriterController controller;

    private static String getCreateArraySignature(int numberOfArguments) {
        if (numberOfArguments >= 255) {
            throw new IllegalArgumentException(String.format("The max number of supported arguments is %s, but found %s", 255, numberOfArguments));
        }
        if (sig[numberOfArguments] == null) {
            StringBuilder sb = new StringBuilder("(");
            for (int i = 0; i != numberOfArguments; ++i) {
                sb.append("Ljava/lang/Object;");
            }
            sb.append(")[Ljava/lang/Object;");
            CallSiteWriter.sig[numberOfArguments] = sb.toString();
        }
        return sig[numberOfArguments];
    }

    public CallSiteWriter(WriterController wc) {
        this.controller = wc;
        ClassNode node = this.controller.getClassNode();
        if (node instanceof InterfaceHelperClassNode) {
            InterfaceHelperClassNode ihcn = (InterfaceHelperClassNode)node;
            this.callSites.addAll(ihcn.getCallSites());
        }
    }

    public void makeSiteEntry() {
        if (this.controller.isNotClinit()) {
            this.controller.getMethodVisitor().visitMethodInsn(184, this.controller.getInternalClassName(), GET_CALLSITE_METHOD, GET_CALLSITE_DESC, false);
            this.controller.getOperandStack().push(CALLSITE_ARRAY_NODE);
            this.callSiteArrayVarIndex = this.controller.getCompileStack().defineTemporaryVariable("$local$callSiteArray", CALLSITE_ARRAY_NODE, true);
        }
    }

    public void generateCallSiteArray() {
        if (!this.controller.getClassNode().isInterface()) {
            this.controller.getClassVisitor().visitField(4106, CALLSITE_FIELD, REF_DESC, null, null);
            this.generateCreateCallSiteArray();
            this.generateGetCallSiteArray();
        }
    }

    private void generateGetCallSiteArray() {
        int visibility = this.controller.getClassNode() instanceof InterfaceHelperClassNode ? 4105 : 4106;
        MethodVisitor mv = this.controller.getClassVisitor().visitMethod(visibility, GET_CALLSITE_METHOD, GET_CALLSITE_DESC, null, null);
        this.controller.setMethodVisitor(mv);
        mv.visitCode();
        mv.visitFieldInsn(178, this.controller.getInternalClassName(), CALLSITE_FIELD, REF_DESC);
        Label l0 = new Label();
        mv.visitJumpInsn(198, l0);
        mv.visitFieldInsn(178, this.controller.getInternalClassName(), CALLSITE_FIELD, REF_DESC);
        mv.visitMethodInsn(182, REF_CLASS, "get", "()Ljava/lang/Object;", false);
        mv.visitTypeInsn(192, CALLSITE_ARRAY_CLASS);
        mv.visitInsn(89);
        mv.visitVarInsn(58, 0);
        Label l1 = new Label();
        mv.visitJumpInsn(199, l1);
        mv.visitLabel(l0);
        mv.visitMethodInsn(184, this.controller.getInternalClassName(), CREATE_CSA_METHOD, GET_CALLSITEARRAY_DESC, false);
        mv.visitVarInsn(58, 0);
        mv.visitTypeInsn(187, REF_CLASS);
        mv.visitInsn(89);
        mv.visitVarInsn(25, 0);
        mv.visitMethodInsn(183, REF_CLASS, "<init>", "(Ljava/lang/Object;)V", false);
        mv.visitFieldInsn(179, this.controller.getInternalClassName(), CALLSITE_FIELD, REF_DESC);
        mv.visitLabel(l1);
        mv.visitVarInsn(25, 0);
        mv.visitFieldInsn(180, CALLSITE_ARRAY_CLASS, "array", CALLSITE_DESC);
        mv.visitInsn(176);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private void generateCreateCallSiteArray() {
        LinkedList<String> callSiteInitMethods = new LinkedList<String>();
        int index = 0;
        int methodIndex = 0;
        int size = this.callSites.size();
        int maxArrayInit = 5000;
        while (index < size) {
            String methodName = "$createCallSiteArray_" + ++methodIndex;
            callSiteInitMethods.add(methodName);
            MethodVisitor mv = this.controller.getClassVisitor().visitMethod(4106, methodName, "([Ljava/lang/String;)V", null, null);
            this.controller.setMethodVisitor(mv);
            mv.visitCode();
            int methodLimit = size;
            if (methodLimit - index > 5000) {
                methodLimit = index + 5000;
            }
            while (index < methodLimit) {
                mv.visitVarInsn(25, 0);
                mv.visitLdcInsn(index);
                mv.visitLdcInsn(this.callSites.get(index));
                mv.visitInsn(83);
                ++index;
            }
            mv.visitInsn(177);
            mv.visitMaxs(2, 1);
            mv.visitEnd();
        }
        MethodVisitor mv = this.controller.getClassVisitor().visitMethod(4106, CREATE_CSA_METHOD, GET_CALLSITEARRAY_DESC, null, null);
        this.controller.setMethodVisitor(mv);
        mv.visitCode();
        mv.visitLdcInsn(size);
        mv.visitTypeInsn(189, "java/lang/String");
        mv.visitVarInsn(58, 0);
        for (String methodName : callSiteInitMethods) {
            mv.visitVarInsn(25, 0);
            mv.visitMethodInsn(184, this.controller.getInternalClassName(), methodName, "([Ljava/lang/String;)V", false);
        }
        mv.visitTypeInsn(187, CALLSITE_ARRAY_CLASS);
        mv.visitInsn(89);
        this.controller.getAcg().visitClassExpression(new ClassExpression(this.controller.getClassNode()));
        mv.visitVarInsn(25, 0);
        mv.visitMethodInsn(183, CALLSITE_ARRAY_CLASS, "<init>", "(Ljava/lang/Class;[Ljava/lang/String;)V", false);
        mv.visitInsn(176);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private int allocateIndex(String name) {
        this.callSites.add(name);
        return this.callSites.size() - 1;
    }

    private void invokeSafe(boolean safe, String unsafeMethod, String safeMethod) {
        String method = unsafeMethod;
        if (safe) {
            method = safeMethod;
        }
        this.controller.getMethodVisitor().visitMethodInsn(185, CALLSITE_CLASS, method, METHOD_OO_DESC, true);
        this.controller.getOperandStack().replace(ClassHelper.OBJECT_TYPE);
    }

    public void prepareCallSite(String message) {
        MethodVisitor mv = this.controller.getMethodVisitor();
        if (this.controller.isNotClinit()) {
            mv.visitVarInsn(25, this.callSiteArrayVarIndex);
        } else {
            mv.visitMethodInsn(184, this.controller.getClassName(), GET_CALLSITE_METHOD, GET_CALLSITE_DESC, false);
        }
        int index = this.allocateIndex(message);
        mv.visitLdcInsn(index);
        mv.visitInsn(50);
    }

    private void prepareSiteAndReceiver(Expression receiver, String methodName, boolean implicitThis) {
        this.prepareSiteAndReceiver(receiver, methodName, implicitThis, false);
    }

    protected void prepareSiteAndReceiver(Expression receiver, String methodName, boolean implicitThis, boolean lhs) {
        this.prepareCallSite(methodName);
        CompileStack compileStack = this.controller.getCompileStack();
        compileStack.pushImplicitThis(implicitThis);
        compileStack.pushLHS(lhs);
        receiver.visit(this.controller.getAcg());
        this.controller.getOperandStack().box();
        compileStack.popLHS();
        compileStack.popImplicitThis();
    }

    protected void visitBoxedArgument(Expression exp) {
        exp.visit(this.controller.getAcg());
        if (!(exp instanceof TupleExpression)) {
            this.controller.getOperandStack().box();
        }
    }

    public void makeSingleArgumentCall(Expression receiver, String message, Expression arguments) {
        OperandStack operandStack = this.controller.getOperandStack();
        int m1 = operandStack.getStackLength();
        this.prepareSiteAndReceiver(receiver, message, false, this.controller.getCompileStack().isLHS());
        this.visitBoxedArgument(arguments);
        int m2 = operandStack.getStackLength();
        this.controller.getMethodVisitor().visitMethodInsn(185, CALLSITE_CLASS, "call", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
        operandStack.replace(ClassHelper.OBJECT_TYPE, m2 - m1);
    }

    public void makeGroovyObjectGetPropertySite(Expression receiver, String methodName, boolean safe, boolean implicitThis) {
        this.prepareSiteAndReceiver(receiver, methodName, implicitThis);
        this.invokeSafe(safe, "callGroovyObjectGetProperty", "callGroovyObjectGetPropertySafe");
    }

    public void makeGetPropertySite(Expression receiver, String methodName, boolean safe, boolean implicitThis) {
        this.prepareSiteAndReceiver(receiver, methodName, implicitThis);
        this.invokeSafe(safe, "callGetProperty", "callGetPropertySafe");
    }

    public void makeCallSite(Expression receiver, String message, Expression arguments, boolean safe, boolean implicitThis, boolean callCurrent, boolean callStatic) {
        this.prepareSiteAndReceiver(receiver, message, implicitThis);
        CompileStack compileStack = this.controller.getCompileStack();
        compileStack.pushImplicitThis(implicitThis);
        compileStack.pushLHS(false);
        boolean constructor = message.equals(CONSTRUCTOR);
        OperandStack operandStack = this.controller.getOperandStack();
        boolean containsSpreadExpression = AsmClassGenerator.containsSpreadExpression(arguments);
        int numberOfArguments = containsSpreadExpression ? -1 : AsmClassGenerator.argumentSize(arguments);
        int operandsToReplace = 1;
        if (numberOfArguments > 0 || containsSpreadExpression) {
            ArgumentListExpression ae;
            if (arguments instanceof ArgumentListExpression) {
                ae = (ArgumentListExpression)arguments;
            } else if (arguments instanceof TupleExpression) {
                TupleExpression te = (TupleExpression)arguments;
                ae = new ArgumentListExpression(te.getExpressions());
            } else {
                ae = new ArgumentListExpression();
                ae.addExpression(arguments);
            }
            this.controller.getCompileStack().pushImplicitThis(false);
            if (containsSpreadExpression) {
                numberOfArguments = -1;
                this.controller.getAcg().despreadList(ae.getExpressions(), true);
            } else {
                numberOfArguments = ae.getExpressions().size();
                for (int i = 0; i < numberOfArguments; ++i) {
                    Expression argument = ae.getExpression(i);
                    argument.visit(this.controller.getAcg());
                    operandStack.box();
                    if (!(argument instanceof CastExpression)) continue;
                    this.controller.getAcg().loadWrapper(argument);
                }
                operandsToReplace += numberOfArguments;
            }
            this.controller.getCompileStack().popImplicitThis();
        }
        this.controller.getCompileStack().popLHS();
        this.controller.getCompileStack().popImplicitThis();
        MethodVisitor mv = this.controller.getMethodVisitor();
        if (numberOfArguments > 4) {
            String createArraySignature = CallSiteWriter.getCreateArraySignature(numberOfArguments);
            mv.visitMethodInsn(184, "org/codehaus/groovy/runtime/ArrayUtil", "createArray", createArraySignature, false);
            operandStack.replace(ClassHelper.OBJECT_TYPE.makeArray(), numberOfArguments);
            operandsToReplace = operandsToReplace - numberOfArguments + 1;
        }
        String desc = CallSiteWriter.getDescForParamNum(numberOfArguments);
        if (callStatic) {
            mv.visitMethodInsn(185, CALLSITE_CLASS, "callStatic", "(Ljava/lang/Class;" + desc, true);
        } else if (constructor) {
            mv.visitMethodInsn(185, CALLSITE_CLASS, "callConstructor", "(Ljava/lang/Object;" + desc, true);
        } else if (callCurrent) {
            mv.visitMethodInsn(185, CALLSITE_CLASS, "callCurrent", "(Lgroovy/lang/GroovyObject;" + desc, true);
        } else if (safe) {
            mv.visitMethodInsn(185, CALLSITE_CLASS, "callSafe", "(Ljava/lang/Object;" + desc, true);
        } else {
            mv.visitMethodInsn(185, CALLSITE_CLASS, "call", "(Ljava/lang/Object;" + desc, true);
        }
        operandStack.replace(ClassHelper.OBJECT_TYPE, operandsToReplace);
    }

    private static String getDescForParamNum(int numberOfArguments) {
        switch (numberOfArguments) {
            case 0: {
                return ")Ljava/lang/Object;";
            }
            case 1: {
                return "Ljava/lang/Object;)Ljava/lang/Object;";
            }
            case 2: {
                return "Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;";
            }
            case 3: {
                return "Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;";
            }
            case 4: {
                return "Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;";
            }
        }
        return "[Ljava/lang/Object;)Ljava/lang/Object;";
    }

    public List<String> getCallSites() {
        return this.callSites;
    }

    public void makeCallSiteArrayInitializer() {
        String classInternalName = BytecodeHelper.getClassInternalName(this.controller.getClassNode());
        MethodVisitor mv = this.controller.getMethodVisitor();
        mv.visitInsn(1);
        mv.visitFieldInsn(179, classInternalName, CALLSITE_FIELD, REF_DESC);
    }

    public boolean hasCallSiteUse() {
        return this.callSiteArrayVarIndex >= 0;
    }

    public void fallbackAttributeOrPropertySite(PropertyExpression expression, Expression objectExpression, String name, MethodCallerMultiAdapter adapter) {
        if (this.controller.getCompileStack().isLHS()) {
            this.controller.getOperandStack().box();
        }
        this.controller.getInvocationWriter().makeCall(expression, objectExpression, new CastExpression(ClassHelper.STRING_TYPE, expression.getProperty()), MethodCallExpression.NO_ARGUMENTS, adapter, expression.isSafe(), expression.isSpreadSafe(), expression.isImplicitThis());
    }
}

