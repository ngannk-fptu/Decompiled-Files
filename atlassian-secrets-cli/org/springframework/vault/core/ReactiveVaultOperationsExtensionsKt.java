/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.ResultKt
 *  kotlin.Unit
 *  kotlin.coroutines.Continuation
 *  kotlin.coroutines.intrinsics.IntrinsicsKt
 *  kotlin.coroutines.jvm.internal.ContinuationImpl
 *  kotlin.jvm.internal.InlineMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlinx.coroutines.flow.Flow
 *  kotlinx.coroutines.reactive.AwaitKt
 *  kotlinx.coroutines.reactive.ReactiveFlowKt
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.vault.core;

import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.jvm.internal.InlineMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.reactive.AwaitKt;
import kotlinx.coroutines.reactive.ReactiveFlowKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reactivestreams.Publisher;
import org.springframework.vault.core.ReactiveVaultOperations;
import org.springframework.vault.core.ReactiveVaultOperationsExtensionsKt;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultResponseSupport;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Metadata(mv={1, 4, 2}, bv={1, 0, 3}, k=2, d1={"\u00006\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u001d\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0005\u001a/\u0010\u0006\u001a\b\u0012\u0004\u0012\u0002H\b0\u0007\"\n\b\u0000\u0010\b\u0018\u0001*\u00020\t*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004H\u0086H\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0005\u001a1\u0010\n\u001a\n\u0012\u0004\u0012\u0002H\b\u0018\u00010\u0007\"\n\b\u0000\u0010\b\u0018\u0001*\u00020\t*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004H\u0086H\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0005\u001a\u001d\u0010\u000b\u001a\u00020\f*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0005\u001a%\u0010\u000b\u001a\u00020\f*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\r\u001a\u00020\tH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u000e\u001a\u0018\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00040\u0010*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004\u001a-\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\b0\u00070\u0012\"\n\b\u0000\u0010\b\u0018\u0001*\u00020\t*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004H\u0086\b\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u0013"}, d2={"awaitDelete", "", "Lorg/springframework/vault/core/ReactiveVaultOperations;", "path", "", "(Lorg/springframework/vault/core/ReactiveVaultOperations;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "awaitRead", "Lorg/springframework/vault/support/VaultResponseSupport;", "T", "", "awaitReadOrNull", "awaitWrite", "Lorg/springframework/vault/support/VaultResponse;", "body", "(Lorg/springframework/vault/core/ReactiveVaultOperations;Ljava/lang/String;Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "listAsFlow", "Lkotlinx/coroutines/flow/Flow;", "read", "Lreactor/core/publisher/Mono;", "spring-vault-core"})
public final class ReactiveVaultOperationsExtensionsKt {
    public static final /* synthetic */ <T> Mono<VaultResponseSupport<T>> read(ReactiveVaultOperations $this$read, String path) {
        int $i$f$read = 0;
        Intrinsics.checkNotNullParameter((Object)$this$read, (String)"$this$read");
        Intrinsics.checkNotNullParameter((Object)path, (String)"path");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        Mono<VaultResponseSupport<Object>> mono = $this$read.read(path, Object.class);
        Intrinsics.checkNotNullExpressionValue(mono, (String)"read(path, T::class.java)");
        return mono;
    }

    public static final /* synthetic */ <T> Object awaitRead(ReactiveVaultOperations $this$awaitRead, String path, Continuation<? super VaultResponseSupport<T>> continuation) {
        int $i$f$awaitRead = 0;
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        Mono<VaultResponseSupport<Object>> mono = $this$awaitRead.read(path, Object.class);
        Intrinsics.checkNotNullExpressionValue(mono, (String)"read(path, T::class.java)");
        Publisher publisher = (Publisher)mono;
        InlineMarker.mark((int)0);
        Object object = AwaitKt.awaitSingle((Publisher)publisher, continuation);
        InlineMarker.mark((int)1);
        Intrinsics.checkNotNullExpressionValue((Object)object, (String)"read(path, T::class.java).awaitSingle()");
        return object;
    }

    public static final /* synthetic */ <T> Object awaitReadOrNull(ReactiveVaultOperations $this$awaitReadOrNull, String path, Continuation<? super VaultResponseSupport<T>> continuation) {
        int $i$f$awaitReadOrNull = 0;
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        Mono<VaultResponseSupport<Object>> mono = $this$awaitReadOrNull.read(path, Object.class);
        Intrinsics.checkNotNullExpressionValue(mono, (String)"read(path, T::class.java)");
        Publisher publisher = (Publisher)mono;
        InlineMarker.mark((int)0);
        Object object = AwaitKt.awaitFirstOrNull((Publisher)publisher, continuation);
        InlineMarker.mark((int)1);
        return object;
    }

    @NotNull
    public static final Flow<String> listAsFlow(@NotNull ReactiveVaultOperations $this$listAsFlow, @NotNull String path) {
        Intrinsics.checkNotNullParameter((Object)$this$listAsFlow, (String)"$this$listAsFlow");
        Intrinsics.checkNotNullParameter((Object)path, (String)"path");
        Flux<String> flux = $this$listAsFlow.list(path);
        Intrinsics.checkNotNullExpressionValue(flux, (String)"list(path)");
        return ReactiveFlowKt.asFlow((Publisher)((Publisher)flux));
    }

    /*
     * Unable to fully structure code
     */
    @Nullable
    public static final Object awaitWrite(@NotNull ReactiveVaultOperations var0, @NotNull String var1_1, @NotNull Continuation<? super VaultResponse> var2_2) {
        if (!(var2_2 instanceof awaitWrite.1)) ** GOTO lbl-1000
        var4_3 = var2_2;
        if ((var4_3.label & -2147483648) != 0) {
            var4_3.label -= -2147483648;
        } else lbl-1000:
        // 2 sources

        {
            $continuation = new ContinuationImpl(var2_2){
                /* synthetic */ Object result;
                int label;

                @Nullable
                public final Object invokeSuspend(@NotNull Object $result) {
                    this.result = $result;
                    this.label |= Integer.MIN_VALUE;
                    return ReactiveVaultOperationsExtensionsKt.awaitWrite(null, null, (Continuation<? super VaultResponse>)this);
                }
            };
        }
        $result = $continuation.result;
        var5_5 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch ($continuation.label) {
            case 0: {
                ResultKt.throwOnFailure((Object)$result);
                v0 = $this$awaitWrite.write((String)path);
                Intrinsics.checkNotNullExpressionValue(v0, (String)"write(path)");
                $continuation.label = 1;
                v1 = AwaitKt.awaitSingle((Publisher)((Publisher)v0), (Continuation)$continuation);
                if (v1 == var5_5) {
                    return var5_5;
                }
                ** GOTO lbl22
            }
            case 1: {
                ResultKt.throwOnFailure((Object)$result);
                v1 = $result;
lbl22:
                // 2 sources

                Intrinsics.checkNotNullExpressionValue((Object)v1, (String)"write(path).awaitSingle()");
                return v1;
            }
        }
        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
    }

    /*
     * Unable to fully structure code
     */
    @Nullable
    public static final Object awaitWrite(@NotNull ReactiveVaultOperations var0, @NotNull String var1_1, @NotNull Object var2_2, @NotNull Continuation<? super VaultResponse> var3_3) {
        if (!(var3_3 instanceof awaitWrite.2)) ** GOTO lbl-1000
        var5_4 = var3_3;
        if ((var5_4.label & -2147483648) != 0) {
            var5_4.label -= -2147483648;
        } else lbl-1000:
        // 2 sources

        {
            $continuation = new ContinuationImpl(var3_3){
                /* synthetic */ Object result;
                int label;

                @Nullable
                public final Object invokeSuspend(@NotNull Object $result) {
                    this.result = $result;
                    this.label |= Integer.MIN_VALUE;
                    return ReactiveVaultOperationsExtensionsKt.awaitWrite(null, null, null, (Continuation<? super VaultResponse>)this);
                }
            };
        }
        $result = $continuation.result;
        var6_6 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch ($continuation.label) {
            case 0: {
                ResultKt.throwOnFailure((Object)$result);
                v0 = $this$awaitWrite.write((String)path, body);
                Intrinsics.checkNotNullExpressionValue(v0, (String)"write(path, body)");
                $continuation.label = 1;
                v1 = AwaitKt.awaitSingle((Publisher)((Publisher)v0), (Continuation)$continuation);
                if (v1 == var6_6) {
                    return var6_6;
                }
                ** GOTO lbl22
            }
            case 1: {
                ResultKt.throwOnFailure((Object)$result);
                v1 = $result;
lbl22:
                // 2 sources

                Intrinsics.checkNotNullExpressionValue((Object)v1, (String)"write(path, body).awaitSingle()");
                return v1;
            }
        }
        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
    }

    @Nullable
    public static final Object awaitDelete(@NotNull ReactiveVaultOperations $this$awaitDelete, @NotNull String path, @NotNull Continuation<? super Unit> $completion) {
        Mono<Void> mono = $this$awaitDelete.delete(path);
        Intrinsics.checkNotNullExpressionValue(mono, (String)"delete(path)");
        Object object = AwaitKt.awaitFirstOrNull((Publisher)((Publisher)mono), $completion);
        if (object == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
            return object;
        }
        return Unit.INSTANCE;
    }
}

