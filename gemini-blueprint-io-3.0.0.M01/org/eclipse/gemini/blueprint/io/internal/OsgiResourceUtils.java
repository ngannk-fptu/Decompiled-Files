/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.UrlResource
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.eclipse.gemini.blueprint.io.internal;

import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public abstract class OsgiResourceUtils {
    public static final String EMPTY_PREFIX = "";
    public static final String PREFIX_DELIMITER = ":";
    public static final String FOLDER_DELIMITER = "/";
    public static final int PREFIX_TYPE_UNKNOWN = -1;
    public static final int PREFIX_TYPE_NOT_SPECIFIED = 0;
    public static final int PREFIX_TYPE_BUNDLE_JAR = 1;
    public static final int PREFIX_TYPE_BUNDLE_SPACE = 16;
    public static final int PREFIX_TYPE_CLASS_SPACE = 256;
    public static final int PREFIX_TYPE_CLASS_ALL_SPACE = 512;

    public static String getPrefix(String path) {
        if (path == null) {
            return EMPTY_PREFIX;
        }
        int index = path.indexOf(PREFIX_DELIMITER);
        return index > 0 ? path.substring(0, index + 1) : EMPTY_PREFIX;
    }

    public static int getSearchType(String path) {
        Assert.notNull((Object)path);
        int type = 0;
        String prefix = OsgiResourceUtils.getPrefix(path);
        type = !StringUtils.hasText((String)prefix) ? 0 : (prefix.startsWith("osgibundle:") ? 16 : (prefix.startsWith("osgibundlejar:") ? 1 : (prefix.startsWith("classpath:") ? 256 : (prefix.startsWith("classpath*:") ? 512 : -1))));
        return type;
    }

    public static boolean isClassPathType(int type) {
        return type == 256 || type == 512;
    }

    public static String stripPrefix(String path) {
        int index = path.indexOf(PREFIX_DELIMITER);
        return index > -1 ? path.substring(index + 1) : path;
    }

    public static Resource[] convertURLArraytoResourceArray(URL[] urls) {
        if (urls == null) {
            return new Resource[0];
        }
        Resource[] res = new Resource[urls.length];
        for (int i = 0; i < urls.length; ++i) {
            res[i] = new UrlResource(urls[i]);
        }
        return res;
    }

    public static Resource[] convertURLEnumerationToResourceArray(Enumeration<URL> enm) {
        LinkedHashSet<UrlResource> resources = new LinkedHashSet<UrlResource>(4);
        while (enm != null && enm.hasMoreElements()) {
            resources.add(new UrlResource(enm.nextElement()));
        }
        return resources.toArray(new Resource[resources.size()]);
    }

    public static String findUpperFolder(String path) {
        int index;
        if (path.length() < 2) {
            return path;
        }
        String newPath = path;
        if (path.endsWith(FOLDER_DELIMITER)) {
            newPath = path.substring(0, path.length() - 1);
        }
        if ((index = newPath.lastIndexOf(FOLDER_DELIMITER)) > 0) {
            return newPath.substring(0, index + 1);
        }
        return path;
    }
}

