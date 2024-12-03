/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.ApplicationException
 *  javax.ejb.TransactionAttribute
 *  javax.ejb.TransactionAttributeType
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.lang.Nullable
 */
package org.springframework.transaction.annotation;

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import javax.ejb.ApplicationException;
import javax.ejb.TransactionAttributeType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.TransactionAnnotationParser;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;

public class Ejb3TransactionAnnotationParser
implements TransactionAnnotationParser,
Serializable {
    @Override
    public boolean isCandidateClass(Class<?> targetClass) {
        return AnnotationUtils.isCandidateClass(targetClass, javax.ejb.TransactionAttribute.class);
    }

    @Override
    @Nullable
    public TransactionAttribute parseTransactionAnnotation(AnnotatedElement element) {
        javax.ejb.TransactionAttribute ann = element.getAnnotation(javax.ejb.TransactionAttribute.class);
        if (ann != null) {
            return this.parseTransactionAnnotation(ann);
        }
        return null;
    }

    public TransactionAttribute parseTransactionAnnotation(javax.ejb.TransactionAttribute ann) {
        return new Ejb3TransactionAttribute(ann.value());
    }

    public boolean equals(@Nullable Object other) {
        return other instanceof Ejb3TransactionAnnotationParser;
    }

    public int hashCode() {
        return Ejb3TransactionAnnotationParser.class.hashCode();
    }

    private static class Ejb3TransactionAttribute
    extends DefaultTransactionAttribute {
        public Ejb3TransactionAttribute(TransactionAttributeType type) {
            this.setPropagationBehaviorName("PROPAGATION_" + type.name());
        }

        @Override
        public boolean rollbackOn(Throwable ex) {
            ApplicationException ann = ex.getClass().getAnnotation(ApplicationException.class);
            return ann != null ? ann.rollback() : super.rollbackOn(ex);
        }
    }
}

