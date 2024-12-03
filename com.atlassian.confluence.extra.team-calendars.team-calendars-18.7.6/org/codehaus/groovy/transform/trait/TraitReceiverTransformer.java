/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.trait;

import java.util.Collection;
import java.util.List;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassCodeExpressionTransformer;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.DynamicVariable;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.Variable;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.TernaryExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.transform.trait.TraitASTTransformation;
import org.codehaus.groovy.transform.trait.Traits;

class TraitReceiverTransformer
extends ClassCodeExpressionTransformer {
    private final VariableExpression weaved;
    private final SourceUnit unit;
    private final ClassNode traitClass;
    private final ClassNode traitHelperClass;
    private final ClassNode fieldHelper;
    private final Collection<String> knownFields;
    private boolean inClosure;

    public TraitReceiverTransformer(VariableExpression thisObject, SourceUnit unit, ClassNode traitClass, ClassNode traitHelperClass, ClassNode fieldHelper, Collection<String> knownFields) {
        this.weaved = thisObject;
        this.unit = unit;
        this.traitClass = traitClass;
        this.traitHelperClass = traitHelperClass;
        this.fieldHelper = fieldHelper;
        this.knownFields = knownFields;
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return this.unit;
    }

    @Override
    public Expression transform(Expression exp) {
        ClassNode weavedType = this.weaved.getOriginType();
        if (exp instanceof BinaryExpression) {
            return this.transformBinaryExpression((BinaryExpression)exp, weavedType);
        }
        if (exp instanceof StaticMethodCallExpression) {
            StaticMethodCallExpression call = (StaticMethodCallExpression)exp;
            ClassNode ownerType = call.getOwnerType();
            if (this.traitClass.equals(ownerType)) {
                MethodCallExpression result = new MethodCallExpression((Expression)new VariableExpression(this.weaved), call.getMethod(), this.transform(call.getArguments()));
                result.setSafe(false);
                result.setImplicitThis(false);
                result.setSpreadSafe(false);
                result.setSourcePosition(call);
                return result;
            }
        } else if (exp instanceof MethodCallExpression) {
            MethodCallExpression call = (MethodCallExpression)exp;
            Expression obj = call.getObjectExpression();
            if (call.isImplicitThis() || "this".equals(obj.getText())) {
                return this.transformMethodCallOnThis(call);
            }
            if ("super".equals(obj.getText())) {
                return this.transformSuperMethodCall(call);
            }
        } else {
            if (exp instanceof FieldExpression) {
                return this.transformFieldExpression((FieldExpression)exp);
            }
            if (exp instanceof VariableExpression) {
                VariableExpression vexp = (VariableExpression)exp;
                Variable accessedVariable = vexp.getAccessedVariable();
                if (accessedVariable instanceof FieldNode) {
                    FieldNode fn = (FieldNode)accessedVariable;
                    Expression receiver = this.createFieldHelperReceiver();
                    boolean isStatic = fn.isStatic();
                    if (isStatic) {
                        receiver = this.createStaticReceiver(receiver);
                    }
                    MethodCallExpression mce = new MethodCallExpression(receiver, Traits.helperGetterName(fn), (Expression)ArgumentListExpression.EMPTY_ARGUMENTS);
                    mce.setSourcePosition(exp);
                    mce.setImplicitThis(false);
                    TraitReceiverTransformer.markDynamicCall(mce, fn, isStatic);
                    return mce;
                }
                if (accessedVariable instanceof PropertyNode) {
                    String propName = accessedVariable.getName();
                    if (this.knownFields.contains(propName)) {
                        return this.createFieldHelperCall(exp, weavedType, propName);
                    }
                    PropertyExpression propertyExpression = new PropertyExpression((Expression)new VariableExpression(this.weaved), accessedVariable.getName());
                    propertyExpression.getProperty().setSourcePosition(exp);
                    return propertyExpression;
                }
                if (accessedVariable instanceof DynamicVariable) {
                    PropertyExpression propertyExpression = new PropertyExpression((Expression)new VariableExpression(this.weaved), accessedVariable.getName());
                    propertyExpression.getProperty().setSourcePosition(exp);
                    return propertyExpression;
                }
                if (vexp.isThisExpression()) {
                    VariableExpression res = new VariableExpression(this.weaved);
                    res.setSourcePosition(exp);
                    return res;
                }
                if (vexp.isSuperExpression()) {
                    this.throwSuperError(vexp);
                }
            } else if (exp instanceof PropertyExpression) {
                String propName;
                PropertyExpression pexp = (PropertyExpression)exp;
                Expression object = pexp.getObjectExpression();
                if ((pexp.isImplicitThis() || "this".equals(object.getText())) && this.knownFields.contains(propName = pexp.getPropertyAsString())) {
                    return this.createFieldHelperCall(exp, weavedType, propName);
                }
            } else if (exp instanceof ClosureExpression) {
                MethodCallExpression mce = new MethodCallExpression(exp, "rehydrate", (Expression)new ArgumentListExpression(new VariableExpression(this.weaved), new VariableExpression(this.weaved), new VariableExpression(this.weaved)));
                mce.setImplicitThis(false);
                mce.setSourcePosition(exp);
                boolean oldInClosure = this.inClosure;
                this.inClosure = true;
                ((ClosureExpression)exp).getCode().visit(this);
                this.inClosure = oldInClosure;
                exp.putNodeMetaData(TraitASTTransformation.POST_TYPECHECKING_REPLACEMENT, mce);
                return exp;
            }
        }
        return super.transform(exp);
    }

    private Expression createFieldHelperCall(Expression exp, ClassNode weavedType, String propName) {
        String method = Traits.helperGetterName(new FieldNode(propName, 0, ClassHelper.OBJECT_TYPE, weavedType, null));
        MethodCallExpression mce = new MethodCallExpression(this.createFieldHelperReceiver(), method, (Expression)ArgumentListExpression.EMPTY_ARGUMENTS);
        mce.setSourcePosition(exp);
        mce.setImplicitThis(false);
        return mce;
    }

    private Expression transformFieldExpression(FieldExpression exp) {
        FieldNode field = exp.getField();
        MethodCallExpression mce = new MethodCallExpression(this.createFieldHelperReceiver(), Traits.helperGetterName(field), (Expression)ArgumentListExpression.EMPTY_ARGUMENTS);
        mce.setSourcePosition(exp);
        mce.setImplicitThis(false);
        TraitReceiverTransformer.markDynamicCall(mce, field, field.isStatic());
        return mce;
    }

    private Expression transformBinaryExpression(BinaryExpression exp, ClassNode weavedType) {
        Expression leftExpression = exp.getLeftExpression();
        Expression rightExpression = exp.getRightExpression();
        Token operation = exp.getOperation();
        if (operation.getText().equals("=")) {
            FieldNode fn;
            String leftFieldName = null;
            if (leftExpression instanceof VariableExpression && ((VariableExpression)leftExpression).getAccessedVariable() instanceof FieldNode) {
                leftFieldName = ((VariableExpression)leftExpression).getAccessedVariable().getName();
            } else if (leftExpression instanceof FieldExpression) {
                leftFieldName = ((FieldExpression)leftExpression).getFieldName();
            } else if (leftExpression instanceof PropertyExpression && (((PropertyExpression)leftExpression).isImplicitThis() || "this".equals(((PropertyExpression)leftExpression).getObjectExpression().getText()))) {
                leftFieldName = ((PropertyExpression)leftExpression).getPropertyAsString();
                fn = TraitReceiverTransformer.tryGetFieldNode(weavedType, leftFieldName);
                if (this.fieldHelper == null || fn == null && !this.fieldHelper.hasPossibleMethod(Traits.helperSetterName(new FieldNode(leftFieldName, 0, ClassHelper.OBJECT_TYPE, weavedType, null)), rightExpression)) {
                    return this.createAssignmentToField(rightExpression, operation, leftFieldName);
                }
            }
            if (leftFieldName != null) {
                boolean isStatic;
                fn = weavedType.getDeclaredField(leftFieldName);
                FieldNode staticField = TraitReceiverTransformer.tryGetFieldNode(weavedType, leftFieldName);
                if (fn == null) {
                    fn = new FieldNode(leftFieldName, 0, ClassHelper.OBJECT_TYPE, weavedType, null);
                }
                Expression receiver = this.createFieldHelperReceiver();
                boolean bl = isStatic = staticField != null && staticField.isStatic();
                if (fn.isStatic()) {
                    receiver = new PropertyExpression(receiver, "class");
                }
                String method = Traits.helperSetterName(fn);
                MethodCallExpression mce = new MethodCallExpression(receiver, method, (Expression)new ArgumentListExpression(super.transform(rightExpression)));
                mce.setSourcePosition(exp);
                mce.setImplicitThis(false);
                TraitReceiverTransformer.markDynamicCall(mce, staticField, isStatic);
                return mce;
            }
        }
        Expression leftTransform = this.transform(leftExpression);
        Expression rightTransform = this.transform(rightExpression);
        BinaryExpression ret = exp instanceof DeclarationExpression ? new DeclarationExpression(leftTransform, operation, rightTransform) : new BinaryExpression(leftTransform, operation, rightTransform);
        ret.setSourcePosition(exp);
        ret.copyNodeMetaData(exp);
        return ret;
    }

    private static void markDynamicCall(MethodCallExpression mce, FieldNode fn, boolean isStatic) {
        if (isStatic) {
            mce.putNodeMetaData(TraitASTTransformation.DO_DYNAMIC, fn.getOriginType());
        }
    }

    private TernaryExpression createStaticReceiver(Expression receiver) {
        return new TernaryExpression(new BooleanExpression(new BinaryExpression(receiver, Token.newSymbol(544, -1, -1), new ClassExpression(ClassHelper.CLASS_Type))), receiver, new MethodCallExpression(this.createFieldHelperReceiver(), "getClass", (Expression)ArgumentListExpression.EMPTY_ARGUMENTS));
    }

    private BinaryExpression createAssignmentToField(Expression rightExpression, Token operation, String fieldName) {
        return new BinaryExpression(new PropertyExpression((Expression)new VariableExpression(this.weaved), fieldName), operation, this.transform(rightExpression));
    }

    private static FieldNode tryGetFieldNode(ClassNode weavedType, String fieldName) {
        GenericsType[] genericsTypes;
        FieldNode fn = weavedType.getDeclaredField(fieldName);
        if (fn == null && ClassHelper.CLASS_Type.equals(weavedType) && (genericsTypes = weavedType.getGenericsTypes()) != null && genericsTypes.length == 1) {
            fn = genericsTypes[0].getType().getDeclaredField(fieldName);
        }
        return fn;
    }

    private void throwSuperError(ASTNode node) {
        this.unit.addError(new SyntaxException("Call to super is not allowed in a trait", node.getLineNumber(), node.getColumnNumber()));
    }

    private Expression transformSuperMethodCall(MethodCallExpression call) {
        String method = call.getMethodAsString();
        if (method == null) {
            this.throwSuperError(call);
        }
        Expression arguments = this.transform(call.getArguments());
        ArgumentListExpression superCallArgs = new ArgumentListExpression();
        if (arguments instanceof ArgumentListExpression) {
            ArgumentListExpression list = (ArgumentListExpression)arguments;
            for (Expression expression : list) {
                superCallArgs.addExpression(expression);
            }
        } else {
            superCallArgs.addExpression(arguments);
        }
        MethodCallExpression transformed = new MethodCallExpression((Expression)this.weaved, Traits.getSuperTraitMethodName(this.traitClass, method), (Expression)superCallArgs);
        transformed.setSourcePosition(call);
        transformed.setSafe(call.isSafe());
        transformed.setSpreadSafe(call.isSpreadSafe());
        transformed.setImplicitThis(false);
        return transformed;
    }

    private Expression transformMethodCallOnThis(MethodCallExpression call) {
        Expression method = call.getMethod();
        Expression arguments = call.getArguments();
        if (method instanceof ConstantExpression) {
            String methodName = method.getText();
            List<MethodNode> methods = this.traitClass.getMethods(methodName);
            for (MethodNode methodNode : methods) {
                if (!methodName.equals(methodNode.getName()) || !methodNode.isPrivate()) continue;
                if (this.inClosure) {
                    return this.transformPrivateMethodCallOnThisInClosure(call, arguments, methodName);
                }
                return this.transformPrivateMethodCallOnThis(call, arguments, methodName);
            }
        }
        if (this.inClosure) {
            return this.transformMethodCallOnThisInClosure(call);
        }
        return this.transformMethodCallOnThisFallBack(call, method, arguments);
    }

    private Expression transformMethodCallOnThisFallBack(MethodCallExpression call, Expression method, Expression arguments) {
        MethodCallExpression transformed = new MethodCallExpression((Expression)this.weaved, method, this.transform(arguments));
        transformed.setSourcePosition(call);
        transformed.setSafe(call.isSafe());
        transformed.setSpreadSafe(call.isSpreadSafe());
        transformed.setImplicitThis(false);
        return transformed;
    }

    private Expression transformMethodCallOnThisInClosure(MethodCallExpression call) {
        MethodCallExpression transformed = new MethodCallExpression((Expression)call.getReceiver(), call.getMethod(), this.transform(call.getArguments()));
        transformed.setSourcePosition(call);
        transformed.setSafe(call.isSafe());
        transformed.setSpreadSafe(call.isSpreadSafe());
        transformed.setImplicitThis(call.isImplicitThis());
        return transformed;
    }

    private Expression transformPrivateMethodCallOnThis(MethodCallExpression call, Expression arguments, String methodName) {
        ArgumentListExpression newArgs = this.createArgumentList(arguments);
        MethodCallExpression transformed = new MethodCallExpression((Expression)new VariableExpression("this"), methodName, (Expression)newArgs);
        transformed.setSourcePosition(call);
        transformed.setSafe(call.isSafe());
        transformed.setSpreadSafe(call.isSpreadSafe());
        transformed.setImplicitThis(true);
        return transformed;
    }

    private Expression transformPrivateMethodCallOnThisInClosure(MethodCallExpression call, Expression arguments, String methodName) {
        ArgumentListExpression newArgs = this.createArgumentList(arguments);
        MethodCallExpression transformed = new MethodCallExpression((Expression)new ClassExpression(this.traitHelperClass), methodName, (Expression)newArgs);
        transformed.setSourcePosition(call);
        transformed.setSafe(call.isSafe());
        transformed.setSpreadSafe(call.isSpreadSafe());
        transformed.setImplicitThis(true);
        return transformed;
    }

    private Expression createFieldHelperReceiver() {
        return ClassHelper.CLASS_Type.equals(this.weaved.getOriginType()) ? this.weaved : new CastExpression(this.fieldHelper, this.weaved);
    }

    private ArgumentListExpression createArgumentList(Expression origCallArgs) {
        ArgumentListExpression newArgs = new ArgumentListExpression();
        newArgs.addExpression(new VariableExpression(this.weaved));
        if (origCallArgs instanceof ArgumentListExpression) {
            List<Expression> expressions = ((ArgumentListExpression)origCallArgs).getExpressions();
            for (Expression expression : expressions) {
                newArgs.addExpression(this.transform(expression));
            }
        } else {
            newArgs.addExpression(origCallArgs);
        }
        return newArgs;
    }
}

