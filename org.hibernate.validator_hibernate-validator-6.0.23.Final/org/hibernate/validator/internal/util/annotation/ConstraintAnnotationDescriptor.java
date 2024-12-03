/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintTarget
 *  javax.validation.Payload
 */
package org.hibernate.validator.internal.util.annotation;

import java.lang.annotation.Annotation;
import java.util.Map;
import javax.validation.ConstraintTarget;
import javax.validation.Payload;
import org.hibernate.validator.internal.util.annotation.AnnotationDescriptor;

public class ConstraintAnnotationDescriptor<A extends Annotation>
extends AnnotationDescriptor<A> {
    public ConstraintAnnotationDescriptor(A annotation) {
        super(annotation);
    }

    public ConstraintAnnotationDescriptor(AnnotationDescriptor<A> descriptor) {
        super(descriptor);
    }

    public String getMessage() {
        return this.getMandatoryAttribute("message", String.class);
    }

    public Class<?>[] getGroups() {
        return this.getMandatoryAttribute("groups", Class[].class);
    }

    public Class<? extends Payload>[] getPayload() {
        return this.getMandatoryAttribute("payload", Class[].class);
    }

    public ConstraintTarget getValidationAppliesTo() {
        return this.getAttribute("validationAppliesTo", ConstraintTarget.class);
    }

    public static class Builder<S extends Annotation>
    extends AnnotationDescriptor.Builder<S> {
        public Builder(Class<S> type) {
            super(type);
        }

        public Builder(Class<S> type, Map<String, Object> attributes) {
            super(type, attributes);
        }

        public Builder(S annotation) {
            super(annotation);
        }

        public Builder<S> setMessage(String message) {
            this.setAttribute("message", message);
            return this;
        }

        public Builder<S> setGroups(Class<?>[] groups) {
            this.setAttribute("groups", groups);
            return this;
        }

        public Builder<S> setPayload(Class<?>[] payload) {
            this.setAttribute("payload", payload);
            return this;
        }

        public Builder<S> setValidationAppliesTo(ConstraintTarget validationAppliesTo) {
            this.setAttribute("validationAppliesTo", validationAppliesTo);
            return this;
        }

        @Override
        public ConstraintAnnotationDescriptor<S> build() {
            return new ConstraintAnnotationDescriptor(super.build());
        }
    }
}

