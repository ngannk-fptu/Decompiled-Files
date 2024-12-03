/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.validation.constraints.NotBlank
 *  javax.validation.constraints.Size
 */
package com.atlassian.webhooks;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public interface WebhookScope {
    public static final String TYPE_GLOBAL = "global";
    public static final WebhookScope GLOBAL = new WebhookScope(){

        @Override
        @Nonnull
        public Optional<String> getId() {
            return Optional.empty();
        }

        @Override
        @Nonnull
        public String getType() {
            return WebhookScope.TYPE_GLOBAL;
        }
    };

    @Nonnull
    @Size(max=255)
    public @Size(max=255) Optional<String> getId();

    @NotBlank
    @Size(max=255)
    public @NotBlank @Size(max=255) String getType();
}

