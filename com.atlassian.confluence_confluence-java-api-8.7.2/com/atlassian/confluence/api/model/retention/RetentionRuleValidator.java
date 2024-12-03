/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.model.retention;

import com.atlassian.confluence.api.model.retention.AgeUnit;

public class RetentionRuleValidator {
    public static final Integer MAX_AGE_LOWER_LIMIT = 1;
    public static final Integer MAX_AGE_UPPER_LIMIT = 2000;
    public static final Integer MAX_NUMBER_LOWER_LIMIT = 1;
    public static final Integer MAX_NUMBER_UPPER_LIMIT = 2000;

    public static Boolean isValidAgeUnit(AgeUnit ageUnit, Integer maxAge) {
        return maxAge == null || ageUnit != null;
    }

    public static Boolean isValidMaxAge(Integer maxAge) {
        if (maxAge == null) {
            return true;
        }
        return maxAge >= MAX_AGE_LOWER_LIMIT && maxAge <= MAX_AGE_UPPER_LIMIT;
    }

    public static Boolean isValidMaxNumber(Integer maxNumberOfVersions) {
        if (maxNumberOfVersions == null) {
            return true;
        }
        return maxNumberOfVersions >= MAX_NUMBER_LOWER_LIMIT && maxNumberOfVersions <= MAX_NUMBER_UPPER_LIMIT;
    }
}

