/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.traits;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.traits.Trait;

@SdkProtectedApi
public final class XmlAttributeTrait
implements Trait {
    private XmlAttributeTrait() {
    }

    public static XmlAttributeTrait create() {
        return new XmlAttributeTrait();
    }
}

