/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.transaction.annotation;

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Ejb3TransactionAnnotationParser;
import org.springframework.transaction.annotation.JtaTransactionAnnotationParser;
import org.springframework.transaction.annotation.SpringTransactionAnnotationParser;
import org.springframework.transaction.annotation.TransactionAnnotationParser;
import org.springframework.transaction.interceptor.AbstractFallbackTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class AnnotationTransactionAttributeSource
extends AbstractFallbackTransactionAttributeSource
implements Serializable {
    private static final boolean jta12Present;
    private static final boolean ejb3Present;
    private final boolean publicMethodsOnly;
    private final Set<TransactionAnnotationParser> annotationParsers;

    public AnnotationTransactionAttributeSource() {
        this(true);
    }

    public AnnotationTransactionAttributeSource(boolean publicMethodsOnly) {
        this.publicMethodsOnly = publicMethodsOnly;
        if (jta12Present || ejb3Present) {
            this.annotationParsers = new LinkedHashSet<TransactionAnnotationParser>(4);
            this.annotationParsers.add(new SpringTransactionAnnotationParser());
            if (jta12Present) {
                this.annotationParsers.add(new JtaTransactionAnnotationParser());
            }
            if (ejb3Present) {
                this.annotationParsers.add(new Ejb3TransactionAnnotationParser());
            }
        } else {
            this.annotationParsers = Collections.singleton(new SpringTransactionAnnotationParser());
        }
    }

    public AnnotationTransactionAttributeSource(TransactionAnnotationParser annotationParser) {
        this.publicMethodsOnly = true;
        Assert.notNull((Object)annotationParser, (String)"TransactionAnnotationParser must not be null");
        this.annotationParsers = Collections.singleton(annotationParser);
    }

    public AnnotationTransactionAttributeSource(TransactionAnnotationParser ... annotationParsers) {
        this.publicMethodsOnly = true;
        Assert.notEmpty((Object[])annotationParsers, (String)"At least one TransactionAnnotationParser needs to be specified");
        this.annotationParsers = new LinkedHashSet<TransactionAnnotationParser>(Arrays.asList(annotationParsers));
    }

    public AnnotationTransactionAttributeSource(Set<TransactionAnnotationParser> annotationParsers) {
        this.publicMethodsOnly = true;
        Assert.notEmpty(annotationParsers, (String)"At least one TransactionAnnotationParser needs to be specified");
        this.annotationParsers = annotationParsers;
    }

    @Override
    public boolean isCandidateClass(Class<?> targetClass) {
        for (TransactionAnnotationParser parser : this.annotationParsers) {
            if (!parser.isCandidateClass(targetClass)) continue;
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    protected TransactionAttribute findTransactionAttribute(Class<?> clazz) {
        return this.determineTransactionAttribute(clazz);
    }

    @Override
    @Nullable
    protected TransactionAttribute findTransactionAttribute(Method method) {
        return this.determineTransactionAttribute(method);
    }

    @Nullable
    protected TransactionAttribute determineTransactionAttribute(AnnotatedElement element) {
        for (TransactionAnnotationParser parser : this.annotationParsers) {
            TransactionAttribute attr = parser.parseTransactionAnnotation(element);
            if (attr == null) continue;
            return attr;
        }
        return null;
    }

    @Override
    protected boolean allowPublicMethodsOnly() {
        return this.publicMethodsOnly;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AnnotationTransactionAttributeSource)) {
            return false;
        }
        AnnotationTransactionAttributeSource otherTas = (AnnotationTransactionAttributeSource)other;
        return this.annotationParsers.equals(otherTas.annotationParsers) && this.publicMethodsOnly == otherTas.publicMethodsOnly;
    }

    public int hashCode() {
        return this.annotationParsers.hashCode();
    }

    static {
        ClassLoader classLoader = AnnotationTransactionAttributeSource.class.getClassLoader();
        jta12Present = ClassUtils.isPresent((String)"javax.transaction.Transactional", (ClassLoader)classLoader);
        ejb3Present = ClassUtils.isPresent((String)"javax.ejb.TransactionAttribute", (ClassLoader)classLoader);
    }
}

