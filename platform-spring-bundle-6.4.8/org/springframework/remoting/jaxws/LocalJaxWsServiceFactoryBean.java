/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.Service
 */
package org.springframework.remoting.jaxws;

import javax.xml.ws.Service;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.remoting.jaxws.LocalJaxWsServiceFactory;

public class LocalJaxWsServiceFactoryBean
extends LocalJaxWsServiceFactory
implements FactoryBean<Service>,
InitializingBean {
    @Nullable
    private Service service;

    @Override
    public void afterPropertiesSet() {
        this.service = this.createJaxWsService();
    }

    @Override
    @Nullable
    public Service getObject() {
        return this.service;
    }

    @Override
    public Class<? extends Service> getObjectType() {
        return this.service != null ? this.service.getClass() : Service.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}

