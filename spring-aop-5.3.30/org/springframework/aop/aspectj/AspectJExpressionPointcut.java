/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.aspectj.util.FuzzyBoolean
 *  org.aspectj.weaver.patterns.NamePattern
 *  org.aspectj.weaver.reflect.ReflectionWorld$ReflectionWorldException
 *  org.aspectj.weaver.reflect.ShadowMatchImpl
 *  org.aspectj.weaver.tools.ContextBasedMatcher
 *  org.aspectj.weaver.tools.FuzzyBoolean
 *  org.aspectj.weaver.tools.JoinPointMatch
 *  org.aspectj.weaver.tools.MatchingContext
 *  org.aspectj.weaver.tools.PointcutDesignatorHandler
 *  org.aspectj.weaver.tools.PointcutExpression
 *  org.aspectj.weaver.tools.PointcutParameter
 *  org.aspectj.weaver.tools.PointcutParser
 *  org.aspectj.weaver.tools.PointcutPrimitive
 *  org.aspectj.weaver.tools.ShadowMatch
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.BeanFactoryUtils
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils
 *  org.springframework.beans.factory.config.ConfigurableBeanFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.aop.aspectj;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.patterns.NamePattern;
import org.aspectj.weaver.reflect.ReflectionWorld;
import org.aspectj.weaver.reflect.ShadowMatchImpl;
import org.aspectj.weaver.tools.ContextBasedMatcher;
import org.aspectj.weaver.tools.JoinPointMatch;
import org.aspectj.weaver.tools.MatchingContext;
import org.aspectj.weaver.tools.PointcutDesignatorHandler;
import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParameter;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.PointcutPrimitive;
import org.aspectj.weaver.tools.ShadowMatch;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.IntroductionAwareMethodMatcher;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.aspectj.RuntimeTestWalker;
import org.springframework.aop.framework.autoproxy.ProxyCreationContext;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.aop.support.AbstractExpressionPointcut;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class AspectJExpressionPointcut
extends AbstractExpressionPointcut
implements ClassFilter,
IntroductionAwareMethodMatcher,
BeanFactoryAware {
    private static final Set<PointcutPrimitive> SUPPORTED_PRIMITIVES = new HashSet<PointcutPrimitive>();
    private static final Log logger;
    @Nullable
    private Class<?> pointcutDeclarationScope;
    private String[] pointcutParameterNames = new String[0];
    private Class<?>[] pointcutParameterTypes = new Class[0];
    @Nullable
    private BeanFactory beanFactory;
    @Nullable
    private transient ClassLoader pointcutClassLoader;
    @Nullable
    private transient PointcutExpression pointcutExpression;
    private transient Map<Method, ShadowMatch> shadowMatchCache = new ConcurrentHashMap<Method, ShadowMatch>(32);

    public AspectJExpressionPointcut() {
    }

    public AspectJExpressionPointcut(Class<?> declarationScope, String[] paramNames, Class<?>[] paramTypes) {
        this.pointcutDeclarationScope = declarationScope;
        if (paramNames.length != paramTypes.length) {
            throw new IllegalStateException("Number of pointcut parameter names must match number of pointcut parameter types");
        }
        this.pointcutParameterNames = paramNames;
        this.pointcutParameterTypes = paramTypes;
    }

    public void setPointcutDeclarationScope(Class<?> pointcutDeclarationScope) {
        this.pointcutDeclarationScope = pointcutDeclarationScope;
    }

    public void setParameterNames(String ... names) {
        this.pointcutParameterNames = names;
    }

    public void setParameterTypes(Class<?> ... types) {
        this.pointcutParameterTypes = types;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public ClassFilter getClassFilter() {
        this.obtainPointcutExpression();
        return this;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        this.obtainPointcutExpression();
        return this;
    }

    private PointcutExpression obtainPointcutExpression() {
        if (this.getExpression() == null) {
            throw new IllegalStateException("Must set property 'expression' before attempting to match");
        }
        if (this.pointcutExpression == null) {
            this.pointcutClassLoader = this.determinePointcutClassLoader();
            this.pointcutExpression = this.buildPointcutExpression(this.pointcutClassLoader);
        }
        return this.pointcutExpression;
    }

    @Nullable
    private ClassLoader determinePointcutClassLoader() {
        if (this.beanFactory instanceof ConfigurableBeanFactory) {
            return ((ConfigurableBeanFactory)this.beanFactory).getBeanClassLoader();
        }
        if (this.pointcutDeclarationScope != null) {
            return this.pointcutDeclarationScope.getClassLoader();
        }
        return ClassUtils.getDefaultClassLoader();
    }

    private PointcutExpression buildPointcutExpression(@Nullable ClassLoader classLoader) {
        PointcutParser parser = this.initializePointcutParser(classLoader);
        PointcutParameter[] pointcutParameters = new PointcutParameter[this.pointcutParameterNames.length];
        for (int i = 0; i < pointcutParameters.length; ++i) {
            pointcutParameters[i] = parser.createPointcutParameter(this.pointcutParameterNames[i], this.pointcutParameterTypes[i]);
        }
        return parser.parsePointcutExpression(this.replaceBooleanOperators(this.resolveExpression()), this.pointcutDeclarationScope, pointcutParameters);
    }

    private String resolveExpression() {
        String expression = this.getExpression();
        Assert.state((expression != null ? 1 : 0) != 0, (String)"No expression set");
        return expression;
    }

    private PointcutParser initializePointcutParser(@Nullable ClassLoader classLoader) {
        PointcutParser parser = PointcutParser.getPointcutParserSupportingSpecifiedPrimitivesAndUsingSpecifiedClassLoaderForResolution(SUPPORTED_PRIMITIVES, (ClassLoader)classLoader);
        parser.registerPointcutDesignatorHandler((PointcutDesignatorHandler)new BeanPointcutDesignatorHandler());
        return parser;
    }

    private String replaceBooleanOperators(String pcExpr) {
        String result = StringUtils.replace((String)pcExpr, (String)" and ", (String)" && ");
        result = StringUtils.replace((String)result, (String)" or ", (String)" || ");
        result = StringUtils.replace((String)result, (String)" not ", (String)" ! ");
        return result;
    }

    public PointcutExpression getPointcutExpression() {
        return this.obtainPointcutExpression();
    }

    @Override
    public boolean matches(Class<?> targetClass) {
        PointcutExpression pointcutExpression = this.obtainPointcutExpression();
        try {
            try {
                return pointcutExpression.couldMatchJoinPointsInType(targetClass);
            }
            catch (ReflectionWorld.ReflectionWorldException ex) {
                logger.debug((Object)"PointcutExpression matching rejected target class - trying fallback expression", (Throwable)ex);
                PointcutExpression fallbackExpression = this.getFallbackPointcutExpression(targetClass);
                if (fallbackExpression != null) {
                    return fallbackExpression.couldMatchJoinPointsInType(targetClass);
                }
            }
        }
        catch (Throwable ex) {
            logger.debug((Object)"PointcutExpression matching rejected target class", ex);
        }
        return false;
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass, boolean hasIntroductions) {
        this.obtainPointcutExpression();
        ShadowMatch shadowMatch = this.getTargetShadowMatch(method, targetClass);
        if (shadowMatch.alwaysMatches()) {
            return true;
        }
        if (shadowMatch.neverMatches()) {
            return false;
        }
        if (hasIntroductions) {
            return true;
        }
        RuntimeTestWalker walker = this.getRuntimeTestWalker(shadowMatch);
        return !walker.testsSubtypeSensitiveVars() || walker.testTargetInstanceOfResidue(targetClass);
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        return this.matches(method, targetClass, false);
    }

    @Override
    public boolean isRuntime() {
        return this.obtainPointcutExpression().mayNeedDynamicTest();
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass, Object ... args) {
        Object thisObject;
        Object targetObject;
        ProxyMethodInvocation pmi;
        ShadowMatch shadowMatch;
        block9: {
            this.obtainPointcutExpression();
            shadowMatch = this.getTargetShadowMatch(method, targetClass);
            pmi = null;
            targetObject = null;
            thisObject = null;
            try {
                MethodInvocation mi = ExposeInvocationInterceptor.currentInvocation();
                targetObject = mi.getThis();
                if (!(mi instanceof ProxyMethodInvocation)) {
                    throw new IllegalStateException("MethodInvocation is not a Spring ProxyMethodInvocation: " + mi);
                }
                pmi = (ProxyMethodInvocation)mi;
                thisObject = pmi.getProxy();
            }
            catch (IllegalStateException ex) {
                if (!logger.isDebugEnabled()) break block9;
                logger.debug((Object)("Could not access current invocation - matching with limited context: " + ex));
            }
        }
        try {
            JoinPointMatch joinPointMatch = shadowMatch.matchesJoinPoint(thisObject, targetObject, args);
            if (pmi != null && thisObject != null) {
                RuntimeTestWalker originalMethodResidueTest = this.getRuntimeTestWalker(this.getShadowMatch(method, method));
                if (!originalMethodResidueTest.testThisInstanceOfResidue(thisObject.getClass())) {
                    return false;
                }
                if (joinPointMatch.matches()) {
                    this.bindParameters(pmi, joinPointMatch);
                }
            }
            return joinPointMatch.matches();
        }
        catch (Throwable ex) {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)("Failed to evaluate join point for arguments " + Arrays.toString(args) + " - falling back to non-match"), ex);
            }
            return false;
        }
    }

    @Nullable
    protected String getCurrentProxiedBeanName() {
        return ProxyCreationContext.getCurrentProxiedBeanName();
    }

    @Nullable
    private PointcutExpression getFallbackPointcutExpression(Class<?> targetClass) {
        try {
            ClassLoader classLoader = targetClass.getClassLoader();
            if (classLoader != null && classLoader != this.pointcutClassLoader) {
                return this.buildPointcutExpression(classLoader);
            }
        }
        catch (Throwable ex) {
            logger.debug((Object)"Failed to create fallback PointcutExpression", ex);
        }
        return null;
    }

    private RuntimeTestWalker getRuntimeTestWalker(ShadowMatch shadowMatch) {
        if (shadowMatch instanceof DefensiveShadowMatch) {
            return new RuntimeTestWalker(((DefensiveShadowMatch)shadowMatch).primary);
        }
        return new RuntimeTestWalker(shadowMatch);
    }

    private void bindParameters(ProxyMethodInvocation invocation, JoinPointMatch jpm) {
        invocation.setUserAttribute(this.resolveExpression(), jpm);
    }

    private ShadowMatch getTargetShadowMatch(Method method, Class<?> targetClass) {
        Set ifcs;
        Method targetMethod = AopUtils.getMostSpecificMethod(method, targetClass);
        if (targetMethod.getDeclaringClass().isInterface() && (ifcs = ClassUtils.getAllInterfacesForClassAsSet(targetClass)).size() > 1) {
            try {
                Class compositeInterface = ClassUtils.createCompositeInterface((Class[])ClassUtils.toClassArray((Collection)ifcs), (ClassLoader)targetClass.getClassLoader());
                targetMethod = ClassUtils.getMostSpecificMethod((Method)targetMethod, (Class)compositeInterface);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        return this.getShadowMatch(targetMethod, method);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ShadowMatch getShadowMatch(Method targetMethod, Method originalMethod) {
        ShadowMatch shadowMatch = this.shadowMatchCache.get(targetMethod);
        if (shadowMatch == null) {
            Map<Method, ShadowMatch> map = this.shadowMatchCache;
            synchronized (map) {
                PointcutExpression fallbackExpression = null;
                shadowMatch = this.shadowMatchCache.get(targetMethod);
                if (shadowMatch == null) {
                    Method methodToMatch;
                    block20: {
                        methodToMatch = targetMethod;
                        try {
                            try {
                                shadowMatch = this.obtainPointcutExpression().matchesMethodExecution(methodToMatch);
                            }
                            catch (ReflectionWorld.ReflectionWorldException ex) {
                                try {
                                    fallbackExpression = this.getFallbackPointcutExpression(methodToMatch.getDeclaringClass());
                                    if (fallbackExpression != null) {
                                        shadowMatch = fallbackExpression.matchesMethodExecution(methodToMatch);
                                    }
                                }
                                catch (ReflectionWorld.ReflectionWorldException ex2) {
                                    fallbackExpression = null;
                                }
                            }
                            if (targetMethod == originalMethod || shadowMatch != null && (!shadowMatch.neverMatches() || !Proxy.isProxyClass(targetMethod.getDeclaringClass()))) break block20;
                            methodToMatch = originalMethod;
                            try {
                                shadowMatch = this.obtainPointcutExpression().matchesMethodExecution(methodToMatch);
                            }
                            catch (ReflectionWorld.ReflectionWorldException ex) {
                                try {
                                    fallbackExpression = this.getFallbackPointcutExpression(methodToMatch.getDeclaringClass());
                                    if (fallbackExpression != null) {
                                        shadowMatch = fallbackExpression.matchesMethodExecution(methodToMatch);
                                    }
                                }
                                catch (ReflectionWorld.ReflectionWorldException ex2) {
                                    fallbackExpression = null;
                                }
                            }
                        }
                        catch (Throwable ex) {
                            logger.debug((Object)"PointcutExpression matching rejected target method", ex);
                            fallbackExpression = null;
                        }
                    }
                    if (shadowMatch == null) {
                        shadowMatch = new ShadowMatchImpl(FuzzyBoolean.NO, null, null, null);
                    } else if (shadowMatch.maybeMatches() && fallbackExpression != null) {
                        shadowMatch = new DefensiveShadowMatch(shadowMatch, fallbackExpression.matchesMethodExecution(methodToMatch));
                    }
                    this.shadowMatchCache.put(targetMethod, shadowMatch);
                }
            }
        }
        return shadowMatch;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AspectJExpressionPointcut)) {
            return false;
        }
        AspectJExpressionPointcut otherPc = (AspectJExpressionPointcut)other;
        return ObjectUtils.nullSafeEquals((Object)this.getExpression(), (Object)otherPc.getExpression()) && ObjectUtils.nullSafeEquals(this.pointcutDeclarationScope, otherPc.pointcutDeclarationScope) && ObjectUtils.nullSafeEquals((Object)this.pointcutParameterNames, (Object)otherPc.pointcutParameterNames) && ObjectUtils.nullSafeEquals(this.pointcutParameterTypes, otherPc.pointcutParameterTypes);
    }

    public int hashCode() {
        int hashCode = ObjectUtils.nullSafeHashCode((Object)this.getExpression());
        hashCode = 31 * hashCode + ObjectUtils.nullSafeHashCode(this.pointcutDeclarationScope);
        hashCode = 31 * hashCode + ObjectUtils.nullSafeHashCode((Object[])this.pointcutParameterNames);
        hashCode = 31 * hashCode + ObjectUtils.nullSafeHashCode((Object[])this.pointcutParameterTypes);
        return hashCode;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("AspectJExpressionPointcut: (");
        for (int i = 0; i < this.pointcutParameterTypes.length; ++i) {
            sb.append(this.pointcutParameterTypes[i].getName());
            sb.append(' ');
            sb.append(this.pointcutParameterNames[i]);
            if (i + 1 >= this.pointcutParameterTypes.length) continue;
            sb.append(", ");
        }
        sb.append(") ");
        if (this.getExpression() != null) {
            sb.append(this.getExpression());
        } else {
            sb.append("<pointcut expression not set>");
        }
        return sb.toString();
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.shadowMatchCache = new ConcurrentHashMap<Method, ShadowMatch>(32);
    }

    static {
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.EXECUTION);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.ARGS);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.REFERENCE);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.THIS);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.TARGET);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.WITHIN);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_ANNOTATION);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_WITHIN);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_ARGS);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_TARGET);
        logger = LogFactory.getLog(AspectJExpressionPointcut.class);
    }

    private static class DefensiveShadowMatch
    implements ShadowMatch {
        private final ShadowMatch primary;
        private final ShadowMatch other;

        public DefensiveShadowMatch(ShadowMatch primary, ShadowMatch other) {
            this.primary = primary;
            this.other = other;
        }

        public boolean alwaysMatches() {
            return this.primary.alwaysMatches();
        }

        public boolean maybeMatches() {
            return this.primary.maybeMatches();
        }

        public boolean neverMatches() {
            return this.primary.neverMatches();
        }

        public JoinPointMatch matchesJoinPoint(Object thisObject, Object targetObject, Object[] args) {
            try {
                return this.primary.matchesJoinPoint(thisObject, targetObject, args);
            }
            catch (ReflectionWorld.ReflectionWorldException ex) {
                return this.other.matchesJoinPoint(thisObject, targetObject, args);
            }
        }

        public void setMatchingContext(MatchingContext aMatchContext) {
            this.primary.setMatchingContext(aMatchContext);
            this.other.setMatchingContext(aMatchContext);
        }
    }

    private class BeanContextMatcher
    implements ContextBasedMatcher {
        private final NamePattern expressionPattern;

        public BeanContextMatcher(String expression) {
            this.expressionPattern = new NamePattern(expression);
        }

        @Deprecated
        public boolean couldMatchJoinPointsInType(Class someClass) {
            return this.contextMatch(someClass) == org.aspectj.weaver.tools.FuzzyBoolean.YES;
        }

        @Deprecated
        public boolean couldMatchJoinPointsInType(Class someClass, MatchingContext context) {
            return this.contextMatch(someClass) == org.aspectj.weaver.tools.FuzzyBoolean.YES;
        }

        public boolean matchesDynamically(MatchingContext context) {
            return true;
        }

        public org.aspectj.weaver.tools.FuzzyBoolean matchesStatically(MatchingContext context) {
            return this.contextMatch(null);
        }

        public boolean mayNeedDynamicTest() {
            return false;
        }

        private org.aspectj.weaver.tools.FuzzyBoolean contextMatch(@Nullable Class<?> targetType) {
            String advisedBeanName = AspectJExpressionPointcut.this.getCurrentProxiedBeanName();
            if (advisedBeanName == null) {
                return org.aspectj.weaver.tools.FuzzyBoolean.MAYBE;
            }
            if (BeanFactoryUtils.isGeneratedBeanName((String)advisedBeanName)) {
                return org.aspectj.weaver.tools.FuzzyBoolean.NO;
            }
            if (targetType != null) {
                boolean isFactory = FactoryBean.class.isAssignableFrom(targetType);
                return org.aspectj.weaver.tools.FuzzyBoolean.fromBoolean((boolean)this.matchesBean(isFactory ? "&" + advisedBeanName : advisedBeanName));
            }
            return org.aspectj.weaver.tools.FuzzyBoolean.fromBoolean((this.matchesBean(advisedBeanName) || this.matchesBean("&" + advisedBeanName) ? 1 : 0) != 0);
        }

        private boolean matchesBean(String advisedBeanName) {
            return BeanFactoryAnnotationUtils.isQualifierMatch(arg_0 -> ((NamePattern)this.expressionPattern).matches(arg_0), (String)advisedBeanName, (BeanFactory)AspectJExpressionPointcut.this.beanFactory);
        }
    }

    private class BeanPointcutDesignatorHandler
    implements PointcutDesignatorHandler {
        private static final String BEAN_DESIGNATOR_NAME = "bean";

        private BeanPointcutDesignatorHandler() {
        }

        public String getDesignatorName() {
            return BEAN_DESIGNATOR_NAME;
        }

        public ContextBasedMatcher parse(String expression) {
            return new BeanContextMatcher(expression);
        }
    }
}

