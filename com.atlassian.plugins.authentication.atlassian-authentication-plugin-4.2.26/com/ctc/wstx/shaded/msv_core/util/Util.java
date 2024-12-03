/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.xml.sax.InputSource;

public class Util {
    public static InputSource getInputSource(String fileOrURL) {
        try {
            new URL(fileOrURL);
            return new InputSource(fileOrURL);
        }
        catch (MalformedURLException e) {
            String path = new File(fileOrURL).getAbsolutePath();
            if (File.separatorChar != '/') {
                path = path.replace(File.separatorChar, '/');
            }
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            return new InputSource("file://" + path);
        }
    }

    public static boolean isAbsoluteURI(String uri) {
        int len = uri.length();
        if (len == 0) {
            return true;
        }
        if (len < 2) {
            return false;
        }
        char ch = uri.charAt(0);
        if ('a' <= ch && ch <= 'z' || 'A' <= ch && ch <= 'Z') {
            for (int i = 1; i < len; ++i) {
                ch = uri.charAt(i);
                if (ch == ':') {
                    return true;
                }
                if ('a' <= ch && ch <= 'z' || 'A' <= ch && ch <= 'Z' || ch == '-' || ch == '+' || ch == '.') continue;
                return false;
            }
        }
        return false;
    }

    public static String which(Class clazz) {
        return Util.which(clazz.getName(), clazz.getClassLoader());
    }

    public static String which(String classname, ClassLoader loader) {
        URL it;
        String classnameAsResource = classname.replace('.', '/') + ".class";
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        if ((it = loader.getResource(classnameAsResource)) != null) {
            return it.toString();
        }
        return null;
    }
}

