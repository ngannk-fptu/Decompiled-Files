/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.stc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.AttributeExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCall;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.transform.stc.StaticTypeCheckingVisitor;
import org.codehaus.groovy.transform.stc.TypeCheckingExtension;

public class DefaultTypeCheckingExtension
extends TypeCheckingExtension {
    protected final List<TypeCheckingExtension> handlers = new LinkedList<TypeCheckingExtension>();

    public DefaultTypeCheckingExtension(StaticTypeCheckingVisitor typeCheckingVisitor) {
        super(typeCheckingVisitor);
    }

    public void addHandler(TypeCheckingExtension handler) {
        this.handlers.add(handler);
    }

    public void removeHandler(TypeCheckingExtension handler) {
        this.handlers.remove(handler);
    }

    @Override
    public boolean handleUnresolvedVariableExpression(VariableExpression vexp) {
        for (TypeCheckingExtension handler : this.handlers) {
            if (!handler.handleUnresolvedVariableExpression(vexp)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean handleUnresolvedProperty(PropertyExpression pexp) {
        for (TypeCheckingExtension handler : this.handlers) {
            if (!handler.handleUnresolvedProperty(pexp)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean handleUnresolvedAttribute(AttributeExpression aexp) {
        for (TypeCheckingExtension handler : this.handlers) {
            if (!handler.handleUnresolvedAttribute(aexp)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean handleIncompatibleAssignment(ClassNode lhsType, ClassNode rhsType, Expression assignmentExpression) {
        for (TypeCheckingExtension handler : this.handlers) {
            if (!handler.handleIncompatibleAssignment(lhsType, rhsType, assignmentExpression)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean handleIncompatibleReturnType(ReturnStatement returnStatement, ClassNode inferredReturnType) {
        for (TypeCheckingExtension handler : this.handlers) {
            if (!handler.handleIncompatibleReturnType(returnStatement, inferredReturnType)) continue;
            return true;
        }
        return false;
    }

    @Override
    public List<MethodNode> handleAmbiguousMethods(List<MethodNode> nodes, Expression origin) {
        List<MethodNode> result = nodes;
        Iterator<TypeCheckingExtension> it = this.handlers.iterator();
        while (result.size() > 1 && it.hasNext()) {
            result = it.next().handleAmbiguousMethods(result, origin);
        }
        return result;
    }

    @Override
    public List<MethodNode> handleMissingMethod(ClassNode receiver, String name, ArgumentListExpression argumentList, ClassNode[] argumentTypes, MethodCall call) {
        LinkedList<MethodNode> result = new LinkedList<MethodNode>();
        for (TypeCheckingExtension handler : this.handlers) {
            List<MethodNode> handlerResult = handler.handleMissingMethod(receiver, name, argumentList, argumentTypes, call);
            for (MethodNode mn : handlerResult) {
                if (mn.getDeclaringClass() != null) continue;
                mn.setDeclaringClass(ClassHelper.OBJECT_TYPE);
            }
            result.addAll(handlerResult);
        }
        return result;
    }

    @Override
    public void afterVisitMethod(MethodNode node) {
        for (TypeCheckingExtension handler : this.handlers) {
            handler.afterVisitMethod(node);
        }
    }

    @Override
    public boolean beforeVisitMethod(MethodNode node) {
        for (TypeCheckingExtension handler : this.handlers) {
            if (!handler.beforeVisitMethod(node)) continue;
            return true;
        }
        return false;
    }

    @Override
    public void afterVisitClass(ClassNode node) {
        for (TypeCheckingExtension handler : this.handlers) {
            handler.afterVisitClass(node);
        }
    }

    @Override
    public boolean beforeVisitClass(ClassNode node) {
        for (TypeCheckingExtension handler : this.handlers) {
            if (!handler.beforeVisitClass(node)) continue;
            return true;
        }
        return false;
    }

    @Override
    public void afterMethodCall(MethodCall call) {
        for (TypeCheckingExtension handler : this.handlers) {
            handler.afterMethodCall(call);
        }
    }

    @Override
    public boolean beforeMethodCall(MethodCall call) {
        for (TypeCheckingExtension handler : this.handlers) {
            if (!handler.beforeMethodCall(call)) continue;
            return true;
        }
        return false;
    }

    @Override
    public void onMethodSelection(Expression expression, MethodNode target) {
        for (TypeCheckingExtension handler : this.handlers) {
            handler.onMethodSelection(expression, target);
        }
    }

    @Override
    public void setup() {
        ArrayList<TypeCheckingExtension> copy = new ArrayList<TypeCheckingExtension>(this.handlers);
        for (TypeCheckingExtension handler : copy) {
            handler.setup();
        }
    }

    @Override
    public void finish() {
        for (TypeCheckingExtension handler : this.handlers) {
            handler.finish();
        }
    }
}

