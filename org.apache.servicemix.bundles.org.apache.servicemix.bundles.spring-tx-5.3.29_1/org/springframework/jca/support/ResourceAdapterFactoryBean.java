/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.ResourceException
 *  javax.resource.spi.BootstrapContext
 *  javax.resource.spi.ResourceAdapter
 *  javax.resource.spi.XATerminator
 *  javax.resource.spi.work.WorkManager
 *  org.springframework.beans.BeanUtils
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 */
package org.springframework.jca.support;

import javax.resource.ResourceException;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.XATerminator;
import javax.resource.spi.work.WorkManager;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jca.support.SimpleBootstrapContext;
import org.springframework.lang.Nullable;

public class ResourceAdapterFactoryBean
implements FactoryBean<ResourceAdapter>,
InitializingBean,
DisposableBean {
    @Nullable
    private ResourceAdapter resourceAdapter;
    @Nullable
    private BootstrapContext bootstrapContext;
    @Nullable
    private WorkManager workManager;
    @Nullable
    private XATerminator xaTerminator;

    public void setResourceAdapterClass(Class<? extends ResourceAdapter> resourceAdapterClass) {
        this.resourceAdapter = (ResourceAdapter)BeanUtils.instantiateClass(resourceAdapterClass);
    }

    public void setResourceAdapter(ResourceAdapter resourceAdapter) {
        this.resourceAdapter = resourceAdapter;
    }

    public void setBootstrapContext(BootstrapContext bootstrapContext) {
        this.bootstrapContext = bootstrapContext;
    }

    public void setWorkManager(WorkManager workManager) {
        this.workManager = workManager;
    }

    public void setXaTerminator(XATerminator xaTerminator) {
        this.xaTerminator = xaTerminator;
    }

    public void afterPropertiesSet() throws ResourceException {
        if (this.resourceAdapter == null) {
            throw new IllegalArgumentException("'resourceAdapter' or 'resourceAdapterClass' is required");
        }
        if (this.bootstrapContext == null) {
            this.bootstrapContext = new SimpleBootstrapContext(this.workManager, this.xaTerminator);
        }
        this.resourceAdapter.start(this.bootstrapContext);
    }

    @Nullable
    public ResourceAdapter getObject() {
        return this.resourceAdapter;
    }

    public Class<? extends ResourceAdapter> getObjectType() {
        return this.resourceAdapter != null ? this.resourceAdapter.getClass() : ResourceAdapter.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void destroy() {
        if (this.resourceAdapter != null) {
            this.resourceAdapter.stop();
        }
    }
}

