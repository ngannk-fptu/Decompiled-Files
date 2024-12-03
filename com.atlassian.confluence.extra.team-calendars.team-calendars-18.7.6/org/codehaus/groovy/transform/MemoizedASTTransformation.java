/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovy.transform.Memoized;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.ast.tools.GenericsUtils;
import org.codehaus.groovy.classgen.VariableScopeVisitor;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
public class MemoizedASTTransformation
extends AbstractASTTransformation {
    private static final String CLOSURE_CALL_METHOD_NAME = "call";
    private static final Class<Memoized> MY_CLASS = Memoized.class;
    private static final ClassNode MY_TYPE = ClassHelper.make(MY_CLASS);
    private static final String MY_TYPE_NAME = "@" + MY_TYPE.getNameWithoutPackage();
    private static final String PROTECTED_CACHE_SIZE_NAME = "protectedCacheSize";
    private static final String MAX_CACHE_SIZE_NAME = "maxCacheSize";
    private static final String CLOSURE_LABEL = "Closure";
    private static final String METHOD_LABEL = "Priv";
    private static final ClassNode OVERRIDE_CLASSNODE = ClassHelper.make(Override.class);
    private static final String MEMOIZE_METHOD_NAME = "memoize";
    private static final String MEMOIZE_AT_MOST_METHOD_NAME = "memoizeAtMost";
    private static final String MEMOIZE_AT_LEAST_METHOD_NAME = "memoizeAtLeast";
    private static final String MEMOIZE_BETWEEN_METHOD_NAME = "memoizeBetween";

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        this.init(nodes, source);
        AnnotationNode annotationNode = (AnnotationNode)nodes[0];
        AnnotatedNode annotatedNode = (AnnotatedNode)nodes[1];
        if (MY_TYPE.equals(annotationNode.getClassNode()) && annotatedNode instanceof MethodNode) {
            MethodNode methodNode = (MethodNode)annotatedNode;
            if (methodNode.isAbstract()) {
                this.addError("Annotation " + MY_TYPE_NAME + " cannot be used for abstract methods.", methodNode);
                return;
            }
            if (methodNode.isVoidMethod()) {
                this.addError("Annotation " + MY_TYPE_NAME + " cannot be used for void methods.", methodNode);
                return;
            }
            ClassNode ownerClassNode = methodNode.getDeclaringClass();
            MethodNode delegatingMethod = this.buildDelegatingMethod(methodNode, ownerClassNode);
            ownerClassNode.addMethod(delegatingMethod);
            int modifiers = 18;
            if (methodNode.isStatic()) {
                modifiers |= 8;
            }
            int protectedCacheSize = this.getMemberIntValue(annotationNode, PROTECTED_CACHE_SIZE_NAME);
            int maxCacheSize = this.getMemberIntValue(annotationNode, MAX_CACHE_SIZE_NAME);
            MethodCallExpression memoizeClosureCallExpression = this.buildMemoizeClosureCallExpression(delegatingMethod, protectedCacheSize, maxCacheSize);
            String memoizedClosureFieldName = MemoizedASTTransformation.buildUniqueName(ownerClassNode, CLOSURE_LABEL, methodNode);
            FieldNode memoizedClosureField = new FieldNode(memoizedClosureFieldName, modifiers, GenericsUtils.newClass(ClassHelper.CLOSURE_TYPE), null, memoizeClosureCallExpression);
            ownerClassNode.addField(memoizedClosureField);
            BlockStatement newCode = new BlockStatement();
            MethodCallExpression closureCallExpression = GeneralUtils.callX((Expression)GeneralUtils.fieldX(memoizedClosureField), CLOSURE_CALL_METHOD_NAME, (Expression)GeneralUtils.args(methodNode.getParameters()));
            closureCallExpression.setImplicitThis(false);
            newCode.addStatement(GeneralUtils.returnS(closureCallExpression));
            methodNode.setCode(newCode);
            VariableScopeVisitor visitor = new VariableScopeVisitor(source);
            visitor.visitClass(ownerClassNode);
        }
    }

    private MethodNode buildDelegatingMethod(MethodNode annotatedMethod, ClassNode ownerClassNode) {
        Statement code = annotatedMethod.getCode();
        int access = 4;
        if (annotatedMethod.isStatic()) {
            access = 10;
        }
        MethodNode method = new MethodNode(MemoizedASTTransformation.buildUniqueName(ownerClassNode, METHOD_LABEL, annotatedMethod), access, annotatedMethod.getReturnType(), GeneralUtils.cloneParams(annotatedMethod.getParameters()), annotatedMethod.getExceptions(), code);
        method.addAnnotations(MemoizedASTTransformation.filterAnnotations(annotatedMethod.getAnnotations()));
        return method;
    }

    private static List<AnnotationNode> filterAnnotations(List<AnnotationNode> annotations) {
        ArrayList<AnnotationNode> result = new ArrayList<AnnotationNode>(annotations.size());
        for (AnnotationNode annotation : annotations) {
            if (OVERRIDE_CLASSNODE.equals(annotation.getClassNode())) continue;
            result.add(annotation);
        }
        return result;
    }

    private MethodCallExpression buildMemoizeClosureCallExpression(MethodNode privateMethod, int protectedCacheSize, int maxCacheSize) {
        Parameter[] srcParams = privateMethod.getParameters();
        Parameter[] newParams = GeneralUtils.cloneParams(srcParams);
        ArrayList<Expression> argList = new ArrayList<Expression>(newParams.length);
        for (int i = 0; i < srcParams.length; ++i) {
            argList.add(GeneralUtils.varX(newParams[i]));
        }
        ClosureExpression expression = new ClosureExpression(newParams, GeneralUtils.stmt(GeneralUtils.callThisX(privateMethod.getName(), GeneralUtils.args(argList))));
        MethodCallExpression mce = protectedCacheSize == 0 && maxCacheSize == 0 ? GeneralUtils.callX(expression, MEMOIZE_METHOD_NAME) : (protectedCacheSize == 0 ? GeneralUtils.callX((Expression)expression, MEMOIZE_AT_MOST_METHOD_NAME, (Expression)GeneralUtils.args(GeneralUtils.constX(maxCacheSize))) : (maxCacheSize == 0 ? GeneralUtils.callX((Expression)expression, MEMOIZE_AT_LEAST_METHOD_NAME, (Expression)GeneralUtils.args(GeneralUtils.constX(protectedCacheSize))) : GeneralUtils.callX((Expression)expression, MEMOIZE_BETWEEN_METHOD_NAME, (Expression)GeneralUtils.args(GeneralUtils.constX(protectedCacheSize), GeneralUtils.constX(maxCacheSize)))));
        mce.setImplicitThis(false);
        return mce;
    }

    private static String buildUniqueName(ClassNode owner, String ident, MethodNode methodNode) {
        StringBuilder nameBuilder = new StringBuilder("memoizedMethod" + ident + "$").append(methodNode.getName());
        if (methodNode.getParameters() != null) {
            for (Parameter parameter : methodNode.getParameters()) {
                nameBuilder.append(MemoizedASTTransformation.buildTypeName(parameter.getType()));
            }
        }
        while (owner.getField(nameBuilder.toString()) != null) {
            nameBuilder.insert(0, "_");
        }
        return nameBuilder.toString();
    }

    private static String buildTypeName(ClassNode type) {
        if (type.isArray()) {
            return String.format("%sArray", MemoizedASTTransformation.buildTypeName(type.getComponentType()));
        }
        return type.getNameWithoutPackage();
    }
}

