/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.Transactional
 *  org.springframework.core.annotation.AnnotatedElementUtils
 *  org.springframework.core.annotation.AnnotationAttributes
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.lang.Nullable
 */
package org.springframework.transaction.annotation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import javax.transaction.Transactional;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.TransactionAnnotationParser;
import org.springframework.transaction.interceptor.NoRollbackRuleAttribute;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;

public class JtaTransactionAnnotationParser
implements TransactionAnnotationParser,
Serializable {
    @Override
    public boolean isCandidateClass(Class<?> targetClass) {
        return AnnotationUtils.isCandidateClass(targetClass, Transactional.class);
    }

    @Override
    @Nullable
    public TransactionAttribute parseTransactionAnnotation(AnnotatedElement element) {
        AnnotationAttributes attributes = AnnotatedElementUtils.getMergedAnnotationAttributes((AnnotatedElement)element, Transactional.class);
        if (attributes != null) {
            return this.parseTransactionAnnotation(attributes);
        }
        return null;
    }

    public TransactionAttribute parseTransactionAnnotation(Transactional ann) {
        return this.parseTransactionAnnotation(AnnotationUtils.getAnnotationAttributes((Annotation)ann, (boolean)false, (boolean)false));
    }

    protected TransactionAttribute parseTransactionAnnotation(AnnotationAttributes attributes) {
        RuleBasedTransactionAttribute rbta = new RuleBasedTransactionAttribute();
        rbta.setPropagationBehaviorName("PROPAGATION_" + attributes.getEnum("value").toString());
        ArrayList<RollbackRuleAttribute> rollbackRules = new ArrayList<RollbackRuleAttribute>();
        for (Class rbRule : attributes.getClassArray("rollbackOn")) {
            rollbackRules.add(new RollbackRuleAttribute(rbRule));
        }
        for (Class rbRule : attributes.getClassArray("dontRollbackOn")) {
            rollbackRules.add(new NoRollbackRuleAttribute(rbRule));
        }
        rbta.setRollbackRules(rollbackRules);
        return rbta;
    }

    public boolean equals(@Nullable Object other) {
        return other instanceof JtaTransactionAnnotationParser;
    }

    public int hashCode() {
        return JtaTransactionAnnotationParser.class.hashCode();
    }
}

