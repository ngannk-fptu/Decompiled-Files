/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.rest.representations;

import com.atlassian.upm.PluginControlHandlerRegistry;
import com.atlassian.upm.core.rest.representations.BaseRepresentationFactory;
import com.atlassian.upm.core.rest.representations.ErrorRepresentation;
import java.util.Objects;

public class DefaultBaseRepresentationFactory
implements BaseRepresentationFactory {
    protected final PluginControlHandlerRegistry pluginControlHandlerRegistry;

    public DefaultBaseRepresentationFactory(PluginControlHandlerRegistry pluginControlHandlerRegistry) {
        this.pluginControlHandlerRegistry = Objects.requireNonNull(pluginControlHandlerRegistry, "pluginControlHandlerRegistry");
    }

    @Override
    public ErrorRepresentation createErrorRepresentation(String message) {
        return new ErrorRepresentation(Objects.requireNonNull(message, "message"), null);
    }

    @Override
    public ErrorRepresentation createErrorRepresentation(String message, String subCode) {
        return new ErrorRepresentation(Objects.requireNonNull(message, "message"), Objects.requireNonNull(subCode, "subCode"));
    }

    @Override
    public ErrorRepresentation createI18nErrorRepresentation(String i18nKey) {
        return new ErrorRepresentation(null, Objects.requireNonNull(i18nKey, "i18nKey"));
    }
}

