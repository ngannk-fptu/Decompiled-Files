/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.domain.ProductType
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.user.request.v2;

import com.atlassian.cmpt.domain.ProductType;
import java.net.URL;
import lombok.Generated;

public class UsersMigrationV2Request {
    private final String sen;
    private final ProductType productType;
    private final String migrationScopeId;
    private final URL data;
    private final String pluginVersion;
    private final String confluenceVersion;
    private final String databaseVersion;

    @Generated
    public UsersMigrationV2Request(String sen, ProductType productType, String migrationScopeId, URL data, String pluginVersion, String confluenceVersion, String databaseVersion) {
        this.sen = sen;
        this.productType = productType;
        this.migrationScopeId = migrationScopeId;
        this.data = data;
        this.pluginVersion = pluginVersion;
        this.confluenceVersion = confluenceVersion;
        this.databaseVersion = databaseVersion;
    }

    @Generated
    public String getSen() {
        return this.sen;
    }

    @Generated
    public ProductType getProductType() {
        return this.productType;
    }

    @Generated
    public String getMigrationScopeId() {
        return this.migrationScopeId;
    }

    @Generated
    public URL getData() {
        return this.data;
    }

    @Generated
    public String getPluginVersion() {
        return this.pluginVersion;
    }

    @Generated
    public String getConfluenceVersion() {
        return this.confluenceVersion;
    }

    @Generated
    public String getDatabaseVersion() {
        return this.databaseVersion;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UsersMigrationV2Request)) {
            return false;
        }
        UsersMigrationV2Request other = (UsersMigrationV2Request)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$sen = this.getSen();
        String other$sen = other.getSen();
        if (this$sen == null ? other$sen != null : !this$sen.equals(other$sen)) {
            return false;
        }
        ProductType this$productType = this.getProductType();
        ProductType other$productType = other.getProductType();
        if (this$productType == null ? other$productType != null : !this$productType.equals(other$productType)) {
            return false;
        }
        String this$migrationScopeId = this.getMigrationScopeId();
        String other$migrationScopeId = other.getMigrationScopeId();
        if (this$migrationScopeId == null ? other$migrationScopeId != null : !this$migrationScopeId.equals(other$migrationScopeId)) {
            return false;
        }
        URL this$data = this.getData();
        URL other$data = other.getData();
        if (this$data == null ? other$data != null : !((Object)this$data).equals(other$data)) {
            return false;
        }
        String this$pluginVersion = this.getPluginVersion();
        String other$pluginVersion = other.getPluginVersion();
        if (this$pluginVersion == null ? other$pluginVersion != null : !this$pluginVersion.equals(other$pluginVersion)) {
            return false;
        }
        String this$confluenceVersion = this.getConfluenceVersion();
        String other$confluenceVersion = other.getConfluenceVersion();
        if (this$confluenceVersion == null ? other$confluenceVersion != null : !this$confluenceVersion.equals(other$confluenceVersion)) {
            return false;
        }
        String this$databaseVersion = this.getDatabaseVersion();
        String other$databaseVersion = other.getDatabaseVersion();
        return !(this$databaseVersion == null ? other$databaseVersion != null : !this$databaseVersion.equals(other$databaseVersion));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof UsersMigrationV2Request;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $sen = this.getSen();
        result = result * 59 + ($sen == null ? 43 : $sen.hashCode());
        ProductType $productType = this.getProductType();
        result = result * 59 + ($productType == null ? 43 : $productType.hashCode());
        String $migrationScopeId = this.getMigrationScopeId();
        result = result * 59 + ($migrationScopeId == null ? 43 : $migrationScopeId.hashCode());
        URL $data = this.getData();
        result = result * 59 + ($data == null ? 43 : ((Object)$data).hashCode());
        String $pluginVersion = this.getPluginVersion();
        result = result * 59 + ($pluginVersion == null ? 43 : $pluginVersion.hashCode());
        String $confluenceVersion = this.getConfluenceVersion();
        result = result * 59 + ($confluenceVersion == null ? 43 : $confluenceVersion.hashCode());
        String $databaseVersion = this.getDatabaseVersion();
        result = result * 59 + ($databaseVersion == null ? 43 : $databaseVersion.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "UsersMigrationV2Request(sen=" + this.getSen() + ", productType=" + this.getProductType() + ", migrationScopeId=" + this.getMigrationScopeId() + ", data=" + this.getData() + ", pluginVersion=" + this.getPluginVersion() + ", confluenceVersion=" + this.getConfluenceVersion() + ", databaseVersion=" + this.getDatabaseVersion() + ")";
    }
}

