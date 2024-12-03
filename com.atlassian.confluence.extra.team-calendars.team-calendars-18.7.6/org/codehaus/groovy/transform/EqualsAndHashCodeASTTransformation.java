/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovy.transform.EqualsAndHashCode;
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
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.ast.tools.GenericsUtils;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.CanonicalASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.codehaus.groovy.transform.stc.StaticTypeCheckingSupport;
import org.codehaus.groovy.util.HashCodeHelper;

@GroovyASTTransformation(phase=CompilePhase.CANONICALIZATION)
public class EqualsAndHashCodeASTTransformation
extends AbstractASTTransformation {
    static final Class MY_CLASS = EqualsAndHashCode.class;
    static final ClassNode MY_TYPE = ClassHelper.make(MY_CLASS);
    static final String MY_TYPE_NAME = "@" + MY_TYPE.getNameWithoutPackage();
    private static final ClassNode HASHUTIL_TYPE = ClassHelper.make(HashCodeHelper.class);
    private static final ClassNode OBJECT_TYPE = GenericsUtils.makeClassSafe(Object.class);

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        this.init(nodes, source);
        AnnotatedNode parent = (AnnotatedNode)nodes[1];
        AnnotationNode anno = (AnnotationNode)nodes[0];
        if (!MY_TYPE.equals(anno.getClassNode())) {
            return;
        }
        if (parent instanceof ClassNode) {
            boolean useCanEqual;
            ClassNode cNode = (ClassNode)parent;
            if (!this.checkNotInterface(cNode, MY_TYPE_NAME)) {
                return;
            }
            boolean callSuper = this.memberHasValue(anno, "callSuper", true);
            boolean cacheHashCode = this.memberHasValue(anno, "cache", true);
            boolean bl = useCanEqual = !this.memberHasValue(anno, "useCanEqual", false);
            if (callSuper && cNode.getSuperClass().getName().equals("java.lang.Object")) {
                this.addError("Error during " + MY_TYPE_NAME + " processing: callSuper=true but '" + cNode.getName() + "' has no super class.", anno);
            }
            boolean includeFields = this.memberHasValue(anno, "includeFields", true);
            List<String> excludes = EqualsAndHashCodeASTTransformation.getMemberList(anno, "excludes");
            List<String> includes = EqualsAndHashCodeASTTransformation.getMemberList(anno, "includes");
            if (this.hasAnnotation(cNode, CanonicalASTTransformation.MY_TYPE)) {
                AnnotationNode canonical = cNode.getAnnotations(CanonicalASTTransformation.MY_TYPE).get(0);
                if (excludes == null || excludes.isEmpty()) {
                    excludes = EqualsAndHashCodeASTTransformation.getMemberList(canonical, "excludes");
                }
                if (includes == null || includes.isEmpty()) {
                    includes = EqualsAndHashCodeASTTransformation.getMemberList(canonical, "includes");
                }
            }
            if (!this.checkIncludeExclude(anno, excludes, includes, MY_TYPE_NAME)) {
                return;
            }
            EqualsAndHashCodeASTTransformation.createHashCode(cNode, cacheHashCode, includeFields, callSuper, excludes, includes);
            EqualsAndHashCodeASTTransformation.createEquals(cNode, includeFields, callSuper, useCanEqual, excludes, includes);
        }
    }

    public static void createHashCode(ClassNode cNode, boolean cacheResult, boolean includeFields, boolean callSuper, List<String> excludes, List<String> includes) {
        boolean hasExistingHashCode = GeneralUtils.hasDeclaredMethod(cNode, "hashCode", 0);
        if (hasExistingHashCode && GeneralUtils.hasDeclaredMethod(cNode, "_hashCode", 0)) {
            return;
        }
        BlockStatement body = new BlockStatement();
        if (cacheResult) {
            FieldNode hashField = cNode.addField("$hash$code", 4098, ClassHelper.int_TYPE, null);
            VariableExpression hash = GeneralUtils.varX(hashField);
            body.addStatement(GeneralUtils.ifS((Expression)GeneralUtils.isZeroX(hash), EqualsAndHashCodeASTTransformation.calculateHashStatements(cNode, hash, includeFields, callSuper, excludes, includes)));
            body.addStatement(GeneralUtils.returnS(hash));
        } else {
            body.addStatement(EqualsAndHashCodeASTTransformation.calculateHashStatements(cNode, null, includeFields, callSuper, excludes, includes));
        }
        cNode.addMethod(new MethodNode(hasExistingHashCode ? "_hashCode" : "hashCode", hasExistingHashCode ? 2 : 1, ClassHelper.int_TYPE, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, body));
    }

    private static Statement calculateHashStatements(ClassNode cNode, Expression hash, boolean includeFields, boolean callSuper, List<String> excludes, List<String> includes) {
        StaticMethodCallExpression current;
        List<PropertyNode> pList = GeneralUtils.getInstanceProperties(cNode);
        ArrayList<FieldNode> fList = new ArrayList<FieldNode>();
        if (includeFields) {
            fList.addAll(GeneralUtils.getInstanceNonPropertyFields(cNode));
        }
        BlockStatement body = new BlockStatement();
        VariableExpression result = GeneralUtils.varX("_result");
        body.addStatement(GeneralUtils.declS(result, GeneralUtils.callX(HASHUTIL_TYPE, "initHash")));
        for (PropertyNode pNode : pList) {
            if (EqualsAndHashCodeASTTransformation.shouldSkip(pNode.getName(), excludes, includes)) continue;
            Expression getter = GeneralUtils.getterThisX(cNode, pNode);
            current = GeneralUtils.callX(HASHUTIL_TYPE, "updateHash", (Expression)GeneralUtils.args(result, getter));
            body.addStatement(GeneralUtils.ifS((Expression)GeneralUtils.notX(GeneralUtils.sameX(getter, GeneralUtils.varX("this"))), GeneralUtils.assignS(result, current)));
        }
        for (FieldNode fNode : fList) {
            if (EqualsAndHashCodeASTTransformation.shouldSkip(fNode.getName(), excludes, includes)) continue;
            VariableExpression fieldExpr = GeneralUtils.varX(fNode);
            current = GeneralUtils.callX(HASHUTIL_TYPE, "updateHash", (Expression)GeneralUtils.args(result, fieldExpr));
            body.addStatement(GeneralUtils.ifS((Expression)GeneralUtils.notX(GeneralUtils.sameX(fieldExpr, GeneralUtils.varX("this"))), GeneralUtils.assignS(result, current)));
        }
        if (callSuper) {
            StaticMethodCallExpression current2 = GeneralUtils.callX(HASHUTIL_TYPE, "updateHash", (Expression)GeneralUtils.args(result, GeneralUtils.callSuperX("hashCode")));
            body.addStatement(GeneralUtils.assignS(result, current2));
        }
        if (hash != null) {
            body.addStatement(GeneralUtils.assignS(hash, result));
        } else {
            body.addStatement(GeneralUtils.returnS(result));
        }
        return body;
    }

    private static void createCanEqual(ClassNode cNode) {
        boolean hasExistingCanEqual = GeneralUtils.hasDeclaredMethod(cNode, "canEqual", 1);
        if (hasExistingCanEqual && GeneralUtils.hasDeclaredMethod(cNode, "_canEqual", 1)) {
            return;
        }
        BlockStatement body = new BlockStatement();
        VariableExpression other = GeneralUtils.varX("other");
        body.addStatement(GeneralUtils.returnS(GeneralUtils.isInstanceOfX(other, GenericsUtils.nonGeneric(cNode))));
        cNode.addMethod(new MethodNode(hasExistingCanEqual ? "_canEqual" : "canEqual", hasExistingCanEqual ? 2 : 1, ClassHelper.boolean_TYPE, GeneralUtils.params(GeneralUtils.param(OBJECT_TYPE, other.getName())), ClassNode.EMPTY_ARRAY, body));
    }

    public static void createEquals(ClassNode cNode, boolean includeFields, boolean callSuper, boolean useCanEqual, List<String> excludes, List<String> includes) {
        boolean hasExistingEquals;
        if (useCanEqual) {
            EqualsAndHashCodeASTTransformation.createCanEqual(cNode);
        }
        if ((hasExistingEquals = GeneralUtils.hasDeclaredMethod(cNode, "equals", 1)) && GeneralUtils.hasDeclaredMethod(cNode, "_equals", 1)) {
            return;
        }
        BlockStatement body = new BlockStatement();
        VariableExpression other = GeneralUtils.varX("other");
        body.addStatement(GeneralUtils.ifS((Expression)GeneralUtils.equalsNullX(other), GeneralUtils.returnS(GeneralUtils.constX(Boolean.FALSE, true))));
        body.addStatement(GeneralUtils.ifS((Expression)GeneralUtils.sameX(GeneralUtils.varX("this"), other), GeneralUtils.returnS(GeneralUtils.constX(Boolean.TRUE, true))));
        if (useCanEqual) {
            body.addStatement(GeneralUtils.ifS((Expression)GeneralUtils.notX(GeneralUtils.isInstanceOfX(other, GenericsUtils.nonGeneric(cNode))), GeneralUtils.returnS(GeneralUtils.constX(Boolean.FALSE, true))));
        } else {
            body.addStatement(GeneralUtils.ifS((Expression)GeneralUtils.notX(GeneralUtils.hasClassX(other, GenericsUtils.nonGeneric(cNode))), GeneralUtils.returnS(GeneralUtils.constX(Boolean.FALSE, true))));
        }
        VariableExpression otherTyped = GeneralUtils.varX("otherTyped", GenericsUtils.nonGeneric(cNode));
        CastExpression castExpression = new CastExpression(GenericsUtils.nonGeneric(cNode), other);
        castExpression.setStrict(true);
        body.addStatement(GeneralUtils.declS(otherTyped, castExpression));
        if (useCanEqual) {
            body.addStatement(GeneralUtils.ifS((Expression)GeneralUtils.notX(GeneralUtils.callX((Expression)otherTyped, "canEqual", (Expression)GeneralUtils.varX("this"))), GeneralUtils.returnS(GeneralUtils.constX(Boolean.FALSE, true))));
        }
        List<PropertyNode> pList = GeneralUtils.getInstanceProperties(cNode);
        for (PropertyNode pNode : pList) {
            if (EqualsAndHashCodeASTTransformation.shouldSkip(pNode.getName(), excludes, includes)) continue;
            boolean canBeSelf = StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(pNode.getOriginType(), cNode);
            if (!canBeSelf) {
                body.addStatement(GeneralUtils.ifS((Expression)GeneralUtils.notX(GeneralUtils.hasEqualPropertyX(pNode, otherTyped)), GeneralUtils.returnS(GeneralUtils.constX(Boolean.FALSE, true))));
                continue;
            }
            body.addStatement(GeneralUtils.ifS((Expression)GeneralUtils.notX(GeneralUtils.hasSamePropertyX(pNode, otherTyped)), GeneralUtils.ifElseS(EqualsAndHashCodeASTTransformation.differentSelfRecursivePropertyX(pNode, otherTyped), GeneralUtils.returnS(GeneralUtils.constX(Boolean.FALSE, true)), GeneralUtils.ifS((Expression)GeneralUtils.notX(EqualsAndHashCodeASTTransformation.bothSelfRecursivePropertyX(pNode, otherTyped)), GeneralUtils.ifS((Expression)GeneralUtils.notX(GeneralUtils.hasEqualPropertyX(pNode, otherTyped)), GeneralUtils.returnS(GeneralUtils.constX(Boolean.FALSE, true)))))));
        }
        ArrayList<FieldNode> fList = new ArrayList<FieldNode>();
        if (includeFields) {
            fList.addAll(GeneralUtils.getInstanceNonPropertyFields(cNode));
        }
        for (FieldNode fNode : fList) {
            if (EqualsAndHashCodeASTTransformation.shouldSkip(fNode.getName(), excludes, includes)) continue;
            body.addStatement(GeneralUtils.ifS((Expression)GeneralUtils.notX(GeneralUtils.hasSameFieldX(fNode, otherTyped)), GeneralUtils.ifElseS(EqualsAndHashCodeASTTransformation.differentSelfRecursiveFieldX(fNode, otherTyped), GeneralUtils.returnS(GeneralUtils.constX(Boolean.FALSE, true)), GeneralUtils.ifS((Expression)GeneralUtils.notX(EqualsAndHashCodeASTTransformation.bothSelfRecursiveFieldX(fNode, otherTyped)), GeneralUtils.ifS((Expression)GeneralUtils.notX(GeneralUtils.hasEqualFieldX(fNode, otherTyped)), GeneralUtils.returnS(GeneralUtils.constX(Boolean.FALSE, true)))))));
        }
        if (callSuper) {
            body.addStatement(GeneralUtils.ifS((Expression)GeneralUtils.notX(GeneralUtils.isTrueX(GeneralUtils.callSuperX("equals", other))), GeneralUtils.returnS(GeneralUtils.constX(Boolean.FALSE, true))));
        }
        body.addStatement(GeneralUtils.returnS(GeneralUtils.constX(Boolean.TRUE, true)));
        cNode.addMethod(new MethodNode(hasExistingEquals ? "_equals" : "equals", hasExistingEquals ? 2 : 1, ClassHelper.boolean_TYPE, GeneralUtils.params(GeneralUtils.param(OBJECT_TYPE, other.getName())), ClassNode.EMPTY_ARRAY, body));
    }

    private static BinaryExpression differentSelfRecursivePropertyX(PropertyNode pNode, Expression other) {
        String getterName = GeneralUtils.getGetterName(pNode);
        MethodCallExpression selfGetter = GeneralUtils.callThisX(getterName);
        MethodCallExpression otherGetter = GeneralUtils.callX(other, getterName);
        return GeneralUtils.orX(GeneralUtils.andX(GeneralUtils.sameX(selfGetter, GeneralUtils.varX("this")), GeneralUtils.notX(GeneralUtils.sameX(otherGetter, other))), GeneralUtils.andX(GeneralUtils.notX(GeneralUtils.sameX(selfGetter, GeneralUtils.varX("this"))), GeneralUtils.sameX(otherGetter, other)));
    }

    private static BinaryExpression bothSelfRecursivePropertyX(PropertyNode pNode, Expression other) {
        String getterName = GeneralUtils.getGetterName(pNode);
        MethodCallExpression selfGetter = GeneralUtils.callThisX(getterName);
        MethodCallExpression otherGetter = GeneralUtils.callX(other, getterName);
        return GeneralUtils.andX(GeneralUtils.sameX(selfGetter, GeneralUtils.varX("this")), GeneralUtils.sameX(otherGetter, other));
    }

    private static BinaryExpression differentSelfRecursiveFieldX(FieldNode fNode, Expression other) {
        VariableExpression fieldExpr = GeneralUtils.varX(fNode);
        Expression otherExpr = GeneralUtils.propX(other, fNode.getName());
        return GeneralUtils.orX(GeneralUtils.andX(GeneralUtils.sameX(fieldExpr, GeneralUtils.varX("this")), GeneralUtils.notX(GeneralUtils.sameX(otherExpr, other))), GeneralUtils.andX(GeneralUtils.notX(GeneralUtils.sameX(fieldExpr, GeneralUtils.varX("this"))), GeneralUtils.sameX(otherExpr, other)));
    }

    private static BinaryExpression bothSelfRecursiveFieldX(FieldNode fNode, Expression other) {
        VariableExpression fieldExpr = GeneralUtils.varX(fNode);
        Expression otherExpr = GeneralUtils.propX(other, fNode.getName());
        return GeneralUtils.andX(GeneralUtils.sameX(fieldExpr, GeneralUtils.varX("this")), GeneralUtils.sameX(otherExpr, other));
    }
}

