/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.id.enhanced;

import java.lang.reflect.Constructor;
import java.util.Properties;
import org.hibernate.id.enhanced.InitialValueAwareOptimizer;
import org.hibernate.id.enhanced.NoopOptimizer;
import org.hibernate.id.enhanced.Optimizer;
import org.hibernate.id.enhanced.StandardOptimizerDescriptor;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.jboss.logging.Logger;

public class OptimizerFactory {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)OptimizerFactory.class.getName());
    private static final Class[] CTOR_SIG = new Class[]{Class.class, Integer.TYPE};
    @Deprecated
    public static final String NONE = StandardOptimizerDescriptor.NONE.getExternalName();
    @Deprecated
    public static final String HILO = StandardOptimizerDescriptor.HILO.getExternalName();
    @Deprecated
    public static final String LEGACY_HILO = "legacy-hilo";
    @Deprecated
    public static final String POOL = "pooled";
    @Deprecated
    public static final String POOL_LO = "pooled-lo";

    public static boolean isPooledOptimizer(String optimizerName) {
        StandardOptimizerDescriptor standardDescriptor = StandardOptimizerDescriptor.fromExternalName(optimizerName);
        return standardDescriptor != null && standardDescriptor.isPooled();
    }

    @Deprecated
    public static Optimizer buildOptimizer(String type, Class returnClass, int incrementSize) {
        Class optimizerClass;
        StandardOptimizerDescriptor standardDescriptor = StandardOptimizerDescriptor.fromExternalName(type);
        if (standardDescriptor != null) {
            optimizerClass = standardDescriptor.getOptimizerClass();
        } else {
            try {
                optimizerClass = ReflectHelper.classForName(type);
            }
            catch (Throwable ignore) {
                LOG.unableToLocateCustomOptimizerClass(type);
                return OptimizerFactory.buildFallbackOptimizer(returnClass, incrementSize);
            }
        }
        try {
            Constructor ctor = optimizerClass.getConstructor(CTOR_SIG);
            return (Optimizer)ctor.newInstance(returnClass, incrementSize);
        }
        catch (Throwable ignore) {
            LOG.unableToInstantiateOptimizer(type);
            return OptimizerFactory.buildFallbackOptimizer(returnClass, incrementSize);
        }
    }

    private static Optimizer buildFallbackOptimizer(Class returnClass, int incrementSize) {
        return new NoopOptimizer(returnClass, incrementSize);
    }

    public static Optimizer buildOptimizer(String type, Class returnClass, int incrementSize, long explicitInitialValue) {
        Optimizer optimizer = OptimizerFactory.buildOptimizer(type, returnClass, incrementSize);
        if (InitialValueAwareOptimizer.class.isInstance(optimizer)) {
            ((InitialValueAwareOptimizer)((Object)optimizer)).injectInitialValue(explicitInitialValue);
        }
        return optimizer;
    }

    public static String determineImplicitOptimizerName(int incrementSize, Properties configSettings) {
        if (incrementSize <= 1) {
            return StandardOptimizerDescriptor.NONE.getExternalName();
        }
        String preferredPooledOptimizerStrategy = configSettings.getProperty("hibernate.id.optimizer.pooled.preferred");
        if (StringHelper.isNotEmpty(preferredPooledOptimizerStrategy)) {
            return preferredPooledOptimizerStrategy;
        }
        return ConfigurationHelper.getBoolean("hibernate.id.optimizer.pooled.prefer_lo", configSettings, false) ? StandardOptimizerDescriptor.POOLED_LO.getExternalName() : StandardOptimizerDescriptor.POOLED.getExternalName();
    }

    private OptimizerFactory() {
    }
}

