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
import org.hibernate.internal.CoreMessageLogger;
import org.jboss.logging.Logger;

public class PooledLoOptimizer
extends AbstractOptimizer {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)PooledLoOptimizer.class.getName());
    private GenerationState noTenantState;
    private Map<String, GenerationState> tenantSpecificState;

    public PooledLoOptimizer(Class returnClass, int incrementSize) {
        super(returnClass, incrementSize);
        if (incrementSize < 1) {
            throw new HibernateException("increment size cannot be less than 1");
        }
        LOG.creatingPooledLoOptimizer(incrementSize, returnClass.getName());
    }

    @Override
    public synchronized Serializable generate(AccessCallback callback) {
        GenerationState generationState = this.locateGenerationState(callback.getTenantIdentifier());
        if (generationState.lastSourceValue == null || !generationState.value.lt(generationState.upperLimitValue)) {
            generationState.lastSourceValue = callback.getNextValue();
            generationState.upperLimitValue = generationState.lastSourceValue.copy().add(this.incrementSize);
            generationState.value = generationState.lastSourceValue.copy();
            while (generationState.value.lt(1L)) {
                generationState.value.increment();
            }
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
        return this.noTenantGenerationState().lastSourceValue;
    }

    @Override
    public boolean applyIncrementSizeToSourceValues() {
        return true;
    }

    private static class GenerationState {
        private IntegralDataTypeHolder lastSourceValue;
        private IntegralDataTypeHolder value;
        private IntegralDataTypeHolder upperLimitValue;

        private GenerationState() {
        }
    }
}

