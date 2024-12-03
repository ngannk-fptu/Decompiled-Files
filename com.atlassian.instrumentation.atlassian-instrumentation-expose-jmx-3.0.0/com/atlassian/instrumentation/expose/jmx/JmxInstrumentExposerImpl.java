/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.instrumentation.Counter
 *  com.atlassian.instrumentation.Gauge
 *  com.atlassian.instrumentation.Instrument
 *  com.atlassian.instrumentation.InstrumentRegistry
 *  com.atlassian.instrumentation.caches.CacheInstrument
 *  com.atlassian.instrumentation.operations.OpInstrument
 *  com.atlassian.instrumentation.utils.dbc.Assertions
 *  org.osgi.framework.BundleContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.instrumentation.expose.jmx;

import com.atlassian.instrumentation.Counter;
import com.atlassian.instrumentation.Gauge;
import com.atlassian.instrumentation.Instrument;
import com.atlassian.instrumentation.InstrumentRegistry;
import com.atlassian.instrumentation.caches.CacheInstrument;
import com.atlassian.instrumentation.expose.jmx.InstrumentMXBeanImpl;
import com.atlassian.instrumentation.expose.jmx.JmxInstrumentExposer;
import com.atlassian.instrumentation.expose.jmx.JmxInstrumentNamer;
import com.atlassian.instrumentation.operations.OpInstrument;
import com.atlassian.instrumentation.utils.dbc.Assertions;
import com.atlassian.pocketknife.api.lifecycle.services.OptionalService;
import com.atlassian.pocketknife.spi.lifecycle.services.OptionalServiceAccessor;
import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmxInstrumentExposerImpl
implements JmxInstrumentExposer {
    private final Map<String, InstrumentMXBeanImpl> seenInstruments;
    private final JmxInstrumentNamer jmxInstrumentNamer;
    private final OptionalServiceAccessor<InstrumentRegistry> instrumentRegistryServiceAccessor;
    private static final Logger log = LoggerFactory.getLogger(JmxInstrumentExposerImpl.class);

    public JmxInstrumentExposerImpl(JmxInstrumentNamer jmxInstrumentNamer, BundleContext bundleContext) {
        this.jmxInstrumentNamer = (JmxInstrumentNamer)Assertions.notNull((String)"jmxInstrumentNamer", (Object)jmxInstrumentNamer);
        this.instrumentRegistryServiceAccessor = new OptionalServiceAccessor((BundleContext)Assertions.notNull((String)"bundleContext", (Object)bundleContext), InstrumentRegistry.class.getName());
        this.seenInstruments = new ConcurrentHashMap<String, InstrumentMXBeanImpl>();
    }

    @Override
    public void exposePeriodically() {
        MBeanServer platformMBean = this.getPlatformMBean();
        try (OptionalService<InstrumentRegistry> optionalService = this.instrumentRegistryServiceAccessor.obtain();){
            if (optionalService.isAvailable()) {
                HashSet<String> registryNames = new HashSet<String>();
                for (InstrumentRegistry instrumentRegistry : optionalService.getAll()) {
                    String registryName = instrumentRegistry.getRegistryConfiguration().getRegistryName();
                    if (!registryNames.add(registryName)) {
                        log.warn("Found exported InstrumentRegistry with duplicate registry name '" + registryName + "'");
                    }
                    List instruments = instrumentRegistry.snapshotInstruments();
                    for (Instrument instrument : instruments) {
                        if (this.seenInstruments.containsKey(instrument.getName())) continue;
                        try {
                            this.registerInstrumentWithJMX(instrument, instrumentRegistry, platformMBean);
                        }
                        catch (MBeanRegistrationException | NotCompliantMBeanException e) {
                            log.error("Unable to register instrument '" + instrument.getName() + "' with JMX", (Throwable)e);
                        }
                    }
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void deregister() {
        try {
            for (InstrumentMXBeanImpl instrumentMBean : this.seenInstruments.values()) {
                try {
                    this.getPlatformMBean().unregisterMBean(instrumentMBean.objectName());
                }
                catch (InstanceNotFoundException | MBeanRegistrationException e) {
                    log.error("Unable to register instrument '" + instrumentMBean.getInstrument().getName() + "' with JMX", (Throwable)e);
                }
            }
        }
        finally {
            this.seenInstruments.clear();
        }
    }

    private void registerInstrumentWithJMX(Instrument instrument, InstrumentRegistry instrumentRegistry, MBeanServer platformMBean) throws MBeanRegistrationException, NotCompliantMBeanException {
        String instrumentName = instrument.getName();
        ObjectName objectName = this.makeObjectName(instrument, instrumentRegistry);
        InstrumentMXBeanImpl instrumentMBean = instrument instanceof Counter ? new InstrumentMXBeanImpl.CounterMXBeanImpl(instrumentRegistry, objectName, instrumentName) : (instrument instanceof Gauge ? new InstrumentMXBeanImpl.GaugeMXBeanImpl(instrumentRegistry, objectName, instrumentName) : (instrument instanceof OpInstrument ? new InstrumentMXBeanImpl.OpInstrumentMXBeanImpl(instrumentRegistry, objectName, instrumentName) : (instrument instanceof CacheInstrument ? new InstrumentMXBeanImpl.CacheInstrumentMXBeanImpl(instrumentRegistry, objectName, instrumentName) : new InstrumentMXBeanImpl(instrumentRegistry, objectName, instrumentName))));
        try {
            platformMBean.registerMBean(instrumentMBean, instrumentMBean.objectName());
            this.seenInstruments.put(instrumentName, instrumentMBean);
            log.info("Registered instrument '" + instrument.getName() + "' with JMX");
        }
        catch (InstanceAlreadyExistsException e) {
            this.seenInstruments.put(instrumentName, instrumentMBean);
        }
    }

    private MBeanServer getPlatformMBean() {
        return ManagementFactory.getPlatformMBeanServer();
    }

    private ObjectName makeObjectName(Instrument instrument, InstrumentRegistry instrumentRegistry) {
        try {
            return this.jmxInstrumentNamer.getObjectName(instrument, instrumentRegistry);
        }
        catch (MalformedObjectNameException e) {
            throw new RuntimeException(e);
        }
    }
}

