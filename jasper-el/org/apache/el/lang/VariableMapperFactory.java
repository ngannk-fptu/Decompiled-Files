/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ValueExpression
 *  javax.el.VariableMapper
 */
package org.apache.el.lang;

import javax.el.ValueExpression;
import javax.el.VariableMapper;
import org.apache.el.lang.VariableMapperImpl;
import org.apache.el.util.MessageFactory;

public class VariableMapperFactory
extends VariableMapper {
    private final VariableMapper target;
    private VariableMapper momento;

    public VariableMapperFactory(VariableMapper target) {
        if (target == null) {
            throw new NullPointerException(MessageFactory.get("error.noVariableMapperTarget"));
        }
        this.target = target;
    }

    public VariableMapper create() {
        return this.momento;
    }

    public ValueExpression resolveVariable(String variable) {
        ValueExpression expr = this.target.resolveVariable(variable);
        if (expr != null) {
            if (this.momento == null) {
                this.momento = new VariableMapperImpl();
            }
            this.momento.setVariable(variable, expr);
        }
        return expr;
    }

    public ValueExpression setVariable(String variable, ValueExpression expression) {
        throw new UnsupportedOperationException(MessageFactory.get("error.cannotSetVariables"));
    }
}

