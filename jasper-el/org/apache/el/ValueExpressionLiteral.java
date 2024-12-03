/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELContext
 *  javax.el.PropertyNotWritableException
 *  javax.el.ValueExpression
 */
package org.apache.el;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javax.el.ELContext;
import javax.el.PropertyNotWritableException;
import javax.el.ValueExpression;
import org.apache.el.util.MessageFactory;
import org.apache.el.util.ReflectionUtil;

public final class ValueExpressionLiteral
extends ValueExpression
implements Externalizable {
    private static final long serialVersionUID = 1L;
    private Object value;
    private String valueString;
    private Class<?> expectedType;

    public ValueExpressionLiteral() {
    }

    public ValueExpressionLiteral(Object value, Class<?> expectedType) {
        this.value = value;
        this.expectedType = expectedType;
    }

    public Object getValue(ELContext context) {
        context.notifyBeforeEvaluation(this.getExpressionString());
        Object result = this.expectedType != null ? context.convertToType(this.value, this.expectedType) : this.value;
        context.notifyAfterEvaluation(this.getExpressionString());
        return result;
    }

    public void setValue(ELContext context, Object value) {
        context.notifyBeforeEvaluation(this.getExpressionString());
        throw new PropertyNotWritableException(MessageFactory.get("error.value.literal.write", this.value));
    }

    public boolean isReadOnly(ELContext context) {
        context.notifyBeforeEvaluation(this.getExpressionString());
        context.notifyAfterEvaluation(this.getExpressionString());
        return true;
    }

    public Class<?> getType(ELContext context) {
        context.notifyBeforeEvaluation(this.getExpressionString());
        Class<?> result = this.value != null ? this.value.getClass() : null;
        context.notifyAfterEvaluation(this.getExpressionString());
        return result;
    }

    public Class<?> getExpectedType() {
        return this.expectedType;
    }

    public String getExpressionString() {
        if (this.valueString == null) {
            this.valueString = this.value != null ? this.value.toString() : null;
        }
        return this.valueString;
    }

    public boolean equals(Object obj) {
        return obj instanceof ValueExpressionLiteral && this.equals((ValueExpressionLiteral)obj);
    }

    public boolean equals(ValueExpressionLiteral ve) {
        return ve != null && this.value != null && ve.value != null && (this.value == ve.value || this.value.equals(ve.value));
    }

    public int hashCode() {
        return this.value != null ? this.value.hashCode() : 0;
    }

    public boolean isLiteralText() {
        return true;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(this.value);
        out.writeUTF(this.expectedType != null ? this.expectedType.getName() : "");
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.value = in.readObject();
        String type = in.readUTF();
        if (!type.isEmpty()) {
            this.expectedType = ReflectionUtil.forName(type);
        }
    }
}

