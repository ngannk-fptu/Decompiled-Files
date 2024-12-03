/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.tailrec;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.IntRange;
import groovy.lang.MetaClass;
import java.lang.reflect.Method;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.AssertStatement;
import org.codehaus.groovy.ast.stmt.CaseStatement;
import org.codehaus.groovy.ast.stmt.DoWhileStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.codehaus.groovy.ast.stmt.SynchronizedStatement;
import org.codehaus.groovy.ast.stmt.ThrowStatement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.StringGroovyMethods;
import org.codehaus.groovy.transform.tailrec.VariableExpressionTransformer;

public class VariableExpressionReplacer
extends CodeVisitorSupport
implements GroovyObject {
    private Closure<Boolean> when;
    private Closure<VariableExpression> replaceWith;
    private ExpressionTransformer transformer;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;

    public VariableExpressionReplacer() {
        MetaClass metaClass;
        _closure2 _closure210;
        _closure1 _closure110;
        this.when = _closure110 = new _closure1(this, this);
        this.replaceWith = _closure210 = new _closure2(this, this);
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public synchronized void replaceIn(ASTNode root) {
        VariableExpressionTransformer variableExpressionTransformer = new VariableExpressionTransformer();
        Closure<Boolean> closure = this.when;
        variableExpressionTransformer.setWhen(closure);
        Closure<VariableExpression> closure2 = this.replaceWith;
        variableExpressionTransformer.setReplaceWith(closure2);
        VariableExpressionTransformer variableExpressionTransformer2 = variableExpressionTransformer;
        this.transformer = variableExpressionTransformer2;
        root.visit(this);
    }

    @Override
    public void visitReturnStatement(ReturnStatement statement) {
        this.replaceExpressionPropertyWhenNecessary(statement);
        super.visitReturnStatement(statement);
    }

    @Override
    public void visitIfElse(IfStatement ifElse) {
        this.replaceExpressionPropertyWhenNecessary(ifElse, "booleanExpression", BooleanExpression.class);
        super.visitIfElse(ifElse);
    }

    @Override
    public void visitForLoop(ForStatement forLoop) {
        this.replaceExpressionPropertyWhenNecessary(forLoop, "collectionExpression");
        super.visitForLoop(forLoop);
    }

    @Override
    public void visitBinaryExpression(BinaryExpression expression) {
        this.replaceExpressionPropertyWhenNecessary(expression, "rightExpression");
        expression.getRightExpression().visit(this);
        super.visitBinaryExpression(expression);
    }

    @Override
    public void visitWhileLoop(WhileStatement loop) {
        this.replaceExpressionPropertyWhenNecessary(loop, "booleanExpression", BooleanExpression.class);
        super.visitWhileLoop(loop);
    }

    @Override
    public void visitDoWhileLoop(DoWhileStatement loop) {
        this.replaceExpressionPropertyWhenNecessary(loop, "booleanExpression", BooleanExpression.class);
        super.visitDoWhileLoop(loop);
    }

    @Override
    public void visitSwitch(SwitchStatement statement) {
        this.replaceExpressionPropertyWhenNecessary(statement);
        super.visitSwitch(statement);
    }

    @Override
    public void visitCaseStatement(CaseStatement statement) {
        this.replaceExpressionPropertyWhenNecessary(statement);
        super.visitCaseStatement(statement);
    }

    @Override
    public void visitExpressionStatement(ExpressionStatement statement) {
        this.replaceExpressionPropertyWhenNecessary(statement);
        super.visitExpressionStatement(statement);
    }

    @Override
    public void visitThrowStatement(ThrowStatement statement) {
        this.replaceExpressionPropertyWhenNecessary(statement);
        super.visitThrowStatement(statement);
    }

    @Override
    public void visitAssertStatement(AssertStatement statement) {
        this.replaceExpressionPropertyWhenNecessary(statement, "booleanExpression", BooleanExpression.class);
        this.replaceExpressionPropertyWhenNecessary(statement, "messageExpression");
        super.visitAssertStatement(statement);
    }

    @Override
    public void visitSynchronizedStatement(SynchronizedStatement statement) {
        this.replaceExpressionPropertyWhenNecessary(statement);
        super.visitSynchronizedStatement(statement);
    }

    private void replaceExpressionPropertyWhenNecessary(ASTNode node, String propName, Class propClass) {
        Expression expr = this.getExpression(node, propName);
        if (expr instanceof VariableExpression) {
            Boolean bl = this.when.call(new Object[]{expr});
            if (bl == null ? false : bl) {
                VariableExpression newExpr = (VariableExpression)ScriptBytecodeAdapter.castToType(this.replaceWith.call(new Object[]{expr}), VariableExpression.class);
                this.replaceExpression(node, propName, propClass, expr, newExpr);
            }
        } else {
            Expression newExpr = expr.transformExpression(this.transformer);
            this.replaceExpression(node, propName, propClass, expr, newExpr);
        }
    }

    private void replaceExpression(ASTNode node, String propName, Class propClass, Expression oldExpr, Expression newExpr) {
        String setterName = StringGroovyMethods.plus("set", (CharSequence)this.capitalizeFirst(propName));
        Method setExpressionMethod = node.getClass().getMethod(setterName, (Class[])ScriptBytecodeAdapter.castToType(ScriptBytecodeAdapter.createList(new Object[]{propClass}).toArray((Object[])ScriptBytecodeAdapter.castToType(new Class[1], Object[].class)), Class[].class));
        newExpr.setSourcePosition(oldExpr);
        newExpr.copyNodeMetaData(oldExpr);
        setExpressionMethod.invoke((Object)node, ScriptBytecodeAdapter.createList(new Object[]{newExpr}).toArray());
    }

    private Expression getExpression(ASTNode node, String propName) {
        String getterName = StringGroovyMethods.plus("get", (CharSequence)this.capitalizeFirst(propName));
        Method getExpressionMethod = node.getClass().getMethod(getterName, new Class[0]);
        return (Expression)ScriptBytecodeAdapter.asType(getExpressionMethod.invoke((Object)node, new Object[0]), Expression.class);
    }

    private String capitalizeFirst(String propName) {
        return StringGroovyMethods.plus(StringGroovyMethods.getAt(propName, 0).toUpperCase(), (CharSequence)StringGroovyMethods.getAt(propName, new IntRange(true, 1, -1)));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != VariableExpressionReplacer.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    private void replaceExpressionPropertyWhenNecessary(ASTNode node, String propName) {
        this.replaceExpressionPropertyWhenNecessary(node, propName, Expression.class);
    }

    private void replaceExpressionPropertyWhenNecessary(ASTNode node) {
        this.replaceExpressionPropertyWhenNecessary(node, "expression", Expression.class);
    }

    @Override
    public /* synthetic */ MetaClass getMetaClass() {
        MetaClass metaClass = this.metaClass;
        if (metaClass != null) {
            return metaClass;
        }
        this.metaClass = this.$getStaticMetaClass();
        return this.metaClass;
    }

    @Override
    public /* synthetic */ void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    @Override
    public /* synthetic */ Object invokeMethod(String string, Object object) {
        return this.getMetaClass().invokeMethod((Object)this, string, object);
    }

    @Override
    public /* synthetic */ Object getProperty(String string) {
        return this.getMetaClass().getProperty(this, string);
    }

    @Override
    public /* synthetic */ void setProperty(String string, Object object) {
        this.getMetaClass().setProperty(this, string, object);
    }

    public Closure<Boolean> getWhen() {
        return this.when;
    }

    public void setWhen(Closure<Boolean> closure) {
        this.when = closure;
    }

    public Closure<VariableExpression> getReplaceWith() {
        return this.replaceWith;
    }

    public void setReplaceWith(Closure<VariableExpression> closure) {
        this.replaceWith = closure;
    }

    public class _closure1
    extends Closure
    implements GeneratedClosure {
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;

        public _closure1(Object _outerInstance, Object _thisObject) {
            super(_outerInstance, _thisObject);
        }

        public Object doCall(VariableExpression node) {
            return false;
        }

        public Object call(VariableExpression node) {
            return this.doCall(node);
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != _closure1.class) {
                return ScriptBytecodeAdapter.initMetaClass(this);
            }
            ClassInfo classInfo = $staticClassInfo;
            if (classInfo == null) {
                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
            }
            return classInfo.getMetaClass();
        }
    }

    public class _closure2
    extends Closure
    implements GeneratedClosure {
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;

        public _closure2(Object _outerInstance, Object _thisObject) {
            super(_outerInstance, _thisObject);
        }

        public Object doCall(VariableExpression variableExpression) {
            return variableExpression;
        }

        public Object call(VariableExpression variableExpression) {
            return this.doCall(variableExpression);
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != _closure2.class) {
                return ScriptBytecodeAdapter.initMetaClass(this);
            }
            ClassInfo classInfo = $staticClassInfo;
            if (classInfo == null) {
                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
            }
            return classInfo.getMetaClass();
        }
    }
}

