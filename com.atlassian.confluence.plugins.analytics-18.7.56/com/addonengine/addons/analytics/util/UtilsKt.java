/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceSystemProperties
 *  com.atlassian.util.profiling.UtilTimerStack
 *  kotlin.Metadata
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.reflect.KClass
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.util;

import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.util.profiling.UtilTimerStack;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.KClass;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=2, xi=48, d1={"\u0000\u001e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\u001a9\u0010\u0000\u001a\u0002H\u0001\"\u0004\b\u0000\u0010\u00012\n\u0010\u0002\u001a\u0006\u0012\u0002\b\u00030\u00032\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u0002H\u00010\u0007H\u0086\b\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\b\u001a\u0006\u0010\t\u001a\u00020\n\u0082\u0002\u0007\n\u0005\b\u009920\u0001\u00a8\u0006\u000b"}, d2={"atlassianProfilingTimer", "T", "klass", "Lkotlin/reflect/KClass;", "name", "", "body", "Lkotlin/Function0;", "(Lkotlin/reflect/KClass;Ljava/lang/String;Lkotlin/jvm/functions/Function0;)Ljava/lang/Object;", "isAddonDevMode", "", "analytics"})
public final class UtilsKt {
    public static final boolean isAddonDevMode() {
        return ConfluenceSystemProperties.isDevMode() && Intrinsics.areEqual((Object)System.getProperty("addonengine.dev"), (Object)"true");
    }

    public static final <T> T atlassianProfilingTimer(@NotNull KClass<?> klass, @NotNull String name, @NotNull Function0<? extends T> body) {
        Intrinsics.checkNotNullParameter(klass, (String)"klass");
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter(body, (String)"body");
        boolean $i$f$atlassianProfilingTimer = false;
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.push((String)(klass.getQualifiedName() + '_' + name));
        }
        Object result = body.invoke();
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.pop((String)(klass.getQualifiedName() + '_' + name));
        }
        return (T)result;
    }
}

