/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.manager.emailscan;

import com.atlassian.crowd.manager.emailscan.ValidationResult;
import java.util.Optional;

public interface AppIssuesWithMailsValidationService {
    public void runValidation(long var1);

    public Optional<ValidationResult> getLastValidationResult(long var1);
}

