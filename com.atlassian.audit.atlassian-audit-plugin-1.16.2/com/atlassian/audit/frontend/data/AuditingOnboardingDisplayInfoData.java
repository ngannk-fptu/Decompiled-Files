/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.audit.frontend.data;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AuditingOnboardingDisplayInfoData {
    @JsonIgnore
    private final boolean dcOnly;
    @JsonProperty(value="title")
    private final String title;
    @JsonProperty(value="description")
    private final String description;
    @JsonProperty(value="image")
    private final String image;
    @JsonProperty(value="articleLink")
    private final String articleLink;
    @JsonProperty(value="buttonLabels")
    private final ButtonLabels buttonLabels;

    public AuditingOnboardingDisplayInfoData(boolean dcOnly, String title, String description, String image, String articleLink, String confirmButtonLabel, String learnMoreButtonLabel) {
        this.dcOnly = dcOnly;
        this.title = title;
        this.description = description;
        this.image = image;
        this.articleLink = articleLink;
        this.buttonLabels = new ButtonLabels(learnMoreButtonLabel, confirmButtonLabel);
    }

    public boolean isDcOnly() {
        return this.dcOnly;
    }

    public class ButtonLabels {
        @JsonProperty(value="learnMore")
        private String learnMore;
        @JsonProperty(value="confirm")
        private String confirm;

        public ButtonLabels(String learnMore, String confirm) {
            this.learnMore = learnMore;
            this.confirm = confirm;
        }
    }
}

