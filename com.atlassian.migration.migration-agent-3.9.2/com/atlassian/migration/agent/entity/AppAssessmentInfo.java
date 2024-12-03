/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.Entity
 *  javax.persistence.EnumType
 *  javax.persistence.Enumerated
 *  javax.persistence.Id
 *  javax.persistence.Lob
 *  javax.persistence.Table
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.AppAssessmentUserAttributedStatus;
import com.atlassian.migration.agent.entity.AssessmentConsent;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Table(name="MIG_APP_ASSESSMENT_INFO")
@Entity
public class AppAssessmentInfo {
    @Id
    @Column(name="appkey", nullable=false, unique=true)
    private String appKey;
    @Column(name="assessmentstatus", nullable=false)
    @Enumerated(value=EnumType.STRING)
    private AppAssessmentUserAttributedStatus migrationStatus;
    @Column(name="notes")
    @Lob
    private String migrationNotes;
    @Column(name="alternativeApp")
    private String alternativeAppKey;
    @Column(name="consent", nullable=false)
    @Enumerated(value=EnumType.STRING)
    private AssessmentConsent consent;

    public AppAssessmentInfo() {
    }

    public AppAssessmentInfo(String appKey, AppAssessmentUserAttributedStatus migrationStatus, String migrationNotes, String alternativeAppKey, AssessmentConsent consent) {
        this.appKey = appKey;
        this.migrationStatus = migrationStatus;
        this.migrationNotes = migrationNotes;
        this.alternativeAppKey = alternativeAppKey;
        this.consent = consent;
    }

    public String getAppKey() {
        return this.appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public AppAssessmentUserAttributedStatus getMigrationStatus() {
        return this.migrationStatus;
    }

    public String getMigrationNotes() {
        return this.migrationNotes;
    }

    public String getAlternativeAppKey() {
        return this.alternativeAppKey;
    }

    public AssessmentConsent getConsent() {
        return this.consent;
    }

    public void setConsent(AssessmentConsent consent) {
        this.consent = consent;
    }

    public static AppAssessmentInfo empty(String appKey) {
        return new AppAssessmentInfo(appKey, AppAssessmentUserAttributedStatus.Unassigned, null, null, AssessmentConsent.NotGiven);
    }

    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals((Object)this, (Object)o, (String[])new String[0]);
    }

    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode((Object)this, (String[])new String[0]);
    }
}

