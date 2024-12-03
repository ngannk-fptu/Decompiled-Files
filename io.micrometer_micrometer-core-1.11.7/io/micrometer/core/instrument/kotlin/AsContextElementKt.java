/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.context.ContextRegistry
 *  io.micrometer.observation.Observation
 *  io.micrometer.observation.ObservationRegistry
 *  kotlin.Metadata
 *  kotlin.coroutines.CoroutineContext
 *  kotlin.coroutines.CoroutineContext$Key
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.micrometer.core.instrument.kotlin;

import io.micrometer.context.ContextRegistry;
import io.micrometer.core.instrument.kotlin.KotlinObservationContextElement;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import kotlin.Metadata;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 7, 1}, k=2, xi=48, d1={"\u0000\u0012\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\n\u0010\u0000\u001a\u00020\u0001*\u00020\u0002\u001a\f\u0010\u0003\u001a\u0004\u0018\u00010\u0004*\u00020\u0001\u00a8\u0006\u0005"}, d2={"asContextElement", "Lkotlin/coroutines/CoroutineContext;", "Lio/micrometer/observation/ObservationRegistry;", "currentObservation", "Lio/micrometer/observation/Observation;", "micrometer-core"})
public final class AsContextElementKt {
    @NotNull
    public static final CoroutineContext asContextElement(@NotNull ObservationRegistry $this$asContextElement) {
        Intrinsics.checkNotNullParameter((Object)$this$asContextElement, (String)"<this>");
        return (CoroutineContext)new KotlinObservationContextElement($this$asContextElement, ContextRegistry.getInstance());
    }

    @Nullable
    public static final Observation currentObservation(@NotNull CoroutineContext $this$currentObservation) {
        Intrinsics.checkNotNullParameter((Object)$this$currentObservation, (String)"<this>");
        CoroutineContext.Key<KotlinObservationContextElement> key = KotlinObservationContextElement.KEY;
        Intrinsics.checkNotNullExpressionValue(key, (String)"KEY");
        KotlinObservationContextElement element = (KotlinObservationContextElement)$this$currentObservation.get(key);
        if (element != null) {
            return element.getCurrentObservation();
        }
        return null;
    }
}

