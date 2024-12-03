/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.instrumentation.Instrument
 *  com.atlassian.instrumentation.InstrumentRegistry
 *  com.atlassian.instrumentation.caches.CacheInstrument
 *  com.atlassian.instrumentation.operations.OpInstrument
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.instrumentation.expose.jmx;

import com.atlassian.instrumentation.Instrument;
import com.atlassian.instrumentation.InstrumentRegistry;
import com.atlassian.instrumentation.caches.CacheInstrument;
import com.atlassian.instrumentation.expose.jmx.InstrumentMXBean;
import com.atlassian.instrumentation.operations.OpInstrument;
import java.util.concurrent.TimeUnit;
import javax.management.ObjectName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstrumentMXBeanImpl
implements InstrumentMXBean {
    private static final Logger log = LoggerFactory.getLogger(InstrumentMXBeanImpl.class);
    final ObjectName objectName;
    final String name;
    final InstrumentRegistry instrumentRegistry;

    public InstrumentMXBeanImpl(InstrumentRegistry instrumentRegistry, ObjectName objectName, String name) {
        this.instrumentRegistry = instrumentRegistry;
        this.objectName = objectName;
        this.name = name;
    }

    ObjectName objectName() {
        return this.objectName;
    }

    long value() {
        return (Long)new JmxVal<Long>(){

            @Override
            Long value() {
                return InstrumentMXBeanImpl.this.getInstrument().getValue();
            }
        }.get();
    }

    Instrument getInstrument() {
        return this.instrumentRegistry.getInstrument(this.name);
    }

    static abstract class JmxVal<T> {
        JmxVal() {
        }

        abstract T value();

        public T get() {
            try {
                return this.value();
            }
            catch (RuntimeException rte) {
                log.error("Unable to get Instrumentation JMX value", (Throwable)rte);
                throw rte;
            }
        }
    }

    public static class CacheInstrumentMXBeanImpl
    extends InstrumentMXBeanImpl
    implements InstrumentMXBean.CacheInstrumentMXBean {
        public CacheInstrumentMXBeanImpl(InstrumentRegistry instrumentRegistry, ObjectName objectName, String name) {
            super(instrumentRegistry, objectName, name);
        }

        private CacheInstrument cacheInstrument() {
            return (CacheInstrument)new JmxVal<CacheInstrument>(){

                @Override
                CacheInstrument value() {
                    return (CacheInstrument)this.getInstrument();
                }
            }.get();
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public long getMisses() {
            return this.cacheInstrument().getMisses();
        }

        @Override
        public long getMissTime() {
            return this.cacheInstrument().getMissTime();
        }

        @Override
        public long getHits() {
            return this.cacheInstrument().getHits();
        }

        @Override
        public long getCacheSize() {
            return this.cacheInstrument().getCacheSize();
        }

        @Override
        public double getHitMissRatio() {
            return this.cacheInstrument().getHitMissRatio();
        }
    }

    public static class OpInstrumentMXBeanImpl
    extends InstrumentMXBeanImpl
    implements InstrumentMXBean.OpInstrumentMXBean {
        public OpInstrumentMXBeanImpl(InstrumentRegistry instrumentRegistry, ObjectName objectName, String opName) {
            super(instrumentRegistry, objectName, opName);
        }

        private OpInstrument opInstrument() {
            return (OpInstrument)new JmxVal<OpInstrument>(){

                @Override
                OpInstrument value() {
                    return (OpInstrument)this.getInstrument();
                }
            }.get();
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public long getInvocationCount() {
            return this.opInstrument().getInvocationCount();
        }

        @Override
        public long getMillisecondsTaken() {
            return this.opInstrument().getMillisecondsTaken();
        }

        @Override
        public long getElapsedTotalTime() {
            return this.opInstrument().getElapsedTotalTime(TimeUnit.NANOSECONDS);
        }

        @Override
        public long getElapsedMinTime() {
            return this.opInstrument().getElapsedMinTime(TimeUnit.NANOSECONDS);
        }

        @Override
        public long getElapsedMaxTime() {
            return this.opInstrument().getElapsedMaxTime(TimeUnit.NANOSECONDS);
        }

        @Override
        public long getCpuTime() {
            return this.opInstrument().getCpuTime();
        }

        @Override
        public long getCpuTotalTime() {
            return this.opInstrument().getCpuTotalTime(TimeUnit.NANOSECONDS);
        }

        @Override
        public long getCpuMinTime() {
            return this.opInstrument().getCpuMinTime(TimeUnit.NANOSECONDS);
        }

        @Override
        public long getCpuMaxTime() {
            return this.opInstrument().getCpuMaxTime(TimeUnit.NANOSECONDS);
        }

        @Override
        public long getResultSetSize() {
            return this.opInstrument().getResultSetSize();
        }
    }

    public static class CounterMXBeanImpl
    extends InstrumentMXBeanImpl
    implements InstrumentMXBean.CounterMXBean {
        public CounterMXBeanImpl(InstrumentRegistry instrumentRegistry, ObjectName objectName, String counterName) {
            super(instrumentRegistry, objectName, counterName);
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public long getValue() {
            return this.value();
        }
    }

    public static class GaugeMXBeanImpl
    extends InstrumentMXBeanImpl
    implements InstrumentMXBean.GaugeMXBean {
        public GaugeMXBeanImpl(InstrumentRegistry instrumentRegistry, ObjectName objectName, String gaugeName) {
            super(instrumentRegistry, objectName, gaugeName);
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public long getValue() {
            return this.value();
        }
    }
}

