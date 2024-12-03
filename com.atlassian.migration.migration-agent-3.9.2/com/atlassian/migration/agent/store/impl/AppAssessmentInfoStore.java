/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.impl;

import com.atlassian.migration.agent.entity.AppAssessmentInfo;
import com.atlassian.migration.agent.entity.AppAssessmentProperty;
import com.atlassian.migration.agent.entity.AppAssessmentUserAttributedStatus;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.List;
import java.util.Optional;

public class AppAssessmentInfoStore {
    private final EntityManagerTemplate tmpl;

    public AppAssessmentInfoStore(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    public Optional<AppAssessmentInfo> getByAppKey(String appKey) {
        return this.tmpl.query(AppAssessmentInfo.class, "select s from AppAssessmentInfo s where s.appKey = :appKey").param("appKey", (Object)appKey).first();
    }

    public void create(AppAssessmentInfo appInfo) {
        this.tmpl.persist(appInfo);
    }

    public void updateProperty(String appKey, AppAssessmentProperty assessmentProperty, Object value) {
        String query;
        switch (assessmentProperty) {
            case MIGRATION_NOTES: {
                query = "update AppAssessmentInfo info set migrationNotes = :propValue where info.appKey = :appKey";
                break;
            }
            case MIGRATION_STATUS: {
                query = "update AppAssessmentInfo info set migrationStatus = :propValue where info.appKey = :appKey";
                break;
            }
            case ALTERNATIVE_APP_KEY: {
                query = "update AppAssessmentInfo info set alternativeAppKey = :propValue where info.appKey = :appKey";
                break;
            }
            case CONSENT_STATUS: {
                query = "update AppAssessmentInfo info set consent = :propValue where info.appKey = :appKey";
                break;
            }
            default: {
                throw new IllegalArgumentException(String.format("Unrecognised property name [%s]", new Object[]{assessmentProperty}));
            }
        }
        this.tmpl.query(query).param("propValue", value).param("appKey", (Object)appKey).update();
    }

    public void updatePropertyForAllApps(AppAssessmentProperty assessmentProperty, Object value) {
        String query;
        switch (assessmentProperty) {
            case MIGRATION_NOTES: {
                query = "update AppAssessmentInfo info set migrationNotes = :propValue";
                break;
            }
            case MIGRATION_STATUS: {
                query = "update AppAssessmentInfo info set migrationStatus = :propValue";
                break;
            }
            case ALTERNATIVE_APP_KEY: {
                query = "update AppAssessmentInfo info set alternativeAppKey = :propValue";
                break;
            }
            case CONSENT_STATUS: {
                query = "update AppAssessmentInfo info set consent = :propValue";
                break;
            }
            default: {
                throw new IllegalArgumentException(String.format("Unrecognised property name [%s]", new Object[]{assessmentProperty}));
            }
        }
        this.tmpl.query(query).param("propValue", value).update();
    }

    public List<AppAssessmentInfo> getAll() {
        return this.tmpl.query(AppAssessmentInfo.class, "select info from AppAssessmentInfo info").list();
    }

    public List<AppAssessmentInfo> getAppsNeededInCloud() {
        return this.tmpl.query(AppAssessmentInfo.class, "select s from AppAssessmentInfo s where s.migrationStatus=:status").param("status", (Object)AppAssessmentUserAttributedStatus.Needed).list();
    }
}

