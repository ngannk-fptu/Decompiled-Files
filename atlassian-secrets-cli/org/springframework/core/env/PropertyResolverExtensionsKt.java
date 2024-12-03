/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package org.springframework.core.env;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.env.PropertyResolver;

@Metadata(mv={1, 1, 10}, bv={1, 0, 2}, k=2, d1={"\u0000\u000e\n\u0000\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u0015\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0001H\u0086\u0002\u00a8\u0006\u0004"}, d2={"get", "", "Lorg/springframework/core/env/PropertyResolver;", "key", "spring-core"})
public final class PropertyResolverExtensionsKt {
    @NotNull
    public static final String get(@NotNull PropertyResolver $receiver, @NotNull String key) {
        Intrinsics.checkParameterIsNotNull((Object)$receiver, (String)"$receiver");
        Intrinsics.checkParameterIsNotNull((Object)key, (String)"key");
        String string = $receiver.getRequiredProperty(key);
        Intrinsics.checkExpressionValueIsNotNull((Object)string, (String)"getRequiredProperty(key)");
        return string;
    }
}

