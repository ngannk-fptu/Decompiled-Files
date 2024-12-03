/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.BooleanUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.LocalizedTextProvider;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.Parameterizable;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.ParametersInterceptor;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import com.opensymphony.xwork2.util.ClearableValueStack;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import java.util.Collections;
import java.util.Map;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.HttpParameters;

public class StaticParametersInterceptor
extends AbstractInterceptor {
    private boolean parse;
    private boolean overwrite;
    private boolean merge = true;
    private boolean devMode = false;
    private static final Logger LOG = LogManager.getLogger(StaticParametersInterceptor.class);
    private ValueStackFactory valueStackFactory;
    private LocalizedTextProvider localizedTextProvider;

    @Inject
    public void setValueStackFactory(ValueStackFactory valueStackFactory) {
        this.valueStackFactory = valueStackFactory;
    }

    @Inject(value="struts.devMode")
    public void setDevMode(String mode) {
        this.devMode = BooleanUtils.toBoolean((String)mode);
    }

    @Inject
    public void setLocalizedTextProvider(LocalizedTextProvider localizedTextProvider) {
        this.localizedTextProvider = localizedTextProvider;
    }

    public void setParse(String value) {
        this.parse = BooleanUtils.toBoolean((String)value);
    }

    public void setMerge(String value) {
        this.merge = BooleanUtils.toBoolean((String)value);
    }

    public void setOverwrite(String value) {
        this.overwrite = BooleanUtils.toBoolean((String)value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        ActionConfig config = invocation.getProxy().getConfig();
        Object action = invocation.getAction();
        Map<String, String> parameters = config.getParams();
        LOG.debug("Setting static parameters: {}", parameters);
        if (action instanceof Parameterizable) {
            ((Parameterizable)action).setParams(parameters);
        }
        if (parameters != null) {
            ActionContext ac = ActionContext.getContext();
            Map<String, Object> contextMap = ac.getContextMap();
            try {
                ReflectionContextState.setCreatingNullObjects(contextMap, true);
                ReflectionContextState.setReportingConversionErrors(contextMap, true);
                ValueStack stack = ac.getValueStack();
                ValueStack newStack = this.valueStackFactory.createValueStack(stack);
                boolean clearableStack = newStack instanceof ClearableValueStack;
                if (clearableStack) {
                    ((ClearableValueStack)((Object)newStack)).clearContextValues();
                    Map<String, Object> context = newStack.getContext();
                    ReflectionContextState.setCreatingNullObjects(context, true);
                    ReflectionContextState.setDenyMethodExecution(context, true);
                    ReflectionContextState.setReportingConversionErrors(context, true);
                    newStack.getActionContext().withLocale(stack.getActionContext().getLocale());
                }
                for (Map.Entry<String, String> entry : parameters.entrySet()) {
                    String val = entry.getValue();
                    if (this.parse && val instanceof String) {
                        val = TextParseUtil.translateVariables(val.toString(), stack);
                    }
                    try {
                        newStack.setValue(entry.getKey(), val);
                    }
                    catch (RuntimeException e) {
                        if (!this.devMode) continue;
                        String developerNotification = this.localizedTextProvider.findText(ParametersInterceptor.class, "devmode.notification", ActionContext.getContext().getLocale(), "Developer Notification:\n{0}", new Object[]{"Unexpected Exception caught setting '" + entry.getKey() + "' on '" + action.getClass() + ": " + e.getMessage()});
                        LOG.error(developerNotification);
                        if (!(action instanceof ValidationAware)) continue;
                        ((ValidationAware)action).addActionMessage(developerNotification);
                    }
                }
                if (clearableStack) {
                    stack.getActionContext().withConversionErrors(newStack.getActionContext().getConversionErrors());
                }
                if (this.merge) {
                    this.addParametersToContext(ac, parameters);
                }
            }
            finally {
                ReflectionContextState.setCreatingNullObjects(contextMap, false);
                ReflectionContextState.setReportingConversionErrors(contextMap, false);
            }
        }
        return invocation.invoke();
    }

    protected Map<String, String> retrieveParameters(ActionContext ac) {
        ActionConfig config = ac.getActionInvocation().getProxy().getConfig();
        if (config != null) {
            return config.getParams();
        }
        return Collections.emptyMap();
    }

    protected void addParametersToContext(ActionContext ac, Map<String, ?> newParams) {
        HttpParameters.Builder combinedParams;
        HttpParameters previousParams = ac.getParameters();
        if (this.overwrite) {
            combinedParams = HttpParameters.create().withParent(previousParams);
            combinedParams = combinedParams.withExtraParams(newParams);
        } else {
            combinedParams = HttpParameters.create(newParams);
            combinedParams = combinedParams.withExtraParams(previousParams);
        }
        ac.withParameters(combinedParams.build());
    }
}

