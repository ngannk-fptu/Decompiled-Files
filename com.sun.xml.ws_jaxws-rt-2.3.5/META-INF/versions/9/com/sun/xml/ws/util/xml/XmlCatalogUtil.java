/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.Nullable
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.util.xml;

import com.sun.istack.Nullable;
import com.sun.xml.ws.server.ServerRtException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import javax.xml.catalog.CatalogFeatures;
import javax.xml.catalog.CatalogManager;
import javax.xml.ws.WebServiceException;
import org.xml.sax.EntityResolver;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class XmlCatalogUtil {
    private static final CatalogFeatures CATALOG_FEATURES = CatalogFeatures.builder().with(CatalogFeatures.Feature.RESOLVE, "continue").build();

    public static EntityResolver createEntityResolver(@Nullable URL catalogUrl) {
        EntityResolver er;
        ArrayList<URL> urlsArray = new ArrayList<URL>();
        if (catalogUrl != null) {
            urlsArray.add(catalogUrl);
        }
        try {
            er = XmlCatalogUtil.createCatalogResolver(urlsArray);
        }
        catch (Exception e) {
            throw new ServerRtException("server.rt.err", e);
        }
        return er;
    }

    public static EntityResolver createDefaultCatalogResolver() {
        EntityResolver er;
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> catalogEnum = cl == null ? ClassLoader.getSystemResources("META-INF/jax-ws-catalog.xml") : cl.getResources("META-INF/jax-ws-catalog.xml");
            er = XmlCatalogUtil.createCatalogResolver(Collections.list(catalogEnum));
        }
        catch (Exception e) {
            throw new WebServiceException((Throwable)e);
        }
        return er;
    }

    private static EntityResolver createCatalogResolver(ArrayList<URL> urls) throws Exception {
        URI[] uris = (URI[])urls.stream().map(u -> URI.create(u.toExternalForm())).toArray(URI[]::new);
        return CatalogManager.catalogResolver(CATALOG_FEATURES, uris);
    }
}

