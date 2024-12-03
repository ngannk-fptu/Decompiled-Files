/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.DateUtils
 */
package software.amazon.awssdk.http.auth.aws.internal.signer.util;

import java.time.Instant;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.utils.DateUtils;

@Immutable
@SdkInternalApi
public final class SignerKey {
    private final long daysSinceEpoch;
    private final byte[] signingKey;

    public SignerKey(Instant date, byte[] signingKey) {
        if (date == null) {
            throw new IllegalArgumentException("Not able to cache signing key. Signing date to be is null");
        }
        if (signingKey == null) {
            throw new IllegalArgumentException("Not able to cache signing key. Signing Key to be cached are null");
        }
        this.daysSinceEpoch = DateUtils.numberOfDaysSinceEpoch((long)date.toEpochMilli());
        this.signingKey = (byte[])signingKey.clone();
    }

    public boolean isValidForDate(Instant other) {
        return this.daysSinceEpoch == DateUtils.numberOfDaysSinceEpoch((long)other.toEpochMilli());
    }

    public byte[] getSigningKey() {
        return (byte[])this.signingKey.clone();
    }
}

