/*
 * Decompiled with CFR 0.152.
 */
package javax.el;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.BeanNameELResolver;
import javax.el.BeanNameResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.FunctionMapper;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.PropertyNotWritableException;
import javax.el.ResourceBundleELResolver;
import javax.el.StaticFieldELResolver;
import javax.el.ValueExpression;
import javax.el.VariableMapper;

public class StandardELContext
extends ELContext {
    private final ELContext wrappedContext;
    private final VariableMapper variableMapper;
    private final FunctionMapper functionMapper;
    private final CompositeELResolver standardResolver;
    private final CompositeELResolver customResolvers;
    private final Map<String, Object> localBeans = new HashMap<String, Object>();

    public StandardELContext(ExpressionFactory factory) {
        this.wrappedContext = null;
        this.variableMapper = new StandardVariableMapper();
        this.functionMapper = new StandardFunctionMapper(factory.getInitFunctionMap());
        this.standardResolver = new CompositeELResolver();
        this.customResolvers = new CompositeELResolver();
        ELResolver streamResolver = factory.getStreamELResolver();
        this.standardResolver.add(new BeanNameELResolver(new StandardBeanNameResolver(this.localBeans)));
        this.standardResolver.add(this.customResolvers);
        if (streamResolver != null) {
            this.standardResolver.add(streamResolver);
        }
        this.standardResolver.add(new StaticFieldELResolver());
        this.standardResolver.add(new MapELResolver());
        this.standardResolver.add(new ResourceBundleELResolver());
        this.standardResolver.add(new ListELResolver());
        this.standardResolver.add(new ArrayELResolver());
        this.standardResolver.add(new BeanELResolver());
    }

    public StandardELContext(ELContext context) {
        this.wrappedContext = context;
        this.variableMapper = context.getVariableMapper();
        this.functionMapper = context.getFunctionMapper();
        this.standardResolver = new CompositeELResolver();
        this.customResolvers = new CompositeELResolver();
        this.standardResolver.add(new BeanNameELResolver(new StandardBeanNameResolver(this.localBeans)));
        this.standardResolver.add(this.customResolvers);
        this.standardResolver.add(context.getELResolver());
    }

    @Override
    public void putContext(Class key, Object contextObject) {
        if (this.wrappedContext == null) {
            super.putContext(key, contextObject);
        } else {
            this.wrappedContext.putContext(key, contextObject);
        }
    }

    @Override
    public Object getContext(Class key) {
        if (this.wrappedContext == null) {
            return super.getContext(key);
        }
        return this.wrappedContext.getContext(key);
    }

    @Override
    public ELResolver getELResolver() {
        return this.standardResolver;
    }

    public void addELResolver(ELResolver resolver) {
        this.customResolvers.add(resolver);
    }

    @Override
    public FunctionMapper getFunctionMapper() {
        return this.functionMapper;
    }

    @Override
    public VariableMapper getVariableMapper() {
        return this.variableMapper;
    }

    Map<String, Object> getLocalBeans() {
        return this.localBeans;
    }

    private static class StandardVariableMapper
    extends VariableMapper {
        private Map<String, ValueExpression> vars;

        private StandardVariableMapper() {
        }

        @Override
        public ValueExpression resolveVariable(String variable) {
            if (this.vars == null) {
                return null;
            }
            return this.vars.get(variable);
        }

        @Override
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

    private static class StandardFunctionMapper
    extends FunctionMapper {
        private final Map<String, Method> methods = new HashMap<String, Method>();

        StandardFunctionMapper(Map<String, Method> initFunctionMap) {
            if (initFunctionMap != null) {
                this.methods.putAll(initFunctionMap);
            }
        }

        @Override
        public Method resolveFunction(String prefix, String localName) {
            String key = prefix + ':' + localName;
            return this.methods.get(key);
        }

        @Override
        public void mapFunction(String prefix, String localName, Method method) {
            String key = prefix + ':' + localName;
            if (method == null) {
                this.methods.remove(key);
            } else {
                this.methods.put(key, method);
            }
        }
    }

    private static class StandardBeanNameResolver
    extends BeanNameResolver {
        private final Map<String, Object> beans;

        StandardBeanNameResolver(Map<String, Object> beans) {
            this.beans = beans;
        }

        @Override
        public boolean isNameResolved(String beanName) {
            return this.beans.containsKey(beanName);
        }

        @Override
        public Object getBean(String beanName) {
            return this.beans.get(beanName);
        }

        @Override
        public void setBeanValue(String beanName, Object value) throws PropertyNotWritableException {
            this.beans.put(beanName, value);
        }

        @Override
        public boolean isReadOnly(String beanName) {
            return false;
        }

        @Override
        public boolean canCreateBean(String beanName) {
            return true;
        }
    }
}

