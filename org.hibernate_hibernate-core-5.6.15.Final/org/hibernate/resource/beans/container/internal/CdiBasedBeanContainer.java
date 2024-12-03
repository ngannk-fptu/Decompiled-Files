/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.enterprise.inject.spi.BeanManager
 */
package org.hibernate.resource.beans.container.internal;

import javax.enterprise.inject.spi.BeanManager;
import org.hibernate.resource.beans.container.spi.BeanContainer;

public interface CdiBasedBeanContainer
extends BeanContainer {
    public BeanManager getUsableBeanManager();
}

