/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.logging.log4j.layout.json;

public class JsonStaticData {
    private final String productName;
    private final long processId;
    private final String serviceId;
    private final String environment;
    private final String dataCenter;
    private final String rack;

    private JsonStaticData(Builder builder) {
        this.productName = builder.productName;
        this.processId = builder.processId;
        this.serviceId = builder.serviceId;
        this.environment = builder.environment;
        this.dataCenter = builder.dataCenter;
        this.rack = builder.rack;
    }

    public String getProductName() {
        return this.productName;
    }

    public long getProcessId() {
        return this.processId;
    }

    public String getServiceId() {
        return this.serviceId;
    }

    public String getEnvironment() {
        return this.environment;
    }

    public String getDataCenter() {
        return this.dataCenter;
    }

    public String getRack() {
        return this.rack;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String productName;
        private long processId;
        private String serviceId;
        private String environment;
        private String dataCenter;
        private String rack;
        private String node;

        public Builder setProductName(String productName) {
            this.productName = productName;
            return this;
        }

        public Builder setProcessId(long processId) {
            this.processId = processId;
            return this;
        }

        public Builder setServiceId(String serviceId) {
            this.serviceId = serviceId;
            return this;
        }

        public Builder setEnvironment(String environment) {
            this.environment = environment;
            return this;
        }

        public Builder setDataCenter(String dataCenter) {
            this.dataCenter = dataCenter;
            return this;
        }

        public Builder setRack(String rack) {
            this.rack = rack;
            return this;
        }

        public Builder setNode(String node) {
            this.node = node;
            return this;
        }

        public JsonStaticData build() {
            return new JsonStaticData(this);
        }
    }
}

