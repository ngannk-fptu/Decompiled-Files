/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.spring.extension.registration;

import com.atlassian.spring.extension.registration.Registration;
import com.atlassian.spring.extension.registration.RegistrationException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;

public class BeanRegistration
implements BeanFactoryAware,
InitializingBean {
    private BeanFactory beanFactory;
    private List registrations = new ArrayList();

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public void setRegistrations(List registrations) {
        this.registrations = registrations;
    }

    public List getRegistrations() {
        return this.registrations;
    }

    public void afterPropertiesSet() throws RegistrationException {
        Iterator it = this.registrations.iterator();
        while (it.hasNext()) {
            Registration registration = (Registration)it.next();
            registration.register(this.beanFactory);
            it.remove();
        }
    }
}

