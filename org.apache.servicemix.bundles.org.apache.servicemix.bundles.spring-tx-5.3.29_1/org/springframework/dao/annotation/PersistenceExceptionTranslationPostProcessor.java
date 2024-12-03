/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.framework.autoproxy.AbstractBeanFactoryAwareAdvisingPostProcessor
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.stereotype.Repository
 *  org.springframework.util.Assert
 */
package org.springframework.dao.annotation;

import java.lang.annotation.Annotation;
import org.springframework.aop.framework.autoproxy.AbstractBeanFactoryAwareAdvisingPostProcessor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.dao.annotation.PersistenceExceptionTranslationAdvisor;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

public class PersistenceExceptionTranslationPostProcessor
extends AbstractBeanFactoryAwareAdvisingPostProcessor {
    private Class<? extends Annotation> repositoryAnnotationType = Repository.class;

    public void setRepositoryAnnotationType(Class<? extends Annotation> repositoryAnnotationType) {
        Assert.notNull(repositoryAnnotationType, (String)"'repositoryAnnotationType' must not be null");
        this.repositoryAnnotationType = repositoryAnnotationType;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        super.setBeanFactory(beanFactory);
        if (!(beanFactory instanceof ListableBeanFactory)) {
            throw new IllegalArgumentException("Cannot use PersistenceExceptionTranslator autodetection without ListableBeanFactory");
        }
        this.advisor = new PersistenceExceptionTranslationAdvisor((ListableBeanFactory)beanFactory, this.repositoryAnnotationType);
    }
}

