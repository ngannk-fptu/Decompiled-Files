/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.Consumer
 *  com.atlassian.oauth.Consumer$InstanceBuilder
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.common.auth.oauth.util;

import com.atlassian.oauth.Consumer;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class Consumers {
    private Consumers() {
        throw new AssertionError((Object)("Do not instantiate " + this.getClass().getSimpleName()));
    }

    public static Consumer.InstanceBuilder consumerBuilder(@Nonnull Consumer consumer) {
        Objects.requireNonNull(consumer, "consumer");
        Consumer.InstanceBuilder builder = new Consumer.InstanceBuilder(consumer.getKey()).name(consumer.getName()).description(consumer.getDescription()).signatureMethod(consumer.getSignatureMethod()).callback(consumer.getCallback()).twoLOAllowed(consumer.getTwoLOAllowed()).executingTwoLOUser(consumer.getExecutingTwoLOUser()).twoLOImpersonationAllowed(consumer.getTwoLOImpersonationAllowed());
        if (consumer.getPublicKey() != null) {
            builder.publicKey(consumer.getPublicKey());
        }
        return builder;
    }
}

