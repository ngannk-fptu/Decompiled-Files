/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import ognl.BooleanExpression;
import ognl.OgnlContext;
import ognl.OgnlOps;
import ognl.OgnlParser;
import ognl.OgnlRuntime;
import ognl.enhance.UnsupportedCompilationException;

public abstract class ComparisonExpression
extends BooleanExpression {
    public ComparisonExpression(int id) {
        super(id);
    }

    public ComparisonExpression(OgnlParser p, int id) {
        super(p, id);
    }

    public abstract String getComparisonFunction();

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        if (target == null) {
            throw new UnsupportedCompilationException("Current target is null, can't compile.");
        }
        try {
            Object value = this.getValueBody(context, target);
            this._getterClass = value != null && Boolean.class.isAssignableFrom(value.getClass()) ? Boolean.TYPE : (value != null ? value.getClass() : Boolean.TYPE);
            OgnlRuntime.getChildSource(context, target, this._children[0]);
            OgnlRuntime.getChildSource(context, target, this._children[1]);
            boolean conversion = OgnlRuntime.shouldConvertNumericTypes(context);
            String result = conversion ? "(" + this.getComparisonFunction() + "( ($w) (" : "(";
            result = result + OgnlRuntime.getChildSource(context, target, this._children[0], conversion) + " " + (conversion ? "), ($w) " : this.getExpressionOperator(0)) + " " + OgnlRuntime.getChildSource(context, target, this._children[1], conversion);
            result = result + (conversion ? ")" : "");
            context.setCurrentType(Boolean.TYPE);
            result = result + ")";
            return result;
        }
        catch (NullPointerException e) {
            throw new UnsupportedCompilationException("evaluation resulted in null expression.");
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
    }
}

