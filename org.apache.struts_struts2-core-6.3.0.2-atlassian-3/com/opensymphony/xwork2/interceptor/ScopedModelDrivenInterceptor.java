/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.ScopedModelDriven;
import java.lang.reflect.Method;
import java.util.Map;
import org.apache.struts2.StrutsException;

public class ScopedModelDrivenInterceptor
extends AbstractInterceptor {
    private static final Class[] EMPTY_CLASS_ARRAY = new Class[0];
    private static final String GET_MODEL = "getModel";
    private String scope;
    private String name;
    private String className;
    private ObjectFactory objectFactory;

    @Inject
    public void setObjectFactory(ObjectFactory factory) {
        this.objectFactory = factory;
    }

    protected Object resolveModel(ObjectFactory factory, ActionContext actionContext, String modelClassName, String modelScope, String modelName) throws Exception {
        Object model;
        Map<String, Object> scopeMap = actionContext.getContextMap();
        if ("session".equals(modelScope)) {
            scopeMap = actionContext.getSession();
        }
        if ((model = scopeMap.get(modelName)) == null) {
            model = factory.buildBean(modelClassName, null);
            scopeMap.put(modelName, model);
        }
        return model;
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        ScopedModelDriven modelDriven;
        Object action = invocation.getAction();
        if (action instanceof ScopedModelDriven && (modelDriven = (ScopedModelDriven)action).getModel() == null) {
            String modelName;
            ActionContext ctx = ActionContext.getContext();
            ActionConfig config = invocation.getProxy().getConfig();
            String cName = this.className;
            if (cName == null) {
                try {
                    Method method = action.getClass().getMethod(GET_MODEL, EMPTY_CLASS_ARRAY);
                    Class<?> cls = method.getReturnType();
                    cName = cls.getName();
                }
                catch (NoSuchMethodException e) {
                    throw new StrutsException("The getModel() is not defined in action " + action.getClass() + "", (Object)config);
                }
            }
            if ((modelName = this.name) == null) {
                modelName = cName;
            }
            Object model = this.resolveModel(this.objectFactory, ctx, cName, this.scope, modelName);
            modelDriven.setModel(model);
            modelDriven.setScopeKey(modelName);
        }
        return invocation.invoke();
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}

