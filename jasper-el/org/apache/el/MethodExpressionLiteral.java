/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELContext
 *  javax.el.ELException
 *  javax.el.MethodExpression
 *  javax.el.MethodInfo
 */
package org.apache.el;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.MethodExpression;
import javax.el.MethodInfo;
import org.apache.el.util.ReflectionUtil;

public class MethodExpressionLiteral
extends MethodExpression
implements Externalizable {
    private Class<?> expectedType;
    private String expr;
    private Class<?>[] paramTypes;

    public MethodExpressionLiteral() {
    }

    public MethodExpressionLiteral(String expr, Class<?> expectedType, Class<?>[] paramTypes) {
        this.expr = expr;
        this.expectedType = expectedType;
        this.paramTypes = paramTypes;
    }

    public MethodInfo getMethodInfo(ELContext context) throws ELException {
        context.notifyBeforeEvaluation(this.getExpressionString());
        MethodInfo result = new MethodInfo(this.expr, this.expectedType, (Class[])this.paramTypes);
        context.notifyAfterEvaluation(this.getExpressionString());
        return result;
    }

    public Object invoke(ELContext context, Object[] params) throws ELException {
        context.notifyBeforeEvaluation(this.getExpressionString());
        Object result = this.expectedType != null ? context.convertToType((Object)this.expr, this.expectedType) : this.expr;
        context.notifyAfterEvaluation(this.getExpressionString());
        return result;
    }

    public String getExpressionString() {
        return this.expr;
    }

    public boolean equals(Object obj) {
        return obj instanceof MethodExpressionLiteral && this.hashCode() == obj.hashCode();
    }

    public int hashCode() {
        return this.expr.hashCode();
    }

    public boolean isLiteralText() {
        return true;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.expr = in.readUTF();
        String type = in.readUTF();
        if (!type.isEmpty()) {
            this.expectedType = ReflectionUtil.forName(type);
        }
        this.paramTypes = ReflectionUtil.toTypeArray((String[])in.readObject());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(this.expr);
        out.writeUTF(this.expectedType != null ? this.expectedType.getName() : "");
        out.writeObject(ReflectionUtil.toTypeNameArray(this.paramTypes));
    }
}

