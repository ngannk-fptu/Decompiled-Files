/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
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

