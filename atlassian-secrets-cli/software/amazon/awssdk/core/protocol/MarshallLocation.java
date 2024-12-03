/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.protocol;

import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public enum MarshallLocation {
    PAYLOAD,
    QUERY_PARAM,
    HEADER,
    PATH,
    GREEDY_PATH,
    STATUS_CODE;

}

