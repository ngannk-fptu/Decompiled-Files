/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.codehaus.jackson.annotate.JacksonAnnotation;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.KeyDeserializer;
import org.codehaus.jackson.map.annotate.NoClass;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(value=RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonDeserialize {
    public Class<? extends JsonDeserializer<?>> using() default JsonDeserializer.None.class;

    public Class<? extends JsonDeserializer<?>> contentUsing() default JsonDeserializer.None.class;

    public Class<? extends KeyDeserializer> keyUsing() default KeyDeserializer.None.class;

    public Class<?> as() default NoClass.class;

    public Class<?> keyAs() default NoClass.class;

    public Class<?> contentAs() default NoClass.class;
}

