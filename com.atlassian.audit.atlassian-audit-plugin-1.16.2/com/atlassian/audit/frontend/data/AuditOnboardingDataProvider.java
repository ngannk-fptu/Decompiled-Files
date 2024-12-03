/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.audit.frontend.data;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.audit.coverage.ProductLicenseChecker;
import com.atlassian.audit.frontend.data.AuditOnboardingData;
import com.atlassian.audit.frontend.data.AuditingOnboardingDisplayInfoData;
import com.atlassian.audit.plugin.AuditPluginInfo;
import com.atlassian.audit.plugin.onboarding.OnboardingSeenService;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.codehaus.jackson.map.ObjectMapper;

public class AuditOnboardingDataProvider
implements WebResourceDataProvider {
    private static final String FALLBACK_URL = "http://www.atlassian.com/";
    private static final String PROP_FILE_PATH = "/atlassian-audit-onboarding-settings.properties";
    private final AuditPluginInfo auditPluginInfo;
    private final OnboardingSeenService onboardingSeenService;
    private final I18nResolver resolver;
    private final ObjectMapper objectMapper;
    private final ProductLicenseChecker licenseChecker;
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final Properties auditSettings;
    private final ApplicationProperties applicationProperties;

    public AuditOnboardingDataProvider(AuditPluginInfo auditPluginInfo, OnboardingSeenService onboardingSeenService, ObjectMapper objectMapper, I18nResolver resolver, ProductLicenseChecker licenseChecker, WebResourceUrlProvider webResourceUrlProvider, ApplicationProperties applicationProperties) throws IOException {
        this(auditPluginInfo, onboardingSeenService, objectMapper, resolver, licenseChecker, webResourceUrlProvider, applicationProperties, PROP_FILE_PATH);
    }

    @VisibleForTesting
    AuditOnboardingDataProvider(AuditPluginInfo auditPluginInfo, OnboardingSeenService onboardingSeenService, ObjectMapper objectMapper, I18nResolver resolver, ProductLicenseChecker licenseChecker, WebResourceUrlProvider webResourceUrlProvider, ApplicationProperties applicationProperties, String propFilePath) throws IOException {
        this.auditPluginInfo = auditPluginInfo;
        this.onboardingSeenService = onboardingSeenService;
        this.objectMapper = objectMapper;
        this.resolver = resolver;
        this.licenseChecker = licenseChecker;
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.applicationProperties = applicationProperties;
        try (InputStream inputStream = this.getClass().getResourceAsStream(propFilePath);){
            this.auditSettings = new Properties();
            this.auditSettings.load(inputStream);
        }
    }

    public Jsonable get() {
        return writer -> this.objectMapper.writeValue(writer, (Object)this.getData());
    }

    private AuditOnboardingData getData() {
        if (!this.onboardingSeenService.shouldDisplay()) {
            return new AuditOnboardingData(Collections.emptyList());
        }
        boolean isDc = this.licenseChecker.isDcLicenseOrExempt();
        return new AuditOnboardingData(this.getOnboardingContents().stream().filter(data -> isDc || !data.isDcOnly()).collect(Collectors.toList()));
    }

    private List<AuditingOnboardingDisplayInfoData> getOnboardingContents() {
        return IntStream.rangeClosed(1, this.getMaximumTabs()).mapToObj(this::createAuditingContent).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    private Optional<String> getProperty(String propertyName) {
        String translatedText = this.resolver.getText(propertyName);
        if (translatedText == null || translatedText.equals(propertyName)) {
            return Optional.empty();
        }
        return Optional.of(translatedText);
    }

    private String getProductNameForPropertiesFile() {
        String productName = this.applicationProperties.getPlatformId();
        if (productName.equals("bitbucket") || productName.equals("stash")) {
            return "bitbucket";
        }
        return productName;
    }

    private Optional<AuditingOnboardingDisplayInfoData> createAuditingContent(int index) {
        Optional<String> dcOnlyFeature = Optional.ofNullable(this.auditSettings.getProperty(String.format("atlassian.audit.onboarding.%d.support.dc.only", index)));
        Optional<String> title = this.getProperty(String.format("atlassian.audit.onboarding.%d.title", index));
        Optional<String> description = this.getProperty(String.format("atlassian.audit.onboarding.%d.description", index));
        Optional<String> confirmButtonLabel = this.getProperty(String.format("atlassian.audit.onboarding.%d.confirm.btn.label", index));
        Optional<String> learnMoreButtonLabel = this.getProperty(String.format("atlassian.audit.onboarding.%d.learn.btn.label", index));
        Optional<String> imageUrl = Optional.ofNullable(this.auditSettings.getProperty(String.format("atlassian.audit.onboarding.%d.image", index)));
        Optional<String> articleUrl = Optional.ofNullable(this.auditSettings.getProperty(String.format("atlassian.audit.onboarding.%d.article." + this.getProductNameForPropertiesFile(), index), FALLBACK_URL));
        if (!(dcOnlyFeature.isPresent() && title.isPresent() && description.isPresent() && imageUrl.isPresent() && articleUrl.isPresent() && confirmButtonLabel.isPresent() && learnMoreButtonLabel.isPresent())) {
            return Optional.empty();
        }
        String image = this.webResourceUrlProvider.getStaticPluginResourceUrl(this.auditPluginInfo.getPluginKey() + ":audit-resources", imageUrl.get(), UrlMode.AUTO);
        return Optional.of(new AuditingOnboardingDisplayInfoData(Boolean.parseBoolean(dcOnlyFeature.get()), title.get(), description.get(), image, articleUrl.get(), confirmButtonLabel.get(), learnMoreButtonLabel.get()));
    }

    private int getMaximumTabs() {
        return Integer.parseInt(this.auditSettings.getProperty("atlassian.audit.onboarding.maximum.tabs"));
    }
}

