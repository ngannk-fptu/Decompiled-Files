/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.mywork.service;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ActionResult {
    public static final ActionResult SUCCESS = new ActionResult(true, "", null);
    public static final ActionResult FAILED = new ActionResult(false, "", null);
    private final boolean successful;
    private final String resultUrl;
    private final String messageKey;

    public ActionResult(boolean successful, String resultUrl) {
        this.successful = successful;
        this.resultUrl = resultUrl;
        this.messageKey = null;
    }

    @JsonCreator
    public ActionResult(@JsonProperty(value="successful") boolean successful, @JsonProperty(value="resultUrl") String resultUrl, @JsonProperty(value="messageKey") String messageKey) {
        this.successful = successful;
        this.resultUrl = resultUrl;
        this.messageKey = messageKey;
    }

    @JsonProperty
    public boolean isSuccessful() {
        return this.successful;
    }

    @JsonProperty
    public String getResultUrl() {
        return this.resultUrl;
    }

    @JsonProperty
    public String getMessageKey() {
        return this.messageKey;
    }
}

