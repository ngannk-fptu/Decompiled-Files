/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.sal.api.user.UserKey
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.notifications.batch.content;

import com.atlassian.confluence.notifications.batch.content.BatchingPayload;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.sal.api.user.UserKey;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.Set;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class SimpleBatchingPayload
implements BatchingPayload {
    private Set<UserKey> originators;
    private String batchingId;
    private String contentType;
    private LinkedHashMap<ModuleCompleteKey, Object> payloads;

    @JsonCreator
    public SimpleBatchingPayload(@JsonProperty(value="randomOriginatorUserKey") Set<UserKey> originators, @JsonProperty(value="batchingId") String batchingId, @JsonProperty(value="contentType") String contentType, @JsonProperty(value="payloads") LinkedHashMap<ModuleCompleteKey, Object> payloads) {
        this.originators = originators;
        this.batchingId = batchingId;
        this.contentType = contentType;
        this.payloads = payloads;
    }

    @Override
    public Set<UserKey> getOriginators() {
        return this.originators;
    }

    @Override
    public String getBatchingId() {
        return this.batchingId;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public LinkedHashMap<ModuleCompleteKey, Object> getPayloads() {
        return this.payloads;
    }

    public Maybe<String> getOriginatingUserKey() {
        return Option.none();
    }

    public Optional<UserKey> getOriginatorUserKey() {
        return this.originators.stream().findAny();
    }
}

