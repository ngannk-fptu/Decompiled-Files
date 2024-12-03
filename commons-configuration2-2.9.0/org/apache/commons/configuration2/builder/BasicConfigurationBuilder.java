/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.configuration2.Initializable;
import org.apache.commons.configuration2.beanutils.BeanDeclaration;
import org.apache.commons.configuration2.beanutils.BeanHelper;
import org.apache.commons.configuration2.beanutils.ConstructorArg;
import org.apache.commons.configuration2.builder.BasicBuilderParameters;
import org.apache.commons.configuration2.builder.BuilderParameters;
import org.apache.commons.configuration2.builder.ConfigurationBuilder;
import org.apache.commons.configuration2.builder.ConfigurationBuilderEvent;
import org.apache.commons.configuration2.builder.ConfigurationBuilderResultCreatedEvent;
import org.apache.commons.configuration2.builder.EventListenerProvider;
import org.apache.commons.configuration2.builder.ReloadingBuilderSupportListener;
import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.event.EventListenerList;
import org.apache.commons.configuration2.event.EventListenerRegistrationData;
import org.apache.commons.configuration2.event.EventSource;
import org.apache.commons.configuration2.event.EventType;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.apache.commons.configuration2.reloading.ReloadingController;

public class BasicConfigurationBuilder<T extends ImmutableConfiguration>
implements ConfigurationBuilder<T> {
    private final Class<? extends T> resultClass;
    private final EventListenerList eventListeners;
    private final boolean allowFailOnInit;
    private Map<String, Object> parameters;
    private BeanDeclaration resultDeclaration;
    private volatile T result;

    public BasicConfigurationBuilder(Class<? extends T> resCls) {
        this(resCls, null);
    }

    public BasicConfigurationBuilder(Class<? extends T> resCls, Map<String, Object> params) {
        this(resCls, params, false);
    }

    public BasicConfigurationBuilder(Class<? extends T> resCls, Map<String, Object> params, boolean allowFailOnInit) {
        if (resCls == null) {
            throw new IllegalArgumentException("Result class must not be null!");
        }
        this.resultClass = resCls;
        this.allowFailOnInit = allowFailOnInit;
        this.eventListeners = new EventListenerList();
        this.updateParameters(params);
    }

    public Class<? extends T> getResultClass() {
        return this.resultClass;
    }

    public boolean isAllowFailOnInit() {
        return this.allowFailOnInit;
    }

    public synchronized BasicConfigurationBuilder<T> setParameters(Map<String, Object> params) {
        this.updateParameters(params);
        return this;
    }

    public synchronized BasicConfigurationBuilder<T> addParameters(Map<String, Object> params) {
        HashMap<String, Object> newParams = new HashMap<String, Object>(this.getParameters());
        if (params != null) {
            newParams.putAll(params);
        }
        this.updateParameters(newParams);
        return this;
    }

    public BasicConfigurationBuilder<T> configure(BuilderParameters ... params) {
        HashMap<String, Object> newParams = new HashMap<String, Object>();
        for (BuilderParameters p : params) {
            newParams.putAll(p.getParameters());
            this.handleEventListenerProviders(p);
        }
        return this.setParameters(newParams);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public T getConfiguration() throws ConfigurationException {
        this.fireBuilderEvent(new ConfigurationBuilderEvent(this, ConfigurationBuilderEvent.CONFIGURATION_REQUEST));
        T resObj = this.result;
        boolean created = false;
        if (resObj == null) {
            BasicConfigurationBuilder basicConfigurationBuilder = this;
            synchronized (basicConfigurationBuilder) {
                resObj = this.result;
                if (resObj == null) {
                    this.result = resObj = this.createResult();
                    created = true;
                }
            }
        }
        if (created) {
            this.fireBuilderEvent(new ConfigurationBuilderResultCreatedEvent(this, (EventType<? extends ConfigurationBuilderResultCreatedEvent>)ConfigurationBuilderResultCreatedEvent.RESULT_CREATED, (ImmutableConfiguration)resObj));
        }
        return resObj;
    }

    public <E extends Event> void addEventListener(EventType<E> eventType, EventListener<? super E> listener) {
        this.installEventListener(eventType, listener);
    }

    public <E extends Event> boolean removeEventListener(EventType<E> eventType, EventListener<? super E> listener) {
        this.fetchEventSource().removeEventListener(eventType, listener);
        return this.eventListeners.removeEventListener(eventType, listener);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void resetResult() {
        T oldResult;
        BasicConfigurationBuilder basicConfigurationBuilder = this;
        synchronized (basicConfigurationBuilder) {
            oldResult = this.result;
            this.result = null;
            this.resultDeclaration = null;
        }
        if (oldResult != null) {
            this.removeEventListeners(oldResult);
        }
        this.fireBuilderEvent(new ConfigurationBuilderEvent(this, ConfigurationBuilderEvent.RESET));
    }

    public void resetParameters() {
        this.setParameters(null);
    }

    public synchronized void reset() {
        this.resetParameters();
        this.resetResult();
    }

    public final void connectToReloadingController(ReloadingController controller) {
        if (controller == null) {
            throw new IllegalArgumentException("ReloadingController must not be null!");
        }
        ReloadingBuilderSupportListener.connect(this, controller);
    }

    protected T createResult() throws ConfigurationException {
        T resObj;
        block2: {
            resObj = this.createResultInstance();
            try {
                this.initResultInstance(resObj);
            }
            catch (ConfigurationException cex) {
                if (this.isAllowFailOnInit()) break block2;
                throw cex;
            }
        }
        return resObj;
    }

    protected T createResultInstance() throws ConfigurationException {
        Object bean = this.fetchBeanHelper().createBean(this.getResultDeclaration());
        this.checkResultInstance(bean);
        return (T)((ImmutableConfiguration)this.getResultClass().cast(bean));
    }

    protected void initResultInstance(T obj) throws ConfigurationException {
        this.fetchBeanHelper().initBean(obj, this.getResultDeclaration());
        this.registerEventListeners(obj);
        this.handleInitializable(obj);
    }

    protected final synchronized BeanDeclaration getResultDeclaration() throws ConfigurationException {
        if (this.resultDeclaration == null) {
            this.resultDeclaration = this.createResultDeclaration(this.getFilteredParameters());
        }
        return this.resultDeclaration;
    }

    protected final synchronized Map<String, Object> getParameters() {
        if (this.parameters != null) {
            return this.parameters;
        }
        return Collections.emptyMap();
    }

    protected final BeanHelper fetchBeanHelper() {
        BeanHelper helper = BasicBuilderParameters.fetchBeanHelper(this.getParameters());
        return helper != null ? helper : BeanHelper.INSTANCE;
    }

    protected BeanDeclaration createResultDeclaration(final Map<String, Object> params) throws ConfigurationException {
        return new BeanDeclaration(){

            @Override
            public Map<String, Object> getNestedBeanDeclarations() {
                return Collections.emptyMap();
            }

            @Override
            public Collection<ConstructorArg> getConstructorArgs() {
                return Collections.emptySet();
            }

            @Override
            public Map<String, Object> getBeanProperties() {
                return params;
            }

            @Override
            public Object getBeanFactoryParameter() {
                return null;
            }

            @Override
            public String getBeanFactoryName() {
                return null;
            }

            @Override
            public String getBeanClassName() {
                return BasicConfigurationBuilder.this.getResultClass().getName();
            }
        };
    }

    protected synchronized void copyEventListeners(BasicConfigurationBuilder<?> target) {
        this.copyEventListeners(target, this.eventListeners);
    }

    protected void copyEventListeners(BasicConfigurationBuilder<?> target, EventListenerList listeners) {
        target.eventListeners.addAll(listeners);
    }

    protected final <E extends Event> void installEventListener(EventType<E> eventType, EventListener<? super E> listener) {
        this.fetchEventSource().addEventListener(eventType, listener);
        this.eventListeners.addEventListener(eventType, listener);
    }

    protected void fireBuilderEvent(ConfigurationBuilderEvent event) {
        this.eventListeners.fire(event);
    }

    private void updateParameters(Map<String, Object> newParams) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        if (newParams != null) {
            map.putAll(newParams);
        }
        this.parameters = Collections.unmodifiableMap(map);
    }

    private void registerEventListeners(T obj) {
        EventSource evSrc = ConfigurationUtils.asEventSource(obj, true);
        this.eventListeners.getRegistrations().forEach(regData -> BasicConfigurationBuilder.registerListener(evSrc, regData));
    }

    private void removeEventListeners(T obj) {
        EventSource evSrc = ConfigurationUtils.asEventSource(obj, true);
        this.eventListeners.getRegistrations().forEach(regData -> BasicConfigurationBuilder.removeListener(evSrc, regData));
    }

    private EventSource fetchEventSource() {
        return ConfigurationUtils.asEventSource(this.result, true);
    }

    private void handleEventListenerProviders(BuilderParameters params) {
        if (params instanceof EventListenerProvider) {
            this.eventListeners.addAll(((EventListenerProvider)((Object)params)).getListeners());
        }
    }

    private void checkResultInstance(Object inst) {
        if (!this.getResultClass().isInstance(inst)) {
            throw new ConfigurationRuntimeException("Incompatible result object: " + inst);
        }
    }

    private Map<String, Object> getFilteredParameters() {
        HashMap<String, Object> filteredMap = new HashMap<String, Object>(this.getParameters());
        filteredMap.keySet().removeIf(key -> key.startsWith("config-"));
        return filteredMap;
    }

    private void handleInitializable(T obj) {
        if (obj instanceof Initializable) {
            ((Initializable)obj).initialize();
        }
    }

    private static <E extends Event> void registerListener(EventSource evSrc, EventListenerRegistrationData<E> regData) {
        evSrc.addEventListener(regData.getEventType(), regData.getListener());
    }

    private static <E extends Event> void removeListener(EventSource evSrc, EventListenerRegistrationData<E> regData) {
        evSrc.removeEventListener(regData.getEventType(), regData.getListener());
    }
}

