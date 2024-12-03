/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.reflect.MethodUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import com.opensymphony.xwork2.interceptor.ValidationErrorAware;
import com.opensymphony.xwork2.interceptor.ValidationWorkflowAware;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;
import java.lang.reflect.Method;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultWorkflowInterceptor
extends MethodFilterInterceptor {
    private static final long serialVersionUID = 7563014655616490865L;
    private static final Logger LOG = LogManager.getLogger(DefaultWorkflowInterceptor.class);
    private static final Class[] EMPTY_CLASS_ARRAY = new Class[0];
    private String inputResultName = "input";

    public void setInputResultName(String inputResultName) {
        this.inputResultName = inputResultName;
    }

    @Override
    protected String doIntercept(ActionInvocation invocation) throws Exception {
        ValidationAware validationAwareAction;
        Object action = invocation.getAction();
        if (action instanceof ValidationAware && (validationAwareAction = (ValidationAware)action).hasErrors()) {
            LOG.debug("Errors on action [{}], returning result name [{}]", (Object)validationAwareAction, (Object)this.inputResultName);
            String resultName = this.inputResultName;
            resultName = this.processValidationWorkflowAware(action, resultName);
            resultName = this.processInputConfig(action, invocation.getProxy().getMethod(), resultName);
            resultName = this.processValidationErrorAware(action, resultName);
            return resultName;
        }
        return invocation.invoke();
    }

    private String processValidationWorkflowAware(Object action, String currentResultName) {
        String resultName = currentResultName;
        if (action instanceof ValidationWorkflowAware) {
            resultName = ((ValidationWorkflowAware)action).getInputResultName();
            LOG.debug("Changing result name from [{}] to [{}] because of processing [{}] interface applied to [{}]", (Object)currentResultName, (Object)resultName, (Object)ValidationWorkflowAware.class.getSimpleName(), action);
        }
        return resultName;
    }

    protected String processInputConfig(Object action, String method, String currentResultName) throws Exception {
        String resultName = currentResultName;
        InputConfig annotation = (InputConfig)MethodUtils.getAnnotation((Method)action.getClass().getMethod(method, EMPTY_CLASS_ARRAY), InputConfig.class, (boolean)true, (boolean)true);
        if (annotation != null) {
            resultName = StringUtils.isNotEmpty((CharSequence)annotation.methodName()) ? (String)MethodUtils.invokeMethod((Object)action, (boolean)true, (String)annotation.methodName()) : annotation.resultName();
            LOG.debug("Changing result name from [{}] to [{}] because of processing annotation [{}] on action [{}]", (Object)currentResultName, (Object)resultName, (Object)InputConfig.class.getSimpleName(), action);
        }
        return resultName;
    }

    protected String processValidationErrorAware(Object action, String currentResultName) {
        String resultName = currentResultName;
        if (action instanceof ValidationErrorAware) {
            resultName = ((ValidationErrorAware)action).actionErrorOccurred(currentResultName);
            LOG.debug("Changing result name from [{}] to [{}] because of processing interface [{}] on action [{}]", (Object)currentResultName, (Object)resultName, (Object)ValidationErrorAware.class.getSimpleName(), action);
        }
        return resultName;
    }
}

