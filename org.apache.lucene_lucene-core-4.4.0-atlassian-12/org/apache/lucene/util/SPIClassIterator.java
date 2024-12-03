/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.ServiceConfigurationError;
import org.apache.lucene.util.IOUtils;

public final class SPIClassIterator<S>
implements Iterator<Class<? extends S>> {
    private static final String META_INF_SERVICES = "META-INF/services/";
    private final Class<S> clazz;
    private final ClassLoader loader;
    private final Enumeration<URL> profilesEnum;
    private Iterator<String> linesIterator;

    public static <S> SPIClassIterator<S> get(Class<S> clazz) {
        return new SPIClassIterator<S>(clazz, Thread.currentThread().getContextClassLoader());
    }

    public static <S> SPIClassIterator<S> get(Class<S> clazz, ClassLoader loader) {
        return new SPIClassIterator<S>(clazz, loader);
    }

    public static boolean isParentClassLoader(ClassLoader parent, ClassLoader child) {
        while (child != null) {
            if (child == parent) {
                return true;
            }
            child = child.getParent();
        }
        return false;
    }

    private SPIClassIterator(Class<S> clazz, ClassLoader loader) {
        this.clazz = clazz;
        try {
            String fullName = META_INF_SERVICES + clazz.getName();
            this.profilesEnum = loader == null ? ClassLoader.getSystemResources(fullName) : loader.getResources(fullName);
        }
        catch (IOException ioe) {
            throw new ServiceConfigurationError("Error loading SPI profiles for type " + clazz.getName() + " from classpath", ioe);
        }
        this.loader = loader == null ? ClassLoader.getSystemClassLoader() : loader;
        this.linesIterator = Collections.emptySet().iterator();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean loadNextProfile() {
        ArrayList<String> lines = null;
        while (this.profilesEnum.hasMoreElements()) {
            block11: {
                if (lines != null) {
                    lines.clear();
                } else {
                    lines = new ArrayList<String>();
                }
                URL url = this.profilesEnum.nextElement();
                try {
                    InputStream in = url.openStream();
                    IOException priorE = null;
                    try {
                        String line;
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in, IOUtils.CHARSET_UTF_8));
                        while ((line = reader.readLine()) != null) {
                            int pos = line.indexOf(35);
                            if (pos >= 0) {
                                line = line.substring(0, pos);
                            }
                            if ((line = line.trim()).length() <= 0) continue;
                            lines.add(line);
                        }
                    }
                    catch (IOException ioe) {
                        try {
                            priorE = ioe;
                        }
                        catch (Throwable throwable) {
                            IOUtils.closeWhileHandlingException(priorE, in);
                            throw throwable;
                        }
                        IOUtils.closeWhileHandlingException(priorE, in);
                        break block11;
                    }
                    IOUtils.closeWhileHandlingException(priorE, in);
                }
                catch (IOException ioe) {
                    throw new ServiceConfigurationError("Error loading SPI class list from URL: " + url, ioe);
                }
            }
            if (lines.isEmpty()) continue;
            this.linesIterator = lines.iterator();
            return true;
        }
        return false;
    }

    @Override
    public boolean hasNext() {
        return this.linesIterator.hasNext() || this.loadNextProfile();
    }

    @Override
    public Class<? extends S> next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        assert (this.linesIterator.hasNext());
        String c = this.linesIterator.next();
        try {
            return Class.forName(c, false, this.loader).asSubclass(this.clazz);
        }
        catch (ClassNotFoundException cnfe) {
            throw new ServiceConfigurationError(String.format(Locale.ROOT, "A SPI class of type %s with classname %s does not exist, please fix the file '%s%1$s' in your classpath.", this.clazz.getName(), c, META_INF_SERVICES));
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

