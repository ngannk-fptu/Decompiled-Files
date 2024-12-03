/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.id.enhanced;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.id.IntegralDataTypeHolder;
import org.hibernate.id.enhanced.AbstractOptimizer;
import org.hibernate.id.enhanced.AccessCallback;
import org.hibernate.id.enhanced.PooledLoOptimizer;
import org.hibernate.internal.CoreMessageLogger;
import org.jboss.logging.Logger;

public class PooledLoThreadLocalOptimizer
extends AbstractOptimizer {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)PooledLoOptimizer.class.getName());
    private final ThreadLocal<GenerationState> singleTenantState = ThreadLocal.withInitial(() -> new GenerationState());
    private final ThreadLocal<Map<String, GenerationState>> multiTenantStates = ThreadLocal.withInitial(HashMap::new);

    public PooledLoThreadLocalOptimizer(Class returnClass, int incrementSize) {
        super(returnClass, incrementSize);
        if (incrementSize < 1) {
            throw new HibernateException("increment size cannot be less than 1");
        }
        LOG.creatingPooledLoOptimizer(incrementSize, returnClass.getName());
    }

    @Override
    public Serializable generate(AccessCallback callback) {
        return this.locateGenerationState(callback.getTenantIdentifier()).generate(callback, this.incrementSize);
    }

    private GenerationState locateGenerationState(String tenantIdentifier) {
        if (tenantIdentifier == null) {
            return this.singleTenantState.get();
        }
        Map<String, GenerationState> states = this.multiTenantStates.get();
        GenerationState state = states.get(tenantIdentifier);
        if (state == null) {
            state = new GenerationState();
            states.put(tenantIdentifier, state);
        }
        return state;
    }

    private GenerationState noTenantGenerationState() {
        GenerationState noTenantState = this.locateGenerationState(null);
        if (noTenantState == null) {
            throw new IllegalStateException("Could not locate previous generation state for no-tenant");
        }
        return noTenantState;
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

        private Serializable generate(AccessCallback callback, int incrementSize) {
            if (this.value == null || !this.value.lt(this.upperLimitValue)) {
                this.lastSourceValue = callback.getNextValue();
                this.upperLimitValue = this.lastSourceValue.copy().add(incrementSize);
                this.value = this.lastSourceValue.copy();
                while (this.value.lt(1L)) {
                    this.value.increment();
                }
            }
            return this.value.makeValueThenIncrement();
        }
    }
}

