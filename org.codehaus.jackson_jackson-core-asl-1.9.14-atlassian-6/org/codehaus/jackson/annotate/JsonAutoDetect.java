/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import org.codehaus.jackson.annotate.JacksonAnnotation;
import org.codehaus.jackson.annotate.JsonMethod;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonAutoDetect {
    public JsonMethod[] value() default {JsonMethod.ALL};

    public Visibility getterVisibility() default Visibility.DEFAULT;

    public Visibility isGetterVisibility() default Visibility.DEFAULT;

    public Visibility setterVisibility() default Visibility.DEFAULT;

    public Visibility creatorVisibility() default Visibility.DEFAULT;

    public Visibility fieldVisibility() default Visibility.DEFAULT;

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Visibility {
        ANY,
        NON_PRIVATE,
        PROTECTED_AND_PUBLIC,
        PUBLIC_ONLY,
        NONE,
        DEFAULT;


        public boolean isVisible(Member m) {
            switch (this) {
                case ANY: {
                    return true;
                }
                case NONE: {
                    return false;
                }
                case NON_PRIVATE: {
                    return !Modifier.isPrivate(m.getModifiers());
                }
                case PROTECTED_AND_PUBLIC: {
                    if (Modifier.isProtected(m.getModifiers())) {
                        return true;
                    }
                }
                case PUBLIC_ONLY: {
                    return Modifier.isPublic(m.getModifiers());
                }
            }
            return false;
        }
    }
}

