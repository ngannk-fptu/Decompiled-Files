/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.beans.spi;

public interface BeanInstanceProducer {
    public <B> B produceBeanInstance(Class<B> var1);

    public <B> B produceBeanInstance(String var1, Class<B> var2);
}

