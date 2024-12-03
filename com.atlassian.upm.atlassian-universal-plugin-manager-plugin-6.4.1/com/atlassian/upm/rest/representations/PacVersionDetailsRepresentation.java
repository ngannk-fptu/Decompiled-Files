/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.rest.representations;

import com.atlassian.marketplace.client.api.AddonExternalLinkType;
import com.atlassian.marketplace.client.api.AddonVersionExternalLinkType;
import com.atlassian.marketplace.client.model.Addon;
import com.atlassian.marketplace.client.model.AddonVersion;
import com.atlassian.marketplace.client.model.AddonVersionStatus;
import com.atlassian.marketplace.client.model.Highlight;
import com.atlassian.marketplace.client.model.HtmlString;
import com.atlassian.marketplace.client.model.ImageInfo;
import com.atlassian.marketplace.client.model.LicenseType;
import com.atlassian.marketplace.client.model.Screenshot;
import com.atlassian.upm.MarketplacePlugins;
import com.atlassian.upm.UpmFugueConverters;
import com.atlassian.upm.UpmInformation;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.Sys;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import com.atlassian.upm.rest.representations.HighlightRepresentation;
import com.atlassian.upm.rest.representations.ScreenshotRepresentation;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class PacVersionDetailsRepresentation {
    @JsonProperty
    private final Date releaseDate;
    @JsonProperty
    private final String supportType;
    @JsonProperty
    private final String state;
    @JsonProperty
    private final String pluginSystemVersion;
    @JsonProperty
    private final boolean deployable;
    @JsonProperty
    private final boolean stable;
    @JsonProperty
    private final String licenseType;
    @JsonProperty
    private final URI licenseUrl;
    @JsonProperty
    private final URI supportUrl;
    @JsonProperty
    private final URI reviewsUrl;
    @JsonProperty
    private final String releaseNotes;
    @JsonProperty
    private final URI releaseNotesUrl;
    @JsonProperty
    private final URI documentationUrl;
    @JsonProperty
    private final URI eulaUrl;
    @JsonProperty
    private final URI privacyUrl;
    @JsonProperty
    private final Collection<HighlightRepresentation> highlights;
    @JsonProperty
    private final Collection<ScreenshotRepresentation> screenshots;
    @JsonProperty
    private final String youtubeId;

    @JsonCreator
    public PacVersionDetailsRepresentation(@JsonProperty(value="releaseDate") Date releaseDate, @JsonProperty(value="supportType") String supportType, @JsonProperty(value="state") String state, @JsonProperty(value="pluginSystemVersion") String pluginSystemVersion, @JsonProperty(value="deployable") boolean deployable, @JsonProperty(value="stable") Boolean stable, @JsonProperty(value="licenseType") String licenseType, @JsonProperty(value="licenseUrl") URI licenseUrl, @JsonProperty(value="supportUrl") URI supportUrl, @JsonProperty(value="reviewsUrl") URI reviewsUrl, @JsonProperty(value="releaseNotes") String releaseNotes, @JsonProperty(value="releaseNotesUrl") URI releaseNotesUrl, @JsonProperty(value="documentationUrl") URI documentationUrl, @JsonProperty(value="eulaUrl") URI eulaUrl, @JsonProperty(value="privacyUrl") URI privacyUrl, @JsonProperty(value="highlights") Collection<HighlightRepresentation> highlights, @JsonProperty(value="screenshots") Collection<ScreenshotRepresentation> screenshots, @JsonProperty(value="youtubeId") String youtubeId) {
        this.releaseDate = releaseDate;
        this.supportType = supportType;
        this.state = state;
        this.pluginSystemVersion = pluginSystemVersion;
        this.deployable = deployable;
        this.stable = stable;
        this.licenseType = licenseType;
        this.licenseUrl = licenseUrl;
        this.supportUrl = supportUrl;
        this.reviewsUrl = reviewsUrl;
        this.releaseNotes = releaseNotes;
        this.releaseNotesUrl = releaseNotesUrl;
        this.documentationUrl = documentationUrl;
        this.eulaUrl = eulaUrl;
        this.privacyUrl = privacyUrl;
        this.highlights = highlights == null ? null : Collections.unmodifiableList(new ArrayList<HighlightRepresentation>(highlights));
        this.screenshots = Collections.unmodifiableList(new ArrayList<ScreenshotRepresentation>(screenshots));
        this.youtubeId = youtubeId;
    }

    public PacVersionDetailsRepresentation(Addon addon, AddonVersion version, Option<Plugin> installedPlugin, UpmInformation upm, PermissionEnforcer permissionEnforcer, PluginLicenseRepository licenseRepository, UpmAppManager appManager) {
        this.releaseDate = version.getReleaseDate().toDateMidnight().toDate();
        this.supportType = MarketplacePlugins.getSupportTypeName(addon, version);
        this.state = version.getStatus() == AddonVersionStatus.PUBLIC ? "Public" : "Private";
        this.pluginSystemVersion = "";
        this.releaseNotes = ((HtmlString)version.getReleaseNotes().getOrElse((Object)HtmlString.html(""))).getHtml();
        this.deployable = upm.getPluginKey().equals(addon.getKey()) ? true : version.isDeployable();
        this.stable = !version.isBeta();
        Option<LicenseType> licenseType = UpmFugueConverters.toUpmOption(version.getLicenseType());
        this.licenseType = (String)licenseType.map(LicenseType::getName).getOrElse((String)null);
        this.licenseUrl = (URI)licenseType.flatMap(lt -> UpmFugueConverters.toUpmOption(lt.getAlternateUri())).getOrElse((URI)null);
        this.supportUrl = (URI)addon.getSupportDetailsPageUri().map(Sys.resolveMarketplaceUri()).getOrElse((Object)null);
        this.reviewsUrl = (URI)addon.getReviewDetailsPageUri().map(Sys.resolveMarketplaceUri()).getOrElse((Object)null);
        this.releaseNotesUrl = (URI)version.getExternalLinkUri(AddonVersionExternalLinkType.RELEASE_NOTES).getOrElse((Object)null);
        this.documentationUrl = (URI)version.getExternalLinkUri(AddonVersionExternalLinkType.DOCUMENTATION).getOrElse((Object)null);
        this.eulaUrl = (URI)version.getExternalLinkUri(AddonVersionExternalLinkType.EULA).getOrElse((Object)null);
        this.privacyUrl = (URI)addon.getExternalLinkUri(AddonExternalLinkType.PRIVACY).getOrElse((Object)null);
        this.screenshots = Collections.unmodifiableList(StreamSupport.stream(version.getScreenshots().spliterator(), false).map(this.toScreenshotRep()).collect(Collectors.toList()));
        List highlights = StreamSupport.stream(version.getHighlights().spliterator(), false).map(this.toHighlightRep()).collect(Collectors.toList());
        this.highlights = highlights.isEmpty() ? null : Collections.unmodifiableList(highlights);
        this.youtubeId = (String)version.getYoutubeId().getOrElse((Object)null);
    }

    public Date getReleaseDate() {
        return this.releaseDate;
    }

    public String getSupportType() {
        return this.supportType;
    }

    public String getState() {
        return this.state;
    }

    public String getPluginSystemVersion() {
        return this.pluginSystemVersion;
    }

    public boolean isDeployable() {
        return this.deployable;
    }

    public Boolean isStable() {
        return this.stable;
    }

    public String getLicenseType() {
        return this.licenseType;
    }

    public URI getLicenseUrl() {
        return this.licenseUrl;
    }

    public URI getSupportUrl() {
        return this.supportUrl;
    }

    public URI getReleaseNotesUrl() {
        return this.releaseNotesUrl;
    }

    public URI getDocumentationUrl() {
        return this.documentationUrl;
    }

    public URI getEulaUrl() {
        return this.eulaUrl;
    }

    public URI getPrivacyUrl() {
        return this.privacyUrl;
    }

    public Collection<ScreenshotRepresentation> getScreenshots() {
        return this.screenshots;
    }

    public String getYoutubeId() {
        return this.youtubeId;
    }

    public URI getReviewsUrl() {
        return this.reviewsUrl;
    }

    private Function<Highlight, HighlightRepresentation> toHighlightRep() {
        return h -> new HighlightRepresentation(h.getTitle(), h.getBody().getHtml(), (String)h.getExplanation().getOrElse((Object)null), h.getFullImage().getImageUri(), h.getThumbnailImage().getImageUri());
    }

    private Function<Screenshot, ScreenshotRepresentation> toScreenshotRep() {
        return s -> new ScreenshotRepresentation((String)s.getCaption().getOrElse((Object)""), 0, 0, s.getImage().getImageUri(), (String)s.getImage().getImageContentType(ImageInfo.Size.DEFAULT_SIZE, ImageInfo.Resolution.DEFAULT_RESOLUTION).getOrElse((Object)""), "");
    }
}

