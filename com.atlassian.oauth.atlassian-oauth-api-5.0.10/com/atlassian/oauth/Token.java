/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth;

import com.atlassian.oauth.Consumer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class Token {
    private final Type type;
    private final String token;
    private final String tokenSecret;
    private final Consumer consumer;
    private final Map<String, String> properties;

    protected Token(TokenBuilder<?, ?> builder) {
        this.type = Objects.requireNonNull(((TokenBuilder)builder).type, "type");
        this.token = Objects.requireNonNull(((TokenBuilder)builder).token, "token");
        this.tokenSecret = Objects.requireNonNull(((TokenBuilder)builder).tokenSecret, "tokenSecret");
        this.consumer = Objects.requireNonNull(((TokenBuilder)builder).consumer, "consumer");
        this.properties = Collections.unmodifiableMap(new HashMap(((TokenBuilder)builder).properties));
    }

    public final String getToken() {
        return this.token;
    }

    public final String getTokenSecret() {
        return this.tokenSecret;
    }

    public final Consumer getConsumer() {
        return this.consumer;
    }

    public final boolean isRequestToken() {
        return this.type == Type.REQUEST;
    }

    public final boolean isAccessToken() {
        return this.type == Type.ACCESS;
    }

    public final boolean hasProperty(String property) {
        return this.properties.containsKey(property);
    }

    public final String getProperty(String property) {
        return this.properties.get(property);
    }

    public final Iterable<String> getPropertyNames() {
        return this.properties.keySet();
    }

    public final Map<String, String> getProperties() {
        return this.properties;
    }

    public String toString() {
        return this.token;
    }

    protected static enum Type {
        REQUEST,
        ACCESS;

    }

    public static abstract class TokenBuilder<T, B extends TokenBuilder<T, B>> {
        private final Type type;
        private final String token;
        private String tokenSecret;
        private Consumer consumer;
        private Map<String, String> properties = new HashMap<String, String>();

        public TokenBuilder(Type type, String token) {
            this.type = type;
            this.token = token;
        }

        public final B tokenSecret(String tokenSecret) {
            this.tokenSecret = Objects.requireNonNull(tokenSecret, "tokenSecret");
            return (B)this;
        }

        public final B consumer(Consumer consumer) {
            this.consumer = Objects.requireNonNull(consumer, "consumer");
            return (B)this;
        }

        public final B properties(Map<String, String> properties) {
            if (properties != null) {
                this.properties = properties;
            }
            return (B)this;
        }

        public abstract T build();
    }
}

