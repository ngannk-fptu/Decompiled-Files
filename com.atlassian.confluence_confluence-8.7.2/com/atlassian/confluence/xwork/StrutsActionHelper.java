/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.xwork;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import java.lang.reflect.Method;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
public final class StrutsActionHelper {
    private StrutsActionHelper() {
        throw new AssertionError((Object)"StrutsActionHelper should not be instantiated.");
    }

    public static Method getActionMethod(Class<?> actionClass, @Nullable String methodName) throws NoSuchMethodException {
        return actionClass.getMethod(Objects.requireNonNullElse(methodName, "execute"), new Class[0]);
    }

    public static Method getActionClassMethod(Class<?> actionClass, @Nullable String methodName) {
        try {
            return StrutsActionHelper.getActionMethod(actionClass, methodName);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException("action method [ " + methodName + " ] not found on [ " + actionClass.getName() + " ]", e);
        }
    }
}

