/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.app;

import com.atlassian.migration.agent.dto.assessment.AppAssessmentUpdateRequest;
import com.atlassian.migration.agent.dto.assessment.UpdateAllAppAssessmentInfoRequest;
import com.atlassian.migration.agent.entity.AppAssessmentInfo;
import com.atlassian.migration.agent.entity.AppAssessmentProperty;
import com.atlassian.migration.agent.entity.AppAssessmentUserAttributedStatus;
import com.atlassian.migration.agent.entity.AssessmentConsent;
import com.atlassian.migration.agent.service.app.PluginManager;
import com.atlassian.migration.agent.store.impl.AppAssessmentInfoStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.atlassian.plugin.Plugin;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class AppAssessmentInfoService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(AppAssessmentInfoService.class);
    private static final AppAssessmentUserAttributedStatus DEFAULT_ASSESSMENT_STATUS = AppAssessmentUserAttributedStatus.Unassigned;
    private static final AssessmentConsent DEFAULT_CONSENT_STATUS = AssessmentConsent.NotGiven;
    private static final String ASSESSMENT_PROPERTY_DEFAULT_STRING_VALUE = "";
    private final AppAssessmentInfoStore appAssessmentInfoStore;
    private final PluginManager pluginManager;
    private final PluginTransactionTemplate ptx;

    public AppAssessmentInfoService(AppAssessmentInfoStore appAssessmentInfoStore, PluginManager pluginManager, PluginTransactionTemplate ptx) {
        this.appAssessmentInfoStore = appAssessmentInfoStore;
        this.pluginManager = pluginManager;
        this.ptx = ptx;
    }

    public List<AppAssessmentInfo> getAllAppAssessmentInfos() {
        return this.appAssessmentInfoStore.getAll();
    }

    public List<AppAssessmentInfo> getAppAssessmentInfosNeededInCloud() {
        List appInfos = this.ptx.read(this.appAssessmentInfoStore::getAppsNeededInCloud);
        Set installedPlugIns = this.pluginManager.getActualUserInstalledPlugins().stream().map(Plugin::getKey).collect(Collectors.toSet());
        return appInfos.stream().filter(app -> installedPlugIns.contains(app.getAppKey())).collect(Collectors.toList());
    }

    public boolean isAppConsented(String appKey) {
        Optional<AppAssessmentInfo> maybeAppAssessmentInfo = this.getAppAssessmentInfoByAppKey(appKey);
        return maybeAppAssessmentInfo.filter(appAssessmentInfo -> AssessmentConsent.Given.equals((Object)appAssessmentInfo.getConsent())).isPresent();
    }

    public Optional<AppAssessmentInfo> getAppAssessmentInfoByAppKey(String appKey) {
        return this.ptx.read(() -> this.appAssessmentInfoStore.getByAppKey(appKey));
    }

    public void updateAppAssessmentInfo(String appKey, AppAssessmentUpdateRequest request) {
        Objects.requireNonNull(request);
        String propName = request.getAppProperty();
        if (!AppAssessmentProperty.isSupported(propName)) {
            throw new IllegalArgumentException(String.format("Unrecognised property name [%s]", propName));
        }
        AppAssessmentProperty assessmentProperty = AppAssessmentProperty.getAppAssessmentPropertyByName(propName);
        this.ptx.write(() -> {
            if (this.appAssessmentInfoStore.getByAppKey(appKey).isPresent()) {
                this.appAssessmentInfoStore.updateProperty(appKey, assessmentProperty, this.resolveValueOrDefault(assessmentProperty, request.getValue(), Object.class));
            } else {
                this.appAssessmentInfoStore.create(this.toAppAssessmentInfo(appKey, request));
            }
        });
    }

    public void updateAllAppAssessmentInfo(UpdateAllAppAssessmentInfoRequest request) {
        Objects.requireNonNull(request);
        String propName = request.getAppProperty();
        if (!AppAssessmentProperty.isSupported(propName)) {
            throw new IllegalArgumentException(String.format("Unrecognised property name [%s]", propName));
        }
        List appInfos = this.ptx.read(this.appAssessmentInfoStore::getAll);
        Set installedPlugIns = this.pluginManager.getActualUserInstalledPlugins().stream().map(Plugin::getKey).collect(Collectors.toSet());
        List notAssessedApps = installedPlugIns.stream().filter(installedAppKey -> appInfos.stream().noneMatch(app -> app.getAppKey().equals(installedAppKey))).collect(Collectors.toList());
        AppAssessmentProperty assessmentProperty = AppAssessmentProperty.getAppAssessmentPropertyByName(propName);
        this.ptx.write(() -> {
            this.appAssessmentInfoStore.updatePropertyForAllApps(assessmentProperty, this.resolveValueOrDefault(assessmentProperty, request.getValue(), Object.class));
            for (String appKey : notAssessedApps) {
                this.appAssessmentInfoStore.create(this.toAppAssessmentInfo(appKey, new AppAssessmentUpdateRequest(appKey, request.getAppProperty(), request.getValue())));
            }
        });
    }

    private <T> T resolveValueOrDefault(AppAssessmentProperty assessmentProperty, Object value, Class<T> returnType) {
        T transformedValue = null;
        switch (assessmentProperty) {
            case MIGRATION_STATUS: {
                if (value instanceof String) {
                    transformedValue = returnType.cast((Object)AppAssessmentUserAttributedStatus.valueOf((String)value));
                    break;
                }
                if (value instanceof AppAssessmentUserAttributedStatus) {
                    transformedValue = returnType.cast(value);
                    break;
                }
                transformedValue = returnType.cast((Object)DEFAULT_ASSESSMENT_STATUS);
                break;
            }
            case MIGRATION_NOTES: 
            case ALTERNATIVE_APP_KEY: {
                transformedValue = returnType.cast(Optional.ofNullable(value).orElse(ASSESSMENT_PROPERTY_DEFAULT_STRING_VALUE));
                break;
            }
            case CONSENT_STATUS: {
                transformedValue = value instanceof String ? (T)returnType.cast((Object)AssessmentConsent.valueOf((String)value)) : (value instanceof AssessmentConsent ? (T)returnType.cast(value) : (T)returnType.cast((Object)DEFAULT_CONSENT_STATUS));
            }
        }
        return transformedValue;
    }

    private AppAssessmentInfo toAppAssessmentInfo(String appKey, AppAssessmentUpdateRequest request) {
        return new AppAssessmentInfo(appKey, this.resolveValueOrDefault(AppAssessmentProperty.MIGRATION_STATUS, AppAssessmentInfoService.maybeValue(request, AppAssessmentProperty.MIGRATION_STATUS), AppAssessmentUserAttributedStatus.class), this.resolveValueOrDefault(AppAssessmentProperty.MIGRATION_NOTES, AppAssessmentInfoService.maybeValue(request, AppAssessmentProperty.MIGRATION_NOTES), String.class), this.resolveValueOrDefault(AppAssessmentProperty.ALTERNATIVE_APP_KEY, AppAssessmentInfoService.maybeValue(request, AppAssessmentProperty.ALTERNATIVE_APP_KEY), String.class), this.resolveValueOrDefault(AppAssessmentProperty.CONSENT_STATUS, AppAssessmentInfoService.maybeValue(request, AppAssessmentProperty.CONSENT_STATUS), AssessmentConsent.class));
    }

    private static String maybeValue(AppAssessmentUpdateRequest request, AppAssessmentProperty propertyToCheck) {
        return request.getAppProperty().equals(propertyToCheck.getName()) ? request.getValue() : null;
    }
}

