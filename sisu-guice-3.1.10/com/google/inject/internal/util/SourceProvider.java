/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Lists
 */
package com.google.inject.internal.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class SourceProvider {
    public static final Object UNKNOWN_SOURCE = "[unknown source]";
    private final SourceProvider parent;
    private final ImmutableSet<String> classNamesToSkip;
    public static final SourceProvider DEFAULT_INSTANCE = new SourceProvider((Iterable<String>)ImmutableSet.of((Object)SourceProvider.class.getName()));

    private SourceProvider(Iterable<String> classesToSkip) {
        this(null, classesToSkip);
    }

    private SourceProvider(SourceProvider parent, Iterable<String> classesToSkip) {
        this.parent = parent;
        ImmutableSet.Builder classNamesToSkipBuilder = ImmutableSet.builder();
        for (String classToSkip : classesToSkip) {
            if (parent != null && parent.shouldBeSkipped(classToSkip)) continue;
            classNamesToSkipBuilder.add((Object)classToSkip);
        }
        this.classNamesToSkip = classNamesToSkipBuilder.build();
    }

    public SourceProvider plusSkippedClasses(Class ... moreClassesToSkip) {
        return new SourceProvider(this, SourceProvider.asStrings(moreClassesToSkip));
    }

    private boolean shouldBeSkipped(String className) {
        return this.parent != null && this.parent.shouldBeSkipped(className) || this.classNamesToSkip.contains((Object)className);
    }

    private static List<String> asStrings(Class ... classes) {
        ArrayList strings = Lists.newArrayList();
        for (Class c : classes) {
            strings.add(c.getName());
        }
        return strings;
    }

    public StackTraceElement get(StackTraceElement[] stackTraceElements) {
        Preconditions.checkNotNull((Object)stackTraceElements, (Object)"The stack trace elements cannot be null.");
        for (StackTraceElement element : stackTraceElements) {
            String className = element.getClassName();
            if (this.shouldBeSkipped(className)) continue;
            return element;
        }
        throw new AssertionError();
    }

    public Object getFromClassNames(List<String> moduleClassNames) {
        Preconditions.checkNotNull(moduleClassNames, (Object)"The list of module class names cannot be null.");
        for (String moduleClassName : moduleClassNames) {
            if (this.shouldBeSkipped(moduleClassName)) continue;
            return new StackTraceElement(moduleClassName, "configure", null, -1);
        }
        return UNKNOWN_SOURCE;
    }
}

