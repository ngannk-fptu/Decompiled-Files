/*
 * Decompiled with CFR 0.152.
 */
package javax.el;

import javax.el.ELContext;
import javax.el.Expression;
import javax.el.ValueReference;

public abstract class ValueExpression
extends Expression {
    private static final long serialVersionUID = 8577809572381654673L;

    public abstract Object getValue(ELContext var1);

    public abstract void setValue(ELContext var1, Object var2);

    public abstract boolean isReadOnly(ELContext var1);

    public abstract Class<?> getType(ELContext var1);

    public abstract Class<?> getExpectedType();

    public ValueReference getValueReference(ELContext context) {
        context.notifyBeforeEvaluation(this.getExpressionString());
        context.notifyAfterEvaluation(this.getExpressionString());
        return null;
    }
}

