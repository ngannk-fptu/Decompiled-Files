/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.ResultKt
 *  kotlin.Unit
 *  kotlin.coroutines.Continuation
 *  kotlin.coroutines.CoroutineContext
 *  kotlin.coroutines.intrinsics.IntrinsicsKt
 *  kotlin.coroutines.jvm.internal.ContinuationImpl
 *  kotlin.jvm.functions.Function2
 *  kotlin.jvm.internal.Intrinsics
 *  kotlinx.coroutines.CoroutineScope
 *  kotlinx.coroutines.Dispatchers
 *  kotlinx.coroutines.flow.Flow
 *  kotlinx.coroutines.reactive.AwaitKt
 *  kotlinx.coroutines.reactive.ReactiveFlowKt
 *  kotlinx.coroutines.reactor.MonoKt
 *  kotlinx.coroutines.reactor.ReactorFlowKt
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.reactivestreams.Publisher
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.transaction.reactive;

import java.util.Optional;
import java.util.function.Function;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.reactive.AwaitKt;
import kotlinx.coroutines.reactive.ReactiveFlowKt;
import kotlinx.coroutines.reactor.MonoKt;
import kotlinx.coroutines.reactor.ReactorFlowKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reactivestreams.Publisher;
import org.springframework.transaction.ReactiveTransaction;
import org.springframework.transaction.reactive.TransactionCallback;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.transaction.reactive.TransactionalOperatorExtensionsKt;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Metadata(mv={1, 1, 18}, bv={1, 0, 3}, k=2, d1={"\u0000&\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001aG\u0010\u0000\u001a\u0004\u0018\u0001H\u0001\"\b\b\u0000\u0010\u0001*\u00020\u0002*\u00020\u00032$\u0010\u0004\u001a \b\u0001\u0012\u0004\u0012\u00020\u0006\u0012\f\u0012\n\u0012\u0006\u0012\u0004\u0018\u0001H\u00010\u0007\u0012\u0006\u0012\u0004\u0018\u00010\u00020\u0005H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\b\u001a(\u0010\t\u001a\b\u0012\u0004\u0012\u0002H\u00010\n\"\b\b\u0000\u0010\u0001*\u00020\u0002*\b\u0012\u0004\u0012\u0002H\u00010\n2\u0006\u0010\u000b\u001a\u00020\u0003\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\f"}, d2={"executeAndAwait", "T", "", "Lorg/springframework/transaction/reactive/TransactionalOperator;", "f", "Lkotlin/Function2;", "Lorg/springframework/transaction/ReactiveTransaction;", "Lkotlin/coroutines/Continuation;", "(Lorg/springframework/transaction/reactive/TransactionalOperator;Lkotlin/jvm/functions/Function2;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "transactional", "Lkotlinx/coroutines/flow/Flow;", "operator", "spring-tx"})
public final class TransactionalOperatorExtensionsKt {
    @NotNull
    public static final <T> Flow<T> transactional(@NotNull Flow<? extends T> $this$transactional, @NotNull TransactionalOperator operator) {
        Intrinsics.checkParameterIsNotNull($this$transactional, (String)"$this$transactional");
        Intrinsics.checkParameterIsNotNull((Object)operator, (String)"operator");
        Flux flux = operator.transactional(ReactorFlowKt.asFlux$default($this$transactional, null, (int)1, null));
        Intrinsics.checkExpressionValueIsNotNull(flux, (String)"operator.transactional(asFlux())");
        return ReactiveFlowKt.asFlow((Publisher)((Publisher)flux));
    }

    /*
     * Unable to fully structure code
     */
    @Nullable
    public static final <T> Object executeAndAwait(@NotNull TransactionalOperator var0, @NotNull Function2<? super ReactiveTransaction, ? super Continuation<? super T>, ? extends Object> var1_1, @NotNull Continuation<? super T> var2_2) {
        if (!(var2_2 instanceof executeAndAwait.1)) ** GOTO lbl-1000
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
                    return TransactionalOperatorExtensionsKt.executeAndAwait(null, null, this);
                }
            };
        }
        $result = $continuation.result;
        var5_5 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch ($continuation.label) {
            case 0: {
                ResultKt.throwOnFailure((Object)$result);
                v0 = $this$executeAndAwait.execute(new TransactionCallback((Function2)f){
                    final /* synthetic */ Function2 $f;

                    @NotNull
                    public final Mono<T> doInTransaction(@NotNull ReactiveTransaction status) {
                        Intrinsics.checkParameterIsNotNull((Object)status, (String)"status");
                        return MonoKt.mono((CoroutineContext)((CoroutineContext)Dispatchers.getUnconfined()), (Function2)((Function2)new Function2<CoroutineScope, Continuation<? super T>, Object>(this, status, null){
                            int label;
                            final /* synthetic */ executeAndAwait.2 this$0;
                            final /* synthetic */ ReactiveTransaction $status;

                            /*
                             * WARNING - void declaration
                             * Enabled force condition propagation
                             * Lifted jumps to return sites
                             */
                            @Nullable
                            public final Object invokeSuspend(@NotNull Object object) {
                                Object object2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                switch (this.label) {
                                    case 0: {
                                        ResultKt.throwOnFailure((Object)object);
                                        this.label = 1;
                                        Object object3 = this.this$0.$f.invoke((Object)this.$status, (Object)((Object)this));
                                        if (object3 != object2) return object3;
                                        return object2;
                                    }
                                    case 1: {
                                        void $result;
                                        ResultKt.throwOnFailure((Object)$result);
                                        Object object3 = $result;
                                        return object3;
                                    }
                                }
                                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                            }
                            {
                                this.this$0 = var1_1;
                                this.$status = reactiveTransaction;
                                super(2, continuation);
                            }

                            @NotNull
                            public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> completion) {
                                Intrinsics.checkParameterIsNotNull(completion, (String)"completion");
                                Function2<CoroutineScope, Continuation<? super T>, Object> function2 = new /* invalid duplicate definition of identical inner class */;
                                return function2;
                            }

                            public final Object invoke(Object object, Object object2) {
                                return (this.create(object, (Continuation)object2)).invokeSuspend(Unit.INSTANCE);
                            }
                        }));
                    }
                    {
                        this.$f = function2;
                    }
                }).map((Function)executeAndAwait.3.INSTANCE).defaultIfEmpty(Optional.empty());
                Intrinsics.checkExpressionValueIsNotNull((Object)v0, (String)"execute { status -> mono\u2026IfEmpty(Optional.empty())");
                $continuation.label = 1;
                v1 = AwaitKt.awaitLast((Publisher)((Publisher)v0), (Continuation)$continuation);
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

                return ((Optional)v1).orElse(null);
            }
        }
        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
    }
}

