/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.checksums;

import software.amazon.awssdk.annotations.SdkPublicApi;

@SdkPublicApi
public enum ChecksumValidation {
    VALIDATED,
    FORCE_SKIP,
    CHECKSUM_ALGORITHM_NOT_FOUND,
    CHECKSUM_RESPONSE_NOT_FOUND;

}

