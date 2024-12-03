/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.json.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.protocol.MarshallLocation;

@SdkInternalApi
public final class MarshallerUtil {
    private MarshallerUtil() {
    }

    public static boolean isInUri(MarshallLocation location) {
        switch (location) {
            case PATH: 
            case QUERY_PARAM: {
                return true;
            }
        }
        return false;
    }
}

