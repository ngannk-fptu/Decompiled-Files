/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 */
package org.springframework.web.servlet.function;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.servlet.function.ServerResponse;

@Metadata(mv={1, 1, 18}, bv={1, 0, 3}, k=2, d1={"\u0000\u0014\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a&\u0010\u0000\u001a\u00020\u0001\"\n\b\u0000\u0010\u0002\u0018\u0001*\u00020\u0003*\u00020\u00042\u0006\u0010\u0005\u001a\u0002H\u0002H\u0086\b\u00a2\u0006\u0002\u0010\u0006\u00a8\u0006\u0007"}, d2={"bodyWithType", "Lorg/springframework/web/servlet/function/ServerResponse;", "T", "", "Lorg/springframework/web/servlet/function/ServerResponse$BodyBuilder;", "body", "(Lorg/springframework/web/servlet/function/ServerResponse$BodyBuilder;Ljava/lang/Object;)Lorg/springframework/web/servlet/function/ServerResponse;", "spring-webmvc"})
public final class ServerResponseExtensionsKt {
    public static final /* synthetic */ <T> ServerResponse bodyWithType(ServerResponse.BodyBuilder $this$bodyWithType, T body2) {
        int $i$f$bodyWithType = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$bodyWithType, (String)"$this$bodyWithType");
        Intrinsics.checkParameterIsNotNull(body2, (String)"body");
        Intrinsics.needClassReification();
        ServerResponse serverResponse = $this$bodyWithType.body(body2, new ParameterizedTypeReference<T>(){});
        Intrinsics.checkExpressionValueIsNotNull((Object)serverResponse, (String)"body(body, object : Para\u2026zedTypeReference<T>() {})");
        return serverResponse;
    }
}

