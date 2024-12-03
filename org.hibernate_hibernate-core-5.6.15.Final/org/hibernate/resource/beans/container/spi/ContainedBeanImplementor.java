/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.beans.container.spi;

import org.hibernate.resource.beans.container.spi.ContainedBean;

public interface ContainedBeanImplementor<B>
extends ContainedBean<B> {
    public void initialize();

    public void release();
}

