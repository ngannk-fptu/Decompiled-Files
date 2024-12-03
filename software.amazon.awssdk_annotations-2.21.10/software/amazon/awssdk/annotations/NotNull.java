/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import software.amazon.awssdk.annotations.SdkProtectedApi;

@Documented
@Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(value=RetentionPolicy.CLASS)
@SdkProtectedApi
public @interface NotNull {
}

