/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.velocity.runtime.RuntimeServices
 *  org.apache.velocity.runtime.log.Log
 *  org.apache.velocity.util.introspection.SecureIntrospectorImpl
 */
package com.atlassian.velocity.htmlsafe.introspection;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.util.introspection.SecureIntrospectorImpl;

public class AllowlistSecureIntrospector
extends SecureIntrospectorImpl {
    public static final String INTROSPECTOR_ALLOW_PACKAGES = "introspector.allow.packages";
    public static final String INTROSPECTOR_ALLOW_CLASSES = "introspector.allow.classes";
    private final Set<String> restrictedClasses;
    private final Set<String> allowedClasses;
    private final Set<String> allowedPackages;

    public AllowlistSecureIntrospector(Log log, RuntimeServices runtimeServices) {
        this(Optional.ofNullable(runtimeServices.getConfiguration().getStringArray("introspector.restrict.classes")), Optional.ofNullable(runtimeServices.getConfiguration().getStringArray("introspector.restrict.packages")), Optional.ofNullable(runtimeServices.getConfiguration().getStringArray("introspector.allowlist.classes")), Optional.ofNullable(runtimeServices.getConfiguration().getStringArray(INTROSPECTOR_ALLOW_CLASSES)), Optional.ofNullable(runtimeServices.getConfiguration().getStringArray(INTROSPECTOR_ALLOW_PACKAGES)), log, runtimeServices);
    }

    public AllowlistSecureIntrospector(Optional<String[]> restrictedClasses, Optional<String[]> restrictedParentPackages, Optional<String[]> allowlistClasses, Optional<String[]> allowedClasses, Optional<String[]> allowedPackages, Log log, RuntimeServices runtimeServices) {
        super(restrictedClasses.orElse(new String[0]), restrictedParentPackages.orElse(new String[0]), allowlistClasses.orElse(new String[0]), log, runtimeServices);
        this.restrictedClasses = this.mapOptionalToSet(restrictedClasses);
        this.allowedClasses = this.mapOptionalToSet(allowedClasses);
        this.allowedPackages = this.mapOptionalToSet(allowedPackages);
    }

    public boolean checkObjectExecutePermission(Class classToCheck, String methodToCheck) {
        boolean allowedByParent = super.checkObjectExecutePermission(classToCheck, methodToCheck);
        return allowedByParent || this.isClassListedAsExceptionFromRestrictRule(classToCheck);
    }

    private Set<String> mapOptionalToSet(Optional<String[]> restrictedClasses) {
        return restrictedClasses.map(Arrays::asList).map(HashSet::new).map(Collections::unmodifiableSet).orElseGet(Collections::emptySet);
    }

    private boolean isClassListedAsExceptionFromRestrictRule(Class classToCheck) {
        String className = classToCheck.getName();
        String packageName = this.getPackageName(classToCheck, className);
        return (this.allowedPackages.contains(packageName) || this.allowedClasses.contains(className)) && !this.restrictedClasses.contains(className);
    }

    private String getPackageName(Class classToCheck, String className) {
        if (classToCheck.getPackage() != null) {
            return classToCheck.getPackage().getName();
        }
        return StringUtils.substringBeforeLast((String)className, (String)".");
    }
}

