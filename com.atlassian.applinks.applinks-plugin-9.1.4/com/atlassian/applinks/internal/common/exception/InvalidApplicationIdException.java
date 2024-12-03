/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.common.exception;

import com.atlassian.applinks.internal.common.exception.InvalidValueException;
import com.atlassian.applinks.internal.common.i18n.I18nKey;
import java.io.Serializable;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InvalidApplicationIdException
extends InvalidValueException {
    public static final String DEFAULT_MESSAGE = "applinks.service.error.invalidvalue.applinkid";

    public InvalidApplicationIdException(@Nullable String message) {
        super(message);
    }

    public InvalidApplicationIdException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    @Nonnull
    public static I18nKey invalidIdI18nKey(@Nullable String invalidIdValue) {
        Optional<String> invalidIdVal = Optional.ofNullable(invalidIdValue);
        return I18nKey.newI18nKey(DEFAULT_MESSAGE, (Serializable)((Object)invalidIdVal.orElse("")));
    }
}

