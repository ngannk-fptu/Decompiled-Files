/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.Policy;
import java.util.Arrays;
import java.util.Iterator;

public final class NestedPolicy
extends Policy {
    private static final String NESTED_POLICY_TOSTRING_NAME = "nested policy";

    private NestedPolicy(AssertionSet set) {
        super(NESTED_POLICY_TOSTRING_NAME, Arrays.asList(set));
    }

    private NestedPolicy(String name, String policyId, AssertionSet set) {
        super(NESTED_POLICY_TOSTRING_NAME, name, policyId, Arrays.asList(set));
    }

    static NestedPolicy createNestedPolicy(AssertionSet set) {
        return new NestedPolicy(set);
    }

    static NestedPolicy createNestedPolicy(String name, String policyId, AssertionSet set) {
        return new NestedPolicy(name, policyId, set);
    }

    public AssertionSet getAssertionSet() {
        Iterator<AssertionSet> iterator = this.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof NestedPolicy)) {
            return false;
        }
        NestedPolicy that = (NestedPolicy)obj;
        return super.equals(that);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return this.toString(0, new StringBuffer()).toString();
    }

    @Override
    StringBuffer toString(int indentLevel, StringBuffer buffer) {
        return super.toString(indentLevel, buffer);
    }
}

