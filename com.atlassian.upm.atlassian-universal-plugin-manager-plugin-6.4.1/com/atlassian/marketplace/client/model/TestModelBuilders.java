/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  io.atlassian.fugue.Option
 *  io.atlassian.fugue.Options
 *  io.atlassian.fugue.Pair
 *  org.joda.time.DateTime
 *  org.joda.time.LocalDate
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.api.AddonExternalLinkType;
import com.atlassian.marketplace.client.api.AddonVersionExternalLinkType;
import com.atlassian.marketplace.client.api.ApplicationKey;
import com.atlassian.marketplace.client.api.VendorExternalLinkType;
import com.atlassian.marketplace.client.api.VendorId;
import com.atlassian.marketplace.client.impl.EntityValidator;
import com.atlassian.marketplace.client.model.Addon;
import com.atlassian.marketplace.client.model.AddonCategorySummary;
import com.atlassian.marketplace.client.model.AddonDistributionSummary;
import com.atlassian.marketplace.client.model.AddonPricing;
import com.atlassian.marketplace.client.model.AddonPricingItem;
import com.atlassian.marketplace.client.model.AddonReference;
import com.atlassian.marketplace.client.model.AddonReviewsSummary;
import com.atlassian.marketplace.client.model.AddonStatus;
import com.atlassian.marketplace.client.model.AddonSummary;
import com.atlassian.marketplace.client.model.AddonVersion;
import com.atlassian.marketplace.client.model.AddonVersionBase;
import com.atlassian.marketplace.client.model.AddonVersionDataCenterStatus;
import com.atlassian.marketplace.client.model.AddonVersionStatus;
import com.atlassian.marketplace.client.model.AddonVersionSummary;
import com.atlassian.marketplace.client.model.Address;
import com.atlassian.marketplace.client.model.Application;
import com.atlassian.marketplace.client.model.ApplicationStatus;
import com.atlassian.marketplace.client.model.ApplicationVersion;
import com.atlassian.marketplace.client.model.ApplicationVersionStatus;
import com.atlassian.marketplace.client.model.ArtifactInfo;
import com.atlassian.marketplace.client.model.ConnectScope;
import com.atlassian.marketplace.client.model.ErrorDetail;
import com.atlassian.marketplace.client.model.Highlight;
import com.atlassian.marketplace.client.model.HtmlString;
import com.atlassian.marketplace.client.model.ImageInfo;
import com.atlassian.marketplace.client.model.LicenseEditionType;
import com.atlassian.marketplace.client.model.LicenseType;
import com.atlassian.marketplace.client.model.Links;
import com.atlassian.marketplace.client.model.ModelBuilders;
import com.atlassian.marketplace.client.model.PaymentModel;
import com.atlassian.marketplace.client.model.Product;
import com.atlassian.marketplace.client.model.ProductVersion;
import com.atlassian.marketplace.client.model.Screenshot;
import com.atlassian.marketplace.client.model.SupportDetails;
import com.atlassian.marketplace.client.model.Vendor;
import com.atlassian.marketplace.client.model.VendorPrograms;
import com.atlassian.marketplace.client.model.VendorSummary;
import com.atlassian.marketplace.client.model.VersionCompatibility;
import com.atlassian.marketplace.client.util.EntityFunctions;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.Options;
import io.atlassian.fugue.Pair;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public abstract class TestModelBuilders {
    public static URI DEFAULT_URI = URI.create("http://default/uri");
    static String DEFAULT_STRING = "DefaultValue";
    static Date DEFAULT_DATE = new Date(0L);
    static int DEFAULT_INT = Integer.MAX_VALUE;
    static LocalDate DEFAULT_LOCAL_DATE = new LocalDate(2000, 1, 1);
    static long DEFAULT_LONG = Long.MAX_VALUE;
    static URI DEFAULT_ABSOLUTE_URI = URI.create("http://default/uri");
    static float DEFAULT_PRICE = 100.0f;
    static int DEFAULT_STARS = 3;

    private TestModelBuilders() {
    }

    public static AddonBuilder addon() {
        return new AddonBuilder();
    }

    public static AddonBuilder addon(Addon from) {
        return new AddonBuilder(from);
    }

    public static AddonCategorySummaryBuilder addonCategorySummary() {
        return new AddonCategorySummaryBuilder();
    }

    public static AddonDistributionSummaryBuilder addonDistributionSummary() {
        return new AddonDistributionSummaryBuilder();
    }

    public static AddonPricingBuilder addonPricing() {
        return new AddonPricingBuilder();
    }

    public static AddonPricingItemBuilder addonPricingItem() {
        return new AddonPricingItemBuilder();
    }

    public static AddonReferenceBuilder addonReference() {
        return new AddonReferenceBuilder();
    }

    public static AddonReviewsSummary addonReviewsSummary(float averageStars, int count) {
        AddonReviewsSummary ret = new AddonReviewsSummary();
        ret.averageStars = Float.valueOf(averageStars);
        ret.count = count;
        return ret;
    }

    public static AddonSummaryBuilder addonSummary() {
        return new AddonSummaryBuilder();
    }

    public static AddonVersionBuilder addonVersion() {
        return new AddonVersionBuilder();
    }

    public static AddonVersionBuilder addonVersion(AddonVersion from) {
        return new AddonVersionBuilder(from);
    }

    public static AddonVersionSummaryBuilder addonVersionSummary() {
        return new AddonVersionSummaryBuilder();
    }

    public static ApplicationBuilder application() {
        return new ApplicationBuilder();
    }

    public static ApplicationBuilder application(Application from) {
        return new ApplicationBuilder(from);
    }

    public static ApplicationVersionBuilder applicationVersion() {
        return new ApplicationVersionBuilder();
    }

    public static ApplicationVersionBuilder applicationVersion(ApplicationVersion from) {
        return new ApplicationVersionBuilder(from);
    }

    public static ArtifactInfo artifactInfo(URI binaryUri, boolean remote) {
        ArtifactInfo ai = new ArtifactInfo();
        ModelBuilders.LinksBuilder links = ModelBuilders.links();
        links.put("self", DEFAULT_URI);
        links.put("binary", binaryUri);
        if (remote) {
            links.put("remote", binaryUri);
        }
        ai._links = links.build();
        return EntityValidator.validateInstance(ai);
    }

    public static ConnectScope connectScope(String key, String name, String description) {
        ConnectScope ret = new ConnectScope();
        ret._links = ModelBuilders.links().put("alternate", DEFAULT_ABSOLUTE_URI).build();
        ret.key = (String)Preconditions.checkNotNull((Object)key);
        ret.name = (String)Preconditions.checkNotNull((Object)name);
        ret.description = (String)Preconditions.checkNotNull((Object)description);
        return ret;
    }

    public static ErrorDetail errorDetail(String message) {
        return TestModelBuilders.errorDetail(message, (Option<String>)Option.none(String.class), (Option<String>)Option.none(String.class));
    }

    public static ErrorDetail errorDetail(String message, Option<String> path, Option<String> code) {
        ErrorDetail ret = new ErrorDetail();
        ret.message = (String)Preconditions.checkNotNull((Object)message);
        ret.path = (Option)Preconditions.checkNotNull(path);
        ret.code = (Option)Preconditions.checkNotNull(code);
        return ret;
    }

    public static ImageInfoBuilder imageInfo() {
        return new ImageInfoBuilder();
    }

    public static ProductBuilder product() {
        return new ProductBuilder();
    }

    public static ProductVersionBuilder productVersion() {
        return new ProductVersionBuilder();
    }

    public static VendorBuilder vendor() {
        return new VendorBuilder();
    }

    public static VendorBuilder vendor(Vendor from) {
        return new VendorBuilder(from);
    }

    public static VendorSummaryBuilder vendorSummary() {
        return new VendorSummaryBuilder();
    }

    public static <T> T safeBuild(ModelBuilders.UnsafeBuilder<T> builder) {
        try {
            return builder.build();
        }
        catch (ModelBuilders.InvalidModelException e) {
            throw new RuntimeException(e);
        }
    }

    public static class VendorSummaryBuilder
    extends ModelBuilders.BuilderWithLinks<VendorSummaryBuilder> {
        private Option<ImageInfo> logo = Option.none();
        private String name = DEFAULT_STRING;
        private Option<String> verifiedStatus = Option.none();
        private Option<Boolean> isAtlassian = Option.none();
        private VendorPrograms programs;

        private VendorSummaryBuilder() {
            this.links.put("alternate", (Option<String>)Option.some((Object)"text/html"), DEFAULT_URI);
            this.programs = new VendorPrograms();
        }

        public VendorSummary build() {
            VendorSummary ret = new VendorSummary();
            ret._links = this.links.build();
            ret._embedded = new VendorSummary.Embedded();
            ret._embedded.logo = this.logo;
            ret.name = this.name;
            ret.verifiedStatus = this.verifiedStatus;
            ret.isAtlassian = this.isAtlassian;
            ret.programs = this.programs;
            return EntityValidator.validateInstance(ret);
        }

        public VendorSummaryBuilder logo(Option<ImageInfo> logo) {
            this.logo = (Option)Preconditions.checkNotNull(logo);
            return this;
        }

        public VendorSummaryBuilder name(String name) {
            this.name = (String)Preconditions.checkNotNull((Object)name);
            return this;
        }

        public VendorSummaryBuilder verifiedStatus(Option<String> verifiedStatus) {
            this.verifiedStatus = (Option)Preconditions.checkNotNull(verifiedStatus);
            return this;
        }

        public VendorSummaryBuilder isAtlassian(Option<Boolean> isAtlassian) {
            this.isAtlassian = (Option)Preconditions.checkNotNull(isAtlassian);
            return this;
        }
    }

    public static class VendorBuilder
    extends ModelBuilders.BuilderWithLinks<VendorBuilder>
    implements ModelBuilders.SafeBuilder<Vendor> {
        private ModelBuilders.VendorBuilder builder;
        private Option<ImageInfo> logo = Option.none();
        private Option<String> verifiedStatus = Option.none();
        private Option<Boolean> isAtlassian = Option.none();

        private VendorBuilder() {
            this.builder = ModelBuilders.vendor();
            this.builder.name(DEFAULT_STRING);
            this.builder.email(DEFAULT_STRING);
            this.builder.supportDetails(new SupportDetails());
        }

        private VendorBuilder(Vendor from) {
            this.builder = ModelBuilders.vendor(from);
            this.logo = from.getLogo();
        }

        @Override
        public Vendor build() {
            this.builder.addLinks(this.links.build());
            Vendor v = TestModelBuilders.safeBuild(this.builder);
            v._embedded.logo = this.logo;
            v.verifiedStatus = this.verifiedStatus;
            v.isAtlassian = this.isAtlassian;
            return v;
        }

        public VendorBuilder logo(Option<ImageInfo> logo) {
            this.links.put("logo", (Option<URI>)logo.flatMap(EntityFunctions.selfUri()));
            this.logo = logo;
            return this;
        }

        public VendorBuilder name(String name) {
            this.builder.name(name);
            return this;
        }

        public VendorBuilder description(Option<String> description) {
            this.builder.description(description);
            return this;
        }

        public VendorBuilder address(Option<Address> address) {
            this.builder.address(address);
            return this;
        }

        public VendorBuilder email(String email) {
            this.builder.email(email);
            return this;
        }

        public VendorBuilder phone(Option<String> phone) {
            this.builder.phone(phone);
            return this;
        }

        public VendorBuilder otherContactDetails(Option<String> otherContactDetails) {
            this.builder.otherContactDetails(otherContactDetails);
            return this;
        }

        public VendorBuilder externalLinkUri(VendorExternalLinkType type, Option<URI> uri) {
            this.builder.externalLinkUri(type, uri);
            return this;
        }

        public VendorBuilder verifiedStatus(Option<String> verifiedStatus) {
            this.verifiedStatus = (Option)Preconditions.checkNotNull(verifiedStatus);
            return this;
        }

        public VendorBuilder isAtlassian(Option<Boolean> isAtlassian) {
            this.isAtlassian = (Option)Preconditions.checkNotNull(isAtlassian);
            return this;
        }

        public VendorBuilder supportDetails(SupportDetails supportDetails) {
            this.builder.supportDetails(supportDetails);
            return this;
        }
    }

    public static class ProductVersionBuilder
    extends ModelBuilders.BuilderWithLinks<ProductVersionBuilder> {
        private String name = DEFAULT_STRING;
        private int buildNumber = DEFAULT_INT;
        private Option<URI> artifactUri = Option.none();
        private PaymentModel paymentModel = PaymentModel.FREE;
        private LocalDate releaseDate = DEFAULT_LOCAL_DATE;
        private ImmutableList<VersionCompatibility> compatibilities = ImmutableList.of();

        public ProductVersion build() {
            ProductVersion ret = new ProductVersion();
            ret._links = this.links.build();
            ret._embedded = new ProductVersion.Embedded();
            ret._embedded.artifact = Option.none();
            ret.buildNumber = this.buildNumber;
            ret.name = this.name;
            ret.paymentModel = this.paymentModel;
            ret.releaseDate = this.releaseDate;
            ret.compatibilities = this.compatibilities;
            for (URI u : this.artifactUri) {
                ret._embedded.artifact = Option.some((Object)TestModelBuilders.artifactInfo(u, false));
            }
            return EntityValidator.validateInstance(ret);
        }

        public ProductVersionBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ProductVersionBuilder buildNumber(int buildNumber) {
            this.buildNumber = buildNumber;
            return this;
        }

        public ProductVersionBuilder artifactUri(Option<URI> artifactUri) {
            this.artifactUri = artifactUri;
            return this;
        }

        public ProductVersionBuilder learnMoreUri(Option<URI> learnMoreUri) {
            for (URI u : learnMoreUri) {
                this.links.put("view", u);
            }
            if (!learnMoreUri.isDefined()) {
                this.links.remove("view");
            }
            return this;
        }

        public ProductVersionBuilder paymentModel(PaymentModel paymentModel) {
            this.paymentModel = (PaymentModel)Preconditions.checkNotNull((Object)paymentModel);
            return this;
        }

        public ProductVersionBuilder releaseDate(LocalDate releaseDate) {
            this.releaseDate = (LocalDate)Preconditions.checkNotNull((Object)releaseDate);
            return this;
        }

        public ProductVersionBuilder compatibilities(Iterable<VersionCompatibility> compatibilities) {
            this.compatibilities = ImmutableList.copyOf(compatibilities);
            return this;
        }
    }

    public static class ProductBuilder
    extends ModelBuilders.BuilderWithLinks<ProductBuilder> {
        private Option<ImageInfo> logo = Option.none();
        private Option<ImageInfo> titleLogo = Option.none();
        private Option<ProductVersion> version = Option.none();
        private String key = DEFAULT_STRING;
        private String name = DEFAULT_STRING;
        private String summary = DEFAULT_STRING;

        public Product build() {
            Product ret = new Product();
            ret._links = this.links.build();
            ret._embedded = new Product.Embedded();
            ret._embedded.logo = this.logo;
            ret._embedded.titleLogo = this.titleLogo;
            ret._embedded.version = this.version;
            ret.key = this.key;
            ret.name = this.name;
            ret.summary = this.summary;
            return EntityValidator.validateInstance(ret);
        }

        public ProductBuilder downloadsPageUri(Option<URI> uri) {
            this.links.put("downloads", uri);
            return this;
        }

        public ProductBuilder key(String key) {
            this.key = key;
            return this;
        }

        public ProductBuilder logo(Option<ImageInfo> logo) {
            this.logo = logo;
            return this;
        }

        public ProductBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ProductBuilder summary(String summary) {
            this.summary = summary;
            return this;
        }

        public ProductBuilder titleLogo(Option<ImageInfo> titleLogo) {
            this.titleLogo = titleLogo;
            return this;
        }

        public ProductBuilder version(Option<ProductVersion> version) {
            this.version = version;
            return this;
        }
    }

    public static class ImageInfoBuilder
    implements ModelBuilders.SafeBuilder<ImageInfo> {
        private ModelBuilders.LinksBuilder links = ModelBuilders.links();

        private ImageInfoBuilder() {
            this.links.put("image", DEFAULT_URI);
            this.links.put("self", DEFAULT_URI);
        }

        @Override
        public ImageInfo build() {
            ImageInfo i = new ImageInfo();
            i._links = this.links.build();
            return EntityValidator.validateInstance(i);
        }

        public ImageInfoBuilder setImageUri(URI uri) {
            this.links.put("image", uri);
            return this;
        }

        public ImageInfoBuilder setResourceUri(URI uri) {
            this.links.put("self", uri);
            return this;
        }

        public ImageInfoBuilder setImageUri(ImageInfo.Size size, ImageInfo.Resolution resolution, URI uri) {
            this.links.put(ImageInfo.getImageLinkRel(size, resolution), uri);
            return this;
        }
    }

    public static class ApplicationVersionBuilder
    extends ModelBuilders.BuilderWithLinks<ApplicationVersionBuilder>
    implements ModelBuilders.SafeBuilder<ApplicationVersion> {
        private ModelBuilders.ApplicationVersionBuilder builder;

        private ApplicationVersionBuilder() {
            this.builder = ModelBuilders.applicationVersion();
            this.builder.name(DEFAULT_STRING);
            this.builder.buildNumber(DEFAULT_INT);
            this.builder.releaseDate(DEFAULT_LOCAL_DATE);
            this.builder.status(ApplicationVersionStatus.PUBLISHED);
            this.builder.dataCenterCompatible(false);
        }

        private ApplicationVersionBuilder(ApplicationVersion v) {
            this.builder = ModelBuilders.applicationVersion(v);
        }

        @Override
        public ApplicationVersion build() {
            this.builder.addLinks(this.links.build());
            return TestModelBuilders.safeBuild(this.builder);
        }

        public ApplicationVersionBuilder buildNumber(int buildNumber) {
            this.builder.buildNumber(buildNumber);
            return this;
        }

        public ApplicationVersionBuilder name(String name) {
            this.builder.name(name);
            return this;
        }

        public ApplicationVersionBuilder releaseDate(LocalDate releaseDate) {
            this.builder.releaseDate(releaseDate);
            return this;
        }

        public ApplicationVersionBuilder status(ApplicationVersionStatus status) {
            this.builder.status(status);
            return this;
        }

        public ApplicationVersionBuilder dataCenterCompatible(boolean dataCenterCompatible) {
            this.builder.dataCenterCompatible(dataCenterCompatible);
            return this;
        }
    }

    public static class ApplicationBuilder
    extends ModelBuilders.BuilderWithLinks<ApplicationBuilder>
    implements ModelBuilders.SafeBuilder<Application> {
        private ApplicationKey key = ApplicationKey.valueOf(DEFAULT_STRING);
        private String name = DEFAULT_STRING;
        private ApplicationStatus status = ApplicationStatus.PUBLISHED;
        private String introduction = DEFAULT_STRING;
        private Option<Integer> cloudFreeUsers = Option.none();
        private Application.CompatibilityUpdateMode compatibilityMode = Application.CompatibilityUpdateMode.MINOR_VERSIONS;
        private String description = DEFAULT_STRING;
        private Option<URI> downloadPage = Option.none();
        private URI learnMore = DEFAULT_ABSOLUTE_URI;

        private ApplicationBuilder() {
        }

        private ApplicationBuilder(Application from) {
            this.links.put(from.getLinks());
            this.key = from.getKey();
            this.name = from.getName();
            this.status = from.getStatus();
            this.introduction = from.getIntroduction();
            this.cloudFreeUsers = from.getCloudFreeUsers();
            this.compatibilityMode = from.getCompatibilityUpdateMode();
            this.description = from.getDescription();
            this.downloadPage = from.getDownloadPageUri();
            this.learnMore = from.getLearnMoreUri();
        }

        @Override
        public Application build() {
            Application a = new Application();
            a._links = this.links.build();
            a.key = this.key;
            a.name = this.name;
            a.status = this.status;
            a.introduction = this.introduction;
            a.cloudFreeUsers = this.cloudFreeUsers;
            a.atlassianConnectSupport = new Application.ConnectSupport();
            a.atlassianConnectSupport.cloud = false;
            a.atlassianConnectSupport.server = false;
            a.compatibilityMode = this.compatibilityMode;
            a.details = new Application.Details();
            a.details.description = this.description;
            a.details.downloadPage = this.downloadPage;
            a.details.learnMore = this.learnMore;
            a.hostingSupport = new Application.HostingSupport();
            a.hostingSupport.cloud = new Application.HostingModelSupport();
            a.hostingSupport.cloud.enabled = true;
            a.hostingSupport.server = new Application.HostingModelSupport();
            a.hostingSupport.server.enabled = true;
            return EntityValidator.validateInstance(a);
        }

        public ApplicationBuilder key(ApplicationKey key) {
            this.key = (ApplicationKey)Preconditions.checkNotNull((Object)key);
            return this;
        }

        public ApplicationBuilder name(String name) {
            this.name = (String)Preconditions.checkNotNull((Object)name);
            return this;
        }

        public ApplicationBuilder introduction(String introduction) {
            this.introduction = (String)Preconditions.checkNotNull((Object)introduction);
            return this;
        }

        public ApplicationBuilder cloudFreeUsers(Option<Integer> cloudFreeUsers) {
            this.cloudFreeUsers = (Option)Preconditions.checkNotNull(cloudFreeUsers);
            return this;
        }

        public ApplicationBuilder status(ApplicationStatus status) {
            this.status = (ApplicationStatus)Preconditions.checkNotNull((Object)status);
            return this;
        }
    }

    public static class AddonVersionSummaryBuilder
    extends ModelBuilders.BuilderWithLinks<AddonVersionSummaryBuilder>
    implements ModelBuilders.SafeBuilder<AddonVersionSummary> {
        private Option<URI> artifactUri = Option.none();
        private ImmutableList<AddonCategorySummary> functionalCategories = ImmutableList.of();
        private String name = DEFAULT_STRING;
        private AddonVersionStatus status = AddonVersionStatus.PUBLIC;
        private PaymentModel paymentModel = PaymentModel.FREE;
        private LocalDate releaseDate = DEFAULT_LOCAL_DATE;
        private Option<String> releasedBy = Option.none();
        private boolean beta = false;
        private boolean supported = false;
        private boolean staticAddon = false;
        private boolean deployable = false;
        private boolean autoUpdateAllowed = false;
        private boolean cloud = false;
        private boolean connect = false;
        private boolean dataCenter = false;
        private Option<AddonVersionDataCenterStatus> dataCenterStatus = Option.none();
        private boolean server = false;
        private Option<ImmutableList<ConnectScope>> scopes = Option.none();
        private Map<String, URI> externalLinks = new HashMap<String, URI>();

        @Override
        public AddonVersionSummary build() {
            AddonVersionSummary ret = new AddonVersionSummary();
            ret._links = this.links.build();
            ret._embedded = new AddonVersionSummary.Embedded();
            ret._embedded.artifact = Option.none();
            ret._embedded.functionalCategories = this.functionalCategories;
            for (URI u : this.artifactUri) {
                ret._embedded.artifact = Option.some((Object)TestModelBuilders.artifactInfo(u, this.connect));
            }
            ret.deployable = this.deployable;
            ret.deployment = new AddonVersionBase.DeploymentProperties();
            ret.deployment.autoUpdateAllowed = this.autoUpdateAllowed;
            ret.deployment.cloud = this.cloud;
            ret.deployment.connect = this.connect;
            ret.deployment.dataCenter = this.dataCenter;
            ret.deployment.dataCenterStatus = this.dataCenterStatus;
            ret.deployment.permissions = this.scopes;
            ret.deployment.server = this.server;
            ret.name = Option.some((Object)this.name);
            ret.paymentModel = this.paymentModel;
            ret.release = new AddonVersionBase.ReleaseProperties();
            ret.release.beta = this.beta;
            ret.release.date = this.releaseDate;
            ret.release.releasedBy = this.releasedBy;
            ret.release.supported = this.supported;
            ret.staticAddon = this.staticAddon;
            ret.status = this.status;
            ret.vendorLinks = ImmutableMap.copyOf(this.externalLinks);
            return EntityValidator.validateInstance(ret);
        }

        public AddonVersionSummaryBuilder artifactUri(Option<URI> artifactUri) {
            this.artifactUri = artifactUri;
            return this;
        }

        public AddonVersionSummaryBuilder functionalCategories(Iterable<AddonCategorySummary> categories) {
            this.functionalCategories = ImmutableList.copyOf(categories);
            this.links.put("categories", Options.flatten((Iterable)StreamSupport.stream(categories.spliterator(), false).map(EntityFunctions.selfUri()).collect(Collectors.toList())));
            return this;
        }

        public AddonVersionSummaryBuilder name(String name) {
            this.name = name;
            return this;
        }

        public AddonVersionSummaryBuilder status(AddonVersionStatus status) {
            this.status = status;
            return this;
        }

        public AddonVersionSummaryBuilder paymentModel(PaymentModel paymentModel) {
            this.paymentModel = paymentModel;
            return this;
        }

        public AddonVersionSummaryBuilder releaseDate(LocalDate releaseDate) {
            this.releaseDate = (LocalDate)Preconditions.checkNotNull((Object)releaseDate);
            return this;
        }

        public AddonVersionSummaryBuilder releasedBy(Option<String> releasedBy) {
            this.releasedBy = (Option)Preconditions.checkNotNull(releasedBy);
            return this;
        }

        public AddonVersionSummaryBuilder beta(boolean beta) {
            this.beta = beta;
            return this;
        }

        public AddonVersionSummaryBuilder supported(boolean supported) {
            this.supported = supported;
            return this;
        }

        public AddonVersionSummaryBuilder staticAddon(boolean staticAddon) {
            this.staticAddon = staticAddon;
            return this;
        }

        public AddonVersionSummaryBuilder autoUpdateAllowed(boolean autoUpdateAllowed) {
            this.autoUpdateAllowed = autoUpdateAllowed;
            return this;
        }

        public AddonVersionSummaryBuilder deployable(boolean deployable) {
            this.deployable = deployable;
            return this;
        }

        public AddonVersionSummaryBuilder cloud(boolean cloud) {
            this.cloud = cloud;
            return this;
        }

        public AddonVersionSummaryBuilder connect(boolean connect) {
            this.connect = connect;
            return this;
        }

        public AddonVersionSummaryBuilder dataCenter(boolean dataCenter) {
            this.dataCenter = dataCenter;
            return this;
        }

        public AddonVersionSummaryBuilder dataCenterStatus(Option<AddonVersionDataCenterStatus> dataCenterStatus) {
            this.dataCenterStatus = dataCenterStatus;
            return this;
        }

        public AddonVersionSummaryBuilder server(boolean server) {
            this.server = server;
            return this;
        }

        public AddonVersionSummaryBuilder scopes(Iterable<ConnectScope> scopes) {
            this.scopes = Option.some((Object)ImmutableList.copyOf(scopes));
            return this;
        }

        public AddonVersionSummaryBuilder externalLinkUri(AddonVersionExternalLinkType type, Option<URI> uri) {
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

    public static class AddonVersionBuilder
    extends ModelBuilders.BuilderWithLinks<AddonVersionBuilder>
    implements ModelBuilders.SafeBuilder<AddonVersion> {
        private ModelBuilders.AddonVersionBuilder builder;
        private Option<ArtifactInfo> artifactInfo = Option.none();
        private ImmutableList<AddonCategorySummary> functionalCategories = ImmutableList.of();
        private Option<LicenseType> licenseType = Option.none();
        private boolean autoUpdateAllowed = false;
        private boolean cloud = false;
        private boolean dataCenter = false;
        private Option<AddonVersionDataCenterStatus> dataCenterStatus = Option.none();
        private boolean connect = false;
        private Map<String, URI> legacyLinks = new HashMap<String, URI>();
        private boolean server = true;
        private Option<ImmutableList<ConnectScope>> scopes = Option.none();

        private AddonVersionBuilder() {
            this.builder = ModelBuilders.addonVersion().name(DEFAULT_STRING).buildNumber(DEFAULT_LONG).paymentModel(PaymentModel.FREE).releaseDate(DEFAULT_LOCAL_DATE).status(AddonVersionStatus.PUBLIC);
        }

        private AddonVersionBuilder(AddonVersion v) {
            this.builder = ModelBuilders.addonVersion(v);
            this.artifactInfo = v.getArtifactInfo();
            this.autoUpdateAllowed = v.isAutoUpdateAllowed();
            this.cloud = v.isCloud();
            this.connect = v.isConnect();
            this.dataCenter = v.isDataCenterCompatible();
            this.dataCenterStatus = v.getDataCenterStatus();
            this.licenseType = v.getLicenseType();
            this.server = v.isServer();
            this.scopes = v.isConnect() ? Option.some((Object)ImmutableList.copyOf(v.getConnectScopes())) : Option.none();
        }

        @Override
        public AddonVersion build() {
            this.builder.addLinks(this.links.build());
            Links ls = this.builder.links.build();
            Option a = this.artifactInfo;
            if (!a.isDefined()) {
                for (URI artifactUri : ls.getUri("artifact")) {
                    a = Option.some((Object)TestModelBuilders.artifactInfo(artifactUri, this.connect));
                }
            }
            AddonVersion v = TestModelBuilders.safeBuild(this.builder);
            v._embedded.artifact = a;
            v._embedded.functionalCategories = this.functionalCategories;
            v._embedded.license = this.licenseType;
            v.deployment.autoUpdateAllowed = this.autoUpdateAllowed;
            v.deployment.cloud = this.cloud;
            v.deployment.connect = this.connect;
            v.deployment.dataCenter = this.dataCenter;
            v.deployment.dataCenterStatus = this.dataCenterStatus;
            v.deployment.server = this.server;
            v.deployment.permissions = this.scopes;
            if (!this.legacyLinks.isEmpty()) {
                AddonVersion.LegacyProperties legacy = new AddonVersion.LegacyProperties();
                legacy.vendorLinks = ImmutableMap.copyOf(this.legacyLinks);
                v.legacy = Option.some((Object)legacy);
            }
            return v;
        }

        public AddonVersionBuilder artifact(Option<ArtifactInfo> artifact) {
            this.artifactInfo = (Option)Preconditions.checkNotNull(artifact);
            this.links.put("artifact", (Option<URI>)artifact.flatMap(EntityFunctions.selfUri()));
            return this;
        }

        public AddonVersionBuilder artifactUri(Option<URI> artifactUri) {
            this.links.put("artifact", artifactUri);
            this.artifactInfo = Option.none();
            return this;
        }

        public AddonVersionBuilder autoUpdateAllowed(boolean flag) {
            this.autoUpdateAllowed = flag;
            return this;
        }

        public AddonVersionBuilder buildNumber(long buildNumber) {
            this.builder.buildNumber(buildNumber);
            return this;
        }

        public AddonVersionBuilder cloud(boolean flag) {
            this.cloud = flag;
            return this;
        }

        public AddonVersionBuilder connect(boolean flag) {
            this.connect = flag;
            return this;
        }

        public AddonVersionBuilder dataCenter(boolean dataCenter) {
            this.dataCenter = dataCenter;
            return this;
        }

        public AddonVersionBuilder dataCenterStatus(Option<AddonVersionDataCenterStatus> dataCenterStatus) {
            this.dataCenterStatus = dataCenterStatus;
            return this;
        }

        public AddonVersionBuilder functionalCategories(Iterable<AddonCategorySummary> categories) {
            this.functionalCategories = ImmutableList.copyOf(categories);
            this.builder.links.put("categories", Options.flatten((Iterable)StreamSupport.stream(categories.spliterator(), false).map(EntityFunctions.selfUri()).collect(Collectors.toList())));
            return this;
        }

        public AddonVersionBuilder licenseType(Option<LicenseType> licenseType) {
            this.licenseType = (Option)Preconditions.checkNotNull(licenseType);
            this.builder.licenseType(licenseType);
            return this;
        }

        public AddonVersionBuilder name(String name) {
            this.builder.name(name);
            return this;
        }

        public AddonVersionBuilder status(AddonVersionStatus status) {
            this.builder.status(status);
            return this;
        }

        public AddonVersionBuilder paymentModel(PaymentModel paymentModel) {
            this.builder.paymentModel(paymentModel);
            return this;
        }

        public AddonVersionBuilder releaseDate(LocalDate releaseDate) {
            this.builder.releaseDate(releaseDate);
            return this;
        }

        public AddonVersionBuilder releasedBy(Option<String> releasedBy) {
            this.builder.releasedBy(releasedBy);
            return this;
        }

        public AddonVersionBuilder beta(boolean flag) {
            this.builder.beta(flag);
            return this;
        }

        public AddonVersionBuilder supported(boolean flag) {
            this.builder.supported(flag);
            return this;
        }

        public AddonVersionBuilder server(boolean flag) {
            this.server = flag;
            return this;
        }

        public AddonVersionBuilder highlights(Iterable<Highlight> highlights) {
            this.builder.highlights(highlights);
            return this;
        }

        public AddonVersionBuilder screenshots(Iterable<Screenshot> screenshots) {
            this.builder.screenshots(screenshots);
            return this;
        }

        public AddonVersionBuilder youtubeId(Option<String> youtubeId) {
            this.builder.youtubeId(youtubeId);
            return this;
        }

        public AddonVersionBuilder compatibilities(Iterable<VersionCompatibility> compatibilities) {
            this.builder.compatibilities(compatibilities);
            return this;
        }

        public AddonVersionBuilder staticAddon(boolean staticAddon) {
            this.builder.staticAddon(staticAddon);
            return this;
        }

        public AddonVersionBuilder deployable(boolean deployable) {
            this.builder.deployable(deployable);
            return this;
        }

        public AddonVersionBuilder releaseSummary(Option<String> releaseSummary) {
            this.builder.releaseSummary(releaseSummary);
            return this;
        }

        public AddonVersionBuilder moreDetails(Option<HtmlString> moreDetails) {
            this.builder.moreDetails(moreDetails);
            return this;
        }

        public AddonVersionBuilder releaseNotes(Option<HtmlString> releaseNotes) {
            this.builder.releaseNotes(releaseNotes);
            return this;
        }

        public AddonVersionBuilder scopes(Iterable<ConnectScope> scopes) {
            this.scopes = Option.some((Object)ImmutableList.copyOf(scopes));
            return this;
        }

        public AddonVersionBuilder externalLinkUri(AddonVersionExternalLinkType type, Option<URI> uri) {
            if (type.canSetForNewAddonVersions()) {
                this.builder.externalLinkUri(type, uri);
            } else {
                this.legacyLinks.remove(type.getKey());
                for (URI u : uri) {
                    this.legacyLinks.put(type.getKey(), u);
                }
            }
            return this;
        }
    }

    public static class AddonSummaryBuilder
    extends ModelBuilders.BuilderWithLinks<AddonSummaryBuilder>
    implements ModelBuilders.SafeBuilder<AddonSummary> {
        private String key = DEFAULT_STRING;
        private String name = DEFAULT_STRING;
        private AddonStatus status = AddonStatus.PUBLIC;
        private Option<String> summary = Option.none();
        private Option<Integer> cloudFreeUsers = Option.none();
        private ImmutableList<AddonCategorySummary> categories = ImmutableList.of();
        private AddonDistributionSummary distribution = TestModelBuilders.addonDistributionSummary().build();
        private Option<ImageInfo> logo = Option.none();
        private AddonReviewsSummary reviews = TestModelBuilders.addonReviewsSummary(DEFAULT_STARS, DEFAULT_INT);
        private Option<VendorSummary> vendor = Option.none();
        private Option<AddonVersionSummary> version = Option.none();

        private AddonSummaryBuilder() {
            this.links.put("alternate", (Option<String>)Option.some((Object)"text/html"), DEFAULT_URI);
            this.links.put("vendor", DEFAULT_URI);
        }

        @Override
        public AddonSummary build() {
            AddonSummary ret = new AddonSummary();
            ret._links = this.links.build();
            ret._embedded = new AddonSummary.Embedded();
            ret._embedded.categories = this.categories;
            ret._embedded.distribution = this.distribution;
            ret._embedded.logo = this.logo;
            ret._embedded.reviews = this.reviews;
            ret._embedded.vendor = this.vendor;
            ret._embedded.version = this.version;
            ret.cloudFreeUsers = this.cloudFreeUsers;
            ret.key = this.key;
            ret.name = this.name;
            ret.status = this.status;
            ret.summary = this.summary;
            ret.storesPersonalData = Option.some((Object)false);
            return EntityValidator.validateInstance(ret);
        }

        public AddonSummaryBuilder key(String key) {
            this.key = key;
            return this;
        }

        public AddonSummaryBuilder name(String name) {
            this.name = name;
            return this;
        }

        public AddonSummaryBuilder status(AddonStatus status) {
            this.status = status;
            return this;
        }

        public AddonSummaryBuilder summary(Option<String> summary) {
            this.summary = (Option)Preconditions.checkNotNull(summary);
            return this;
        }

        public AddonSummaryBuilder cloudFreeUsers(Option<Integer> cloudFreeUsers) {
            this.cloudFreeUsers = (Option)Preconditions.checkNotNull(cloudFreeUsers);
            return this;
        }

        public AddonSummaryBuilder categories(Iterable<AddonCategorySummary> categories) {
            this.categories = ImmutableList.copyOf(categories);
            this.links.put("categories", Options.flatten((Iterable)StreamSupport.stream(categories.spliterator(), false).map(EntityFunctions.selfUri()).collect(Collectors.toList())));
            return this;
        }

        public AddonSummaryBuilder distribution(AddonDistributionSummary distribution) {
            this.distribution = distribution;
            return this;
        }

        public AddonSummaryBuilder logo(Option<ImageInfo> logo) {
            this.logo = logo;
            this.links.put("logo", (Option<URI>)logo.flatMap(EntityFunctions.selfUri()));
            return this;
        }

        public AddonSummaryBuilder reviews(AddonReviewsSummary reviews) {
            this.reviews = reviews;
            return this;
        }

        public AddonSummaryBuilder vendor(Option<VendorSummary> vendor) {
            this.vendor = vendor;
            this.links.put("vendor", (Option<URI>)vendor.flatMap(EntityFunctions.selfUri()));
            return this;
        }

        public AddonSummaryBuilder version(Option<AddonVersionSummary> version) {
            this.version = version;
            return this;
        }
    }

    public static class AddonReferenceBuilder
    extends ModelBuilders.BuilderWithLinks<AddonReferenceBuilder>
    implements ModelBuilders.SafeBuilder<AddonReference> {
        private String key = DEFAULT_STRING;
        private String name = DEFAULT_STRING;
        private Option<ImageInfo> image = Option.none();
        private Option<AddonReviewsSummary> reviews = Option.none();

        private AddonReferenceBuilder() {
            this.links.put("self", DEFAULT_URI);
            this.links.put("alternate", (Option<String>)Option.some((Object)"text/html"), DEFAULT_URI);
            this.links.put("vendor", DEFAULT_URI);
        }

        @Override
        public AddonReference build() {
            AddonReference ret = new AddonReference();
            ret._links = this.links.build();
            ret._embedded = new AddonReference.Embedded();
            ret._embedded.image = this.image;
            ret._embedded.reviews = this.reviews;
            ret.name = this.name;
            ret.key = this.key;
            return ret;
        }

        public AddonReferenceBuilder key(String key) {
            this.key = key;
            return this;
        }

        public AddonReferenceBuilder name(String name) {
            this.name = name;
            return this;
        }

        public AddonReferenceBuilder image(Option<ImageInfo> image) {
            this.image = image;
            this.links.put("image", (Option<URI>)image.flatMap(EntityFunctions.selfUri()));
            return this;
        }

        public AddonReferenceBuilder reviews(Option<AddonReviewsSummary> reviews) {
            this.reviews = reviews;
            return this;
        }
    }

    public static class AddonPricingItemBuilder
    implements ModelBuilders.SafeBuilder<AddonPricingItem> {
        private String description = DEFAULT_STRING;
        private String editionId = DEFAULT_STRING;
        private String editionDescription = DEFAULT_STRING;
        private LicenseEditionType editionType = LicenseEditionType.USER_TIER;
        private String licenseType = DEFAULT_STRING;
        private float amount;
        private Option<Float> renewalAmount = Option.none();
        private int unitCount = 1;
        private int monthsValid = 1;

        @Override
        public AddonPricingItem build() {
            AddonPricingItem ret = new AddonPricingItem();
            ret.description = this.description;
            ret.editionId = this.editionId;
            ret.editionDescription = this.editionDescription;
            ret.editionType = this.editionType;
            ret.licenseType = this.licenseType;
            ret.amount = this.amount;
            ret.renewalAmount = this.renewalAmount;
            ret.unitCount = this.unitCount;
            ret.monthsValid = this.monthsValid;
            return ret;
        }

        public AddonPricingItemBuilder description(String description) {
            this.description = (String)Preconditions.checkNotNull((Object)description);
            return this;
        }

        public AddonPricingItemBuilder editionId(String editionId) {
            this.editionId = (String)Preconditions.checkNotNull((Object)editionId);
            return this;
        }

        public AddonPricingItemBuilder editionDescription(String editionDescription) {
            this.editionDescription = (String)Preconditions.checkNotNull((Object)editionDescription);
            return this;
        }

        public AddonPricingItemBuilder editionType(LicenseEditionType editionType) {
            this.editionType = (LicenseEditionType)Preconditions.checkNotNull((Object)editionType);
            return this;
        }

        public AddonPricingItemBuilder licenseType(String licenseType) {
            this.licenseType = (String)Preconditions.checkNotNull((Object)licenseType);
            return this;
        }

        public AddonPricingItemBuilder amount(float amount) {
            this.amount = amount;
            return this;
        }

        public AddonPricingItemBuilder renewalAmount(Option<Float> renewalAmount) {
            this.renewalAmount = (Option)Preconditions.checkNotNull(renewalAmount);
            return this;
        }

        public AddonPricingItemBuilder unitCount(int unitCount) {
            this.unitCount = unitCount;
            return this;
        }

        public AddonPricingItemBuilder monthsValid(int monthsValid) {
            this.monthsValid = monthsValid;
            return this;
        }
    }

    public static class AddonPricingBuilder
    extends ModelBuilders.BuilderWithLinks<AddonPricingBuilder>
    implements ModelBuilders.SafeBuilder<AddonPricing> {
        private ModelBuilders.LinksBuilder links = ModelBuilders.links().put("self", DEFAULT_URI);
        private ImmutableList<AddonPricingItem> items = ImmutableList.of();
        private Option<ImmutableList<AddonPricingItem>> perUnitItems = Option.none();
        private boolean expertDiscountOptOut;
        private boolean contactSalesForAdditionalPricing;
        private Option<String> parent = Option.none();
        private Option<DateTime> lastModified = Option.none();
        private Option<AddonPricing.RoleInfo> role = Option.none();

        @Override
        public AddonPricing build() {
            AddonPricing ret = new AddonPricing();
            ret._links = this.links.build();
            ret.items = this.items;
            ret.perUnitItems = this.perUnitItems;
            ret.expertDiscountOptOut = this.expertDiscountOptOut;
            ret.contactSalesForAdditionalPricing = this.contactSalesForAdditionalPricing;
            ret.parent = this.parent;
            ret.lastModified = this.lastModified;
            ret.role = this.role;
            return ret;
        }

        public AddonPricingBuilder items(Iterable<AddonPricingItem> items) {
            this.items = ImmutableList.copyOf(items);
            return this;
        }

        public AddonPricingBuilder perUnitItems(Iterable<AddonPricingItem> items) {
            this.perUnitItems = Option.some((Object)ImmutableList.copyOf(items));
            return this;
        }

        public AddonPricingBuilder expertDiscountOptOut(boolean expertDiscountOptOut) {
            this.expertDiscountOptOut = expertDiscountOptOut;
            return this;
        }

        public AddonPricingBuilder contactSalesForAdditionalPricing(boolean contactSalesForAdditionalPricing) {
            this.contactSalesForAdditionalPricing = contactSalesForAdditionalPricing;
            return this;
        }

        public AddonPricingBuilder parent(Option<String> parent) {
            this.parent = (Option)Preconditions.checkNotNull(parent);
            return this;
        }

        public AddonPricingBuilder lastModified(Option<DateTime> lastModified) {
            this.lastModified = (Option)Preconditions.checkNotNull(lastModified);
            return this;
        }

        public AddonPricingBuilder role(Option<Pair<String, String>> singularAndPluralRoleName) {
            Iterator iterator = singularAndPluralRoleName.iterator();
            if (iterator.hasNext()) {
                Pair p = (Pair)iterator.next();
                AddonPricing.RoleInfo r = new AddonPricing.RoleInfo();
                r.singularName = (String)p.left();
                r.pluralName = (String)p.right();
                this.role = Option.some((Object)r);
                return this;
            }
            this.role = Option.none();
            return this;
        }
    }

    public static class AddonDistributionSummaryBuilder
    implements ModelBuilders.SafeBuilder<AddonDistributionSummary> {
        private boolean bundled;
        private boolean bundledCloud;
        private int downloads = 1;
        private Option<Integer> totalInstalls = Option.none();
        private Option<Integer> totalUsers = Option.none();

        @Override
        public AddonDistributionSummary build() {
            AddonDistributionSummary ret = new AddonDistributionSummary();
            ret.bundled = this.bundled;
            ret.bundledCloud = this.bundledCloud;
            ret.downloads = this.downloads;
            ret.totalInstalls = this.totalInstalls;
            ret.totalUsers = this.totalUsers;
            return ret;
        }

        public AddonDistributionSummaryBuilder bundled(boolean bundled) {
            this.bundled = bundled;
            return this;
        }

        public AddonDistributionSummaryBuilder bundledCloud(boolean bundledCloud) {
            this.bundledCloud = bundledCloud;
            return this;
        }

        public AddonDistributionSummaryBuilder downloads(int downloads) {
            this.downloads = downloads;
            return this;
        }

        public AddonDistributionSummaryBuilder totalInstalls(Option<Integer> totalInstalls) {
            this.totalInstalls = totalInstalls;
            return this;
        }

        public AddonDistributionSummaryBuilder totalUsers(Option<Integer> totalUsers) {
            this.totalUsers = totalUsers;
            return this;
        }
    }

    public static class AddonCategorySummaryBuilder
    extends ModelBuilders.BuilderWithLinks<AddonCategorySummaryBuilder>
    implements ModelBuilders.SafeBuilder<AddonCategorySummary> {
        private String name = DEFAULT_STRING;

        @Override
        public AddonCategorySummary build() {
            AddonCategorySummary ret = new AddonCategorySummary();
            ret._links = this.links.build();
            ret.name = this.name;
            return ret;
        }

        public AddonCategorySummaryBuilder name(String name) {
            this.name = (String)Preconditions.checkNotNull((Object)name);
            return this;
        }
    }

    public static class AddonBuilder
    extends ModelBuilders.BuilderWithLinks<AddonBuilder>
    implements ModelBuilders.SafeBuilder<Addon> {
        private ModelBuilders.AddonBuilder builder;
        private Option<ImageInfo> banner = Option.none();
        private ImmutableList<AddonCategorySummary> categories = ImmutableList.of();
        private AddonDistributionSummary distribution = TestModelBuilders.addonDistributionSummary().build();
        private Option<ImageInfo> logo = Option.none();
        private AddonReviewsSummary reviews = TestModelBuilders.addonReviewsSummary(DEFAULT_STARS, DEFAULT_INT);
        private VendorSummary vendor = TestModelBuilders.vendorSummary().build();
        private Option<Integer> cloudFreeUsers = Option.none();
        private Map<String, URI> legacyLinks = new HashMap<String, URI>();
        private Option<HtmlString> legacyDescription = Option.none();

        private AddonBuilder() {
            this.builder = ModelBuilders.addon().name(DEFAULT_STRING).key(DEFAULT_STRING).status(AddonStatus.PUBLIC).vendor(VendorId.fromUri(DEFAULT_URI)).storesPersonalData((Option<Boolean>)Option.some((Object)false));
        }

        private AddonBuilder(Addon a) {
            this.builder = ModelBuilders.addon(a);
            this.banner = a.getBanner();
            this.categories = ImmutableList.copyOf(a.getCategories());
            this.distribution = a.getDistribution();
            this.logo = a.getLogo();
            this.reviews = a.getReviews();
            this.vendor = (VendorSummary)a.getVendor().getOrElse((Object)this.vendor);
            this.legacyDescription = a.getDescription();
        }

        @Override
        public Addon build() {
            this.builder.addLinks(this.links.build());
            Addon a = TestModelBuilders.safeBuild(this.builder);
            a._embedded.banner = this.banner;
            a._embedded.logo = this.logo;
            a._embedded.categories = this.categories;
            a._embedded.distribution = this.distribution;
            a._embedded.reviews = this.reviews;
            a._embedded.vendor = Option.some((Object)this.vendor);
            a.cloudFreeUsers = this.cloudFreeUsers;
            if (!this.legacyLinks.isEmpty() || !this.legacyDescription.isEmpty()) {
                Addon.LegacyProperties legacy = new Addon.LegacyProperties();
                legacy.description = this.legacyDescription;
                legacy.vendorLinks = ImmutableMap.copyOf(this.legacyLinks);
            }
            return a;
        }

        public AddonBuilder name(String name) {
            this.builder.name(name);
            return this;
        }

        public AddonBuilder key(String key) {
            this.builder.key(key);
            return this;
        }

        public AddonBuilder status(AddonStatus status) {
            this.builder.status(status);
            return this;
        }

        public AddonBuilder summary(Option<String> summary) {
            this.builder.summary(summary);
            return this;
        }

        public AddonBuilder tagLine(Option<String> tagLine) {
            this.builder.tagLine(tagLine);
            return this;
        }

        public AddonBuilder cloudFreeUsers(Option<Integer> cloudFreeUsers) {
            this.cloudFreeUsers = (Option)Preconditions.checkNotNull(cloudFreeUsers);
            return this;
        }

        public AddonBuilder banner(Option<ImageInfo> banner) {
            this.banner = (Option)Preconditions.checkNotNull(banner);
            this.links.put("banner", (Option<URI>)banner.flatMap(EntityFunctions.selfUri()));
            return this;
        }

        public AddonBuilder logo(Option<ImageInfo> logo) {
            this.logo = (Option)Preconditions.checkNotNull(logo);
            this.links.put("logo", (Option<URI>)logo.flatMap(EntityFunctions.selfUri()));
            return this;
        }

        public AddonBuilder categories(Iterable<AddonCategorySummary> categories) {
            this.categories = ImmutableList.copyOf((Iterable)((Iterable)Preconditions.checkNotNull(categories)));
            this.builder.links.put("categories", Options.flatten((Iterable)StreamSupport.stream(categories.spliterator(), false).map(EntityFunctions.selfUri()).collect(Collectors.toList())));
            return this;
        }

        public AddonBuilder distribution(AddonDistributionSummary distribution) {
            this.distribution = distribution;
            return this;
        }

        public AddonBuilder reviews(AddonReviewsSummary reviews) {
            this.reviews = reviews;
            return this;
        }

        public AddonBuilder vendor(VendorSummary vendor) {
            this.vendor = (VendorSummary)Preconditions.checkNotNull((Object)vendor);
            this.builder.vendor(vendor);
            return this;
        }

        public AddonBuilder version(Option<AddonVersion> version) {
            this.builder.version(version);
            return this;
        }

        public AddonBuilder externalLinkUri(AddonExternalLinkType type, Option<URI> issueTrackerUri) {
            if (type.canSetForNewAddons()) {
                this.builder.externalLinkUri(type, issueTrackerUri);
            } else {
                this.legacyLinks.remove(type.getKey());
                for (URI u : issueTrackerUri) {
                    this.legacyLinks.put(type.getKey(), u);
                }
            }
            return this;
        }
    }
}

