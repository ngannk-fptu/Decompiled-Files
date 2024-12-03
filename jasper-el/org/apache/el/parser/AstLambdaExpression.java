/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELContext
 *  javax.el.ELException
 *  javax.el.LambdaExpression
 *  javax.el.ValueExpression
 */
package org.apache.el.parser;

import java.util.ArrayList;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.LambdaExpression;
import javax.el.ValueExpression;
import org.apache.el.ValueExpressionImpl;
import org.apache.el.lang.EvaluationContext;
import org.apache.el.lang.LambdaExpressionNestedState;
import org.apache.el.parser.AstLambdaParameters;
import org.apache.el.parser.AstMethodParameters;
import org.apache.el.parser.Node;
import org.apache.el.parser.SimpleNode;
import org.apache.el.util.MessageFactory;

public class AstLambdaExpression
extends SimpleNode {
    public AstLambdaExpression(int id) {
        super(id);
    }

    @Override
    public Object getValue(EvaluationContext ctx) throws ELException {
        int methodParameterSetCount;
        LambdaExpressionNestedState state = ctx.getLambdaExpressionNestedState();
        if (state == null) {
            state = new LambdaExpressionNestedState();
            this.populateNestedState(state);
            ctx.setLambdaExpressionNestedState(state);
        }
        if ((methodParameterSetCount = this.jjtGetNumChildren() - 2) > state.getNestingCount()) {
            throw new ELException(MessageFactory.get("error.lambda.tooManyMethodParameterSets"));
        }
        AstLambdaParameters formalParametersNode = (AstLambdaParameters)this.children[0];
        SimpleNode[] formalParamNodes = formalParametersNode.children;
        ValueExpressionImpl ve = new ValueExpressionImpl("", this.children[1], ctx.getFunctionMapper(), ctx.getVariableMapper(), null);
        ArrayList<String> formalParameters = new ArrayList<String>();
        if (formalParamNodes != null) {
            for (SimpleNode formalParamNode : formalParamNodes) {
                formalParameters.add(formalParamNode.getImage());
            }
        }
        LambdaExpression le = new LambdaExpression(formalParameters, (ValueExpression)ve);
        le.setELContext((ELContext)ctx);
        if (this.jjtGetNumChildren() == 2) {
            if (state.getHasFormalParameters()) {
                return le;
            }
            return le.invoke((ELContext)ctx, (Object[])null);
        }
        int methodParameterIndex = 2;
        Object result = le.invoke(((AstMethodParameters)this.children[methodParameterIndex]).getParameters(ctx));
        ++methodParameterIndex;
        while (result instanceof LambdaExpression && methodParameterIndex < this.jjtGetNumChildren()) {
            result = ((LambdaExpression)result).invoke(((AstMethodParameters)this.children[methodParameterIndex]).getParameters(ctx));
            ++methodParameterIndex;
        }
        return result;
    }

    private void populateNestedState(LambdaExpressionNestedState lambdaExpressionNestedState) {
        lambdaExpressionNestedState.incrementNestingCount();
        if (this.jjtGetNumChildren() > 1) {
            Node firstChild = this.jjtGetChild(0);
            if (firstChild instanceof AstLambdaParameters) {
                if (firstChild.jjtGetNumChildren() > 0) {
                    lambdaExpressionNestedState.setHasFormalParameters();
                }
            } else {
                return;
            }
            Node secondChild = this.jjtGetChild(1);
            if (secondChild instanceof AstLambdaExpression) {
                ((AstLambdaExpression)secondChild).populateNestedState(lambdaExpressionNestedState);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (SimpleNode n : this.children) {
            result.append(((Object)n).toString());
        }
        return result.toString();
    }
}

