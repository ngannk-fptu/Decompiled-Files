/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.TupleElement
 */
package com.atlassian.migration.agent.store.guardrails;

import javax.persistence.TupleElement;

public class GuardrailsTupleElement<X>
implements TupleElement<X> {
    private Class<? extends X> type;
    private final String alias;
    private final X value;

    public GuardrailsTupleElement(String alias, X value) {
        if (value != null) {
            this.type = value.getClass();
        }
        this.alias = alias;
        this.value = value;
    }

    public GuardrailsTupleElement(String alias, X value, Class<? extends X> type) {
        this.alias = alias;
        this.value = value;
        this.type = type;
    }

    public Class<? extends X> getJavaType() {
        return this.type;
    }

    public String getAlias() {
        return this.alias;
    }

    public X getValue() {
        return this.value;
    }
}

