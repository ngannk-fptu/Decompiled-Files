/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.embedded.api;

import com.atlassian.crowd.embedded.api.ValidatePasswordRequest;

public interface PasswordConstraint {
    public boolean validate(ValidatePasswordRequest var1);
}

