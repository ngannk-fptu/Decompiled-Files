/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.id.enhanced;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.hibernate.HibernateException;
import org.hibernate.id.IntegralDataTypeHolder;
import org.hibernate.id.enhanced.AbstractOptimizer;
import org.hibernate.id.enhanced.AccessCallback;
import org.jboss.logging.Logger;

public class LegacyHiLoAlgorithmOptimizer
extends AbstractOptimizer {
    private static final Logger log = Logger.getLogger(LegacyHiLoAlgorithmOptimizer.class);
    private final long initialMaxLo;
    private GenerationState noTenantState;
    private Map<String, GenerationState> tenantSpecificState;

    public LegacyHiLoAlgorithmOptimizer(Class returnClass, int incrementSize) {
        super(returnClass, incrementSize);
        if (incrementSize < 1) {
            throw new HibernateException("increment size cannot be less than 1");
        }
        if (log.isTraceEnabled()) {
            log.tracev("Creating hilo optimizer (legacy) with [incrementSize={0}; returnClass={1}]", (Object)incrementSize, (Object)returnClass.getName());
        }
        this.initialMaxLo = incrementSize;
    }

    @Override
    public synchronized Serializable generate(AccessCallback callback) {
        GenerationState generationState = this.locateGenerationState(callback.getTenantIdentifier());
        if (generationState.lo > generationState.maxLo) {
            generationState.lastSourceValue = callback.getNextValue();
            generationState.lo = generationState.lastSourceValue.eq(0L) ? 1L : 0L;
            generationState.hi = generationState.lastSourceValue.copy().multiplyBy(generationState.maxLo + 1L);
        }
        generationState.value = generationState.hi.copy().add(generationState.lo++);
        return generationState.value.makeValue();
    }

    private GenerationState locateGenerationState(String tenantIdentifier) {
        GenerationState state;
        if (tenantIdentifier == null) {
            if (this.noTenantState == null) {
                this.noTenantState = this.createGenerationState();
            }
            return this.noTenantState;
        }
        if (this.tenantSpecificState == null) {
            this.tenantSpecificState = new ConcurrentHashMap<String, GenerationState>();
            state = this.createGenerationState();
            this.tenantSpecificState.put(tenantIdentifier, state);
        } else {
            state = this.tenantSpecificState.get(tenantIdentifier);
            if (state == null) {
                state = this.createGenerationState();
                this.tenantSpecificState.put(tenantIdentifier, state);
            }
        }
        return state;
    }

    private GenerationState createGenerationState() {
        GenerationState state = new GenerationState();
        state.maxLo = this.initialMaxLo;
        state.lo = this.initialMaxLo + 1L;
        return state;
    }

    private GenerationState noTenantGenerationState() {
        if (this.noTenantState == null) {
            throw new IllegalStateException("Could not locate previous generation state for no-tenant");
        }
        return this.noTenantState;
    }

    @Override
    public synchronized IntegralDataTypeHolder getLastSourceValue() {
        return this.noTenantGenerationState().lastSourceValue.copy();
    }

    @Override
    public boolean applyIncrementSizeToSourceValues() {
        return false;
    }

    public synchronized IntegralDataTypeHolder getLastValue() {
        return this.noTenantGenerationState().value;
    }

    private static class GenerationState {
        private long maxLo;
        private long lo;
        private IntegralDataTypeHolder hi;
        private IntegralDataTypeHolder lastSourceValue;
        private IntegralDataTypeHolder value;

        private GenerationState() {
        }
    }
}

