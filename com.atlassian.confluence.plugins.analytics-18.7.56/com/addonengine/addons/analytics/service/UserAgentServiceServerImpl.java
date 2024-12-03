/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.UtilTimerStack
 *  javax.inject.Named
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.Reflection
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.reflect.KClass
 *  kotlin.text.StringsKt
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.service;

import com.addonengine.addons.analytics.service.UserAgentService;
import com.atlassian.util.profiling.UtilTimerStack;
import javax.inject.Named;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KClass;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Named
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016\u00a8\u0006\u0007"}, d2={"Lcom/addonengine/addons/analytics/service/UserAgentServiceServerImpl;", "Lcom/addonengine/addons/analytics/service/UserAgentService;", "()V", "isRobotUserAgent", "", "userAgent", "", "analytics"})
@SourceDebugExtension(value={"SMAP\nUserAgentServiceServerImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UserAgentServiceServerImpl.kt\ncom/addonengine/addons/analytics/service/UserAgentServiceServerImpl\n+ 2 utils.kt\ncom/addonengine/addons/analytics/util/UtilsKt\n*L\n1#1,33:1\n11#2,11:34\n*S KotlinDebug\n*F\n+ 1 UserAgentServiceServerImpl.kt\ncom/addonengine/addons/analytics/service/UserAgentServiceServerImpl\n*L\n9#1:34,11\n*E\n"})
public final class UserAgentServiceServerImpl
implements UserAgentService {
    /*
     * WARNING - void declaration
     */
    @Override
    public boolean isRobotUserAgent(@NotNull String userAgent) {
        Intrinsics.checkNotNullParameter((Object)userAgent, (String)"userAgent");
        KClass kClass = Reflection.getOrCreateKotlinClass(this.getClass());
        String name$iv = "isRobotUserAgent";
        boolean $i$f$atlassianProfilingTimer = false;
        if (UtilTimerStack.isActive()) {
            void klass$iv;
            UtilTimerStack.push((String)(klass$iv.getQualifiedName() + '_' + name$iv));
        }
        boolean bl = false;
        return StringsKt.startsWith$default((String)userAgent, (String)"gsa-crawler", (boolean)false, (int)2, null);
    }
}

