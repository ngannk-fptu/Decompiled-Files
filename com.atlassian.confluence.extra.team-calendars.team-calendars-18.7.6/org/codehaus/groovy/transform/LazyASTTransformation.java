/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import java.lang.ref.SoftReference;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.SynchronizedStatement;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.runtime.MetaClassHelper;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
public class LazyASTTransformation
extends AbstractASTTransformation {
    private static final ClassNode SOFT_REF = ClassHelper.makeWithoutCaching(SoftReference.class, false);
    private static final Expression NULL_EXPR = ConstantExpression.NULL;

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        this.init(nodes, source);
        AnnotatedNode parent = (AnnotatedNode)nodes[1];
        AnnotationNode node = (AnnotationNode)nodes[0];
        if (parent instanceof FieldNode) {
            FieldNode fieldNode = (FieldNode)parent;
            LazyASTTransformation.visitField(node, fieldNode);
        }
    }

    static void visitField(AnnotationNode node, FieldNode fieldNode) {
        Expression soft = node.getMember("soft");
        Expression init = LazyASTTransformation.getInitExpr(fieldNode);
        String backingFieldName = "$" + fieldNode.getName();
        fieldNode.rename(backingFieldName);
        fieldNode.setModifiers(2 | fieldNode.getModifiers() & 0xFFFFFFFA);
        PropertyNode pNode = fieldNode.getDeclaringClass().getProperty(backingFieldName);
        if (pNode != null) {
            fieldNode.getDeclaringClass().getProperties().remove(pNode);
        }
        if (soft instanceof ConstantExpression && ((ConstantExpression)soft).getValue().equals(true)) {
            LazyASTTransformation.createSoft(fieldNode, init);
        } else {
            LazyASTTransformation.create(fieldNode, init);
            if (ClassHelper.isPrimitiveType(fieldNode.getType())) {
                fieldNode.setType(ClassHelper.getWrapper(fieldNode.getType()));
            }
        }
    }

    private static void create(FieldNode fieldNode, Expression initExpr) {
        BlockStatement body = new BlockStatement();
        if (fieldNode.isStatic()) {
            LazyASTTransformation.addHolderClassIdiomBody(body, fieldNode, initExpr);
        } else if (fieldNode.isVolatile()) {
            LazyASTTransformation.addDoubleCheckedLockingBody(body, fieldNode, initExpr);
        } else {
            LazyASTTransformation.addNonThreadSafeBody(body, fieldNode, initExpr);
        }
        LazyASTTransformation.addMethod(fieldNode, body, fieldNode.getType());
    }

    private static void addHolderClassIdiomBody(BlockStatement body, FieldNode fieldNode, Expression initExpr) {
        ClassNode declaringClass = fieldNode.getDeclaringClass();
        ClassNode fieldType = fieldNode.getType();
        int visibility = 10;
        String fullName = declaringClass.getName() + "$" + fieldType.getNameWithoutPackage() + "Holder_" + fieldNode.getName().substring(1);
        InnerClassNode holderClass = new InnerClassNode(declaringClass, fullName, 10, ClassHelper.OBJECT_TYPE);
        String innerFieldName = "INSTANCE";
        String initializeMethodName = (fullName + "_initExpr").replace('.', '_');
        declaringClass.addMethod(initializeMethodName, 26, fieldType, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, GeneralUtils.returnS(initExpr));
        holderClass.addField("INSTANCE", 26, fieldType, GeneralUtils.callX(declaringClass, initializeMethodName));
        Expression innerField = GeneralUtils.propX((Expression)GeneralUtils.classX(holderClass), "INSTANCE");
        declaringClass.getModule().addClass(holderClass);
        body.addStatement(GeneralUtils.returnS(innerField));
    }

    private static void addDoubleCheckedLockingBody(BlockStatement body, FieldNode fieldNode, Expression initExpr) {
        VariableExpression fieldExpr = GeneralUtils.varX(fieldNode);
        VariableExpression localVar = GeneralUtils.varX(fieldNode.getName() + "_local");
        body.addStatement(GeneralUtils.declS(localVar, fieldExpr));
        body.addStatement(GeneralUtils.ifElseS(GeneralUtils.notNullX(localVar), GeneralUtils.returnS(localVar), new SynchronizedStatement(LazyASTTransformation.syncTarget(fieldNode), GeneralUtils.ifElseS(GeneralUtils.notNullX(fieldExpr), GeneralUtils.returnS(fieldExpr), GeneralUtils.returnS(GeneralUtils.assignX(fieldExpr, initExpr))))));
    }

    private static void addNonThreadSafeBody(BlockStatement body, FieldNode fieldNode, Expression initExpr) {
        VariableExpression fieldExpr = GeneralUtils.varX(fieldNode);
        body.addStatement(GeneralUtils.ifElseS(GeneralUtils.notNullX(fieldExpr), GeneralUtils.stmt(fieldExpr), GeneralUtils.assignS(fieldExpr, initExpr)));
    }

    private static void addMethod(FieldNode fieldNode, BlockStatement body, ClassNode type) {
        int visibility = 1;
        if (fieldNode.isStatic()) {
            visibility |= 8;
        }
        String propName = MetaClassHelper.capitalize(fieldNode.getName().substring(1));
        fieldNode.getDeclaringClass().addMethod("get" + propName, visibility, type, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, body);
        if (ClassHelper.boolean_TYPE.equals(type)) {
            fieldNode.getDeclaringClass().addMethod("is" + propName, visibility, type, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, GeneralUtils.stmt(GeneralUtils.callThisX("get" + propName)));
        }
    }

    private static void createSoft(FieldNode fieldNode, Expression initExpr) {
        ClassNode type = fieldNode.getType();
        fieldNode.setType(SOFT_REF);
        LazyASTTransformation.createSoftGetter(fieldNode, initExpr, type);
        LazyASTTransformation.createSoftSetter(fieldNode, type);
    }

    private static void createSoftGetter(FieldNode fieldNode, Expression initExpr, ClassNode type) {
        BlockStatement body = new BlockStatement();
        VariableExpression fieldExpr = GeneralUtils.varX(fieldNode);
        VariableExpression resExpr = GeneralUtils.varX("res", type);
        MethodCallExpression callExpression = GeneralUtils.callX(fieldExpr, "get");
        callExpression.setSafe(true);
        body.addStatement(GeneralUtils.declS(resExpr, callExpression));
        Statement mainIf = GeneralUtils.ifElseS(GeneralUtils.notNullX(resExpr), GeneralUtils.stmt(resExpr), GeneralUtils.block(GeneralUtils.assignS(resExpr, initExpr), GeneralUtils.assignS(fieldExpr, GeneralUtils.ctorX(SOFT_REF, resExpr)), GeneralUtils.stmt(resExpr)));
        if (fieldNode.isVolatile()) {
            body.addStatement(GeneralUtils.ifElseS(GeneralUtils.notNullX(resExpr), GeneralUtils.stmt(resExpr), new SynchronizedStatement(LazyASTTransformation.syncTarget(fieldNode), GeneralUtils.block(GeneralUtils.assignS(resExpr, callExpression), mainIf))));
        } else {
            body.addStatement(mainIf);
        }
        LazyASTTransformation.addMethod(fieldNode, body, type);
    }

    private static void createSoftSetter(FieldNode fieldNode, ClassNode type) {
        BlockStatement body = new BlockStatement();
        VariableExpression fieldExpr = GeneralUtils.varX(fieldNode);
        String name = "set" + MetaClassHelper.capitalize(fieldNode.getName().substring(1));
        Parameter parameter = GeneralUtils.param(type, "value");
        VariableExpression paramExpr = GeneralUtils.varX(parameter);
        body.addStatement(GeneralUtils.ifElseS(GeneralUtils.notNullX(paramExpr), GeneralUtils.assignS(fieldExpr, GeneralUtils.ctorX(SOFT_REF, paramExpr)), GeneralUtils.assignS(fieldExpr, NULL_EXPR)));
        int visibility = 1;
        if (fieldNode.isStatic()) {
            visibility |= 8;
        }
        fieldNode.getDeclaringClass().addMethod(name, visibility, ClassHelper.VOID_TYPE, GeneralUtils.params(parameter), ClassNode.EMPTY_ARRAY, body);
    }

    private static Expression syncTarget(FieldNode fieldNode) {
        return fieldNode.isStatic() ? GeneralUtils.classX(fieldNode.getDeclaringClass()) : GeneralUtils.varX("this");
    }

    private static Expression getInitExpr(FieldNode fieldNode) {
        Expression initExpr = fieldNode.getInitialValueExpression();
        fieldNode.setInitialValueExpression(null);
        if (initExpr == null) {
            initExpr = GeneralUtils.ctorX(fieldNode.getType());
        }
        return initExpr;
    }
}

