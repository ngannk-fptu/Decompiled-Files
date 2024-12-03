/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.spi;

public interface HydratedCompoundValueHandler {
    public Object extract(Object var1);

    public void inject(Object var1, Object var2);
}

