/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.org.apache.xml.internal.resolver.CatalogManager
 *  com.sun.org.apache.xml.internal.resolver.tools.CatalogResolver
 */
package org.apache.xmlbeans.impl.tool;

import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import com.sun.org.apache.xml.internal.resolver.tools.CatalogResolver;
import org.apache.xmlbeans.impl.util.SuppressForbidden;
import org.xml.sax.EntityResolver;

@SuppressForbidden(value="class is available in Java 8 and multi-release version handles newer official package namespace")
public class MavenPluginResolver {
    public static EntityResolver getResolver(String catalogLocation) {
        if (catalogLocation == null) {
            return null;
        }
        CatalogManager catalogManager = CatalogManager.getStaticManager();
        catalogManager.setCatalogFiles(catalogLocation);
        return new CatalogResolver(catalogManager);
    }
}

