/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.pac;

import com.atlassian.upm.core.HostingType;

public final class ClientContext {
    public static final String CLIENT_CONTEXT_HEADER = "X-Pac-Client-Info";
    private static final String CLIENT_TYPE_FIELD = "client";
    private static final String PRODUCT_NAME_FIELD = "product";
    private static final String PRODUCT_VERSION_FIELD = "version";
    private static final String SERVER_ID_FIELD = "sid";
    private static final String SEN_FIELD = "sen";
    private static final String PRODUCT_EVALUATION_FIELD = "eval";
    private static final String ON_DEMAND_FIELD = "ondemand";
    private static final String PRODUCT_HOSTING_FIELD = "hosting";
    private static final String USER_COUNT_FIELD = "users";
    private static final String FIELD_SEPARATOR = ",";
    private static final String VALUE_SEPARATOR = "=";
    private final String clientType;
    private final String productName;
    private final String productVersion;
    private final String serverId;
    private final String sen;
    private final Boolean productEvaluation;
    private final Boolean onDemand;
    private final String hosting;
    private final Integer userCount;

    private ClientContext(Builder builder) {
        this.clientType = builder.clientType;
        this.productName = builder.productName;
        this.productVersion = builder.productVersion;
        this.serverId = builder.serverId;
        this.sen = builder.sen;
        this.productEvaluation = builder.productEvaluation;
        this.onDemand = builder.onDemand;
        this.hosting = builder.hosting;
        this.userCount = builder.userCount;
    }

    public String getClientType() {
        return this.clientType;
    }

    public String getProductName() {
        return this.productName;
    }

    public String getProductVersion() {
        return this.productVersion;
    }

    public String getServerId() {
        return this.serverId;
    }

    public String getSen() {
        return this.sen;
    }

    public Boolean getProductEvaluation() {
        return this.productEvaluation;
    }

    public Boolean getOnDemand() {
        return this.onDemand;
    }

    public Integer getUserCount() {
        return this.userCount;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        this.addField(buf, CLIENT_TYPE_FIELD, this.clientType);
        this.addField(buf, PRODUCT_NAME_FIELD, this.productName);
        this.addField(buf, PRODUCT_VERSION_FIELD, this.productVersion);
        this.addField(buf, SERVER_ID_FIELD, this.serverId);
        this.addField(buf, SEN_FIELD, this.sen);
        this.addField(buf, PRODUCT_EVALUATION_FIELD, this.productEvaluation);
        this.addField(buf, USER_COUNT_FIELD, this.userCount);
        this.addField(buf, ON_DEMAND_FIELD, this.onDemand);
        this.addField(buf, PRODUCT_HOSTING_FIELD, this.hosting);
        return buf.toString();
    }

    public static ClientContext fromString(String s) {
        Builder builder = new Builder();
        if (s != null) {
            for (String field : s.trim().split(FIELD_SEPARATOR)) {
                String[] parts = field.trim().split(VALUE_SEPARATOR);
                if (parts.length != 2) continue;
                String name = parts[0];
                String value = parts[1];
                if (name.equals(CLIENT_TYPE_FIELD)) {
                    builder.clientType(value);
                    continue;
                }
                if (name.equals(PRODUCT_NAME_FIELD)) {
                    builder.productName(value);
                    continue;
                }
                if (name.equals(PRODUCT_VERSION_FIELD)) {
                    builder.productVersion(value);
                    continue;
                }
                if (name.equals(SERVER_ID_FIELD)) {
                    builder.serverId(value);
                    continue;
                }
                if (name.equals(SEN_FIELD)) {
                    builder.sen(value);
                    continue;
                }
                if (name.equals(PRODUCT_EVALUATION_FIELD)) {
                    builder.productEvaluation(Boolean.parseBoolean(value));
                    continue;
                }
                if (name.equals(ON_DEMAND_FIELD)) {
                    builder.onDemand(Boolean.parseBoolean(value));
                    continue;
                }
                if (name.equals(PRODUCT_HOSTING_FIELD)) {
                    try {
                        builder.hosting(HostingType.valueOf(value));
                    }
                    catch (IllegalArgumentException illegalArgumentException) {}
                    continue;
                }
                if (!name.equals(USER_COUNT_FIELD)) continue;
                try {
                    builder.userCount(Integer.parseInt(value));
                }
                catch (NumberFormatException numberFormatException) {
                    // empty catch block
                }
            }
        }
        return builder.build();
    }

    private final void addField(StringBuilder buf, String name, Object value) {
        if (value != null) {
            if (buf.length() > 0) {
                buf.append(FIELD_SEPARATOR);
            }
            buf.append(name);
            buf.append(VALUE_SEPARATOR);
            buf.append(value.toString());
        }
    }

    public static final class Builder {
        private String clientType;
        private String productName;
        private String productVersion;
        private String serverId;
        private String sen;
        private Boolean productEvaluation;
        private Boolean onDemand;
        private String hosting;
        private Integer userCount;

        public Builder clientType(String clientType) {
            this.clientType = clientType;
            return this;
        }

        public Builder productName(String productName) {
            this.productName = productName;
            return this;
        }

        public Builder productVersion(String productVersion) {
            this.productVersion = productVersion;
            return this;
        }

        public Builder serverId(String serverId) {
            this.serverId = serverId;
            return this;
        }

        public Builder sen(String sen) {
            this.sen = sen;
            return this;
        }

        public Builder productEvaluation(Boolean productEvaluation) {
            this.productEvaluation = productEvaluation;
            return this;
        }

        public Builder onDemand(Boolean onDemand) {
            this.onDemand = onDemand;
            return this;
        }

        public Builder userCount(Integer userCount) {
            this.userCount = userCount;
            return this;
        }

        public Builder hosting(HostingType hosting) {
            this.hosting = hosting.getKey();
            return this;
        }

        public ClientContext build() {
            return new ClientContext(this);
        }
    }
}

