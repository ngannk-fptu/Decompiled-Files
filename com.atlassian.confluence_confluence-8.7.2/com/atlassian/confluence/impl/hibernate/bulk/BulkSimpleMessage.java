/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.messages.Message
 *  com.google.common.collect.ImmutableList
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 *  org.codehaus.jackson.map.ser.std.ToStringSerializer
 */
package com.atlassian.confluence.impl.hibernate.bulk;

import com.atlassian.confluence.api.model.messages.Message;
import com.atlassian.confluence.impl.hibernate.bulk.BulkSimpleMessageTypes;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.std.ToStringSerializer;

public class BulkSimpleMessage
implements Message<BulkSimpleMessage> {
    @JsonProperty
    private final String key;
    @JsonProperty
    private final BulkSimpleMessageTypes type;
    @JsonDeserialize(contentAs=String.class)
    @JsonSerialize(contentUsing=ToStringSerializer.class)
    @JsonProperty
    private final List<Object> args;
    @JsonProperty
    private final String translation;

    @JsonCreator
    private BulkSimpleMessage() {
        this(BulkSimpleMessage.builder());
    }

    private BulkSimpleMessage(BulkSimpleMessageBuilder builder) {
        this.translation = builder.translation;
        this.key = builder.key;
        this.type = builder.type;
        this.args = builder.args != null ? ImmutableList.copyOf((Object[])builder.args) : ImmutableList.of();
    }

    public static BulkSimpleMessageBuilder builder() {
        return new BulkSimpleMessageBuilder();
    }

    public static BulkSimpleMessage withKeyAndArgs(String key, Object ... args) {
        return BulkSimpleMessage.builder().type(BulkSimpleMessageTypes.INFO).key(key).args(args).build();
    }

    public static BulkSimpleMessage withKeyAndArgs(BulkSimpleMessageTypes type, String key, Object ... args) {
        return BulkSimpleMessage.builder().type(type).key(key).args(args).build();
    }

    public static BulkSimpleMessage withTranslation(String translation) {
        return BulkSimpleMessage.builder().translation(translation).build();
    }

    public String getKey() {
        return this.key;
    }

    public Object[] getArgs() {
        return this.args.toArray();
    }

    public String getTranslation() {
        return this.translation;
    }

    public String toString() {
        return "SimpleMessage{key='" + this.key + "'type='" + this.type.name() + "', args=" + this.args + ", translation='" + this.translation + "'}";
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        BulkSimpleMessage message = (BulkSimpleMessage)obj;
        return Objects.equals(this.key, message.key) && Objects.equals(this.args, message.args) && Objects.equals(this.translation, message.translation);
    }

    public int hashCode() {
        return Objects.hash(this.key, this.args, this.translation);
    }

    public static class BulkSimpleMessageBuilder {
        private String translation;
        private String key;
        private BulkSimpleMessageTypes type;
        private Object[] args;

        private BulkSimpleMessageBuilder() {
        }

        public BulkSimpleMessageBuilder key(String key) {
            this.key = key;
            return this;
        }

        public BulkSimpleMessageBuilder type(BulkSimpleMessageTypes type) {
            this.type = type;
            return this;
        }

        public BulkSimpleMessageBuilder args(Object ... args) {
            this.args = args;
            return this;
        }

        public BulkSimpleMessageBuilder translation(String translation) {
            this.translation = translation;
            return this;
        }

        public BulkSimpleMessage build() {
            return new BulkSimpleMessage(this);
        }
    }
}

