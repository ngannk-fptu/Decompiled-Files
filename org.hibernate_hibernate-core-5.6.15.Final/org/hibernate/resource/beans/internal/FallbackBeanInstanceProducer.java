/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.resource.beans.internal;

import java.lang.reflect.Constructor;
import org.hibernate.InstantiationException;
import org.hibernate.resource.beans.spi.BeanInstanceProducer;
import org.jboss.logging.Logger;

public class FallbackBeanInstanceProducer
implements BeanInstanceProducer {
    private static final Logger log = Logger.getLogger(FallbackBeanInstanceProducer.class);
    public static final FallbackBeanInstanceProducer INSTANCE = new FallbackBeanInstanceProducer();

    private FallbackBeanInstanceProducer() {
    }

    @Override
    public <B> B produceBeanInstance(Class<B> beanType) {
        log.tracef("Creating ManagedBean(%s) using direct instantiation", (Object)beanType.getName());
        try {
            Constructor<B> constructor = beanType.getDeclaredConstructor(new Class[0]);
            constructor.setAccessible(true);
            return constructor.newInstance(new Object[0]);
        }
        catch (Exception e) {
            throw new InstantiationException("Could not instantiate managed bean directly", beanType, e);
        }
    }

    @Override
    public <B> B produceBeanInstance(String name, Class<B> beanType) {
        return this.produceBeanInstance(beanType);
    }
}

