/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.BooleanUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.interceptor.parameter;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import com.opensymphony.xwork2.security.AcceptedPatternsChecker;
import com.opensymphony.xwork2.security.ExcludedPatternsChecker;
import com.opensymphony.xwork2.util.ClearableValueStack;
import com.opensymphony.xwork2.util.MemberAccessValueStack;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.action.NoParameters;
import org.apache.struts2.action.ParameterNameAware;
import org.apache.struts2.action.ParameterValueAware;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.Parameter;

public class ParametersInterceptor
extends MethodFilterInterceptor {
    private static final Logger LOG = LogManager.getLogger(ParametersInterceptor.class);
    protected static final int PARAM_NAME_MAX_LENGTH = 100;
    private static final Pattern DMI_IGNORED_PATTERN = Pattern.compile("^(action|method):.*", 2);
    private int paramNameMaxLength = 100;
    private boolean devMode = false;
    private boolean dmiEnabled = false;
    protected boolean ordered = false;
    private ValueStackFactory valueStackFactory;
    private ExcludedPatternsChecker excludedPatterns;
    private AcceptedPatternsChecker acceptedPatterns;
    private Set<Pattern> excludedValuePatterns = null;
    private Set<Pattern> acceptedValuePatterns = null;
    static final Comparator<String> rbCollator = (s1, s2) -> {
        int l2;
        int l1 = ParametersInterceptor.countOGNLCharacters(s1);
        return l1 < (l2 = ParametersInterceptor.countOGNLCharacters(s2)) ? -1 : (l2 < l1 ? 1 : s1.compareTo((String)s2));
    };

    @Inject
    public void setValueStackFactory(ValueStackFactory valueStackFactory) {
        this.valueStackFactory = valueStackFactory;
    }

    @Inject(value="struts.devMode")
    public void setDevMode(String mode) {
        this.devMode = BooleanUtils.toBoolean((String)mode);
    }

    @Inject
    public void setExcludedPatterns(ExcludedPatternsChecker excludedPatterns) {
        this.excludedPatterns = excludedPatterns;
    }

    @Inject
    public void setAcceptedPatterns(AcceptedPatternsChecker acceptedPatterns) {
        this.acceptedPatterns = acceptedPatterns;
    }

    @Inject(value="struts.enable.DynamicMethodInvocation", required=false)
    protected void setDynamicMethodInvocation(String dmiEnabled) {
        this.dmiEnabled = Boolean.parseBoolean(dmiEnabled);
    }

    public void setParamNameMaxLength(int paramNameMaxLength) {
        this.paramNameMaxLength = paramNameMaxLength;
    }

    private static int countOGNLCharacters(String s) {
        int count = 0;
        for (int i = s.length() - 1; i >= 0; --i) {
            char c = s.charAt(i);
            if (c != '.' && c != '[') continue;
            ++count;
        }
        return count;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String doIntercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        if (!(action instanceof NoParameters)) {
            ActionContext ac = invocation.getInvocationContext();
            HttpParameters parameters = this.retrieveParameters(ac);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Setting params {}", (Object)StringUtils.normalizeSpace((String)this.getParameterLogMap(parameters)));
            }
            if (parameters != null) {
                Map<String, Object> contextMap = ac.getContextMap();
                try {
                    ReflectionContextState.setCreatingNullObjects(contextMap, true);
                    ReflectionContextState.setDenyMethodExecution(contextMap, true);
                    ReflectionContextState.setReportingConversionErrors(contextMap, true);
                    ValueStack stack = ac.getValueStack();
                    this.setParameters(action, stack, parameters);
                }
                finally {
                    ReflectionContextState.setCreatingNullObjects(contextMap, false);
                    ReflectionContextState.setDenyMethodExecution(contextMap, false);
                    ReflectionContextState.setReportingConversionErrors(contextMap, false);
                }
            }
        }
        return invocation.invoke();
    }

    protected HttpParameters retrieveParameters(ActionContext ac) {
        return ac.getParameters();
    }

    protected void addParametersToContext(ActionContext ac, Map<String, ?> newParams) {
    }

    protected void setParameters(Object action, ValueStack stack, HttpParameters parameters) {
        boolean memberAccessStack;
        TreeMap<Object, Parameter> acceptableParameters;
        HttpParameters params;
        if (this.ordered) {
            params = HttpParameters.create().withComparator(this.getOrderedComparator()).withParent(parameters).build();
            acceptableParameters = new TreeMap(this.getOrderedComparator());
        } else {
            params = HttpParameters.create().withParent(parameters).build();
            acceptableParameters = new TreeMap();
        }
        for (Map.Entry<String, Parameter> entry : params.entrySet()) {
            String parameterName = entry.getKey();
            boolean isAcceptableParameter = this.isAcceptableParameter(parameterName, action);
            if (!(isAcceptableParameter &= this.isAcceptableParameterValue(entry.getValue(), action))) continue;
            acceptableParameters.put(parameterName, entry.getValue());
        }
        ValueStack newStack = this.valueStackFactory.createValueStack(stack);
        boolean clearableStack = newStack instanceof ClearableValueStack;
        if (clearableStack) {
            ((ClearableValueStack)((Object)newStack)).clearContextValues();
            Map<String, Object> context = newStack.getContext();
            ReflectionContextState.setCreatingNullObjects(context, true);
            ReflectionContextState.setDenyMethodExecution(context, true);
            ReflectionContextState.setReportingConversionErrors(context, true);
            newStack.getActionContext().withLocale(stack.getActionContext().getLocale()).withValueStack(stack);
        }
        if (memberAccessStack = newStack instanceof MemberAccessValueStack) {
            MemberAccessValueStack accessValueStack = (MemberAccessValueStack)((Object)newStack);
            accessValueStack.useAcceptProperties(this.acceptedPatterns.getAcceptedPatterns());
            accessValueStack.useExcludeProperties(this.excludedPatterns.getExcludedPatterns());
        }
        for (Map.Entry entry : acceptableParameters.entrySet()) {
            String name = (String)entry.getKey();
            Parameter value = (Parameter)entry.getValue();
            try {
                newStack.setParameter(name, value.getObject());
            }
            catch (RuntimeException e) {
                if (!this.devMode) continue;
                this.notifyDeveloperParameterException(action, name, e.getMessage());
            }
        }
        if (clearableStack) {
            stack.getActionContext().withConversionErrors(newStack.getActionContext().getConversionErrors());
        }
        this.addParametersToContext(ActionContext.getContext(), acceptableParameters);
    }

    protected void notifyDeveloperParameterException(Object action, String property, String message) {
        String developerNotification = "Unexpected Exception caught setting '" + property + "' on '" + action.getClass() + ": " + message;
        if (action instanceof TextProvider) {
            TextProvider tp = (TextProvider)action;
            developerNotification = tp.getText("devmode.notification", "Developer Notification:\n{0}", new String[]{developerNotification});
        }
        LOG.error(developerNotification);
        if (action instanceof ValidationAware) {
            Collection<String> messages = ((ValidationAware)action).getActionMessages();
            messages.add(message);
            ((ValidationAware)action).setActionMessages(messages);
        }
    }

    protected boolean isAcceptableParameter(String name, Object action) {
        ParameterNameAware parameterNameAware = action instanceof ParameterNameAware ? (ParameterNameAware)action : null;
        return this.acceptableName(name) && (parameterNameAware == null || parameterNameAware.acceptableParameterName(name));
    }

    protected boolean isAcceptableParameterValue(Parameter param, Object action) {
        boolean acceptableParamValue;
        ParameterValueAware parameterValueAware = action instanceof ParameterValueAware ? (ParameterValueAware)action : null;
        boolean bl = acceptableParamValue = parameterValueAware == null || parameterValueAware.acceptableParameterValue(param.getValue());
        if (this.hasParamValuesToExclude() || this.hasParamValuesToAccept()) {
            acceptableParamValue &= this.acceptableValue(param.getName(), param.getValue());
        }
        return acceptableParamValue;
    }

    protected Comparator<String> getOrderedComparator() {
        return rbCollator;
    }

    protected String getParameterLogMap(HttpParameters parameters) {
        if (parameters == null) {
            return "NONE";
        }
        StringBuilder logEntry = new StringBuilder();
        for (Map.Entry<String, Parameter> entry : parameters.entrySet()) {
            logEntry.append(entry.getKey());
            logEntry.append(" => ");
            logEntry.append(entry.getValue().getValue());
            logEntry.append(" ");
        }
        return logEntry.toString();
    }

    protected boolean acceptableName(String name) {
        boolean accepted;
        if (this.isIgnoredDMI(name)) {
            LOG.trace("DMI is enabled, ignoring DMI method: {}", (Object)name);
            return false;
        }
        boolean bl = accepted = this.isWithinLengthLimit(name) && !this.isExcluded(name) && this.isAccepted(name);
        if (this.devMode && accepted) {
            LOG.debug("Parameter [{}] was accepted and will be appended to action!", (Object)name);
        }
        return accepted;
    }

    private boolean isIgnoredDMI(String name) {
        if (this.dmiEnabled) {
            return DMI_IGNORED_PATTERN.matcher(name).matches();
        }
        return false;
    }

    protected boolean acceptableValue(String name, String value) {
        boolean accepted;
        boolean bl = accepted = value == null || value.isEmpty() || !this.isParamValueExcluded(value) && this.isParamValueAccepted(value);
        if (!accepted) {
            String message = "Value [{}] of parameter [{}] was not accepted and will be dropped!";
            if (this.devMode) {
                LOG.warn(message, (Object)StringUtils.normalizeSpace((String)value), (Object)StringUtils.normalizeSpace((String)name));
            } else {
                LOG.debug(message, (Object)StringUtils.normalizeSpace((String)value), (Object)StringUtils.normalizeSpace((String)name));
            }
        }
        return accepted;
    }

    protected boolean isWithinLengthLimit(String name) {
        boolean matchLength;
        boolean bl = matchLength = name.length() <= this.paramNameMaxLength;
        if (!matchLength) {
            if (this.devMode) {
                LOG.warn("Parameter [{}] is too long, allowed length is [{}]. Use Interceptor Parameter Overriding to override the limit, see more at\nhttps://struts.apache.org/core-developers/interceptors.html#interceptor-parameter-overriding", (Object)name, (Object)this.paramNameMaxLength);
            } else {
                LOG.warn("Parameter [{}] is too long, allowed length is [{}]", (Object)name, (Object)this.paramNameMaxLength);
            }
        }
        return matchLength;
    }

    protected boolean isAccepted(String paramName) {
        AcceptedPatternsChecker.IsAccepted result = this.acceptedPatterns.isAccepted(paramName);
        if (result.isAccepted()) {
            return true;
        }
        if (this.devMode) {
            LOG.warn("Parameter [{}] didn't match accepted pattern [{}]! See Accepted / Excluded patterns at\nhttps://struts.apache.org/security/#accepted--excluded-patterns", (Object)paramName, (Object)result.getAcceptedPattern());
        } else {
            LOG.debug("Parameter [{}] didn't match accepted pattern [{}]!", (Object)paramName, (Object)result.getAcceptedPattern());
        }
        return false;
    }

    protected boolean isExcluded(String paramName) {
        ExcludedPatternsChecker.IsExcluded result = this.excludedPatterns.isExcluded(paramName);
        if (result.isExcluded()) {
            if (this.devMode) {
                LOG.warn("Parameter [{}] matches excluded pattern [{}]! See Accepted / Excluded patterns at\nhttps://struts.apache.org/security/#accepted--excluded-patterns", (Object)paramName, (Object)result.getExcludedPattern());
            } else {
                LOG.debug("Parameter [{}] matches excluded pattern [{}]!", (Object)paramName, (Object)result.getExcludedPattern());
            }
            return true;
        }
        return false;
    }

    protected boolean isParamValueExcluded(String value) {
        if (!this.hasParamValuesToExclude()) {
            LOG.debug("'excludedValuePatterns' not defined so anything is allowed");
            return false;
        }
        for (Pattern excludedValuePattern : this.excludedValuePatterns) {
            if (!excludedValuePattern.matcher(value).matches()) continue;
            if (this.devMode) {
                LOG.warn("Parameter value [{}] matches excluded pattern [{}]! See Accepting/Excluding parameter values at\nhttps://struts.apache.org/core-developers/parameters-interceptor#excluding-parameter-values", (Object)value, this.excludedValuePatterns);
            } else {
                LOG.debug("Parameter value [{}] matches excluded pattern [{}]", (Object)value, (Object)excludedValuePattern);
            }
            return true;
        }
        return false;
    }

    protected boolean isParamValueAccepted(String value) {
        if (!this.hasParamValuesToAccept()) {
            LOG.debug("'acceptedValuePatterns' not defined so anything is allowed");
            return true;
        }
        for (Pattern acceptedValuePattern : this.acceptedValuePatterns) {
            if (!acceptedValuePattern.matcher(value).matches()) continue;
            return true;
        }
        if (this.devMode) {
            LOG.warn("Parameter value [{}] didn't match accepted pattern [{}]! See Accepting/Excluding parameter values at\nhttps://struts.apache.org/core-developers/parameters-interceptor#excluding-parameter-values", (Object)value, this.acceptedValuePatterns);
        } else {
            LOG.debug("Parameter value [{}] was not accepted!", (Object)value);
        }
        return false;
    }

    private boolean hasParamValuesToExclude() {
        return this.excludedValuePatterns != null && this.excludedValuePatterns.size() > 0;
    }

    private boolean hasParamValuesToAccept() {
        return this.acceptedValuePatterns != null && this.acceptedValuePatterns.size() > 0;
    }

    public boolean isOrdered() {
        return this.ordered;
    }

    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }

    public void setAcceptParamNames(String commaDelim) {
        this.acceptedPatterns.setAcceptedPatterns(commaDelim);
    }

    public void setExcludeParams(String commaDelim) {
        this.excludedPatterns.setExcludedPatterns(commaDelim);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setAcceptedValuePatterns(String commaDelimitedPatterns) {
        Set<String> patterns = TextParseUtil.commaDelimitedStringToSet(commaDelimitedPatterns);
        if (this.acceptedValuePatterns == null) {
            LOG.debug("Sets accepted value patterns to [{}], note this may impact the safety of your application!", patterns);
        } else {
            LOG.warn("Replacing accepted patterns [{}] with [{}], be aware that this may impact safety of your application!", this.acceptedValuePatterns, patterns);
        }
        this.acceptedValuePatterns = new HashSet<Pattern>(patterns.size());
        try {
            for (String pattern : patterns) {
                this.acceptedValuePatterns.add(Pattern.compile(pattern, 2));
            }
        }
        finally {
            this.acceptedValuePatterns = Collections.unmodifiableSet(this.acceptedValuePatterns);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setExcludedValuePatterns(String commaDelimitedPatterns) {
        Set<String> patterns = TextParseUtil.commaDelimitedStringToSet(commaDelimitedPatterns);
        if (this.excludedValuePatterns == null) {
            LOG.debug("Setting excluded value patterns to [{}]", patterns);
        } else {
            LOG.warn("Replacing excluded value patterns [{}] with [{}], be aware that this may impact safety of your application!", this.excludedValuePatterns, patterns);
        }
        this.excludedValuePatterns = new HashSet<Pattern>(patterns.size());
        try {
            for (String pattern : patterns) {
                this.excludedValuePatterns.add(Pattern.compile(pattern, 2));
            }
        }
        finally {
            this.excludedValuePatterns = Collections.unmodifiableSet(this.excludedValuePatterns);
        }
    }
}

