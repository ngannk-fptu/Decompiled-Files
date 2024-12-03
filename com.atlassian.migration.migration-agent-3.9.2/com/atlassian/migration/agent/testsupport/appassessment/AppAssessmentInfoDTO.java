/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.testsupport.appassessment;

import org.codehaus.jackson.annotate.JsonProperty;

public class AppAssessmentInfoDTO {
    public final String appKey;
    public final String migrationStatus;
    public final String migrationNotes;
    public final String alternativeAppKey;

    public AppAssessmentInfoDTO(@JsonProperty(value="appKey") String appKey, @JsonProperty(value="migrationStatus") String migrationStatus, @JsonProperty(value="migrationNotes") String migrationNotes, @JsonProperty(value="alternativeAppKey") String alternativeAppKey) {
        this.appKey = appKey;
        this.migrationStatus = migrationStatus;
        this.migrationNotes = migrationNotes;
        this.alternativeAppKey = alternativeAppKey;
    }
}

