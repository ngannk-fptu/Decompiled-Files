/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.apache.tomcat.util.descriptor.tld.TldResourcePath
 */
package org.apache.jasper.servlet;

import java.net.URI;
import java.net.URL;
import java.util.Collection;
import javax.servlet.ServletContext;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.servlet.TldScanner;
import org.apache.tomcat.util.descriptor.tld.TldResourcePath;

public class TldPreScanned
extends TldScanner {
    private final Collection<URL> preScannedURLs;

    public TldPreScanned(ServletContext context, boolean namespaceAware, boolean validation, boolean blockExternal, Collection<URL> preScannedTlds) {
        super(context, namespaceAware, validation, blockExternal);
        this.preScannedURLs = preScannedTlds;
    }

    @Override
    public void scanJars() {
        for (URL url : this.preScannedURLs) {
            String str = url.toExternalForm();
            int a = str.indexOf("jar:");
            int b = str.indexOf("!/");
            if (a >= 0 && b > 0) {
                String fileUrl = str.substring(a + 4, b);
                String path = str.substring(b + 2);
                try {
                    this.parseTld(new TldResourcePath(new URI(fileUrl).toURL(), null, path));
                    continue;
                }
                catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            throw new IllegalStateException(Localizer.getMessage("jsp.error.tld.url", str));
        }
    }
}

