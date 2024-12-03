/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.Nullable
 *  com.sun.org.apache.xml.internal.resolver.Catalog
 *  com.sun.org.apache.xml.internal.resolver.CatalogManager
 *  com.sun.org.apache.xml.internal.resolver.tools.CatalogResolver
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.util.xml;

import com.sun.istack.Nullable;
import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import com.sun.org.apache.xml.internal.resolver.tools.CatalogResolver;
import com.sun.xml.ws.server.ServerRtException;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import javax.xml.ws.WebServiceException;
import org.xml.sax.EntityResolver;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class XmlCatalogUtil {
    public static EntityResolver createEntityResolver(@Nullable URL catalogUrl) {
        CatalogManager manager = new CatalogManager();
        manager.setIgnoreMissingProperties(true);
        manager.setUseStaticCatalog(false);
        Catalog catalog = manager.getCatalog();
        try {
            if (catalogUrl != null) {
                catalog.parseCatalog(catalogUrl);
            }
        }
        catch (IOException e) {
            throw new ServerRtException("server.rt.err", e);
        }
        return XmlCatalogUtil.workaroundCatalogResolver(catalog);
    }

    public static EntityResolver createDefaultCatalogResolver() {
        CatalogManager manager = new CatalogManager();
        manager.setIgnoreMissingProperties(true);
        manager.setUseStaticCatalog(false);
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Catalog catalog = manager.getCatalog();
        try {
            Enumeration<URL> catalogEnum = cl == null ? ClassLoader.getSystemResources("META-INF/jax-ws-catalog.xml") : cl.getResources("META-INF/jax-ws-catalog.xml");
            while (catalogEnum.hasMoreElements()) {
                URL url = catalogEnum.nextElement();
                catalog.parseCatalog(url);
            }
        }
        catch (IOException e) {
            throw new WebServiceException((Throwable)e);
        }
        return XmlCatalogUtil.workaroundCatalogResolver(catalog);
    }

    private static CatalogResolver workaroundCatalogResolver(final Catalog catalog) {
        CatalogManager manager = new CatalogManager(){

            public Catalog getCatalog() {
                return catalog;
            }
        };
        manager.setIgnoreMissingProperties(true);
        manager.setUseStaticCatalog(false);
        return new CatalogResolver(manager);
    }
}

