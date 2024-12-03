/*
 * Decompiled with CFR 0.152.
 */
package javax.el;

import java.lang.reflect.Method;
import java.util.Map;
import javax.el.BeanNameELResolver;
import javax.el.BeanNameResolver;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.EvaluationListener;
import javax.el.ExpressionFactory;
import javax.el.StandardELContext;
import javax.el.Util;
import javax.el.ValueExpression;

public class ELManager {
    private StandardELContext context = null;

    public static ExpressionFactory getExpressionFactory() {
        return Util.getExpressionFactory();
    }

    public StandardELContext getELContext() {
        if (this.context == null) {
            this.context = new StandardELContext(ELManager.getExpressionFactory());
        }
        return this.context;
    }

    public ELContext setELContext(ELContext context) {
        StandardELContext oldContext = this.context;
        this.context = new StandardELContext(context);
        return oldContext;
    }

    public void addBeanNameResolver(BeanNameResolver beanNameResolver) {
        this.getELContext().addELResolver(new BeanNameELResolver(beanNameResolver));
    }

    public void addELResolver(ELResolver resolver) {
        this.getELContext().addELResolver(resolver);
    }

    public void mapFunction(String prefix, String function, Method method) {
        this.getELContext().getFunctionMapper().mapFunction(prefix, function, method);
    }

    public void setVariable(String variable, ValueExpression expression) {
        this.getELContext().getVariableMapper().setVariable(variable, expression);
    }

    public void importStatic(String staticMemberName) throws ELException {
        this.getELContext().getImportHandler().importStatic(staticMemberName);
    }

    public void importClass(String className) throws ELException {
        this.getELContext().getImportHandler().importClass(className);
    }

    public void importPackage(String packageName) {
        this.getELContext().getImportHandler().importPackage(packageName);
    }

    public Object defineBean(String name, Object bean) {
        Map<String, Object> localBeans = this.getELContext().getLocalBeans();
        if (bean == null) {
            return localBeans.remove(name);
        }
        return localBeans.put(name, bean);
    }

    public void addEvaluationListener(EvaluationListener listener) {
        this.getELContext().addEvaluationListener(listener);
    }
}

