/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ognl.MethodFailedException
 *  ognl.NoSuchPropertyException
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.ActionChainResult;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionEventListener;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.AsyncManager;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.UnknownHandlerManager;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.ConditionalInterceptor;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.interceptor.WithLazyParams;
import com.opensymphony.xwork2.ognl.OgnlUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import ognl.MethodFailedException;
import ognl.NoSuchPropertyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsException;

public class DefaultActionInvocation
implements ActionInvocation {
    private static final Logger LOG = LogManager.getLogger(DefaultActionInvocation.class);
    protected Object action;
    protected ActionProxy proxy;
    protected List<PreResultListener> preResultListeners;
    protected Map<String, Object> extraContext;
    protected ActionContext invocationContext;
    protected Iterator<InterceptorMapping> interceptors;
    protected ValueStack stack;
    protected Result result;
    protected Result explicitResult;
    protected String resultCode;
    protected boolean executed = false;
    protected boolean pushAction;
    protected ObjectFactory objectFactory;
    protected ActionEventListener actionEventListener;
    protected ValueStackFactory valueStackFactory;
    protected Container container;
    protected UnknownHandlerManager unknownHandlerManager;
    protected OgnlUtil ognlUtil;
    protected AsyncManager asyncManager;
    protected Callable<?> asyncAction;
    protected WithLazyParams.LazyParamInjector lazyParamInjector;

    public DefaultActionInvocation(Map<String, Object> extraContext, boolean pushAction) {
        this.extraContext = extraContext;
        this.pushAction = pushAction;
    }

    @Inject
    public void setUnknownHandlerManager(UnknownHandlerManager unknownHandlerManager) {
        this.unknownHandlerManager = unknownHandlerManager;
    }

    @Inject
    public void setValueStackFactory(ValueStackFactory fac) {
        this.valueStackFactory = fac;
    }

    @Inject
    public void setObjectFactory(ObjectFactory fac) {
        this.objectFactory = fac;
    }

    @Inject
    public void setContainer(Container cont) {
        this.container = cont;
    }

    @Override
    @Inject(required=false)
    public void setActionEventListener(ActionEventListener listener) {
        this.actionEventListener = listener;
    }

    @Inject
    public void setOgnlUtil(OgnlUtil ognlUtil) {
        this.ognlUtil = ognlUtil;
    }

    @Inject(required=false)
    public void setAsyncManager(AsyncManager asyncManager) {
        this.asyncManager = asyncManager;
    }

    @Override
    public Object getAction() {
        return this.action;
    }

    @Override
    public boolean isExecuted() {
        return this.executed;
    }

    @Override
    public ActionContext getInvocationContext() {
        return this.invocationContext;
    }

    @Override
    public ActionProxy getProxy() {
        return this.proxy;
    }

    @Override
    public Result getResult() throws Exception {
        Result proxyResult;
        ActionProxy aProxy;
        Result returnResult = this.result;
        while (returnResult instanceof ActionChainResult && (aProxy = ((ActionChainResult)returnResult).getProxy()) != null && (proxyResult = aProxy.getInvocation().getResult()) != null && aProxy.getExecuteResult()) {
            returnResult = proxyResult;
        }
        return returnResult;
    }

    @Override
    public String getResultCode() {
        return this.resultCode;
    }

    @Override
    public void setResultCode(String resultCode) {
        if (this.isExecuted()) {
            throw new IllegalStateException("Result has already been executed.");
        }
        this.resultCode = resultCode;
    }

    @Override
    public ValueStack getStack() {
        return this.stack;
    }

    @Override
    public void addPreResultListener(PreResultListener listener) {
        if (this.preResultListeners == null) {
            this.preResultListeners = new ArrayList<PreResultListener>(1);
        }
        this.preResultListeners.add(listener);
    }

    public Result createResult() throws Exception {
        LOG.trace("Creating result related to resultCode [{}]", (Object)this.resultCode);
        if (this.explicitResult != null) {
            Result ret = this.explicitResult;
            this.explicitResult = null;
            return ret;
        }
        ActionConfig config = this.proxy.getConfig();
        Map<String, ResultConfig> results = config.getResults();
        ResultConfig resultConfig = null;
        try {
            resultConfig = results.get(this.resultCode);
        }
        catch (NullPointerException e) {
            LOG.debug("Got NPE trying to read result configuration for resultCode [{}]", (Object)this.resultCode);
        }
        if (resultConfig == null) {
            resultConfig = results.get("*");
        }
        if (resultConfig != null) {
            try {
                return this.objectFactory.buildResult(resultConfig, this.invocationContext.getContextMap());
            }
            catch (Exception e) {
                LOG.error("There was an exception while instantiating the result of type {}", (Object)resultConfig.getClassName(), (Object)e);
                throw new StrutsException(e, (Object)resultConfig);
            }
        }
        if (this.resultCode != null && !"none".equals(this.resultCode) && this.unknownHandlerManager.hasUnknownHandlers()) {
            return this.unknownHandlerManager.handleUnknownResult(this.invocationContext, this.proxy.getActionName(), this.proxy.getConfig(), this.resultCode);
        }
        return null;
    }

    @Override
    public String invoke() throws Exception {
        if (this.executed) {
            throw new IllegalStateException("Action has already executed");
        }
        if (this.asyncManager == null || !this.asyncManager.hasAsyncActionResult()) {
            if (this.interceptors.hasNext()) {
                InterceptorMapping interceptorMapping = this.interceptors.next();
                Interceptor interceptor = interceptorMapping.getInterceptor();
                if (interceptor instanceof WithLazyParams) {
                    interceptor = this.lazyParamInjector.injectParams(interceptor, interceptorMapping.getParams(), this.invocationContext);
                }
                if (interceptor instanceof ConditionalInterceptor) {
                    this.resultCode = this.executeConditional((ConditionalInterceptor)interceptor);
                } else {
                    LOG.debug("Executing normal interceptor: {}", (Object)interceptorMapping.getName());
                    this.resultCode = interceptor.intercept(this);
                }
            } else {
                this.resultCode = this.invokeActionOnly();
            }
        } else {
            Object asyncActionResult = this.asyncManager.getAsyncActionResult();
            if (asyncActionResult instanceof Throwable) {
                throw new Exception((Throwable)asyncActionResult);
            }
            this.asyncAction = null;
            this.resultCode = this.saveResult(this.proxy.getConfig(), asyncActionResult);
        }
        if (this.asyncManager == null || this.asyncAction == null) {
            if (!this.executed) {
                if (this.preResultListeners != null) {
                    LOG.trace("Executing PreResultListeners for result [{}]", (Object)this.result);
                    for (PreResultListener listener : this.preResultListeners) {
                        listener.beforeResult(this, this.resultCode);
                    }
                }
                if (this.proxy.getExecuteResult()) {
                    this.executeResult();
                }
                this.executed = true;
            }
        } else {
            this.asyncManager.invokeAsyncAction(this.asyncAction);
        }
        return this.resultCode;
    }

    protected String executeConditional(ConditionalInterceptor conditionalInterceptor) throws Exception {
        if (conditionalInterceptor.shouldIntercept(this)) {
            LOG.debug("Executing conditional interceptor: {}", (Object)conditionalInterceptor.getClass().getSimpleName());
            return conditionalInterceptor.intercept(this);
        }
        LOG.debug("Interceptor: {} is disabled, skipping to next", (Object)conditionalInterceptor.getClass().getSimpleName());
        return this.invoke();
    }

    @Override
    public String invokeActionOnly() throws Exception {
        return this.invokeAction(this.getAction(), this.proxy.getConfig());
    }

    protected void createAction(Map<String, Object> contextMap) {
        try {
            this.action = this.objectFactory.buildAction(this.proxy.getActionName(), this.proxy.getNamespace(), this.proxy.getConfig(), contextMap);
        }
        catch (InstantiationException e) {
            throw new StrutsException("Unable to instantiate Action!", e, this.proxy.getConfig());
        }
        catch (IllegalAccessException e) {
            throw new StrutsException("Illegal access to constructor, is it public?", e, this.proxy.getConfig());
        }
        catch (Exception e) {
            String gripe = this.proxy == null ? "Whoa!  No ActionProxy instance found in current ActionInvocation.  This is bad ... very bad" : (this.proxy.getConfig() == null ? "Sheesh.  Where'd that ActionProxy get to?  I can't find it in the current ActionInvocation!?" : (this.proxy.getConfig().getClassName() == null ? "No Action defined for '" + this.proxy.getActionName() + "' in namespace '" + this.proxy.getNamespace() + "'" : "Unable to instantiate Action, " + this.proxy.getConfig().getClassName() + ",  defined for '" + this.proxy.getActionName() + "' in namespace '" + this.proxy.getNamespace() + "'"));
            gripe = gripe + e.getMessage();
            throw new StrutsException(gripe, e, this.proxy.getConfig());
        }
        if (this.actionEventListener != null) {
            this.action = this.actionEventListener.prepare(this.action, this.stack);
        }
    }

    protected Map<String, Object> createContextMap() {
        ActionContext actionContext;
        if (ActionContext.containsValueStack(this.extraContext)) {
            this.stack = ActionContext.of(this.extraContext).getValueStack();
            if (this.stack == null) {
                throw new IllegalStateException("There was a null Stack set into the extra params.");
            }
            actionContext = this.stack.getActionContext();
        } else {
            this.stack = this.valueStackFactory.createValueStack();
            actionContext = this.stack.getActionContext();
        }
        return actionContext.withExtraContext(this.extraContext).withActionInvocation(this).withContainer(this.container).getContextMap();
    }

    private void executeResult() throws Exception {
        this.result = this.createResult();
        if (this.result != null) {
            this.result.execute(this);
        } else {
            if (this.resultCode != null && !"none".equals(this.resultCode)) {
                throw new ConfigurationException("No result defined for action " + this.getAction().getClass().getName() + " and result " + this.getResultCode(), (Object)this.proxy.getConfig());
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("No result returned for action {} at {}", (Object)this.getAction().getClass().getName(), (Object)this.proxy.getConfig().getLocation());
            }
        }
    }

    @Override
    public void init(ActionProxy proxy) {
        this.proxy = proxy;
        Map<String, Object> contextMap = this.createContextMap();
        ActionContext actionContext = ActionContext.getContext();
        if (actionContext != null) {
            actionContext.withActionInvocation(this);
        }
        this.createAction(contextMap);
        if (this.pushAction) {
            this.stack.push(this.action);
            contextMap.put("action", this.action);
        }
        this.invocationContext = ActionContext.of(contextMap).withActionName(proxy.getActionName());
        this.createInterceptors(proxy);
        this.prepareLazyParamInjector(this.invocationContext.getValueStack());
    }

    protected void prepareLazyParamInjector(ValueStack valueStack) {
        this.lazyParamInjector = new WithLazyParams.LazyParamInjector(valueStack);
        this.container.inject(this.lazyParamInjector);
    }

    protected void createInterceptors(ActionProxy proxy) {
        ArrayList<InterceptorMapping> interceptorList = new ArrayList<InterceptorMapping>(proxy.getConfig().getInterceptors());
        this.interceptors = interceptorList.iterator();
    }

    protected String invokeAction(Object action, ActionConfig actionConfig) throws Exception {
        String methodName = this.proxy.getMethod();
        LOG.debug("Executing action method = {}", (Object)methodName);
        try {
            Object methodResult;
            try {
                methodResult = this.ognlUtil.callMethod(methodName + "()", this.getStack().getContext(), action);
            }
            catch (MethodFailedException e) {
                if (e.getReason() instanceof NoSuchMethodException) {
                    if (this.unknownHandlerManager.hasUnknownHandlers()) {
                        try {
                            methodResult = this.unknownHandlerManager.handleUnknownMethod(action, methodName);
                        }
                        catch (NoSuchMethodException ignore) {
                            throw e;
                        }
                    } else {
                        throw e;
                    }
                    if (methodResult == null) {
                        throw e;
                    }
                }
                throw e;
            }
            return this.saveResult(actionConfig, methodResult);
        }
        catch (NoSuchPropertyException e) {
            throw new IllegalArgumentException("The " + methodName + "() is not defined in action " + this.getAction().getClass() + "");
        }
        catch (MethodFailedException e) {
            String result;
            Throwable t = e.getCause();
            if (this.actionEventListener != null && (result = this.actionEventListener.handleException(t, this.getStack())) != null) {
                return result;
            }
            if (t instanceof Exception) {
                throw (Exception)t;
            }
            throw e;
        }
    }

    protected String saveResult(ActionConfig actionConfig, Object methodResult) {
        if (methodResult instanceof Result) {
            this.explicitResult = (Result)methodResult;
            this.container.inject(this.explicitResult);
            return null;
        }
        if (methodResult instanceof Callable) {
            this.asyncAction = (Callable)methodResult;
            return null;
        }
        return (String)methodResult;
    }
}

