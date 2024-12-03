/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ClassUtils
 */
package org.apache.velocity.util.introspection;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.ClassUtils;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.util.FilesystemUtils;
import org.apache.velocity.util.introspection.Introspector;
import org.apache.velocity.util.introspection.SecureIntrospectorControl;

public class SecureIntrospectorImpl
extends Introspector
implements SecureIntrospectorControl {
    private final Set<Class> badClasses;
    private final Set<String> badPackages;
    private final Set<String> allowlistClasses;
    private final Map<String, Boolean> checkedClasses = new ConcurrentHashMap<String, Boolean>();

    public SecureIntrospectorImpl(String[] badClasses, String[] badPackages, String[] allowlistClasses, Log log, RuntimeServices runtimeServices) {
        super(log, runtimeServices);
        this.badClasses = Collections.unmodifiableSet(this.prepareClassSet(Arrays.asList(badClasses)));
        this.allowlistClasses = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(allowlistClasses)));
        this.badPackages = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(badPackages)));
    }

    @Override
    public Method getMethod(Class clazz, String methodName, Object[] params) throws IllegalArgumentException {
        if (!this.checkObjectExecutePermission(clazz, methodName)) {
            this.log.warn(String.format("Cannot retrieve method %s from object of class %s due to security restrictions.", methodName, clazz.getName()));
            return null;
        }
        if (this.paramContainsPathTraversal(params)) {
            this.log.warn(String.format("Found a potential path traversal attempt in the parameters: %s of method: %s from object of class: %s, rejecting due to security restrictions.", Arrays.toString(params), methodName, clazz.getName()));
            return null;
        }
        return super.getMethod(clazz, methodName, params);
    }

    private boolean paramContainsPathTraversal(Object[] params) {
        for (Object param : params) {
            if (!(param instanceof String) || !FilesystemUtils.containsPathTraversal((String)param)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean checkObjectExecutePermission(Class clazz, String methodName) {
        if (methodName != null && (methodName.equals("wait") || methodName.equals("notify"))) {
            return false;
        }
        if (Number.class.isAssignableFrom(clazz)) {
            return true;
        }
        if (Boolean.class.isAssignableFrom(clazz)) {
            return true;
        }
        if (String.class.isAssignableFrom(clazz)) {
            return true;
        }
        if (Class.class.isAssignableFrom(clazz) && methodName != null && methodName.equals("getName")) {
            return true;
        }
        return this.checkedClasses.computeIfAbsent(clazz.getName(), c -> {
            try {
                return this.isAllowedClass(clazz);
            }
            catch (ClassNotFoundException e) {
                this.log.error("Class not found", e);
                return true;
            }
        });
    }

    private boolean isAllowedClass(Class clazz) throws ClassNotFoundException {
        if (this.badClasses.stream().filter(badClass -> badClass.isAssignableFrom(clazz)).findFirst().isPresent()) {
            return false;
        }
        boolean isAllowClass = true;
        String className = clazz.getName();
        if (className.startsWith("[L") && className.endsWith(";")) {
            className = className.substring(2, className.length() - 1);
            isAllowClass = this.isAllowedClass(Class.forName(className));
        } else {
            Iterator<Class<?>> allInterfacesAndClassIterator = this.getAllInterfacesAndClassIterator(clazz);
            while (allInterfacesAndClassIterator.hasNext() && isAllowClass) {
                List<String> parentPackageNames;
                Class<?> checkClass = allInterfacesAndClassIterator.next();
                String superClassName = checkClass.getName();
                if (this.allowlistClasses.contains(superClassName) || !(parentPackageNames = SecureIntrospectorImpl.populateParentPackages(superClassName, new ArrayList<String>())).stream().filter(packageName -> this.badPackages.contains(packageName)).findFirst().isPresent()) continue;
                isAllowClass = false;
            }
        }
        return isAllowClass;
    }

    private Set<Class> prepareClassSet(List<String> badClassNames) {
        HashSet<Class> badClasses = new HashSet<Class>();
        for (String badClassName : badClassNames) {
            try {
                badClasses.add(Class.forName(badClassName));
            }
            catch (ClassNotFoundException e) {
                this.log.warn(String.format("Cannot find class %s for security introspection in velocity classloader.", badClassName));
            }
        }
        return badClasses;
    }

    private static List<String> populateParentPackages(String name, List<String> packages) {
        int dotPos = name.lastIndexOf(46);
        if (dotPos != -1) {
            String packageName = name.substring(0, dotPos);
            packages.add(packageName);
            SecureIntrospectorImpl.populateParentPackages(packageName, packages);
        }
        return packages;
    }

    private Iterator<Class<?>> getAllInterfacesAndClassIterator(Class clazz) {
        ArrayList<Class> allInterfacesAndClass = new ArrayList<Class>();
        allInterfacesAndClass.add(clazz);
        allInterfacesAndClass.addAll(ClassUtils.getAllInterfaces((Class)clazz));
        allInterfacesAndClass.addAll(ClassUtils.getAllSuperclasses((Class)clazz));
        return allInterfacesAndClass.iterator();
    }
}

