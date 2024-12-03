/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy;

import com.sun.xml.ws.policy.NestedPolicy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import java.util.Collection;

public abstract class SimpleAssertion
extends PolicyAssertion {
    protected SimpleAssertion() {
    }

    protected SimpleAssertion(AssertionData data, Collection<? extends PolicyAssertion> assertionParameters) {
        super(data, assertionParameters);
    }

    @Override
    public final boolean hasNestedPolicy() {
        return false;
    }

    @Override
    public final NestedPolicy getNestedPolicy() {
        return null;
    }
}

