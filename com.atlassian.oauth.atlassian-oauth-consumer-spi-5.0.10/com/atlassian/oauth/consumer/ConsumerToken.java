/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.Token
 *  com.atlassian.oauth.Token$TokenBuilder
 *  com.atlassian.oauth.Token$Type
 *  net.jcip.annotations.Immutable
 */
package com.atlassian.oauth.consumer;

import com.atlassian.oauth.Token;
import java.util.Objects;
import net.jcip.annotations.Immutable;

@Immutable
public final class ConsumerToken
extends Token {
    private ConsumerToken(ConsumerTokenBuilder builder) {
        super((Token.TokenBuilder)builder);
    }

    public static ConsumerTokenBuilder newRequestToken(String token) {
        return new ConsumerTokenBuilder(Token.Type.REQUEST, Objects.requireNonNull(token, "token"));
    }

    public static ConsumerTokenBuilder newAccessToken(String token) {
        return new ConsumerTokenBuilder(Token.Type.ACCESS, Objects.requireNonNull(token, "token"));
    }

    public static final class ConsumerTokenBuilder
    extends Token.TokenBuilder<ConsumerToken, ConsumerTokenBuilder> {
        public ConsumerTokenBuilder(Token.Type type, String token) {
            super(type, token);
        }

        public ConsumerToken build() {
            return new ConsumerToken(this);
        }
    }
}

