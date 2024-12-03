/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.annotation.AnnotatedElementUtils
 *  org.springframework.core.annotation.AnnotationAttributes
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.transaction.annotation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.TransactionAnnotationParser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.NoRollbackRuleAttribute;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class SpringTransactionAnnotationParser
implements TransactionAnnotationParser,
Serializable {
    @Override
    public boolean isCandidateClass(Class<?> targetClass) {
        return AnnotationUtils.isCandidateClass(targetClass, Transactional.class);
    }

    @Override
    @Nullable
    public TransactionAttribute parseTransactionAnnotation(AnnotatedElement element) {
        AnnotationAttributes attributes = AnnotatedElementUtils.findMergedAnnotationAttributes((AnnotatedElement)element, Transactional.class, (boolean)false, (boolean)false);
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
        Propagation propagation = (Propagation)attributes.getEnum("propagation");
        rbta.setPropagationBehavior(propagation.value());
        Isolation isolation = (Isolation)attributes.getEnum("isolation");
        rbta.setIsolationLevel(isolation.value());
        rbta.setTimeout(attributes.getNumber("timeout").intValue());
        String timeoutString = attributes.getString("timeoutString");
        Assert.isTrue((!StringUtils.hasText((String)timeoutString) || rbta.getTimeout() < 0 ? 1 : 0) != 0, (String)"Specify 'timeout' or 'timeoutString', not both");
        rbta.setTimeoutString(timeoutString);
        rbta.setReadOnly(attributes.getBoolean("readOnly"));
        rbta.setQualifier(attributes.getString("value"));
        rbta.setLabels(Arrays.asList(attributes.getStringArray("label")));
        ArrayList<RollbackRuleAttribute> rollbackRules = new ArrayList<RollbackRuleAttribute>();
        for (Class clazz : attributes.getClassArray("rollbackFor")) {
            rollbackRules.add(new RollbackRuleAttribute(clazz));
        }
        for (String string : attributes.getStringArray("rollbackForClassName")) {
            rollbackRules.add(new RollbackRuleAttribute(string));
        }
        for (Class clazz : attributes.getClassArray("noRollbackFor")) {
            rollbackRules.add(new NoRollbackRuleAttribute(clazz));
        }
        for (String string : attributes.getStringArray("noRollbackForClassName")) {
            rollbackRules.add(new NoRollbackRuleAttribute(string));
        }
        rbta.setRollbackRules(rollbackRules);
        return rbta;
    }

    public boolean equals(@Nullable Object other) {
        return other instanceof SpringTransactionAnnotationParser;
    }

    public int hashCode() {
        return SpringTransactionAnnotationParser.class.hashCode();
    }
}

