/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELContext
 *  javax.el.ELResolver
 *  javax.el.FunctionMapper
 *  javax.el.ValueExpression
 *  javax.el.VariableMapper
 */
package org.apache.sling.scripting.jsp.jasper.el;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import org.apache.sling.scripting.jsp.jasper.el.ELResolverImpl;

public final class ELContextImpl
extends ELContext {
    private static final FunctionMapper NullFunctionMapper = new FunctionMapper(){

        public Method resolveFunction(String prefix, String localName) {
            return null;
        }
    };
    private final ELResolver resolver;
    private FunctionMapper functionMapper = NullFunctionMapper;
    private VariableMapper variableMapper;

    public ELContextImpl() {
        this(ELResolverImpl.DefaultResolver);
    }

    public ELContextImpl(ELResolver resolver) {
        this.resolver = resolver;
    }

    public ELResolver getELResolver() {
        return this.resolver;
    }

    public FunctionMapper getFunctionMapper() {
        return this.functionMapper;
    }

    public VariableMapper getVariableMapper() {
        if (this.variableMapper == null) {
            this.variableMapper = new VariableMapperImpl();
        }
        return this.variableMapper;
    }

    public void setFunctionMapper(FunctionMapper functionMapper) {
        this.functionMapper = functionMapper;
    }

    public void setVariableMapper(VariableMapper variableMapper) {
        this.variableMapper = variableMapper;
    }

    private static final class VariableMapperImpl
    extends VariableMapper {
        private Map<String, ValueExpression> vars;

        private VariableMapperImpl() {
        }

        public ValueExpression resolveVariable(String variable) {
            if (this.vars == null) {
                return null;
            }
            return this.vars.get(variable);
        }

        public ValueExpression setVariable(String variable, ValueExpression expression) {
            if (this.vars == null) {
                this.vars = new HashMap<String, ValueExpression>();
            }
            return this.vars.put(variable, expression);
        }
    }
}

