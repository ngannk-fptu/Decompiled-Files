/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.Jar
 *  org.apache.tomcat.util.buf.UriUtil
 */
package org.apache.tomcat.util.scan;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import org.apache.tomcat.Jar;
import org.apache.tomcat.util.buf.UriUtil;
import org.apache.tomcat.util.scan.JarFileUrlJar;
import org.apache.tomcat.util.scan.JarFileUrlNestedJar;
import org.apache.tomcat.util.scan.UrlJar;

public class JarFactory {
    private JarFactory() {
    }

    public static Jar newInstance(URL url) throws IOException {
        String urlString = url.toString();
        if (urlString.startsWith("jar:file:")) {
            if (urlString.endsWith("!/")) {
                return new JarFileUrlJar(url, true);
            }
            return new JarFileUrlNestedJar(url);
        }
        if (urlString.startsWith("war:file:")) {
            URL jarUrl = UriUtil.warToJar((URL)url);
            return new JarFileUrlNestedJar(jarUrl);
        }
        if (urlString.startsWith("file:")) {
            return new JarFileUrlJar(url, false);
        }
        return new UrlJar(url);
    }

    public static URL getJarEntryURL(URL baseUrl, String entryName) throws MalformedURLException {
        String baseExternal = baseUrl.toExternalForm();
        if (baseExternal.startsWith("jar")) {
            baseExternal = baseExternal.replaceFirst("^jar:", "war:");
            baseExternal = baseExternal.replaceFirst("!/", Matcher.quoteReplacement(UriUtil.getWarSeparator()));
        }
        return new URL("jar:" + baseExternal + "!/" + entryName);
    }
}

