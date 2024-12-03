/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ognl.ClassResolver
 *  ognl.MemberAccess
 *  ognl.Ognl
 *  ognl.OgnlContext
 *  ognl.OgnlException
 *  ognl.OgnlRuntime
 *  ognl.SimpleNode
 *  ognl.TypeConverter
 *  org.apache.commons.lang3.BooleanUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.ognl;

import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.BeanInfoCacheFactory;
import com.opensymphony.xwork2.ognl.DefaultOgnlBeanInfoCacheFactory;
import com.opensymphony.xwork2.ognl.DefaultOgnlExpressionCacheFactory;
import com.opensymphony.xwork2.ognl.ExpressionCacheFactory;
import com.opensymphony.xwork2.ognl.OgnlCache;
import com.opensymphony.xwork2.ognl.OgnlTypeConverterWrapper;
import com.opensymphony.xwork2.ognl.SecurityMemberAccess;
import com.opensymphony.xwork2.ognl.accessor.RootAccessor;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ConfigParseUtil;
import com.opensymphony.xwork2.util.reflection.ReflectionException;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import ognl.ClassResolver;
import ognl.MemberAccess;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.SimpleNode;
import ognl.TypeConverter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ognl.OgnlGuard;
import org.apache.struts2.ognl.StrutsOgnlGuard;

public class OgnlUtil {
    private static final Logger LOG = LogManager.getLogger(OgnlUtil.class);
    private final AtomicBoolean warnReported = new AtomicBoolean(false);
    private final OgnlCache<String, Object> expressionCache;
    private final OgnlCache<Class<?>, BeanInfo> beanInfoCache;
    private TypeConverter defaultConverter;
    private final OgnlGuard ognlGuard;
    private boolean devMode;
    private boolean enableExpressionCache = true;
    private boolean enableEvalExpression;
    private String devModeExcludedClasses = "";
    private String devModeExcludedPackageNamePatterns = "";
    private String devModeExcludedPackageNames = "";
    private String devModeExcludedPackageExemptClasses = "";
    private Container container;

    @Deprecated
    public OgnlUtil() {
        this(new DefaultOgnlExpressionCacheFactory<String, Object>(), new DefaultOgnlBeanInfoCacheFactory(), new StrutsOgnlGuard());
    }

    @Inject
    public OgnlUtil(@Inject ExpressionCacheFactory<String, Object> ognlExpressionCacheFactory, @Inject BeanInfoCacheFactory<Class<?>, BeanInfo> ognlBeanInfoCacheFactory, @Inject OgnlGuard ognlGuard) {
        this.expressionCache = Objects.requireNonNull(ognlExpressionCacheFactory).buildOgnlCache();
        this.beanInfoCache = Objects.requireNonNull(ognlBeanInfoCacheFactory).buildOgnlCache();
        this.ognlGuard = Objects.requireNonNull(ognlGuard);
    }

    @Inject
    protected void setXWorkConverter(XWorkConverter conv) {
        this.defaultConverter = new OgnlTypeConverterWrapper(conv);
    }

    @Inject(value="struts.devMode")
    protected void setDevMode(String mode) {
        this.devMode = BooleanUtils.toBoolean((String)mode);
    }

    @Inject(value="struts.ognl.enableExpressionCache", required=false)
    protected void setEnableExpressionCache(String cache) {
        this.enableExpressionCache = BooleanUtils.toBoolean((String)cache);
    }

    @Deprecated
    protected void setExpressionCacheMaxSize(String maxSize) {
        this.expressionCache.setEvictionLimit(Integer.parseInt(maxSize));
    }

    @Deprecated
    protected void setBeanInfoCacheMaxSize(String maxSize) {
        this.beanInfoCache.setEvictionLimit(Integer.parseInt(maxSize));
    }

    @Inject(value="struts.ognl.enableEvalExpression", required=false)
    protected void setEnableEvalExpression(String evalExpression) {
        this.enableEvalExpression = BooleanUtils.toBoolean((String)evalExpression);
        if (this.enableEvalExpression) {
            LOG.warn("Enabling OGNL expression evaluation may introduce security risks (see http://struts.apache.org/release/2.3.x/docs/s2-013.html for further details)");
        }
    }

    @Deprecated
    protected void setExcludedClasses(String commaDelimitedClasses) {
    }

    @Inject(value="struts.devMode.excludedClasses", required=false)
    protected void setDevModeExcludedClasses(String commaDelimitedClasses) {
        this.devModeExcludedClasses = commaDelimitedClasses;
    }

    @Deprecated
    protected void setExcludedPackageNamePatterns(String commaDelimitedPackagePatterns) {
    }

    @Inject(value="struts.devMode.excludedPackageNamePatterns", required=false)
    protected void setDevModeExcludedPackageNamePatterns(String commaDelimitedPackagePatterns) {
        this.devModeExcludedPackageNamePatterns = commaDelimitedPackagePatterns;
    }

    @Deprecated
    protected void setExcludedPackageNames(String commaDelimitedPackageNames) {
    }

    @Inject(value="struts.devMode.excludedPackageNames", required=false)
    protected void setDevModeExcludedPackageNames(String commaDelimitedPackageNames) {
        this.devModeExcludedPackageNames = commaDelimitedPackageNames;
    }

    @Deprecated
    public void setExcludedPackageExemptClasses(String commaDelimitedClasses) {
    }

    @Inject(value="struts.devMode.excludedPackageExemptClasses", required=false)
    public void setDevModeExcludedPackageExemptClasses(String commaDelimitedClasses) {
        this.devModeExcludedPackageExemptClasses = commaDelimitedClasses;
    }

    @Deprecated
    public Set<String> getExcludedClasses() {
        return ConfigParseUtil.toClassesSet(this.container.getInstance(String.class, "struts.excludedClasses"));
    }

    @Deprecated
    public Set<Pattern> getExcludedPackageNamePatterns() {
        return ConfigParseUtil.toNewPatternsSet(Collections.emptySet(), this.container.getInstance(String.class, "struts.excludedPackageNamePatterns"));
    }

    @Deprecated
    public Set<String> getExcludedPackageNames() {
        return ConfigParseUtil.toPackageNamesSet(this.container.getInstance(String.class, "struts.excludedPackageNames"));
    }

    @Deprecated
    public Set<String> getExcludedPackageExemptClasses() {
        return ConfigParseUtil.toClassesSet(this.container.getInstance(String.class, "struts.excludedPackageExemptClasses"));
    }

    @Inject
    protected void setContainer(Container container) {
        this.container = container;
    }

    @Deprecated
    protected void setAllowStaticFieldAccess(String allowStaticFieldAccess) {
    }

    @Deprecated
    protected void setDisallowProxyMemberAccess(String disallowProxyMemberAccess) {
    }

    @Deprecated
    protected void setDisallowDefaultPackageAccess(String disallowDefaultPackageAccess) {
    }

    @Inject(value="struts.ognl.expressionMaxLength", required=false)
    protected void applyExpressionMaxLength(String maxLength) {
        try {
            if (maxLength == null || maxLength.isEmpty()) {
                Ognl.applyExpressionMaxLength(null);
                LOG.warn("OGNL Expression Max Length disabled.");
            } else {
                Ognl.applyExpressionMaxLength((Integer)Integer.parseInt(maxLength));
                LOG.debug("OGNL Expression Max Length enabled with {}.", (Object)maxLength);
            }
        }
        catch (Exception ex) {
            LOG.error("Unable to set OGNL Expression Max Length {}.", (Object)maxLength);
            throw ex;
        }
    }

    @Deprecated
    public boolean isDisallowProxyMemberAccess() {
        return BooleanUtils.toBoolean((String)this.container.getInstance(String.class, "struts.disallowProxyMemberAccess"));
    }

    @Deprecated
    public boolean isDisallowDefaultPackageAccess() {
        return BooleanUtils.toBoolean((String)this.container.getInstance(String.class, "struts.disallowDefaultPackageAccess"));
    }

    public static void clearRuntimeCache() {
        OgnlRuntime.clearCache();
    }

    public void clearExpressionCache() {
        this.expressionCache.clear();
    }

    public int expressionCacheSize() {
        return this.expressionCache.size();
    }

    public void clearBeanInfoCache() {
        this.beanInfoCache.clear();
    }

    public int beanInfoCacheSize() {
        return this.beanInfoCache.size();
    }

    public void setProperties(Map<String, ?> props, Object o, Map<String, Object> context) {
        this.setProperties(props, o, context, false);
    }

    public void setProperties(Map<String, ?> props, Object o, Map<String, Object> context, boolean throwPropertyExceptions) throws ReflectionException {
        if (props == null) {
            return;
        }
        Object oldRoot = Ognl.getRoot(context);
        Ognl.setRoot(context, (Object)o);
        for (Map.Entry<String, ?> entry : props.entrySet()) {
            String expression = entry.getKey();
            this.internalSetProperty(expression, entry.getValue(), o, context, throwPropertyExceptions);
        }
        Ognl.setRoot(context, (Object)oldRoot);
    }

    public void setProperties(Map<String, ?> properties, Object o) {
        this.setProperties(properties, o, false);
    }

    public void setProperties(Map<String, ?> properties, Object o, boolean throwPropertyExceptions) {
        Map<String, Object> context = this.createDefaultContext(o);
        this.setProperties(properties, o, context, throwPropertyExceptions);
    }

    public void setProperty(String name, Object value, Object o, Map<String, Object> context) {
        this.setProperty(name, value, o, context, false);
    }

    public void setProperty(String name, Object value, Object o, Map<String, Object> context, boolean throwPropertyExceptions) {
        Object oldRoot = Ognl.getRoot(context);
        Ognl.setRoot(context, (Object)o);
        this.internalSetProperty(name, value, o, context, throwPropertyExceptions);
        Ognl.setRoot(context, (Object)oldRoot);
    }

    public Object getRealTarget(String property, Map<String, Object> context, Object root) throws OgnlException {
        if ("top".equals(property)) {
            return root;
        }
        if (root instanceof CompoundRoot) {
            CompoundRoot cr = (CompoundRoot)root;
            try {
                for (Object target : cr) {
                    if (!OgnlRuntime.hasSetProperty((OgnlContext)((OgnlContext)context), target, (Object)property) && !OgnlRuntime.hasGetProperty((OgnlContext)((OgnlContext)context), target, (Object)property) && OgnlRuntime.getIndexedPropertyType((OgnlContext)((OgnlContext)context), target.getClass(), (String)property) == OgnlRuntime.INDEXED_PROPERTY_NONE) continue;
                    return target;
                }
            }
            catch (IntrospectionException ex) {
                throw new ReflectionException("Cannot figure out real target class", ex);
            }
            return null;
        }
        return root;
    }

    public void setValue(String name, Map<String, Object> context, Object root, Object value) throws OgnlException {
        this.ognlSet(name, context, root, value, context, this::checkEvalExpression, this::checkArithmeticExpression);
    }

    private boolean isEvalExpression(Object tree, Map<String, Object> context) throws OgnlException {
        if (tree instanceof SimpleNode) {
            SimpleNode node = (SimpleNode)tree;
            OgnlContext ognlContext = null;
            if (context instanceof OgnlContext) {
                ognlContext = (OgnlContext)context;
            }
            return node.isEvalChain(ognlContext) || node.isSequence(ognlContext);
        }
        return false;
    }

    private boolean isArithmeticExpression(Object tree, Map<String, Object> context) throws OgnlException {
        if (tree instanceof SimpleNode) {
            SimpleNode node = (SimpleNode)tree;
            OgnlContext ognlContext = null;
            if (context instanceof OgnlContext) {
                ognlContext = (OgnlContext)context;
            }
            return node.isOperation(ognlContext);
        }
        return false;
    }

    private boolean isSimpleMethod(Object tree, Map<String, Object> context) throws OgnlException {
        if (tree instanceof SimpleNode) {
            SimpleNode node = (SimpleNode)tree;
            OgnlContext ognlContext = null;
            if (context instanceof OgnlContext) {
                ognlContext = (OgnlContext)context;
            }
            return node.isSimpleMethod(ognlContext) && !node.isChain(ognlContext);
        }
        return false;
    }

    public Object getValue(String name, Map<String, Object> context, Object root) throws OgnlException {
        return this.getValue(name, context, root, null);
    }

    public Object callMethod(String name, Map<String, Object> context, Object root) throws OgnlException {
        return this.ognlGet(name, context, root, null, context, this::checkSimpleMethod);
    }

    public Object getValue(String name, Map<String, Object> context, Object root, Class<?> resultType) throws OgnlException {
        return this.ognlGet(name, context, root, resultType, context, this::checkEnableEvalExpression);
    }

    public Object compile(String expression) throws OgnlException {
        return this.compile(expression, null);
    }

    private void ognlSet(String expr, Map<String, Object> context, Object root, Object value, Map<String, Object> checkContext, TreeValidator ... treeValidators) throws OgnlException {
        Object tree = this.toTree(expr);
        for (TreeValidator validator : treeValidators) {
            validator.validate(tree, checkContext);
        }
        Ognl.setValue((Object)tree, context, (Object)root, (Object)value);
    }

    private <T> T ognlGet(String expr, Map<String, Object> context, Object root, Class<T> resultType, Map<String, Object> checkContext, TreeValidator ... treeValidators) throws OgnlException {
        Object tree = this.toTree(expr);
        for (TreeValidator validator : treeValidators) {
            validator.validate(tree, checkContext);
        }
        return (T)Ognl.getValue((Object)tree, context, (Object)root, resultType);
    }

    private Object toTree(String expr) throws OgnlException {
        Object tree = null;
        if (this.enableExpressionCache) {
            tree = this.expressionCache.get(expr);
        }
        if (tree == null) {
            tree = this.ognlGuard.parseExpression(expr);
            if (this.enableExpressionCache) {
                this.expressionCache.put(expr, tree);
            }
        }
        if ("_ognl_guard_blocked".equals(tree)) {
            throw new OgnlException("Expression blocked by OgnlGuard: " + expr);
        }
        return tree;
    }

    public Object compile(String expression, Map<String, Object> context) throws OgnlException {
        Object tree = this.toTree(expression);
        this.checkEnableEvalExpression(tree, context);
        return tree;
    }

    private void checkEnableEvalExpression(Object tree, Map<String, Object> context) throws OgnlException {
        if (!this.enableEvalExpression && this.isEvalExpression(tree, context)) {
            throw new OgnlException("Eval expressions/chained expressions have been disabled!");
        }
    }

    private void checkSimpleMethod(Object tree, Map<String, Object> context) throws OgnlException {
        if (!this.isSimpleMethod(tree, context)) {
            throw new OgnlException("It isn't a simple method which can be called!");
        }
    }

    private void checkEvalExpression(Object tree, Map<String, Object> context) throws OgnlException {
        if (this.isEvalExpression(tree, context)) {
            throw new OgnlException("Eval expression/chained expressions cannot be used as parameter name");
        }
    }

    private void checkArithmeticExpression(Object tree, Map<String, Object> context) throws OgnlException {
        if (this.isArithmeticExpression(tree, context)) {
            throw new OgnlException("Arithmetic expressions cannot be used as parameter name");
        }
    }

    public void copy(Object from, Object to, Map<String, Object> context, Collection<String> exclusions, Collection<String> inclusions) {
        this.copy(from, to, context, exclusions, inclusions, null);
    }

    public void copy(Object from, Object to, Map<String, Object> context, Collection<String> exclusions, Collection<String> inclusions, Class<?> editable) {
        PropertyDescriptor[] toPds;
        PropertyDescriptor[] fromPds;
        if (from == null || to == null) {
            LOG.warn("Skipping attempt to copy from, or to, a null source.", (Throwable)new RuntimeException());
            return;
        }
        Map<String, Object> contextFrom = this.createDefaultContext(from);
        Map<String, Object> contextTo = this.createDefaultContext(to);
        try {
            fromPds = this.getPropertyDescriptors(from);
            toPds = editable != null ? this.getPropertyDescriptors(editable) : this.getPropertyDescriptors(to);
        }
        catch (IntrospectionException e) {
            LOG.error("An error occurred", (Throwable)e);
            return;
        }
        HashMap<String, PropertyDescriptor> toPdHash = new HashMap<String, PropertyDescriptor>();
        for (PropertyDescriptor toPd : toPds) {
            toPdHash.put(toPd.getName(), toPd);
        }
        for (PropertyDescriptor fromPd : fromPds) {
            PropertyDescriptor toPd;
            if (fromPd.getReadMethod() == null || exclusions != null && exclusions.contains(fromPd.getName()) || inclusions != null && !inclusions.contains(fromPd.getName()) || (toPd = (PropertyDescriptor)toPdHash.get(fromPd.getName())) == null || toPd.getWriteMethod() == null) continue;
            try {
                Object value = this.ognlGet(fromPd.getName(), contextFrom, from, null, context, this::checkEnableEvalExpression);
                this.ognlSet(fromPd.getName(), contextTo, to, value, context, new TreeValidator[0]);
            }
            catch (OgnlException e) {
                LOG.debug("Got OGNL exception", (Throwable)e);
            }
        }
    }

    public void copy(Object from, Object to, Map<String, Object> context) {
        this.copy(from, to, context, null, null);
    }

    public PropertyDescriptor[] getPropertyDescriptors(Object source) throws IntrospectionException {
        BeanInfo beanInfo = this.getBeanInfo(source);
        return beanInfo.getPropertyDescriptors();
    }

    public PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) throws IntrospectionException {
        BeanInfo beanInfo = this.getBeanInfo(clazz);
        return beanInfo.getPropertyDescriptors();
    }

    public Map<String, Object> getBeanMap(Object source) throws IntrospectionException, OgnlException {
        PropertyDescriptor[] propertyDescriptors;
        HashMap<String, Object> beanMap = new HashMap<String, Object>();
        Map<String, Object> sourceMap = this.createDefaultContext(source);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors = this.getPropertyDescriptors(source)) {
            String propertyName = propertyDescriptor.getDisplayName();
            Method readMethod = propertyDescriptor.getReadMethod();
            if (readMethod != null) {
                Object value = this.ognlGet(propertyName, sourceMap, source, null, null, this::checkEnableEvalExpression);
                beanMap.put(propertyName, value);
                continue;
            }
            beanMap.put(propertyName, "There is no read method for " + propertyName);
        }
        return beanMap;
    }

    public BeanInfo getBeanInfo(Object from) throws IntrospectionException {
        return this.getBeanInfo(from.getClass());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public BeanInfo getBeanInfo(Class<?> clazz) throws IntrospectionException {
        OgnlCache<Class<?>, BeanInfo> ognlCache = this.beanInfoCache;
        synchronized (ognlCache) {
            BeanInfo beanInfo = this.beanInfoCache.get(clazz);
            if (beanInfo == null) {
                beanInfo = Introspector.getBeanInfo(clazz, Object.class);
                this.beanInfoCache.putIfAbsent(clazz, beanInfo);
            }
            return beanInfo;
        }
    }

    void internalSetProperty(String name, Object value, Object o, Map<String, Object> context, boolean throwPropertyExceptions) throws ReflectionException {
        block4: {
            try {
                this.setValue(name, context, o, value);
            }
            catch (OgnlException e) {
                Throwable exception;
                Throwable reason = e.getReason();
                if (reason instanceof SecurityException) {
                    LOG.error("Could not evaluate this expression due to security constraints: [{}]", (Object)name, (Object)e);
                }
                String msg = "Caught OgnlException while setting property '" + name + "' on type '" + o.getClass().getName() + "'.";
                Throwable throwable = exception = reason == null ? e : reason;
                if (throwPropertyExceptions) {
                    throw new ReflectionException(msg, exception);
                }
                if (!this.devMode) break block4;
                LOG.warn(msg, exception);
            }
        }
    }

    protected Map<String, Object> createDefaultContext(Object root) {
        return this.createDefaultContext(root, null);
    }

    protected Map<String, Object> createDefaultContext(Object root, ClassResolver resolver) {
        if (resolver == null && (resolver = (ClassResolver)this.container.getInstance(RootAccessor.class)) == null) {
            throw new IllegalStateException("Cannot find ClassResolver");
        }
        SecurityMemberAccess memberAccess = this.container.getInstance(SecurityMemberAccess.class);
        memberAccess.useEnforceAllowlistEnabled(Boolean.FALSE.toString());
        if (this.devMode) {
            if (!this.warnReported.get()) {
                this.warnReported.set(true);
                LOG.warn("Working in devMode, using devMode excluded classes and packages!");
            }
            memberAccess.useExcludedClasses(this.devModeExcludedClasses);
            memberAccess.useExcludedPackageNamePatterns(this.devModeExcludedPackageNamePatterns);
            memberAccess.useExcludedPackageNames(this.devModeExcludedPackageNames);
            memberAccess.useExcludedPackageExemptClasses(this.devModeExcludedPackageExemptClasses);
        }
        return Ognl.createDefaultContext((Object)root, (MemberAccess)memberAccess, (ClassResolver)resolver, (TypeConverter)this.defaultConverter);
    }

    @FunctionalInterface
    private static interface TreeValidator {
        public void validate(Object var1, Map<String, Object> var2) throws OgnlException;
    }
}

