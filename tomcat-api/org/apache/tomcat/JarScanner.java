/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package org.apache.tomcat;

import javax.servlet.ServletContext;
import org.apache.tomcat.JarScanFilter;
import org.apache.tomcat.JarScanType;
import org.apache.tomcat.JarScannerCallback;

public interface JarScanner {
    public void scan(JarScanType var1, ServletContext var2, JarScannerCallback var3);

    public JarScanFilter getJarScanFilter();

    public void setJarScanFilter(JarScanFilter var1);
}

