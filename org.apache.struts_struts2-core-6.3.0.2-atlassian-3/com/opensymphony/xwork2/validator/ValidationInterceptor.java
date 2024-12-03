/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.Validateable;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.opensymphony.xwork2.interceptor.PrefixMethodInvocationUtil;
import com.opensymphony.xwork2.validator.ActionValidatorManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ValidationInterceptor
extends MethodFilterInterceptor {
    private static final Logger LOG = LogManager.getLogger(ValidationInterceptor.class);
    private static final String VALIDATE_PREFIX = "validate";
    private static final String ALT_VALIDATE_PREFIX = "validateDo";
    private boolean validateAnnotatedMethodOnly;
    private ActionValidatorManager actionValidatorManager;
    private boolean alwaysInvokeValidate = true;
    private boolean programmatic = true;
    private boolean declarative = true;

    @Inject
    public void setActionValidatorManager(ActionValidatorManager mgr) {
        this.actionValidatorManager = mgr;
    }

    public void setProgrammatic(boolean programmatic) {
        this.programmatic = programmatic;
    }

    public void setDeclarative(boolean declarative) {
        this.declarative = declarative;
    }

    public void setAlwaysInvokeValidate(String alwaysInvokeValidate) {
        this.alwaysInvokeValidate = Boolean.parseBoolean(alwaysInvokeValidate);
    }

    public boolean isValidateAnnotatedMethodOnly() {
        return this.validateAnnotatedMethodOnly;
    }

    public void setValidateAnnotatedMethodOnly(boolean validateAnnotatedMethodOnly) {
        this.validateAnnotatedMethodOnly = validateAnnotatedMethodOnly;
    }

    protected void doBeforeInvocation(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        ActionProxy proxy = invocation.getProxy();
        String context = this.getValidationContext(proxy);
        String method = proxy.getMethod();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Validating {}/{} with method {}.", (Object)invocation.getProxy().getNamespace(), (Object)invocation.getProxy().getActionName(), (Object)method);
        }
        if (this.declarative) {
            if (this.validateAnnotatedMethodOnly) {
                this.actionValidatorManager.validate(action, context, method);
            } else {
                this.actionValidatorManager.validate(action, context);
            }
        }
        if (action instanceof Validateable && this.programmatic) {
            Exception exception = null;
            Validateable validateable = (Validateable)action;
            LOG.debug("Invoking validate() on action {}", (Object)validateable);
            try {
                PrefixMethodInvocationUtil.invokePrefixMethod(invocation, new String[]{VALIDATE_PREFIX, ALT_VALIDATE_PREFIX});
            }
            catch (Exception e) {
                LOG.warn("an exception occured while executing the prefix method", (Throwable)e);
                exception = e;
            }
            if (this.alwaysInvokeValidate) {
                validateable.validate();
            }
            if (exception != null) {
                throw exception;
            }
        }
    }

    @Override
    protected String doIntercept(ActionInvocation invocation) throws Exception {
        this.doBeforeInvocation(invocation);
        return invocation.invoke();
    }

    protected String getValidationContext(ActionProxy proxy) {
        return proxy.getActionName();
    }
}

