/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Alert
 *  com.atlassian.diagnostics.CallbackResult
 *  javax.annotation.Nonnull
 *  javax.ws.rs.core.UriBuilder
 *  org.codehaus.jackson.JsonGenerator
 */
package com.atlassian.diagnostics.internal.rest;

import com.atlassian.diagnostics.Alert;
import com.atlassian.diagnostics.CallbackResult;
import com.atlassian.diagnostics.internal.rest.AbstractPageWritingCallback;
import com.atlassian.diagnostics.internal.rest.RestAlert;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.ws.rs.core.UriBuilder;
import org.codehaus.jackson.JsonGenerator;

public class AlertPageWritingCallback
extends AbstractPageWritingCallback<Alert> {
    private final String[] suppressedAlertFields;

    public AlertPageWritingCallback(JsonGenerator generator, Supplier<UriBuilder> uriBuilderSupplier, String ... suppressedAlertFields) {
        super(generator, uriBuilderSupplier);
        this.suppressedAlertFields = suppressedAlertFields;
    }

    @Nonnull
    public CallbackResult onItem(@Nonnull Alert alert) {
        this.generator.writeObject(new RestAlert(alert, this.suppressedAlertFields));
        return CallbackResult.CONTINUE;
    }
}

