/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen;

import java.beans.Introspector;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.DynamicVariable;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.Variable;
import org.codehaus.groovy.ast.VariableScope;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.SourceUnit;

public class VariableScopeVisitor
extends ClassCodeVisitorSupport {
    private VariableScope currentScope = null;
    private VariableScope headScope = new VariableScope();
    private ClassNode currentClass = null;
    private SourceUnit source;
    private boolean isSpecialConstructorCall = false;
    private boolean inConstructor = false;
    private final boolean recurseInnerClasses;
    private LinkedList stateStack = new LinkedList();

    public VariableScopeVisitor(SourceUnit source, boolean recurseInnerClasses) {
        this.source = source;
        this.currentScope = this.headScope;
        this.recurseInnerClasses = recurseInnerClasses;
    }

    public VariableScopeVisitor(SourceUnit source) {
        this(source, false);
    }

    private void pushState(boolean isStatic) {
        this.stateStack.add(new StateStackElement());
        this.currentScope = new VariableScope(this.currentScope);
        this.currentScope.setInStaticContext(isStatic);
    }

    private void pushState() {
        this.pushState(this.currentScope.isInStaticContext());
    }

    private void popState() {
        StateStackElement element = (StateStackElement)this.stateStack.removeLast();
        this.currentScope = element.scope;
        this.currentClass = element.clazz;
        this.inConstructor = element.inConstructor;
    }

    private void declare(Parameter[] parameters, ASTNode node) {
        for (Parameter parameter : parameters) {
            if (parameter.hasInitialExpression()) {
                parameter.getInitialExpression().visit(this);
            }
            this.declare(parameter, node);
        }
    }

    private void declare(VariableExpression vex) {
        vex.setInStaticContext(this.currentScope.isInStaticContext());
        this.declare(vex, (ASTNode)vex);
        vex.setAccessedVariable(vex);
    }

    private void declare(Variable var, ASTNode expr) {
        String scopeType = "scope";
        String variableType = "variable";
        if (expr.getClass() == FieldNode.class) {
            scopeType = "class";
            variableType = "field";
        } else if (expr.getClass() == PropertyNode.class) {
            scopeType = "class";
            variableType = "property";
        }
        StringBuilder msg = new StringBuilder();
        msg.append("The current ").append(scopeType);
        msg.append(" already contains a ").append(variableType);
        msg.append(" of the name ").append(var.getName());
        if (this.currentScope.getDeclaredVariable(var.getName()) != null) {
            this.addError(msg.toString(), expr);
            return;
        }
        for (VariableScope scope = this.currentScope.getParent(); scope != null && scope.getClassScope() == null; scope = scope.getParent()) {
            if (scope.getDeclaredVariable(var.getName()) == null) continue;
            this.addError(msg.toString(), expr);
            break;
        }
        this.currentScope.putDeclaredVariable(var);
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return this.source;
    }

    private Variable findClassMember(ClassNode cn, String name) {
        if (cn == null) {
            return null;
        }
        if (cn.isScript()) {
            return new DynamicVariable(name, false);
        }
        for (FieldNode fn : cn.getFields()) {
            if (!fn.getName().equals(name)) continue;
            return fn;
        }
        for (MethodNode mn : cn.getMethods()) {
            String pName = VariableScopeVisitor.getPropertyName(mn);
            if (pName == null || !pName.equals(name)) continue;
            return new PropertyNode(pName, mn.getModifiers(), ClassHelper.OBJECT_TYPE, cn, null, null, null);
        }
        for (PropertyNode pn : cn.getProperties()) {
            if (!pn.getName().equals(name)) continue;
            return pn;
        }
        Variable ret = this.findClassMember(cn.getSuperClass(), name);
        if (ret != null) {
            return ret;
        }
        return this.findClassMember(cn.getOuterClass(), name);
    }

    private static String getPropertyName(MethodNode m) {
        String name = m.getName();
        if (!name.startsWith("set") && !name.startsWith("get")) {
            return null;
        }
        String pname = name.substring(3);
        if (pname.length() == 0) {
            return null;
        }
        pname = Introspector.decapitalize(pname);
        if (name.startsWith("get") && (m.getReturnType() == ClassHelper.VOID_TYPE || m.getParameters().length != 0)) {
            return null;
        }
        if (name.startsWith("set") && m.getParameters().length != 1) {
            return null;
        }
        return pname;
    }

    private Variable checkVariableNameForDeclaration(String name, Expression expression) {
        if ("super".equals(name) || "this".equals(name)) {
            return null;
        }
        VariableScope scope = this.currentScope;
        Variable var = new DynamicVariable(name, this.currentScope.isInStaticContext());
        DynamicVariable orig = var;
        boolean crossingStaticContext = false;
        while (true) {
            crossingStaticContext = crossingStaticContext || scope.isInStaticContext();
            Variable var1 = scope.getDeclaredVariable(var.getName());
            if (var1 != null) {
                var = var1;
                break;
            }
            var1 = scope.getReferencedLocalVariable(var.getName());
            if (var1 != null) {
                var = var1;
                break;
            }
            var1 = scope.getReferencedClassVariable(var.getName());
            if (var1 != null) {
                var = var1;
                break;
            }
            ClassNode classScope = scope.getClassScope();
            if (classScope != null) {
                Variable member = this.findClassMember(classScope, var.getName());
                if (member == null) break;
                boolean staticScope = crossingStaticContext || this.isSpecialConstructorCall;
                boolean staticMember = member.isInStaticContext();
                if (staticScope && !staticMember) break;
                var = member;
                break;
            }
            scope = scope.getParent();
        }
        if (var == orig && crossingStaticContext) {
            var = new DynamicVariable(var.getName(), true);
        }
        VariableScope end = scope;
        for (scope = this.currentScope; scope != end; scope = scope.getParent()) {
            if (end.isClassScope() || end.isReferencedClassVariable(name) && end.getDeclaredVariable(name) == null) {
                scope.putReferencedClassVariable(var);
                continue;
            }
            scope.putReferencedLocalVariable(var);
        }
        return var;
    }

    private void checkPropertyOnExplicitThis(PropertyExpression pe) {
        if (!this.currentScope.isInStaticContext()) {
            return;
        }
        Expression object = pe.getObjectExpression();
        if (!(object instanceof VariableExpression)) {
            return;
        }
        VariableExpression ve = (VariableExpression)object;
        if (!ve.getName().equals("this")) {
            return;
        }
        String name = pe.getPropertyAsString();
        if (name == null || name.equals("class")) {
            return;
        }
        Variable member = this.findClassMember(this.currentClass, name);
        if (member == null) {
            return;
        }
        this.checkVariableContextAccess(member, pe);
    }

    private void checkVariableContextAccess(Variable v, Expression expr) {
        if (v.isInStaticContext() || !this.currentScope.isInStaticContext()) {
            return;
        }
        String msg = v.getName() + " is declared in a dynamic context, but you tried to access it from a static context.";
        this.addError(msg, expr);
        DynamicVariable v2 = new DynamicVariable(v.getName(), this.currentScope.isInStaticContext());
        this.currentScope.putDeclaredVariable(v2);
    }

    @Override
    public void visitBlockStatement(BlockStatement block) {
        this.pushState();
        block.setVariableScope(this.currentScope);
        super.visitBlockStatement(block);
        this.popState();
    }

    @Override
    public void visitForLoop(ForStatement forLoop) {
        this.pushState();
        forLoop.setVariableScope(this.currentScope);
        Parameter p = forLoop.getVariable();
        p.setInStaticContext(this.currentScope.isInStaticContext());
        if (p != ForStatement.FOR_LOOP_DUMMY) {
            this.declare(p, (ASTNode)forLoop);
        }
        super.visitForLoop(forLoop);
        this.popState();
    }

    @Override
    public void visitIfElse(IfStatement ifElse) {
        ifElse.getBooleanExpression().visit(this);
        this.pushState();
        ifElse.getIfBlock().visit(this);
        this.popState();
        this.pushState();
        ifElse.getElseBlock().visit(this);
        this.popState();
    }

    @Override
    public void visitDeclarationExpression(DeclarationExpression expression) {
        this.visitAnnotations(expression);
        expression.getRightExpression().visit(this);
        if (expression.isMultipleAssignmentDeclaration()) {
            TupleExpression list = expression.getTupleExpression();
            for (Expression e : list.getExpressions()) {
                this.declare((VariableExpression)e);
            }
        } else {
            this.declare(expression.getVariableExpression());
        }
    }

    @Override
    public void visitBinaryExpression(BinaryExpression be) {
        super.visitBinaryExpression(be);
        switch (be.getOperation().getType()) {
            case 100: 
            case 210: 
            case 211: 
            case 212: 
            case 213: 
            case 214: 
            case 215: 
            case 216: 
            case 285: 
            case 286: 
            case 287: 
            case 350: 
            case 351: 
            case 352: {
                this.checkFinalFieldAccess(be.getLeftExpression());
                break;
            }
        }
    }

    private void checkFinalFieldAccess(Expression expression) {
        if (!(expression instanceof VariableExpression) && !(expression instanceof TupleExpression)) {
            return;
        }
        if (expression instanceof TupleExpression) {
            TupleExpression list = (TupleExpression)expression;
            for (Expression e : list.getExpressions()) {
                this.checkForFinal(expression, (VariableExpression)e);
            }
        } else {
            this.checkForFinal(expression, (VariableExpression)expression);
        }
    }

    private void checkForFinal(Expression expression, VariableExpression ve) {
        Variable v = ve.getAccessedVariable();
        if (v != null) {
            boolean isFinal = Modifier.isFinal(v.getModifiers());
            boolean isParameter = v instanceof Parameter;
            if (isFinal && isParameter) {
                this.addError("Cannot assign a value to final variable '" + v.getName() + "'", expression);
            }
        }
    }

    @Override
    public void visitVariableExpression(VariableExpression expression) {
        String name = expression.getName();
        Variable v = this.checkVariableNameForDeclaration(name, expression);
        if (v == null) {
            return;
        }
        expression.setAccessedVariable(v);
        this.checkVariableContextAccess(v, expression);
    }

    @Override
    public void visitPropertyExpression(PropertyExpression expression) {
        expression.getObjectExpression().visit(this);
        expression.getProperty().visit(this);
        this.checkPropertyOnExplicitThis(expression);
    }

    @Override
    public void visitClosureExpression(ClosureExpression expression) {
        this.pushState();
        expression.setVariableScope(this.currentScope);
        if (expression.isParameterSpecified()) {
            Parameter[] parameters;
            for (Parameter parameter : parameters = expression.getParameters()) {
                parameter.setInStaticContext(this.currentScope.isInStaticContext());
                if (parameter.hasInitialExpression()) {
                    parameter.getInitialExpression().visit(this);
                }
                this.declare(parameter, (ASTNode)expression);
            }
        } else if (expression.getParameters() != null) {
            Parameter var = new Parameter(ClassHelper.OBJECT_TYPE, "it");
            var.setInStaticContext(this.currentScope.isInStaticContext());
            this.currentScope.putDeclaredVariable(var);
        }
        super.visitClosureExpression(expression);
        this.markClosureSharedVariables();
        this.popState();
    }

    private void markClosureSharedVariables() {
        VariableScope scope = this.currentScope;
        Iterator<Variable> it = scope.getReferencedLocalVariablesIterator();
        while (it.hasNext()) {
            it.next().setClosureSharedVariable(true);
        }
    }

    @Override
    public void visitCatchStatement(CatchStatement statement) {
        this.pushState();
        Parameter p = statement.getVariable();
        p.setInStaticContext(this.currentScope.isInStaticContext());
        this.declare(p, (ASTNode)statement);
        super.visitCatchStatement(statement);
        this.popState();
    }

    @Override
    public void visitFieldExpression(FieldExpression expression) {
        String name = expression.getFieldName();
        Variable v = this.checkVariableNameForDeclaration(name, expression);
        this.checkVariableContextAccess(v, expression);
    }

    @Override
    public void visitClass(ClassNode node) {
        InnerClassNode in;
        if (node instanceof InnerClassNode && (in = (InnerClassNode)node).isAnonymous() && !in.isEnum()) {
            return;
        }
        this.pushState();
        this.prepareVisit(node);
        super.visitClass(node);
        if (this.recurseInnerClasses) {
            Iterator<InnerClassNode> innerClasses = node.getInnerClasses();
            while (innerClasses.hasNext()) {
                this.visitClass(innerClasses.next());
            }
        }
        this.popState();
    }

    public void prepareVisit(ClassNode node) {
        this.currentClass = node;
        this.currentScope.setClassScope(node);
    }

    @Override
    protected void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        Parameter[] parameters;
        this.pushState(node.isStatic());
        this.inConstructor = isConstructor;
        node.setVariableScope(this.currentScope);
        this.visitAnnotations(node);
        for (Parameter parameter : parameters = node.getParameters()) {
            this.visitAnnotations(parameter);
        }
        this.declare(node.getParameters(), (ASTNode)node);
        this.visitClassCodeContainer(node.getCode());
        this.popState();
    }

    @Override
    public void visitMethodCallExpression(MethodCallExpression call) {
        if (call.isImplicitThis() && call.getMethod() instanceof ConstantExpression) {
            ConstantExpression methodNameConstant = (ConstantExpression)call.getMethod();
            String value = methodNameConstant.getText();
            if (!(value instanceof String)) {
                throw new GroovyBugError("tried to make a method call with a non-String constant method name.");
            }
            String methodName = value;
            Variable v = this.checkVariableNameForDeclaration(methodName, call);
            if (v != null && !(v instanceof DynamicVariable)) {
                this.checkVariableContextAccess(v, call);
            }
            if (v instanceof VariableExpression || v instanceof Parameter) {
                VariableExpression object = new VariableExpression(v);
                object.setSourcePosition(methodNameConstant);
                call.setObjectExpression(object);
                ConstantExpression method = new ConstantExpression("call");
                method.setSourcePosition(methodNameConstant);
                call.setImplicitThis(false);
                call.setMethod(method);
            }
        }
        super.visitMethodCallExpression(call);
    }

    @Override
    public void visitConstructorCallExpression(ConstructorCallExpression call) {
        this.isSpecialConstructorCall = call.isSpecialCall();
        super.visitConstructorCallExpression(call);
        this.isSpecialConstructorCall = false;
        if (!call.isUsingAnonymousInnerClass()) {
            return;
        }
        this.pushState();
        InnerClassNode innerClass = (InnerClassNode)call.getType();
        innerClass.setVariableScope(this.currentScope);
        for (MethodNode method : innerClass.getMethods()) {
            Parameter[] parameters = method.getParameters();
            if (parameters.length == 0) {
                parameters = null;
            }
            ClosureExpression cl = new ClosureExpression(parameters, method.getCode());
            this.visitClosureExpression(cl);
        }
        for (FieldNode field : innerClass.getFields()) {
            Expression expression = field.getInitialExpression();
            this.pushState(field.isStatic());
            if (expression != null) {
                VariableExpression vexp;
                if (expression instanceof VariableExpression && (vexp = (VariableExpression)expression).getAccessedVariable() instanceof Parameter) {
                    this.popState();
                    continue;
                }
                expression.visit(this);
            }
            this.popState();
        }
        for (Statement statement : innerClass.getObjectInitializerStatements()) {
            statement.visit(this);
        }
        this.markClosureSharedVariables();
        this.popState();
    }

    @Override
    public void visitProperty(PropertyNode node) {
        this.pushState(node.isStatic());
        super.visitProperty(node);
        this.popState();
    }

    @Override
    public void visitField(FieldNode node) {
        this.pushState(node.isStatic());
        super.visitField(node);
        this.popState();
    }

    @Override
    public void visitAnnotations(AnnotatedNode node) {
        List<AnnotationNode> annotations = node.getAnnotations();
        if (annotations.isEmpty()) {
            return;
        }
        for (AnnotationNode an : annotations) {
            if (an.isBuiltIn()) continue;
            for (Map.Entry<String, Expression> member : an.getMembers().entrySet()) {
                Expression annMemberValue = member.getValue();
                annMemberValue.visit(this);
            }
        }
    }

    private class StateStackElement {
        VariableScope scope;
        ClassNode clazz;
        boolean inConstructor;

        StateStackElement() {
            this.scope = VariableScopeVisitor.this.currentScope;
            this.clazz = VariableScopeVisitor.this.currentClass;
            this.inConstructor = VariableScopeVisitor.this.inConstructor;
        }
    }
}

