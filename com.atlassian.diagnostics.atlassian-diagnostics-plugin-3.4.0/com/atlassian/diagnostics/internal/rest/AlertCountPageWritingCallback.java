/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.AlertCount
 *  com.atlassian.diagnostics.CallbackResult
 *  com.atlassian.diagnostics.Issue
 *  javax.annotation.Nonnull
 *  javax.ws.rs.core.UriBuilder
 *  org.codehaus.jackson.JsonGenerator
 */
package com.atlassian.diagnostics.internal.rest;

import com.atlassian.diagnostics.AlertCount;
import com.atlassian.diagnostics.CallbackResult;
import com.atlassian.diagnostics.Issue;
import com.atlassian.diagnostics.internal.rest.AbstractPageWritingCallback;
import com.atlassian.diagnostics.internal.rest.RestAlertCount;
import com.atlassian.diagnostics.internal.rest.RestIssue;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.ws.rs.core.UriBuilder;
import org.codehaus.jackson.JsonGenerator;

public class AlertCountPageWritingCallback
extends AbstractPageWritingCallback<AlertCount> {
    private final Set<Issue> issues = new LinkedHashSet<Issue>();

    public AlertCountPageWritingCallback(JsonGenerator generator, Supplier<UriBuilder> uriBuilderSupplier) {
        super(generator, uriBuilderSupplier);
    }

    @Nonnull
    public CallbackResult onItem(@Nonnull AlertCount alertCount) {
        this.issues.add(alertCount.getIssue());
        this.generator.writeObject(new RestAlertCount(alertCount));
        return CallbackResult.CONTINUE;
    }

    @Override
    protected void writeAdditionalEntities() {
        if (!this.issues.isEmpty()) {
            this.generator.writeArrayFieldStart("issues");
            this.issues.forEach(issue -> this.generator.writeObject(new RestIssue((Issue)issue)));
            this.generator.writeEndArray();
            this.issues.clear();
        }
    }
}

