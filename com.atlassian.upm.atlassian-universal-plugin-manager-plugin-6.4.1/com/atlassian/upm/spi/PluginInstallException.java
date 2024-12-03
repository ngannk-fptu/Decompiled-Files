/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.spi;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.api.util.Pair;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;

public class PluginInstallException
extends RuntimeException {
    private final Option<Pair<String, Serializable[]>> i18nMessage;
    private final boolean stackTraceSignificant;
    private static final Function<String, Pair<String, Serializable[]>> I18N_MAPPER = i18nKey -> Pair.pair(i18nKey, new Serializable[0]);

    public PluginInstallException(String message) {
        this(message, true);
    }

    public PluginInstallException(String message, boolean stackTraceSignificant) {
        this(message, Option.none(String.class), stackTraceSignificant);
    }

    public PluginInstallException(String message, Throwable cause) {
        this(message, cause, true);
    }

    public PluginInstallException(String message, Throwable cause, boolean stackTraceSignificant) {
        super(message, cause);
        this.i18nMessage = Option.none();
        this.stackTraceSignificant = stackTraceSignificant;
    }

    public PluginInstallException(String message, Option<String> code) {
        this(message, code, true);
    }

    public PluginInstallException(String message, String code, Serializable ... params) {
        super(message);
        this.i18nMessage = Option.some(Pair.pair(Objects.requireNonNull(code), Arrays.copyOf(params, params.length)));
        this.stackTraceSignificant = true;
    }

    public PluginInstallException(String message, Option<String> code, boolean stackTraceSignificant) {
        super(message);
        this.i18nMessage = Objects.requireNonNull(code).map(I18N_MAPPER);
        this.stackTraceSignificant = stackTraceSignificant;
    }

    public PluginInstallException(String message, Option<String> code, Throwable cause, boolean stackTraceSignificant) {
        super(message, cause);
        this.i18nMessage = Objects.requireNonNull(code).map(I18N_MAPPER);
        this.stackTraceSignificant = stackTraceSignificant;
    }

    public Option<String> getCode() {
        Iterator<Pair<String, Serializable[]>> iterator = this.i18nMessage.iterator();
        if (iterator.hasNext()) {
            Pair<String, Serializable[]> i18n = iterator.next();
            return Option.some(i18n.first());
        }
        return Option.none();
    }

    public Option<Pair<String, Serializable[]>> getI18nMessageProperties() {
        return this.i18nMessage;
    }

    public boolean isStackTraceSignificant() {
        return this.stackTraceSignificant;
    }
}

