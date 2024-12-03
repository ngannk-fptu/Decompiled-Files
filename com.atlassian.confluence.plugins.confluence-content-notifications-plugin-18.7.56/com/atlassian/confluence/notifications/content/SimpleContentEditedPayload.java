/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.notifications.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.notifications.content.ContentEditedPayload;
import com.atlassian.confluence.notifications.content.SimpleContentIdPayload;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
public class SimpleContentEditedPayload
extends SimpleContentIdPayload
implements ContentEditedPayload {
    private final long originalId;
    private final String inlineContext;

    @JsonCreator
    public SimpleContentEditedPayload(@JsonProperty(value="contentType") ContentType contentType, @JsonProperty(value="contentId") long contentId, @JsonProperty(value="originalId") long originalId, @JsonProperty(value="originatingUserKey") String originatingUserKey, @JsonProperty(value="inlineContext") String inlineContext) {
        super(contentType, contentId, originatingUserKey);
        this.originalId = originalId;
        this.inlineContext = inlineContext;
    }

    @Override
    public long getOriginalId() {
        return this.originalId;
    }

    @Override
    public Maybe<String> getInlineContext() {
        return Option.option((Object)this.inlineContext);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("SimpleContentEditedPayload{");
        sb.append("originalId=").append(this.originalId);
        sb.append(", inlineContext=").append(this.inlineContext);
        sb.append("; ").append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}

