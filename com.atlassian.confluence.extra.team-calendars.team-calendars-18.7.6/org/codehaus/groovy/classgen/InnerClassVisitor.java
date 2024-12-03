/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen;

import groovyjarjarasm.asm.Opcodes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.Variable;
import org.codehaus.groovy.ast.VariableScope;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.classgen.InnerClassVisitorHelper;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.SourceUnit;

public class InnerClassVisitor
extends InnerClassVisitorHelper
implements Opcodes {
    private final SourceUnit sourceUnit;
    private ClassNode classNode;
    private static final int PUBLIC_SYNTHETIC = 4097;
    private FieldNode thisField = null;
    private MethodNode currentMethod;
    private FieldNode currentField;
    private boolean processingObjInitStatements = false;
    private boolean inClosure = false;

    public InnerClassVisitor(CompilationUnit cu, SourceUnit su) {
        this.sourceUnit = su;
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return this.sourceUnit;
    }

    @Override
    public void visitClass(ClassNode node) {
        this.classNode = node;
        this.thisField = null;
        InnerClassNode innerClass = null;
        if (!node.isEnum() && !node.isInterface() && node instanceof InnerClassNode && !InnerClassVisitor.isStatic(innerClass = (InnerClassNode)node) && innerClass.getVariableScope() == null) {
            this.thisField = innerClass.addField("this$0", 4097, node.getOuterClass().getPlainNodeReference(), null);
        }
        super.visitClass(node);
        if (node.isEnum() || node.isInterface()) {
            return;
        }
        if (innerClass == null) {
            return;
        }
        if (node.getSuperClass().isInterface()) {
            node.addInterface(node.getUnresolvedSuperClass());
            node.setUnresolvedSuperClass(ClassHelper.OBJECT_TYPE);
        }
    }

    @Override
    public void visitClosureExpression(ClosureExpression expression) {
        boolean inClosureOld = this.inClosure;
        this.inClosure = true;
        super.visitClosureExpression(expression);
        this.inClosure = inClosureOld;
    }

    @Override
    protected void visitObjectInitializerStatements(ClassNode node) {
        this.processingObjInitStatements = true;
        super.visitObjectInitializerStatements(node);
        this.processingObjInitStatements = false;
    }

    @Override
    protected void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        this.currentMethod = node;
        this.visitAnnotations(node);
        this.visitClassCodeContainer(node.getCode());
        for (Parameter param : node.getParameters()) {
            if (param.hasInitialExpression()) {
                param.getInitialExpression().visit(this);
            }
            this.visitAnnotations(param);
        }
        this.currentMethod = null;
    }

    @Override
    public void visitField(FieldNode node) {
        this.currentField = node;
        super.visitField(node);
        this.currentField = null;
    }

    @Override
    public void visitProperty(PropertyNode node) {
        FieldNode field = node.getField();
        Expression init = field.getInitialExpression();
        field.setInitialValueExpression(null);
        super.visitProperty(node);
        field.setInitialValueExpression(init);
    }

    @Override
    public void visitConstructorCallExpression(ConstructorCallExpression call) {
        super.visitConstructorCallExpression(call);
        if (!call.isUsingAnonymousInnerClass()) {
            this.passThisReference(call);
            return;
        }
        InnerClassNode innerClass = (InnerClassNode)call.getType();
        ClassNode outerClass = innerClass.getOuterClass();
        ClassNode superClass = innerClass.getSuperClass();
        if (superClass instanceof InnerClassNode && !superClass.isInterface() && !superClass.isStaticClass() && (superClass.getModifiers() & 8) != 8) {
            this.insertThis0ToSuperCall(call, innerClass);
        }
        if (!innerClass.getDeclaredConstructors().isEmpty()) {
            return;
        }
        if ((innerClass.getModifiers() & 8) != 0) {
            return;
        }
        VariableScope scope = innerClass.getVariableScope();
        if (scope == null) {
            return;
        }
        List<Expression> expressions = ((TupleExpression)call.getArguments()).getExpressions();
        BlockStatement block = new BlockStatement();
        int additionalParamCount = 1 + scope.getReferencedLocalVariablesCount();
        ArrayList<Parameter> parameters = new ArrayList<Parameter>(expressions.size() + additionalParamCount);
        ArrayList<Expression> superCallArguments = new ArrayList<Expression>(expressions.size());
        int pCount = additionalParamCount;
        for (Expression expr : expressions) {
            Parameter param = new Parameter(ClassHelper.OBJECT_TYPE, "p" + ++pCount);
            parameters.add(param);
            superCallArguments.add(new VariableExpression(param));
        }
        ConstructorCallExpression cce = new ConstructorCallExpression(ClassNode.SUPER, new TupleExpression(superCallArguments));
        block.addStatement(new ExpressionStatement(cce));
        pCount = 0;
        expressions.add(pCount, VariableExpression.THIS_EXPRESSION);
        boolean isStatic = this.isStaticThis(innerClass, scope);
        ClassNode outerClassType = InnerClassVisitor.getClassNode(outerClass, isStatic);
        if (!isStatic && this.inClosure) {
            outerClassType = ClassHelper.CLOSURE_TYPE;
        }
        outerClassType = outerClassType.getPlainNodeReference();
        Parameter thisParameter = new Parameter(outerClassType, "p" + pCount);
        parameters.add(pCount, thisParameter);
        this.thisField = innerClass.addField("this$0", 4097, outerClassType, null);
        InnerClassVisitor.addFieldInit(thisParameter, this.thisField, block);
        Iterator<Variable> it = scope.getReferencedLocalVariablesIterator();
        while (it.hasNext()) {
            Variable var = it.next();
            VariableExpression ve = new VariableExpression(var);
            ve.setClosureSharedVariable(true);
            ve.setUseReferenceDirectly(true);
            expressions.add(++pCount, ve);
            ClassNode rawReferenceType = ClassHelper.REFERENCE_TYPE.getPlainNodeReference();
            Parameter p = new Parameter(rawReferenceType, "p" + pCount);
            parameters.add(pCount, p);
            p.setOriginType(var.getOriginType());
            VariableExpression initial = new VariableExpression(p);
            initial.setSynthetic(true);
            initial.setUseReferenceDirectly(true);
            FieldNode pField = innerClass.addFieldFirst(ve.getName(), 4097, rawReferenceType, initial);
            pField.setHolder(true);
            pField.setOriginType(ClassHelper.getWrapper(var.getOriginType()));
        }
        innerClass.addConstructor(4096, parameters.toArray(new Parameter[0]), ClassNode.EMPTY_ARRAY, block);
    }

    private boolean isStaticThis(InnerClassNode innerClass, VariableScope scope) {
        if (this.inClosure) {
            return false;
        }
        boolean ret = innerClass.isStaticClass();
        if (innerClass.getEnclosingMethod() != null) {
            ret = ret || innerClass.getEnclosingMethod().isStatic();
        } else if (this.currentField != null) {
            ret = ret || this.currentField.isStatic();
        } else if (this.currentMethod != null && "<clinit>".equals(this.currentMethod.getName())) {
            ret = true;
        }
        return ret;
    }

    private void passThisReference(ConstructorCallExpression call) {
        ClassNode cn = call.getType().redirect();
        if (!InnerClassVisitor.shouldHandleImplicitThisForInnerClass(cn)) {
            return;
        }
        boolean isInStaticContext = true;
        if (this.currentMethod != null) {
            isInStaticContext = this.currentMethod.getVariableScope().isInStaticContext();
        } else if (this.currentField != null) {
            isInStaticContext = this.currentField.isStatic();
        } else if (this.processingObjInitStatements) {
            isInStaticContext = false;
        }
        if (isInStaticContext) {
            Expression args = call.getArguments();
            if (args instanceof TupleExpression && ((TupleExpression)args).getExpressions().isEmpty()) {
                this.addError("No enclosing instance passed in constructor call of a non-static inner class", call);
            }
            return;
        }
        this.insertThis0ToSuperCall(call, cn);
    }

    private void insertThis0ToSuperCall(ConstructorCallExpression call, ClassNode cn) {
        ClassNode parent;
        int level = 0;
        for (parent = this.classNode; parent != null && parent != cn.getOuterClass(); parent = parent.getOuterClass()) {
            ++level;
        }
        if (parent == null) {
            return;
        }
        Expression argsExp = call.getArguments();
        if (argsExp instanceof TupleExpression) {
            TupleExpression argsListExp = (TupleExpression)argsExp;
            Expression this0 = VariableExpression.THIS_EXPRESSION;
            for (int i = 0; i != level; ++i) {
                this0 = new PropertyExpression(this0, "this$0");
            }
            argsListExp.getExpressions().add(0, this0);
        }
    }
}

