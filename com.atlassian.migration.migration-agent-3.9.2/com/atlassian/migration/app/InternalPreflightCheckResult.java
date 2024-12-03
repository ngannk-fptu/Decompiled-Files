/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.InternalPreflightOutput;
import java.util.ArrayList;
import java.util.List;

public class InternalPreflightCheckResult {
    private final PreflightCheckStatus status;
    private final List<InternalPreflightOutput> outputs;

    public InternalPreflightCheckResult(PreflightCheckStatus status, List<InternalPreflightOutput> outputs) {
        this.status = status;
        this.outputs = outputs;
    }

    public static PreflightCheckResultBuilder builder() {
        return new PreflightCheckResultBuilder();
    }

    public PreflightCheckStatus getStatus() {
        return this.status;
    }

    public List<InternalPreflightOutput> getOutputs() {
        return this.outputs;
    }

    public static enum PreflightCheckStatus {
        RUNNING,
        OK,
        WARNING,
        BLOCKER;

    }

    public static class PreflightCheckResultBuilder {
        private PreflightCheckStatus status;
        private List<InternalPreflightOutput> outputs = new ArrayList<InternalPreflightOutput>();

        public PreflightCheckResultBuilder withStatus(PreflightCheckStatus status) {
            this.status = status;
            return this;
        }

        public PreflightCheckResultBuilder withOutputs(List<InternalPreflightOutput> outputs) {
            this.outputs = outputs;
            return this;
        }

        public PreflightCheckResultBuilder addOutput(InternalPreflightOutput output) {
            this.outputs.add(output);
            return this;
        }

        public InternalPreflightCheckResult build() {
            return new InternalPreflightCheckResult(this.status, this.outputs);
        }
    }
}

