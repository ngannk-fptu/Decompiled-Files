/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.sal.api.features;

import com.atlassian.sal.api.features.InvalidFeatureKeyException;
import com.google.common.base.Predicate;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public enum ValidFeatureKeyPredicate implements Predicate<String>
{
    INSTANCE;

    private static final Pattern VALID_FEATURE_KEY_PATTERN;

    public static boolean isValidFeatureKey(@Nullable String input) {
        return INSTANCE.test(input);
    }

    public static String checkFeatureKey(@Nullable String input) {
        if (ValidFeatureKeyPredicate.isValidFeatureKey(input)) {
            return input;
        }
        throw new InvalidFeatureKeyException("Invalid feature key: '" + input + "'");
    }

    @Deprecated
    public boolean apply(@Nullable String input) {
        return input != null && VALID_FEATURE_KEY_PATTERN.matcher(input).matches();
    }

    public boolean test(@Nullable String input) {
        return input != null && VALID_FEATURE_KEY_PATTERN.matcher(input).matches();
    }

    static {
        VALID_FEATURE_KEY_PATTERN = Pattern.compile("[\\w\\.\\-]+");
    }
}

