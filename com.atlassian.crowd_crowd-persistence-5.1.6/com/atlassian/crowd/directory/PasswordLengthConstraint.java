/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.PasswordConstraint
 *  com.atlassian.crowd.embedded.api.ValidatePasswordRequest
 *  com.google.common.base.Preconditions
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.embedded.api.PasswordConstraint;
import com.atlassian.crowd.embedded.api.ValidatePasswordRequest;
import com.google.common.base.Preconditions;

public class PasswordLengthConstraint
implements PasswordConstraint {
    private final int minimumLength;

    public PasswordLengthConstraint(int minimumLength) {
        Preconditions.checkArgument((minimumLength >= 0 ? 1 : 0) != 0);
        this.minimumLength = minimumLength;
    }

    public int getMinimumLength() {
        return this.minimumLength;
    }

    public String toString() {
        return "PasswordLengthConstraint(minimum=" + this.minimumLength + ")";
    }

    public boolean validate(ValidatePasswordRequest request) {
        return request.getPassword().getCredential().length() >= this.minimumLength;
    }
}

