/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen;

import java.util.ArrayList;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.VariableScope;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.GStringExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.SpreadExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.syntax.Token;

public abstract class InnerClassVisitorHelper
extends ClassCodeVisitorSupport {
    protected static void setPropertyGetterDispatcher(BlockStatement block, Expression thiz, Parameter[] parameters) {
        ArrayList<ConstantExpression> gStringStrings = new ArrayList<ConstantExpression>();
        gStringStrings.add(new ConstantExpression(""));
        gStringStrings.add(new ConstantExpression(""));
        ArrayList<Expression> gStringValues = new ArrayList<Expression>();
        gStringValues.add(new VariableExpression(parameters[0]));
        block.addStatement(new ReturnStatement(new PropertyExpression(thiz, new GStringExpression("$name", gStringStrings, gStringValues))));
    }

    protected static void setPropertySetterDispatcher(BlockStatement block, Expression thiz, Parameter[] parameters) {
        ArrayList<ConstantExpression> gStringStrings = new ArrayList<ConstantExpression>();
        gStringStrings.add(new ConstantExpression(""));
        gStringStrings.add(new ConstantExpression(""));
        ArrayList<Expression> gStringValues = new ArrayList<Expression>();
        gStringValues.add(new VariableExpression(parameters[0]));
        block.addStatement(new ExpressionStatement(new BinaryExpression(new PropertyExpression(thiz, new GStringExpression("$name", gStringStrings, gStringValues)), Token.newSymbol(100, -1, -1), new VariableExpression(parameters[1]))));
    }

    protected static void setMethodDispatcherCode(BlockStatement block, Expression thiz, Parameter[] parameters) {
        ArrayList<ConstantExpression> gStringStrings = new ArrayList<ConstantExpression>();
        gStringStrings.add(new ConstantExpression(""));
        gStringStrings.add(new ConstantExpression(""));
        ArrayList<Expression> gStringValues = new ArrayList<Expression>();
        gStringValues.add(new VariableExpression(parameters[0]));
        block.addStatement(new ReturnStatement(new MethodCallExpression(thiz, new GStringExpression("$name", gStringStrings, gStringValues), (Expression)new ArgumentListExpression(new SpreadExpression(new VariableExpression(parameters[1]))))));
    }

    protected static boolean isStatic(InnerClassNode node) {
        VariableScope scope = node.getVariableScope();
        if (scope != null) {
            return scope.isInStaticContext();
        }
        return (node.getModifiers() & 8) != 0;
    }

    protected static ClassNode getClassNode(ClassNode node, boolean isStatic) {
        if (isStatic) {
            node = ClassHelper.CLASS_Type;
        }
        return node;
    }

    protected static int getObjectDistance(ClassNode node) {
        int count = 0;
        while (node != null && node != ClassHelper.OBJECT_TYPE) {
            ++count;
            node = node.getSuperClass();
        }
        return count;
    }

    protected static void addFieldInit(Parameter p, FieldNode fn, BlockStatement block) {
        VariableExpression ve = new VariableExpression(p);
        FieldExpression fe = new FieldExpression(fn);
        block.addStatement(new ExpressionStatement(new BinaryExpression(fe, Token.newSymbol(100, -1, -1), ve)));
    }

    protected static boolean shouldHandleImplicitThisForInnerClass(ClassNode cn) {
        if (cn.isEnum() || cn.isInterface()) {
            return false;
        }
        if ((cn.getModifiers() & 8) != 0) {
            return false;
        }
        if (!(cn instanceof InnerClassNode)) {
            return false;
        }
        InnerClassNode innerClass = (InnerClassNode)cn;
        if (innerClass.getVariableScope() != null) {
            return false;
        }
        return (innerClass.getModifiers() & 8) == 0;
    }
}

