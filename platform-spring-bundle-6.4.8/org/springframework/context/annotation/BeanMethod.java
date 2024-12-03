/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import org.springframework.beans.factory.parsing.Problem;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClass;
import org.springframework.context.annotation.ConfigurationMethod;
import org.springframework.core.type.MethodMetadata;
import org.springframework.lang.Nullable;

final class BeanMethod
extends ConfigurationMethod {
    BeanMethod(MethodMetadata metadata, ConfigurationClass configurationClass) {
        super(metadata, configurationClass);
    }

    @Override
    public void validate(ProblemReporter problemReporter) {
        if (this.getMetadata().isStatic()) {
            return;
        }
        if (this.configurationClass.getMetadata().isAnnotated(Configuration.class.getName()) && !this.getMetadata().isOverridable()) {
            problemReporter.error(new NonOverridableMethodError());
        }
    }

    public boolean equals(@Nullable Object other) {
        return this == other || other instanceof BeanMethod && this.metadata.equals(((BeanMethod)other).metadata);
    }

    public int hashCode() {
        return this.metadata.hashCode();
    }

    @Override
    public String toString() {
        return "BeanMethod: " + this.metadata;
    }

    private class NonOverridableMethodError
    extends Problem {
        NonOverridableMethodError() {
            super(String.format("@Bean method '%s' must not be private or final; change the method's modifiers to continue", BeanMethod.this.getMetadata().getMethodName()), BeanMethod.this.getResourceLocation());
        }
    }
}

