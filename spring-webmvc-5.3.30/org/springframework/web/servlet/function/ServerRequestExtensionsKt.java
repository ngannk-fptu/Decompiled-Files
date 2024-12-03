/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.springframework.core.ParameterizedTypeReference
 *  org.springframework.http.MediaType
 */
package org.springframework.web.servlet.function;

import java.net.InetSocketAddress;
import java.security.Principal;
import java.util.OptionalLong;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.ServerRequest;

@Metadata(mv={1, 1, 18}, bv={1, 0, 3}, k=2, d1={"\u00004\n\u0000\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u0014\u0010\u0000\u001a\u0004\u0018\u00010\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004\u001a\u001e\u0010\u0005\u001a\u0002H\u0006\"\n\b\u0000\u0010\u0006\u0018\u0001*\u00020\u0001*\u00020\u0002H\u0086\b\u00a2\u0006\u0002\u0010\u0007\u001a\u0011\u0010\b\u001a\u0004\u0018\u00010\t*\u00020\n\u00a2\u0006\u0002\u0010\u000b\u001a\f\u0010\f\u001a\u0004\u0018\u00010\r*\u00020\n\u001a\u0014\u0010\u000e\u001a\u0004\u0018\u00010\u0004*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004\u001a\f\u0010\u000f\u001a\u0004\u0018\u00010\u0010*\u00020\u0002\u001a\f\u0010\u0011\u001a\u0004\u0018\u00010\u0012*\u00020\u0002\u00a8\u0006\u0013"}, d2={"attributeOrNull", "", "Lorg/springframework/web/servlet/function/ServerRequest;", "name", "", "body", "T", "(Lorg/springframework/web/servlet/function/ServerRequest;)Ljava/lang/Object;", "contentLengthOrNull", "", "Lorg/springframework/web/servlet/function/ServerRequest$Headers;", "(Lorg/springframework/web/servlet/function/ServerRequest$Headers;)Ljava/lang/Long;", "contentTypeOrNull", "Lorg/springframework/http/MediaType;", "paramOrNull", "principalOrNull", "Ljava/security/Principal;", "remoteAddressOrNull", "Ljava/net/InetSocketAddress;", "spring-webmvc"})
public final class ServerRequestExtensionsKt {
    @Nullable
    public static final InetSocketAddress remoteAddressOrNull(@NotNull ServerRequest $this$remoteAddressOrNull) {
        Intrinsics.checkParameterIsNotNull((Object)$this$remoteAddressOrNull, (String)"$this$remoteAddressOrNull");
        return $this$remoteAddressOrNull.remoteAddress().orElse(null);
    }

    public static final /* synthetic */ <T> T body(ServerRequest $this$body) {
        int $i$f$body = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$body, (String)"$this$body");
        Intrinsics.needClassReification();
        Object t = $this$body.body((ParameterizedTypeReference)new ParameterizedTypeReference<T>(){});
        Intrinsics.checkExpressionValueIsNotNull(t, (String)"body(object : ParameterizedTypeReference<T>() {})");
        return t;
    }

    @Nullable
    public static final Object attributeOrNull(@NotNull ServerRequest $this$attributeOrNull, @NotNull String name) {
        Intrinsics.checkParameterIsNotNull((Object)$this$attributeOrNull, (String)"$this$attributeOrNull");
        Intrinsics.checkParameterIsNotNull((Object)name, (String)"name");
        return $this$attributeOrNull.attribute(name).orElse(null);
    }

    @Nullable
    public static final String paramOrNull(@NotNull ServerRequest $this$paramOrNull, @NotNull String name) {
        Intrinsics.checkParameterIsNotNull((Object)$this$paramOrNull, (String)"$this$paramOrNull");
        Intrinsics.checkParameterIsNotNull((Object)name, (String)"name");
        return $this$paramOrNull.param(name).orElse(null);
    }

    @Nullable
    public static final Principal principalOrNull(@NotNull ServerRequest $this$principalOrNull) {
        Intrinsics.checkParameterIsNotNull((Object)$this$principalOrNull, (String)"$this$principalOrNull");
        return $this$principalOrNull.principal().orElse(null);
    }

    @Nullable
    public static final Long contentLengthOrNull(@NotNull ServerRequest.Headers $this$contentLengthOrNull) {
        Intrinsics.checkParameterIsNotNull((Object)$this$contentLengthOrNull, (String)"$this$contentLengthOrNull");
        OptionalLong optionalLong = $this$contentLengthOrNull.contentLength();
        boolean bl = false;
        boolean bl2 = false;
        OptionalLong it = optionalLong;
        boolean bl3 = false;
        OptionalLong optionalLong2 = it;
        Intrinsics.checkExpressionValueIsNotNull((Object)optionalLong2, (String)"it");
        return optionalLong2.isPresent() ? Long.valueOf(it.getAsLong()) : null;
    }

    @Nullable
    public static final MediaType contentTypeOrNull(@NotNull ServerRequest.Headers $this$contentTypeOrNull) {
        Intrinsics.checkParameterIsNotNull((Object)$this$contentTypeOrNull, (String)"$this$contentTypeOrNull");
        return $this$contentTypeOrNull.contentType().orElse(null);
    }
}

