/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen;

import groovyjarjarasm.asm.MethodVisitor;
import groovyjarjarasm.asm.Opcodes;
import java.util.List;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.classgen.BytecodeInstruction;
import org.codehaus.groovy.classgen.BytecodeSequence;
import org.codehaus.groovy.classgen.InnerClassVisitorHelper;
import org.codehaus.groovy.classgen.asm.BytecodeHelper;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.SourceUnit;

public class InnerClassCompletionVisitor
extends InnerClassVisitorHelper
implements Opcodes {
    private final SourceUnit sourceUnit;
    private ClassNode classNode;
    private FieldNode thisField = null;
    private static final String CLOSURE_INTERNAL_NAME = BytecodeHelper.getClassInternalName(ClassHelper.CLOSURE_TYPE);
    private static final String CLOSURE_DESCRIPTOR = BytecodeHelper.getTypeDescription(ClassHelper.CLOSURE_TYPE);

    public InnerClassCompletionVisitor(CompilationUnit cu, SourceUnit su) {
        this.sourceUnit = su;
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return this.sourceUnit;
    }

    @Override
    public void visitClass(ClassNode node) {
        this.classNode = node;
        this.thisField = null;
        InnerClassNode innerClass = null;
        if (!node.isEnum() && !node.isInterface() && node instanceof InnerClassNode) {
            innerClass = (InnerClassNode)node;
            this.thisField = innerClass.getField("this$0");
            if (innerClass.getVariableScope() == null && innerClass.getDeclaredConstructors().isEmpty()) {
                innerClass.addConstructor(1, Parameter.EMPTY_ARRAY, null, null);
            }
        }
        if (node.isEnum() || node.isInterface()) {
            return;
        }
        if (node.getInnerClasses().hasNext()) {
            InnerClassCompletionVisitor.addDispatcherMethods(node);
        }
        if (innerClass == null) {
            return;
        }
        super.visitClass(node);
        this.addDefaultMethods(innerClass);
    }

    @Override
    public void visitConstructor(ConstructorNode node) {
        this.addThisReference(node);
        super.visitConstructor(node);
    }

    private static String getTypeDescriptor(ClassNode node, boolean isStatic) {
        return BytecodeHelper.getTypeDescription(InnerClassCompletionVisitor.getClassNode(node, isStatic));
    }

    private static String getInternalName(ClassNode node, boolean isStatic) {
        return BytecodeHelper.getClassInternalName(InnerClassCompletionVisitor.getClassNode(node, isStatic));
    }

    private static void addDispatcherMethods(ClassNode classNode) {
        int objectDistance = InnerClassCompletionVisitor.getObjectDistance(classNode);
        Parameter[] parameters = new Parameter[]{new Parameter(ClassHelper.STRING_TYPE, "name"), new Parameter(ClassHelper.OBJECT_TYPE, "args")};
        MethodNode method = classNode.addSyntheticMethod("this$dist$invoke$" + objectDistance, 4097, ClassHelper.OBJECT_TYPE, parameters, ClassNode.EMPTY_ARRAY, null);
        BlockStatement block = new BlockStatement();
        InnerClassCompletionVisitor.setMethodDispatcherCode(block, VariableExpression.THIS_EXPRESSION, parameters);
        method.setCode(block);
        parameters = new Parameter[]{new Parameter(ClassHelper.STRING_TYPE, "name"), new Parameter(ClassHelper.OBJECT_TYPE, "value")};
        method = classNode.addSyntheticMethod("this$dist$set$" + objectDistance, 4097, ClassHelper.VOID_TYPE, parameters, ClassNode.EMPTY_ARRAY, null);
        block = new BlockStatement();
        InnerClassCompletionVisitor.setPropertySetterDispatcher(block, VariableExpression.THIS_EXPRESSION, parameters);
        method.setCode(block);
        parameters = new Parameter[]{new Parameter(ClassHelper.STRING_TYPE, "name")};
        method = classNode.addSyntheticMethod("this$dist$get$" + objectDistance, 4097, ClassHelper.OBJECT_TYPE, parameters, ClassNode.EMPTY_ARRAY, null);
        block = new BlockStatement();
        InnerClassCompletionVisitor.setPropertyGetterDispatcher(block, VariableExpression.THIS_EXPRESSION, parameters);
        method.setCode(block);
    }

    private void getThis(MethodVisitor mv, String classInternalName, String outerClassDescriptor, String innerClassInternalName) {
        mv.visitVarInsn(25, 0);
        if (ClassHelper.CLOSURE_TYPE.equals(this.thisField.getType())) {
            mv.visitFieldInsn(180, classInternalName, "this$0", CLOSURE_DESCRIPTOR);
            mv.visitMethodInsn(182, CLOSURE_INTERNAL_NAME, "getThisObject", "()Ljava/lang/Object;", false);
            mv.visitTypeInsn(192, innerClassInternalName);
        } else {
            mv.visitFieldInsn(180, classInternalName, "this$0", outerClassDescriptor);
        }
    }

    private void addDefaultMethods(InnerClassNode node) {
        boolean isStatic = InnerClassCompletionVisitor.isStatic(node);
        ClassNode outerClass = node.getOuterClass();
        final String classInternalName = BytecodeHelper.getClassInternalName(node);
        final String outerClassInternalName = InnerClassCompletionVisitor.getInternalName(outerClass, isStatic);
        final String outerClassDescriptor = InnerClassCompletionVisitor.getTypeDescriptor(outerClass, isStatic);
        final int objectDistance = InnerClassCompletionVisitor.getObjectDistance(outerClass);
        Parameter[] parameters = new Parameter[]{new Parameter(ClassHelper.STRING_TYPE, "name"), new Parameter(ClassHelper.OBJECT_TYPE, "args")};
        String methodName = "methodMissing";
        if (isStatic) {
            this.addCompilationErrorOnCustomMethodNode(node, methodName, parameters);
        }
        MethodNode method = node.addSyntheticMethod(methodName, 1, ClassHelper.OBJECT_TYPE, parameters, ClassNode.EMPTY_ARRAY, null);
        BlockStatement block = new BlockStatement();
        if (isStatic) {
            InnerClassCompletionVisitor.setMethodDispatcherCode(block, new ClassExpression(outerClass), parameters);
        } else {
            block.addStatement(new BytecodeSequence(new BytecodeInstruction(){

                @Override
                public void visit(MethodVisitor mv) {
                    InnerClassCompletionVisitor.this.getThis(mv, classInternalName, outerClassDescriptor, outerClassInternalName);
                    mv.visitVarInsn(25, 1);
                    mv.visitVarInsn(25, 2);
                    mv.visitMethodInsn(182, outerClassInternalName, "this$dist$invoke$" + objectDistance, "(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;", false);
                    mv.visitInsn(176);
                }
            }));
        }
        method.setCode(block);
        methodName = "$static_methodMissing";
        if (isStatic) {
            this.addCompilationErrorOnCustomMethodNode(node, methodName, parameters);
        }
        method = node.addSyntheticMethod(methodName, 9, ClassHelper.OBJECT_TYPE, parameters, ClassNode.EMPTY_ARRAY, null);
        block = new BlockStatement();
        InnerClassCompletionVisitor.setMethodDispatcherCode(block, new ClassExpression(outerClass), parameters);
        method.setCode(block);
        parameters = new Parameter[]{new Parameter(ClassHelper.STRING_TYPE, "name"), new Parameter(ClassHelper.OBJECT_TYPE, "val")};
        methodName = "propertyMissing";
        if (isStatic) {
            this.addCompilationErrorOnCustomMethodNode(node, methodName, parameters);
        }
        method = node.addSyntheticMethod(methodName, 1, ClassHelper.VOID_TYPE, parameters, ClassNode.EMPTY_ARRAY, null);
        block = new BlockStatement();
        if (isStatic) {
            InnerClassCompletionVisitor.setPropertySetterDispatcher(block, new ClassExpression(outerClass), parameters);
        } else {
            block.addStatement(new BytecodeSequence(new BytecodeInstruction(){

                @Override
                public void visit(MethodVisitor mv) {
                    InnerClassCompletionVisitor.this.getThis(mv, classInternalName, outerClassDescriptor, outerClassInternalName);
                    mv.visitVarInsn(25, 1);
                    mv.visitVarInsn(25, 2);
                    mv.visitMethodInsn(182, outerClassInternalName, "this$dist$set$" + objectDistance, "(Ljava/lang/String;Ljava/lang/Object;)V", false);
                    mv.visitInsn(177);
                }
            }));
        }
        method.setCode(block);
        methodName = "$static_propertyMissing";
        if (isStatic) {
            this.addCompilationErrorOnCustomMethodNode(node, methodName, parameters);
        }
        method = node.addSyntheticMethod(methodName, 9, ClassHelper.VOID_TYPE, parameters, ClassNode.EMPTY_ARRAY, null);
        block = new BlockStatement();
        InnerClassCompletionVisitor.setPropertySetterDispatcher(block, new ClassExpression(outerClass), parameters);
        method.setCode(block);
        parameters = new Parameter[]{new Parameter(ClassHelper.STRING_TYPE, "name")};
        methodName = "propertyMissing";
        if (isStatic) {
            this.addCompilationErrorOnCustomMethodNode(node, methodName, parameters);
        }
        method = node.addSyntheticMethod(methodName, 1, ClassHelper.OBJECT_TYPE, parameters, ClassNode.EMPTY_ARRAY, null);
        block = new BlockStatement();
        if (isStatic) {
            InnerClassCompletionVisitor.setPropertyGetterDispatcher(block, new ClassExpression(outerClass), parameters);
        } else {
            block.addStatement(new BytecodeSequence(new BytecodeInstruction(){

                @Override
                public void visit(MethodVisitor mv) {
                    InnerClassCompletionVisitor.this.getThis(mv, classInternalName, outerClassDescriptor, outerClassInternalName);
                    mv.visitVarInsn(25, 1);
                    mv.visitMethodInsn(182, outerClassInternalName, "this$dist$get$" + objectDistance, "(Ljava/lang/String;)Ljava/lang/Object;", false);
                    mv.visitInsn(176);
                }
            }));
        }
        method.setCode(block);
        methodName = "$static_propertyMissing";
        if (isStatic) {
            this.addCompilationErrorOnCustomMethodNode(node, methodName, parameters);
        }
        method = node.addSyntheticMethod(methodName, 9, ClassHelper.OBJECT_TYPE, parameters, ClassNode.EMPTY_ARRAY, null);
        block = new BlockStatement();
        InnerClassCompletionVisitor.setPropertyGetterDispatcher(block, new ClassExpression(outerClass), parameters);
        method.setCode(block);
    }

    private void addCompilationErrorOnCustomMethodNode(InnerClassNode node, String methodName, Parameter[] parameters) {
        MethodNode existingMethodNode = node.getMethod(methodName, parameters);
        if (existingMethodNode != null && !existingMethodNode.isSynthetic()) {
            this.addError("\"" + methodName + "\" implementations are not supported on static inner classes as a synthetic version of \"" + methodName + "\" is added during compilation for the purpose of outer class delegation.", existingMethodNode);
        }
    }

    private void addThisReference(ConstructorNode node) {
        Parameter thisPara;
        if (!InnerClassCompletionVisitor.shouldHandleImplicitThisForInnerClass(this.classNode)) {
            return;
        }
        Statement code = node.getCode();
        Parameter[] params = node.getParameters();
        Parameter[] newParams = new Parameter[params.length + 1];
        System.arraycopy(params, 0, newParams, 1, params.length);
        String name = this.getUniqueName(params, node);
        newParams[0] = thisPara = new Parameter(this.classNode.getOuterClass().getPlainNodeReference(), name);
        node.setParameters(newParams);
        BlockStatement block = null;
        if (code == null) {
            block = new BlockStatement();
        } else if (!(code instanceof BlockStatement)) {
            block = new BlockStatement();
            block.addStatement(code);
        } else {
            block = (BlockStatement)code;
        }
        BlockStatement newCode = new BlockStatement();
        InnerClassCompletionVisitor.addFieldInit(thisPara, this.thisField, newCode);
        ConstructorCallExpression cce = InnerClassCompletionVisitor.getFirstIfSpecialConstructorCall(block);
        if (cce == null) {
            cce = new ConstructorCallExpression(ClassNode.SUPER, new TupleExpression());
            block.getStatements().add(0, new ExpressionStatement(cce));
        }
        if (this.shouldImplicitlyPassThisPara(cce)) {
            TupleExpression args = (TupleExpression)cce.getArguments();
            List<Expression> expressions = args.getExpressions();
            VariableExpression ve = new VariableExpression(thisPara.getName());
            ve.setAccessedVariable(thisPara);
            expressions.add(0, ve);
        }
        if (cce.isSuperCall()) {
            block.getStatements().add(1, newCode);
        }
        node.setCode(block);
    }

    private boolean shouldImplicitlyPassThisPara(ConstructorCallExpression cce) {
        InnerClassNode superInnerCN;
        boolean pass = false;
        ClassNode superCN = this.classNode.getSuperClass();
        if (cce.isThisCall()) {
            pass = true;
        } else if (cce.isSuperCall() && !superCN.isEnum() && !superCN.isInterface() && superCN instanceof InnerClassNode && !InnerClassCompletionVisitor.isStatic(superInnerCN = (InnerClassNode)superCN) && this.classNode.getOuterClass().isDerivedFrom(superCN.getOuterClass())) {
            pass = true;
        }
        return pass;
    }

    private String getUniqueName(Parameter[] params, ConstructorNode node) {
        String namePrefix = "$p";
        block0: for (int i = 0; i < 100; ++i) {
            namePrefix = namePrefix + "$";
            for (Parameter p : params) {
                if (p.getName().equals(namePrefix)) continue block0;
            }
            return namePrefix;
        }
        this.addError("unable to find a unique prefix name for synthetic this reference in inner class constructor", node);
        return namePrefix;
    }

    private static ConstructorCallExpression getFirstIfSpecialConstructorCall(BlockStatement code) {
        if (code == null) {
            return null;
        }
        List<Statement> statementList = code.getStatements();
        if (statementList.isEmpty()) {
            return null;
        }
        Statement statement = statementList.get(0);
        if (!(statement instanceof ExpressionStatement)) {
            return null;
        }
        Expression expression = ((ExpressionStatement)statement).getExpression();
        if (!(expression instanceof ConstructorCallExpression)) {
            return null;
        }
        ConstructorCallExpression cce = (ConstructorCallExpression)expression;
        if (cce.isSpecialCall()) {
            return cce;
        }
        return null;
    }
}

