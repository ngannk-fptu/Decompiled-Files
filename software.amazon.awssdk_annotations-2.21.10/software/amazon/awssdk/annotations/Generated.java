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
@Retention(value=RetentionPolicy.SOURCE)
@Target(value={ElementType.PACKAGE, ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.PARAMETER})
@SdkProtectedApi
public @interface Generated {
    public String[] value();

    public String date() default "";

    public String comments() default "";
}

