/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.HandleResolver
 *  org.apache.commons.beanutils.BeanUtils
 *  org.hibernate.Hibernate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.core.persistence.hibernate;

import com.atlassian.bonnie.HandleResolver;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateHandleResolver
implements HandleResolver {
    private static final Logger log = LoggerFactory.getLogger(HibernateHandleResolver.class);
    private static final String HANDLE_ATTRIBUTE_NAME = "id";

    public HibernateHandle getHandle(Object obj) {
        try {
            String handleAttributeValue = BeanUtils.getProperty((Object)obj, (String)HANDLE_ATTRIBUTE_NAME);
            return new HibernateHandle(Hibernate.getClass((Object)obj).getName() + "-" + handleAttributeValue);
        }
        catch (Exception e) {
            log.error("Cannot identify object: " + e, (Throwable)e);
            return null;
        }
    }
}

