/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.SOURCE)
@Target(value={ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
public @interface GrabConfig {
    public boolean systemClassLoader() default false;

    public String[] systemProperties() default {""};

    public boolean initContextClassLoader() default false;

    public boolean autoDownload() default true;

    public boolean disableChecksums() default false;
}

