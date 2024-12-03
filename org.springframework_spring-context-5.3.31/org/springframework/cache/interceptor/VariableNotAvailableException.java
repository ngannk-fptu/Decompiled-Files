/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.expression.EvaluationException
 */
package org.springframework.cache.interceptor;

import org.springframework.expression.EvaluationException;

class VariableNotAvailableException
extends EvaluationException {
    private final String name;

    public VariableNotAvailableException(String name) {
        super("Variable not available");
        this.name = name;
    }

    public final String getName() {
        return this.name;
    }
}

