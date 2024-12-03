/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm.sc;

import java.util.Arrays;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.classgen.AsmClassGenerator;
import org.codehaus.groovy.transform.sc.ListOfExpressionsExpression;
import org.codehaus.groovy.transform.sc.TemporaryVariableExpression;

public abstract class StaticPropertyAccessHelper {
    public static Expression transformToSetterCall(Expression receiver, MethodNode setterMethod, Expression arguments, boolean implicitThis, boolean safe, boolean spreadSafe, boolean requiresReturnValue, Expression location) {
        if (requiresReturnValue) {
            TemporaryVariableExpression tmp = new TemporaryVariableExpression(arguments);
            PoppingMethodCallExpression call = new PoppingMethodCallExpression(receiver, setterMethod, tmp);
            call.setImplicitThis(implicitThis);
            call.setSafe(safe);
            call.setSpreadSafe(spreadSafe);
            call.setSourcePosition(location);
            PoppingListOfExpressionsExpression result = new PoppingListOfExpressionsExpression(tmp, call);
            result.setSourcePosition(location);
            return result;
        }
        MethodCallExpression call = new MethodCallExpression(receiver, setterMethod.getName(), arguments);
        call.setImplicitThis(implicitThis);
        call.setSafe(safe);
        call.setSpreadSafe(spreadSafe);
        call.setMethodTarget(setterMethod);
        call.setSourcePosition(location);
        return call;
    }

    private static class PoppingMethodCallExpression
    extends MethodCallExpression {
        private final Expression receiver;
        private final MethodNode setter;
        private final TemporaryVariableExpression tmp;

        public PoppingMethodCallExpression(Expression receiver, MethodNode setterMethod, TemporaryVariableExpression tmp) {
            super(receiver, setterMethod.getName(), (Expression)tmp);
            this.receiver = receiver;
            this.setter = setterMethod;
            this.tmp = tmp;
            this.setMethodTarget(setterMethod);
        }

        @Override
        public Expression transformExpression(ExpressionTransformer transformer) {
            PoppingMethodCallExpression trn = new PoppingMethodCallExpression(this.receiver.transformExpression(transformer), this.setter, (TemporaryVariableExpression)this.tmp.transformExpression(transformer));
            trn.copyNodeMetaData(this);
            trn.setImplicitThis(this.isImplicitThis());
            trn.setSafe(this.isSafe());
            trn.setSpreadSafe(this.isSpreadSafe());
            return trn;
        }

        @Override
        public void visit(GroovyCodeVisitor visitor) {
            super.visit(visitor);
            if (visitor instanceof AsmClassGenerator) {
                ((AsmClassGenerator)visitor).getController().getOperandStack().pop();
            }
        }
    }

    private static class PoppingListOfExpressionsExpression
    extends ListOfExpressionsExpression {
        private final TemporaryVariableExpression tmp;
        private final PoppingMethodCallExpression call;

        public PoppingListOfExpressionsExpression(TemporaryVariableExpression tmp, PoppingMethodCallExpression call) {
            super(Arrays.asList(tmp, call));
            this.tmp = tmp;
            this.call = call;
        }

        @Override
        public Expression transformExpression(ExpressionTransformer transformer) {
            PoppingMethodCallExpression tcall = (PoppingMethodCallExpression)this.call.transformExpression(transformer);
            return new PoppingListOfExpressionsExpression(tcall.tmp, tcall);
        }

        @Override
        public void visit(GroovyCodeVisitor visitor) {
            super.visit(visitor);
            if (visitor instanceof AsmClassGenerator) {
                this.tmp.remove(((AsmClassGenerator)visitor).getController());
            }
        }
    }
}

