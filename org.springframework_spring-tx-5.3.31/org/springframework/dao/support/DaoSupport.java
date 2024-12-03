/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.BeanInitializationException
 *  org.springframework.beans.factory.InitializingBean
 */
package org.springframework.dao.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;

public abstract class DaoSupport
implements InitializingBean {
    protected final Log logger = LogFactory.getLog(this.getClass());

    public final void afterPropertiesSet() throws IllegalArgumentException, BeanInitializationException {
        this.checkDaoConfig();
        try {
            this.initDao();
        }
        catch (Exception ex) {
            throw new BeanInitializationException("Initialization of DAO failed", (Throwable)ex);
        }
    }

    protected abstract void checkDaoConfig() throws IllegalArgumentException;

    protected void initDao() throws Exception {
    }
}

