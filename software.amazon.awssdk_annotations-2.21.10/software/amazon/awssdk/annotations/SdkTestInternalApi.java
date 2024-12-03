/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import software.amazon.awssdk.annotations.SdkProtectedApi;

@Target(value={ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@SdkProtectedApi
public @interface SdkTestInternalApi {
}

