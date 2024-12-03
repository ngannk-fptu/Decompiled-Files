/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.discovery.tools;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import org.apache.commons.discovery.ResourceClass;
import org.apache.commons.discovery.ResourceClassIterator;
import org.apache.commons.discovery.ResourceNameIterator;
import org.apache.commons.discovery.resource.ClassLoaders;
import org.apache.commons.discovery.resource.classes.DiscoverClasses;
import org.apache.commons.discovery.resource.names.DiscoverServiceNames;
import org.apache.commons.discovery.tools.SPInterface;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Service {
    protected Service() {
    }

    public static <T, S extends T> Enumeration<S> providers(Class<T> spiClass) {
        return Service.providers(new SPInterface<T>(spiClass), null);
    }

    public static <T, S extends T> Enumeration<S> providers(final SPInterface<T> spi, ClassLoaders loaders) {
        if (loaders == null) {
            loaders = ClassLoaders.getAppLoaders(spi.getSPClass(), Service.class, true);
        }
        ResourceNameIterator servicesIter = new DiscoverServiceNames(loaders).findResourceNames(spi.getSPName());
        final ResourceClassIterator services = new DiscoverClasses(loaders).findResourceClasses(servicesIter);
        return new Enumeration<S>(){
            private S object = this.getNextClassInstance();

            @Override
            public boolean hasMoreElements() {
                return this.object != null;
            }

            @Override
            public S nextElement() {
                if (this.object == null) {
                    throw new NoSuchElementException();
                }
                Object obj = this.object;
                this.object = this.getNextClassInstance();
                return obj;
            }

            private S getNextClassInstance() {
                while (services.hasNext()) {
                    ResourceClass info = services.nextResourceClass();
                    try {
                        return spi.newInstance(info.loadClass());
                    }
                    catch (Exception e) {
                    }
                    catch (LinkageError linkageError) {
                    }
                }
                return null;
            }
        };
    }
}

