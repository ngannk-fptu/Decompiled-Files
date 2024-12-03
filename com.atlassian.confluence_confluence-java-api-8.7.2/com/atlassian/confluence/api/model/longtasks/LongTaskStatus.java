/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 */
package com.atlassian.confluence.api.model.longtasks;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.longtasks.LongTaskId;
import com.atlassian.confluence.api.model.messages.Message;
import com.atlassian.confluence.api.model.messages.SimpleMessage;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

@ExperimentalApi
@RestEnrichable
@JsonIgnoreProperties(ignoreUnknown=true)
public class LongTaskStatus
implements Comparable<LongTaskStatus> {
    @JsonProperty
    private final LongTaskId id;
    @JsonProperty
    @JsonDeserialize(as=SimpleMessage.class)
    private final Message name;
    @JsonProperty
    private final long elapsedTime;
    @JsonProperty
    private final int percentageComplete;
    @JsonProperty
    private final boolean successful;
    @JsonProperty
    @JsonDeserialize(contentAs=SimpleMessage.class)
    private final List<Message> messages;

    public LongTaskId getId() {
        return this.id;
    }

    public long getElapsedTime() {
        return this.elapsedTime;
    }

    public Message getName() {
        return this.name;
    }

    public int getPercentageComplete() {
        return this.percentageComplete;
    }

    public boolean isSuccessful() {
        return this.successful;
    }

    public List<Message> getMessages() {
        return this.messages;
    }

    @JsonCreator
    private LongTaskStatus() {
        this(new LongTaskBuilder());
    }

    public static LongTaskBuilder builder(LongTaskId id) {
        return new LongTaskBuilder().id(id);
    }

    private LongTaskStatus(LongTaskBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.elapsedTime = builder.elapsedTime;
        this.percentageComplete = builder.percentageComplete;
        this.successful = builder.successful;
        this.messages = Collections.unmodifiableList(builder.messages);
    }

    public boolean equals(Object other) {
        if (other instanceof LongTaskStatus) {
            return Objects.equals(this.id, ((LongTaskStatus)other).id);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public int compareTo(LongTaskStatus longTaskStatus) {
        if (longTaskStatus == null) {
            return -1;
        }
        if (this.getName() == null && longTaskStatus.getName() == null) {
            return -1;
        }
        if (this.getName() == null && longTaskStatus.getName() != null) {
            return 1;
        }
        if (this.getName() != null && longTaskStatus.getName() == null) {
            return -1;
        }
        return this.getName().compareTo(longTaskStatus.getName());
    }

    public static class LongTaskBuilder {
        private LongTaskId id;
        private Message name;
        private long elapsedTime;
        private int percentageComplete;
        private boolean successful;
        private List<Message> messages = new ArrayList<Message>();

        public LongTaskBuilder id(LongTaskId id) {
            this.id = id;
            return this;
        }

        public LongTaskBuilder name(Message name) {
            this.name = name;
            return this;
        }

        public LongTaskBuilder elapsedTime(long elapsedTime) {
            this.elapsedTime = elapsedTime;
            return this;
        }

        public LongTaskBuilder percentageComplete(int percentageComplete) {
            this.percentageComplete = percentageComplete;
            return this;
        }

        public LongTaskBuilder successful(boolean successful) {
            this.successful = successful;
            return this;
        }

        public LongTaskBuilder addMessage(Message message) {
            this.messages.add(message);
            return this;
        }

        public LongTaskStatus build() {
            return new LongTaskStatus(this);
        }
    }
}

