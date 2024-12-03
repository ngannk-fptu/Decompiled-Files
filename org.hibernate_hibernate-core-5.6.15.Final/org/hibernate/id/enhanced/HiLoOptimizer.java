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

public class HiLoOptimizer
extends AbstractOptimizer {
    private static final Logger log = Logger.getLogger(HiLoOptimizer.class);
    private GenerationState noTenantState;
    private Map<String, GenerationState> tenantSpecificState;

    public HiLoOptimizer(Class returnClass, int incrementSize) {
        super(returnClass, incrementSize);
        if (incrementSize < 1) {
            throw new HibernateException("increment size cannot be less than 1");
        }
        if (log.isTraceEnabled()) {
            log.tracev("Creating hilo optimizer with [incrementSize={0}; returnClass={1}]", (Object)incrementSize, (Object)returnClass.getName());
        }
    }

    @Override
    public synchronized Serializable generate(AccessCallback callback) {
        GenerationState generationState = this.locateGenerationState(callback.getTenantIdentifier());
        if (generationState.lastSourceValue == null) {
            generationState.lastSourceValue = callback.getNextValue();
            while (generationState.lastSourceValue.lt(1L)) {
                generationState.lastSourceValue = callback.getNextValue();
            }
            generationState.upperLimit = generationState.lastSourceValue.copy().multiplyBy(this.incrementSize).increment();
            generationState.value = generationState.upperLimit.copy().subtract(this.incrementSize);
        } else if (!generationState.upperLimit.gt(generationState.value)) {
            generationState.lastSourceValue = callback.getNextValue();
            generationState.upperLimit = generationState.lastSourceValue.copy().multiplyBy(this.incrementSize).increment();
            generationState.value = generationState.upperLimit.copy().subtract(this.incrementSize);
        }
        return generationState.value.makeValueThenIncrement();
    }

    private GenerationState locateGenerationState(String tenantIdentifier) {
        GenerationState state;
        if (tenantIdentifier == null) {
            if (this.noTenantState == null) {
                this.noTenantState = new GenerationState();
            }
            return this.noTenantState;
        }
        if (this.tenantSpecificState == null) {
            this.tenantSpecificState = new ConcurrentHashMap<String, GenerationState>();
            state = new GenerationState();
            this.tenantSpecificState.put(tenantIdentifier, state);
        } else {
            state = this.tenantSpecificState.get(tenantIdentifier);
            if (state == null) {
                state = new GenerationState();
                this.tenantSpecificState.put(tenantIdentifier, state);
            }
        }
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
        return this.noTenantGenerationState().lastSourceValue;
    }

    @Override
    public boolean applyIncrementSizeToSourceValues() {
        return false;
    }

    public synchronized IntegralDataTypeHolder getLastValue() {
        return this.noTenantGenerationState().value.copy().decrement();
    }

    public synchronized IntegralDataTypeHolder getHiValue() {
        return this.noTenantGenerationState().upperLimit;
    }

    private static class GenerationState {
        private IntegralDataTypeHolder lastSourceValue;
        private IntegralDataTypeHolder upperLimit;
        private IntegralDataTypeHolder value;

        private GenerationState() {
        }
    }
}

