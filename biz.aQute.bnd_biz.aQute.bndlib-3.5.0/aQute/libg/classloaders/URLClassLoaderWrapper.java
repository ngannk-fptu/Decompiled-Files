/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.classloaders;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class URLClassLoaderWrapper {
    final URLClassLoader loader;
    final Method addURL;

    public URLClassLoaderWrapper(ClassLoader loader) throws Exception {
        this.loader = (URLClassLoader)loader;
        this.addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        this.addURL.setAccessible(true);
    }

    public void addURL(URL url) throws Exception {
        try {
            this.addURL.invoke((Object)this.loader, url);
        }
        catch (InvocationTargetException ite) {
            throw (Exception)ite.getTargetException();
        }
    }

    public Class<?> loadClass(String name) throws Exception {
        return this.loader.loadClass(name);
    }
}

