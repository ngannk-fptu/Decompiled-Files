/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.frontend.data;

import java.net.URI;
import java.util.Objects;
import javax.annotation.Nonnull;

public class KbArticleLinkData {
    private final URI featureDescription;
    private final URI schema;
    private final URI troubleshooting;

    public KbArticleLinkData(@Nonnull URI featureDescription, @Nonnull URI schema, @Nonnull URI troubleshooting) {
        this.featureDescription = Objects.requireNonNull(featureDescription);
        this.schema = Objects.requireNonNull(schema);
        this.troubleshooting = Objects.requireNonNull(troubleshooting);
    }

    @Nonnull
    public URI getFeatureDescription() {
        return this.featureDescription;
    }

    @Nonnull
    public URI getSchema() {
        return this.schema;
    }

    @Nonnull
    public URI getTroubleshooting() {
        return this.troubleshooting;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KbArticleLinkData)) {
            return false;
        }
        KbArticleLinkData that = (KbArticleLinkData)o;
        return Objects.equals(this.featureDescription, that.featureDescription) && Objects.equals(this.schema, that.schema) && Objects.equals(this.troubleshooting, that.troubleshooting);
    }

    public int hashCode() {
        return Objects.hash(this.featureDescription, this.schema, this.troubleshooting);
    }
}

