/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.concurrent.ConcurrentUtils
 */
package org.apache.commons.configuration2.builder.combined;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.builder.BasicBuilderParameters;
import org.apache.commons.configuration2.builder.BasicConfigurationBuilder;
import org.apache.commons.configuration2.builder.BuilderParameters;
import org.apache.commons.configuration2.builder.ConfigurationBuilderEvent;
import org.apache.commons.configuration2.builder.ConfigurationBuilderResultCreatedEvent;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.combined.MultiFileBuilderParametersImpl;
import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.event.EventListenerList;
import org.apache.commons.configuration2.event.EventType;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;
import org.apache.commons.configuration2.interpol.InterpolatorSpecification;
import org.apache.commons.lang3.concurrent.ConcurrentUtils;

public class MultiFileConfigurationBuilder<T extends FileBasedConfiguration>
extends BasicConfigurationBuilder<T> {
    private static final String KEY_INTERPOLATOR = "interpolator";
    private final ConcurrentMap<String, FileBasedConfigurationBuilder<T>> managedBuilders = new ConcurrentHashMap<String, FileBasedConfigurationBuilder<T>>();
    private final AtomicReference<ConfigurationInterpolator> interpolator = new AtomicReference();
    private final ThreadLocal<Boolean> inInterpolation = new ThreadLocal();
    private final EventListenerList configurationListeners = new EventListenerList();
    private final EventListener<ConfigurationBuilderEvent> managedBuilderDelegationListener = this::handleManagedBuilderEvent;

    public MultiFileConfigurationBuilder(Class<? extends T> resCls, Map<String, Object> params, boolean allowFailOnInit) {
        super(resCls, params, allowFailOnInit);
    }

    public MultiFileConfigurationBuilder(Class<? extends T> resCls, Map<String, Object> params) {
        super(resCls, params);
    }

    public MultiFileConfigurationBuilder(Class<? extends T> resCls) {
        super(resCls);
    }

    @Override
    public MultiFileConfigurationBuilder<T> configure(BuilderParameters ... params) {
        super.configure(params);
        return this;
    }

    @Override
    public T getConfiguration() throws ConfigurationException {
        return (T)((FileBasedConfiguration)this.getManagedBuilder().getConfiguration());
    }

    public FileBasedConfigurationBuilder<T> getManagedBuilder() throws ConfigurationException {
        Map<String, Object> params = this.getParameters();
        MultiFileBuilderParametersImpl multiParams = MultiFileBuilderParametersImpl.fromParameters(params, true);
        if (multiParams.getFilePattern() == null) {
            throw new ConfigurationException("No file name pattern is set!");
        }
        String fileName = this.fetchFileName(multiParams);
        FileBasedConfigurationBuilder builder = (FileBasedConfigurationBuilder)this.getManagedBuilders().get(fileName);
        if (builder == null) {
            builder = this.createInitializedManagedBuilder(fileName, MultiFileConfigurationBuilder.createManagedBuilderParameters(params, multiParams));
            FileBasedConfigurationBuilder newBuilder = (FileBasedConfigurationBuilder)ConcurrentUtils.putIfAbsent(this.getManagedBuilders(), (Object)fileName, builder);
            if (newBuilder == builder) {
                this.initListeners(newBuilder);
            } else {
                builder = newBuilder;
            }
        }
        return builder;
    }

    @Override
    public synchronized <E extends Event> void addEventListener(EventType<E> eventType, EventListener<? super E> l) {
        super.addEventListener(eventType, l);
        if (MultiFileConfigurationBuilder.isEventTypeForManagedBuilders(eventType)) {
            this.getManagedBuilders().values().forEach(b -> b.addEventListener(eventType, l));
            this.configurationListeners.addEventListener(eventType, l);
        }
    }

    @Override
    public synchronized <E extends Event> boolean removeEventListener(EventType<E> eventType, EventListener<? super E> l) {
        boolean result = super.removeEventListener(eventType, l);
        if (MultiFileConfigurationBuilder.isEventTypeForManagedBuilders(eventType)) {
            this.getManagedBuilders().values().forEach(b -> b.removeEventListener(eventType, l));
            this.configurationListeners.removeEventListener(eventType, l);
        }
        return result;
    }

    @Override
    public synchronized void resetParameters() {
        this.getManagedBuilders().values().forEach(b -> b.removeEventListener(ConfigurationBuilderEvent.ANY, this.managedBuilderDelegationListener));
        this.getManagedBuilders().clear();
        this.interpolator.set(null);
        super.resetParameters();
    }

    protected ConfigurationInterpolator getInterpolator() {
        ConfigurationInterpolator result;
        boolean done;
        do {
            if ((result = this.interpolator.get()) != null) {
                done = true;
                continue;
            }
            result = this.createInterpolator();
            done = this.interpolator.compareAndSet(null, result);
        } while (!done);
        return result;
    }

    protected ConfigurationInterpolator createInterpolator() {
        InterpolatorSpecification spec = BasicBuilderParameters.fetchInterpolatorSpecification(this.getParameters());
        return ConfigurationInterpolator.fromSpecification(spec);
    }

    protected String constructFileName(MultiFileBuilderParametersImpl multiParams) {
        ConfigurationInterpolator ci = this.getInterpolator();
        return String.valueOf(ci.interpolate(multiParams.getFilePattern()));
    }

    protected FileBasedConfigurationBuilder<T> createManagedBuilder(String fileName, Map<String, Object> params) throws ConfigurationException {
        return new FileBasedConfigurationBuilder(this.getResultClass(), params, this.isAllowFailOnInit());
    }

    protected FileBasedConfigurationBuilder<T> createInitializedManagedBuilder(String fileName, Map<String, Object> params) throws ConfigurationException {
        FileBasedConfigurationBuilder<T> managedBuilder = this.createManagedBuilder(fileName, params);
        managedBuilder.getFileHandler().setFileName(fileName);
        return managedBuilder;
    }

    protected ConcurrentMap<String, FileBasedConfigurationBuilder<T>> getManagedBuilders() {
        return this.managedBuilders;
    }

    private void initListeners(FileBasedConfigurationBuilder<T> newBuilder) {
        this.copyEventListeners(newBuilder, this.configurationListeners);
        newBuilder.addEventListener(ConfigurationBuilderEvent.ANY, this.managedBuilderDelegationListener);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String fetchFileName(MultiFileBuilderParametersImpl multiParams) {
        String fileName;
        Boolean reentrant = this.inInterpolation.get();
        if (reentrant != null && reentrant.booleanValue()) {
            fileName = multiParams.getFilePattern();
        } else {
            this.inInterpolation.set(Boolean.TRUE);
            try {
                fileName = this.constructFileName(multiParams);
            }
            finally {
                this.inInterpolation.set(Boolean.FALSE);
            }
        }
        return fileName;
    }

    private void handleManagedBuilderEvent(ConfigurationBuilderEvent event) {
        if (ConfigurationBuilderEvent.RESET.equals(event.getEventType())) {
            this.resetResult();
        } else {
            this.fireBuilderEvent(this.createEventWithChangedSource(event));
        }
    }

    private ConfigurationBuilderEvent createEventWithChangedSource(ConfigurationBuilderEvent event) {
        if (ConfigurationBuilderResultCreatedEvent.RESULT_CREATED.equals(event.getEventType())) {
            return new ConfigurationBuilderResultCreatedEvent(this, ConfigurationBuilderResultCreatedEvent.RESULT_CREATED, ((ConfigurationBuilderResultCreatedEvent)event).getConfiguration());
        }
        EventType<? extends Event> type = event.getEventType();
        return new ConfigurationBuilderEvent(this, type);
    }

    private static Map<String, Object> createManagedBuilderParameters(Map<String, Object> params, MultiFileBuilderParametersImpl multiParams) {
        HashMap<String, Object> newParams = new HashMap<String, Object>(params);
        newParams.remove(KEY_INTERPOLATOR);
        BuilderParameters managedBuilderParameters = multiParams.getManagedBuilderParameters();
        if (managedBuilderParameters != null) {
            BuilderParameters copy = (BuilderParameters)ConfigurationUtils.cloneIfPossible(managedBuilderParameters);
            newParams.putAll(copy.getParameters());
        }
        return newParams;
    }

    private static boolean isEventTypeForManagedBuilders(EventType<?> eventType) {
        return !EventType.isInstanceOf(eventType, ConfigurationBuilderEvent.ANY);
    }
}

