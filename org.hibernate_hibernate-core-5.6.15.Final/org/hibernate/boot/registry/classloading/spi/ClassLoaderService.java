/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.registry.classloading.spi;

import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import org.hibernate.service.Service;
import org.hibernate.service.spi.Stoppable;

public interface ClassLoaderService
extends Service,
Stoppable {
    public <T> Class<T> classForName(String var1);

    public URL locateResource(String var1);

    public InputStream locateResourceStream(String var1);

    public List<URL> locateResources(String var1);

    public <S> Collection<S> loadJavaServices(Class<S> var1);

    public <T> T generateProxy(InvocationHandler var1, Class ... var2);

    public Package packageForNameOrNull(String var1);

    public <T> T workWithClassLoader(Work<T> var1);

    public static interface Work<T> {
        public T doWork(ClassLoader var1);
    }
}

