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
import org.hibernate.id.enhanced.InitialValueAwareOptimizer;
import org.hibernate.internal.CoreMessageLogger;
import org.jboss.logging.Logger;

public class PooledOptimizer
extends AbstractOptimizer
implements InitialValueAwareOptimizer {
    private static final CoreMessageLogger log = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)PooledOptimizer.class.getName());
    private long initialValue = -1L;
    private GenerationState noTenantState;
    private Map<String, GenerationState> tenantSpecificState;

    public PooledOptimizer(Class returnClass, int incrementSize) {
        super(returnClass, incrementSize);
        if (incrementSize < 1) {
            throw new HibernateException("increment size cannot be less than 1");
        }
        if (log.isTraceEnabled()) {
            log.tracev("Creating pooled optimizer with [incrementSize={0}; returnClass={1}]", incrementSize, returnClass.getName());
        }
    }

    @Override
    public synchronized Serializable generate(AccessCallback callback) {
        GenerationState generationState = this.locateGenerationState(callback.getTenantIdentifier());
        if (generationState.hiValue == null) {
            generationState.hiValue = callback.getNextValue();
            if (generationState.hiValue.lt(1L)) {
                log.pooledOptimizerReportedInitialValue(generationState.hiValue);
            }
            if (this.initialValue == -1L && generationState.hiValue.lt(this.incrementSize) || generationState.hiValue.eq(this.initialValue)) {
                generationState.value = generationState.hiValue.copy();
            } else {
                generationState.value = generationState.hiValue.copy().subtract(this.incrementSize - 1);
            }
        } else if (generationState.value.gt(generationState.hiValue)) {
            generationState.hiValue = callback.getNextValue();
            generationState.value = generationState.hiValue.copy().subtract(this.incrementSize - 1);
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
    public IntegralDataTypeHolder getLastSourceValue() {
        return this.noTenantGenerationState().hiValue;
    }

    @Override
    public boolean applyIncrementSizeToSourceValues() {
        return true;
    }

    public IntegralDataTypeHolder getLastValue() {
        return this.noTenantGenerationState().value.copy().decrement();
    }

    @Override
    public void injectInitialValue(long initialValue) {
        this.initialValue = initialValue;
    }

    private static class GenerationState {
        private IntegralDataTypeHolder hiValue;
        private IntegralDataTypeHolder value;

        private GenerationState() {
        }
    }
}

