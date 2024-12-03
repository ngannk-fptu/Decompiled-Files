/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.emailtracker;

import com.atlassian.confluence.plugins.emailtracker.InvalidTrackingRequestException;
import java.util.Map;

public interface EmailUrlValidator {
    public Map<String, String> addValidationDataToQueryParameters(String var1, Map<String, String> var2);

    public Map<String, String> validateQueryParameters(String var1, Map<String, String> var2) throws InvalidTrackingRequestException;
}

