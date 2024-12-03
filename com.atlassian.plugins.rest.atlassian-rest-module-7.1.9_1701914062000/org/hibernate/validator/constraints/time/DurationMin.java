/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.Constraint
 *  javax.validation.Payload
 *  javax.validation.ReportAsSingleViolation
 */
package org.hibernate.validator.constraints.time;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import org.hibernate.validator.Incubating;

@Documented
@Constraint(validatedBy={})
@Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=List.class)
@ReportAsSingleViolation
@Incubating
public @interface DurationMin {
    public String message() default "{org.hibernate.validator.constraints.time.DurationMin.message}";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

    public long days() default 0L;

    public long hours() default 0L;

    public long minutes() default 0L;

    public long seconds() default 0L;

    public long millis() default 0L;

    public long nanos() default 0L;

    public boolean inclusive() default true;

    @Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
    @Retention(value=RetentionPolicy.RUNTIME)
    @Documented
    public static @interface List {
        public DurationMin[] value();
    }
}

