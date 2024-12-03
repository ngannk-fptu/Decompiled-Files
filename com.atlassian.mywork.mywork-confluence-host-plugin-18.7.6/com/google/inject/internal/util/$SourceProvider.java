/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.util;

import com.google.inject.internal.util.$ImmutableSet;
import com.google.inject.internal.util.$Iterables;
import com.google.inject.internal.util.$Lists;
import java.util.ArrayList;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class $SourceProvider {
    public static final Object UNKNOWN_SOURCE = "[unknown source]";
    private final $ImmutableSet<String> classNamesToSkip;
    public static final $SourceProvider DEFAULT_INSTANCE = new $SourceProvider($ImmutableSet.of($SourceProvider.class.getName()));

    private $SourceProvider(Iterable<String> classesToSkip) {
        this.classNamesToSkip = $ImmutableSet.copyOf(classesToSkip);
    }

    public $SourceProvider plusSkippedClasses(Class ... moreClassesToSkip) {
        return new $SourceProvider($Iterables.concat(this.classNamesToSkip, $SourceProvider.asStrings(moreClassesToSkip)));
    }

    private static List<String> asStrings(Class ... classes) {
        ArrayList<String> strings = $Lists.newArrayList();
        for (Class c : classes) {
            strings.add(c.getName());
        }
        return strings;
    }

    public StackTraceElement get() {
        for (StackTraceElement element : new Throwable().getStackTrace()) {
            String className = element.getClassName();
            if (this.classNamesToSkip.contains(className)) continue;
            return element;
        }
        throw new AssertionError();
    }
}

