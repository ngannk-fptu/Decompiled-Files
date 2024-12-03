/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.identity.spi;

import java.time.Instant;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;

@SdkPublicApi
@ThreadSafe
public interface Identity {
    default public Optional<Instant> expirationTime() {
        return Optional.empty();
    }
}

