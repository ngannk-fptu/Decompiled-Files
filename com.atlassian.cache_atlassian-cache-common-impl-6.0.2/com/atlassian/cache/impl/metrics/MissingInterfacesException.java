/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ClassUtils
 */
package com.atlassian.cache.impl.metrics;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ClassUtils;

public class MissingInterfacesException
extends IllegalArgumentException {
    public MissingInterfacesException(Object objectWithMissingInterfaces, Class<?> ... expectedImplementations) {
        super(MissingInterfacesException.generateErrorString(objectWithMissingInterfaces, expectedImplementations));
    }

    private static String generateErrorString(Object objectWithMissingImplementations, Class<?>[] expectedImplementations) {
        String expectedImplementationsString = Arrays.stream(expectedImplementations).map(Class::getName).collect(Collectors.joining(", ", "[", "]"));
        String actualInterfacesString = ClassUtils.getAllInterfaces(objectWithMissingImplementations.getClass()).toString();
        return String.format("Error: %s is expected to implement at least: %s. Class actually implements: %s", objectWithMissingImplementations.getClass().getName(), expectedImplementationsString, actualInterfacesString);
    }
}

