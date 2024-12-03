/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.guardrails;

import java.io.OutputStream;
import lombok.Generated;

public class GuardrailsCsvOutputStreamResult {
    public final OutputStream outputStream;
    public final boolean noRecords;
    public final Throwable error;

    public GuardrailsCsvOutputStreamResult(OutputStream outputStream, boolean noRecords, Throwable error) {
        this.outputStream = outputStream;
        this.noRecords = noRecords;
        this.error = error;
    }

    public static GuardrailsCsvOutputStreamResult failed(OutputStream outputStream, boolean noRecords, Throwable error) {
        return new GuardrailsCsvOutputStreamResult(outputStream, noRecords, error);
    }

    public static GuardrailsCsvOutputStreamResult succeeded(OutputStream outputStream) {
        return new GuardrailsCsvOutputStreamResult(outputStream, false, null);
    }

    @Generated
    public OutputStream getOutputStream() {
        return this.outputStream;
    }

    @Generated
    public boolean isNoRecords() {
        return this.noRecords;
    }

    @Generated
    public Throwable getError() {
        return this.error;
    }
}

