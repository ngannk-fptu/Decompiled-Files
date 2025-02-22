/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.internal.util.JavaVersion;
import com.hazelcast.internal.util.JavaVm;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public final class ModularJavaUtils {
    private static final ILogger LOGGER = Logger.getLogger(ModularJavaUtils.class);

    private ModularJavaUtils() {
    }

    public static String getHazelcastModuleName() {
        if (!JavaVersion.isAtLeast(JavaVersion.JAVA_9)) {
            return null;
        }
        try {
            Method methodGetModule = Class.class.getMethod("getModule", new Class[0]);
            Class<?> classModule = Class.forName("java.lang.Module");
            Method methodGetName = classModule.getMethod("getName", new Class[0]);
            Object moduleHazelcast = methodGetModule.invoke(Hazelcast.class, new Object[0]);
            return (String)methodGetName.invoke(moduleHazelcast, new Object[0]);
        }
        catch (Exception e) {
            LOGGER.finest("Getting Hazelcast module name failed", e);
            return null;
        }
    }

    public static void checkJavaInternalAccess(ILogger logger) {
        if (logger == null || !JavaVersion.isAtLeast(JavaVersion.JAVA_9)) {
            return;
        }
        TreeMap<String, PackageAccessRequirement[]> requirements = new TreeMap<String, PackageAccessRequirement[]>();
        requirements.put("java.base", new PackageAccessRequirement[]{PackageAccessRequirement.createRequirement(false, "jdk.internal.ref"), PackageAccessRequirement.createRequirement(true, "java.lang"), PackageAccessRequirement.createRequirement(true, "java.nio"), PackageAccessRequirement.createRequirement(true, "sun.nio.ch")});
        requirements.put("jdk.management", ModularJavaUtils.getJdkManagementRequirements());
        requirements.put("java.management", new PackageAccessRequirement[]{PackageAccessRequirement.createRequirement(true, "sun.management")});
        ModularJavaUtils.checkPackageRequirements(logger, requirements);
    }

    private static PackageAccessRequirement[] getJdkManagementRequirements() {
        if (JavaVm.CURRENT_VM == JavaVm.OPENJ9) {
            return new PackageAccessRequirement[]{PackageAccessRequirement.createRequirement(true, "com.sun.management.internal"), PackageAccessRequirement.createRequirement(true, "com.ibm.lang.management.internal")};
        }
        return new PackageAccessRequirement[]{PackageAccessRequirement.createRequirement(true, "com.sun.management.internal")};
    }

    static void checkPackageRequirements(ILogger logger, Map<String, PackageAccessRequirement[]> requirements) {
        if (!ModularJavaUtils.hasHazelcastPackageAccess(requirements)) {
            String hazelcastModule = ModularJavaUtils.getHazelcastModuleName();
            if (hazelcastModule == null) {
                hazelcastModule = "ALL-UNNAMED";
            }
            logger.warning("Hazelcast is starting in a Java modular environment (Java 9 and newer) but without proper access to required Java packages. Use additional Java arguments to provide Hazelcast access to Java internal API. The internal API access is used to get the best performance results. Arguments to be used:\n --add-modules java.se" + ModularJavaUtils.createOpenPackageJavaArguments(hazelcastModule, requirements));
        }
    }

    private static boolean hasHazelcastPackageAccess(Map<String, PackageAccessRequirement[]> requirements) {
        try {
            Class<?> classModuleLayer = Class.forName("java.lang.ModuleLayer");
            Class<?> classModule = Class.forName("java.lang.Module");
            Method methodGetModule = Class.class.getMethod("getModule", new Class[0]);
            Method methodBoot = classModuleLayer.getMethod("boot", new Class[0]);
            Method methodModules = classModuleLayer.getMethod("modules", new Class[0]);
            Method methodGetName = classModule.getMethod("getName", new Class[0]);
            Method methodIsOpen = classModule.getMethod("isOpen", String.class, classModule);
            Method methodIsExported = classModule.getMethod("isExported", String.class, classModule);
            Object moduleHazelcast = methodGetModule.invoke(Hazelcast.class, new Object[0]);
            Object moduleLayerBoot = methodBoot.invoke(null, new Object[0]);
            Set moduleSet = (Set)methodModules.invoke(moduleLayerBoot, new Object[0]);
            for (Object m : moduleSet) {
                PackageAccessRequirement[] reqArray = requirements.get(methodGetName.invoke(m, new Object[0]));
                if (reqArray == null) continue;
                for (PackageAccessRequirement req : reqArray) {
                    Method methodToCheck = req.isForReflection() ? methodIsOpen : methodIsExported;
                    boolean hasAccess = (Boolean)methodToCheck.invoke(m, req.getPackageName(), moduleHazelcast);
                    if (hasAccess) continue;
                    return false;
                }
            }
        }
        catch (Exception e) {
            LOGGER.finest("Checking Hazelcast package access", e);
            return false;
        }
        return true;
    }

    private static String createOpenPackageJavaArguments(String hzModuleName, Map<String, PackageAccessRequirement[]> requirements) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, PackageAccessRequirement[]> moduleEntry : requirements.entrySet()) {
            for (PackageAccessRequirement requirement : moduleEntry.getValue()) {
                sb.append(requirement.forReflection ? " --add-opens " : " --add-exports ").append(moduleEntry.getKey()).append("/").append(requirement.packageName).append("=").append(hzModuleName);
            }
        }
        return sb.toString();
    }

    static final class PackageAccessRequirement {
        private final String packageName;
        private final boolean forReflection;

        private PackageAccessRequirement(boolean forReflection, String packageName) {
            this.packageName = packageName;
            this.forReflection = forReflection;
        }

        static PackageAccessRequirement createRequirement(boolean forReflection, String packageName) {
            return new PackageAccessRequirement(forReflection, packageName);
        }

        String getPackageName() {
            return this.packageName;
        }

        boolean isForReflection() {
            return this.forReflection;
        }
    }
}

