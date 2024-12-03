/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.app;

import java.net.URL;
import java.util.Optional;

public class InternalPreflightOutput {
    private final String description;
    private final Optional<URL> url;

    private InternalPreflightOutput(String description, URL url) {
        this.description = description;
        this.url = Optional.of(url);
    }

    private InternalPreflightOutput(String description) {
        this.description = description;
        this.url = Optional.empty();
    }

    public static InternalPreflightOutput withDescription(String description) {
        return new InternalPreflightOutput(description);
    }

    public static InternalPreflightOutput withDescriptionAndUrl(String description, URL url) {
        return new InternalPreflightOutput(description, url);
    }

    public static class PreflightOutputBuilder {
        private String description;
        private URL url;

        public PreflightOutputBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public PreflightOutputBuilder withUrl(URL url) {
            this.url = url;
            return this;
        }

        public InternalPreflightOutput createPreflightOutput() {
            return new InternalPreflightOutput(this.description, this.url);
        }
    }
}

