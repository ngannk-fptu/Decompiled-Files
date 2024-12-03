/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ognl.ClassResolver
 *  ognl.MemberAccess
 *  ognl.MethodFailedException
 *  ognl.NoSuchPropertyException
 *  ognl.Ognl
 *  ognl.OgnlContext
 *  ognl.OgnlException
 *  ognl.TypeConverter
 *  org.apache.commons.lang3.BooleanUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.ognl;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.ErrorMessageBuilder;
import com.opensymphony.xwork2.ognl.OgnlTypeConverterWrapper;
import com.opensymphony.xwork2.ognl.OgnlUtil;
import com.opensymphony.xwork2.ognl.SecurityMemberAccess;
import com.opensymphony.xwork2.ognl.accessor.CompoundRootAccessor;
import com.opensymphony.xwork2.ognl.accessor.RootAccessor;
import com.opensymphony.xwork2.util.ClearableValueStack;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.MemberAccessValueStack;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import ognl.ClassResolver;
import ognl.MemberAccess;
import ognl.MethodFailedException;
import ognl.NoSuchPropertyException;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.TypeConverter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsException;

public class OgnlValueStack
implements Serializable,
ValueStack,
ClearableValueStack,
MemberAccessValueStack {
    public static final String THROW_EXCEPTION_ON_FAILURE = OgnlValueStack.class.getName() + ".throwExceptionOnFailure";
    private static final Logger LOG = LogManager.getLogger(OgnlValueStack.class);
    private static final long serialVersionUID = 370737852934925530L;
    private static final String MAP_IDENTIFIER_KEY = "com.opensymphony.xwork2.util.OgnlValueStack.MAP_IDENTIFIER_KEY";
    protected CompoundRoot root;
    protected transient Map<String, Object> context;
    protected Class defaultType;
    protected Map<Object, Object> overrides;
    protected transient OgnlUtil ognlUtil;
    protected transient SecurityMemberAccess securityMemberAccess;
    private transient XWorkConverter converter;
    private boolean devMode;
    private boolean logMissingProperties;
    private boolean shouldFallbackToContext = true;

    protected OgnlValueStack(ValueStack vs, XWorkConverter xworkConverter, RootAccessor accessor, TextProvider prov, SecurityMemberAccess securityMemberAccess) {
        this.setRoot(xworkConverter, accessor, vs != null ? new CompoundRoot(vs.getRoot()) : new CompoundRoot(), securityMemberAccess);
        if (prov != null) {
            this.push(prov);
        }
    }

    protected OgnlValueStack(XWorkConverter xworkConverter, RootAccessor accessor, TextProvider prov, SecurityMemberAccess securityMemberAccess) {
        this(null, xworkConverter, accessor, prov, securityMemberAccess);
    }

    protected OgnlValueStack(ValueStack vs, XWorkConverter xworkConverter, RootAccessor accessor, SecurityMemberAccess securityMemberAccess) {
        this(vs, xworkConverter, accessor, null, securityMemberAccess);
    }

    @Deprecated
    protected OgnlValueStack(ValueStack vs, XWorkConverter xworkConverter, CompoundRootAccessor accessor, TextProvider prov, boolean allowStaticFieldAccess) {
        this(vs, xworkConverter, (RootAccessor)accessor, prov, new SecurityMemberAccess(allowStaticFieldAccess));
    }

    @Deprecated
    protected OgnlValueStack(XWorkConverter xworkConverter, CompoundRootAccessor accessor, TextProvider prov, boolean allowStaticFieldAccess) {
        this(xworkConverter, (RootAccessor)accessor, prov, new SecurityMemberAccess(allowStaticFieldAccess));
    }

    @Deprecated
    protected OgnlValueStack(ValueStack vs, XWorkConverter xworkConverter, CompoundRootAccessor accessor, boolean allowStaticFieldAccess) {
        this(vs, xworkConverter, (RootAccessor)accessor, new SecurityMemberAccess(allowStaticFieldAccess));
    }

    @Inject
    protected void setOgnlUtil(OgnlUtil ognlUtil) {
        this.ognlUtil = ognlUtil;
    }

    protected void setRoot(XWorkConverter xworkConverter, RootAccessor accessor, CompoundRoot compoundRoot, SecurityMemberAccess securityMemberAccess) {
        this.root = compoundRoot;
        this.securityMemberAccess = securityMemberAccess;
        this.context = Ognl.createDefaultContext((Object)this.root, (MemberAccess)securityMemberAccess, (ClassResolver)accessor, (TypeConverter)new OgnlTypeConverterWrapper(xworkConverter));
        this.converter = xworkConverter;
        this.context.put("com.opensymphony.xwork2.util.ValueStack.ValueStack", this);
        ((OgnlContext)this.context).setTraceEvaluations(false);
        ((OgnlContext)this.context).setKeepLastEvaluation(false);
    }

    @Deprecated
    protected void setRoot(XWorkConverter xworkConverter, CompoundRootAccessor accessor, CompoundRoot compoundRoot, boolean allowStaticFieldAccess) {
        this.setRoot(xworkConverter, (RootAccessor)accessor, compoundRoot, new SecurityMemberAccess(allowStaticFieldAccess));
    }

    @Inject(value="struts.devMode")
    protected void setDevMode(String mode) {
        this.devMode = BooleanUtils.toBoolean((String)mode);
    }

    @Inject(value="struts.ognl.logMissingProperties", required=false)
    protected void setLogMissingProperties(String logMissingProperties) {
        this.logMissingProperties = BooleanUtils.toBoolean((String)logMissingProperties);
    }

    @Inject(value="struts.ognl.valueStackFallbackToContext", required=false)
    protected void setShouldFallbackToContext(String shouldFallbackToContext) {
        this.shouldFallbackToContext = BooleanUtils.toBoolean((String)shouldFallbackToContext);
    }

    @Override
    public Map<String, Object> getContext() {
        return this.context;
    }

    @Override
    public ActionContext getActionContext() {
        return ActionContext.of(this.context);
    }

    @Override
    public void setDefaultType(Class defaultType) {
        this.defaultType = defaultType;
    }

    @Override
    public void setExprOverrides(Map<Object, Object> overrides) {
        if (this.overrides == null) {
            this.overrides = overrides;
        } else {
            this.overrides.putAll(overrides);
        }
    }

    @Override
    public Map<Object, Object> getExprOverrides() {
        return this.overrides;
    }

    @Override
    public CompoundRoot getRoot() {
        return this.root;
    }

    @Override
    public void setParameter(String expr, Object value) {
        this.setValue(expr, value, this.devMode);
    }

    @Override
    public void setValue(String expr, Object value) {
        this.setValue(expr, value, this.devMode);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setValue(String expr, Object value, boolean throwExceptionOnFailure) {
        Map<String, Object> context = this.getContext();
        try {
            this.trySetValue(expr, value, throwExceptionOnFailure, context);
        }
        catch (OgnlException e) {
            this.handleOgnlException(expr, value, throwExceptionOnFailure, e);
        }
        catch (RuntimeException re) {
            this.handleRuntimeException(expr, value, throwExceptionOnFailure, re);
        }
        finally {
            this.cleanUpContext(context);
        }
    }

    private void trySetValue(String expr, Object value, boolean throwExceptionOnFailure, Map<String, Object> context) throws OgnlException {
        context.put("conversion.property.fullName", expr);
        context.put("com.opensymphony.xwork2.util.ValueStack.ReportErrorsOnNoProp", throwExceptionOnFailure || this.logMissingProperties ? Boolean.TRUE : Boolean.FALSE);
        this.ognlUtil.setValue(expr, context, this.root, value);
    }

    private void cleanUpContext(Map<String, Object> context) {
        ReflectionContextState.clear(context);
        context.remove("conversion.property.fullName");
        context.remove("com.opensymphony.xwork2.util.ValueStack.ReportErrorsOnNoProp");
    }

    protected void handleRuntimeException(String expr, Object value, boolean throwExceptionOnFailure, RuntimeException re) {
        if (throwExceptionOnFailure) {
            String message = ErrorMessageBuilder.create().errorSettingExpressionWithValue(expr, value).build();
            throw new StrutsException(message, re);
        }
        LOG.warn("Error setting value [{}] with expression [{}]", value, (Object)expr, (Object)re);
    }

    protected void handleOgnlException(String expr, Object value, boolean throwExceptionOnFailure, OgnlException e) {
        if (e != null && e.getReason() instanceof SecurityException) {
            LOG.error("Could not evaluate this expression due to security constraints: [{}]", (Object)expr, (Object)e);
        }
        boolean shouldLog = this.shouldLogMissingPropertyWarning(e);
        String msg = null;
        if (throwExceptionOnFailure || shouldLog) {
            msg = ErrorMessageBuilder.create().errorSettingExpressionWithValue(expr, value).build();
        }
        if (shouldLog) {
            LOG.warn(msg, (Throwable)e);
        }
        if (throwExceptionOnFailure) {
            throw new StrutsException(msg, e);
        }
    }

    @Override
    public String findString(String expr) {
        return (String)this.findValue(expr, String.class);
    }

    @Override
    public String findString(String expr, boolean throwExceptionOnFailure) {
        return (String)this.findValue(expr, String.class, throwExceptionOnFailure);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object findValue(String expr, boolean throwExceptionOnFailure) {
        try {
            this.setupExceptionOnFailure(throwExceptionOnFailure);
            Object object = this.tryFindValueWhenExpressionIsNotNull(expr);
            return object;
        }
        catch (OgnlException e) {
            Object object = this.handleOgnlException(expr, throwExceptionOnFailure, e);
            return object;
        }
        catch (Exception e) {
            Object object = this.handleOtherException(expr, throwExceptionOnFailure, e);
            return object;
        }
        finally {
            ReflectionContextState.clear(this.context);
        }
    }

    protected void setupExceptionOnFailure(boolean throwExceptionOnFailure) {
        if (throwExceptionOnFailure || this.logMissingProperties) {
            this.context.put(THROW_EXCEPTION_ON_FAILURE, true);
        }
    }

    private Object tryFindValueWhenExpressionIsNotNull(String expr) throws OgnlException {
        if (expr == null) {
            return null;
        }
        return this.tryFindValue(expr);
    }

    protected Object handleOtherException(String expr, boolean throwExceptionOnFailure, Exception e) {
        this.logLookupFailure(expr, e);
        if (throwExceptionOnFailure) {
            throw new StrutsException(e);
        }
        return this.findInContext(expr);
    }

    private Object tryFindValue(String expr) throws OgnlException {
        return this.tryFindValue(expr, this.defaultType);
    }

    private String lookupForOverrides(String expr) {
        if (this.overrides != null && this.overrides.containsKey(expr)) {
            expr = (String)this.overrides.get(expr);
        }
        return expr;
    }

    @Override
    public Object findValue(String expr) {
        return this.findValue(expr, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object findValue(String expr, Class asType, boolean throwExceptionOnFailure) {
        try {
            this.setupExceptionOnFailure(throwExceptionOnFailure);
            Object object = this.tryFindValueWhenExpressionIsNotNull(expr, asType);
            return object;
        }
        catch (OgnlException e) {
            Object value = this.handleOgnlException(expr, throwExceptionOnFailure, e);
            Object object = this.converter.convertValue(this.getContext(), value, asType);
            return object;
        }
        catch (Exception e) {
            Object value = this.handleOtherException(expr, throwExceptionOnFailure, e);
            Object object = this.converter.convertValue(this.getContext(), value, asType);
            return object;
        }
        finally {
            ReflectionContextState.clear(this.context);
        }
    }

    private Object tryFindValueWhenExpressionIsNotNull(String expr, Class asType) throws OgnlException {
        if (expr == null) {
            return null;
        }
        return this.tryFindValue(expr, asType);
    }

    protected Object handleOgnlException(String expr, boolean throwExceptionOnFailure, OgnlException e) {
        Object ret = null;
        if (e != null && e.getReason() instanceof SecurityException) {
            LOG.error("Could not evaluate this expression due to security constraints: [{}]", (Object)expr, (Object)e);
        } else {
            ret = this.findInContext(expr);
        }
        if (ret == null) {
            if (this.shouldLogMissingPropertyWarning(e)) {
                LOG.warn("Could not find property [{}]!", (Object)expr, (Object)e);
            }
            if (throwExceptionOnFailure) {
                throw new StrutsException(e);
            }
        }
        return ret;
    }

    protected boolean shouldLogMissingPropertyWarning(OgnlException e) {
        return (e instanceof NoSuchPropertyException || e instanceof MethodFailedException && e.getReason() instanceof NoSuchMethodException) && this.logMissingProperties;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Object tryFindValue(String expr, Class asType) throws OgnlException {
        try {
            expr = this.lookupForOverrides(expr);
            Object value = this.ognlUtil.getValue(expr, this.context, this.root, asType);
            if (value == null && (value = this.findInContext(expr)) != null && asType != null) {
                value = this.converter.convertValue(this.getContext(), value, asType);
            }
            Object object = value;
            return object;
        }
        finally {
            this.context.remove(THROW_EXCEPTION_ON_FAILURE);
        }
    }

    protected Object findInContext(String name) {
        if (!this.shouldFallbackToContext) {
            return null;
        }
        return this.getContext().get(name);
    }

    @Override
    public Object findValue(String expr, Class asType) {
        return this.findValue(expr, asType, false);
    }

    private void logLookupFailure(String expr, Exception e) {
        if (this.devMode) {
            LOG.warn("Caught an exception while evaluating expression '{}' against value stack", (Object)expr, (Object)e);
            LOG.warn("NOTE: Previous warning message was issued due to devMode set to true.");
        } else {
            LOG.debug("Caught an exception while evaluating expression '{}' against value stack", (Object)expr, (Object)e);
        }
    }

    @Override
    public Object peek() {
        return this.root.peek();
    }

    @Override
    public Object pop() {
        return this.root.pop();
    }

    @Override
    public void push(Object o) {
        this.root.push(o);
    }

    @Override
    public void set(String key, Object o) {
        Map setMap = this.retrieveSetMap();
        setMap.put(key, o);
    }

    private Map retrieveSetMap() {
        HashMap<String, String> setMap;
        Object topObj = this.peek();
        if (this.shouldUseOldMap(topObj)) {
            setMap = (HashMap<String, String>)topObj;
        } else {
            setMap = new HashMap<String, String>();
            setMap.put(MAP_IDENTIFIER_KEY, "");
            this.push(setMap);
        }
        return setMap;
    }

    private boolean shouldUseOldMap(Object topObj) {
        return topObj instanceof Map && ((Map)topObj).get(MAP_IDENTIFIER_KEY) != null;
    }

    @Override
    public int size() {
        return this.root.size();
    }

    private Object readResolve() {
        ActionContext ac = ActionContext.getContext();
        Container cont = ac.getContainer();
        XWorkConverter xworkConverter = cont.getInstance(XWorkConverter.class);
        RootAccessor accessor = cont.getInstance(RootAccessor.class);
        TextProvider prov = cont.getInstance(TextProvider.class, "system");
        SecurityMemberAccess sma = cont.getInstance(SecurityMemberAccess.class);
        OgnlValueStack aStack = new OgnlValueStack(xworkConverter, accessor, prov, sma);
        aStack.setOgnlUtil(cont.getInstance(OgnlUtil.class));
        aStack.setRoot(xworkConverter, accessor, this.root, sma);
        return aStack;
    }

    @Override
    public void clearContextValues() {
        ((OgnlContext)this.context).getValues().clear();
    }

    @Override
    public void useAcceptProperties(Set<Pattern> acceptedProperties) {
        this.securityMemberAccess.useAcceptProperties(acceptedProperties);
    }

    @Override
    public void useExcludeProperties(Set<Pattern> excludeProperties) {
        this.securityMemberAccess.useExcludeProperties(excludeProperties);
    }

    @Deprecated
    protected void setXWorkConverter(XWorkConverter converter) {
    }
}

