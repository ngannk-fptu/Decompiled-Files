/*
 * Decompiled with CFR 0.152.
 */
package groovy.text.markup;

import groovy.text.markup.IncludeType;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.ast.ClassCodeExpressionTransformer;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.DynamicVariable;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.VariableScope;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.EmptyExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.SourceUnit;

class MarkupBuilderCodeTransformer
extends ClassCodeExpressionTransformer {
    static final String TARGET_VARIABLE = "target.variable";
    private final SourceUnit unit;
    private final boolean autoEscape;
    private final ClassNode classNode;

    public MarkupBuilderCodeTransformer(SourceUnit unit, ClassNode classNode, boolean autoEscape) {
        this.unit = unit;
        this.autoEscape = autoEscape;
        this.classNode = classNode;
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return this.unit;
    }

    @Override
    public Expression transform(Expression exp) {
        VariableExpression var;
        if (exp instanceof BinaryExpression) {
            return this.transformBinaryExpression((BinaryExpression)exp);
        }
        if (exp instanceof MethodCallExpression) {
            return this.transformMethodCall((MethodCallExpression)exp);
        }
        if (exp instanceof ClosureExpression) {
            ClosureExpression cl = (ClosureExpression)exp;
            cl.getCode().visit(this);
            return cl;
        }
        if (exp instanceof VariableExpression && (var = (VariableExpression)exp).getAccessedVariable() instanceof DynamicVariable) {
            MethodCallExpression callGetModel = new MethodCallExpression((Expression)new VariableExpression("this"), "getModel", (Expression)ArgumentListExpression.EMPTY_ARGUMENTS);
            callGetModel.setImplicitThis(true);
            callGetModel.setSourcePosition(exp);
            String varName = var.getName();
            if ("model".equals(varName) || "unescaped".equals(varName)) {
                return callGetModel;
            }
            MethodCallExpression mce = new MethodCallExpression((Expression)callGetModel, "get", (Expression)new ArgumentListExpression(new ConstantExpression(varName)));
            mce.setSourcePosition(exp);
            mce.setImplicitThis(false);
            MethodCallExpression yield = new MethodCallExpression((Expression)new VariableExpression("this"), "tryEscape", (Expression)new ArgumentListExpression(mce));
            yield.setImplicitThis(true);
            yield.setSourcePosition(exp);
            yield.putNodeMetaData(TARGET_VARIABLE, varName);
            return this.autoEscape ? yield : mce;
        }
        return super.transform(exp);
    }

    private Expression transformBinaryExpression(BinaryExpression bin) {
        String varName;
        VariableExpression var;
        boolean assignment;
        Expression left = bin.getLeftExpression();
        Expression right = bin.getRightExpression();
        boolean bl = assignment = bin.getOperation().getType() == 100;
        if (assignment && left instanceof VariableExpression && (var = (VariableExpression)left).getAccessedVariable() instanceof DynamicVariable && !"modelTypes".equals(varName = var.getName())) {
            MethodCallExpression callGetModel = new MethodCallExpression((Expression)new VariableExpression("this"), "getModel", (Expression)ArgumentListExpression.EMPTY_ARGUMENTS);
            callGetModel.setImplicitThis(true);
            callGetModel.setSourcePosition(left);
            MethodCallExpression mce = new MethodCallExpression((Expression)callGetModel, "put", (Expression)new ArgumentListExpression(new ConstantExpression(varName), right));
            mce.setSourcePosition(left);
            mce.setImplicitThis(false);
            return this.transform(mce);
        }
        if (assignment && left instanceof VariableExpression && right instanceof ClosureExpression && "modelTypes".equals((var = (VariableExpression)left).getName())) {
            Map<String, ClassNode> modelTypes = this.extractModelTypesFromClosureExpression((ClosureExpression)right);
            EmptyExpression result = new EmptyExpression();
            result.setSourcePosition(bin);
            this.classNode.putNodeMetaData("MTE.modelTypes", modelTypes);
            return result;
        }
        return super.transform(bin);
    }

    private Map<String, ClassNode> extractModelTypesFromClosureExpression(ClosureExpression expression) {
        HashMap<String, ClassNode> model = new HashMap<String, ClassNode>();
        this.extractModelTypesFromStatement(expression.getCode(), model);
        return model;
    }

    private void extractModelTypesFromStatement(Statement code, Map<String, ClassNode> model) {
        Expression expression;
        if (code instanceof BlockStatement) {
            BlockStatement block = (BlockStatement)code;
            for (Statement statement : block.getStatements()) {
                this.extractModelTypesFromStatement(statement, model);
            }
        } else if (code instanceof ExpressionStatement && (expression = ((ExpressionStatement)code).getExpression()) instanceof DeclarationExpression) {
            VariableExpression var = ((DeclarationExpression)expression).getVariableExpression();
            model.put(var.getName(), var.getOriginType());
        }
    }

    private Expression transformMethodCall(MethodCallExpression exp) {
        String name = exp.getMethodAsString();
        if (exp.isImplicitThis() && "include".equals(name)) {
            return this.tryTransformInclude(exp);
        }
        if (exp.isImplicitThis() && name.startsWith(":")) {
            List<Expression> args = exp.getArguments() instanceof ArgumentListExpression ? ((ArgumentListExpression)exp.getArguments()).getExpressions() : Collections.singletonList(exp.getArguments());
            Expression newArguments = this.transform(new ArgumentListExpression(new ConstantExpression(name.substring(1)), new ArrayExpression(ClassHelper.OBJECT_TYPE, args)));
            MethodCallExpression call = new MethodCallExpression((Expression)new VariableExpression("this"), "methodMissing", newArguments);
            call.setImplicitThis(true);
            call.setSafe(exp.isSafe());
            call.setSpreadSafe(exp.isSpreadSafe());
            call.setSourcePosition(exp);
            return call;
        }
        if (name != null && name.startsWith("$")) {
            MethodCallExpression reformatted = new MethodCallExpression(exp.getObjectExpression(), name.substring(1), exp.getArguments());
            reformatted.setImplicitThis(exp.isImplicitThis());
            reformatted.setSafe(exp.isSafe());
            reformatted.setSpreadSafe(exp.isSpreadSafe());
            reformatted.setSourcePosition(exp);
            ClosureExpression clos = new ClosureExpression(Parameter.EMPTY_ARRAY, new ExpressionStatement(reformatted));
            clos.setVariableScope(new VariableScope());
            MethodCallExpression stringOf = new MethodCallExpression((Expression)new VariableExpression("this"), "stringOf", (Expression)clos);
            stringOf.setImplicitThis(true);
            stringOf.setSourcePosition(reformatted);
            return stringOf;
        }
        return super.transform(exp);
    }

    private Expression tryTransformInclude(MethodCallExpression exp) {
        MapExpression map;
        List<MapEntryExpression> entries;
        List<Expression> expressions;
        Expression arguments = exp.getArguments();
        if (arguments instanceof TupleExpression && (expressions = ((TupleExpression)arguments).getExpressions()).size() == 1 && expressions.get(0) instanceof MapExpression && (entries = (map = (MapExpression)expressions.get(0)).getMapEntryExpressions()).size() == 1) {
            MapEntryExpression mapEntry = entries.get(0);
            Expression keyExpression = mapEntry.getKeyExpression();
            try {
                IncludeType includeType = IncludeType.valueOf(keyExpression.getText().toLowerCase());
                MethodCallExpression call = new MethodCallExpression(exp.getObjectExpression(), includeType.getMethodName(), (Expression)new ArgumentListExpression(mapEntry.getValueExpression()));
                call.setImplicitThis(true);
                call.setSafe(exp.isSafe());
                call.setSpreadSafe(exp.isSpreadSafe());
                call.setSourcePosition(exp);
                return call;
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        return super.transform(exp);
    }
}

