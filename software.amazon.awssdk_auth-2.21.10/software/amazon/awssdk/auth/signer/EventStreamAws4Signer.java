/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.auth.signer;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.auth.signer.internal.BaseEventStreamAsyncAws4Signer;

@SdkProtectedApi
public final class EventStreamAws4Signer
extends BaseEventStreamAsyncAws4Signer {
    private EventStreamAws4Signer() {
    }

    public static EventStreamAws4Signer create() {
        return new EventStreamAws4Signer();
    }
}

