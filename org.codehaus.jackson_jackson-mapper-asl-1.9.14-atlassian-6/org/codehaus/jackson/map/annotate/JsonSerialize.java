/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JacksonAnnotation
 */
package org.codehaus.jackson.map.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.codehaus.jackson.annotate.JacksonAnnotation;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.annotate.NoClass;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(value=RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonSerialize {
    public Class<? extends JsonSerializer<?>> using() default JsonSerializer.None.class;

    public Class<? extends JsonSerializer<?>> contentUsing() default JsonSerializer.None.class;

    public Class<? extends JsonSerializer<?>> keyUsing() default JsonSerializer.None.class;

    public Class<?> as() default NoClass.class;

    public Class<?> keyAs() default NoClass.class;

    public Class<?> contentAs() default NoClass.class;

    public Typing typing() default Typing.DYNAMIC;

    public Inclusion include() default Inclusion.ALWAYS;

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Typing {
        DYNAMIC,
        STATIC;

    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Inclusion {
        ALWAYS,
        NON_NULL,
        NON_DEFAULT,
        NON_EMPTY;

    }
}

