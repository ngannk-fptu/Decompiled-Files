/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonAnyGetter
 *  com.fasterxml.jackson.annotation.JsonAnySetter
 *  com.fasterxml.jackson.annotation.JsonIgnoreProperties
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  com.fasterxml.jackson.annotation.JsonPropertyOrder
 *  com.fasterxml.jackson.databind.annotation.JsonSerialize
 *  com.fasterxml.jackson.databind.annotation.JsonSerialize$Inclusion
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.sisyphus.marketplace;

import com.atlassian.sisyphus.marketplace.Compatibility;
import com.atlassian.sisyphus.marketplace.CompatibleApplication;
import com.atlassian.sisyphus.marketplace.Deployment;
import com.atlassian.sisyphus.marketplace.License;
import com.atlassian.sisyphus.marketplace.Link;
import com.atlassian.sisyphus.marketplace.MarketplaceType;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder(value={"links", "buildNumber", "releaseDate", "releasedBy", "stable", "status", "license", "version", "deployable", "deployment", "supportType", "pluginSystemVersion", "addOnType", "summary", "compatibleApplications", "compatibilities", "marketplaceType", "marketplaceAgreementAccepted", "autoUpdateAllowed", "screenshots"})
public class Version {
    @JsonProperty(value="links")
    private List<Link> links = new ArrayList<Link>();
    @JsonProperty(value="buildNumber")
    private Long buildNumber;
    @JsonProperty(value="releaseDate")
    private String releaseDate;
    @JsonProperty(value="releasedBy")
    private String releasedBy;
    @JsonProperty(value="stable")
    private Boolean stable;
    @JsonProperty(value="status")
    private String status;
    @JsonProperty(value="license")
    private License license;
    @JsonProperty(value="version")
    private String version;
    @JsonProperty(value="deployable")
    private Boolean deployable;
    @JsonProperty(value="deployment")
    private Deployment deployment;
    @JsonProperty(value="supportType")
    private String supportType;
    @JsonProperty(value="pluginSystemVersion")
    private String pluginSystemVersion;
    @JsonProperty(value="addOnType")
    private String addOnType;
    @JsonProperty(value="summary")
    private String summary;
    @JsonProperty(value="compatibleApplications")
    private List<CompatibleApplication> compatibleApplications = new ArrayList<CompatibleApplication>();
    @JsonProperty(value="compatibilities")
    private List<Compatibility> compatibilities = new ArrayList<Compatibility>();
    @JsonProperty(value="marketplaceType")
    private MarketplaceType marketplaceType;
    @JsonProperty(value="marketplaceAgreementAccepted")
    private Boolean marketplaceAgreementAccepted;
    @JsonProperty(value="autoUpdateAllowed")
    private Boolean autoUpdateAllowed;
    @JsonProperty(value="screenshots")
    private List<Object> screenshots = new ArrayList<Object>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty(value="links")
    public List<Link> getLinks() {
        return this.links;
    }

    @JsonProperty(value="links")
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    @JsonProperty(value="buildNumber")
    public Long getBuildNumber() {
        return this.buildNumber;
    }

    @JsonProperty(value="buildNumber")
    public void setBuildNumber(Long buildNumber) {
        this.buildNumber = buildNumber;
    }

    @JsonProperty(value="releaseDate")
    public String getReleaseDate() {
        return this.releaseDate;
    }

    @JsonProperty(value="releaseDate")
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @JsonProperty(value="releasedBy")
    public String getReleasedBy() {
        return this.releasedBy;
    }

    @JsonProperty(value="releasedBy")
    public void setReleasedBy(String releasedBy) {
        this.releasedBy = releasedBy;
    }

    @JsonProperty(value="stable")
    public Boolean getStable() {
        return this.stable;
    }

    @JsonProperty(value="stable")
    public void setStable(Boolean stable) {
        this.stable = stable;
    }

    @JsonProperty(value="status")
    public String getStatus() {
        return this.status;
    }

    @JsonProperty(value="status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty(value="license")
    public License getLicense() {
        return this.license;
    }

    @JsonProperty(value="license")
    public void setLicense(License license) {
        this.license = license;
    }

    @JsonProperty(value="version")
    public String getVersion() {
        return this.version;
    }

    @JsonProperty(value="version")
    public void setVersion(String version) {
        this.version = version;
    }

    @JsonProperty(value="deployable")
    public Boolean getDeployable() {
        return this.deployable;
    }

    @JsonProperty(value="deployable")
    public void setDeployable(Boolean deployable) {
        this.deployable = deployable;
    }

    @JsonProperty(value="deployment")
    public Deployment getDeployment() {
        return this.deployment;
    }

    @JsonProperty(value="deployment")
    public void setDeployment(Deployment deployment) {
        this.deployment = deployment;
    }

    @JsonProperty(value="supportType")
    public String getSupportType() {
        return this.supportType;
    }

    @JsonProperty(value="supportType")
    public void setSupportType(String supportType) {
        this.supportType = supportType;
    }

    @JsonProperty(value="pluginSystemVersion")
    public String getPluginSystemVersion() {
        return this.pluginSystemVersion;
    }

    @JsonProperty(value="pluginSystemVersion")
    public void setPluginSystemVersion(String pluginSystemVersion) {
        this.pluginSystemVersion = pluginSystemVersion;
    }

    @JsonProperty(value="addOnType")
    public String getAddOnType() {
        return this.addOnType;
    }

    @JsonProperty(value="addOnType")
    public void setAddOnType(String addOnType) {
        this.addOnType = addOnType;
    }

    @JsonProperty(value="summary")
    public String getSummary() {
        return this.summary;
    }

    @JsonProperty(value="summary")
    public void setSummary(String summary) {
        this.summary = summary;
    }

    @JsonProperty(value="compatibleApplications")
    public List<CompatibleApplication> getCompatibleApplications() {
        return this.compatibleApplications;
    }

    @JsonProperty(value="compatibleApplications")
    public void setCompatibleApplications(List<CompatibleApplication> compatibleApplications) {
        this.compatibleApplications = compatibleApplications;
    }

    @JsonProperty(value="compatibilities")
    public List<Compatibility> getCompatibilities() {
        return this.compatibilities;
    }

    @JsonProperty(value="compatibilities")
    public void setCompatibilities(List<Compatibility> compatibilities) {
        this.compatibilities = compatibilities;
    }

    @JsonProperty(value="marketplaceType")
    public MarketplaceType getMarketplaceType() {
        return this.marketplaceType;
    }

    @JsonProperty(value="marketplaceType")
    public void setMarketplaceType(MarketplaceType marketplaceType) {
        this.marketplaceType = marketplaceType;
    }

    @JsonProperty(value="marketplaceAgreementAccepted")
    public Boolean getMarketplaceAgreementAccepted() {
        return this.marketplaceAgreementAccepted;
    }

    @JsonProperty(value="marketplaceAgreementAccepted")
    public void setMarketplaceAgreementAccepted(Boolean marketplaceAgreementAccepted) {
        this.marketplaceAgreementAccepted = marketplaceAgreementAccepted;
    }

    @JsonProperty(value="autoUpdateAllowed")
    public Boolean getAutoUpdateAllowed() {
        return this.autoUpdateAllowed;
    }

    @JsonProperty(value="autoUpdateAllowed")
    public void setAutoUpdateAllowed(Boolean autoUpdateAllowed) {
        this.autoUpdateAllowed = autoUpdateAllowed;
    }

    @JsonProperty(value="screenshots")
    public List<Object> getScreenshots() {
        return this.screenshots;
    }

    @JsonProperty(value="screenshots")
    public void setScreenshots(List<Object> screenshots) {
        this.screenshots = screenshots;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}

