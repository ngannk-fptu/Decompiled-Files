/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.signer;

import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public enum SigningMethod {
    PROTOCOL_STREAMING_SIGNING_AUTH,
    UNSIGNED_PAYLOAD,
    PROTOCOL_BASED_UNSIGNED,
    HEADER_BASED_AUTH;

}

