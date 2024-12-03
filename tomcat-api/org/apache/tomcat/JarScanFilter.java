/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat;

import org.apache.tomcat.JarScanType;

public interface JarScanFilter {
    public boolean check(JarScanType var1, String var2);

    default public boolean isSkipAll() {
        return false;
    }
}

