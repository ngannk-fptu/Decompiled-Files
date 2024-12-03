/*
 * Decompiled with CFR 0.152.
 */
package com.timgroup.statsd;

public class ServiceCheck {
    private String name;
    private String hostname;
    private String message;
    private int checkRunId;
    private int timestamp;
    private Status status;
    private String[] tags;

    public static Builder builder() {
        return new Builder();
    }

    private ServiceCheck() {
    }

    public String getName() {
        return this.name;
    }

    public int getStatus() {
        return this.status.val;
    }

    public String getMessage() {
        return this.message;
    }

    public String getEscapedMessage() {
        return this.message.replace("\n", "\\n").replace("m:", "m\\:");
    }

    public String getHostname() {
        return this.hostname;
    }

    public int getTimestamp() {
        return this.timestamp;
    }

    public String[] getTags() {
        return this.tags;
    }

    static /* synthetic */ String[] access$702(ServiceCheck x0, String[] x1) {
        x0.tags = x1;
        return x1;
    }

    public static class Builder {
        final ServiceCheck res = new ServiceCheck();

        public Builder withName(String name) {
            this.res.name = name;
            return this;
        }

        public Builder withHostname(String hostname) {
            this.res.hostname = hostname;
            return this;
        }

        public Builder withMessage(String message) {
            this.res.message = message;
            return this;
        }

        public Builder withCheckRunId(int checkRunId) {
            this.res.checkRunId = checkRunId;
            return this;
        }

        public Builder withTimestamp(int timestamp) {
            this.res.timestamp = timestamp;
            return this;
        }

        public Builder withStatus(Status status) {
            this.res.status = status;
            return this;
        }

        public Builder withTags(String[] tags) {
            ServiceCheck.access$702(this.res, tags);
            return this;
        }

        public ServiceCheck build() {
            return this.res;
        }
    }

    public static enum Status {
        OK(0),
        WARNING(1),
        CRITICAL(2),
        UNKNOWN(3);

        private final int val;

        private Status(int val) {
            this.val = val;
        }
    }
}

