/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.spi;

import java.net.URL;
import org.hibernate.boot.spi.ClassLoaderAccess;

public abstract class ClassLoaderAccessDelegateImpl
implements ClassLoaderAccess {
    protected abstract ClassLoaderAccess getDelegate();

    @Override
    public <T> Class<T> classForName(String name) {
        return this.getDelegate().classForName(name);
    }

    @Override
    public URL locateResource(String resourceName) {
        return this.getDelegate().locateResource(resourceName);
    }
}

