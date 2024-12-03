/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 */
package software.amazon.awssdk.core.waiters;

import software.amazon.awssdk.annotations.SdkPublicApi;

@SdkPublicApi
public enum WaiterState {
    SUCCESS,
    FAILURE,
    RETRY;

}

