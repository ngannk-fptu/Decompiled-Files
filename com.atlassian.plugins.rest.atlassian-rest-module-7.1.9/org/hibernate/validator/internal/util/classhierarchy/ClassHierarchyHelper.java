/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.util.classhierarchy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.classhierarchy.Filter;
import org.hibernate.validator.internal.util.classhierarchy.Filters;

public class ClassHierarchyHelper {
    private ClassHierarchyHelper() {
    }

    public static <T> List<Class<? super T>> getHierarchy(Class<T> clazz, Filter ... filters) {
        Contracts.assertNotNull(clazz);
        ArrayList<Class<? super T>> classes = CollectionHelper.newArrayList();
        ArrayList<Filter> allFilters = CollectionHelper.newArrayList();
        allFilters.addAll(Arrays.asList(filters));
        allFilters.add(Filters.excludeProxies());
        ClassHierarchyHelper.getHierarchy(clazz, classes, allFilters);
        return classes;
    }

    private static <T> void getHierarchy(Class<? super T> clazz, List<Class<? super T>> classes, Iterable<Filter> filters) {
        for (Class<T> current = clazz; current != null; current = current.getSuperclass()) {
            if (classes.contains(current)) {
                return;
            }
            if (ClassHierarchyHelper.acceptedByAllFilters(current, filters)) {
                classes.add(current);
            }
            Class<?>[] classArray = current.getInterfaces();
            int n = classArray.length;
            for (int i = 0; i < n; ++i) {
                Class<?> currentInterface;
                Class<?> currentInterfaceCasted = currentInterface = classArray[i];
                ClassHierarchyHelper.getHierarchy(currentInterfaceCasted, classes, filters);
            }
        }
    }

    private static boolean acceptedByAllFilters(Class<?> clazz, Iterable<Filter> filters) {
        for (Filter classFilter : filters) {
            if (classFilter.accepts(clazz)) continue;
            return false;
        }
        return true;
    }

    public static <T> Set<Class<? super T>> getDirectlyImplementedInterfaces(Class<T> clazz) {
        Contracts.assertNotNull(clazz);
        HashSet<Class<? super T>> classes = CollectionHelper.newHashSet();
        ClassHierarchyHelper.getImplementedInterfaces(clazz, classes);
        return classes;
    }

    private static <T> void getImplementedInterfaces(Class<? super T> clazz, Set<Class<? super T>> classes) {
        Class<?>[] classArray = clazz.getInterfaces();
        int n = classArray.length;
        for (int i = 0; i < n; ++i) {
            Class<?> currentInterface;
            Class<?> currentInterfaceCasted = currentInterface = classArray[i];
            classes.add(currentInterfaceCasted);
            ClassHierarchyHelper.getImplementedInterfaces(currentInterfaceCasted, classes);
        }
    }
}

