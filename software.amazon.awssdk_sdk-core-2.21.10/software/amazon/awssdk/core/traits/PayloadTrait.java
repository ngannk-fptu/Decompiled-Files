/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.core.traits;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.traits.Trait;

@SdkProtectedApi
public final class PayloadTrait
implements Trait {
    private PayloadTrait() {
    }

    public static PayloadTrait create() {
        return new PayloadTrait();
    }
}

