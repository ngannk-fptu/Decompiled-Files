/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 *  io.micrometer.context.ContextRegistry
 *  io.micrometer.context.ContextSnapshot
 *  io.micrometer.context.ContextSnapshot$Scope
 *  io.micrometer.context.ContextSnapshotFactory
 *  io.micrometer.observation.Observation
 *  io.micrometer.observation.ObservationRegistry
 *  kotlin.coroutines.CoroutineContext
 *  kotlin.coroutines.CoroutineContext$DefaultImpls
 *  kotlin.coroutines.CoroutineContext$Element
 *  kotlin.coroutines.CoroutineContext$Element$DefaultImpls
 *  kotlin.coroutines.CoroutineContext$Key
 *  kotlin.jvm.functions.Function2
 *  kotlinx.coroutines.ThreadContextElement
 */
package io.micrometer.core.instrument.kotlin;

import io.micrometer.common.lang.Nullable;
import io.micrometer.context.ContextRegistry;
import io.micrometer.context.ContextSnapshot;
import io.micrometer.context.ContextSnapshotFactory;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.ThreadContextElement;

class KotlinObservationContextElement
implements ThreadContextElement<ContextSnapshot.Scope> {
    static final CoroutineContext.Key<KotlinObservationContextElement> KEY = new CoroutineContext.Key<KotlinObservationContextElement>(){};
    private final ObservationRegistry observationRegistry;
    private final ContextSnapshot contextSnapshot;

    KotlinObservationContextElement(ObservationRegistry observationRegistry, ContextRegistry contextRegistry) {
        this.observationRegistry = observationRegistry;
        this.contextSnapshot = ContextSnapshotFactory.builder().captureKeyPredicate("micrometer.observation"::equals).contextRegistry(contextRegistry).build().captureAll(new Object[0]);
    }

    public CoroutineContext.Key<?> getKey() {
        return KEY;
    }

    Observation getCurrentObservation() {
        return this.observationRegistry.getCurrentObservation();
    }

    public ContextSnapshot.Scope updateThreadContext(CoroutineContext coroutineContext) {
        return this.contextSnapshot.setThreadLocals("micrometer.observation"::equals);
    }

    public void restoreThreadContext(CoroutineContext coroutineContext, ContextSnapshot.Scope scope) {
        scope.close();
    }

    public CoroutineContext plus(CoroutineContext coroutineContext) {
        return CoroutineContext.DefaultImpls.plus((CoroutineContext)this, (CoroutineContext)coroutineContext);
    }

    public <R> R fold(R initial, Function2<? super R, ? super CoroutineContext.Element, ? extends R> operation) {
        return (R)CoroutineContext.Element.DefaultImpls.fold((CoroutineContext.Element)this, initial, operation);
    }

    @Nullable
    public <E extends CoroutineContext.Element> E get(CoroutineContext.Key<E> key) {
        return (E)CoroutineContext.Element.DefaultImpls.get((CoroutineContext.Element)this, key);
    }

    public CoroutineContext minusKey(CoroutineContext.Key<?> key) {
        return CoroutineContext.Element.DefaultImpls.minusKey((CoroutineContext.Element)this, key);
    }
}

