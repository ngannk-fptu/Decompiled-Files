/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.validator;

import java.util.List;

public interface EmailAddressValidator {
    public boolean isValidSyntax(String var1);

    public long validateSyntax(List<String> var1);

    public long findDuplicates(List<String> var1);
}

