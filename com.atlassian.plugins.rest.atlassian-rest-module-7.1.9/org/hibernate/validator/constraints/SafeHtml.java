/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.Constraint
 *  javax.validation.Payload
 */
package org.hibernate.validator.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy={})
@Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=List.class)
@Deprecated
public @interface SafeHtml {
    public String message() default "{org.hibernate.validator.constraints.SafeHtml.message}";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

    public WhiteListType whitelistType() default WhiteListType.RELAXED;

    public String[] additionalTags() default {};

    public Tag[] additionalTagsWithAttributes() default {};

    public String baseURI() default "";

    public static enum WhiteListType {
        NONE,
        SIMPLE_TEXT,
        BASIC,
        BASIC_WITH_IMAGES,
        RELAXED;

    }

    @Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
    @Retention(value=RetentionPolicy.RUNTIME)
    @Documented
    public static @interface List {
        public SafeHtml[] value();
    }

    @Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
    @Retention(value=RetentionPolicy.RUNTIME)
    @Documented
    public static @interface Attribute {
        public String name();

        public String[] protocols();
    }

    @Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
    @Retention(value=RetentionPolicy.RUNTIME)
    @Documented
    public static @interface Tag {
        public String name();

        public String[] attributes() default {};

        public Attribute[] attributesWithProtocols() default {};
    }
}

