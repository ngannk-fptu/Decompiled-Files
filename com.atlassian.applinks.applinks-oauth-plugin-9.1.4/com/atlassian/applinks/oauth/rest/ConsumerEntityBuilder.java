/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.core.rest.model.ConsumerEntity
 *  com.atlassian.oauth.Consumer
 *  com.atlassian.oauth.util.RSAKeys
 *  com.atlassian.plugins.rest.common.Link
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.oauth.rest;

import com.atlassian.applinks.core.rest.model.ConsumerEntity;
import com.atlassian.oauth.Consumer;
import com.atlassian.oauth.util.RSAKeys;
import com.atlassian.plugins.rest.common.Link;
import java.net.URI;
import java.security.Key;
import javax.annotation.Nonnull;

public class ConsumerEntityBuilder {
    private Consumer consumer;
    private Link self;

    public ConsumerEntityBuilder(Consumer consumer) {
        this.consumer = consumer;
    }

    @Nonnull
    public static ConsumerEntityBuilder consumer(Consumer consumer) {
        return new ConsumerEntityBuilder(consumer);
    }

    @Nonnull
    public ConsumerEntityBuilder self(@Nonnull URI selfUri) {
        this.self = Link.self((URI)selfUri);
        return this;
    }

    @Nonnull
    public ConsumerEntity build() {
        String publicKey = this.consumer.getPublicKey() != null ? RSAKeys.toPemEncoding((Key)this.consumer.getPublicKey()) : null;
        return new ConsumerEntity(this.self, this.consumer.getKey(), this.consumer.getName(), this.consumer.getDescription(), this.consumer.getSignatureMethod().name(), publicKey, this.consumer.getCallback(), this.consumer.getTwoLOAllowed(), this.consumer.getExecutingTwoLOUser(), this.consumer.getTwoLOImpersonationAllowed());
    }
}

