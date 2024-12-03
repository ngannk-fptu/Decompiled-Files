/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.trait;

import groovy.lang.MetaProperty;
import java.util.List;
import org.codehaus.groovy.ast.ClassCodeExpressionTransformer;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.trait.TraitHelpersTuple;
import org.codehaus.groovy.transform.trait.Traits;

class SuperCallTraitTransformer
extends ClassCodeExpressionTransformer {
    static final String UNRESOLVED_HELPER_CLASS = "UNRESOLVED_HELPER_CLASS";
    private final SourceUnit unit;

    SuperCallTraitTransformer(SourceUnit unit) {
        this.unit = unit;
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return this.unit;
    }

    @Override
    public Expression transform(Expression exp) {
        if (exp instanceof MethodCallExpression) {
            return this.transformMethodCallExpression((MethodCallExpression)exp);
        }
        if (exp instanceof BinaryExpression) {
            return this.transformBinaryExpression((BinaryExpression)exp);
        }
        return super.transform(exp);
    }

    private Expression transformBinaryExpression(BinaryExpression exp) {
        Expression trn = super.transform(exp);
        if (trn instanceof BinaryExpression) {
            BinaryExpression bin = (BinaryExpression)trn;
            Expression leftExpression = bin.getLeftExpression();
            if (bin.getOperation().getType() == 100 && leftExpression instanceof PropertyExpression) {
                ClassNode traitReceiver = null;
                PropertyExpression leftPropertyExpression = (PropertyExpression)leftExpression;
                if (this.isTraitSuperPropertyExpression(leftPropertyExpression.getObjectExpression())) {
                    PropertyExpression pexp = (PropertyExpression)leftPropertyExpression.getObjectExpression();
                    traitReceiver = pexp.getObjectExpression().getType();
                }
                if (traitReceiver != null) {
                    TraitHelpersTuple helpers = Traits.findHelpers(traitReceiver);
                    ClassNode helper = helpers.getHelper();
                    String setterName = MetaProperty.getSetterName(leftPropertyExpression.getPropertyAsString());
                    List<MethodNode> methods = helper.getMethods(setterName);
                    for (MethodNode method : methods) {
                        Parameter[] parameters = method.getParameters();
                        if (parameters.length != 2 || !parameters[0].getType().equals(traitReceiver)) continue;
                        ArgumentListExpression args = new ArgumentListExpression(new VariableExpression("this"), this.transform(exp.getRightExpression()));
                        MethodCallExpression setterCall = new MethodCallExpression((Expression)new ClassExpression(helper), setterName, (Expression)args);
                        setterCall.setMethodTarget(method);
                        setterCall.setImplicitThis(false);
                        return setterCall;
                    }
                    return bin;
                }
            }
        }
        return trn;
    }

    private Expression transformMethodCallExpression(MethodCallExpression exp) {
        Expression objectExpression;
        ClassNode traitReceiver;
        if (this.isTraitSuperPropertyExpression(exp.getObjectExpression()) && (traitReceiver = ((PropertyExpression)(objectExpression = exp.getObjectExpression())).getObjectExpression().getType()) != null) {
            ClassExpression receiver = new ClassExpression(this.getHelper(traitReceiver));
            ArgumentListExpression newArgs = new ArgumentListExpression();
            Expression arguments = exp.getArguments();
            newArgs.addExpression(new VariableExpression("this"));
            if (arguments instanceof TupleExpression) {
                List<Expression> expressions = ((TupleExpression)arguments).getExpressions();
                for (Expression expression : expressions) {
                    newArgs.addExpression(this.transform(expression));
                }
            } else {
                newArgs.addExpression(this.transform(arguments));
            }
            MethodCallExpression result = new MethodCallExpression((Expression)receiver, this.transform(exp.getMethod()), (Expression)newArgs);
            result.setImplicitThis(false);
            result.setSpreadSafe(exp.isSpreadSafe());
            result.setSafe(exp.isSafe());
            result.setSourcePosition(exp);
            return result;
        }
        return super.transform(exp);
    }

    private ClassNode getHelper(ClassNode traitReceiver) {
        if (this.helperClassNotCreatedYet(traitReceiver)) {
            ClassNode ret = new InnerClassNode(traitReceiver, Traits.helperClassName(traitReceiver), 5129, ClassHelper.OBJECT_TYPE, ClassNode.EMPTY_ARRAY, null).getPlainNodeReference();
            ret.setRedirect(null);
            traitReceiver.redirect().setNodeMetaData(UNRESOLVED_HELPER_CLASS, ret);
            return ret;
        }
        TraitHelpersTuple helpers = Traits.findHelpers(traitReceiver);
        return helpers.getHelper();
    }

    private boolean helperClassNotCreatedYet(ClassNode traitReceiver) {
        return !traitReceiver.redirect().getInnerClasses().hasNext() && this.unit.getAST().getClasses().contains(traitReceiver.redirect());
    }

    private boolean isTraitSuperPropertyExpression(Expression exp) {
        ClassNode type;
        PropertyExpression pexp;
        Expression objectExpression;
        return exp instanceof PropertyExpression && (objectExpression = (pexp = (PropertyExpression)exp).getObjectExpression()) instanceof ClassExpression && Traits.isTrait(type = objectExpression.getType()) && "super".equals(pexp.getPropertyAsString());
    }
}

