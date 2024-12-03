/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.audit.frontend.data;

import com.atlassian.audit.frontend.data.AuditingOnboardingDisplayInfoData;
import java.util.List;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AuditOnboardingData {
    @JsonProperty(value="onboardingInfoList")
    final List<AuditingOnboardingDisplayInfoData> onboardingInfoList;

    public AuditOnboardingData(List<AuditingOnboardingDisplayInfoData> onboardingInfoList) {
        this.onboardingInfoList = onboardingInfoList;
    }

    public List<AuditingOnboardingDisplayInfoData> getOnboardingInfoList() {
        return this.onboardingInfoList;
    }
}

