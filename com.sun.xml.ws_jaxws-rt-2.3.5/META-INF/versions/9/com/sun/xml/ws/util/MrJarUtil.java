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
                String noPool = System.getProperty(baseName + ".noPool");
                return noPool == null || Boolean.parseBoolean(noPool);
            }
        });
    }

    static InputStream getResourceAsStream(Class clazz, String resource) {
        Package pkg = clazz.getPackage();
        String fullpath = MrJarUtil.addPackagePath(resource, pkg);
        InputStream is = MrJarUtil.moduleResource(clazz, resource);
        if (is != null) {
            return is;
        }
        is = MrJarUtil.moduleResource(clazz, fullpath);
        if (is != null) {
            return is;
        }
        URL url = MrJarUtil.cpResource(clazz, resource);
        if (url == null) {
            url = MrJarUtil.cpResource(clazz, fullpath);
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

    private static URL cpResource(Class clazz, String name) {
        URL url = clazz.getResource(name);
        if (url == null) {
            ClassLoader tccl = Thread.currentThread().getContextClassLoader();
            url = tccl.getResource(name);
        }
        return url;
    }

    private static InputStream moduleResource(Class resolvingClass, String name) {
        Module module = resolvingClass.getModule();
        try {
            InputStream stream = module.getResourceAsStream(name);
            if (stream != null) {
                return stream;
            }
        }
        catch (IOException e) {
            throw new UtilException("util.failed.to.find.handlerchain.file", resolvingClass.getName(), name);
        }
        return null;
    }

    private static String addPackagePath(String file, Package pkg) {
        Object tmp = pkg.getName();
        tmp = ((String)tmp).replace('.', '/');
        tmp = (String)tmp + "/" + file;
        return tmp;
    }
}

