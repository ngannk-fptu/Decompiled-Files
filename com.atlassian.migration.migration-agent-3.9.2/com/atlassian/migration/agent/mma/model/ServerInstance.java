/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.mma.model;

import lombok.Generated;

public class ServerInstance {
    private String cloudId;
    private String serverId;
    private String supportEntitlementNumber;
    private String productKey;
    private String productVersion;
    private String cmaVersion;
    private String organisationName;
    private String nickname;
    private String serverBaseUrl;

    @Generated
    ServerInstance(String cloudId, String serverId, String supportEntitlementNumber, String productKey, String productVersion, String cmaVersion, String organisationName, String nickname, String serverBaseUrl) {
        this.cloudId = cloudId;
        this.serverId = serverId;
        this.supportEntitlementNumber = supportEntitlementNumber;
        this.productKey = productKey;
        this.productVersion = productVersion;
        this.cmaVersion = cmaVersion;
        this.organisationName = organisationName;
        this.nickname = nickname;
        this.serverBaseUrl = serverBaseUrl;
    }

    @Generated
    public static ServerInstanceBuilder builder() {
        return new ServerInstanceBuilder();
    }

    @Generated
    public String getCloudId() {
        return this.cloudId;
    }

    @Generated
    public String getServerId() {
        return this.serverId;
    }

    @Generated
    public String getSupportEntitlementNumber() {
        return this.supportEntitlementNumber;
    }

    @Generated
    public String getProductKey() {
        return this.productKey;
    }

    @Generated
    public String getProductVersion() {
        return this.productVersion;
    }

    @Generated
    public String getCmaVersion() {
        return this.cmaVersion;
    }

    @Generated
    public String getOrganisationName() {
        return this.organisationName;
    }

    @Generated
    public String getNickname() {
        return this.nickname;
    }

    @Generated
    public String getServerBaseUrl() {
        return this.serverBaseUrl;
    }

    @Generated
    public String toString() {
        return "ServerInstance(cloudId=" + this.getCloudId() + ", serverId=" + this.getServerId() + ", supportEntitlementNumber=" + this.getSupportEntitlementNumber() + ", productKey=" + this.getProductKey() + ", productVersion=" + this.getProductVersion() + ", cmaVersion=" + this.getCmaVersion() + ", organisationName=" + this.getOrganisationName() + ", nickname=" + this.getNickname() + ", serverBaseUrl=" + this.getServerBaseUrl() + ")";
    }

    @Generated
    public static class ServerInstanceBuilder {
        @Generated
        private String cloudId;
        @Generated
        private String serverId;
        @Generated
        private String supportEntitlementNumber;
        @Generated
        private String productKey;
        @Generated
        private String productVersion;
        @Generated
        private String cmaVersion;
        @Generated
        private String organisationName;
        @Generated
        private String nickname;
        @Generated
        private String serverBaseUrl;

        @Generated
        ServerInstanceBuilder() {
        }

        @Generated
        public ServerInstanceBuilder cloudId(String cloudId) {
            this.cloudId = cloudId;
            return this;
        }

        @Generated
        public ServerInstanceBuilder serverId(String serverId) {
            this.serverId = serverId;
            return this;
        }

        @Generated
        public ServerInstanceBuilder supportEntitlementNumber(String supportEntitlementNumber) {
            this.supportEntitlementNumber = supportEntitlementNumber;
            return this;
        }

        @Generated
        public ServerInstanceBuilder productKey(String productKey) {
            this.productKey = productKey;
            return this;
        }

        @Generated
        public ServerInstanceBuilder productVersion(String productVersion) {
            this.productVersion = productVersion;
            return this;
        }

        @Generated
        public ServerInstanceBuilder cmaVersion(String cmaVersion) {
            this.cmaVersion = cmaVersion;
            return this;
        }

        @Generated
        public ServerInstanceBuilder organisationName(String organisationName) {
            this.organisationName = organisationName;
            return this;
        }

        @Generated
        public ServerInstanceBuilder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        @Generated
        public ServerInstanceBuilder serverBaseUrl(String serverBaseUrl) {
            this.serverBaseUrl = serverBaseUrl;
            return this;
        }

        @Generated
        public ServerInstance build() {
            return new ServerInstance(this.cloudId, this.serverId, this.supportEntitlementNumber, this.productKey, this.productVersion, this.cmaVersion, this.organisationName, this.nickname, this.serverBaseUrl);
        }

        @Generated
        public String toString() {
            return "ServerInstance.ServerInstanceBuilder(cloudId=" + this.cloudId + ", serverId=" + this.serverId + ", supportEntitlementNumber=" + this.supportEntitlementNumber + ", productKey=" + this.productKey + ", productVersion=" + this.productVersion + ", cmaVersion=" + this.cmaVersion + ", organisationName=" + this.organisationName + ", nickname=" + this.nickname + ", serverBaseUrl=" + this.serverBaseUrl + ")";
        }
    }
}

