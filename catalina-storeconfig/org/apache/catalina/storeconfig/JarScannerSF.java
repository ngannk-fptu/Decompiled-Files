/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.JarScanFilter
 *  org.apache.tomcat.JarScanner
 */
package org.apache.catalina.storeconfig;

import java.io.PrintWriter;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.catalina.storeconfig.StoreFactoryBase;
import org.apache.tomcat.JarScanFilter;
import org.apache.tomcat.JarScanner;

public class JarScannerSF
extends StoreFactoryBase {
    @Override
    public void storeChildren(PrintWriter aWriter, int indent, Object aJarScanner, StoreDescription parentDesc) throws Exception {
        JarScanner jarScanner;
        JarScanFilter jarScanFilter;
        if (aJarScanner instanceof JarScanner && (jarScanFilter = (jarScanner = (JarScanner)aJarScanner).getJarScanFilter()) != null) {
            this.storeElement(aWriter, indent, jarScanFilter);
        }
    }
}

