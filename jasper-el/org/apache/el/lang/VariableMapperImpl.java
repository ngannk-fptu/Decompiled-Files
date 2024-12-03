/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ValueExpression
 *  javax.el.VariableMapper
 */
package org.apache.el.lang;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import javax.el.ValueExpression;
import javax.el.VariableMapper;

public class VariableMapperImpl
extends VariableMapper
implements Externalizable {
    private static final long serialVersionUID = 1L;
    private Map<String, ValueExpression> vars = new HashMap<String, ValueExpression>();

    public ValueExpression resolveVariable(String variable) {
        return this.vars.get(variable);
    }

    public ValueExpression setVariable(String variable, ValueExpression expression) {
        if (expression == null) {
            return this.vars.remove(variable);
        }
        return this.vars.put(variable, expression);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.vars = (Map)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(this.vars);
    }
}

