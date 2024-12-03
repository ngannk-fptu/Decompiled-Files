/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.LocalizedTextProvider;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.ParametersInterceptor;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import com.opensymphony.xwork2.security.AcceptedPatternsChecker;
import com.opensymphony.xwork2.security.ExcludedPatternsChecker;
import com.opensymphony.xwork2.util.ClearableValueStack;
import com.opensymphony.xwork2.util.Evaluated;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.Parameter;

public class AliasInterceptor
extends AbstractInterceptor {
    private static final Logger LOG = LogManager.getLogger(AliasInterceptor.class);
    private static final String DEFAULT_ALIAS_KEY = "aliases";
    protected String aliasesKey = "aliases";
    protected ValueStackFactory valueStackFactory;
    protected LocalizedTextProvider localizedTextProvider;
    protected boolean devMode = false;
    private ExcludedPatternsChecker excludedPatterns;
    private AcceptedPatternsChecker acceptedPatterns;

    @Inject(value="struts.devMode")
    public void setDevMode(String mode) {
        this.devMode = Boolean.parseBoolean(mode);
    }

    @Inject
    public void setValueStackFactory(ValueStackFactory valueStackFactory) {
        this.valueStackFactory = valueStackFactory;
    }

    @Inject
    public void setLocalizedTextProvider(LocalizedTextProvider localizedTextProvider) {
        this.localizedTextProvider = localizedTextProvider;
    }

    @Inject
    public void setExcludedPatterns(ExcludedPatternsChecker excludedPatterns) {
        this.excludedPatterns = excludedPatterns;
    }

    @Inject
    public void setAcceptedPatterns(AcceptedPatternsChecker acceptedPatterns) {
        this.acceptedPatterns = acceptedPatterns;
    }

    public void setAliasesKey(String aliasesKey) {
        this.aliasesKey = aliasesKey;
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        ActionConfig config = invocation.getProxy().getConfig();
        ActionContext ac = invocation.getInvocationContext();
        Object action = invocation.getAction();
        Map<String, String> parameters = config.getParams();
        if (parameters.containsKey(this.aliasesKey)) {
            String aliasExpression = parameters.get(this.aliasesKey);
            ValueStack stack = ac.getValueStack();
            Object obj = stack.findValue(aliasExpression);
            if (obj instanceof Map) {
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
                Map aliases = (Map)obj;
                for (Map.Entry o : aliases.entrySet()) {
                    Parameter param;
                    HttpParameters contextParameters;
                    String alias;
                    Map.Entry entry = o;
                    String name = entry.getKey().toString();
                    if (this.isNotAcceptableExpression(name) || this.isNotAcceptableExpression(alias = (String)entry.getValue())) continue;
                    Evaluated value = new Evaluated(stack.findValue(name));
                    if (!value.isDefined() && null != (contextParameters = ActionContext.getContext().getParameters()) && (param = contextParameters.get(name)).isDefined()) {
                        value = new Evaluated(param.getValue());
                    }
                    if (!value.isDefined()) continue;
                    try {
                        newStack.setValue(alias, value.get());
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
            } else {
                LOG.debug("invalid alias expression: {}", (Object)this.aliasesKey);
            }
        }
        return invocation.invoke();
    }

    protected boolean isAccepted(String paramName) {
        AcceptedPatternsChecker.IsAccepted result = this.acceptedPatterns.isAccepted(paramName);
        if (result.isAccepted()) {
            return true;
        }
        LOG.warn("Parameter [{}] didn't match accepted pattern [{}]! See Accepted / Excluded patterns at\nhttps://struts.apache.org/security/#accepted--excluded-patterns", (Object)paramName, (Object)result.getAcceptedPattern());
        return false;
    }

    protected boolean isExcluded(String paramName) {
        ExcludedPatternsChecker.IsExcluded result = this.excludedPatterns.isExcluded(paramName);
        if (!result.isExcluded()) {
            return false;
        }
        LOG.warn("Parameter [{}] matches excluded pattern [{}]! See Accepted / Excluded patterns at\nhttps://struts.apache.org/security/#accepted--excluded-patterns", (Object)paramName, (Object)result.getExcludedPattern());
        return true;
    }

    protected boolean isNotAcceptableExpression(String expression) {
        return this.isExcluded(expression) || !this.isAccepted(expression);
    }

    public void setAcceptParamNames(String commaDelim) {
        this.acceptedPatterns.setAcceptedPatterns(commaDelim);
    }

    public void setExcludeParams(String commaDelim) {
        this.excludedPatterns.setExcludedPatterns(commaDelim);
    }
}

