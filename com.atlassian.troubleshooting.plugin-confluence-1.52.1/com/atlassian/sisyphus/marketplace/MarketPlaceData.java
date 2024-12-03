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

import com.atlassian.sisyphus.marketplace.Approval;
import com.atlassian.sisyphus.marketplace.Category;
import com.atlassian.sisyphus.marketplace.CompatibleApplication;
import com.atlassian.sisyphus.marketplace.Deployment;
import com.atlassian.sisyphus.marketplace.LastModified;
import com.atlassian.sisyphus.marketplace.Link;
import com.atlassian.sisyphus.marketplace.Media;
import com.atlassian.sisyphus.marketplace.ReviewSummary;
import com.atlassian.sisyphus.marketplace.Reviews;
import com.atlassian.sisyphus.marketplace.Vendor;
import com.atlassian.sisyphus.marketplace.Version;
import com.atlassian.sisyphus.marketplace.Versions;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder(value={"links", "creationDate", "name", "pluginKey", "description", "summary", "approval", "deployable", "downloadCount", "lastModified", "vendor", "media", "reviewSummary", "reviews", "categories", "version", "versions", "isOldVersion", "compatibleApplications", "deployment", "googleAnalyticsId"})
public class MarketPlaceData {
    @JsonProperty(value="links")
    private List<Link> links = new ArrayList<Link>();
    @JsonProperty(value="creationDate")
    private String creationDate;
    @JsonProperty(value="name")
    private String name;
    @JsonProperty(value="pluginKey")
    private String pluginKey;
    @JsonProperty(value="description")
    private String description;
    @JsonProperty(value="summary")
    private String summary;
    @JsonProperty(value="approval")
    private Approval approval;
    @JsonProperty(value="deployable")
    private Boolean deployable;
    @JsonProperty(value="downloadCount")
    private Long downloadCount;
    @JsonProperty(value="lastModified")
    private LastModified lastModified;
    @JsonProperty(value="vendor")
    private Vendor vendor;
    @JsonProperty(value="media")
    private Media media;
    @JsonProperty(value="reviewSummary")
    private ReviewSummary reviewSummary;
    @JsonProperty(value="reviews")
    private Reviews reviews;
    @JsonProperty(value="categories")
    private List<Category> categories = new ArrayList<Category>();
    @JsonProperty(value="version")
    private Version version;
    @JsonProperty(value="versions")
    private Versions versions;
    @JsonProperty(value="isOldVersion")
    private Boolean isOldVersion;
    @JsonProperty(value="compatibleApplications")
    private List<CompatibleApplication> compatibleApplications = new ArrayList<CompatibleApplication>();
    @JsonProperty(value="deployment")
    private Deployment deployment;
    @JsonProperty(value="googleAnalyticsId")
    private String googleAnalyticsId;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private Date cachedTime;
    private Date loadedAt = new Date();

    public Date getCachedTime() {
        return this.cachedTime;
    }

    public void setCachedTime(Date cachedTime) {
        this.cachedTime = cachedTime;
    }

    public Date getLoadedAt() {
        return this.loadedAt;
    }

    @JsonProperty(value="links")
    public List<Link> getLinks() {
        return this.links;
    }

    @JsonProperty(value="links")
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    @JsonProperty(value="creationDate")
    public String getCreationDate() {
        return this.creationDate;
    }

    @JsonProperty(value="creationDate")
    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    @JsonProperty(value="name")
    public String getName() {
        return this.name;
    }

    @JsonProperty(value="name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty(value="pluginKey")
    public String getPluginKey() {
        return this.pluginKey;
    }

    @JsonProperty(value="pluginKey")
    public void setPluginKey(String pluginKey) {
        this.pluginKey = pluginKey;
    }

    @JsonProperty(value="description")
    public String getDescription() {
        return this.description;
    }

    @JsonProperty(value="description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty(value="summary")
    public String getSummary() {
        return this.summary;
    }

    @JsonProperty(value="summary")
    public void setSummary(String summary) {
        this.summary = summary;
    }

    @JsonProperty(value="approval")
    public Approval getApproval() {
        return this.approval;
    }

    @JsonProperty(value="approval")
    public void setApproval(Approval approval) {
        this.approval = approval;
    }

    @JsonProperty(value="deployable")
    public Boolean getDeployable() {
        return this.deployable;
    }

    @JsonProperty(value="deployable")
    public void setDeployable(Boolean deployable) {
        this.deployable = deployable;
    }

    @JsonProperty(value="downloadCount")
    public Long getDownloadCount() {
        return this.downloadCount;
    }

    @JsonProperty(value="downloadCount")
    public void setDownloadCount(Long downloadCount) {
        this.downloadCount = downloadCount;
    }

    @JsonProperty(value="lastModified")
    public LastModified getLastModified() {
        return this.lastModified;
    }

    @JsonProperty(value="lastModified")
    public void setLastModified(LastModified lastModified) {
        this.lastModified = lastModified;
    }

    @JsonProperty(value="vendor")
    public Vendor getVendor() {
        return this.vendor;
    }

    @JsonProperty(value="vendor")
    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    @JsonProperty(value="media")
    public Media getMedia() {
        return this.media;
    }

    @JsonProperty(value="media")
    public void setMedia(Media media) {
        this.media = media;
    }

    @JsonProperty(value="reviewSummary")
    public ReviewSummary getReviewSummary() {
        return this.reviewSummary;
    }

    @JsonProperty(value="reviewSummary")
    public void setReviewSummary(ReviewSummary reviewSummary) {
        this.reviewSummary = reviewSummary;
    }

    @JsonProperty(value="reviews")
    public Reviews getReviews() {
        return this.reviews;
    }

    @JsonProperty(value="reviews")
    public void setReviews(Reviews reviews) {
        this.reviews = reviews;
    }

    @JsonProperty(value="categories")
    public List<Category> getCategories() {
        return this.categories;
    }

    @JsonProperty(value="categories")
    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    @JsonProperty(value="version")
    public Version getVersion() {
        return this.version;
    }

    @JsonProperty(value="version")
    public void setVersion(Version version) {
        this.version = version;
    }

    @JsonProperty(value="versions")
    public Versions getVersions() {
        return this.versions;
    }

    @JsonProperty(value="versions")
    public void setVersions(Versions versions) {
        this.versions = versions;
    }

    @JsonProperty(value="isOldVersion")
    public Boolean getIsOldVersion() {
        return this.isOldVersion;
    }

    @JsonProperty(value="isOldVersion")
    public void setIsOldVersion(Boolean isOldVersion) {
        this.isOldVersion = isOldVersion;
    }

    @JsonProperty(value="compatibleApplications")
    public List<CompatibleApplication> getCompatibleApplications() {
        return this.compatibleApplications;
    }

    @JsonProperty(value="compatibleApplications")
    public void setCompatibleApplications(List<CompatibleApplication> compatibleApplications) {
        this.compatibleApplications = compatibleApplications;
    }

    @JsonProperty(value="deployment")
    public Deployment getDeployment() {
        return this.deployment;
    }

    @JsonProperty(value="deployment")
    public void setDeployment(Deployment deployment) {
        this.deployment = deployment;
    }

    @JsonProperty(value="googleAnalyticsId")
    public String getGoogleAnalyticsId() {
        return this.googleAnalyticsId;
    }

    @JsonProperty(value="googleAnalyticsId")
    public void setGoogleAnalyticsId(String googleAnalyticsId) {
        this.googleAnalyticsId = googleAnalyticsId;
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

