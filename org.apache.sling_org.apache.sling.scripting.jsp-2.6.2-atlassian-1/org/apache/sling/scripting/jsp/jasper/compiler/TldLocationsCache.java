/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import java.net.URL;
import org.apache.sling.scripting.jsp.jasper.JasperException;

public abstract class TldLocationsCache {
    public static final int ABS_URI = 0;
    public static final int ROOT_REL_URI = 1;
    public static final int NOROOT_REL_URI = 2;

    public abstract String[] getLocation(String var1) throws JasperException;

    public abstract URL getTldLocationURL(String var1);

    public static int uriType(String uri) {
        if (uri.indexOf(58) != -1) {
            return 0;
        }
        if (uri.startsWith("/")) {
            return 1;
        }
        return 2;
    }
}

