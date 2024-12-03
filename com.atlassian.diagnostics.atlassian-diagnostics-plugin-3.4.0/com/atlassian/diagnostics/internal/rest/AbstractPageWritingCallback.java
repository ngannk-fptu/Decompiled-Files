/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.PageCallback
 *  com.atlassian.diagnostics.PageRequest
 *  com.atlassian.diagnostics.PageSummary
 *  javax.annotation.Nonnull
 *  javax.ws.rs.core.UriBuilder
 *  org.codehaus.jackson.JsonGenerator
 */
package com.atlassian.diagnostics.internal.rest;

import com.atlassian.diagnostics.PageCallback;
import com.atlassian.diagnostics.PageRequest;
import com.atlassian.diagnostics.PageSummary;
import com.atlassian.diagnostics.internal.rest.RestLinkUtils;
import com.atlassian.diagnostics.internal.rest.RestPageRequest;
import com.atlassian.diagnostics.internal.rest.UncheckedJsonGenerator;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.ws.rs.core.UriBuilder;
import org.codehaus.jackson.JsonGenerator;

public abstract class AbstractPageWritingCallback<T>
implements PageCallback<T, Void> {
    protected final UncheckedJsonGenerator generator;
    protected final Supplier<UriBuilder> uriBuilderSupplier;

    public AbstractPageWritingCallback(@Nonnull JsonGenerator generator, @Nonnull Supplier<UriBuilder> uriBuilderSupplier) {
        this.generator = new UncheckedJsonGenerator(Objects.requireNonNull(generator, "generator"));
        this.uriBuilderSupplier = Objects.requireNonNull(uriBuilderSupplier, "uriBuilderSupplier");
    }

    public Void onEnd(@Nonnull PageSummary summary) {
        this.writeEndPage(summary);
        return null;
    }

    public void onStart(@Nonnull PageRequest pageRequest) {
        this.generator.writeStartObject();
        this.generator.writeObjectField("request", new RestPageRequest(pageRequest));
        this.generator.writeArrayFieldStart("values");
    }

    protected void writeAdditionalEntities() {
    }

    protected void writeEndPage(PageSummary summary) {
        this.generator.writeEndArray();
        this.writeAdditionalEntities();
        this.generator.writeNumberField("size", summary.size());
        this.generator.writeObjectField("links", RestLinkUtils.linksFor(this.uriBuilderSupplier, summary.getPrevRequest().orElse(null), summary.getNextRequest().orElse(null)));
        this.generator.writeEndObject();
        this.generator.flush();
    }
}

