/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jmx.support;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.MBeanServerNotFoundException;
import org.springframework.jmx.support.JmxUtils;
import org.springframework.lang.Nullable;

public class MBeanServerFactoryBean
implements FactoryBean<MBeanServer>,
InitializingBean,
DisposableBean {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private boolean locateExistingServerIfPossible = false;
    @Nullable
    private String agentId;
    @Nullable
    private String defaultDomain;
    private boolean registerWithFactory = true;
    @Nullable
    private MBeanServer server;
    private boolean newlyRegistered = false;

    public void setLocateExistingServerIfPossible(boolean locateExistingServerIfPossible) {
        this.locateExistingServerIfPossible = locateExistingServerIfPossible;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public void setDefaultDomain(String defaultDomain) {
        this.defaultDomain = defaultDomain;
    }

    public void setRegisterWithFactory(boolean registerWithFactory) {
        this.registerWithFactory = registerWithFactory;
    }

    @Override
    public void afterPropertiesSet() throws MBeanServerNotFoundException {
        if (this.locateExistingServerIfPossible || this.agentId != null) {
            try {
                this.server = this.locateMBeanServer(this.agentId);
            }
            catch (MBeanServerNotFoundException ex) {
                if (this.agentId != null) {
                    throw ex;
                }
                this.logger.info("No existing MBeanServer found - creating new one");
            }
        }
        if (this.server == null) {
            this.server = this.createMBeanServer(this.defaultDomain, this.registerWithFactory);
            this.newlyRegistered = this.registerWithFactory;
        }
    }

    protected MBeanServer locateMBeanServer(@Nullable String agentId) throws MBeanServerNotFoundException {
        return JmxUtils.locateMBeanServer(agentId);
    }

    protected MBeanServer createMBeanServer(@Nullable String defaultDomain, boolean registerWithFactory) {
        if (registerWithFactory) {
            return MBeanServerFactory.createMBeanServer(defaultDomain);
        }
        return MBeanServerFactory.newMBeanServer(defaultDomain);
    }

    @Override
    @Nullable
    public MBeanServer getObject() {
        return this.server;
    }

    @Override
    public Class<? extends MBeanServer> getObjectType() {
        return this.server != null ? this.server.getClass() : MBeanServer.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() {
        if (this.newlyRegistered) {
            MBeanServerFactory.releaseMBeanServer(this.server);
        }
    }
}

