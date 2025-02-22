/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.wiring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.wiring.BeanWiringInfo;
import org.springframework.beans.factory.wiring.BeanWiringInfoResolver;
import org.springframework.beans.factory.wiring.ClassNameBeanWiringInfoResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class BeanConfigurerSupport
implements BeanFactoryAware,
InitializingBean,
DisposableBean {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private volatile BeanWiringInfoResolver beanWiringInfoResolver;
    @Nullable
    private volatile ConfigurableListableBeanFactory beanFactory;

    public void setBeanWiringInfoResolver(BeanWiringInfoResolver beanWiringInfoResolver) {
        Assert.notNull((Object)beanWiringInfoResolver, "BeanWiringInfoResolver must not be null");
        this.beanWiringInfoResolver = beanWiringInfoResolver;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new IllegalArgumentException("Bean configurer aspect needs to run in a ConfigurableListableBeanFactory: " + beanFactory);
        }
        this.beanFactory = (ConfigurableListableBeanFactory)beanFactory;
        if (this.beanWiringInfoResolver == null) {
            this.beanWiringInfoResolver = this.createDefaultBeanWiringInfoResolver();
        }
    }

    @Nullable
    protected BeanWiringInfoResolver createDefaultBeanWiringInfoResolver() {
        return new ClassNameBeanWiringInfoResolver();
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull((Object)this.beanFactory, "BeanFactory must be set");
    }

    @Override
    public void destroy() {
        this.beanFactory = null;
        this.beanWiringInfoResolver = null;
    }

    public void configureBean(Object beanInstance) {
        if (this.beanFactory == null) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("BeanFactory has not been set on " + ClassUtils.getShortName(this.getClass()) + ": Make sure this configurer runs in a Spring container. Unable to configure bean of type [" + ClassUtils.getDescriptiveType(beanInstance) + "]. Proceeding without injection.");
            }
            return;
        }
        BeanWiringInfoResolver bwiResolver = this.beanWiringInfoResolver;
        Assert.state(bwiResolver != null, "No BeanWiringInfoResolver available");
        BeanWiringInfo bwi = bwiResolver.resolveWiringInfo(beanInstance);
        if (bwi == null) {
            return;
        }
        ConfigurableListableBeanFactory beanFactory = this.beanFactory;
        Assert.state(beanFactory != null, "No BeanFactory available");
        try {
            String beanName = bwi.getBeanName();
            if (bwi.indicatesAutowiring() || bwi.isDefaultBeanName() && beanName != null && !beanFactory.containsBean(beanName)) {
                beanFactory.autowireBeanProperties(beanInstance, bwi.getAutowireMode(), bwi.getDependencyCheck());
                beanFactory.initializeBean(beanInstance, beanName != null ? beanName : "");
            } else {
                beanFactory.configureBean(beanInstance, beanName != null ? beanName : "");
            }
        }
        catch (BeanCreationException ex) {
            BeanCreationException bce;
            String bceBeanName;
            Throwable rootCause = ex.getMostSpecificCause();
            if (rootCause instanceof BeanCurrentlyInCreationException && (bceBeanName = (bce = (BeanCreationException)rootCause).getBeanName()) != null && beanFactory.isCurrentlyInCreation(bceBeanName)) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Failed to create target bean '" + bce.getBeanName() + "' while configuring object of type [" + beanInstance.getClass().getName() + "] - probably due to a circular reference. This is a common startup situation and usually not fatal. Proceeding without injection. Original exception: " + ex);
                }
                return;
            }
            throw ex;
        }
    }
}

