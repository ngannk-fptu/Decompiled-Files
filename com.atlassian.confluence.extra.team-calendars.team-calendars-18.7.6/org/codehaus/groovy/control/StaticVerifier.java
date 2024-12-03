/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.DynamicVariable;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.Variable;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.control.SourceUnit;

public class StaticVerifier
extends ClassCodeVisitorSupport {
    private boolean inSpecialConstructorCall;
    private boolean inPropertyExpression;
    private boolean inClosure;
    private MethodNode currentMethod;
    private SourceUnit source;

    public void visitClass(ClassNode node, SourceUnit source) {
        this.source = source;
        super.visitClass(node);
    }

    @Override
    public void visitVariableExpression(VariableExpression ve) {
        Variable v = ve.getAccessedVariable();
        if (v != null && v instanceof DynamicVariable && (!this.inPropertyExpression || this.inSpecialConstructorCall)) {
            this.addStaticVariableError(ve);
        }
    }

    @Override
    public void visitClosureExpression(ClosureExpression ce) {
        boolean oldInClosure = this.inClosure;
        this.inClosure = true;
        super.visitClosureExpression(ce);
        this.inClosure = oldInClosure;
    }

    @Override
    public void visitConstructorCallExpression(ConstructorCallExpression cce) {
        boolean oldIsSpecialConstructorCall = this.inSpecialConstructorCall;
        this.inSpecialConstructorCall = cce.isSpecialCall();
        super.visitConstructorCallExpression(cce);
        this.inSpecialConstructorCall = oldIsSpecialConstructorCall;
    }

    @Override
    public void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        MethodNode oldCurrentMethod = this.currentMethod;
        this.currentMethod = node;
        super.visitConstructorOrMethod(node, isConstructor);
        if (isConstructor) {
            final HashSet<String> exceptions = new HashSet<String>();
            for (final Parameter param : node.getParameters()) {
                exceptions.add(param.getName());
                if (!param.hasInitialExpression()) continue;
                param.getInitialExpression().visit(new CodeVisitorSupport(){

                    @Override
                    public void visitVariableExpression(VariableExpression ve) {
                        if (exceptions.contains(ve.getName())) {
                            return;
                        }
                        Variable av = ve.getAccessedVariable();
                        if (av instanceof DynamicVariable || !av.isInStaticContext()) {
                            StaticVerifier.this.addVariableError(ve);
                        }
                    }

                    @Override
                    public void visitMethodCallExpression(MethodCallExpression call) {
                        VariableExpression ve;
                        Expression objectExpression = call.getObjectExpression();
                        if (objectExpression instanceof VariableExpression && (ve = (VariableExpression)objectExpression).isThisExpression()) {
                            StaticVerifier.this.addError("Can't access instance method '" + call.getMethodAsString() + "' for a constructor parameter default value", param);
                            return;
                        }
                        super.visitMethodCallExpression(call);
                    }

                    @Override
                    public void visitClosureExpression(ClosureExpression expression) {
                    }
                });
            }
        }
        this.currentMethod = oldCurrentMethod;
    }

    @Override
    public void visitMethodCallExpression(MethodCallExpression mce) {
        super.visitMethodCallExpression(mce);
    }

    @Override
    public void visitPropertyExpression(PropertyExpression pe) {
        if (!this.inSpecialConstructorCall) {
            this.checkStaticScope(pe);
        }
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return this.source;
    }

    private void checkStaticScope(PropertyExpression pe) {
        if (this.inClosure) {
            return;
        }
        for (Expression it = pe; it != null; it = it.getObjectExpression()) {
            if (it instanceof PropertyExpression) continue;
            if (it instanceof VariableExpression) {
                this.addStaticVariableError((VariableExpression)it);
            }
            return;
        }
    }

    private void addStaticVariableError(VariableExpression ve) {
        FieldNode fieldNode;
        if (!(this.inSpecialConstructorCall || !this.inClosure && ve.isInStaticContext())) {
            return;
        }
        if (ve.isThisExpression() || ve.isSuperExpression()) {
            return;
        }
        Variable v = ve.getAccessedVariable();
        if (this.currentMethod != null && this.currentMethod.isStatic() && (fieldNode = StaticVerifier.getDeclaredOrInheritedField(this.currentMethod.getDeclaringClass(), ve.getName())) != null && fieldNode.isStatic()) {
            return;
        }
        if (v != null && !(v instanceof DynamicVariable) && v.isInStaticContext()) {
            return;
        }
        this.addVariableError(ve);
    }

    private void addVariableError(VariableExpression ve) {
        this.addError("Apparent variable '" + ve.getName() + "' was found in a static scope but doesn't refer to a local variable, static field or class. Possible causes:\nYou attempted to reference a variable in the binding or an instance variable from a static context.\nYou misspelled a classname or statically imported field. Please check the spelling.\nYou attempted to use a method '" + ve.getName() + "' but left out brackets in a place not allowed by the grammar.", ve);
    }

    private static FieldNode getDeclaredOrInheritedField(ClassNode cn, String fieldName) {
        for (ClassNode node = cn; node != null; node = node.getSuperClass()) {
            FieldNode fn = node.getDeclaredField(fieldName);
            if (fn != null) {
                return fn;
            }
            ArrayList<ClassNode> interfacesToCheck = new ArrayList<ClassNode>(Arrays.asList(node.getInterfaces()));
            while (!interfacesToCheck.isEmpty()) {
                ClassNode nextInterface = (ClassNode)interfacesToCheck.remove(0);
                fn = nextInterface.getDeclaredField(fieldName);
                if (fn != null) {
                    return fn;
                }
                interfacesToCheck.addAll(Arrays.asList(nextInterface.getInterfaces()));
            }
        }
        return null;
    }
}

