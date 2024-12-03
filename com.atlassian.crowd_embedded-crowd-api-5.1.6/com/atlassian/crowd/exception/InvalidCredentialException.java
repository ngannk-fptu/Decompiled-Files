/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.exception;

import com.atlassian.crowd.embedded.api.PasswordConstraint;
import com.atlassian.crowd.exception.CrowdException;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import javax.annotation.Nullable;

public class InvalidCredentialException
extends CrowdException {
    @Nullable
    private final Collection<PasswordConstraint> violatedConstraints;
    @Nullable
    private final String policyDescription;

    public InvalidCredentialException() {
        this.policyDescription = null;
        this.violatedConstraints = null;
    }

    public InvalidCredentialException(String message) {
        super(message);
        this.policyDescription = null;
        this.violatedConstraints = null;
    }

    public InvalidCredentialException(String genericMessage, @Nullable String policyDescription, Collection<PasswordConstraint> violatedConstraints) {
        super(policyDescription == null ? genericMessage : genericMessage + ": " + policyDescription);
        this.policyDescription = policyDescription;
        this.violatedConstraints = ImmutableList.copyOf(violatedConstraints);
    }

    public InvalidCredentialException(String message, Throwable cause) {
        super(message, cause);
        this.policyDescription = null;
        this.violatedConstraints = null;
    }

    public InvalidCredentialException(Throwable throwable) {
        super(throwable);
        this.policyDescription = null;
        this.violatedConstraints = null;
    }

    @Nullable
    public String getPolicyDescription() {
        return this.policyDescription;
    }

    @Nullable
    public Collection<PasswordConstraint> getViolatedConstraints() {
        return this.violatedConstraints;
    }
}

