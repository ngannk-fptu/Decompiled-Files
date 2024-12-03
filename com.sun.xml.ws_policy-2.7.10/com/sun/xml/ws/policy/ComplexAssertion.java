/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.NestedPolicy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import java.util.Collection;

public abstract class ComplexAssertion
extends PolicyAssertion {
    private final NestedPolicy nestedPolicy;

    protected ComplexAssertion() {
        this.nestedPolicy = NestedPolicy.createNestedPolicy(AssertionSet.emptyAssertionSet());
    }

    protected ComplexAssertion(AssertionData data, Collection<? extends PolicyAssertion> assertionParameters, AssertionSet nestedAlternative) {
        super(data, assertionParameters);
        AssertionSet nestedSet = nestedAlternative != null ? nestedAlternative : AssertionSet.emptyAssertionSet();
        this.nestedPolicy = NestedPolicy.createNestedPolicy(nestedSet);
    }

    @Override
    public final boolean hasNestedPolicy() {
        return true;
    }

    @Override
    public final NestedPolicy getNestedPolicy() {
        return this.nestedPolicy;
    }
}

