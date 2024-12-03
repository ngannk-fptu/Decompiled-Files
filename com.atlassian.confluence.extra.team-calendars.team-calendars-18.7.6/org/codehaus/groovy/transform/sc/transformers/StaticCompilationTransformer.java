/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.sc.transformers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.codehaus.groovy.ast.ClassCodeExpressionTransformer;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.RangeExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.classgen.asm.sc.StaticTypesTypeChooser;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.transform.sc.transformers.BinaryExpressionTransformer;
import org.codehaus.groovy.transform.sc.transformers.BooleanExpressionTransformer;
import org.codehaus.groovy.transform.sc.transformers.CastExpressionOptimizer;
import org.codehaus.groovy.transform.sc.transformers.ClosureExpressionTransformer;
import org.codehaus.groovy.transform.sc.transformers.ConstructorCallTransformer;
import org.codehaus.groovy.transform.sc.transformers.ListExpressionTransformer;
import org.codehaus.groovy.transform.sc.transformers.MethodCallExpressionTransformer;
import org.codehaus.groovy.transform.sc.transformers.RangeExpressionTransformer;
import org.codehaus.groovy.transform.sc.transformers.StaticMethodCallExpressionTransformer;
import org.codehaus.groovy.transform.sc.transformers.VariableExpressionTransformer;
import org.codehaus.groovy.transform.stc.StaticTypeCheckingVisitor;

public class StaticCompilationTransformer
extends ClassCodeExpressionTransformer {
    protected static final ClassNode BYTECODE_ADAPTER_CLASS = ClassHelper.make(ScriptBytecodeAdapter.class);
    protected static final Map<Integer, MethodNode> BYTECODE_BINARY_ADAPTERS = Collections.unmodifiableMap(new HashMap<Integer, MethodNode>(){
        {
            this.put(123, BYTECODE_ADAPTER_CLASS.getMethods("compareEqual").get(0));
            this.put(126, BYTECODE_ADAPTER_CLASS.getMethods("compareGreaterThan").get(0));
            this.put(127, BYTECODE_ADAPTER_CLASS.getMethods("compareGreaterThanEqual").get(0));
            this.put(124, BYTECODE_ADAPTER_CLASS.getMethods("compareLessThan").get(0));
            this.put(125, BYTECODE_ADAPTER_CLASS.getMethods("compareLessThanEqual").get(0));
            this.put(120, BYTECODE_ADAPTER_CLASS.getMethods("compareNotEqual").get(0));
            this.put(128, BYTECODE_ADAPTER_CLASS.getMethods("compareTo").get(0));
        }
    });
    private ClassNode classNode;
    private final SourceUnit unit;
    private final StaticTypesTypeChooser typeChooser = new StaticTypesTypeChooser();
    private final StaticTypeCheckingVisitor staticCompilationVisitor;
    private final StaticMethodCallExpressionTransformer staticMethodCallExpressionTransformer = new StaticMethodCallExpressionTransformer(this);
    private final ConstructorCallTransformer constructorCallTransformer = new ConstructorCallTransformer(this);
    private final MethodCallExpressionTransformer methodCallExpressionTransformer = new MethodCallExpressionTransformer(this);
    private final BinaryExpressionTransformer binaryExpressionTransformer = new BinaryExpressionTransformer(this);
    private final ClosureExpressionTransformer closureExpressionTransformer = new ClosureExpressionTransformer(this);
    private final BooleanExpressionTransformer booleanExpressionTransformer = new BooleanExpressionTransformer(this);
    private final VariableExpressionTransformer variableExpressionTransformer = new VariableExpressionTransformer();
    private final RangeExpressionTransformer rangeExpressionTransformer = new RangeExpressionTransformer(this);
    private final ListExpressionTransformer listExpressionTransformer = new ListExpressionTransformer(this);
    private final CastExpressionOptimizer castExpressionTransformer = new CastExpressionOptimizer(this);

    public StaticCompilationTransformer(SourceUnit unit, StaticTypeCheckingVisitor visitor) {
        this.unit = unit;
        this.staticCompilationVisitor = visitor;
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return this.unit;
    }

    public StaticTypesTypeChooser getTypeChooser() {
        return this.typeChooser;
    }

    public ClassNode getClassNode() {
        return this.classNode;
    }

    @Override
    public void visitClassCodeContainer(Statement code) {
        super.visitClassCodeContainer(code);
    }

    @Override
    public Expression transform(Expression expr) {
        if (expr instanceof StaticMethodCallExpression) {
            return this.staticMethodCallExpressionTransformer.transformStaticMethodCallExpression((StaticMethodCallExpression)expr);
        }
        if (expr instanceof BinaryExpression) {
            return this.binaryExpressionTransformer.transformBinaryExpression((BinaryExpression)expr);
        }
        if (expr instanceof MethodCallExpression) {
            return this.methodCallExpressionTransformer.transformMethodCallExpression((MethodCallExpression)expr);
        }
        if (expr instanceof ClosureExpression) {
            return this.closureExpressionTransformer.transformClosureExpression((ClosureExpression)expr);
        }
        if (expr instanceof ConstructorCallExpression) {
            return this.constructorCallTransformer.transformConstructorCall((ConstructorCallExpression)expr);
        }
        if (expr instanceof BooleanExpression) {
            return this.booleanExpressionTransformer.transformBooleanExpression((BooleanExpression)expr);
        }
        if (expr instanceof VariableExpression) {
            return this.variableExpressionTransformer.transformVariableExpression((VariableExpression)expr);
        }
        if (expr instanceof RangeExpression) {
            return this.rangeExpressionTransformer.transformRangeExpression((RangeExpression)expr);
        }
        if (expr instanceof ListExpression) {
            return this.listExpressionTransformer.transformListExpression((ListExpression)expr);
        }
        if (expr instanceof CastExpression) {
            return this.castExpressionTransformer.transformCastExpression((CastExpression)expr);
        }
        return super.transform(expr);
    }

    final Expression superTransform(Expression expr) {
        return super.transform(expr);
    }

    @Override
    public void visitClass(ClassNode node) {
        ClassNode prec = this.classNode;
        this.classNode = node;
        super.visitClass(node);
        Iterator<InnerClassNode> innerClasses = this.classNode.getInnerClasses();
        while (innerClasses.hasNext()) {
            InnerClassNode innerClassNode = innerClasses.next();
            this.visitClass(innerClassNode);
        }
        this.classNode = prec;
    }

    @Override
    protected void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        if (this.staticCompilationVisitor.isSkipMode(node)) {
            return;
        }
        super.visitConstructorOrMethod(node, isConstructor);
    }
}

