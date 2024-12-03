/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl.hibernate.configuration;

import com.atlassian.user.configuration.ConfigurationException;
import com.atlassian.user.configuration.DefaultRepositoryProcessor;
import com.atlassian.user.configuration.RepositoryAccessor;
import com.atlassian.user.configuration.RepositoryConfiguration;
import com.atlassian.user.impl.hibernate.configuration.HibernateAccessor;

public class HibernateRepositoryProcessor
extends DefaultRepositoryProcessor {
    public RepositoryAccessor process(RepositoryConfiguration config) throws ConfigurationException {
        HibernateAccessor accessor = config.hasComponent("accessor") ? (HibernateAccessor)config.getComponent("accessor") : (HibernateAccessor)this.createBean("accessor", config);
        config.addComponent("sessionFactory", accessor.getSessionFactory());
        config.addComponent("hibernateProvider", this.createBean("hibernateProvider", config));
        if (!config.hasComponent("externalEntityDAO")) {
            config.addComponent("externalEntityDAO", this.createBean("externalEntityDAO", config));
        }
        return super.process(config);
    }
}

