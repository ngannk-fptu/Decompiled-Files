/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package org.springframework.web.servlet.function;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctionDsl;
import org.springframework.web.servlet.function.ServerResponse;

@Metadata(mv={1, 1, 18}, bv={1, 0, 3}, k=2, d1={"\u0000 \n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a%\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u00012\u0017\u0010\u0003\u001a\u0013\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004\u00a2\u0006\u0002\b\u0007\u001a9\u0010\b\u001a\u0010\u0012\f\u0012\n \n*\u0004\u0018\u0001H\tH\t0\u0001\"\b\b\u0000\u0010\t*\u00020\u0002*\b\u0012\u0004\u0012\u0002H\t0\u00012\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u0002H\t0\u0001H\u0086\u0002\u00a8\u0006\f"}, d2={"router", "Lorg/springframework/web/servlet/function/RouterFunction;", "Lorg/springframework/web/servlet/function/ServerResponse;", "routes", "Lkotlin/Function1;", "Lorg/springframework/web/servlet/function/RouterFunctionDsl;", "", "Lkotlin/ExtensionFunctionType;", "plus", "T", "kotlin.jvm.PlatformType", "other", "spring-webmvc"})
public final class RouterFunctionDslKt {
    @NotNull
    public static final RouterFunction<ServerResponse> router(@NotNull Function1<? super RouterFunctionDsl, Unit> routes) {
        Intrinsics.checkParameterIsNotNull(routes, (String)"routes");
        return new RouterFunctionDsl(routes).build$spring_webmvc();
    }

    @NotNull
    public static final <T extends ServerResponse> RouterFunction<T> plus(@NotNull RouterFunction<T> $this$plus, @NotNull RouterFunction<T> other) {
        Intrinsics.checkParameterIsNotNull($this$plus, (String)"$this$plus");
        Intrinsics.checkParameterIsNotNull(other, (String)"other");
        RouterFunction<T> routerFunction = $this$plus.and(other);
        Intrinsics.checkExpressionValueIsNotNull(routerFunction, (String)"this.and(other)");
        return routerFunction;
    }
}

