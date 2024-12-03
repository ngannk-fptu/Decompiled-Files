/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.longtasks;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.longtasks.LongTaskId;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
public class LongTaskSubmission {
    private static final String RESULT_PATH_KEY = "result";
    private static final String STATUS_PATH_KEY = "status";
    @JsonProperty
    private final LongTaskId id;
    @JsonProperty
    private final Map<String, String> links;

    public static LongTaskSubmissionBuilder builder() {
        return new LongTaskSubmissionBuilder();
    }

    @JsonCreator
    private LongTaskSubmission() {
        this(LongTaskSubmission.builder());
    }

    private LongTaskSubmission(LongTaskSubmissionBuilder builder) {
        this.id = builder.id;
        this.links = Collections.unmodifiableMap(builder.links);
    }

    public LongTaskId getId() {
        return this.id;
    }

    public String getResultPath() {
        return this.links.get(RESULT_PATH_KEY);
    }

    public String getStatusPath() {
        return this.links.get(STATUS_PATH_KEY);
    }

    public String getLink(String key) {
        return this.links.get(key);
    }

    public static class LongTaskSubmissionBuilder {
        private LongTaskId id;
        private final Map<String, String> links = new HashMap<String, String>();

        public LongTaskSubmissionBuilder id(LongTaskId id) {
            this.id = id;
            return this;
        }

        public LongTaskSubmissionBuilder result(String resultPath) {
            this.links.put(LongTaskSubmission.RESULT_PATH_KEY, resultPath);
            return this;
        }

        public LongTaskSubmissionBuilder status(String statusPath) {
            this.links.put(LongTaskSubmission.STATUS_PATH_KEY, statusPath);
            return this;
        }

        public LongTaskSubmission build() {
            return new LongTaskSubmission(this);
        }
    }
}

