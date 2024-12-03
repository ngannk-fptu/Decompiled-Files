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
import java.text.Normalizer;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy={})
@Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=List.class)
public @interface CodePointLength {
    public int min() default 0;

    public int max() default 0x7FFFFFFF;

    public NormalizationStrategy normalizationStrategy() default NormalizationStrategy.NONE;

    public String message() default "{org.hibernate.validator.constraints.CodePointLength.message}";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

    public static enum NormalizationStrategy {
        NONE(null),
        NFD(Normalizer.Form.NFD),
        NFC(Normalizer.Form.NFC),
        NFKD(Normalizer.Form.NFKD),
        NFKC(Normalizer.Form.NFKC);

        private final Normalizer.Form form;

        private NormalizationStrategy(Normalizer.Form form) {
            this.form = form;
        }

        public CharSequence normalize(CharSequence value) {
            if (this.form == null || value == null || value.length() == 0) {
                return value;
            }
            return Normalizer.normalize(value, this.form);
        }
    }

    @Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
    @Retention(value=RetentionPolicy.RUNTIME)
    @Documented
    public static @interface List {
        public CodePointLength[] value();
    }
}

