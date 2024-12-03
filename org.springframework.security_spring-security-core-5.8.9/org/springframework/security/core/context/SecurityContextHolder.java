/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.security.core.context;

import java.lang.reflect.Constructor;
import java.util.function.Supplier;
import org.springframework.security.core.context.GlobalSecurityContextHolderStrategy;
import org.springframework.security.core.context.InheritableThreadLocalSecurityContextHolderStrategy;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.context.ThreadLocalSecurityContextHolderStrategy;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

public class SecurityContextHolder {
    public static final String MODE_THREADLOCAL = "MODE_THREADLOCAL";
    public static final String MODE_INHERITABLETHREADLOCAL = "MODE_INHERITABLETHREADLOCAL";
    public static final String MODE_GLOBAL = "MODE_GLOBAL";
    private static final String MODE_PRE_INITIALIZED = "MODE_PRE_INITIALIZED";
    public static final String SYSTEM_PROPERTY = "spring.security.strategy";
    private static String strategyName = System.getProperty("spring.security.strategy");
    private static SecurityContextHolderStrategy strategy;
    private static int initializeCount;

    private static void initialize() {
        SecurityContextHolder.initializeStrategy();
        ++initializeCount;
    }

    private static void initializeStrategy() {
        if (MODE_PRE_INITIALIZED.equals(strategyName)) {
            Assert.state((strategy != null ? 1 : 0) != 0, (String)"When using MODE_PRE_INITIALIZED, setContextHolderStrategy must be called with the fully constructed strategy");
            return;
        }
        if (!StringUtils.hasText((String)strategyName)) {
            strategyName = MODE_THREADLOCAL;
        }
        if (strategyName.equals(MODE_THREADLOCAL)) {
            strategy = new ThreadLocalSecurityContextHolderStrategy();
            return;
        }
        if (strategyName.equals(MODE_INHERITABLETHREADLOCAL)) {
            strategy = new InheritableThreadLocalSecurityContextHolderStrategy();
            return;
        }
        if (strategyName.equals(MODE_GLOBAL)) {
            strategy = new GlobalSecurityContextHolderStrategy();
            return;
        }
        try {
            Class<?> clazz = Class.forName(strategyName);
            Constructor<?> customStrategy = clazz.getConstructor(new Class[0]);
            strategy = (SecurityContextHolderStrategy)customStrategy.newInstance(new Object[0]);
        }
        catch (Exception ex) {
            ReflectionUtils.handleReflectionException((Exception)ex);
        }
    }

    public static void clearContext() {
        strategy.clearContext();
    }

    public static SecurityContext getContext() {
        return strategy.getContext();
    }

    public static Supplier<SecurityContext> getDeferredContext() {
        return strategy.getDeferredContext();
    }

    public static int getInitializeCount() {
        return initializeCount;
    }

    public static void setContext(SecurityContext context) {
        strategy.setContext(context);
    }

    public static void setDeferredContext(Supplier<SecurityContext> deferredContext) {
        strategy.setDeferredContext(deferredContext);
    }

    public static void setStrategyName(String strategyName) {
        SecurityContextHolder.strategyName = strategyName;
        SecurityContextHolder.initialize();
    }

    public static void setContextHolderStrategy(SecurityContextHolderStrategy strategy) {
        Assert.notNull((Object)strategy, (String)"securityContextHolderStrategy cannot be null");
        strategyName = MODE_PRE_INITIALIZED;
        SecurityContextHolder.strategy = strategy;
        SecurityContextHolder.initialize();
    }

    public static SecurityContextHolderStrategy getContextHolderStrategy() {
        return strategy;
    }

    public static SecurityContext createEmptyContext() {
        return strategy.createEmptyContext();
    }

    public String toString() {
        return "SecurityContextHolder[strategy='" + strategy.getClass().getSimpleName() + "'; initializeCount=" + initializeCount + "]";
    }

    static {
        initializeCount = 0;
        SecurityContextHolder.initialize();
    }
}

