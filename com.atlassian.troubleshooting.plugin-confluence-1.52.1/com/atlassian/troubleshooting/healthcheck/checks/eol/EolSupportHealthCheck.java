/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck.checks.eol;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.healthcheck.SupportHealthStatusBuilder;
import com.atlassian.troubleshooting.healthcheck.checks.eol.ClockFactory;
import com.atlassian.troubleshooting.healthcheck.checks.eol.EolCheckVersionMissingEvent;
import com.atlassian.troubleshooting.healthcheck.checks.eol.Product;
import com.atlassian.troubleshooting.healthcheck.checks.eol.ProductReleaseDateManager;
import com.atlassian.troubleshooting.healthcheck.checks.eol.Release;
import com.atlassian.troubleshooting.stp.spi.Version;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import java.io.InputStream;
import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;

public class EolSupportHealthCheck
implements SupportHealthCheck {
    @VisibleForTesting
    static final int SUPPORTED_PERIOD_IN_MONTHS = 24;
    @VisibleForTesting
    static final int WARNING_TIME_BEFORE_EOL_IN_MONTHS = 6;
    @VisibleForTesting
    static final int SHOW_AS_DAYS_LIMIT = 14;
    private static final Map<String, String> MPAC_CODE_BY_PLATFORM_ID = ImmutableMap.of((Object)"conf", (Object)"confluence", (Object)"fisheye", (Object)"fecru", (Object)"stash", (Object)"bitbucket");
    private final ApplicationProperties applicationProperties;
    private final LocaleResolver localeResolver;
    private final Clock clock;
    private final SupportHealthStatusBuilder statusBuilder;
    private final EventPublisher eventPublisher;
    private final ProductReleaseDateManager productReleaseDateManager;

    @Autowired
    public EolSupportHealthCheck(ApplicationProperties applicationProperties, LocaleResolver localeResolver, ClockFactory clockFactory, SupportHealthStatusBuilder statusBuilder, EventPublisher eventPublisher, ProductReleaseDateManager productReleaseDateManager) {
        this.applicationProperties = applicationProperties;
        this.localeResolver = localeResolver;
        this.clock = clockFactory.makeClock();
        this.statusBuilder = statusBuilder;
        this.eventPublisher = eventPublisher;
        this.productReleaseDateManager = productReleaseDateManager;
    }

    private static SupportHealthStatus checkReleaseDate(Builder builder, LocalDate today, Release release) {
        LocalDate warningDate;
        LocalDate eolDate;
        if (release.getOverriddenEOLDate() != null) {
            eolDate = EolSupportHealthCheck.toLocalDate(release.getOverriddenEOLDate());
            warningDate = EolSupportHealthCheck.calculateWarningDateFromEOLDate(eolDate);
        } else {
            LocalDate releaseDate = EolSupportHealthCheck.toLocalDate(release.getReleaseDate());
            eolDate = EolSupportHealthCheck.calculateEolDate(releaseDate);
            warningDate = EolSupportHealthCheck.calculateWarningDateFromReleaseDate(releaseDate);
        }
        builder = builder.daysTilEndOfLife(today, eolDate);
        if (today.isBefore(warningDate)) {
            return builder.ok().key("healthcheck.eol.pass").build();
        }
        if (today.isBefore(eolDate)) {
            return builder.warning().key("healthcheck.eol.warn").build();
        }
        return builder.major().key("healthcheck.eol.fail").build();
    }

    private static LocalDate calculateEolDate(LocalDate releaseDate) {
        return releaseDate.plusMonths(24L);
    }

    private static LocalDate calculateWarningDateFromReleaseDate(LocalDate releaseDate) {
        return releaseDate.plusMonths(18L);
    }

    private static LocalDate calculateWarningDateFromEOLDate(LocalDate eolDate) {
        return eolDate.minusMonths(6L);
    }

    private static LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static String mapPlatformToMarketplaceName(String platformId) {
        String marketPlaceName = MPAC_CODE_BY_PLATFORM_ID.get(platformId);
        return marketPlaceName != null ? marketPlaceName : platformId;
    }

    @Override
    public boolean isNodeSpecific() {
        return false;
    }

    @Override
    public SupportHealthStatus check() {
        LocalDate today = LocalDate.now(this.clock);
        Optional<Version> versionOption = Version.strictOf(this.applicationProperties.getVersion());
        if (versionOption.isPresent()) {
            Version version = versionOption.get();
            Builder builder = new Builder(this).minorVersion(version.getMajorAndMinor()).productName(this.applicationProperties.getDisplayName());
            if (this.buildDateImpliesNoWarningNeeded(version, today)) {
                return builder.ok().key("healthcheck.eol.pass-estimated").build();
            }
            return this.checkForProduct(today, EolSupportHealthCheck.mapPlatformToMarketplaceName(this.applicationProperties.getPlatformId()), version, builder);
        }
        return this.errorPerformingHealthCheck(this.applicationProperties.getDisplayName(), this.applicationProperties.getVersion());
    }

    private boolean buildDateImpliesNoWarningNeeded(Version version, LocalDate today) {
        LocalDate warningDate = EolSupportHealthCheck.calculateWarningDateFromReleaseDate(EolSupportHealthCheck.toLocalDate(this.applicationProperties.getBuildDate()));
        return version.isFirstMinorVersion() && today.isBefore(warningDate);
    }

    private SupportHealthStatus checkForProduct(LocalDate today, String marketplaceName, Version version, Builder builder) {
        InputStream jsonFile = this.getClass().getResourceAsStream("/product-release-dates.json");
        List<Product> products = this.productReleaseDateManager.readProducts(jsonFile);
        Optional<Product> optionalProduct = products.stream().filter(p -> p.getName().equals(marketplaceName)).findFirst();
        return optionalProduct.flatMap(product -> this.checkLocalReleaseDatesForVersion(builder, today, (Product)product, version)).orElseGet(() -> this.errorPerformingHealthCheck(marketplaceName, version.getMajorAndMinor()));
    }

    private Optional<SupportHealthStatus> checkLocalReleaseDatesForVersion(Builder builder, LocalDate today, Product product, Version version) {
        Optional<Release> release = product.getReleases().stream().filter(r -> r.getVersion().equals(version.getMajorAndMinor())).findFirst();
        return release.map(r -> EolSupportHealthCheck.checkReleaseDate(builder, today, r));
    }

    private SupportHealthStatus errorPerformingHealthCheck(String marketplaceName, String version) {
        this.triggerMissingVersionEvent(marketplaceName, version);
        return this.statusBuilder.ok(this, "healthcheck.eol.error", new Serializable[]{this.applicationProperties.getDisplayName(), this.applicationProperties.getVersion()});
    }

    private void triggerMissingVersionEvent(String marketplaceName, String versionString) {
        EolCheckVersionMissingEvent event = new EolCheckVersionMissingEvent(marketplaceName, versionString);
        this.eventPublisher.publish((Object)event);
    }

    private class Builder {
        private final SupportHealthCheck healthCheck;
        private SupportHealthStatus.Severity severity;
        private String key;
        private String minorVersion;
        private String productName;
        private LocalDate today;
        private LocalDate eolDate;

        Builder(SupportHealthCheck healthCheck) {
            this.healthCheck = Objects.requireNonNull(healthCheck);
        }

        public Builder ok() {
            this.severity = SupportHealthStatus.Severity.UNDEFINED;
            return this;
        }

        public Builder warning() {
            this.severity = SupportHealthStatus.Severity.WARNING;
            return this;
        }

        public Builder major() {
            this.severity = SupportHealthStatus.Severity.MAJOR;
            return this;
        }

        public Builder critical() {
            this.severity = SupportHealthStatus.Severity.CRITICAL;
            return this;
        }

        public Builder productName(String name) {
            this.productName = Objects.requireNonNull(name);
            return this;
        }

        public Builder minorVersion(String minorVersion) {
            this.minorVersion = Objects.requireNonNull(minorVersion);
            return this;
        }

        public Builder key(String key) {
            this.key = Objects.requireNonNull(key);
            return this;
        }

        public SupportHealthStatus build() {
            if (this.healthCheck == null || this.severity == null || this.key == null || this.minorVersion == null || this.productName == null) {
                throw new IllegalArgumentException("Builder parameter not set: " + this);
            }
            if (this.today != null && this.eolDate != null) {
                long daysTilEol = ChronoUnit.DAYS.between(this.today, this.eolDate);
                boolean showAsDayCount = daysTilEol <= 14L && daysTilEol > 0L;
                String keySuffix = showAsDayCount ? ".days" : ".date";
                Long daysTilEolValue = showAsDayCount ? Long.valueOf(daysTilEol) : DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(EolSupportHealthCheck.this.localeResolver.getLocale()).format(this.eolDate);
                return EolSupportHealthCheck.this.statusBuilder.buildStatus(this.healthCheck, this.severity, this.key + keySuffix, new Serializable[]{this.minorVersion, daysTilEolValue, this.productName});
            }
            return EolSupportHealthCheck.this.statusBuilder.buildStatus(this.healthCheck, this.severity, this.key, new Serializable[]{this.minorVersion, this.productName});
        }

        Builder daysTilEndOfLife(LocalDate today, LocalDate eolDate) {
            this.today = today;
            this.eolDate = eolDate;
            return this;
        }

        public String toString() {
            return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.MULTI_LINE_STYLE);
        }
    }
}

