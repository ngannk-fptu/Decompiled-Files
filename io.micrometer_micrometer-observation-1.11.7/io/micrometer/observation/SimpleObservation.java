/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.KeyValue
 *  io.micrometer.common.lang.Nullable
 *  io.micrometer.common.util.StringUtils
 *  io.micrometer.common.util.internal.logging.InternalLogger
 *  io.micrometer.common.util.internal.logging.InternalLoggerFactory
 */
package io.micrometer.observation;

import io.micrometer.common.KeyValue;
import io.micrometer.common.lang.Nullable;
import io.micrometer.common.util.StringUtils;
import io.micrometer.common.util.internal.logging.InternalLogger;
import io.micrometer.common.util.internal.logging.InternalLoggerFactory;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationConvention;
import io.micrometer.observation.ObservationFilter;
import io.micrometer.observation.ObservationHandler;
import io.micrometer.observation.ObservationRegistry;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class SimpleObservation
implements Observation {
    final ObservationRegistry registry;
    private final Observation.Context context;
    @Nullable
    private ObservationConvention convention;
    private final Deque<ObservationHandler> handlers;
    private final Collection<ObservationFilter> filters;
    final Map<Thread, Observation.Scope> lastScope = new ConcurrentHashMap<Thread, Observation.Scope>();

    SimpleObservation(@Nullable String name, ObservationRegistry registry, Observation.Context context) {
        this.registry = registry;
        this.context = context;
        this.context.setName(name);
        this.convention = SimpleObservation.getConventionFromConfig(registry, context);
        this.handlers = SimpleObservation.getHandlersFromConfig(registry, context);
        this.filters = registry.observationConfig().getObservationFilters();
        this.setParentFromCurrentObservation();
    }

    SimpleObservation(ObservationConvention<? extends Observation.Context> convention, ObservationRegistry registry, Observation.Context context) {
        this.registry = registry;
        this.context = context;
        this.handlers = SimpleObservation.getHandlersFromConfig(registry, context);
        this.filters = registry.observationConfig().getObservationFilters();
        if (!convention.supportsContext(context)) {
            throw new IllegalStateException("Convention [" + convention + "] doesn't support context [" + context + "]");
        }
        this.convention = convention;
        this.setParentFromCurrentObservation();
    }

    private void setParentFromCurrentObservation() {
        Observation currentObservation = this.registry.getCurrentObservation();
        if (currentObservation != null) {
            this.context.setParentObservation(currentObservation);
        }
    }

    @Nullable
    private static ObservationConvention getConventionFromConfig(ObservationRegistry registry, Observation.Context context) {
        for (ObservationConvention<?> convention : registry.observationConfig().getObservationConventions()) {
            if (!convention.supportsContext(context)) continue;
            return convention;
        }
        return null;
    }

    private static Deque<ObservationHandler> getHandlersFromConfig(ObservationRegistry registry, Observation.Context context) {
        Collection<ObservationHandler<?>> handlers = registry.observationConfig().getObservationHandlers();
        ArrayDeque<ObservationHandler> deque = new ArrayDeque<ObservationHandler>(handlers.size());
        for (ObservationHandler<?> handler : handlers) {
            if (!handler.supportsContext(context)) continue;
            deque.add(handler);
        }
        return deque;
    }

    @Override
    public Observation contextualName(@Nullable String contextualName) {
        this.context.setContextualName(contextualName);
        return this;
    }

    @Override
    public Observation parentObservation(@Nullable Observation parentObservation) {
        this.context.setParentObservation(parentObservation);
        return this;
    }

    @Override
    public Observation lowCardinalityKeyValue(KeyValue keyValue) {
        this.context.addLowCardinalityKeyValue(keyValue);
        return this;
    }

    @Override
    public Observation highCardinalityKeyValue(KeyValue keyValue) {
        this.context.addHighCardinalityKeyValue(keyValue);
        return this;
    }

    @Override
    public Observation observationConvention(ObservationConvention<?> convention) {
        if (convention.supportsContext(this.context)) {
            this.convention = convention;
        }
        return this;
    }

    @Override
    public Observation error(Throwable error) {
        this.context.setError(error);
        this.notifyOnError();
        return this;
    }

    @Override
    public Observation event(Observation.Event event) {
        this.notifyOnEvent(event);
        return this;
    }

    @Override
    public Observation start() {
        if (this.convention != null) {
            this.context.addLowCardinalityKeyValues(this.convention.getLowCardinalityKeyValues(this.context));
            this.context.addHighCardinalityKeyValues(this.convention.getHighCardinalityKeyValues(this.context));
            String newName = this.convention.getName();
            if (StringUtils.isNotBlank((String)newName)) {
                this.context.setName(newName);
            }
        }
        this.notifyOnObservationStarted();
        return this;
    }

    @Override
    public Observation.Context getContext() {
        return this.context;
    }

    @Override
    public void stop() {
        if (this.convention != null) {
            this.context.addLowCardinalityKeyValues(this.convention.getLowCardinalityKeyValues(this.context));
            this.context.addHighCardinalityKeyValues(this.convention.getHighCardinalityKeyValues(this.context));
            String newContextualName = this.convention.getContextualName(this.context);
            if (StringUtils.isNotBlank((String)newContextualName)) {
                this.context.setContextualName(newContextualName);
            }
        }
        Observation.Context modifiedContext = this.context;
        for (ObservationFilter filter : this.filters) {
            modifiedContext = filter.map(modifiedContext);
        }
        this.notifyOnObservationStopped(modifiedContext);
    }

    @Override
    public Observation.Scope openScope() {
        SimpleScope scope = new SimpleScope(this.registry, this);
        this.notifyOnScopeOpened();
        this.lastScope.put(Thread.currentThread(), scope);
        return scope;
    }

    @Override
    @Nullable
    public Observation.Scope getEnclosingScope() {
        return this.lastScope.get(Thread.currentThread());
    }

    public String toString() {
        return "{name=" + this.context.getName() + "(" + this.context.getContextualName() + "), error=" + this.context.getError() + ", context=" + this.context + '}';
    }

    void notifyOnObservationStarted() {
        for (ObservationHandler handler : this.handlers) {
            handler.onStart(this.context);
        }
    }

    void notifyOnError() {
        for (ObservationHandler handler : this.handlers) {
            handler.onError(this.context);
        }
    }

    void notifyOnEvent(Observation.Event event) {
        for (ObservationHandler handler : this.handlers) {
            handler.onEvent(event, this.context);
        }
    }

    void notifyOnScopeOpened() {
        for (ObservationHandler handler : this.handlers) {
            handler.onScopeOpened(this.context);
        }
    }

    void notifyOnScopeClosed() {
        Iterator<ObservationHandler> iterator = this.handlers.descendingIterator();
        while (iterator.hasNext()) {
            ObservationHandler handler = iterator.next();
            handler.onScopeClosed(this.context);
        }
    }

    void notifyOnScopeMakeCurrent() {
        for (ObservationHandler handler : this.handlers) {
            handler.onScopeOpened(this.context);
        }
    }

    void notifyOnScopeReset() {
        for (ObservationHandler handler : this.handlers) {
            handler.onScopeReset(this.context);
        }
    }

    void notifyOnObservationStopped(Observation.Context context) {
        this.handlers.descendingIterator().forEachRemaining(handler -> handler.onStop(context));
    }

    @Override
    public ObservationRegistry getObservationRegistry() {
        return this.registry;
    }

    static class SimpleScope
    implements Observation.Scope {
        private static final InternalLogger log = InternalLoggerFactory.getInstance(SimpleScope.class);
        final ObservationRegistry registry;
        private final Observation currentObservation;
        @Nullable
        final Observation.Scope previousObservationScope;

        SimpleScope(ObservationRegistry registry, Observation current) {
            this.registry = registry;
            this.currentObservation = current;
            this.previousObservationScope = registry.getCurrentObservationScope();
            this.registry.setCurrentObservationScope(this);
        }

        @Override
        public Observation getCurrentObservation() {
            return this.currentObservation;
        }

        @Override
        public void close() {
            if (this.currentObservation instanceof SimpleObservation) {
                SimpleObservation observation = (SimpleObservation)this.currentObservation;
                SimpleScope lastScopeForThisObservation = this.getLastScope(this);
                if (lastScopeForThisObservation != null) {
                    observation.lastScope.put(Thread.currentThread(), lastScopeForThisObservation);
                } else {
                    observation.lastScope.remove(Thread.currentThread());
                }
                observation.notifyOnScopeClosed();
            } else if (this.currentObservation != null && !this.currentObservation.isNoop()) {
                log.debug("Custom observation type was used in combination with SimpleScope - that's unexpected");
            } else {
                log.trace("NoOp observation used with SimpleScope");
            }
            this.registry.setCurrentObservationScope(this.previousObservationScope);
        }

        @Nullable
        private SimpleScope getLastScope(SimpleScope simpleScope) {
            SimpleScope scope = simpleScope;
            while ((scope = (SimpleScope)scope.previousObservationScope) != null && !this.currentObservation.equals(scope.currentObservation)) {
            }
            return scope;
        }

        @Override
        public void reset() {
            SimpleScope scope = this;
            if (scope.currentObservation instanceof SimpleObservation) {
                SimpleObservation simpleObservation = (SimpleObservation)scope.currentObservation;
                do {
                    simpleObservation.notifyOnScopeReset();
                } while ((scope = (SimpleScope)scope.previousObservationScope) != null);
            }
            this.registry.setCurrentObservationScope(null);
        }

        @Override
        public void makeCurrent() {
            SimpleScope scope = this;
            do {
                if (!(scope.currentObservation instanceof SimpleObservation)) continue;
                ((SimpleObservation)scope.currentObservation).notifyOnScopeReset();
            } while ((scope = (SimpleScope)scope.previousObservationScope) != null);
            ArrayDeque<SimpleScope> scopes = new ArrayDeque<SimpleScope>();
            scope = this;
            do {
                scopes.addFirst(scope);
            } while ((scope = (SimpleScope)scope.previousObservationScope) != null);
            for (SimpleScope simpleScope : scopes) {
                if (!(simpleScope.currentObservation instanceof SimpleObservation)) continue;
                ((SimpleObservation)simpleScope.currentObservation).notifyOnScopeMakeCurrent();
            }
            this.registry.setCurrentObservationScope(this);
        }

        @Override
        @Nullable
        public Observation.Scope getPreviousObservationScope() {
            return this.previousObservationScope;
        }
    }
}

