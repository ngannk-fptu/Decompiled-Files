/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy;

import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMerger;
import com.sun.xml.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.ws.policy.privateutil.PolicyUtils;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public final class PolicySubject {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicySubject.class);
    private final List<Policy> policies = new LinkedList<Policy>();
    private final Object subject;

    public PolicySubject(Object subject, Policy policy) throws IllegalArgumentException {
        if (subject == null || policy == null) {
            throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0021_SUBJECT_AND_POLICY_PARAM_MUST_NOT_BE_NULL(subject, policy)));
        }
        this.subject = subject;
        this.attach(policy);
    }

    public PolicySubject(Object subject, Collection<Policy> policies) throws IllegalArgumentException {
        if (subject == null || policies == null) {
            throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0062_INPUT_PARAMS_MUST_NOT_BE_NULL()));
        }
        if (policies.isEmpty()) {
            throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0064_INITIAL_POLICY_COLLECTION_MUST_NOT_BE_EMPTY()));
        }
        this.subject = subject;
        this.policies.addAll(policies);
    }

    public void attach(Policy policy) {
        if (policy == null) {
            throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0038_POLICY_TO_ATTACH_MUST_NOT_BE_NULL()));
        }
        this.policies.add(policy);
    }

    public Policy getEffectivePolicy(PolicyMerger merger) throws PolicyException {
        return merger.merge(this.policies);
    }

    public Object getSubject() {
        return this.subject;
    }

    public String toString() {
        return this.toString(0, new StringBuffer()).toString();
    }

    StringBuffer toString(int indentLevel, StringBuffer buffer) {
        String indent = PolicyUtils.Text.createIndent(indentLevel);
        String innerIndent = PolicyUtils.Text.createIndent(indentLevel + 1);
        buffer.append(indent).append("policy subject {").append(PolicyUtils.Text.NEW_LINE);
        buffer.append(innerIndent).append("subject = '").append(this.subject).append('\'').append(PolicyUtils.Text.NEW_LINE);
        for (Policy policy : this.policies) {
            policy.toString(indentLevel + 1, buffer).append(PolicyUtils.Text.NEW_LINE);
        }
        buffer.append(indent).append('}');
        return buffer;
    }
}

