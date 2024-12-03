/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.BeanCreationException
 *  org.springframework.beans.factory.BeanCurrentlyInCreationException
 *  org.springframework.beans.factory.BeanFactoryUtils
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.aop.framework.autoproxy;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.Advisor;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class BeanFactoryAdvisorRetrievalHelper {
    private static final Log logger = LogFactory.getLog(BeanFactoryAdvisorRetrievalHelper.class);
    private final ConfigurableListableBeanFactory beanFactory;
    @Nullable
    private volatile String[] cachedAdvisorBeanNames;

    public BeanFactoryAdvisorRetrievalHelper(ConfigurableListableBeanFactory beanFactory) {
        Assert.notNull((Object)beanFactory, (String)"ListableBeanFactory must not be null");
        this.beanFactory = beanFactory;
    }

    public List<Advisor> findAdvisorBeans() {
        String[] advisorNames = this.cachedAdvisorBeanNames;
        if (advisorNames == null) {
            this.cachedAdvisorBeanNames = advisorNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors((ListableBeanFactory)this.beanFactory, Advisor.class, (boolean)true, (boolean)false);
        }
        if (advisorNames.length == 0) {
            return new ArrayList<Advisor>();
        }
        ArrayList<Advisor> advisors = new ArrayList<Advisor>();
        for (String name : advisorNames) {
            if (!this.isEligibleBean(name)) continue;
            if (this.beanFactory.isCurrentlyInCreation(name)) {
                if (!logger.isTraceEnabled()) continue;
                logger.trace((Object)("Skipping currently created advisor '" + name + "'"));
                continue;
            }
            try {
                advisors.add((Advisor)this.beanFactory.getBean(name, Advisor.class));
            }
            catch (BeanCreationException ex) {
                BeanCreationException bce;
                String bceBeanName;
                Throwable rootCause = ex.getMostSpecificCause();
                if (rootCause instanceof BeanCurrentlyInCreationException && (bceBeanName = (bce = (BeanCreationException)rootCause).getBeanName()) != null && this.beanFactory.isCurrentlyInCreation(bceBeanName)) {
                    if (!logger.isTraceEnabled()) continue;
                    logger.trace((Object)("Skipping advisor '" + name + "' with dependency on currently created bean: " + ex.getMessage()));
                    continue;
                }
                throw ex;
            }
        }
        return advisors;
    }

    protected boolean isEligibleBean(String beanName) {
        return true;
    }
}

