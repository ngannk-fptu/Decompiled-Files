/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.stc;

import java.util.Collections;
import java.util.List;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.AttributeExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCall;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.transform.stc.StaticTypeCheckingVisitor;
import org.codehaus.groovy.transform.stc.StaticTypesMarker;

public class TypeCheckingExtension {
    protected final StaticTypeCheckingVisitor typeCheckingVisitor;

    public TypeCheckingExtension(StaticTypeCheckingVisitor typeCheckingVisitor) {
        this.typeCheckingVisitor = typeCheckingVisitor;
    }

    public void setup() {
    }

    public void finish() {
    }

    public boolean handleUnresolvedVariableExpression(VariableExpression vexp) {
        return false;
    }

    public boolean handleUnresolvedProperty(PropertyExpression pexp) {
        return false;
    }

    public boolean handleUnresolvedAttribute(AttributeExpression aexp) {
        return false;
    }

    public List<MethodNode> handleMissingMethod(ClassNode receiver, String name, ArgumentListExpression argumentList, ClassNode[] argumentTypes, MethodCall call) {
        return Collections.emptyList();
    }

    public boolean handleIncompatibleAssignment(ClassNode lhsType, ClassNode rhsType, Expression assignmentExpression) {
        return false;
    }

    public List<MethodNode> handleAmbiguousMethods(List<MethodNode> nodes, Expression origin) {
        return nodes;
    }

    public boolean beforeVisitMethod(MethodNode node) {
        return false;
    }

    public void afterVisitMethod(MethodNode node) {
    }

    public boolean beforeVisitClass(ClassNode node) {
        return false;
    }

    public void afterVisitClass(ClassNode node) {
    }

    public boolean beforeMethodCall(MethodCall call) {
        return false;
    }

    public void afterMethodCall(MethodCall call) {
    }

    public void onMethodSelection(Expression expression, MethodNode target) {
    }

    public boolean handleIncompatibleReturnType(ReturnStatement returnStatement, ClassNode inferredReturnType) {
        return false;
    }

    public ClassNode getType(ASTNode exp) {
        return this.typeCheckingVisitor.getType(exp);
    }

    public void addStaticTypeError(String msg, ASTNode expr) {
        this.typeCheckingVisitor.addStaticTypeError(msg, expr);
    }

    public void storeType(Expression exp, ClassNode cn) {
        this.typeCheckingVisitor.storeType(exp, cn);
    }

    public boolean existsProperty(PropertyExpression pexp, boolean checkForReadOnly) {
        return this.typeCheckingVisitor.existsProperty(pexp, checkForReadOnly);
    }

    public boolean existsProperty(PropertyExpression pexp, boolean checkForReadOnly, ClassCodeVisitorSupport visitor) {
        return this.typeCheckingVisitor.existsProperty(pexp, checkForReadOnly, visitor);
    }

    public ClassNode[] getArgumentTypes(ArgumentListExpression args) {
        return this.typeCheckingVisitor.getArgumentTypes(args);
    }

    public MethodNode getTargetMethod(Expression expression) {
        return (MethodNode)expression.getNodeMetaData((Object)StaticTypesMarker.DIRECT_METHOD_CALL_TARGET);
    }

    public ClassNode classNodeFor(Class type) {
        return ClassHelper.make(type);
    }

    public ClassNode classNodeFor(String type) {
        return ClassHelper.make(type);
    }

    public ClassNode lookupClassNodeFor(String type) {
        for (ClassNode cn : this.typeCheckingVisitor.getSourceUnit().getAST().getClasses()) {
            if (!cn.getName().equals(type)) continue;
            return cn;
        }
        return null;
    }

    public ClassNode parameterizedType(ClassNode baseType, ClassNode ... genericsTypeArguments) {
        ClassNode result = baseType.getPlainNodeReference();
        if (result.isUsingGenerics()) {
            GenericsType[] gts = new GenericsType[genericsTypeArguments.length];
            int expectedLength = result.getGenericsTypes().length;
            if (expectedLength != genericsTypeArguments.length) {
                throw new GroovyBugError("Expected number of generic type arguments for " + baseType.toString(false) + " is " + expectedLength + " but you gave " + genericsTypeArguments.length);
            }
            for (int i = 0; i < gts.length; ++i) {
                gts[i] = new GenericsType(genericsTypeArguments[i]);
            }
            result.setGenericsTypes(gts);
        }
        return result;
    }

    public ClassNode buildListType(ClassNode componentType) {
        return this.parameterizedType(ClassHelper.LIST_TYPE, componentType);
    }

    public ClassNode buildMapType(ClassNode keyType, ClassNode valueType) {
        return this.parameterizedType(ClassHelper.MAP_TYPE, keyType, valueType);
    }

    public ClassNode extractStaticReceiver(MethodCall call) {
        if (call instanceof StaticMethodCallExpression) {
            return ((StaticMethodCallExpression)call).getOwnerType();
        }
        if (call instanceof MethodCallExpression) {
            GenericsType[] genericsTypes;
            Expression objectExpr = ((MethodCallExpression)call).getObjectExpression();
            if (objectExpr instanceof ClassExpression && ClassHelper.CLASS_Type.equals(objectExpr.getType()) && (genericsTypes = objectExpr.getType().getGenericsTypes()) != null && genericsTypes.length == 1) {
                return genericsTypes[0].getType();
            }
            if (objectExpr instanceof ClassExpression) {
                return objectExpr.getType();
            }
        }
        return null;
    }

    public boolean isStaticMethodCallOnClass(MethodCall call, ClassNode receiver) {
        ClassNode staticReceiver = this.extractStaticReceiver(call);
        return staticReceiver != null && staticReceiver.equals(receiver);
    }
}

