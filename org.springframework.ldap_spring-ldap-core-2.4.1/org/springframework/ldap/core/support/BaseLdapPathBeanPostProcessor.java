/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.NoSuchBeanDefinitionException
 *  org.springframework.beans.factory.config.BeanPostProcessor
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 *  org.springframework.core.Ordered
 *  org.springframework.util.StringUtils
 */
package org.springframework.ldap.core.support;

import java.util.Collection;
import javax.naming.ldap.LdapName;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.support.AbstractContextSource;
import org.springframework.ldap.core.support.BaseLdapNameAware;
import org.springframework.ldap.core.support.BaseLdapPathAware;
import org.springframework.ldap.core.support.BaseLdapPathSource;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.util.StringUtils;

public class BaseLdapPathBeanPostProcessor
implements BeanPostProcessor,
ApplicationContextAware,
Ordered {
    private ApplicationContext applicationContext;
    private LdapName basePath;
    private String baseLdapPathSourceName;
    private int order = Integer.MAX_VALUE;

    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (bean instanceof BaseLdapNameAware) {
            BaseLdapNameAware baseLdapNameAware = (BaseLdapNameAware)bean;
            if (this.basePath != null) {
                baseLdapNameAware.setBaseLdapPath(LdapUtils.newLdapName(this.basePath));
            } else {
                BaseLdapPathSource ldapPathSource = this.getBaseLdapPathSourceFromApplicationContext();
                baseLdapNameAware.setBaseLdapPath(LdapUtils.newLdapName(ldapPathSource.getBaseLdapName()));
            }
        } else if (bean instanceof BaseLdapPathAware) {
            BaseLdapPathAware baseLdapPathAware = (BaseLdapPathAware)bean;
            if (this.basePath != null) {
                baseLdapPathAware.setBaseLdapPath(new DistinguishedName(this.basePath));
            } else {
                BaseLdapPathSource ldapPathSource = this.getBaseLdapPathSourceFromApplicationContext();
                baseLdapPathAware.setBaseLdapPath(ldapPathSource.getBaseLdapPath().immutableDistinguishedName());
            }
        }
        return bean;
    }

    BaseLdapPathSource getBaseLdapPathSourceFromApplicationContext() {
        if (StringUtils.hasLength((String)this.baseLdapPathSourceName)) {
            return (BaseLdapPathSource)this.applicationContext.getBean(this.baseLdapPathSourceName, BaseLdapPathSource.class);
        }
        Collection beans = this.applicationContext.getBeansOfType(BaseLdapPathSource.class).values();
        if (beans.isEmpty()) {
            throw new NoSuchBeanDefinitionException("No BaseLdapPathSource implementation definition found");
        }
        if (beans.size() == 1) {
            return (BaseLdapPathSource)beans.iterator().next();
        }
        BaseLdapPathSource found = null;
        for (BaseLdapPathSource bean : beans) {
            if (!(bean instanceof AbstractContextSource)) continue;
            if (found != null) {
                throw new NoSuchBeanDefinitionException("More than BaseLdapPathSource implementation definition found in current ApplicationContext; unable to determine the one to use. Please specify 'baseLdapPathSourceName'");
            }
            found = bean;
        }
        if (found == null) {
            throw new NoSuchBeanDefinitionException("More than BaseLdapPathSource implementation definition found in current ApplicationContext; unable to determine the one to use (one of them should be an AbstractContextSource instance). Please specify 'baseLdapPathSourceName'");
        }
        return found;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setBasePath(DistinguishedName basePath) {
        this.basePath = LdapUtils.newLdapName(basePath);
    }

    public void setBasePath(String basePath) {
        this.basePath = LdapUtils.newLdapName(basePath);
    }

    public void setBaseLdapPathSourceName(String contextSourceName) {
        this.baseLdapPathSourceName = contextSourceName;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }
}

