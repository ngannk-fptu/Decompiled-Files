/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  io.atlassian.fugue.Option
 *  javax.annotation.Nullable
 *  org.joda.time.LocalDate
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.marketplace.client.api.AddonCategoryId;
import com.atlassian.marketplace.client.api.AddonExternalLinkType;
import com.atlassian.marketplace.client.api.AddonVersionExternalLinkType;
import com.atlassian.marketplace.client.api.ApplicationKey;
import com.atlassian.marketplace.client.api.ArtifactId;
import com.atlassian.marketplace.client.api.ImageId;
import com.atlassian.marketplace.client.api.LicenseTypeId;
import com.atlassian.marketplace.client.api.ResourceId;
import com.atlassian.marketplace.client.api.UriTemplate;
import com.atlassian.marketplace.client.api.VendorExternalLinkType;
import com.atlassian.marketplace.client.api.VendorId;
import com.atlassian.marketplace.client.encoding.SchemaViolation;
import com.atlassian.marketplace.client.impl.EntityValidator;
import com.atlassian.marketplace.client.impl.SchemaViolationException;
import com.atlassian.marketplace.client.model.Addon;
import com.atlassian.marketplace.client.model.AddonStatus;
import com.atlassian.marketplace.client.model.AddonVersion;
import com.atlassian.marketplace.client.model.AddonVersionBase;
import com.atlassian.marketplace.client.model.AddonVersionStatus;
import com.atlassian.marketplace.client.model.Address;
import com.atlassian.marketplace.client.model.ApplicationVersion;
import com.atlassian.marketplace.client.model.ApplicationVersionStatus;
import com.atlassian.marketplace.client.model.Highlight;
import com.atlassian.marketplace.client.model.HtmlString;
import com.atlassian.marketplace.client.model.ImageInfo;
import com.atlassian.marketplace.client.model.LicenseType;
import com.atlassian.marketplace.client.model.Link;
import com.atlassian.marketplace.client.model.Links;
import com.atlassian.marketplace.client.model.PaymentModel;
import com.atlassian.marketplace.client.model.Screenshot;
import com.atlassian.marketplace.client.model.SupportDetails;
import com.atlassian.marketplace.client.model.TestModelBuilders;
import com.atlassian.marketplace.client.model.Vendor;
import com.atlassian.marketplace.client.model.VendorPrograms;
import com.atlassian.marketplace.client.model.VendorSummary;
import com.atlassian.marketplace.client.model.VersionCompatibility;
import com.atlassian.marketplace.client.util.EntityFunctions;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.atlassian.fugue.Option;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import org.joda.time.LocalDate;

public abstract class ModelBuilders {
    private ModelBuilders() {
    }

    private static InvalidModelException modelException(SchemaViolationException e) {
        return new InvalidModelException(e.getSchemaViolations());
    }

    public static AddonBuilder addon() {
        return new AddonBuilder();
    }

    public static AddonBuilder addon(Addon from) {
        return new AddonBuilder(from);
    }

    public static AddonVersionBuilder addonVersion() {
        return new AddonVersionBuilder();
    }

    public static AddonVersionBuilder addonVersion(AddonVersion from) {
        return new AddonVersionBuilder(from);
    }

    public static AddressBuilder address() {
        return new AddressBuilder();
    }

    public static ApplicationVersionBuilder applicationVersion() {
        return new ApplicationVersionBuilder();
    }

    public static ApplicationVersionBuilder applicationVersion(ApplicationVersion from) {
        return new ApplicationVersionBuilder(from);
    }

    public static HighlightBuilder highlight() {
        return new HighlightBuilder();
    }

    public static LinksBuilder links() {
        return new LinksBuilder();
    }

    public static ScreenshotBuilder screenshot() {
        return new ScreenshotBuilder();
    }

    public static VendorBuilder vendor() {
        return new VendorBuilder();
    }

    public static VendorBuilder vendor(Vendor from) {
        return new VendorBuilder(from);
    }

    public static VersionCompatibilityBuilder versionCompatibility(VersionCompatibility from) {
        return new VersionCompatibilityBuilder(from);
    }

    public static VersionCompatibility versionCompatibilityForCloud(ApplicationKey appKey) {
        return new VersionCompatibility(appKey, new VersionCompatibility.CompatibilityHosting((Option<VersionCompatibility.CompatibilityHostingBounds>)Option.none(VersionCompatibility.CompatibilityHostingBounds.class), (Option<VersionCompatibility.CompatibilityHostingBounds>)Option.none(VersionCompatibility.CompatibilityHostingBounds.class), (Option<Boolean>)Option.some((Object)true)));
    }

    public static VersionCompatibility versionCompatibilityForServer(ApplicationKey appKey, int minBuild, int maxBuild) {
        return new VersionCompatibility(appKey, new VersionCompatibility.CompatibilityHosting((Option<VersionCompatibility.CompatibilityHostingBounds>)Option.some((Object)ModelBuilders.makeHostingBounds(minBuild, maxBuild)), (Option<VersionCompatibility.CompatibilityHostingBounds>)Option.none(VersionCompatibility.CompatibilityHostingBounds.class), (Option<Boolean>)Option.some((Object)false)));
    }

    public static VersionCompatibility versionCompatibilityForDataCenter(ApplicationKey appKey, int minBuild, int maxBuild) {
        return new VersionCompatibility(appKey, new VersionCompatibility.CompatibilityHosting((Option<VersionCompatibility.CompatibilityHostingBounds>)Option.none(VersionCompatibility.CompatibilityHostingBounds.class), (Option<VersionCompatibility.CompatibilityHostingBounds>)Option.some((Object)ModelBuilders.makeHostingBounds(minBuild, maxBuild)), (Option<Boolean>)Option.some((Object)false)));
    }

    public static VersionCompatibility versionCompatibilityForServerAndDataCenter(ApplicationKey appKey, int minBuild, int maxBuild, int dataCenterMinBuild, int dataCenterMaxBuild) {
        return new VersionCompatibility(appKey, new VersionCompatibility.CompatibilityHosting((Option<VersionCompatibility.CompatibilityHostingBounds>)Option.some((Object)ModelBuilders.makeHostingBounds(minBuild, maxBuild)), (Option<VersionCompatibility.CompatibilityHostingBounds>)Option.some((Object)ModelBuilders.makeHostingBounds(dataCenterMinBuild, dataCenterMaxBuild)), (Option<Boolean>)Option.some((Object)false)));
    }

    public static VersionCompatibility versionCompatibilityForServerAndCloud(ApplicationKey appKey, int minBuild, int maxBuild) {
        return new VersionCompatibility(appKey, new VersionCompatibility.CompatibilityHosting((Option<VersionCompatibility.CompatibilityHostingBounds>)Option.some((Object)ModelBuilders.makeHostingBounds(minBuild, maxBuild)), (Option<VersionCompatibility.CompatibilityHostingBounds>)Option.none(VersionCompatibility.CompatibilityHostingBounds.class), (Option<Boolean>)Option.some((Object)true)));
    }

    private static VersionCompatibility.CompatibilityHostingBounds makeHostingBounds(int min, int max) {
        return new VersionCompatibility.CompatibilityHostingBounds(new VersionCompatibility.VersionPoint(min, (Option<String>)Option.none(String.class)), new VersionCompatibility.VersionPoint(max, (Option<String>)Option.none(String.class)));
    }

    private static <T> Option<ImmutableList<T>> copyOptionalList(Option<Iterable<T>> list) {
        Iterator iterator = list.iterator();
        if (iterator.hasNext()) {
            Iterable l = (Iterable)iterator.next();
            return Option.some((Object)ImmutableList.copyOf((Iterable)l));
        }
        return Option.none();
    }

    public static class VendorBuilder
    extends UnsafeBuilderWithLinks<Vendor, VendorBuilder> {
        private String name;
        private Option<String> description = Option.none();
        private String email;
        private Option<Address> address = Option.none();
        private Option<String> phone = Option.none();
        private Option<String> otherContactDetails = Option.none();
        private Map<String, URI> externalLinks = new HashMap<String, URI>();
        private SupportDetails supportDetails = new SupportDetails();
        private VendorPrograms programs;

        private VendorBuilder() {
            this.links.put("alternate", TestModelBuilders.DEFAULT_URI);
            this.programs = new VendorPrograms();
        }

        private VendorBuilder(Vendor from) {
            this.links.put(from.getLinks());
            this.name = from.getName();
            this.description = from.getDescription();
            this.address = from.getAddress();
            this.email = from.getEmail();
            this.phone = from.getPhone();
            this.otherContactDetails = from.getOtherContactDetails();
            for (VendorExternalLinkType type : VendorExternalLinkType.values()) {
                for (URI u : from.getExternalLinkUri(type)) {
                    this.externalLinks.put(type.getKey(), u);
                }
            }
            this.supportDetails = from.getSupportDetails();
            this.programs = from.getPrograms();
        }

        @Override
        protected Vendor buildUnsafe() {
            Vendor ret = new Vendor();
            ret._links = this.links.build();
            ret._embedded = new Vendor.Embedded();
            ret._embedded.logo = Option.none();
            ret.address = this.address;
            ret.description = this.description;
            ret.email = this.email;
            ret.name = this.name;
            ret.otherContactDetails = this.otherContactDetails;
            ret.phone = this.phone;
            ret.vendorLinks = ImmutableMap.copyOf(this.externalLinks);
            ret.verifiedStatus = Option.none();
            ret.isAtlassian = Option.none();
            ret.supportDetails = this.supportDetails;
            ret.programs = this.programs;
            return EntityValidator.validateInstance(ret);
        }

        public VendorBuilder logo(Option<ImageId> logo) {
            this.links.put("logo", (Option<URI>)logo.map(ResourceId.resourceIdToUriFunc));
            return this;
        }

        public VendorBuilder name(String name) {
            this.name = (String)Preconditions.checkNotNull((Object)name);
            return this;
        }

        public VendorBuilder description(Option<String> description) {
            this.description = (Option)Preconditions.checkNotNull(description);
            return this;
        }

        public VendorBuilder address(Option<Address> address) {
            this.address = (Option)Preconditions.checkNotNull(address);
            return this;
        }

        public VendorBuilder email(String email) {
            this.email = (String)Preconditions.checkNotNull((Object)email);
            return this;
        }

        public VendorBuilder phone(Option<String> phone) {
            this.phone = (Option)Preconditions.checkNotNull(phone);
            return this;
        }

        public VendorBuilder otherContactDetails(Option<String> otherContactDetails) {
            this.otherContactDetails = (Option)Preconditions.checkNotNull(otherContactDetails);
            return this;
        }

        public VendorBuilder supportDetails(SupportDetails supportDetails) {
            this.supportDetails = supportDetails;
            return this;
        }

        public VendorBuilder programs(VendorPrograms programs) {
            this.programs = programs;
            return this;
        }

        public VendorBuilder externalLinkUri(VendorExternalLinkType type, Option<URI> uri) {
            this.externalLinks.remove(type.getKey());
            for (URI u : uri) {
                this.externalLinks.put(type.getKey(), u);
            }
            return this;
        }
    }

    public static class VersionCompatibilityBuilder
    implements SafeBuilder<VersionCompatibility> {
        private ApplicationKey applicationKey;
        private Option<VersionCompatibility.CompatibilityHostingBounds> server = Option.none();
        private Option<VersionCompatibility.CompatibilityHostingBounds> dataCenter = Option.none();
        private Option<Boolean> cloud = Option.none();

        public VersionCompatibilityBuilder(VersionCompatibility from) {
            this.applicationKey = from.getApplication();
            this.server = from.getHosting().getServer();
            this.dataCenter = from.getHosting().getDataCenter();
            this.cloud = Option.some((Object)from.isCloudCompatible());
        }

        @Override
        public VersionCompatibility build() {
            return new VersionCompatibility(this.applicationKey, new VersionCompatibility.CompatibilityHosting(this.server, this.dataCenter, this.cloud));
        }

        public VersionCompatibilityBuilder setCloud(@Nullable Boolean cloud) {
            this.cloud = Option.option((Object)cloud);
            return this;
        }

        public VersionCompatibilityBuilder setServer(int min, int max) {
            this.server = Option.some((Object)ModelBuilders.makeHostingBounds(min, max));
            return this;
        }

        public VersionCompatibilityBuilder setDataCenter(int min, int max) {
            this.dataCenter = Option.some((Object)ModelBuilders.makeHostingBounds(min, max));
            return this;
        }
    }

    public static class ScreenshotBuilder
    extends UnsafeBuilderWithLinks<Screenshot, ScreenshotBuilder> {
        private ImageInfo image;
        private Option<String> caption = Option.none();

        @Override
        protected Screenshot buildUnsafe() {
            Screenshot ret = new Screenshot();
            ret._links = this.links.build();
            ret._embedded = new Screenshot.ScreenshotEmbedded();
            ret._embedded.image = this.image;
            ret._embedded = EntityValidator.validateInstance(ret._embedded);
            ret.caption = this.caption;
            return EntityValidator.validateInstance(ret);
        }

        public ScreenshotBuilder image(ImageId image) {
            this.links.put("image", image.getUri());
            this.image = TestModelBuilders.imageInfo().build();
            return this;
        }

        public ScreenshotBuilder caption(Option<String> caption) {
            this.caption = (Option)Preconditions.checkNotNull(caption);
            return this;
        }
    }

    public static class LinksBuilder
    implements SafeBuilder<Links> {
        private Map<String, ImmutableList<Link>> links = new HashMap<String, ImmutableList<Link>>();

        @Override
        public Links build() {
            return new Links(this.links);
        }

        public LinksBuilder put(String rel, URI uri) {
            return this.put(rel, (Option<String>)Option.none(String.class), uri);
        }

        public LinksBuilder put(String rel, Iterable<URI> uris) {
            return this.put(rel, (ImmutableList<Link>)ImmutableList.copyOf((Collection)StreamSupport.stream(uris.spliterator(), false).map(uri -> Link.fromUri(uri, (Option<String>)Option.none(String.class))).collect(Collectors.toList())));
        }

        public LinksBuilder put(String rel, Option<String> type, URI uri) {
            return this.put(rel, (ImmutableList<Link>)ImmutableList.of((Object)Link.fromUri(uri, type)));
        }

        public LinksBuilder put(String rel, Option<URI> maybeUri) {
            Iterator iterator = maybeUri.iterator();
            if (iterator.hasNext()) {
                URI uri = (URI)iterator.next();
                return this.put(rel, uri);
            }
            return this;
        }

        public LinksBuilder put(String rel, ImmutableList<Link> values) {
            this.links.put(rel, values);
            return this;
        }

        public LinksBuilder put(Links from) {
            this.links.putAll(from.getItems());
            return this;
        }

        public LinksBuilder putTemplate(String rel, String template) {
            return this.put(rel, (ImmutableList<Link>)ImmutableList.of((Object)Link.fromUriTemplate(UriTemplate.create(template), (Option<String>)Option.none(String.class))));
        }

        public LinksBuilder remove(String rel) {
            this.links.remove(rel);
            return this;
        }

        public LinksBuilder removeAll() {
            this.links.clear();
            return this;
        }
    }

    public static class HighlightBuilder
    extends UnsafeBuilderWithLinks<Highlight, HighlightBuilder> {
        private ImageInfo fullImage;
        private ImageInfo thumbnailImage;
        private String title;
        private HtmlString body;
        private Option<String> explanation = Option.none();

        @Override
        protected Highlight buildUnsafe() {
            Highlight ret = new Highlight();
            ret._embedded = new Highlight.Embedded();
            ret._embedded.screenshot = this.fullImage;
            ret._embedded.thumbnail = this.thumbnailImage;
            ret._embedded = EntityValidator.validateInstance(ret._embedded);
            ret._links = this.links.build();
            ret.body = this.body;
            ret.title = this.title;
            ret.explanation = this.explanation;
            return EntityValidator.validateInstance(ret);
        }

        public HighlightBuilder fullImage(ImageId fullImage) {
            this.links.put("screenshot", fullImage.getUri());
            this.fullImage = TestModelBuilders.imageInfo().build();
            return this;
        }

        public HighlightBuilder thumbnailImage(ImageId thumbnailImage) {
            this.links.put("thumbnail", thumbnailImage.getUri());
            this.thumbnailImage = TestModelBuilders.imageInfo().build();
            return this;
        }

        public HighlightBuilder title(String title) {
            this.title = (String)Preconditions.checkNotNull((Object)title);
            return this;
        }

        public HighlightBuilder body(HtmlString body) {
            this.body = (HtmlString)Preconditions.checkNotNull((Object)body);
            return this;
        }

        public HighlightBuilder explanation(Option<String> explanation) {
            this.explanation = (Option)Preconditions.checkNotNull(explanation);
            return this;
        }
    }

    public static class ApplicationVersionBuilder
    extends UnsafeBuilderWithLinks<ApplicationVersion, ApplicationVersionBuilder> {
        private Integer buildNumber;
        private String name;
        private LocalDate releaseDate;
        private ApplicationVersionStatus status;
        private boolean dataCenterCompatible;

        private ApplicationVersionBuilder() {
        }

        private ApplicationVersionBuilder(ApplicationVersion from) {
            this.links.put(from.getLinks());
            this.buildNumber = from.getBuildNumber();
            this.name = from.getName();
            this.releaseDate = from.getReleaseDate();
            this.status = from.getStatus();
            this.dataCenterCompatible = from.isDataCenterCompatible();
        }

        @Override
        protected ApplicationVersion buildUnsafe() {
            ApplicationVersion ret = new ApplicationVersion();
            ret._links = this.links.build();
            ret.buildNumber = this.buildNumber;
            ret.version = this.name;
            ret.releaseDate = this.releaseDate;
            ret.status = this.status;
            ret.dataCenterCompatible = this.dataCenterCompatible;
            return EntityValidator.validateInstance(ret);
        }

        public ApplicationVersionBuilder buildNumber(int buildNumber) {
            this.buildNumber = buildNumber;
            return this;
        }

        public ApplicationVersionBuilder name(String name) {
            this.name = (String)Preconditions.checkNotNull((Object)name);
            return this;
        }

        public ApplicationVersionBuilder releaseDate(LocalDate releaseDate) {
            this.releaseDate = (LocalDate)Preconditions.checkNotNull((Object)releaseDate);
            return this;
        }

        public ApplicationVersionBuilder status(ApplicationVersionStatus status) {
            this.status = (ApplicationVersionStatus)Preconditions.checkNotNull((Object)status);
            return this;
        }

        public ApplicationVersionBuilder dataCenterCompatible(boolean dataCenterCompatible) {
            this.dataCenterCompatible = dataCenterCompatible;
            return this;
        }
    }

    public static class AddressBuilder
    implements UnsafeBuilder<Address> {
        private String line1;
        private Option<String> line2 = Option.none();
        private Option<String> city = Option.none();
        private Option<String> state = Option.none();
        private Option<String> postCode = Option.none();
        private Option<String> country = Option.none();

        @Override
        public Address build() throws InvalidModelException {
            try {
                Address ret = new Address();
                ret.line1 = this.line1;
                ret.line2 = this.line2;
                ret.city = this.city;
                ret.state = this.state;
                ret.postCode = this.postCode;
                ret.country = this.country;
                return EntityValidator.validateInstance(ret);
            }
            catch (SchemaViolationException e) {
                throw ModelBuilders.modelException(e);
            }
        }

        public AddressBuilder line1(String line1) {
            this.line1 = (String)Preconditions.checkNotNull((Object)line1);
            return this;
        }

        public AddressBuilder line2(Option<String> line2) {
            this.line2 = (Option)Preconditions.checkNotNull(line2);
            return this;
        }

        public AddressBuilder city(Option<String> city) {
            this.city = (Option)Preconditions.checkNotNull(city);
            return this;
        }

        public AddressBuilder state(Option<String> state) {
            this.state = (Option)Preconditions.checkNotNull(state);
            return this;
        }

        public AddressBuilder postCode(Option<String> postCode) {
            this.postCode = (Option)Preconditions.checkNotNull(postCode);
            return this;
        }

        public AddressBuilder country(Option<String> country) {
            this.country = (Option)Preconditions.checkNotNull(country);
            return this;
        }
    }

    public static class AddonVersionBuilder
    extends UnsafeBuilderWithLinks<AddonVersion, AddonVersionBuilder> {
        private Long buildNumber;
        private Option<Long> dataCenterBuildNumber = Option.none();
        private Option<String> name = Option.none();
        private AddonVersionStatus status;
        private PaymentModel paymentModel;
        private Option<URI> agreement = Option.none();
        private Option<ImmutableList<Highlight>> highlights = Option.none();
        private Option<ImmutableList<Screenshot>> screenshots = Option.none();
        private Option<String> youtubeId = Option.none();
        private Option<ImmutableList<VersionCompatibility>> compatibilities = Option.none();
        private LocalDate releaseDate;
        private Option<String> releasedBy = Option.none();
        private boolean beta = false;
        private boolean staticAddon = false;
        private boolean supported = false;
        private boolean deployable = false;
        private Option<String> releaseSummary = Option.none();
        private Option<HtmlString> moreDetails = Option.none();
        private Option<HtmlString> releaseNotes = Option.none();
        private Map<String, URI> externalLinks = new HashMap<String, URI>();

        private AddonVersionBuilder() {
        }

        private AddonVersionBuilder(AddonVersion from) {
            this.links.put(from.getLinks());
            this.buildNumber = from.getBuildNumber();
            this.dataCenterBuildNumber = from.getDataCenterBuildNumber();
            this.name = from.getName();
            this.status = from.getStatus();
            this.paymentModel = from.getPaymentModel();
            this.releaseDate = from.getReleaseDate();
            this.releasedBy = from.getReleasedBy();
            this.highlights = ModelBuilders.copyOptionalList(from.getHighlightsIfSpecified());
            this.screenshots = ModelBuilders.copyOptionalList(from.getScreenshotsIfSpecified());
            this.youtubeId = from.getYoutubeId();
            this.compatibilities = ModelBuilders.copyOptionalList(from.getCompatibilitiesIfSpecified());
            this.beta = from.isBeta();
            this.supported = from.isSupported();
            this.staticAddon = from.isStatic();
            this.deployable = from.isDeployable();
            this.releaseSummary = from.getReleaseSummary();
            this.moreDetails = from.getMoreDetails();
            this.releaseNotes = from.getReleaseNotes();
            for (AddonVersionExternalLinkType type : AddonVersionExternalLinkType.values()) {
                for (URI u : from.getExternalLinkUri(type)) {
                    this.externalLinks.put(type.getKey(), u);
                }
            }
        }

        @Override
        protected AddonVersion buildUnsafe() {
            AddonVersion ret = new AddonVersion();
            ret._links = this.links.put("agreement", this.agreement).build();
            ret._embedded = new AddonVersion.Embedded();
            ret._embedded.artifact = Option.none();
            ret._embedded.functionalCategories = ImmutableList.of();
            ret._embedded.highlights = this.highlights;
            ret._embedded.license = Option.none();
            ret._embedded.screenshots = this.screenshots;
            ret.buildNumber = this.buildNumber;
            ret.dataCenterBuildNumber = this.dataCenterBuildNumber;
            ret.name = this.name;
            ret.status = this.status;
            ret.paymentModel = this.paymentModel;
            ret.youtubeId = this.youtubeId;
            ret.compatibilities = this.compatibilities;
            ret.staticAddon = this.staticAddon;
            ret.deployable = this.deployable;
            ret.deployment = new AddonVersionBase.DeploymentProperties();
            ret.deployment.autoUpdateAllowed = false;
            ret.deployment.cloud = false;
            ret.deployment.connect = false;
            ret.deployment.dataCenter = false;
            ret.deployment.dataCenterStatus = Option.none();
            ret.deployment.permissions = Option.none();
            ret.deployment.server = false;
            ret.legacy = Option.none();
            AddonVersionBase.ReleaseProperties release = new AddonVersionBase.ReleaseProperties();
            release.beta = this.beta;
            release.date = this.releaseDate;
            release.releasedBy = this.releasedBy;
            release.supported = this.supported;
            ret.release = EntityValidator.validateInstance(release);
            ret.text = new AddonVersion.TextProperties();
            ret.text.releaseSummary = this.releaseSummary;
            ret.text.moreDetails = this.moreDetails;
            ret.text.releaseNotes = this.releaseNotes;
            ret.text = EntityValidator.validateInstance(ret.text);
            ret.vendorLinks = ImmutableMap.copyOf(this.externalLinks);
            return EntityValidator.validateInstance(ret);
        }

        public AddonVersionBuilder artifact(Option<ArtifactId> artifact) {
            this.links.put("artifact", (Option<URI>)artifact.map(ResourceId.resourceIdToUriFunc));
            return this;
        }

        public AddonVersionBuilder buildNumber(long buildNumber) {
            this.buildNumber = buildNumber;
            return this;
        }

        public AddonVersionBuilder dataCenterBuildNumber(@Nullable Long dataCenterBuildNumber) {
            this.dataCenterBuildNumber = Option.option((Object)dataCenterBuildNumber);
            return this;
        }

        public AddonVersionBuilder name(String name) {
            this.name = Option.some((Object)name);
            return this;
        }

        public AddonVersionBuilder agreement(URI agreement) {
            this.agreement = Option.some((Object)agreement);
            return this;
        }

        public AddonVersionBuilder status(AddonVersionStatus status) {
            this.status = (AddonVersionStatus)Preconditions.checkNotNull((Object)status);
            return this;
        }

        public AddonVersionBuilder licenseType(Option<LicenseType> licenseType) {
            this.links.put("license", (Option<URI>)licenseType.flatMap(EntityFunctions.selfUri()));
            return this;
        }

        public AddonVersionBuilder licenseTypeId(Option<LicenseTypeId> licenseTypeId) {
            this.links.put("license", (Option<URI>)licenseTypeId.map(ResourceId.resourceIdToUriFunc));
            return this;
        }

        public AddonVersionBuilder paymentModel(PaymentModel paymentModel) {
            this.paymentModel = (PaymentModel)Preconditions.checkNotNull((Object)paymentModel);
            return this;
        }

        public AddonVersionBuilder releaseDate(LocalDate releaseDate) {
            this.releaseDate = (LocalDate)Preconditions.checkNotNull((Object)releaseDate);
            return this;
        }

        public AddonVersionBuilder releasedBy(Option<String> releasedBy) {
            this.releasedBy = (Option)Preconditions.checkNotNull(releasedBy);
            return this;
        }

        public AddonVersionBuilder highlights(Iterable<Highlight> highlights) {
            this.highlights = Option.some((Object)ImmutableList.copyOf(highlights));
            return this;
        }

        public AddonVersionBuilder screenshots(Iterable<Screenshot> screenshots) {
            this.screenshots = Option.some((Object)ImmutableList.copyOf(screenshots));
            return this;
        }

        public AddonVersionBuilder youtubeId(Option<String> youtubeId) {
            this.youtubeId = youtubeId;
            return this;
        }

        public AddonVersionBuilder compatibilities(Iterable<VersionCompatibility> compatibilities) {
            this.compatibilities = Option.some((Object)ImmutableList.copyOf(compatibilities));
            return this;
        }

        public AddonVersionBuilder beta(boolean beta) {
            this.beta = beta;
            return this;
        }

        public AddonVersionBuilder supported(boolean supported) {
            this.supported = supported;
            return this;
        }

        public AddonVersionBuilder staticAddon(boolean staticAddon) {
            this.staticAddon = staticAddon;
            return this;
        }

        public AddonVersionBuilder deployable(boolean deployable) {
            this.deployable = deployable;
            return this;
        }

        public AddonVersionBuilder releaseSummary(Option<String> releaseSummary) {
            this.releaseSummary = releaseSummary;
            return this;
        }

        public AddonVersionBuilder moreDetails(Option<HtmlString> moreDetails) {
            this.moreDetails = moreDetails;
            return this;
        }

        public AddonVersionBuilder releaseNotes(Option<HtmlString> releaseNotes) {
            this.releaseNotes = releaseNotes;
            return this;
        }

        public AddonVersionBuilder externalLinkUri(AddonVersionExternalLinkType type, Option<URI> uri) {
            if (!type.canSetForNewAddonVersions()) {
                throw new IllegalArgumentException("Cannot set " + (Object)((Object)type) + " link for new add-ons");
            }
            this.externalLinks.remove(type.getKey());
            for (URI u : uri) {
                this.externalLinks.put(type.getKey(), u);
            }
            return this;
        }
    }

    public static class AddonBuilder
    extends UnsafeBuilderWithLinks<Addon, AddonBuilder> {
        private String name;
        private String key;
        private AddonStatus status;
        private Option<String> summary = Option.none();
        private Option<String> tagLine = Option.none();
        private Option<AddonVersion> version = Option.none();
        private Option<Boolean> enableAtlassianAnswers = Option.none();
        private Map<String, URI> externalLinks = new HashMap<String, URI>();
        private Option<Boolean> storesPersonalData;

        private AddonBuilder() {
            this.links.put("alternate", TestModelBuilders.DEFAULT_URI);
        }

        private AddonBuilder(Addon from) {
            this.links.put(from.getLinks());
            this.name = from.getName();
            this.key = from.getKey();
            this.status = from.getStatus();
            this.summary = from.getSummary();
            this.tagLine = from.getTagLine();
            this.version = from.getVersion();
            this.enableAtlassianAnswers = from.isEnableAtlassianAnswers();
            for (AddonExternalLinkType type : AddonExternalLinkType.values()) {
                for (URI u : from.getExternalLinkUri(type)) {
                    this.externalLinks.put(type.getKey(), u);
                }
            }
            this.storesPersonalData = from.storesPersonalData();
        }

        @Override
        protected Addon buildUnsafe() {
            Addon addon = new Addon();
            addon._links = this.links.build();
            addon._embedded = new Addon.Embedded();
            addon._embedded.banner = Option.none();
            addon._embedded.logo = Option.none();
            addon._embedded.categories = ImmutableList.of();
            addon._embedded.distribution = TestModelBuilders.addonDistributionSummary().build();
            addon._embedded.reviews = TestModelBuilders.addonReviewsSummary(0.0f, 0);
            addon._embedded.vendor = Option.none();
            addon._embedded.version = this.version;
            addon._embedded = EntityValidator.validateInstance(addon._embedded);
            addon.name = this.name;
            addon.key = this.key;
            addon.status = this.status;
            addon.summary = this.summary;
            addon.tagLine = this.tagLine;
            addon.legacy = Option.none();
            addon.enableAtlassianAnswers = this.enableAtlassianAnswers;
            addon.vendorLinks = ImmutableMap.copyOf(this.externalLinks);
            addon.storesPersonalData = this.storesPersonalData;
            return EntityValidator.validateInstance(addon);
        }

        public AddonBuilder name(String name) {
            this.name = (String)Preconditions.checkNotNull((Object)name);
            return this;
        }

        public AddonBuilder key(String key) {
            this.key = (String)Preconditions.checkNotNull((Object)key);
            return this;
        }

        public AddonBuilder status(AddonStatus status) {
            this.status = status;
            return this;
        }

        public AddonBuilder summary(Option<String> summary) {
            this.summary = (Option)Preconditions.checkNotNull(summary);
            return this;
        }

        public AddonBuilder tagLine(Option<String> tagLine) {
            this.tagLine = (Option)Preconditions.checkNotNull(tagLine);
            return this;
        }

        public AddonBuilder banner(Option<ImageId> image) {
            this.links.put("banner", (Option<URI>)image.map(ResourceId.resourceIdToUriFunc));
            return this;
        }

        public AddonBuilder logo(Option<ImageId> image) {
            this.links.put("logo", (Option<URI>)image.map(ResourceId.resourceIdToUriFunc));
            return this;
        }

        public AddonBuilder categories(Iterable<AddonCategoryId> categories) {
            this.links.put("categories", StreamSupport.stream(categories.spliterator(), false).map(ResourceId.resourceIdToUriFunc).collect(Collectors.toList()));
            return this;
        }

        public AddonBuilder vendor(VendorSummary vendor) {
            return this.vendor(vendor.getId());
        }

        public AddonBuilder vendor(VendorId id) {
            this.links.put("vendor", id.getUri());
            return this;
        }

        public AddonBuilder enableAtlassianAnswers(boolean enableAtlassianAnswers) {
            this.enableAtlassianAnswers = Option.some((Object)enableAtlassianAnswers);
            return this;
        }

        public AddonBuilder version(Option<AddonVersion> version) {
            this.version = (Option)Preconditions.checkNotNull(version);
            return this;
        }

        public AddonBuilder externalLinkUri(AddonExternalLinkType type, Option<URI> uri) {
            if (!type.canSetForNewAddons()) {
                throw new IllegalArgumentException("Cannot set " + (Object)((Object)type) + " link for new add-ons");
            }
            this.externalLinks.remove(type.getKey());
            for (URI u : uri) {
                this.externalLinks.put(type.getKey(), u);
            }
            return this;
        }

        public AddonBuilder storesPersonalData(Option<Boolean> storesPersonalData) {
            this.storesPersonalData = (Option)Preconditions.checkNotNull(storesPersonalData);
            return this;
        }
    }

    public static abstract class UnsafeBuilderWithLinks<A, T extends UnsafeBuilderWithLinks<A, T>>
    extends BuilderWithLinks<T>
    implements UnsafeBuilder<A> {
        protected abstract A buildUnsafe();

        @Override
        public A build() throws InvalidModelException {
            try {
                return this.buildUnsafe();
            }
            catch (SchemaViolationException e) {
                throw ModelBuilders.modelException(e);
            }
        }
    }

    public static abstract class BuilderWithLinks<T extends BuilderWithLinks<T>> {
        protected final LinksBuilder links = new LinksBuilder();

        protected BuilderWithLinks() {
            this.links.put("self", TestModelBuilders.DEFAULT_URI);
        }

        public T links(Links links) {
            this.links.removeAll();
            this.links.put(links);
            return (T)this;
        }

        public T addLinks(Links links) {
            this.links.put(links);
            return (T)this;
        }

        public T addLink(String rel, URI uri) {
            this.links.put(rel, uri);
            return (T)this;
        }

        public T addLink(String rel, String type, URI uri) {
            this.links.put(rel, (Option<String>)Option.some((Object)type), uri);
            return (T)this;
        }

        public T addLinkTemplate(String rel, String template) {
            this.links.putTemplate(rel, template);
            return (T)this;
        }
    }

    public static interface UnsafeBuilder<T> {
        public T build() throws InvalidModelException;
    }

    public static interface SafeBuilder<T> {
        public T build();
    }

    public static class InvalidModelException
    extends MpacException {
        private ImmutableList<SchemaViolation> schemaViolations;

        InvalidModelException(Iterable<SchemaViolation> schemaViolations) {
            this.schemaViolations = ImmutableList.copyOf(schemaViolations);
        }

        public Iterable<SchemaViolation> getSchemaViolations() {
            return this.schemaViolations;
        }

        @Override
        public String getMessage() {
            return Joiner.on((String)", ").join(this.schemaViolations);
        }
    }
}

