/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.rest;

import com.atlassian.migration.agent.dto.CloudType;
import java.net.URI;
import java.util.Arrays;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class GenerateCloudSiteSetupUrlDto {
    public static final String DEFAULT_REDIRECT = RedirectTo.planConfiguration.name();
    @JsonProperty
    public final URI returnUrl;
    @JsonProperty
    public final String redirectTo;
    @JsonProperty
    public final String planId;
    public final CloudType cloudType;

    @JsonCreator
    public GenerateCloudSiteSetupUrlDto(@JsonProperty(value="returnUrl") URI returnUrl, @JsonProperty(value="redirectTo") String redirectTo, @JsonProperty(value="planId") String planId, @JsonProperty(value="cloudType") @Nullable String cloudType) {
        this.returnUrl = returnUrl;
        this.redirectTo = this.validate(redirectTo);
        this.planId = planId;
        this.cloudType = cloudType != null ? CloudType.valueOf(cloudType) : CloudType.STANDARD;
    }

    private String validate(String redirectTo) {
        return Arrays.stream(RedirectTo.values()).map(Enum::name).filter(name -> name.equals(redirectTo)).findFirst().orElseThrow(() -> new IllegalArgumentException("Incorrect redirectTo value: " + redirectTo));
    }

    private static enum RedirectTo {
        planConfiguration,
        appAssessment,
        checkForErrors,
        migrationErrors,
        userAssessmentConnect,
        userAssessmentResults,
        userAssessmentReview,
        chooseDomains;

    }
}

