/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.Constants
 *  org.springframework.lang.Nullable
 */
package org.springframework.transaction.support;

import java.io.Serializable;
import org.springframework.core.Constants;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionDefinition;

public class DefaultTransactionDefinition
implements TransactionDefinition,
Serializable {
    public static final String PREFIX_PROPAGATION = "PROPAGATION_";
    public static final String PREFIX_ISOLATION = "ISOLATION_";
    public static final String PREFIX_TIMEOUT = "timeout_";
    public static final String READ_ONLY_MARKER = "readOnly";
    static final Constants constants = new Constants(TransactionDefinition.class);
    private int propagationBehavior = 0;
    private int isolationLevel = -1;
    private int timeout = -1;
    private boolean readOnly = false;
    @Nullable
    private String name;

    public DefaultTransactionDefinition() {
    }

    public DefaultTransactionDefinition(TransactionDefinition other) {
        this.propagationBehavior = other.getPropagationBehavior();
        this.isolationLevel = other.getIsolationLevel();
        this.timeout = other.getTimeout();
        this.readOnly = other.isReadOnly();
        this.name = other.getName();
    }

    public DefaultTransactionDefinition(int propagationBehavior) {
        this.propagationBehavior = propagationBehavior;
    }

    public final void setPropagationBehaviorName(String constantName) throws IllegalArgumentException {
        if (!constantName.startsWith(PREFIX_PROPAGATION)) {
            throw new IllegalArgumentException("Only propagation constants allowed");
        }
        this.setPropagationBehavior(constants.asNumber(constantName).intValue());
    }

    public final void setPropagationBehavior(int propagationBehavior) {
        if (!constants.getValues(PREFIX_PROPAGATION).contains(propagationBehavior)) {
            throw new IllegalArgumentException("Only values of propagation constants allowed");
        }
        this.propagationBehavior = propagationBehavior;
    }

    @Override
    public final int getPropagationBehavior() {
        return this.propagationBehavior;
    }

    public final void setIsolationLevelName(String constantName) throws IllegalArgumentException {
        if (!constantName.startsWith(PREFIX_ISOLATION)) {
            throw new IllegalArgumentException("Only isolation constants allowed");
        }
        this.setIsolationLevel(constants.asNumber(constantName).intValue());
    }

    public final void setIsolationLevel(int isolationLevel) {
        if (!constants.getValues(PREFIX_ISOLATION).contains(isolationLevel)) {
            throw new IllegalArgumentException("Only values of isolation constants allowed");
        }
        this.isolationLevel = isolationLevel;
    }

    @Override
    public final int getIsolationLevel() {
        return this.isolationLevel;
    }

    public final void setTimeout(int timeout) {
        if (timeout < -1) {
            throw new IllegalArgumentException("Timeout must be a positive integer or TIMEOUT_DEFAULT");
        }
        this.timeout = timeout;
    }

    @Override
    public final int getTimeout() {
        return this.timeout;
    }

    public final void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public final boolean isReadOnly() {
        return this.readOnly;
    }

    public final void setName(String name) {
        this.name = name;
    }

    @Override
    @Nullable
    public final String getName() {
        return this.name;
    }

    public boolean equals(@Nullable Object other) {
        return this == other || other instanceof TransactionDefinition && this.toString().equals(other.toString());
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    public String toString() {
        return this.getDefinitionDescription().toString();
    }

    protected final StringBuilder getDefinitionDescription() {
        StringBuilder result = new StringBuilder();
        result.append(constants.toCode((Object)this.propagationBehavior, PREFIX_PROPAGATION));
        result.append(',');
        result.append(constants.toCode((Object)this.isolationLevel, PREFIX_ISOLATION));
        if (this.timeout != -1) {
            result.append(',');
            result.append(PREFIX_TIMEOUT).append(this.timeout);
        }
        if (this.readOnly) {
            result.append(',');
            result.append(READ_ONLY_MARKER);
        }
        return result;
    }
}

