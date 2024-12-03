/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tiles.TilesException
 *  org.apache.tiles.preparer.PreparerException
 *  org.apache.tiles.preparer.ViewPreparer
 *  org.apache.tiles.preparer.factory.NoSuchPreparerException
 *  org.springframework.util.ClassUtils
 *  org.springframework.web.context.WebApplicationContext
 */
package org.springframework.web.servlet.view.tiles3;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.tiles.TilesException;
import org.apache.tiles.preparer.PreparerException;
import org.apache.tiles.preparer.ViewPreparer;
import org.apache.tiles.preparer.factory.NoSuchPreparerException;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.view.tiles3.AbstractSpringPreparerFactory;

public class SimpleSpringPreparerFactory
extends AbstractSpringPreparerFactory {
    private final Map<String, ViewPreparer> sharedPreparers = new ConcurrentHashMap<String, ViewPreparer>(16);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected ViewPreparer getPreparer(String name, WebApplicationContext context) throws TilesException {
        ViewPreparer preparer = this.sharedPreparers.get(name);
        if (preparer == null) {
            Map<String, ViewPreparer> map = this.sharedPreparers;
            synchronized (map) {
                preparer = this.sharedPreparers.get(name);
                if (preparer == null) {
                    try {
                        Class beanClass = ClassUtils.forName((String)name, (ClassLoader)context.getClassLoader());
                        if (!ViewPreparer.class.isAssignableFrom(beanClass)) {
                            throw new PreparerException("Invalid preparer class [" + name + "]: does not implement ViewPreparer interface");
                        }
                        preparer = (ViewPreparer)context.getAutowireCapableBeanFactory().createBean(beanClass);
                        this.sharedPreparers.put(name, preparer);
                    }
                    catch (ClassNotFoundException ex) {
                        throw new NoSuchPreparerException("Preparer class [" + name + "] not found", (Throwable)ex);
                    }
                }
            }
        }
        return preparer;
    }
}

