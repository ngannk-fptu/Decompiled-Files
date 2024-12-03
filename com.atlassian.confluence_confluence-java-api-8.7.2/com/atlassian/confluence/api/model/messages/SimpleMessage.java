/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.messages;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.messages.Message;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
@ExperimentalApi
public class SimpleMessage
implements Message<SimpleMessage> {
    @JsonProperty
    private final String key;
    @JsonIgnore
    private List<Object> args;
    @JsonProperty
    private final String translation;

    @JsonCreator
    private SimpleMessage() {
        this(SimpleMessage.builder());
    }

    @JsonProperty(value="args")
    private void setJsonArgs(List<String> args) {
        this.args = args == null ? Collections.emptyList() : Collections.unmodifiableList(args);
    }

    @JsonProperty(value="args")
    private List<String> getJsonArgs() {
        return this.args.stream().map(arg -> arg.toString()).collect(Collectors.toList());
    }

    private SimpleMessage(SimpleMessageBuilder builder) {
        this.translation = builder.translation;
        this.key = builder.key;
        this.args = builder.args != null ? Collections.unmodifiableList(Arrays.asList(builder.args)) : Collections.emptyList();
    }

    public static SimpleMessageBuilder builder() {
        return new SimpleMessageBuilder();
    }

    public static SimpleMessage withKeyAndArgs(String key, Object ... args) {
        return SimpleMessage.builder().key(key).args(args).build();
    }

    public static SimpleMessage withTranslation(String translation) {
        return SimpleMessage.builder().translation(translation).build();
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public Object[] getArgs() {
        return this.args.toArray();
    }

    @Override
    public String getTranslation() {
        return this.translation;
    }

    public static SimpleMessage copyOf(Message message) {
        return new SimpleMessageBuilder(message).build();
    }

    public String toString() {
        return "SimpleMessage{key='" + this.key + '\'' + ", args=" + this.args + ", translation='" + this.translation + '\'' + '}';
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        SimpleMessage message = (SimpleMessage)obj;
        return Objects.equals(this.key, message.key) && Objects.equals(this.args, message.args) && Objects.equals(this.translation, message.translation);
    }

    public int hashCode() {
        return Objects.hash(this.key, this.args, this.translation);
    }

    public static class SimpleMessageBuilder {
        private String translation;
        private String key;
        private Object[] args;

        private SimpleMessageBuilder() {
        }

        private SimpleMessageBuilder(Message message) {
            this.key = message.getKey();
            this.args = message.getArgs();
            this.translation = message.getTranslation();
        }

        public SimpleMessageBuilder key(String key) {
            this.key = key;
            return this;
        }

        public SimpleMessageBuilder args(Object ... args) {
            this.args = args;
            return this;
        }

        public SimpleMessageBuilder translation(String translation) {
            this.translation = translation;
            return this;
        }

        public SimpleMessage build() {
            return new SimpleMessage(this);
        }
    }
}

