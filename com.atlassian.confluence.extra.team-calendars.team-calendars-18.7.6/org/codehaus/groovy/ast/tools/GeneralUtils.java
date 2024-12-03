/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.Variable;
import org.codehaus.groovy.ast.VariableScope;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.AttributeExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.NotExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.TernaryExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.ThrowStatement;
import org.codehaus.groovy.classgen.Verifier;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.MetaClassHelper;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.transform.AbstractASTTransformation;

public class GeneralUtils {
    public static final Token ASSIGN = Token.newSymbol(100, -1, -1);
    public static final Token EQ = Token.newSymbol(123, -1, -1);
    public static final Token NE = Token.newSymbol(120, -1, -1);
    public static final Token LT = Token.newSymbol(124, -1, -1);
    public static final Token AND = Token.newSymbol(164, -1, -1);
    public static final Token OR = Token.newSymbol(162, -1, -1);
    public static final Token CMP = Token.newSymbol(128, -1, -1);
    private static final Token INSTANCEOF = Token.newSymbol(544, -1, -1);
    private static final Token PLUS = Token.newSymbol(200, -1, -1);
    private static final Token INDEX = Token.newSymbol("[", -1, -1);

    public static BinaryExpression andX(Expression lhv, Expression rhv) {
        return new BinaryExpression(lhv, AND, rhv);
    }

    public static ArgumentListExpression args(Expression ... expressions) {
        ArrayList<Expression> args = new ArrayList<Expression>();
        Collections.addAll(args, expressions);
        return new ArgumentListExpression(args);
    }

    public static ArgumentListExpression args(List<Expression> expressions) {
        return new ArgumentListExpression(expressions);
    }

    public static ArgumentListExpression args(Parameter[] parameters) {
        return new ArgumentListExpression(parameters);
    }

    public static ArgumentListExpression args(String ... names) {
        ArrayList<Expression> vars = new ArrayList<Expression>();
        for (String name : names) {
            vars.add(GeneralUtils.varX(name));
        }
        return new ArgumentListExpression(vars);
    }

    public static Statement assignS(Expression target, Expression value) {
        return new ExpressionStatement(GeneralUtils.assignX(target, value));
    }

    public static Expression assignX(Expression target, Expression value) {
        return new BinaryExpression(target, ASSIGN, value);
    }

    public static Expression attrX(Expression oe, Expression prop) {
        return new AttributeExpression(oe, prop);
    }

    public static BinaryExpression binX(Expression left, Token token, Expression right) {
        return new BinaryExpression(left, token, right);
    }

    public static BlockStatement block(VariableScope varScope, Statement ... stmts) {
        BlockStatement block = new BlockStatement();
        block.setVariableScope(varScope);
        for (Statement stmt : stmts) {
            block.addStatement(stmt);
        }
        return block;
    }

    public static BlockStatement block(VariableScope varScope, List<Statement> stmts) {
        BlockStatement block = new BlockStatement();
        block.setVariableScope(varScope);
        for (Statement stmt : stmts) {
            block.addStatement(stmt);
        }
        return block;
    }

    public static BlockStatement block(Statement ... stmts) {
        BlockStatement block = new BlockStatement();
        for (Statement stmt : stmts) {
            block.addStatement(stmt);
        }
        return block;
    }

    public static MethodCallExpression callSuperX(String methodName, Expression args) {
        return GeneralUtils.callX((Expression)GeneralUtils.varX("super"), methodName, args);
    }

    public static MethodCallExpression callSuperX(String methodName) {
        return GeneralUtils.callSuperX(methodName, MethodCallExpression.NO_ARGUMENTS);
    }

    public static MethodCallExpression callThisX(String methodName, Expression args) {
        return GeneralUtils.callX((Expression)GeneralUtils.varX("this"), methodName, args);
    }

    public static MethodCallExpression callThisX(String methodName) {
        return GeneralUtils.callThisX(methodName, MethodCallExpression.NO_ARGUMENTS);
    }

    public static MethodCallExpression callX(Expression receiver, String methodName, Expression args) {
        return new MethodCallExpression(receiver, methodName, args);
    }

    public static MethodCallExpression callX(Expression receiver, Expression method, Expression args) {
        return new MethodCallExpression(receiver, method, args);
    }

    public static MethodCallExpression callX(Expression receiver, String methodName) {
        return GeneralUtils.callX(receiver, methodName, MethodCallExpression.NO_ARGUMENTS);
    }

    public static StaticMethodCallExpression callX(ClassNode receiver, String methodName, Expression args) {
        return new StaticMethodCallExpression(receiver, methodName, args);
    }

    public static StaticMethodCallExpression callX(ClassNode receiver, String methodName) {
        return GeneralUtils.callX(receiver, methodName, MethodCallExpression.NO_ARGUMENTS);
    }

    public static CastExpression castX(ClassNode type, Expression expression) {
        return new CastExpression(type, expression);
    }

    public static CastExpression castX(ClassNode type, Expression expression, boolean ignoreAutoboxing) {
        return new CastExpression(type, expression, ignoreAutoboxing);
    }

    public static ClassExpression classX(ClassNode clazz) {
        return new ClassExpression(clazz);
    }

    public static ClassExpression classX(Class clazz) {
        return GeneralUtils.classX(ClassHelper.make(clazz).getPlainNodeReference());
    }

    public static ClosureExpression closureX(Parameter[] params, Statement code) {
        return new ClosureExpression(params, code);
    }

    public static ClosureExpression closureX(Statement code) {
        return GeneralUtils.closureX(Parameter.EMPTY_ARRAY, code);
    }

    public static Parameter[] cloneParams(Parameter[] source) {
        Parameter[] result = new Parameter[source.length];
        for (int i = 0; i < source.length; ++i) {
            Parameter dstParam;
            Parameter srcParam = source[i];
            result[i] = dstParam = new Parameter(srcParam.getOriginType(), srcParam.getName());
        }
        return result;
    }

    public static BinaryExpression cmpX(Expression lhv, Expression rhv) {
        return new BinaryExpression(lhv, CMP, rhv);
    }

    public static ConstantExpression constX(Object val) {
        return new ConstantExpression(val);
    }

    public static ConstantExpression constX(Object val, boolean keepPrimitive) {
        return new ConstantExpression(val, keepPrimitive);
    }

    public static void copyAnnotatedNodeAnnotations(AnnotatedNode annotatedNode, List<AnnotationNode> copied, List<AnnotationNode> notCopied) {
        List<AnnotationNode> annotationList = annotatedNode.getAnnotations();
        for (AnnotationNode annotation : annotationList) {
            PropertyExpression propertyExpression;
            boolean processAnnotation;
            List<AnnotationNode> annotations = annotation.getClassNode().getAnnotations(AbstractASTTransformation.RETENTION_CLASSNODE);
            if (annotations.isEmpty()) continue;
            if (GeneralUtils.hasClosureMember(annotation)) {
                notCopied.add(annotation);
                continue;
            }
            AnnotationNode retentionPolicyAnnotation = annotations.get(0);
            Expression valueExpression = retentionPolicyAnnotation.getMember("value");
            if (!(valueExpression instanceof PropertyExpression) || !(processAnnotation = (propertyExpression = (PropertyExpression)valueExpression).getProperty() instanceof ConstantExpression && ("RUNTIME".equals(((ConstantExpression)propertyExpression.getProperty()).getValue()) || "CLASS".equals(((ConstantExpression)propertyExpression.getProperty()).getValue())))) continue;
            AnnotationNode newAnnotation = new AnnotationNode(annotation.getClassNode());
            for (Map.Entry<String, Expression> member : annotation.getMembers().entrySet()) {
                newAnnotation.addMember(member.getKey(), member.getValue());
            }
            newAnnotation.setSourcePosition(annotatedNode);
            copied.add(newAnnotation);
        }
    }

    public static Statement createConstructorStatementDefault(FieldNode fNode) {
        String name = fNode.getName();
        ClassNode fType = fNode.getType();
        Expression fieldExpr = GeneralUtils.propX((Expression)GeneralUtils.varX("this"), name);
        Expression initExpr = fNode.getInitialValueExpression();
        Statement assignInit = initExpr == null || initExpr instanceof ConstantExpression && ((ConstantExpression)initExpr).isNullExpression() ? (ClassHelper.isPrimitiveType(fType) ? EmptyStatement.INSTANCE : GeneralUtils.assignS(fieldExpr, ConstantExpression.EMPTY_EXPRESSION)) : GeneralUtils.assignS(fieldExpr, initExpr);
        fNode.setInitialValueExpression(null);
        Expression value = GeneralUtils.findArg(name);
        return GeneralUtils.ifElseS(GeneralUtils.equalsNullX(value), assignInit, GeneralUtils.assignS(fieldExpr, GeneralUtils.castX(fType, value)));
    }

    public static ConstructorCallExpression ctorX(ClassNode type, Expression args) {
        return new ConstructorCallExpression(type, args);
    }

    public static ConstructorCallExpression ctorX(ClassNode type) {
        return new ConstructorCallExpression(type, ArgumentListExpression.EMPTY_ARGUMENTS);
    }

    public static Statement ctorSuperS(Expression args) {
        return GeneralUtils.stmt(GeneralUtils.ctorX(ClassNode.SUPER, args));
    }

    public static Statement ctorThisS(Expression args) {
        return GeneralUtils.stmt(GeneralUtils.ctorX(ClassNode.THIS, args));
    }

    public static Statement ctorSuperS() {
        return GeneralUtils.stmt(GeneralUtils.ctorX(ClassNode.SUPER));
    }

    public static Statement ctorThisS() {
        return GeneralUtils.stmt(GeneralUtils.ctorX(ClassNode.THIS));
    }

    public static Statement declS(Expression target, Expression init) {
        return new ExpressionStatement(new DeclarationExpression(target, ASSIGN, init));
    }

    public static BinaryExpression eqX(Expression lhv, Expression rhv) {
        return new BinaryExpression(lhv, EQ, rhv);
    }

    public static BooleanExpression equalsNullX(Expression argExpr) {
        return new BooleanExpression(GeneralUtils.eqX(argExpr, new ConstantExpression(null)));
    }

    public static FieldExpression fieldX(FieldNode fieldNode) {
        return new FieldExpression(fieldNode);
    }

    public static FieldExpression fieldX(ClassNode owner, String fieldName) {
        return new FieldExpression(owner.getField(fieldName));
    }

    public static Expression findArg(String argName) {
        return new PropertyExpression((Expression)new VariableExpression("args"), argName);
    }

    public static List<MethodNode> getAllMethods(ClassNode type) {
        ArrayList<MethodNode> result = new ArrayList<MethodNode>();
        for (ClassNode node = type; node != null; node = node.getSuperClass()) {
            result.addAll(node.getMethods());
        }
        return result;
    }

    public static List<PropertyNode> getAllProperties(ClassNode type) {
        ArrayList<PropertyNode> result = new ArrayList<PropertyNode>();
        for (ClassNode node = type; node != null; node = node.getSuperClass()) {
            result.addAll(node.getProperties());
        }
        return result;
    }

    public static String getGetterName(PropertyNode pNode) {
        return "get" + Verifier.capitalize(pNode.getName());
    }

    public static List<FieldNode> getInstanceNonPropertyFields(ClassNode cNode) {
        ArrayList<FieldNode> result = new ArrayList<FieldNode>();
        for (FieldNode fNode : cNode.getFields()) {
            if (fNode.isStatic() || cNode.getProperty(fNode.getName()) != null) continue;
            result.add(fNode);
        }
        return result;
    }

    public static List<PropertyNode> getInstanceProperties(ClassNode cNode) {
        ArrayList<PropertyNode> result = new ArrayList<PropertyNode>();
        for (PropertyNode pNode : cNode.getProperties()) {
            if (pNode.isStatic()) continue;
            result.add(pNode);
        }
        return result;
    }

    public static List<FieldNode> getInstancePropertyFields(ClassNode cNode) {
        ArrayList<FieldNode> result = new ArrayList<FieldNode>();
        for (PropertyNode pNode : cNode.getProperties()) {
            if (pNode.isStatic()) continue;
            result.add(pNode.getField());
        }
        return result;
    }

    public static Set<ClassNode> getInterfacesAndSuperInterfaces(ClassNode type) {
        LinkedHashSet<ClassNode> res = new LinkedHashSet<ClassNode>();
        if (type.isInterface()) {
            res.add(type);
            return res;
        }
        for (ClassNode next = type; next != null; next = next.getSuperClass()) {
            res.addAll(next.getAllInterfaces());
        }
        return res;
    }

    public static List<FieldNode> getSuperNonPropertyFields(ClassNode cNode) {
        List<Object> result = cNode == ClassHelper.OBJECT_TYPE ? new ArrayList() : GeneralUtils.getSuperNonPropertyFields(cNode.getSuperClass());
        for (FieldNode fNode : cNode.getFields()) {
            if (fNode.isStatic() || cNode.getProperty(fNode.getName()) != null) continue;
            result.add(fNode);
        }
        return result;
    }

    public static List<FieldNode> getSuperPropertyFields(ClassNode cNode) {
        List<Object> result = cNode == ClassHelper.OBJECT_TYPE ? new ArrayList() : GeneralUtils.getSuperPropertyFields(cNode.getSuperClass());
        for (PropertyNode pNode : cNode.getProperties()) {
            if (pNode.isStatic()) continue;
            result.add(pNode.getField());
        }
        return result;
    }

    public static BinaryExpression hasClassX(Expression instance, ClassNode cNode) {
        return GeneralUtils.eqX(GeneralUtils.classX(cNode), GeneralUtils.callX(instance, "getClass"));
    }

    private static boolean hasClosureMember(AnnotationNode annotation) {
        Map<String, Expression> members = annotation.getMembers();
        for (Map.Entry<String, Expression> member : members.entrySet()) {
            ClassExpression classExpression;
            Class typeClass;
            if (member.getValue() instanceof ClosureExpression) {
                return true;
            }
            if (!(member.getValue() instanceof ClassExpression) || (typeClass = (classExpression = (ClassExpression)member.getValue()).getType().isResolved() ? classExpression.getType().redirect().getTypeClass() : null) == null || !GeneratedClosure.class.isAssignableFrom(typeClass)) continue;
            return true;
        }
        return false;
    }

    public static boolean hasDeclaredMethod(ClassNode cNode, String name, int argsCount) {
        List<MethodNode> ms = cNode.getDeclaredMethods(name);
        for (MethodNode m : ms) {
            Parameter[] paras = m.getParameters();
            if (paras == null || paras.length != argsCount) continue;
            return true;
        }
        return false;
    }

    public static BinaryExpression hasEqualFieldX(FieldNode fNode, Expression other) {
        return GeneralUtils.eqX(GeneralUtils.varX(fNode), GeneralUtils.propX(other, fNode.getName()));
    }

    public static BinaryExpression hasEqualPropertyX(PropertyNode pNode, Expression other) {
        String getterName = GeneralUtils.getGetterName(pNode);
        return GeneralUtils.eqX(GeneralUtils.callThisX(getterName), GeneralUtils.callX(other, getterName));
    }

    public static BooleanExpression hasSameFieldX(FieldNode fNode, Expression other) {
        return GeneralUtils.sameX(GeneralUtils.varX(fNode), GeneralUtils.propX(other, fNode.getName()));
    }

    public static BooleanExpression hasSamePropertyX(PropertyNode pNode, Expression other) {
        ClassNode cNode = pNode.getDeclaringClass();
        return GeneralUtils.sameX(GeneralUtils.getterThisX(cNode, pNode), GeneralUtils.getterX(cNode, other, pNode));
    }

    public static Statement ifElseS(Expression cond, Statement thenStmt, Statement elseStmt) {
        return new IfStatement(cond instanceof BooleanExpression ? (BooleanExpression)cond : new BooleanExpression(cond), thenStmt, elseStmt);
    }

    public static Statement ifS(Expression cond, Expression trueExpr) {
        return GeneralUtils.ifS(cond, new ExpressionStatement(trueExpr));
    }

    public static Statement ifS(Expression cond, Statement trueStmt) {
        return new IfStatement(cond instanceof BooleanExpression ? (BooleanExpression)cond : new BooleanExpression(cond), trueStmt, EmptyStatement.INSTANCE);
    }

    public static Expression indexX(Expression target, Expression value) {
        return new BinaryExpression(target, INDEX, value);
    }

    public static BooleanExpression isInstanceOfX(Expression objectExpression, ClassNode cNode) {
        return new BooleanExpression(new BinaryExpression(objectExpression, INSTANCEOF, GeneralUtils.classX(cNode)));
    }

    public static BooleanExpression isOneX(Expression expr) {
        return new BooleanExpression(new BinaryExpression(expr, EQ, new ConstantExpression(1)));
    }

    public static boolean isOrImplements(ClassNode type, ClassNode interfaceType) {
        return type.equals(interfaceType) || type.implementsInterface(interfaceType);
    }

    public static BooleanExpression isTrueX(Expression argExpr) {
        return new BooleanExpression(new BinaryExpression(argExpr, EQ, new ConstantExpression(Boolean.TRUE)));
    }

    public static BooleanExpression isZeroX(Expression expr) {
        return new BooleanExpression(new BinaryExpression(expr, EQ, new ConstantExpression(0)));
    }

    public static BinaryExpression ltX(Expression lhv, Expression rhv) {
        return new BinaryExpression(lhv, LT, rhv);
    }

    public static String makeDescriptorWithoutReturnType(MethodNode mn) {
        StringBuilder sb = new StringBuilder();
        sb.append(mn.getName()).append(':');
        for (Parameter p : mn.getParameters()) {
            sb.append(p.getType()).append(',');
        }
        return sb.toString();
    }

    public static BinaryExpression neX(Expression lhv, Expression rhv) {
        return new BinaryExpression(lhv, NE, rhv);
    }

    public static BooleanExpression notNullX(Expression argExpr) {
        return new BooleanExpression(new BinaryExpression(argExpr, NE, new ConstantExpression(null)));
    }

    public static NotExpression notX(Expression expr) {
        return new NotExpression(expr instanceof BooleanExpression ? expr : new BooleanExpression(expr));
    }

    public static BinaryExpression orX(Expression lhv, Expression rhv) {
        return new BinaryExpression(lhv, OR, rhv);
    }

    public static Parameter param(ClassNode type, String name) {
        return GeneralUtils.param(type, name, null);
    }

    public static Parameter param(ClassNode type, String name, Expression initialExpression) {
        Parameter param = new Parameter(type, name);
        if (initialExpression != null) {
            param.setInitialExpression(initialExpression);
        }
        return param;
    }

    public static Parameter[] params(Parameter ... params) {
        return params != null ? params : Parameter.EMPTY_ARRAY;
    }

    public static BinaryExpression plusX(Expression lhv, Expression rhv) {
        return new BinaryExpression(lhv, PLUS, rhv);
    }

    public static Expression propX(Expression owner, String property) {
        return new PropertyExpression(owner, property);
    }

    public static Expression propX(Expression owner, Expression property) {
        return new PropertyExpression(owner, property);
    }

    public static Statement returnS(Expression expr) {
        return new ReturnStatement(new ExpressionStatement(expr));
    }

    public static Statement safeExpression(Expression fieldExpr, Expression expression) {
        return new IfStatement(GeneralUtils.equalsNullX(fieldExpr), new ExpressionStatement(fieldExpr), new ExpressionStatement(expression));
    }

    public static BooleanExpression sameX(Expression self, Expression other) {
        return new BooleanExpression(GeneralUtils.callX(self, "is", (Expression)GeneralUtils.args(other)));
    }

    public static Statement stmt(Expression expr) {
        return new ExpressionStatement(expr);
    }

    public static TernaryExpression ternaryX(Expression cond, Expression trueExpr, Expression elseExpr) {
        return new TernaryExpression(cond instanceof BooleanExpression ? (BooleanExpression)cond : new BooleanExpression(cond), trueExpr, elseExpr);
    }

    public static VariableExpression varX(String name) {
        return new VariableExpression(name);
    }

    public static VariableExpression varX(Variable variable) {
        return new VariableExpression(variable);
    }

    public static VariableExpression varX(String name, ClassNode type) {
        return new VariableExpression(name, type);
    }

    public static ThrowStatement throwS(Expression expr) {
        return new ThrowStatement(expr);
    }

    public static CatchStatement catchS(Parameter variable, Statement code) {
        return new CatchStatement(variable, code);
    }

    @Deprecated
    public static Expression getterX(ClassNode annotatedNode, PropertyNode pNode) {
        ClassNode owner = pNode.getDeclaringClass();
        if (annotatedNode.equals(owner)) {
            String getterName = "get" + MetaClassHelper.capitalize(pNode.getName());
            if (ClassHelper.boolean_TYPE.equals(pNode.getOriginType())) {
                getterName = "is" + MetaClassHelper.capitalize(pNode.getName());
            }
            return GeneralUtils.callX((Expression)new VariableExpression("this"), getterName, (Expression)ArgumentListExpression.EMPTY_ARGUMENTS);
        }
        return GeneralUtils.propX((Expression)new VariableExpression("this"), pNode.getName());
    }

    public static Expression getterThisX(ClassNode annotatedNode, PropertyNode pNode) {
        return GeneralUtils.getterX(annotatedNode, new VariableExpression("this"), pNode);
    }

    public static Expression getterX(ClassNode annotatedNode, Expression receiver, PropertyNode pNode) {
        ClassNode owner = pNode.getDeclaringClass();
        if (annotatedNode.equals(owner)) {
            boolean existingExplicitGetter;
            String getterName = "get" + MetaClassHelper.capitalize(pNode.getName());
            boolean bl = existingExplicitGetter = annotatedNode.getGetterMethod(getterName) != null;
            if (ClassHelper.boolean_TYPE.equals(pNode.getOriginType()) && !existingExplicitGetter) {
                getterName = "is" + MetaClassHelper.capitalize(pNode.getName());
            }
            return GeneralUtils.callX(receiver, getterName);
        }
        return GeneralUtils.propX(receiver, pNode.getName());
    }
}

