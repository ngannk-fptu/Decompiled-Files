/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.expiry;

import java.io.Serializable;
import javax.cache.configuration.Factory;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;

public final class CreatedExpiryPolicy
implements ExpiryPolicy,
Serializable {
    public static final long serialVersionUID = 201305291023L;
    private Duration expiryDuration;

    public CreatedExpiryPolicy(Duration expiryDuration) {
        this.expiryDuration = expiryDuration;
    }

    public static Factory<ExpiryPolicy> factoryOf(Duration duration) {
        return new FactoryBuilder.SingletonFactory<ExpiryPolicy>(new CreatedExpiryPolicy(duration));
    }

    @Override
    public Duration getExpiryForCreation() {
        return this.expiryDuration;
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
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.expiryDuration == null ? 0 : this.expiryDuration.hashCode());
        return result;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (!(object instanceof CreatedExpiryPolicy)) {
            return false;
        }
        CreatedExpiryPolicy other = (CreatedExpiryPolicy)object;
        return !(this.expiryDuration == null ? other.expiryDuration != null : !this.expiryDuration.equals(other.expiryDuration));
    }
}

