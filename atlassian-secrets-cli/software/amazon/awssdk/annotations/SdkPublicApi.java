/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import software.amazon.awssdk.annotations.SdkProtectedApi;

@Target(value={ElementType.PACKAGE, ElementType.TYPE, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.METHOD})
@SdkProtectedApi
public @interface SdkPublicApi {
}

