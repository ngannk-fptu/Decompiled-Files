/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.webresources;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import org.apache.tomcat.util.res.StringManager;

public class ClasspathURLStreamHandler
extends URLStreamHandler {
    private static final StringManager sm = StringManager.getManager(ClasspathURLStreamHandler.class);

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        String path = u.getPath();
        URL classpathUrl = Thread.currentThread().getContextClassLoader().getResource(path);
        if (classpathUrl == null) {
            classpathUrl = ClasspathURLStreamHandler.class.getResource(path);
        }
        if (classpathUrl == null) {
            throw new FileNotFoundException(sm.getString("classpathUrlStreamHandler.notFound", new Object[]{u}));
        }
        return classpathUrl.openConnection();
    }
}

