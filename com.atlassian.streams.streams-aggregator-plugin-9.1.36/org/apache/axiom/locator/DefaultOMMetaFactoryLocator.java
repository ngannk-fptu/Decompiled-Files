/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.locator;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import org.apache.axiom.locator.DefaultLoader;
import org.apache.axiom.locator.Implementation;
import org.apache.axiom.locator.ImplementationFactory;
import org.apache.axiom.locator.PriorityBasedOMMetaFactoryLocator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class DefaultOMMetaFactoryLocator
extends PriorityBasedOMMetaFactoryLocator {
    private static final Log log = LogFactory.getLog(DefaultOMMetaFactoryLocator.class);

    public DefaultOMMetaFactoryLocator() {
        ClassLoader classLoader = DefaultOMMetaFactoryLocator.class.getClassLoader();
        DefaultLoader loader = new DefaultLoader(classLoader);
        ArrayList<Implementation> implementations = new ArrayList<Implementation>();
        String metaFactoryClassName = null;
        try {
            metaFactoryClassName = System.getProperty("org.apache.axiom.om.OMMetaFactory");
            if ("".equals(metaFactoryClassName)) {
                metaFactoryClassName = null;
            }
        }
        catch (SecurityException e) {
            // empty catch block
        }
        if (metaFactoryClassName != null) {
            Implementation implementation;
            if (log.isDebugEnabled()) {
                log.debug((Object)("org.apache.axiom.om.OMMetaFactory system property is set; value=" + metaFactoryClassName));
            }
            if ((implementation = ImplementationFactory.createDefaultImplementation(loader, metaFactoryClassName)) != null) {
                implementations.add(implementation);
            }
        }
        log.debug((Object)"Starting class path based discovery");
        try {
            Enumeration<URL> e = classLoader.getResources("META-INF/axiom.xml");
            while (e.hasMoreElements()) {
                implementations.addAll(ImplementationFactory.parseDescriptor(loader, e.nextElement()));
            }
        }
        catch (IOException ex) {
            // empty catch block
        }
        this.loadImplementations(implementations);
    }
}

