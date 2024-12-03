/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.expiry;

import java.io.Serializable;
import javax.cache.configuration.Factory;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;

public final class EternalExpiryPolicy
implements ExpiryPolicy,
Serializable {
    public static final long serialVersionUID = 201305101603L;

    public static Factory<ExpiryPolicy> factoryOf() {
        return new FactoryBuilder.SingletonFactory<ExpiryPolicy>(new EternalExpiryPolicy());
    }

    @Override
    public Duration getExpiryForCreation() {
        return Duration.ETERNAL;
    }

    @Override
    public Duration getExpiryForAccess() {
        return null;
    }

    @Override
    public Duration getExpiryForUpdate() {
        return null;
    }

    public int hashCode() {
        return EternalExpiryPolicy.class.hashCode();
    }

    public boolean equals(Object other) {
        return other instanceof EternalExpiryPolicy;
    }
}

