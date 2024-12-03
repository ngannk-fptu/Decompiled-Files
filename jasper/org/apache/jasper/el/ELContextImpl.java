/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ArrayELResolver
 *  javax.el.BeanELResolver
 *  javax.el.CompositeELResolver
 *  javax.el.ELContext
 *  javax.el.ELManager
 *  javax.el.ELResolver
 *  javax.el.ExpressionFactory
 *  javax.el.FunctionMapper
 *  javax.el.ListELResolver
 *  javax.el.MapELResolver
 *  javax.el.ResourceBundleELResolver
 *  javax.el.StaticFieldELResolver
 *  javax.el.ValueExpression
 *  javax.el.VariableMapper
 */
package org.apache.jasper.el;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELManager;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.FunctionMapper;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ResourceBundleELResolver;
import javax.el.StaticFieldELResolver;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import org.apache.jasper.Constants;

public class ELContextImpl
extends ELContext {
    private static final FunctionMapper NullFunctionMapper = new FunctionMapper(){

        public Method resolveFunction(String prefix, String localName) {
            return null;
        }
    };
    private static final ELResolver DefaultResolver;
    private final ELResolver resolver;
    private FunctionMapper functionMapper = NullFunctionMapper;
    private VariableMapper variableMapper;

    public ELContextImpl(ExpressionFactory factory) {
        this(ELContextImpl.getDefaultResolver(factory));
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

    public static ELResolver getDefaultResolver(ExpressionFactory factory) {
        if (Constants.IS_SECURITY_ENABLED) {
            CompositeELResolver defaultResolver = new CompositeELResolver();
            defaultResolver.add(factory.getStreamELResolver());
            defaultResolver.add((ELResolver)new StaticFieldELResolver());
            defaultResolver.add((ELResolver)new MapELResolver());
            defaultResolver.add((ELResolver)new ResourceBundleELResolver());
            defaultResolver.add((ELResolver)new ListELResolver());
            defaultResolver.add((ELResolver)new ArrayELResolver());
            defaultResolver.add((ELResolver)new BeanELResolver());
            return defaultResolver;
        }
        return DefaultResolver;
    }

    static {
        if (Constants.IS_SECURITY_ENABLED) {
            DefaultResolver = null;
        } else {
            DefaultResolver = new CompositeELResolver();
            ((CompositeELResolver)DefaultResolver).add(ELManager.getExpressionFactory().getStreamELResolver());
            ((CompositeELResolver)DefaultResolver).add((ELResolver)new StaticFieldELResolver());
            ((CompositeELResolver)DefaultResolver).add((ELResolver)new MapELResolver());
            ((CompositeELResolver)DefaultResolver).add((ELResolver)new ResourceBundleELResolver());
            ((CompositeELResolver)DefaultResolver).add((ELResolver)new ListELResolver());
            ((CompositeELResolver)DefaultResolver).add((ELResolver)new ArrayELResolver());
            ((CompositeELResolver)DefaultResolver).add((ELResolver)new BeanELResolver());
        }
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
            if (expression == null) {
                return this.vars.remove(variable);
            }
            return this.vars.put(variable, expression);
        }
    }
}

