/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.config;

import com.hazelcast.cache.impl.merge.policy.CacheMergePolicyProvider;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.ReplicatedMapConfig;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.logging.ILogger;
import com.hazelcast.map.merge.MergePolicyProvider;
import com.hazelcast.spi.merge.MergingExpirationTime;
import com.hazelcast.spi.merge.MergingLastStoredTime;
import com.hazelcast.spi.merge.MergingValue;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergePolicyProvider;
import com.hazelcast.spi.merge.SplitBrainMergeTypeProvider;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

public final class MergePolicyValidator {
    private MergePolicyValidator() {
    }

    public static boolean checkMergePolicySupportsInMemoryFormat(String name, Object mergePolicy, InMemoryFormat inMemoryFormat, boolean failFast, ILogger logger) {
        if (inMemoryFormat != InMemoryFormat.NATIVE) {
            return true;
        }
        if (mergePolicy instanceof SplitBrainMergePolicy) {
            return true;
        }
        if (failFast) {
            throw new InvalidConfigurationException(MergePolicyValidator.createSplitRecoveryWarningMsg(name, mergePolicy.getClass().getName()));
        }
        logger.warning(MergePolicyValidator.createSplitRecoveryWarningMsg(name, mergePolicy.getClass().getName()));
        return false;
    }

    private static String createSplitRecoveryWarningMsg(String name, String mergePolicy) {
        String messageTemplate = "Split brain recovery is not supported for '%s', because it's using merge policy `%s` to merge `%s` data. To fix this, use an implementation of `%s` with a cluster version `%s` or later";
        return String.format(messageTemplate, new Object[]{name, mergePolicy, InMemoryFormat.NATIVE, SplitBrainMergePolicy.class.getName(), Versions.V3_10});
    }

    static void checkCacheMergePolicy(String mergePolicyClassname, SplitBrainMergeTypeProvider mergeTypeProvider, CacheMergePolicyProvider mergePolicyProvider) {
        if (mergePolicyProvider == null) {
            return;
        }
        Object mergePolicyInstance = MergePolicyValidator.getMergePolicyInstance(mergePolicyProvider, mergePolicyClassname);
        MergePolicyValidator.checkMergePolicy(mergeTypeProvider, mergePolicyInstance);
    }

    private static Object getMergePolicyInstance(CacheMergePolicyProvider mergePolicyProvider, String mergePolicyClassname) {
        try {
            return mergePolicyProvider.getMergePolicy(mergePolicyClassname);
        }
        catch (InvalidConfigurationException e) {
            throw new InvalidConfigurationException("Merge policy must be an instance of SplitBrainMergePolicy or CacheMergePolicy, but was " + mergePolicyClassname, e.getCause());
        }
    }

    static void checkReplicatedMapMergePolicy(ReplicatedMapConfig replicatedMapConfig, com.hazelcast.replicatedmap.merge.MergePolicyProvider mergePolicyProvider) {
        String mergePolicyClassName = replicatedMapConfig.getMergePolicyConfig().getPolicy();
        Object mergePolicyInstance = MergePolicyValidator.getMergePolicyInstance(mergePolicyProvider, mergePolicyClassName);
        MergePolicyValidator.checkMergePolicy(replicatedMapConfig, mergePolicyInstance);
    }

    private static Object getMergePolicyInstance(com.hazelcast.replicatedmap.merge.MergePolicyProvider mergePolicyProvider, String mergePolicyClassName) {
        try {
            return mergePolicyProvider.getMergePolicy(mergePolicyClassName);
        }
        catch (InvalidConfigurationException e) {
            throw new InvalidConfigurationException("Merge policy must be an instance of SplitBrainMergePolicy or ReplicatedMapMergePolicy, but was " + mergePolicyClassName, e.getCause());
        }
    }

    static void checkMapMergePolicy(MapConfig mapConfig, MergePolicyProvider mergePolicyProvider) {
        String mergePolicyClassName = mapConfig.getMergePolicyConfig().getPolicy();
        Object mergePolicyInstance = MergePolicyValidator.getMergePolicyInstance(mergePolicyProvider, mergePolicyClassName);
        List<Class> requiredMergeTypes = MergePolicyValidator.checkMergePolicy(mapConfig, mergePolicyInstance);
        if (!mapConfig.isStatisticsEnabled() && requiredMergeTypes != null) {
            MergePolicyValidator.checkMapMergePolicyWhenStatisticsAreDisabled(mergePolicyClassName, requiredMergeTypes);
        }
    }

    private static Object getMergePolicyInstance(MergePolicyProvider mergePolicyProvider, String mergePolicyClassName) {
        try {
            return mergePolicyProvider.getMergePolicy(mergePolicyClassName);
        }
        catch (InvalidConfigurationException e) {
            throw new InvalidConfigurationException("Merge policy must be an instance of SplitBrainMergePolicy or MapMergePolicy, but was " + mergePolicyClassName, e.getCause());
        }
    }

    private static void checkMapMergePolicyWhenStatisticsAreDisabled(String mergePolicyClass, List<Class> requiredMergeTypes) {
        for (Class requiredMergeType : requiredMergeTypes) {
            if (!MergingLastStoredTime.class.isAssignableFrom(requiredMergeType) && !MergingExpirationTime.class.isAssignableFrom(requiredMergeType)) continue;
            throw new InvalidConfigurationException("The merge policy " + mergePolicyClass + " requires the merge type " + requiredMergeType.getName() + ", which is just provided if the map statistics are enabled.");
        }
    }

    static void checkMergePolicy(SplitBrainMergeTypeProvider mergeTypeProvider, SplitBrainMergePolicyProvider mergePolicyProvider, String mergePolicyClassName) {
        SplitBrainMergePolicy mergePolicy = MergePolicyValidator.getMergePolicyInstance(mergePolicyProvider, mergePolicyClassName);
        MergePolicyValidator.checkSplitBrainMergePolicy(mergeTypeProvider, mergePolicy);
    }

    private static SplitBrainMergePolicy getMergePolicyInstance(SplitBrainMergePolicyProvider mergePolicyProvider, String mergePolicyClassName) {
        try {
            return mergePolicyProvider.getMergePolicy(mergePolicyClassName);
        }
        catch (InvalidConfigurationException e) {
            throw new InvalidConfigurationException("Merge policy must be an instance of SplitBrainMergePolicy, but was " + mergePolicyClassName, e.getCause());
        }
    }

    private static List<Class> checkMergePolicy(SplitBrainMergeTypeProvider mergeTypeProvider, Object mergePolicyInstance) {
        if (mergePolicyInstance instanceof SplitBrainMergePolicy) {
            return MergePolicyValidator.checkSplitBrainMergePolicy(mergeTypeProvider, (SplitBrainMergePolicy)mergePolicyInstance);
        }
        return null;
    }

    private static List<Class> checkSplitBrainMergePolicy(SplitBrainMergeTypeProvider mergeTypeProvider, SplitBrainMergePolicy mergePolicyInstance) {
        ArrayList<Class> requiredMergeTypes = new ArrayList<Class>();
        Class providedMergeTypes = mergeTypeProvider.getProvidedMergeTypes();
        Class<?> mergePolicyClass = mergePolicyInstance.getClass();
        String mergePolicyClassName = mergePolicyClass.getName();
        do {
            MergePolicyValidator.checkSplitBrainMergePolicyGenerics(requiredMergeTypes, providedMergeTypes, mergePolicyClassName, mergePolicyClass);
        } while ((mergePolicyClass = mergePolicyClass.getSuperclass()) != null);
        return requiredMergeTypes;
    }

    private static void checkSplitBrainMergePolicyGenerics(List<Class> requiredMergeTypes, Class providedMergeTypes, String mergePolicyClassName, Class<?> mergePolicyClass) {
        for (TypeVariable<Class<?>> classTypeVariable : mergePolicyClass.getTypeParameters()) {
            for (Type requireMergeType : classTypeVariable.getBounds()) {
                MergePolicyValidator.checkRequiredMergeType(requiredMergeTypes, providedMergeTypes, mergePolicyClassName, requireMergeType);
            }
        }
        for (Type type : mergePolicyClass.getGenericInterfaces()) {
            MergePolicyValidator.checkRequiredGenericType(requiredMergeTypes, providedMergeTypes, mergePolicyClassName, type);
        }
        Type type = mergePolicyClass.getGenericSuperclass();
        MergePolicyValidator.checkRequiredGenericType(requiredMergeTypes, providedMergeTypes, mergePolicyClassName, type);
    }

    private static void checkRequiredGenericType(List<Class> requiredMergeTypes, Class providedMergeTypes, String mergePolicyClassName, Type requiredMergeType) {
        if (requiredMergeType instanceof ParameterizedType) {
            Type[] actualTypeArguments;
            for (Type requireMergeType : actualTypeArguments = ((ParameterizedType)requiredMergeType).getActualTypeArguments()) {
                MergePolicyValidator.checkRequiredMergeType(requiredMergeTypes, providedMergeTypes, mergePolicyClassName, requireMergeType);
            }
        }
    }

    private static void checkRequiredMergeType(List<Class> requiredMergeTypes, Class providedMergeTypes, String mergePolicyClassName, Type requireMergeType) {
        if (requireMergeType instanceof ParameterizedType) {
            Class requiredMergeType = (Class)((ParameterizedType)requireMergeType).getRawType();
            MergePolicyValidator.checkRequiredMergeTypeClass(requiredMergeTypes, providedMergeTypes, mergePolicyClassName, requiredMergeType);
        } else if (requireMergeType instanceof Class) {
            Class requiredMergeType = (Class)requireMergeType;
            MergePolicyValidator.checkRequiredMergeTypeClass(requiredMergeTypes, providedMergeTypes, mergePolicyClassName, requiredMergeType);
        }
    }

    private static void checkRequiredMergeTypeClass(List<Class> requiredMergeTypes, Class providedMergeTypes, String mergePolicyClassName, Class<?> requiredMergeTypeClass) {
        if (!MergingValue.class.isAssignableFrom(requiredMergeTypeClass)) {
            return;
        }
        if (!requiredMergeTypeClass.isAssignableFrom(providedMergeTypes)) {
            throw new InvalidConfigurationException("The merge policy " + mergePolicyClassName + " can just be configured on data structures which provide the merging type " + requiredMergeTypeClass.getName() + ". See SplitBrainMergeTypes for supported merging types.");
        }
        requiredMergeTypes.add(requiredMergeTypeClass);
    }
}

