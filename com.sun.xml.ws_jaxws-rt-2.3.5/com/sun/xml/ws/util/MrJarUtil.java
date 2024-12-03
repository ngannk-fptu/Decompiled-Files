/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.util;

import com.sun.xml.ws.util.UtilException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class MrJarUtil {
    public static boolean getNoPoolProperty(final String baseName) {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>(){

            @Override
            public Boolean run() {
                return Boolean.getBoolean(baseName + ".noPool");
            }
        });
    }

    static InputStream getResourceAsStream(Class clazz, String resource) {
        URL url = clazz.getResource(resource);
        if (url == null) {
            url = Thread.currentThread().getContextClassLoader().getResource(resource);
        }
        if (url == null) {
            String tmp = clazz.getPackage().getName();
            tmp = tmp.replace('.', '/');
            tmp = tmp + "/" + resource;
            url = Thread.currentThread().getContextClassLoader().getResource(tmp);
        }
        if (url == null) {
            throw new UtilException("util.failed.to.find.handlerchain.file", clazz.getName(), resource);
        }
        try {
            return url.openStream();
        }
        catch (IOException e) {
            throw new UtilException("util.failed.to.parse.handlerchain.file", clazz.getName(), resource);
        }
    }
}

