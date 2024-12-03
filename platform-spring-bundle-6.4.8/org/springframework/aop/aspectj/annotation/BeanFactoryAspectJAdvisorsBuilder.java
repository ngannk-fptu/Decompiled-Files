/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aspectj.lang.reflect.PerClauseKind
 */
package org.springframework.aop.aspectj.annotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.aspectj.lang.reflect.PerClauseKind;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.annotation.AspectJAdvisorFactory;
import org.springframework.aop.aspectj.annotation.AspectMetadata;
import org.springframework.aop.aspectj.annotation.BeanFactoryAspectInstanceFactory;
import org.springframework.aop.aspectj.annotation.MetadataAwareAspectInstanceFactory;
import org.springframework.aop.aspectj.annotation.PrototypeAspectInstanceFactory;
import org.springframework.aop.aspectj.annotation.ReflectiveAspectJAdvisorFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class BeanFactoryAspectJAdvisorsBuilder {
    private final ListableBeanFactory beanFactory;
    private final AspectJAdvisorFactory advisorFactory;
    @Nullable
    private volatile List<String> aspectBeanNames;
    private final Map<String, List<Advisor>> advisorsCache = new ConcurrentHashMap<String, List<Advisor>>();
    private final Map<String, MetadataAwareAspectInstanceFactory> aspectFactoryCache = new ConcurrentHashMap<String, MetadataAwareAspectInstanceFactory>();

    public BeanFactoryAspectJAdvisorsBuilder(ListableBeanFactory beanFactory) {
        this(beanFactory, new ReflectiveAspectJAdvisorFactory(beanFactory));
    }

    public BeanFactoryAspectJAdvisorsBuilder(ListableBeanFactory beanFactory, AspectJAdvisorFactory advisorFactory) {
        Assert.notNull((Object)beanFactory, "ListableBeanFactory must not be null");
        Assert.notNull((Object)advisorFactory, "AspectJAdvisorFactory must not be null");
        this.beanFactory = beanFactory;
        this.advisorFactory = advisorFactory;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Advisor> buildAspectJAdvisors() {
        List<String> aspectNames = this.aspectBeanNames;
        if (aspectNames == null) {
            BeanFactoryAspectJAdvisorsBuilder beanFactoryAspectJAdvisorsBuilder = this;
            synchronized (beanFactoryAspectJAdvisorsBuilder) {
                aspectNames = this.aspectBeanNames;
                if (aspectNames == null) {
                    String[] beanNames;
                    ArrayList<Advisor> advisors = new ArrayList<Advisor>();
                    aspectNames = new ArrayList<String>();
                    for (String beanName : beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(this.beanFactory, Object.class, true, false)) {
                        BeanFactoryAspectInstanceFactory factory;
                        Class<?> beanType;
                        if (!this.isEligibleBean(beanName) || (beanType = this.beanFactory.getType(beanName, false)) == null || !this.advisorFactory.isAspect(beanType)) continue;
                        aspectNames.add(beanName);
                        AspectMetadata amd = new AspectMetadata(beanType, beanName);
                        if (amd.getAjType().getPerClause().getKind() == PerClauseKind.SINGLETON) {
                            factory = new BeanFactoryAspectInstanceFactory(this.beanFactory, beanName);
                            List<Advisor> classAdvisors = this.advisorFactory.getAdvisors(factory);
                            if (this.beanFactory.isSingleton(beanName)) {
                                this.advisorsCache.put(beanName, classAdvisors);
                            } else {
                                this.aspectFactoryCache.put(beanName, factory);
                            }
                            advisors.addAll(classAdvisors);
                            continue;
                        }
                        if (this.beanFactory.isSingleton(beanName)) {
                            throw new IllegalArgumentException("Bean with name '" + beanName + "' is a singleton, but aspect instantiation model is not singleton");
                        }
                        factory = new PrototypeAspectInstanceFactory(this.beanFactory, beanName);
                        this.aspectFactoryCache.put(beanName, factory);
                        advisors.addAll(this.advisorFactory.getAdvisors(factory));
                    }
                    this.aspectBeanNames = aspectNames;
                    return advisors;
                }
            }
        }
        if (aspectNames.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<Advisor> advisors = new ArrayList<Advisor>();
        for (String aspectName : aspectNames) {
            List<Advisor> cachedAdvisors = this.advisorsCache.get(aspectName);
            if (cachedAdvisors != null) {
                advisors.addAll(cachedAdvisors);
                continue;
            }
            MetadataAwareAspectInstanceFactory factory = this.aspectFactoryCache.get(aspectName);
            advisors.addAll(this.advisorFactory.getAdvisors(factory));
        }
        return advisors;
    }

    protected boolean isEligibleBean(String beanName) {
        return true;
    }
}

