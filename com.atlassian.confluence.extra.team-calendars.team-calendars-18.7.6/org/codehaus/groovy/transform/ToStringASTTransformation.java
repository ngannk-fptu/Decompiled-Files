/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovy.transform.ToString;
import java.util.ArrayList;
import java.util.Iterator;
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
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.CanonicalASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.CANONICALIZATION)
public class ToStringASTTransformation
extends AbstractASTTransformation {
    static final Class MY_CLASS = ToString.class;
    static final ClassNode MY_TYPE = ClassHelper.make(MY_CLASS);
    static final String MY_TYPE_NAME = "@" + MY_TYPE.getNameWithoutPackage();
    private static final ClassNode STRINGBUILDER_TYPE = ClassHelper.make(StringBuilder.class);
    private static final ClassNode INVOKER_TYPE = ClassHelper.make(InvokerHelper.class);

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        this.init(nodes, source);
        AnnotatedNode parent = (AnnotatedNode)nodes[1];
        AnnotationNode anno = (AnnotationNode)nodes[0];
        if (!MY_TYPE.equals(anno.getClassNode())) {
            return;
        }
        if (parent instanceof ClassNode) {
            boolean includePackage;
            ClassNode cNode = (ClassNode)parent;
            if (!this.checkNotInterface(cNode, MY_TYPE_NAME)) {
                return;
            }
            boolean includeSuper = this.memberHasValue(anno, "includeSuper", true);
            boolean includeSuperProperties = this.memberHasValue(anno, "includeSuperProperties", true);
            boolean cacheToString = this.memberHasValue(anno, "cache", true);
            if (includeSuper && cNode.getSuperClass().getName().equals("java.lang.Object")) {
                this.addError("Error during " + MY_TYPE_NAME + " processing: includeSuper=true but '" + cNode.getName() + "' has no super class.", anno);
            }
            boolean includeNames = this.memberHasValue(anno, "includeNames", true);
            boolean includeFields = this.memberHasValue(anno, "includeFields", true);
            List<String> excludes = ToStringASTTransformation.getMemberList(anno, "excludes");
            List<String> includes = ToStringASTTransformation.getMemberList(anno, "includes");
            boolean ignoreNulls = this.memberHasValue(anno, "ignoreNulls", true);
            boolean bl = includePackage = !this.memberHasValue(anno, "includePackage", false);
            if (this.hasAnnotation(cNode, CanonicalASTTransformation.MY_TYPE)) {
                AnnotationNode canonical = cNode.getAnnotations(CanonicalASTTransformation.MY_TYPE).get(0);
                if (excludes == null || excludes.isEmpty()) {
                    excludes = ToStringASTTransformation.getMemberList(canonical, "excludes");
                }
                if (includes == null || includes.isEmpty()) {
                    includes = ToStringASTTransformation.getMemberList(canonical, "includes");
                }
            }
            if (!this.checkIncludeExclude(anno, excludes, includes, MY_TYPE_NAME)) {
                return;
            }
            ToStringASTTransformation.createToString(cNode, includeSuper, includeFields, excludes, includes, includeNames, ignoreNulls, includePackage, cacheToString, includeSuperProperties);
        }
    }

    public static void createToString(ClassNode cNode, boolean includeSuper, boolean includeFields, List<String> excludes, List<String> includes, boolean includeNames) {
        ToStringASTTransformation.createToString(cNode, includeSuper, includeFields, excludes, includes, includeNames, false);
    }

    public static void createToString(ClassNode cNode, boolean includeSuper, boolean includeFields, List<String> excludes, List<String> includes, boolean includeNames, boolean ignoreNulls) {
        ToStringASTTransformation.createToString(cNode, includeSuper, includeFields, excludes, includes, includeNames, ignoreNulls, true);
    }

    public static void createToString(ClassNode cNode, boolean includeSuper, boolean includeFields, List<String> excludes, List<String> includes, boolean includeNames, boolean ignoreNulls, boolean includePackage) {
        ToStringASTTransformation.createToString(cNode, includeSuper, includeFields, excludes, includes, includeNames, ignoreNulls, includePackage, false);
    }

    public static void createToString(ClassNode cNode, boolean includeSuper, boolean includeFields, List<String> excludes, List<String> includes, boolean includeNames, boolean ignoreNulls, boolean includePackage, boolean cache) {
        ToStringASTTransformation.createToString(cNode, includeSuper, includeFields, excludes, includes, includeNames, ignoreNulls, includePackage, false, false);
    }

    public static void createToString(ClassNode cNode, boolean includeSuper, boolean includeFields, List<String> excludes, List<String> includes, boolean includeNames, boolean ignoreNulls, boolean includePackage, boolean cache, boolean includeSuperProperties) {
        Expression tempToString;
        boolean hasExistingToString = GeneralUtils.hasDeclaredMethod(cNode, "toString", 0);
        if (hasExistingToString && GeneralUtils.hasDeclaredMethod(cNode, "_toString", 0)) {
            return;
        }
        BlockStatement body = new BlockStatement();
        if (cache) {
            FieldNode cacheField = cNode.addField("$to$string", 4098, ClassHelper.STRING_TYPE, null);
            VariableExpression savedToString = GeneralUtils.varX(cacheField);
            body.addStatement(GeneralUtils.ifS((Expression)GeneralUtils.equalsNullX(savedToString), GeneralUtils.assignS(savedToString, ToStringASTTransformation.calculateToStringStatements(cNode, includeSuper, includeFields, excludes, includes, includeNames, ignoreNulls, includePackage, includeSuperProperties, body))));
            tempToString = savedToString;
        } else {
            tempToString = ToStringASTTransformation.calculateToStringStatements(cNode, includeSuper, includeFields, excludes, includes, includeNames, ignoreNulls, includePackage, includeSuperProperties, body);
        }
        body.addStatement(GeneralUtils.returnS(tempToString));
        cNode.addMethod(new MethodNode(hasExistingToString ? "_toString" : "toString", hasExistingToString ? 2 : 1, ClassHelper.STRING_TYPE, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, body));
    }

    private static Expression calculateToStringStatements(ClassNode cNode, boolean includeSuper, boolean includeFields, List<String> excludes, List<String> includes, boolean includeNames, boolean ignoreNulls, boolean includePackage, boolean includeSuperProperties, BlockStatement body) {
        List<PropertyNode> pList;
        VariableExpression result = GeneralUtils.varX("_result");
        body.addStatement(GeneralUtils.declS(result, GeneralUtils.ctorX(STRINGBUILDER_TYPE)));
        VariableExpression first = GeneralUtils.varX("$toStringFirst");
        body.addStatement(GeneralUtils.declS(first, GeneralUtils.constX(Boolean.TRUE)));
        String className = includePackage ? cNode.getName() : cNode.getNameWithoutPackage();
        body.addStatement(ToStringASTTransformation.appendS(result, GeneralUtils.constX(className + "(")));
        if (includeSuperProperties) {
            pList = GeneralUtils.getAllProperties(cNode);
            Iterator<PropertyNode> pIterator = pList.iterator();
            while (pIterator.hasNext()) {
                if (!pIterator.next().isStatic()) continue;
                pIterator.remove();
            }
        } else {
            pList = GeneralUtils.getInstanceProperties(cNode);
        }
        for (PropertyNode pNode : pList) {
            if (ToStringASTTransformation.shouldSkip(pNode.getName(), excludes, includes)) continue;
            Expression getter = GeneralUtils.getterThisX(cNode, pNode);
            ToStringASTTransformation.appendValue(body, result, first, getter, pNode.getName(), includeNames, ignoreNulls);
        }
        if (includeFields) {
            ArrayList<FieldNode> fList = new ArrayList<FieldNode>();
            fList.addAll(GeneralUtils.getInstanceNonPropertyFields(cNode));
            for (FieldNode fNode : fList) {
                if (ToStringASTTransformation.shouldSkip(fNode.getName(), excludes, includes)) continue;
                ToStringASTTransformation.appendValue(body, result, first, GeneralUtils.varX(fNode), fNode.getName(), includeNames, ignoreNulls);
            }
        }
        if (includeSuper) {
            ToStringASTTransformation.appendCommaIfNotFirst(body, result, first);
            ToStringASTTransformation.appendPrefix(body, result, "super", includeNames);
            body.addStatement(ToStringASTTransformation.appendS(result, GeneralUtils.callSuperX("toString")));
        }
        body.addStatement(ToStringASTTransformation.appendS(result, GeneralUtils.constX(")")));
        MethodCallExpression toString = GeneralUtils.callX(result, "toString");
        toString.setImplicitThis(false);
        return toString;
    }

    private static void appendValue(BlockStatement body, Expression result, VariableExpression first, Expression value, String name, boolean includeNames, boolean ignoreNulls) {
        BlockStatement thenBlock = new BlockStatement();
        BlockStatement appendValue = ignoreNulls ? GeneralUtils.ifS((Expression)GeneralUtils.notNullX(value), thenBlock) : thenBlock;
        ToStringASTTransformation.appendCommaIfNotFirst(thenBlock, result, first);
        ToStringASTTransformation.appendPrefix(thenBlock, result, name, includeNames);
        thenBlock.addStatement(GeneralUtils.ifElseS(GeneralUtils.sameX(value, new VariableExpression("this")), ToStringASTTransformation.appendS(result, GeneralUtils.constX("(this)")), ToStringASTTransformation.appendS(result, GeneralUtils.callX(INVOKER_TYPE, "toString", value))));
        body.addStatement(appendValue);
    }

    private static void appendCommaIfNotFirst(BlockStatement body, Expression result, VariableExpression first) {
        body.addStatement(GeneralUtils.ifElseS(first, GeneralUtils.assignS(first, ConstantExpression.FALSE), ToStringASTTransformation.appendS(result, GeneralUtils.constX(", "))));
    }

    private static void appendPrefix(BlockStatement body, Expression result, String name, boolean includeNames) {
        if (includeNames) {
            body.addStatement(ToStringASTTransformation.toStringPropertyName(result, name));
        }
    }

    private static Statement toStringPropertyName(Expression result, String fName) {
        BlockStatement body = new BlockStatement();
        body.addStatement(ToStringASTTransformation.appendS(result, GeneralUtils.constX(fName + ":")));
        return body;
    }

    private static Statement appendS(Expression result, Expression expr) {
        MethodCallExpression append = GeneralUtils.callX(result, "append", expr);
        append.setImplicitThis(false);
        return GeneralUtils.stmt(append);
    }
}

