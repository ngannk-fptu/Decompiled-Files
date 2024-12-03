/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.component.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.osgi.service.component.annotations.FieldOption;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.component.annotations.ReferenceScope;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Retention(value=RetentionPolicy.CLASS)
@Target(value={ElementType.METHOD, ElementType.FIELD})
public @interface Reference {
    public String name() default "";

    public Class<?> service() default Object.class;

    public ReferenceCardinality cardinality() default ReferenceCardinality.MANDATORY;

    public ReferencePolicy policy() default ReferencePolicy.STATIC;

    public String target() default "";

    public ReferencePolicyOption policyOption() default ReferencePolicyOption.RELUCTANT;

    public ReferenceScope scope() default ReferenceScope.BUNDLE;

    public String bind() default "";

    public String updated() default "";

    public String unbind() default "";

    public String field() default "";

    public FieldOption fieldOption() default FieldOption.REPLACE;
}

