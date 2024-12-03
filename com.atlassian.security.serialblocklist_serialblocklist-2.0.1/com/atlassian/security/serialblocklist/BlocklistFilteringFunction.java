/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.security.serialblocklist;

import com.atlassian.security.serialblocklist.BlockedPattern;
import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BlocklistFilteringFunction
implements Predicate<Class<?>> {
    private static final Logger log = LoggerFactory.getLogger(BlocklistFilteringFunction.class);
    private final Set<String> blockedClasses;
    private final List<BlockedPattern> blockedPatterns;

    public BlocklistFilteringFunction(Set<String> blockedClasses, List<BlockedPattern> blockedPatterns) {
        this.blockedClasses = new HashSet<String>(blockedClasses);
        this.blockedPatterns = new ArrayList<BlockedPattern>(blockedPatterns);
    }

    @Override
    public boolean test(Class<?> rawClass) {
        if (rawClass == null) {
            return true;
        }
        String className = BlocklistFilteringFunction.getActualClassName(rawClass);
        if (!this.checkClassName(className)) {
            return false;
        }
        return this.checkPattern(rawClass);
    }

    @VisibleForTesting
    static String getActualClassName(Class<?> rawClass) {
        if (rawClass.isArray()) {
            return rawClass.getComponentType().getName();
        }
        return rawClass.getName();
    }

    @VisibleForTesting
    boolean checkClassName(String className) {
        if (this.blockedClasses.contains(className)) {
            BlocklistFilteringFunction.logWarning(className);
            return false;
        }
        return true;
    }

    @VisibleForTesting
    boolean checkPattern(Class<?> rawClass) {
        if (rawClass.isInterface()) {
            return true;
        }
        for (BlockedPattern pattern : this.blockedPatterns) {
            if (!this.matchPattern(pattern, rawClass)) continue;
            BlocklistFilteringFunction.logWarning(rawClass.getName());
            return false;
        }
        return true;
    }

    private boolean matchPattern(BlockedPattern pattern, Class<?> rawClass) {
        if (pattern.isEmpty()) {
            return false;
        }
        boolean isPatternMatch = true;
        if (!pattern.getPrefixes().isEmpty()) {
            isPatternMatch = this.matchesPrefix(pattern, rawClass);
        }
        if (isPatternMatch && !pattern.getParentClasses().isEmpty()) {
            isPatternMatch = this.matchesParentClass(pattern, rawClass);
        }
        if (isPatternMatch && !pattern.getSuffixes().isEmpty()) {
            isPatternMatch = this.matchesSuffix(pattern, rawClass);
        }
        return isPatternMatch;
    }

    private boolean matchesPrefix(BlockedPattern pattern, Class<?> rawClass) {
        return pattern.getPrefixes().stream().anyMatch(rawClass.getName()::startsWith);
    }

    private boolean matchesParentClass(BlockedPattern pattern, Class<?> rawClass) {
        for (Class<?> cls = rawClass; cls != null && cls != Object.class; cls = cls.getSuperclass()) {
            String superClassName = cls.getSimpleName();
            if (!pattern.getParentClasses().contains(superClassName)) continue;
            return true;
        }
        return false;
    }

    private boolean matchesSuffix(BlockedPattern pattern, Class<?> rawClass) {
        return pattern.getSuffixes().stream().anyMatch(rawClass.getName()::endsWith);
    }

    private static void logWarning(String className) {
        log.warn("Deserialize step prevented for class: {}", (Object)className);
    }
}

