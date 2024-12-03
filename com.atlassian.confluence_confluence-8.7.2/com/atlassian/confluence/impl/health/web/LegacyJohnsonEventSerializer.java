/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.config.bootstrap.AtlassianBootstrapManager
 *  com.atlassian.johnson.event.Event
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.impl.health.web;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.config.bootstrap.AtlassianBootstrapManager;
import com.atlassian.confluence.impl.health.web.JohnsonEventSerializer;
import com.atlassian.confluence.json.json.Json;
import com.atlassian.confluence.json.json.JsonObject;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.johnson.event.Event;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import org.checkerframework.checker.nullness.qual.NonNull;

@ParametersAreNonnullByDefault
public class LegacyJohnsonEventSerializer
implements JohnsonEventSerializer {
    private final I18NBeanFactory i18NBeanFactory;
    private final BootstrapManager bootstrapManager;

    public LegacyJohnsonEventSerializer(I18NBeanFactory i18NBeanFactory, BootstrapManager bootstrapManager) {
        this.i18NBeanFactory = Objects.requireNonNull(i18NBeanFactory);
        this.bootstrapManager = Objects.requireNonNull(bootstrapManager);
    }

    @Override
    public @NonNull Json toJson(@NonNull Event johnsonEvent) {
        JsonObject eventJSON = new JsonObject();
        eventJSON.setProperty("description", this.getTranslatedDescription(johnsonEvent)).setProperty("date", johnsonEvent.getDate()).setProperty("level", johnsonEvent.getLevel().getLevel()).setProperty("dismissible", Boolean.TRUE.equals(johnsonEvent.getAttribute((Object)"dismissible"))).setProperty("exception", johnsonEvent.getException()).setProperty("old", true);
        this.getProgress(johnsonEvent).ifPresent(progress -> eventJSON.setProperty("progress", progress));
        return eventJSON;
    }

    private String getTranslatedDescription(Event johnsonEvent) {
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean();
        return Optional.ofNullable(johnsonEvent.getAttribute((Object)"i18nKey")).filter(String.class::isInstance).map(String.class::cast).map(i18NBean::getText).orElseGet(() -> this.getDescriptionReplacingContext(johnsonEvent.getDesc()));
    }

    private String getDescriptionReplacingContext(String description) {
        String contextPathToReplaceWith = this.getContextPath().orElse("");
        return description == null ? null : description.replace("$CONTEXT", contextPathToReplaceWith);
    }

    private Optional<String> getContextPath() {
        return Optional.ofNullable(this.bootstrapManager).filter(AtlassianBootstrapManager::isBootstrapped).filter(BootstrapManager::isWebAppContextPathSet).map(BootstrapManager::getWebAppContextPath);
    }

    private OptionalInt getProgress(Event event) {
        if (event.hasProgress()) {
            return OptionalInt.of(event.getProgress());
        }
        return OptionalInt.empty();
    }
}

