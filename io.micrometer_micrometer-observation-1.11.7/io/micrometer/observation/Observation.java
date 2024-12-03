/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.KeyValue
 *  io.micrometer.common.KeyValues
 *  io.micrometer.common.lang.NonNull
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.observation;

import io.micrometer.common.KeyValue;
import io.micrometer.common.KeyValues;
import io.micrometer.common.lang.NonNull;
import io.micrometer.common.lang.Nullable;
import io.micrometer.observation.NoopObservation;
import io.micrometer.observation.NoopObservationConvention;
import io.micrometer.observation.ObservationConvention;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.ObservationView;
import io.micrometer.observation.SimpleObservation;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface Observation
extends ObservationView {
    public static final Observation NOOP = new NoopObservation();

    public static Observation start(String name, @Nullable ObservationRegistry registry) {
        return Observation.start(name, Context::new, registry);
    }

    public static <T extends Context> Observation start(String name, Supplier<T> contextSupplier, @Nullable ObservationRegistry registry) {
        return Observation.createNotStarted(name, contextSupplier, registry).start();
    }

    public static Observation createNotStarted(String name, @Nullable ObservationRegistry registry) {
        return Observation.createNotStarted(name, Context::new, registry);
    }

    public static <T extends Context> Observation createNotStarted(String name, Supplier<T> contextSupplier, @Nullable ObservationRegistry registry) {
        if (registry == null || registry.isNoop()) {
            return NOOP;
        }
        Context context = (Context)contextSupplier.get();
        if (!registry.observationConfig().isObservationEnabled(name, context)) {
            return NOOP;
        }
        return new SimpleObservation(name, registry, context == null ? new Context() : context);
    }

    public static <T extends Context> Observation createNotStarted(@Nullable ObservationConvention<T> customConvention, ObservationConvention<T> defaultConvention, Supplier<T> contextSupplier, @Nullable ObservationRegistry registry) {
        if (registry == null || registry.isNoop()) {
            return NOOP;
        }
        Context context = (Context)contextSupplier.get();
        ObservationConvention<Object> convention = customConvention != null ? customConvention : registry.observationConfig().getObservationConvention(context, defaultConvention);
        if (!registry.observationConfig().isObservationEnabled(convention.getName(), context)) {
            return NOOP;
        }
        return new SimpleObservation(convention, registry, context == null ? new Context() : context);
    }

    public static Observation start(ObservationConvention<Context> observationConvention, ObservationRegistry registry) {
        return Observation.start(observationConvention, Context::new, registry);
    }

    public static <T extends Context> Observation start(ObservationConvention<T> observationConvention, Supplier<T> contextSupplier, ObservationRegistry registry) {
        return Observation.createNotStarted(observationConvention, contextSupplier, registry).start();
    }

    public static <T extends Context> Observation start(@Nullable ObservationConvention<T> customConvention, ObservationConvention<T> defaultConvention, Supplier<T> contextSupplier, ObservationRegistry registry) {
        return Observation.createNotStarted(customConvention, defaultConvention, contextSupplier, registry).start();
    }

    public static Observation createNotStarted(ObservationConvention<Context> observationConvention, ObservationRegistry registry) {
        return Observation.createNotStarted(observationConvention, Context::new, registry);
    }

    public static <T extends Context> Observation createNotStarted(ObservationConvention<T> observationConvention, Supplier<T> contextSupplier, ObservationRegistry registry) {
        if (registry == null || registry.isNoop() || observationConvention == NoopObservationConvention.INSTANCE) {
            return NOOP;
        }
        Context context = (Context)contextSupplier.get();
        if (!registry.observationConfig().isObservationEnabled(observationConvention.getName(), context)) {
            return NOOP;
        }
        return new SimpleObservation(observationConvention, registry, context == null ? new Context() : context);
    }

    public Observation contextualName(@Nullable String var1);

    public Observation parentObservation(@Nullable Observation var1);

    public Observation lowCardinalityKeyValue(KeyValue var1);

    default public Observation lowCardinalityKeyValue(String key, String value) {
        return this.lowCardinalityKeyValue(KeyValue.of((String)key, (String)value));
    }

    default public Observation lowCardinalityKeyValues(KeyValues keyValues) {
        for (KeyValue keyValue : keyValues) {
            this.lowCardinalityKeyValue(keyValue);
        }
        return this;
    }

    public Observation highCardinalityKeyValue(KeyValue var1);

    default public Observation highCardinalityKeyValue(String key, String value) {
        return this.highCardinalityKeyValue(KeyValue.of((String)key, (String)value));
    }

    default public Observation highCardinalityKeyValues(KeyValues keyValues) {
        for (KeyValue keyValue : keyValues) {
            this.highCardinalityKeyValue(keyValue);
        }
        return this;
    }

    default public boolean isNoop() {
        return this == NOOP;
    }

    public Observation observationConvention(ObservationConvention<?> var1);

    public Observation error(Throwable var1);

    public Observation event(Event var1);

    public Observation start();

    public Context getContext();

    @Override
    default public ContextView getContextView() {
        return this.getContext();
    }

    public void stop();

    public Scope openScope();

    default public void observe(Runnable runnable) {
        this.observeWithContext(context -> {
            runnable.run();
            return null;
        });
    }

    default public Runnable wrap(Runnable runnable) {
        return () -> this.observe(runnable);
    }

    default public <E extends Throwable> void observeChecked(CheckedRunnable<E> checkedRunnable) throws E {
        this.observeCheckedWithContext(context -> {
            checkedRunnable.run();
            return null;
        });
    }

    default public <E extends Throwable> CheckedRunnable<E> wrapChecked(CheckedRunnable<E> checkedRunnable) throws E {
        return () -> this.observeChecked(checkedRunnable);
    }

    @Nullable
    default public <T> T observe(Supplier<T> supplier) {
        return (T)this.observeWithContext(context -> supplier.get());
    }

    default public <T> Supplier<T> wrap(Supplier<T> supplier) {
        return () -> this.observe(supplier);
    }

    @Nullable
    default public <T, E extends Throwable> T observeChecked(CheckedCallable<T, E> checkedCallable) throws E {
        return (T)this.observeCheckedWithContext(context -> checkedCallable.call());
    }

    default public <T, E extends Throwable> CheckedCallable<T, E> wrapChecked(CheckedCallable<T, E> checkedCallable) throws E {
        return () -> this.observeChecked(checkedCallable);
    }

    @Nullable
    default public <C extends Context, T> T observeWithContext(Function<C, T> function) {
        this.start();
        try {
            T t;
            block11: {
                Scope scope = this.openScope();
                try {
                    t = function.apply(this.getContext());
                    if (scope == null) break block11;
                    scope.close();
                }
                catch (Throwable throwable) {
                    try {
                        if (scope != null) {
                            try {
                                scope.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (Throwable error) {
                        this.error(error);
                        throw error;
                    }
                }
            }
            return t;
        }
        finally {
            this.stop();
        }
    }

    @Nullable
    default public <C extends Context, T, E extends Throwable> T observeCheckedWithContext(CheckedFunction<C, T, E> function) throws E {
        this.start();
        try {
            T t;
            block11: {
                Scope scope = this.openScope();
                try {
                    t = function.apply(this.getContext());
                    if (scope == null) break block11;
                    scope.close();
                }
                catch (Throwable throwable) {
                    try {
                        if (scope != null) {
                            try {
                                scope.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (Throwable error) {
                        this.error(error);
                        throw error;
                    }
                }
            }
            return t;
        }
        finally {
            this.stop();
        }
    }

    default public void scoped(Runnable runnable) {
        try (Scope scope = this.openScope();){
            runnable.run();
        }
        catch (Exception exception) {
            this.error(exception);
            throw exception;
        }
    }

    default public <E extends Throwable> void scopedChecked(CheckedRunnable<E> checkedRunnable) throws E {
        try (Scope scope = this.openScope();){
            checkedRunnable.run();
        }
        catch (Throwable throwable) {
            this.error(throwable);
            throw throwable;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    default public <T> T scoped(Supplier<T> supplier) {
        try (Scope scope = this.openScope();){
            T t = supplier.get();
            return t;
        }
        catch (Exception exception) {
            this.error(exception);
            throw exception;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    default public <T, E extends Throwable> T scopedChecked(CheckedCallable<T, E> checkedCallable) throws E {
        try (Scope scope = this.openScope();){
            T t = checkedCallable.call();
            return t;
        }
        catch (Throwable error) {
            this.error(error);
            throw error;
        }
    }

    public static void tryScoped(@Nullable Observation parent, Runnable action) {
        if (parent != null) {
            parent.scoped(action);
        } else {
            action.run();
        }
    }

    public static <E extends Throwable> void tryScopedChecked(@Nullable Observation parent, CheckedRunnable<E> checkedRunnable) throws E {
        if (parent != null) {
            parent.scopedChecked(checkedRunnable);
        } else {
            checkedRunnable.run();
        }
    }

    public static <T> T tryScoped(@Nullable Observation parent, Supplier<T> action) {
        if (parent != null) {
            return parent.scoped(action);
        }
        return action.get();
    }

    public static <T, E extends Throwable> T tryScopedChecked(@Nullable Observation parent, CheckedCallable<T, E> checkedCallable) throws E {
        if (parent != null) {
            return parent.scopedChecked(checkedCallable);
        }
        return checkedCallable.call();
    }

    public static class Context
    implements ContextView {
        private final Map<Object, Object> map = new ConcurrentHashMap<Object, Object>();
        private String name;
        @Nullable
        private String contextualName;
        @Nullable
        private Throwable error;
        @Nullable
        private ObservationView parentObservation;
        private final Map<String, KeyValue> lowCardinalityKeyValues = new LinkedHashMap<String, KeyValue>();
        private final Map<String, KeyValue> highCardinalityKeyValues = new LinkedHashMap<String, KeyValue>();

        @Override
        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String getContextualName() {
            return this.contextualName;
        }

        public void setContextualName(@Nullable String contextualName) {
            this.contextualName = contextualName;
        }

        @Override
        @Nullable
        public ObservationView getParentObservation() {
            return this.parentObservation;
        }

        public void setParentObservation(@Nullable ObservationView parentObservation) {
            this.parentObservation = parentObservation;
        }

        @Override
        @Nullable
        public Throwable getError() {
            return this.error;
        }

        public void setError(Throwable error) {
            this.error = error;
        }

        public <T> Context put(Object key, T object) {
            this.map.put(key, object);
            return this;
        }

        @Override
        @Nullable
        public <T> T get(Object key) {
            return (T)this.map.get(key);
        }

        public Object remove(Object key) {
            return this.map.remove(key);
        }

        @Override
        @NonNull
        public <T> T getRequired(Object key) {
            Object object = this.map.get(key);
            if (object == null) {
                throw new IllegalArgumentException("Context does not have an entry for key [" + key + "]");
            }
            return (T)object;
        }

        @Override
        public boolean containsKey(Object key) {
            return this.map.containsKey(key);
        }

        @Override
        public <T> T getOrDefault(Object key, T defaultObject) {
            return (T)this.map.getOrDefault(key, defaultObject);
        }

        public <T> T computeIfAbsent(Object key, Function<Object, ? extends T> mappingFunction) {
            return this.map.computeIfAbsent(key, mappingFunction);
        }

        public void clear() {
            this.map.clear();
        }

        public Context addLowCardinalityKeyValue(KeyValue keyValue) {
            this.lowCardinalityKeyValues.put(keyValue.getKey(), keyValue);
            return this;
        }

        public Context addHighCardinalityKeyValue(KeyValue keyValue) {
            this.highCardinalityKeyValues.put(keyValue.getKey(), keyValue);
            return this;
        }

        public Context removeLowCardinalityKeyValue(String keyName) {
            this.lowCardinalityKeyValues.remove(keyName);
            return this;
        }

        public Context removeHighCardinalityKeyValue(String keyName) {
            this.highCardinalityKeyValues.remove(keyName);
            return this;
        }

        public Context addLowCardinalityKeyValues(KeyValues keyValues) {
            for (KeyValue keyValue : keyValues) {
                this.addLowCardinalityKeyValue(keyValue);
            }
            return this;
        }

        public Context addHighCardinalityKeyValues(KeyValues keyValues) {
            for (KeyValue keyValue : keyValues) {
                this.addHighCardinalityKeyValue(keyValue);
            }
            return this;
        }

        public Context removeLowCardinalityKeyValues(String ... keyNames) {
            for (String keyName : keyNames) {
                this.removeLowCardinalityKeyValue(keyName);
            }
            return this;
        }

        public Context removeHighCardinalityKeyValues(String ... keyNames) {
            for (String keyName : keyNames) {
                this.removeHighCardinalityKeyValue(keyName);
            }
            return this;
        }

        @Override
        @NonNull
        public KeyValues getLowCardinalityKeyValues() {
            return KeyValues.of(this.lowCardinalityKeyValues.values());
        }

        @Override
        @NonNull
        public KeyValues getHighCardinalityKeyValues() {
            return KeyValues.of(this.highCardinalityKeyValues.values());
        }

        @Override
        public KeyValue getLowCardinalityKeyValue(String key) {
            return this.lowCardinalityKeyValues.get(key);
        }

        @Override
        public KeyValue getHighCardinalityKeyValue(String key) {
            return this.highCardinalityKeyValues.get(key);
        }

        @Override
        @NonNull
        public KeyValues getAllKeyValues() {
            return this.getLowCardinalityKeyValues().and((Iterable)this.getHighCardinalityKeyValues());
        }

        public String toString() {
            return "name='" + this.name + '\'' + ", contextualName='" + this.contextualName + '\'' + ", error='" + this.error + '\'' + ", lowCardinalityKeyValues=" + this.toString(this.getLowCardinalityKeyValues()) + ", highCardinalityKeyValues=" + this.toString(this.getHighCardinalityKeyValues()) + ", map=" + this.toString(this.map) + ", parentObservation=" + this.parentObservation;
        }

        private String toString(KeyValues keyValues) {
            return keyValues.stream().map(keyValue -> String.format("%s='%s'", keyValue.getKey(), keyValue.getValue())).collect(Collectors.joining(", ", "[", "]"));
        }

        private String toString(Map<Object, Object> map) {
            return map.entrySet().stream().map(entry -> String.format("%s='%s'", entry.getKey(), entry.getValue())).collect(Collectors.joining(", ", "[", "]"));
        }
    }

    @FunctionalInterface
    public static interface CheckedRunnable<E extends Throwable> {
        public void run() throws E;
    }

    @FunctionalInterface
    public static interface CheckedFunction<T, R, E extends Throwable> {
        @Nullable
        public R apply(T var1) throws E;
    }

    @FunctionalInterface
    public static interface CheckedCallable<T, E extends Throwable> {
        public T call() throws E;
    }

    public static interface Scope
    extends AutoCloseable {
        public static final Scope NOOP = NoopObservation.NoopScope.INSTANCE;

        public Observation getCurrentObservation();

        @Nullable
        default public Scope getPreviousObservationScope() {
            return null;
        }

        @Override
        public void close();

        public void reset();

        public void makeCurrent();

        default public boolean isNoop() {
            return this == NOOP;
        }
    }

    public static interface ContextView {
        public String getName();

        @Nullable
        public String getContextualName();

        @Nullable
        public ObservationView getParentObservation();

        @Nullable
        public Throwable getError();

        @Nullable
        public <T> T get(Object var1);

        @NonNull
        public <T> T getRequired(Object var1);

        public boolean containsKey(Object var1);

        public <T> T getOrDefault(Object var1, T var2);

        default public <T> T getOrDefault(Object key, Supplier<T> defaultObjectSupplier) {
            T value = this.get(key);
            return value != null ? value : defaultObjectSupplier.get();
        }

        public KeyValues getLowCardinalityKeyValues();

        @NonNull
        public KeyValues getHighCardinalityKeyValues();

        @Nullable
        public KeyValue getLowCardinalityKeyValue(String var1);

        @Nullable
        public KeyValue getHighCardinalityKeyValue(String var1);

        @NonNull
        public KeyValues getAllKeyValues();
    }

    public static interface Event {
        public static Event of(final String name, final String contextualName) {
            return new Event(){

                @Override
                public String getName() {
                    return name;
                }

                @Override
                public String getContextualName() {
                    return contextualName;
                }

                public String toString() {
                    return "event.name='" + this.getName() + "', event.contextualName='" + this.getContextualName() + '\'';
                }
            };
        }

        public static Event of(String name) {
            return Event.of(name, name);
        }

        public String getName();

        default public String getContextualName() {
            return this.getName();
        }

        default public Event format(Object ... dynamicEntriesForContextualName) {
            return Event.of(this.getName(), String.format(this.getContextualName(), dynamicEntriesForContextualName));
        }
    }
}

