/*
 * Decompiled with CFR 0.152.
 */
package javax.el;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.Util;
import javax.el.ValueExpression;

public class LambdaExpression {
    private final List<String> formalParameters;
    private final ValueExpression expression;
    private final Map<String, Object> nestedArguments = new HashMap<String, Object>();
    private ELContext context = null;

    public LambdaExpression(List<String> formalParameters, ValueExpression expression) {
        this.formalParameters = formalParameters;
        this.expression = expression;
    }

    public void setELContext(ELContext context) {
        this.context = context;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object invoke(ELContext context, Object ... args) throws ELException {
        Objects.requireNonNull(context);
        int formalParamCount = 0;
        if (this.formalParameters != null) {
            formalParamCount = this.formalParameters.size();
        }
        int argCount = 0;
        if (args != null) {
            argCount = args.length;
        }
        if (formalParamCount > argCount) {
            throw new ELException(Util.message(context, "lambdaExpression.tooFewArgs", argCount, formalParamCount));
        }
        HashMap<String, Object> lambdaArguments = new HashMap<String, Object>(this.nestedArguments);
        for (int i = 0; i < formalParamCount; ++i) {
            lambdaArguments.put(this.formalParameters.get(i), args[i]);
        }
        context.enterLambdaScope(lambdaArguments);
        try {
            Object result = this.expression.getValue(context);
            if (result instanceof LambdaExpression) {
                ((LambdaExpression)result).nestedArguments.putAll(lambdaArguments);
            }
            Object object = result;
            return object;
        }
        finally {
            context.exitLambdaScope();
        }
    }

    public Object invoke(Object ... args) {
        return this.invoke(this.context, args);
    }
}

